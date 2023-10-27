package io.muun.common.model;

import com.google.common.annotations.VisibleForTesting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * Immutable Bitcoin amount.
 */
public class BtcAmount {

    public static final BtcAmount ZERO = new BtcAmount(0);

    private static final long MSATS_PER_SAT = 1_000L;

    @VisibleForTesting
    static final long MSATS_PER_BTC = 100_000_000_000L;

    private final long milliSats;

    private BtcAmount(long milliSats) {
        this.milliSats = milliSats;
    }

    /**
     * Build an amount from sats.
     */
    public static BtcAmount fromSats(long satoshis) {
        return new BtcAmount(satoshis * MSATS_PER_SAT);
    }

    /**
     * Build an amount from BTC.
     */
    public static BtcAmount fromBtc(double btc) {
        return new BtcAmount(Math.round(btc * MSATS_PER_BTC));
    }

    /**
     * Build an amount from msats.
     */
    public static BtcAmount fromMilliSats(long milliSatoshis) {
        return new BtcAmount(milliSatoshis);
    }

    /**
     * Convert to msats.
     */
    public long toMilliSats() {
        return milliSats;
    }

    /**
     * Convert to Sats.
     *
     * @deprecated Use toSats(RoundingMode) for explicit rounding
     */
    @Deprecated
    public long toSats() {
        return milliSats / MSATS_PER_SAT;
    }

    /**
     * Convert to sats using the specified rounding mode.
     */
    public long toSats(final RoundingMode roundingMode) {
        return BigDecimal.valueOf(milliSats)
                .divide(BigDecimal.valueOf(MSATS_PER_SAT), roundingMode)
                .longValueExact();
    }

    /**
     * Converts to BTC. Amounts greater than 10,000 BTC will lose the milliSat resolution
     */
    public double toBtc() {
        return ((double) milliSats) / MSATS_PER_BTC;
    }

    /**
     * Add this amount to another.
     */
    public BtcAmount add(@NotNull BtcAmount other) {
        return new BtcAmount(milliSats + other.toMilliSats());
    }

    /**
     * Substract another amount from this one.
     */
    public BtcAmount sub(@NotNull BtcAmount other) {
        return new BtcAmount(milliSats - other.toMilliSats());
    }

    /**
     * Multiply amount by integer value.
     */
    public BtcAmount integerProduct(int scalar) {
        return new BtcAmount(milliSats * scalar);
    }

    /**
     * Check whether this amount is greater than the other.
     */
    public boolean greaterThan(@NotNull BtcAmount other) {
        return milliSats > other.milliSats;
    }

    /**
     * Check whether this amount is less than the other.
     */
    public boolean lessThan(@NotNull BtcAmount other) {
        return milliSats < other.milliSats;
    }

    /**
     * Check whether this amount is greater than or equal to the other.
     */
    public boolean greaterOrEqualThan(@NotNull BtcAmount other) {
        return milliSats >= other.milliSats;
    }

    /**
     * Check whether this amount is less than or equal to the other.
     */
    public boolean lessOrEqualThan(@NotNull BtcAmount other) {
        return milliSats <= other.milliSats;
    }

    public boolean isZero() {
        return milliSats == 0;
    }

    /**
     * Return the biggest of this amount and the other.
     */
    public BtcAmount max(@NotNull BtcAmount other) {
        return BtcAmount.fromMilliSats(Math.max(milliSats, other.milliSats));
    }

    /**
     * Return the smallest of this amount and the other.
     */
    public BtcAmount min(@NotNull BtcAmount other) {
        return BtcAmount.fromMilliSats(Math.min(milliSats, other.milliSats));
    }

    /**
     * Compute the lightning fee for this amount, given specific base and proportional fee rates.
     */
    public BtcAmount computeFee(@NotNull BtcAmount baseFee, long proportionalFeeMillionths) {

        return new BtcAmount(baseFee.milliSats + milliSats * proportionalFeeMillionths / 1_000_000);
    }

    @Override
    public boolean equals(Object other) {

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        return milliSats == ((BtcAmount) other).milliSats;
    }

    @Override
    public int hashCode() {
        return Objects.hash(milliSats);
    }

    @Override
    public String toString() {
        return milliSats + " msats";
    }
}
