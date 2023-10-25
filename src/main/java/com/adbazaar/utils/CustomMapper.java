package com.adbazaar.utils;

import com.adbazaar.dto.book.BookUpdate;
import com.adbazaar.dto.user.UserUpdate;
import com.adbazaar.model.AppUser;
import com.adbazaar.model.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    @Mapping(target = "buyers", ignore = true)
    void updateBook(BookUpdate updateDetails, @MappingTarget Book book);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "favoriteBooks", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void mapUserUpdateToAppUser(UserUpdate detailsUpdate, @MappingTarget AppUser user);

}
