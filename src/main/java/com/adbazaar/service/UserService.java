package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.authentication.LoginRequest;
import com.adbazaar.dto.authentication.LoginResponse;
import com.adbazaar.dto.authentication.RegistrationRequest;
import com.adbazaar.dto.authentication.RegistrationResponse;
import com.adbazaar.dto.authentication.UserVerification;
import com.adbazaar.dto.book.FavoriteBookResp;
import com.adbazaar.dto.book.OrderedBookResp;
import com.adbazaar.dto.user.UserDetails;
import com.adbazaar.exception.AccountVerificationException;
import com.adbazaar.exception.BookException;
import com.adbazaar.exception.BookNotFoundException;
import com.adbazaar.exception.UserAlreadyExistException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.AppUser;
import com.adbazaar.model.VerificationCode;
import com.adbazaar.repository.BookRepository;
import com.adbazaar.repository.CommentRepository;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.repository.UserVerifyTokenRepository;
import com.adbazaar.security.JwtService;
import com.adbazaar.utils.MailUtils;
import com.adbazaar.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.adbazaar.utils.MessageUtils.BOOK_ALREADY_IN_FAVORITES;
import static com.adbazaar.utils.MessageUtils.BOOK_ALREADY_IN_ORDERS;
import static com.adbazaar.utils.MessageUtils.BOOK_NOT_FOUND_IN_USER_FAVORITES_LIST;
import static com.adbazaar.utils.MessageUtils.BOOK_NOT_FOUND_IN_USER_ORDERS_LIST;
import static com.adbazaar.utils.MessageUtils.USER_ADD_TO_FAVORITES_OK;
import static com.adbazaar.utils.MessageUtils.USER_ADD_TO_ORDERS_OK;
import static com.adbazaar.utils.MessageUtils.USER_ALREADY_EXIST;
import static com.adbazaar.utils.MessageUtils.USER_ALREADY_VERIFIED;
import static com.adbazaar.utils.MessageUtils.USER_AND_SELLER_ARE_THE_SAME;
import static com.adbazaar.utils.MessageUtils.USER_DELETE_FROM_FAVORITES_OK;
import static com.adbazaar.utils.MessageUtils.USER_DELETE_FROM_ORDERS_OK;
import static com.adbazaar.utils.MessageUtils.USER_NOT_FOUND_BY_EMAIL;
import static com.adbazaar.utils.MessageUtils.USER_VERIFICATION_CODE_EXPIRED;
import static com.adbazaar.utils.MessageUtils.USER_VERIFICATION_INVALID_CODE;
import static com.adbazaar.utils.MessageUtils.USER_VERIFICATION_REASSIGNED;
import static com.adbazaar.utils.MessageUtils.USER_VERIFICATION_SUCCESSFUL;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepo;

    private final BookRepository bookRepo;

    private final UserVerifyTokenRepository userVerifyTokenRepo;

    private final CommentRepository commentRepo;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final MailUtils mailUtils;

    private final ServiceUtils serviceUtils;

    public RegistrationResponse createUser(RegistrationRequest userDetails) {
        if (userRepo.existsByEmail(userDetails.getEmail())) {
            throw new UserAlreadyExistException(String.format(USER_ALREADY_EXIST, userDetails.getEmail()));
        }
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        var user = AppUser.build(userDetails);
        var code = userVerifyTokenRepo.save(user.getEmail(), VerificationCode.build(user.getEmail()));
        userRepo.save(user);
        log.info("==== CODE: {}  for User: {} ====", code.getCode(), user.getEmail());
//        mailUtils.sendEmail(user, code, VERIFICATION_MAIL_SUBJECT);
        return RegistrationResponse.builder().email(user.getEmail()).build();
    }

    public LoginResponse login(LoginRequest userDetails) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getEmail(), userDetails.getPassword()));
        var user = findUserByEmail(userDetails.getEmail());
        return LoginResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.assignRefreshToken(user))
                .build();
    }

    public ApiResp verifyCode(UserVerification userDetails) {
        var user = findUserByEmail(userDetails.getEmail());
        var code = userVerifyTokenRepo.findByEmail(user.getEmail());
        if (user.getIsVerified()) {
            throw new AccountVerificationException(String.format(USER_ALREADY_VERIFIED, user.getEmail()));
        }
        if (!LocalDateTime.now().isBefore(code.getExpirationDate())) {
            throw new AccountVerificationException(String.format(USER_VERIFICATION_CODE_EXPIRED, user.getEmail()));
        }
        if (!Objects.equals(userDetails.getVerificationCode(), code.getCode())) {
            throw new AccountVerificationException(String.format(USER_VERIFICATION_INVALID_CODE, user.getEmail()));
        }
        userVerifyTokenRepo.delete(user.getEmail());
        user.setIsVerified(Boolean.TRUE);
        userRepo.save(user);
        return ApiResp.build(HttpStatus.OK, String.format(USER_VERIFICATION_SUCCESSFUL, user.getEmail()));
    }

    public ApiResp reassignVerificationCode(String email) {
        var user = findUserByEmail(email);
        if (user.getIsVerified()) {
            throw new AccountVerificationException(String.format(USER_ALREADY_VERIFIED, user.getEmail()));
        }
        var code = userVerifyTokenRepo.save(user.getEmail(), VerificationCode.build(user.getEmail()));
        log.info("==== CODE REASSIGN: {}  for User: {} ====", code.getCode(), user.getEmail());
//        mailUtils.sendEmail(user, code, VERIFICATION_MAIL_SUBJECT);
        return ApiResp.build(HttpStatus.OK, String.format(USER_VERIFICATION_REASSIGNED, email));
    }

    public UserDetails findUserDetailsByJwt(String token) {
        var email = jwtService.extractUsernameFromAccessToken(token.substring(7));
        var user = userRepo.findUserDetailsByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email)));
        user.setComments(commentRepo.findAllUserComments(user.getId()));
        user.setBooks(bookRepo.findAllUserBooks(user.getId()));
        user.setFavorites(bookRepo.findAllUserFavoriteBooks(user.getId()));
        user.setOrders(bookRepo.findAllUserOrderedBooks(user.getId()));
        return user;
    }

    public FavoriteBookResp addToUserFavorites(Long userId, Long bookId, String token) {
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
        var book = serviceUtils.findBookById(bookId);
        if (user.getEmail().equals(book.getSeller().getEmail())) {
            throw new BookException(String.format(USER_AND_SELLER_ARE_THE_SAME, user.getEmail(), book.getSeller().getEmail()));
        }
        var userFavoriteBooks = user.getFavoriteBooks();
        if (userFavoriteBooks.contains(book)) {
            throw new BookException(String.format(BOOK_ALREADY_IN_FAVORITES, bookId, userId));
        }
        userFavoriteBooks.add(book);
        userRepo.save(user);
        return FavoriteBookResp.build(bookId, String.format(USER_ADD_TO_FAVORITES_OK, userId, bookId), HttpStatus.OK);
    }

    public OrderedBookResp addToUserOrders(Long userId, Long bookId, String token) {
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
        var book = serviceUtils.findBookById(bookId);
        if (user.getEmail().equals(book.getSeller().getEmail())) {
            throw new BookException(String.format(USER_AND_SELLER_ARE_THE_SAME, user.getEmail(), book.getSeller().getEmail()));
        }
        var userOrders = user.getOrders();
        if (userOrders.contains(book)) {
            throw new BookException(String.format(BOOK_ALREADY_IN_ORDERS, bookId, userId));
        }
        userOrders.add(book);
        userRepo.save(user);
        return OrderedBookResp.build(bookId, String.format(USER_ADD_TO_ORDERS_OK, userId, bookId), HttpStatus.OK);
    }

    public OrderedBookResp deleteFromUserOrders(Long userId, Long bookId, String token) {
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
        var book = serviceUtils.findBookById(bookId);
        var orders = user.getOrders();
        if (!orders.contains(book)) {
            throw new BookNotFoundException(String.format(BOOK_NOT_FOUND_IN_USER_ORDERS_LIST, userId, bookId));
        }
        orders.remove(book);
        userRepo.save(user);
        return OrderedBookResp.build(bookId, String.format(USER_DELETE_FROM_ORDERS_OK, userId, bookId), HttpStatus.OK);
    }

    public FavoriteBookResp deleteFromUserFavorites(Long userId, Long bookId, String token) {
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
        var book = serviceUtils.findBookById(bookId);
        var favorites = user.getFavoriteBooks();
        if (!favorites.contains(book)) {
            throw new BookNotFoundException(String.format(BOOK_NOT_FOUND_IN_USER_FAVORITES_LIST, userId, bookId));
        }
        favorites.remove(book);
        userRepo.save(user);
        return FavoriteBookResp.build(bookId, String.format(USER_DELETE_FROM_FAVORITES_OK, userId, bookId), HttpStatus.OK);
    }

    private AppUser findUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email)));
    }


}
