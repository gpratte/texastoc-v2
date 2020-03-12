package com.texastoc.exception;

public class DoubleBuyInChangeDisallowedException extends RuntimeException {

  public DoubleBuyInChangeDisallowedException() {
    super("Cannot change the game double buy-in");
  }

}
