package com.scarlatti.certloader.cert;

import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Tuesday, 3/13/2018
 */
public class DeserializeCertTest {

    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERT = "-----END CERTIFICATE-----";

    @Test
    public void canDeserializeX64Cert() throws Exception {

        byte[] certStr = IOUtils.toByteArray(getClass().getResourceAsStream("/x64.cer"));

//        String certStr = new String(IOUtils.toByteArray(getClass().getResourceAsStream("/x64.cer")), StandardCharsets.UTF_8);
//        certStr = certStr.replaceAll(BEGIN_CERT, "").replaceAll(END_CERT, "").replaceAll("\r", "").replaceAll("\n", "").trim();
        //before decoding we need to get rod off the prefix and suffix
//        byte [] decoded = Base64.getDecoder().decode(certStr);

        X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certStr));

        System.out.println(cert);
    }

    @Test
    public void canDeserializeDerCert() throws Exception {

        byte[] certStr = IOUtils.toByteArray(getClass().getResourceAsStream("/der.cer"));
//        certStr = certStr.replaceAll(BEGIN_CERT, "").replaceAll(END_CERT, "").replaceAll("\r", "").replaceAll("\n", "").trim();
        //before decoding we need to get rod off the prefix and suffix
//        byte [] decoded = Base64.getDecoder().decode(certStr);

        X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certStr));

        System.out.println(cert);
    }
}
