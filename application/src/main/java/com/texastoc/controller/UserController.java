package com.texastoc.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SuppressWarnings("unused")
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

  @RequestMapping("/user")
  public Principal user(Principal user) {
    return user;
  }
}
