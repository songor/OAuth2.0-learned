package com.example.oauth2.server;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final DataSource dataSource;

  @Autowired
  public WebSecurityConfiguration(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * 密码授权
   * 当采用密码模式的时候，客户端（第三方软件）直接提供用户名和密码
   */
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  /**
   * 配置认证方式为用户账户
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication().dataSource(this.dataSource)
        .passwordEncoder(new BCryptPasswordEncoder());
  }

  /**
   * 开放 /login 和 /oauth/authorize 两个路径的匿名访问
   * 前者用于登录，后者用于换授权码，这两个端点访问的时机都在登录之前
   * 设置 /login 使用表单验证进行登录
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers("/js/**", "/css/**").permitAll()
        .antMatchers("/login", "/oauth/authorize").permitAll().anyRequest()
        .authenticated().and().formLogin().loginPage("/login");
  }
}
