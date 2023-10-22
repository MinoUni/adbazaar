package com.adbazaar.dto.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderedBookResp {

    @JsonProperty("book_id")
    private Long id;

    private String message;

    @JsonProperty("status_code")
    private Integer status;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssa")
    private LocalDateTime timestamp = LocalDateTime.now();

    public static OrderedBookResp build(Long id, String message, HttpStatus status) {
        return OrderedBookResp.builder()
                .id(id)
                .message(message)
                .status(status.value())
                .build();
    }
}
