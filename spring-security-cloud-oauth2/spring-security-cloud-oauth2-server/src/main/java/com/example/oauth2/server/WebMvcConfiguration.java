package com.example.oauth2.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  /**
   * 配置登录页面
   */
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("login").setViewName("login");
  }
}
