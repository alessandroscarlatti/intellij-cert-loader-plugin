package com.scarlatti.certloader.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Tuesday, 12/12/2017
 */
public class SavingTrustManager implements X509TrustManager {

    private final X509TrustManager x509TrustManager;
    private X509Certificate[] chain;

    public SavingTrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
    }

    public X509Certificate[] getAcceptedIssuers() {

        /**
         * This change has been done due to the following resolution advised for Java 1.7+
         http://infposs.blogspot.kr/2013/06/installcert-and-java-7.html
         **/

        return new X509Certificate[0];
        //throw new UnsupportedOperationException();
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
        throw new UnsupportedOperationException();
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
        this.chain = chain;
        x509TrustManager.checkServerTrusted(chain, authType);
    }

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    public X509Certificate[] getChain() {
        return chain;
    }

    public void setChain(X509Certificate[] chain) {
        this.chain = chain;
    }
}