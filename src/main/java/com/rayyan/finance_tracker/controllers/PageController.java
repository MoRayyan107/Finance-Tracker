package com.rayyan.finance_tracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.service.annotation.GetExchange;

@Controller
public class PageController {

  @GetExchange("/login")
  public String loginPage() {
    return "login.html";
  }

  @GetExchange("/register")
  public String registerPage() {
    return "register.html";
  }

}
