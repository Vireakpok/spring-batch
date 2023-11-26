package com.batch.demo.batch.exception;


import java.io.Serial;

public class AmountException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  public AmountException(String message) {
    super(message);
  }

}
