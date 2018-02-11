package com.scarlatti.certloader.plugin;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Tuesday, 12/12/2017
 */
public class SSLConnectionException extends RuntimeException {
    public SSLConnectionException() {
        super();
    }

    public SSLConnectionException(String message) {
        super(message);
    }

    public SSLConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SSLConnectionException(Throwable cause) {
        super(cause);
    }

    protected SSLConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
