package com.example.oauth2.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ClientController {

  private final OAuth2RestTemplate restTemplate;

  @Autowired
  public ClientController(OAuth2RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @GetMapping("/secured-page")
  public ModelAndView securedPage(OAuth2Authentication authentication) {
    return new ModelAndView("secured-page").addObject("authentication", authentication);
  }

  @GetMapping("remote-call")
  public String remoteCall() {
    ResponseEntity<String> responseEntity = restTemplate
        .getForEntity("http://localhost:8081/user/name", String.class);
    return responseEntity.getBody();
  }
}
