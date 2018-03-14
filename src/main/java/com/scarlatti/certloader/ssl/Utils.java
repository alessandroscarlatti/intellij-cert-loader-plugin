package com.scarlatti.certloader.ssl;

import com.scarlatti.certloader.exceptions.ProcessAbortedException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Tuesday, 12/12/2017
 */
public class Utils {

    public static SSLContext newSSLContext(TrustManager trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException("Error creating SSL Context.", e);
        }
    }

    public static SavingTrustManager newTrustManager(KeyStore keyStore) {
        try {
            TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];

            return new SavingTrustManager(defaultTrustManager);
        } catch (Exception e) {
            throw new RuntimeException("Error creating trust manager", e);
        }
    }

    public static KeyStore newKeyStore() {

        File file = new File("jssecacerts");
        if (!file.isFile()) {

            final char SEP = File.separatorChar;
            File defaultKeyStoreDir = new File(System.getProperty("java.home") + SEP
                + "lib" + SEP + "security");

            file = new File(defaultKeyStoreDir, "jssecacerts");
            if (!file.isFile()) {
                file = new File(defaultKeyStoreDir, "cacerts");
            }
        }

        try (InputStream in = new FileInputStream(file)) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            try {
                keyStore.load(in, "changeit".toCharArray());
            } catch (IOException e) {
                String password = promptForKeystorePassword();
                try {
                    keyStore.load(in, password.toCharArray());
                } catch (IOException e2) {
                    throw new ProcessAbortedException("Unable to access keystore at " + file.getAbsolutePath());
                }
            }

            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException("Error creating keystore.", e);
        }
    }

    public static String promptForKeystorePassword() {

        JPanel jPanel = new JPanel();
        JLabel label = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(10);
        jPanel.add(label);
        jPanel.add(passwordField);

        String[] options = new String[]{"Authenticate", "Cancel"};

        JOptionPane passwordDialog = new JOptionPane(jPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, options[0]) {
            @Override
            public void selectInitialValue() {
                passwordField.requestFocusInWindow();
            }
        };

        JDialog dialog = passwordDialog.createDialog(null, "Authenticate Keystore");
        try {
            // show dialog
            dialog.setVisible(true);

            if (passwordDialog.getValue() != options[0]) throw new ProcessAbortedException("User canceled password prompt");
            return new String(passwordField.getPassword());
        } finally {
            dialog.dispose();  // notify swing thread to exit
        }
    }


}
