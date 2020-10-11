package com.texastoc.exception;

public class SeasonInProgressException extends RuntimeException {

  public SeasonInProgressException(String message) {
    super(message);
  }
}
