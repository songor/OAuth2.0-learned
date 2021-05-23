package com.example.oauth2.server;

import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * 配置授权服务器
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfiguration extends AuthorizationServerConfigurerAdapter {

  private final DataSource dataSource;

  private final AuthenticationManager authenticationManager;

  @Autowired
  public OAuth2ServerConfiguration(
      DataSource dataSource, AuthenticationManager authenticationManager) {
    this.dataSource = dataSource;
    this.authenticationManager = authenticationManager;
  }

  /**
   * 使用数据库维护客户端详情信息
   * 几个重要的属性：clientId、secret、scope、authorizedGrantTypes、authorities
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.jdbc(this.dataSource);
  }

  /**
   * tokenKeyAccess：提供 /oauth/token_key 接口让资源服务器获取公钥
   * checkTokenAccess：提供 /oauth/check_token 接口让资源服务器解码令牌
   * allowFormAuthenticationForClients：允许客户端进行表单身份验证，
   * 主要是让获取令牌（/oauth/token） 支持 client_id 以及 client_secret 作登录认证
   */
  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) {
    security.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()")
        .allowFormAuthenticationForClients().passwordEncoder(
        new BCryptPasswordEncoder());
  }

  /**
   * tokenEnhancer()：配置一个自定义的 Token 增强器
   * jwtTokenEnhancer()：配置使用非对称加密方式编码和解码 JWT Token
   * approvalStore：配置使用数据库保存用户的授权记录
   * authorizationCodeServices：配置使用数据库保存授权码
   * tokenStore：配置使用 JWT 存储令牌
   * tokenEnhancer：配置令牌存放方式为 JWT
   */
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
    TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
    tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtTokenEnhancer()));

    endpoints.approvalStore(approvalStore()).authorizationCodeServices(authorizationCodeServices())
        .tokenStore(tokenStore()).tokenEnhancer(tokenEnhancerChain)
        .authenticationManager(this.authenticationManager);
  }

  @Bean
  public TokenEnhancer tokenEnhancer() {
    return new CustomTokenEnhancer();
  }

  /**
   * 配置使用非对称加密方式编码和解码 JWT Token
   */
  @Bean
  public JwtAccessTokenConverter jwtTokenEnhancer() {
    KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
        new ClassPathResource("oauth2.jks"),
        "changeit".toCharArray());
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setKeyPair(keyStoreKeyFactory.getKeyPair("oauth2"));
    return converter;
  }

  /**
   * 配置使用数据库保存用户的授权记录
   */
  @Bean
  public JdbcApprovalStore approvalStore() {
    return new JdbcApprovalStore(this.dataSource);
  }

  /**
   * 配置使用数据库保存授权码
   */
  @Bean
  public AuthorizationCodeServices authorizationCodeServices() {
    return new JdbcAuthorizationCodeServices(this.dataSource);
  }

  /**
   * 配置使用 JWT 存储令牌
   */
  @Bean
  public TokenStore tokenStore() {
    return new JwtTokenStore(jwtTokenEnhancer());
  }
}
