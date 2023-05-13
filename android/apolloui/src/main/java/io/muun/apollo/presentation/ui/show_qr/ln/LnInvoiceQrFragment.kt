package io.muun.apollo.presentation.ui.show_qr.ln

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import butterknife.BindView
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import icepick.State
import io.muun.apollo.R
import io.muun.apollo.domain.libwallet.DecodedInvoice
import io.muun.apollo.domain.model.BitcoinUnit
import io.muun.apollo.presentation.ui.MuunCountdownTimer
import io.muun.apollo.presentation.ui.new_operation.TitleAndDescriptionDrawer
import io.muun.apollo.presentation.ui.select_amount.SelectAmountActivity
import io.muun.apollo.presentation.ui.show_qr.QrFragment
import io.muun.apollo.presentation.ui.utils.ReceiveLnInvoiceFormatter
import io.muun.apollo.presentation.ui.view.ExpirationTimeItem
import io.muun.apollo.presentation.ui.view.HiddenSection
import io.muun.apollo.presentation.ui.view.LoadingView
import javax.money.MonetaryAmount


class LnInvoiceQrFragment : QrFragment<LnInvoiceQrPresenter>(), LnInvoiceView {

    companion object {
        private const val REQUEST_AMOUNT = 2
    }

    @BindView(R.id.scrollView)
    lateinit var scrollView: NestedScrollView

    @BindView(R.id.qr_overlay)
    lateinit var qrOverlay: View

    @BindView(R.id.invoice_settings)
    lateinit var hiddenSection: HiddenSection

    @BindView(R.id.invoice_settings_content)
    lateinit var invoiceSettingsContent: View

    @BindView(R.id.expiration_time_item)
    lateinit var expirationTimeItem: ExpirationTimeItem

    @BindView(R.id.invoice_loading)
    lateinit var loadingView: LoadingView

    @BindView(R.id.invoice_expired_overlay)
    lateinit var invoiceExpiredOverlay: View

    @BindView(R.id.create_other_invoice)
    lateinit var createOtherInvoice: View

    @BindView(R.id.high_fees_warning_overlay)
    lateinit var highFeesOverlay: View

    @BindView(R.id.high_fees_continue_button)
    lateinit var highFeesContinueButton: View

    // State:

    // Part of our (ugly) hack to allow SATs as an input currency option
    @State
    @JvmField
    var satSelectedAsCurrency = false

    @State
    @JvmField
    var highFees = false

    @State
    @JvmField
    var isFirstTime = true // Flag to decide when to stop showing high fees warning

    private var countdownTimer: MuunCountdownTimer? = null

    override fun inject() {
        component.inject(this)
    }

    override fun getLayoutResource() =
        R.layout.fragment_show_qr_ln

    override fun initializeUi(view: View?) {
        super.initializeUi(view)
        hiddenSection.setOnClickListener { presenter.toggleAdvancedSettings() }
        createOtherInvoice.setOnClickListener { onCreateInvoiceClick() }
        highFeesContinueButton.setOnClickListener { onHighFeesContinueButtonClick() }

    }

    override fun setShowHighFeesWarning(showHighFeesWarning: Boolean) {
        if (showHighFeesWarning) {
            highFees = true
            showHighFeesOverlay() // Needed for the Lightning First scenario
        }
    }

    override fun setShowingAdvancedSettings(showingAdvancedSettings: Boolean) {
        hiddenSection.setExpanded(showingAdvancedSettings)
        if (showingAdvancedSettings) {
            invoiceSettingsContent.visibility = View.VISIBLE
        }
    }

    override fun setLoading(loading: Boolean) {
        loadingView.visibility = if (loading) View.VISIBLE else View.GONE
        qrContent.visibility = if (loading) View.INVISIBLE else View.VISIBLE // Keep view Bounds

        copyButton.isEnabled = !loading
        shareButton.isEnabled = !loading

        editAmountItem.setLoading(loading)
        expirationTimeItem.setLoading(loading)
    }

    override fun setInvoice(invoice: DecodedInvoice, amount: MonetaryAmount?) {

        // Enable extra QR compression mode. Uppercase bech32 strings are more efficiently encoded
        super.setQrContent(invoice.original, invoice.original.toUpperCase())

        stopTimer()
        countdownTimer = buildCountDownTimer(invoice.remainingMillis())
        countdownTimer!!.start()

        if (amount != null) {
            editAmountItem.setAmount(amount, getBitcoinUnit())

        } else {
            editAmountItem.resetAmount()
        }
    }

    override fun onStop() {
        super.onStop()
        stopTimer()
    }

    override fun showFullContent(invoice: String) {
        val dialog = TitleAndDescriptionDrawer()
        dialog.setTitle(R.string.your_ln_invoice)
        dialog.setDescription(invoice)
        showDrawerDialog(dialog)
    }

    override fun getErrorCorrection(): ErrorCorrectionLevel =
        ErrorCorrectionLevel.L  // Bech 32 already has its own error correction.

    override fun toggleAdvancedSettings() {
        hiddenSection.toggleSection()

        if (invoiceSettingsContent.visibility == View.VISIBLE) {
            invoiceSettingsContent.visibility = View.GONE

        } else {
            invoiceSettingsContent.visibility = View.VISIBLE

            scrollView.postDelayed({
                scrollView.fullScroll(View.FOCUS_DOWN)
            }, 100)
        }
    }

    override fun onEditAmount(amount: MonetaryAmount?) {
        requestDelegatedExternalResult(
            REQUEST_AMOUNT,
            SelectAmountActivity.getSelectInvoiceAmountIntent(
                requireContext(),
                amount,
                satSelectedAsCurrency
            )
        )
    }

    override fun onExternalResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onExternalResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_AMOUNT && resultCode == Activity.RESULT_OK) {
            this.satSelectedAsCurrency = SelectAmountActivity.getSatSelectedAsCurrencyResult(data!!)
            val result = SelectAmountActivity.getResult(data)

            if (result != null && !result.isZero) {
                presenter.setAmount(result)

            } else {
                resetAmount()
            }
        }
    }

    override fun resetAmount() {
        presenter.setAmount(null)
    }

    private fun resetViewState() {
        highFeesOverlay.visibility = View.GONE
        invoiceExpiredOverlay.visibility = View.GONE
        qrOverlay.visibility = View.VISIBLE
        hiddenSection.visibility = View.VISIBLE
    }

    private fun stopTimer() {
        if (countdownTimer != null) {
            countdownTimer!!.cancel()
            countdownTimer = null
        }
    }

    private fun buildCountDownTimer(remainingMillis: Long): MuunCountdownTimer {

        return object : MuunCountdownTimer(remainingMillis) {

            override fun onTickSeconds(remainingSeconds: Long) {
                // Using minimalistic pattern/display/formatting to better fit in small screen space
                expirationTimeItem.setExpirationTime(
                    ReceiveLnInvoiceFormatter().formatSeconds(remainingSeconds)
                )
            }

            override fun onFinish() {
                showInvoiceExpiredOverlay()
            }
        }
    }

    private fun showHighFeesOverlay() {
        highFeesOverlay.visibility = View.VISIBLE
        invoiceExpiredOverlay.visibility = View.GONE
        qrOverlay.visibility = View.GONE
        hiddenSection.visibility = View.GONE
        hiddenSection.setExpanded(false)
        invoiceSettingsContent.visibility = View.GONE
    }

    private fun onHighFeesContinueButtonClick() {
        isFirstTime = false
        if (invoiceExpiredOverlay.isVisible) {
            highFeesOverlay.visibility = View.GONE

        } else {
            resetViewState()
        }
    }

    private fun showInvoiceExpiredOverlay() {
        // highFeesOverlay may be showing at this point. This assumes that highFeesOverlay takes
        // precedence when both highFeesOverlay and invoiceExpiredOverlay are visible.
        invoiceExpiredOverlay.visibility = View.VISIBLE
        qrOverlay.visibility = View.GONE
        hiddenSection.visibility = View.GONE
        hiddenSection.setExpanded(false)
        invoiceSettingsContent.visibility = View.GONE
    }

    private fun onCreateInvoiceClick() {
        presenter.generateNewEmptyInvoice()
        resetViewState()
    }

    fun refresh() {
        if (isFirstTime && highFees) {
            showHighFeesOverlay()

        } else {
            resetViewState()
            presenter.generateNewInvoice()
        }
    }

    // Part of our (ugly) hack to allow SATs as an input currency option
    private fun getBitcoinUnit() = if (satSelectedAsCurrency) {
        BitcoinUnit.SATS
    } else {
        BitcoinUnit.BTC
    }
}
