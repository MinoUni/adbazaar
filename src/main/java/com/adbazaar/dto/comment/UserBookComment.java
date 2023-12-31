package com.adbazaar.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBookComment {

    private Long id;

    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("author")
    private String fullName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ssa")
    @JsonProperty("creation_date")
    private LocalDateTime creationDate;

    private String message;

    private Double rate;

    private Integer likes;

    private Integer dislikes;
}
