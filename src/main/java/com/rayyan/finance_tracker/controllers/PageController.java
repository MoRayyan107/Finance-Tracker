package com.rayyan.finance_tracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping("/login")
  public String loginPage() {
    return "login.html";
  }

  @GetMapping("/register")
  public String registerPage() {
    return "register.html";
  }

  @GetMapping("/dashboard")
  public String dashboardPage() {
    return "dashboard.html";
  }

}
