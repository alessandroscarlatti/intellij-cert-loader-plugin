package com.scarlatti.certloader.plugin;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Tuesday, 12/12/2017
 */

import com.scarlatti.certloader.ssl.SavingTrustManager;
import com.scarlatti.certloader.ssl.Utils;
import com.scarlatti.certloader.ui.controls.CertLoaderDialogOld;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.scarlatti.certloader.ssl.Utils.newKeyStore;
import static com.scarlatti.certloader.ssl.Utils.newTrustManager;

public class CertDownloader {

    private SSLContext sslContext;
    private SavingTrustManager trustManager;
    private KeyStore keyStore;

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    protected int SSL_CONNECTION_TIMEOUT_MS = 10000;

    public CertDownloader(SSLContext sslContext, SavingTrustManager trustManager, KeyStore keyStore) {
        this.sslContext = sslContext;
        this.trustManager = trustManager;
        this.keyStore = keyStore;
    }

    public static List<X509Certificate> downloadCerts(String url) {
        KeyStore keyStore = newKeyStore();
        SavingTrustManager trustManager = newTrustManager(keyStore);
        SSLContext sslContext = Utils.newSSLContext(trustManager);

        return new CertDownloader(sslContext,  trustManager, keyStore).doDownloadCerts(url);
    }

    public List<X509Certificate> doDownloadCerts(String url) {

        if (sslContext == null)
            sslContext = newSSLContext();

        Props props = new Props();

        processURL(url, props);

        performSSLHandshake(props);

        X509Certificate[] certs = trustManager.getChain();
        if (certs == null) {
            throw new RuntimeException("Could not obtain server certificate chain");
        }

        return Arrays.asList(certs);
    }

    protected void processURL(String url, Props props) {
        String[] hostPort = url.split(":");
        String host = hostPort[0];
        int port = (hostPort.length == 1) ? 443 : Integer.parseInt(hostPort[1]);

        props.setHost(host);
        props.setPort(port);
    }

    // TODO I want this to throw an exception...
    protected void performSSLHandshake(Props props) {
        SSLSocketFactory factory = sslContext.getSocketFactory();
        System.out.println("Opening connection to " + props.getHost() + ":" + props.getPort() + "...");
        CountDownLatch latch = new CountDownLatch(1);

        AtomicBoolean success = new AtomicBoolean(false);
        AtomicReference<Exception> exception = new AtomicReference<>(null);

        new Thread(() -> {
            try {
                SSLSocket socket = (SSLSocket) factory.createSocket(props.getHost(), props.getPort());
                socket.setSoTimeout(SSL_CONNECTION_TIMEOUT_MS);
                try {
                    System.out.println("Starting SSL handshake...");

                    // display progress bar here...
                    Thread.sleep(2000);
                    socket.startHandshake();
                    socket.close();
                    System.out.println();
                    System.out.println("No ,errors, certificate is already trusted");
                } catch (SSLException e) {
                    System.out.println();
                    e.printStackTrace(System.out);
                }

                success.set(true);
            } catch (Exception e) {
                exception.set(e);
            } finally {
                latch.countDown();
            }
        }).start();

        try {
            latch.await();
            if (!success.get()) {
                throw new SSLConnectionException("Error connecting to host " + props.getHost() + " on port " +
                    props.getPort() + ".", exception.get());
            }
        } catch (InterruptedException e) {
            throw new ProcessAbortedException("Countdown latch interrupted!", e);
        }

    }

    protected void printCerts(X509Certificate[] certs) {
        System.out.println();
        System.out.println("Server sent " + certs.length + " certificate(s):");
        System.out.println();

        for (int i = 0; i < certs.length; i++) {
            try {
                printCert(certs[i], i + 1);
            } catch (Exception e) {
                throw new RuntimeException("Error printing cert " + i, e);
            }
        }
    }

    protected void printCert(X509Certificate cert, int id) throws Exception {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        System.out.println
            (" " + (id) + " Subject " + cert.getSubjectDN());
        System.out.println("   Issuer  " + cert.getIssuerDN());
        sha1.update(cert.getEncoded());
        System.out.println("   sha1    " + toHexString(sha1.digest()));
        md5.update(cert.getEncoded());
        System.out.println("   md5     " + toHexString(md5.digest()));
        System.out.println();
    }

    protected SSLContext newSSLContext() {

        try {
            keyStore = newKeyStore();
            trustManager = newTrustManager(keyStore);
            return Utils.newSSLContext(trustManager);
        } catch (Exception e) {
            throw new RuntimeException("Error creating SSL context.", e);
        }
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEX_DIGITS[b >> 4]);
            sb.append(HEX_DIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }
}
