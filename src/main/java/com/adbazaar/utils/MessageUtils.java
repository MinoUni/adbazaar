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

    public static final String USER_DELETE_FROM_ORDERS_OK = "User with id: {%d} delete from orders Book with id: {%d}";

    public static final String USER_DELETE_FROM_FAVORITES_OK = "User with id: {%d} delete from favorites Book with id: {%d}";

    public static final String USER_ADD_TO_ORDERS_OK = "User with id: {%d} add to orders Book with id: {%d}";

    public static final String USER_AND_SELLER_ARE_THE_SAME = "User: {%s} and Seller: {%s} are the same person.";

    public static final String USER_NOT_MATCH_WITH_JWT = "User: {%s} not match with jwt username: {%s}";

    public static final String USER_ADD_COMMENT_TO_A_BOOK = "The User: {%d} added a comment to a Book: {%d}.";

    public static final String USER_PASSWORD_CHANGE_OK = "User: {%d} changed password";

    public static final String REFRESH_TOKEN_NOT_FOUND = "JWT refresh associated with user: {%s} not found";

    public static final String REFRESH_TOKEN_INVALID_OR_EXPIRED = "Provided refresh JWT is invalid or expired";

    public static final String REFRESH_TOKEN_REVOKED = "Revoke refresh JWT for user: {%s}";

    public static final String INVALID_JWT = "Provided JWT is invalid";

    public static final String EXPIRED_JWT = "Provided JWT is expired";

    public static final String BOOK_CREATED = "User with id {%d} create a new book";

    public static final String BOOK_DELETED = "User with id: {%d} delete Book with id: {%d}.";

    public static final String BOOK_UPDATED = "User with id: {%d} update Book with id: {%d}.";

    public static final String BOOK_NOT_FOUND_BY_ID = "Book with id: {%d} not found";

    public static final String BOOK_ALREADY_EXISTS = "Book with such parameters already exists.";

    public static final String BOOK_ALREADY_IN_FAVORITES = "Book with id: {%d} already in favorite list of user with id: {%d}";

    public static final String BOOK_ALREADY_IN_ORDERS = "Book with id: {%d} already in orders list of user with id: {%d}";

    public static final String BOOK_NOT_FOUND_IN_USER_BOOKS_LIST = "Book with id: {%d} not found in users: {%d} book list.";

    public static final String BOOK_NOT_FOUND_IN_USER_FAVORITES_LIST = "Book with id: {%d} not found in users: {%d} favorites list.";

    public static final String BOOK_NOT_FOUND_IN_USER_ORDERS_LIST = "Book with id: {%d} not found in users: {%d} orders list.";

    public static final String BOOK_RATE_COUNT_FAIL = "Failed to count book rate";
}
