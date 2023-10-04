package com.adbazaar.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInComment {

    private Long id;

    private String title;

    private String author;

    @JsonProperty("image_path")
    private String imagePath;

}
