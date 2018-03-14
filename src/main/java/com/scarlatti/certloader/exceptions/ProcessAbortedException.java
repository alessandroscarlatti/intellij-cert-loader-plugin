package com.scarlatti.certloader.exceptions;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Tuesday, 12/12/2017
 */
public class ProcessAbortedException extends RuntimeException {
    public ProcessAbortedException() {
        super();
    }

    public ProcessAbortedException(String message) {
        super(message);
    }

    public ProcessAbortedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessAbortedException(Throwable cause) {
        super(cause);
    }

    protected ProcessAbortedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
