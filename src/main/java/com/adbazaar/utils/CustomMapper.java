package com.adbazaar.utils;

import com.adbazaar.dto.book.BookUpdate;
import com.adbazaar.dto.book.UserBook;
import com.adbazaar.model.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface CustomMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    void updateBook(BookUpdate updateDetails, @MappingTarget Book book);

    Set<UserBook> booksToUserBooks(Set<Book> userFavoriteBooks);
}
