package com.adbazaar.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Users Management")
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/index")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok().body("SECURED POINT!");
    }
}
