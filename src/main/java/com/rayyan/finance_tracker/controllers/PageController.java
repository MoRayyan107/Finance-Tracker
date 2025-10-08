package com.rayyan.finance_tracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PageController {

  /**
   * Insights page (Landing page)
   * @return insights.html page
   */
  @GetMapping
  public String insightsPage() { return "insights.html"; }

  /**
   * Login Page
   * @return Login.html page
   */
  @GetMapping("/login")
  public String loginPage() {
    return "login.html";
  }

  /**
   * Register Page
   * @return Register.html page
   */
  @GetMapping("/register")
  public String registerPage() {
    return "register.html";
  }

  /**
   * Dashboard Page
   * @return Dashboard.html page
   */
  @GetMapping("/dashboard")
  public String dashboardPage() {
    return "dashboard.html";
  }

}
