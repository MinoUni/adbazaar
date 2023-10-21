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
public class BookComment {

    private Long id;

    @JsonProperty("author")
    private String fullName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssa")
    @JsonProperty("creation_date")
    private LocalDateTime creationDate;

    private String message;

    private Double rate;

    private Integer likes;

    private Integer dislikes;

}
