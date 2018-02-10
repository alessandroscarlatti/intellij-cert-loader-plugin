package com.scarlatti.certloader.plugin;

import java.util.List;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Thursday, 11/30/2017
 */
public class Props {
    private String host;
    private int port;
    private String url;
    private String keystorePath;
    private String keystorePassword;
    private List<String> args;

    public void setHost(String host) {
        this.host = host;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Props{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", keystorePath='" + keystorePath + '\'' +
            ", keystorePassword='" + keystorePassword + '\'' +
            ", args=" + args +
            '}';
    }
}
