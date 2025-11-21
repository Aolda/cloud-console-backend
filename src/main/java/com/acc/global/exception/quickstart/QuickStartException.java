package com.acc.global.exception.quickstart;

import com.acc.global.exception.AccBaseException;

public class QuickStartException extends AccBaseException {
  public QuickStartException(QuickStartErrorCode errorCode) {
    super(errorCode);
  }

  public QuickStartException(QuickStartErrorCode errorCode, Throwable cause) {
    super(errorCode, errorCode.getMessage(), cause);
  }
}
