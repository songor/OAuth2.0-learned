package com.example.oauth2.resource;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptTest {

  @Test
  void testBCrypt() {
    System.out.println(BCrypt.hashpw("reader.123", BCrypt.gensalt()));
    System.out.println(BCrypt.hashpw("writer.123", BCrypt.gensalt()));
    System.out.println(BCrypt.hashpw("client.123", BCrypt.gensalt()));
    System.out.println(BCrypt.hashpw("password.123", BCrypt.gensalt()));
    System.out.println(BCrypt.hashpw("authorization.123", BCrypt.gensalt()));
  }
}
