package com.adbazaar.utils;

public final class MessageUtils {

    public static final String VERIFICATION_MAIL_SUBJECT = "Your verification code from Adbazaar";

    public static final String USER_ALREADY_EXIST = "User with email: {%s} already exist";

    public static final String USER_NOT_FOUND_BY_EMAIL = "User with email: {%s} not found";

    public static final String USER_ALREADY_VERIFIED = "User: {%s} already verified";

    public static final String USER_VERIFICATION_CODE_EXPIRED = "User: {%s} send expired verification code";

    public static final String USER_VERIFICATION_INVALID_CODE = "User: {%s} provide invalid verification code";

    public static final String USER_VERIFICATION_SUCCESSFUL = "User: {%s} verification successful";

    public static final String USER_VERIFICATION_REASSIGNED = "Verification code reassigned for user: {%s}";

    public static final String REFRESH_TOKEN_NOT_FOUND = "Token associated with user: {%s} not found";

    public static final String REFRESH_TOKEN_INVALID_OR_EXPIRED = "Provided refresh token is invalid or expired";

    public static final String REFRESH_TOKEN_REVOKED = "Revoke refresh token for user: {%s}";
}
