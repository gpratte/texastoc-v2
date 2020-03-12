package com.texastoc.security;

public class SecurityConstants {
  public static final String SIGNING_KEY = "5Lmr5JwJP4CSU";
  public static final String AUTHORITIES_KEY = "scopes";
  public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;

  public static final String HEADER_STRING = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String SIGN_UP_URL = "/api/v2/players";
}
