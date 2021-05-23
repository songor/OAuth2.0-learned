package com.example.oauth2.server;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * 自定义 Token 增强器，将用户信息嵌入到 JWT Token 中
 * 在实际应用中，可能会从数据库或外部服务查询更多的用户信息加入到 JWT Token 中
 */
public class CustomTokenEnhancer implements TokenEnhancer {

  @Override
  public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
      OAuth2Authentication authentication) {
    Authentication userAuthentication = authentication.getUserAuthentication();
    if (userAuthentication != null) {
      Object principal = userAuthentication.getPrincipal();
      Map<String, Object> additionalInformation = new HashMap<>();
      additionalInformation.put("userDetails", principal);
      ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
    }
    return accessToken;
  }
}
