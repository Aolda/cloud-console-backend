package com.acc.global.exception.network;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class NetworkException extends AccBaseException {
    public NetworkException(ErrorCode errorCode) {
        super(errorCode);
    }
  public NetworkException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, errorCode.getMessage(), cause);
  }
}
