package io.muun.apollo.presentation.ui.home;

import io.muun.apollo.data.async.tasks.TaskScheduler;
import io.muun.apollo.domain.LoggingContextManager;
import io.muun.apollo.domain.SignupDraftManager;
import io.muun.apollo.domain.action.ContactActions;
import io.muun.apollo.domain.action.NotificationActions;
import io.muun.apollo.domain.action.realtime.FetchRealTimeDataAction;
import io.muun.apollo.domain.analytics.AnalyticsEvent;
import io.muun.apollo.domain.analytics.PdfFontIssueTracker;
import io.muun.apollo.domain.model.UserActivatedFeatureStatus;
import io.muun.apollo.domain.selector.UserActivatedFeatureStatusSelector;
import io.muun.apollo.domain.selector.UserSelector;
import io.muun.apollo.presentation.ui.base.BasePresenter;
import io.muun.apollo.presentation.ui.base.di.PerActivity;
import io.muun.apollo.presentation.ui.bundler.CurrencyUnitBundler;
import io.muun.apollo.presentation.ui.fragments.home.HomeFragmentParentPresenter;
import io.muun.apollo.presentation.ui.fragments.operations.OperationsCache;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import icepick.State;
import libwallet.Libwallet;
import rx.Observable;

import javax.inject.Inject;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.constraints.NotNull;

@PerActivity
public class HomePresenter extends BasePresenter<HomeView> implements HomeFragmentParentPresenter {

    private final LoggingContextManager loggingContextManager;
    private final ContactActions contactActions;
    private final NotificationActions notificationActions;
    private final UserSelector userSel;
    private final UserActivatedFeatureStatusSelector userActivatedFeatureStatusSel;
    private final SignupDraftManager signupDraftManager;

    private final TaskScheduler taskScheduler;

    private final FetchRealTimeDataAction fetchRealTimeData;

    private final OperationsCache operationsCache;

    // Setting a default to avoid race condition, paymentCtx may take too long to load/fetch
    // and if state must be saved, null can't be serialized by CurrencyUnitBundler
    @State(CurrencyUnitBundler.class)
    CurrencyUnit selectedCurrency = Monetary.getCurrency("USD");

    /**
     * Creates a home presenter.
     */
    @Inject
    public HomePresenter(LoggingContextManager loggingContextManager,
                         ContactActions contactActions,
                         NotificationActions notificationActions,
                         UserSelector userSel,
                         UserActivatedFeatureStatusSelector userActivatedFeatureStatusSel,
                         SignupDraftManager signupDraftManager,
                         TaskScheduler taskScheduler,
                         FetchRealTimeDataAction fetchRealTimeData,
                         OperationsCache operationsCache) {

        this.loggingContextManager = loggingContextManager;
        this.contactActions = contactActions;
        this.userSel = userSel;
        this.userActivatedFeatureStatusSel = userActivatedFeatureStatusSel;
        this.signupDraftManager = signupDraftManager;
        this.fetchRealTimeData = fetchRealTimeData;
        this.notificationActions = notificationActions;
        this.taskScheduler = taskScheduler;
        this.operationsCache = operationsCache;
    }

    /**
     * Call to report HomeActivity was first created.
     */
    @Override
    public void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        assertGooglePlayServicesPresent();

        taskScheduler.scheduleAllTasks();
        loggingContextManager.setupCrashlytics();
        signupDraftManager.clear(); // if we're here, we're 100% sure signup was successful

        operationsCache.start();

        fetchRealTimeData.runForced();

        new PdfFontIssueTracker(getContext(), analytics)
                .track(AnalyticsEvent.PDF_FONT_ISSUE_TYPE.HOME_VIEW);
    }


    /**
     * Call to report activity was destroyed.
     */
    @Override
    public void destroy() {
        operationsCache.stop();
    }

    @Override
    public void setUp(@NotNull Bundle arguments) {
        super.setUp(arguments);

        setUpAnalyticsProfile();

        fetchRealTimeData.run();
        notificationActions.pullNotificationsAction.run();

        if (hasPermission(Manifest.permission.READ_CONTACTS)) {
            contactActions.startPhoneContactsAutoSync();
        }

        setUpTaprootCelebrationDialog();
    }

    private void setUpTaprootCelebrationDialog() {
        final Observable<?> taprootDialogObs = Observable.combineLatest(
                userSel.watchPendingTaprootCelebration(),
                userActivatedFeatureStatusSel.watch(Libwallet.getUserActivatedFeatureTaproot()),

                (isCelebPending, status) -> {
                    if (!isCelebPending && status == UserActivatedFeatureStatus.PREACTIVATED) {
                        userSel.setPendingTaprootCelebration(true); // for the next time (hackish)
                    }

                    if (isCelebPending && status == UserActivatedFeatureStatus.ACTIVE) {
                        view.showTaprootCelebration();
                    }

                    return null;
                }
        );

        subscribeTo(taprootDialogObs);
    }

    private void setUpAnalyticsProfile() {
        final Observable<?> observable = userSel.watch()
                .compose(getAsyncExecutor())
                .doOnNext(analytics::setUserProperties);

        subscribeTo(observable);
    }

    @Override
    public void navigateToSecurityCenter() {
        view.navigateToSecurityCenter();
    }

    @Override
    public void navigateToHighFeesExplanationScreen() {
        navigator.navigateToHighFeesExplanation(getContext());
    }

    @Override
    public void navigateToOperations() {
        navigator.navigateToOperations((Activity) view);
    }

    /**
     * Navigate to send feedback screen.
     */
    public void navigateToSendFeedbackScreen() {
        navigator.navigateToSendGenericFeedback(getContext());
    }

    /**
     * Avoid showing taproot celebration again.
     */
    public void reportTaprootCelebrationShown() {
        userSel.setPendingTaprootCelebration(false);
    }
}
