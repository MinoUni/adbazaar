package com.adbazaar.utils;

public final class MessageUtils {

    public static final String VERIFICATION_MAIL_SUBJECT = "Your verification code from Adbazaar";

    public static final String USER_ALREADY_EXIST = "User with email: {%s} already exist";

    public static final String USER_NOT_FOUND_BY_EMAIL = "User with email: {%s} not found";

    public static final String USER_NOT_FOUND_BY_ID = "User with id: {%d} not found";

    public static final String USER_ALREADY_VERIFIED = "User: {%s} already verified";

    public static final String USER_NOT_VERIFIED = "User: {%s} not verified";

    public static final String USER_VERIFICATION_CODE_EXPIRED = "User: {%s} send expired verification code";

    public static final String USER_VERIFICATION_INVALID_CODE = "User: {%s} provide invalid verification code";

    public static final String USER_VERIFICATION_SUCCESSFUL = "User: {%s} verification successful";

    public static final String USER_VERIFICATION_REASSIGNED = "Verification code reassigned for user: {%s}";

    public static final String USER_ADD_TO_FAVORITES_OK = "User with id: {%d} add to favorites Book with id: {%d}";


    public static final String REFRESH_TOKEN_NOT_FOUND = "JWT refresh associated with user: {%s} not found";

    public static final String REFRESH_TOKEN_INVALID_OR_EXPIRED = "Provided refresh JWT is invalid or expired";

    public static final String REFRESH_TOKEN_REVOKED = "Revoke refresh JWT for user: {%s}";

    public static final String INVALID_JWT = "Provided JWT is invalid";

    public static final String EXPIRED_JWT = "Provided JWT is expired";

    public static final String BOOK_CREATED = "User with id {%d} create a new book";

    public static final String BOOK_NOT_FOUND_BY_ID = "Book with id: {%d} not found";

}
