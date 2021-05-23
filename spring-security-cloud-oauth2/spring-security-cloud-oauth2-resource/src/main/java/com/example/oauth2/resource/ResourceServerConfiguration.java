package com.example.oauth2.resource;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

/**
 * 配置受保护资源服务器
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  /**
   * 声明资源服务器的 ID 为 userservice
   */
  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws IOException {
    resources.resourceId("userservice").tokenStore(tokenStore());
  }

  @Bean
  public TokenStore tokenStore() throws IOException {
    return new JwtTokenStore(jwtAccessTokenConverter());
  }

  /**
   * 配置公钥
   */
  @Bean
  public JwtAccessTokenConverter jwtAccessTokenConverter() throws IOException {
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    Resource resource = new ClassPathResource("public.cert");
    String publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
    converter.setVerifierKey(publicKey);
    return converter;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers("/user/**").authenticated().anyRequest().permitAll();
  }
}
