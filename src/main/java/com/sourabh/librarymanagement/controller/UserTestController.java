package com.sourabh.librarymanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserTestController {

    @GetMapping("/user/test")
    public String testUser() {
        return "USER ACCESS OK";
    }
}
