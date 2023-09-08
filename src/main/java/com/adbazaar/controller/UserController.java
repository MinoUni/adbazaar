package com.adbazaar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/index")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok().body("SECURED POINT!");
    }
}
