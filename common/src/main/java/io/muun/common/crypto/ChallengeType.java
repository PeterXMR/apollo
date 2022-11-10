package io.muun.common.crypto;


import io.muun.common.exception.MissingCaseError;

public enum ChallengeType {

    /**
     * Client-generated public key before any other key was set up.
     * DEPRECATED, due to UX problems. USER_KEY "fake" chalenge key was introduced instead. See:
     * https://www.notion.so/muunwallet/Email-less-recovery-and-ANON-challenge-keys-77e381289dd843659f531c8265603fd5
     */
    ANON(false),

    /**
     * Fake challenge key type used to sign/verify challenges with the user's private/public key.
     * Replaces ANON. See:
     * https://www.notion.so/muunwallet/Email-less-recovery-and-ANON-challenge-keys-77e381289dd843659f531c8265603fd5
     */
    USER_KEY(false),

    /**
     * User-provided password public key will be used to sign Challenge.
     */
    PASSWORD(true),

    /**
     * User-provided recovery code public key will be used to sign Challenge.
     */
    RECOVERY_CODE(true);

    private static final int ANON_CHALLENGE_VERSION = 1;
    private static final int PASSWORD_CHALLENGE_VERSION = 1;

    // Version 2 was added during the email-less recovery feature, apollo build version >= 76
    // Version 1 is deprecated and only used in older clients
    private static final int RECOVERY_CODE_CHALLENGE_VERSION = 2;

    /**
     * Whether this challenge is used to access and encrypt/decrypt a PrivateKey.
     */
    public final boolean encryptsPrivateKey;

    ChallengeType(boolean encryptsPrivateKey) {
        this.encryptsPrivateKey = encryptsPrivateKey;
    }

    /**
     * Get the current version of a challenge type.
     */
    public static int getVersion(ChallengeType type) {
        switch (type) {
            case ANON:
                return ANON_CHALLENGE_VERSION;

            case PASSWORD:
                return PASSWORD_CHALLENGE_VERSION;

            case RECOVERY_CODE:
                return RECOVERY_CODE_CHALLENGE_VERSION;

            default:
                throw new MissingCaseError(type);
        }
    }
}
