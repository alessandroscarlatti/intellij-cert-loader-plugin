package com.scarlatti.certloader.plugin;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Tuesday, 12/12/2017
 */

import com.scarlatti.certloader.ui.controls.CertLoaderDialogOld;
import com.scarlatti.certloader.ssl.SavingTrustManager;
import com.scarlatti.certloader.ssl.Utils;

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
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.scarlatti.certloader.ssl.Utils.newKeyStore;
import static com.scarlatti.certloader.ssl.Utils.newTrustManager;

public class CertInstaller {

    private SSLContext sslContext;
    private SavingTrustManager trustManager;
    private KeyStore keyStore;

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();
    protected int SSL_CONNECTION_TIMEOUT_MS = 10000;

    public CertInstaller(SSLContext sslContext, SavingTrustManager trustManager, KeyStore keyStore) {
        this.sslContext = sslContext;
        this.trustManager = trustManager;
        this.keyStore = keyStore;
    }

    public static void installCert() {
        KeyStore keyStore = newKeyStore();
        SavingTrustManager trustManager = newTrustManager(keyStore);
        SSLContext sslContext = Utils.newSSLContext(trustManager);

        new CertInstaller(sslContext,  trustManager, keyStore).doInstallCert();
    }

    public void doInstallCert() {

        if (sslContext == null)
            sslContext = newSSLContext();

        Props props = new Props();

        processURL(CertLoaderDialogOld.getInstance().getUrlTextBox().getText(), props);

        performSSLHandshake(props);

        X509Certificate[] certs = trustManager.getChain();
        if (certs == null) {
            throw new RuntimeException("Could not obtain server certificate chain");
        }

        printCerts(certs);

        List<X509Certificate> certsChosen = promptUserToChooseCerts(certs);

//        if (certsChosen.size() > 0) {
//            propsBuilder.buildStep2(props);
//            try {
//                saveCerts(certsChosen, props);
//            } catch (ProcessAbortedException e) {
//                throw new ProcessAbortedException(e);
//            } catch (Exception e) {
//                showErrorDialog("Error", "Error installing certificate(s).", e);
//                throw new ProcessAbortedException(e);
//            }
//        }

//        JOptionPane.showMessageDialog(null, "Installed " + certsChosen.size() + " certificate(s).", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void processURL(String url, Props props) {
        String[] hostPort = url.split(":");
        String host = hostPort[0];
        int port = (hostPort.length == 1) ? 443 : Integer.parseInt(hostPort[1]);

        props.setHost(host);
        props.setPort(port);
    }

    protected void performSSLHandshake(Props props) {
        SSLSocketFactory factory = sslContext.getSocketFactory();

        System.out.println("Opening connection to " + props.getHost() + ":" + props.getPort() + "...");
//        JDialog jDialog = new JDialog();
//        Container content = jDialog.getContentPane();
//        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        JProgressBar progressBar = new JProgressBar();
        progressBar.setMaximum(SSL_CONNECTION_TIMEOUT_MS);
        CertLoaderDialogOld.getInstance().getCertsPanel().removeAll();
        CertLoaderDialogOld.getInstance().getCertsPanel().add(progressBar);
        CertLoaderDialogOld.getInstance().getCertLoaderDialog().revalidate();

//        jDialog.setSize(300, 100);
//        jDialog.withTitle("Connecting to " + props.getHost() + " on port " + props.getPort() + "...");
//        jDialog.setVisible(true);

        Thread progressBarThread = new Thread(() -> {
            final int REFRESH_RATE_MS = 15;

            for (int i = 0; i * REFRESH_RATE_MS < SSL_CONNECTION_TIMEOUT_MS; i++) {
                try {
                    progressBar.setValue(i * (SSL_CONNECTION_TIMEOUT_MS / (SSL_CONNECTION_TIMEOUT_MS / REFRESH_RATE_MS)));
                    CertLoaderDialogOld.getInstance().getCertsPanel().revalidate();
                    Thread.sleep(REFRESH_RATE_MS);
                } catch (InterruptedException e) {
                    break;
                }
            }


        });

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                progressBarThread.start();

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
            } catch (Exception e) {
                progressBarThread.interrupt();
                showErrorDialog("Error", "Error connecting to host " + props.getHost() + " on port " +
                    props.getPort() + ".", e);
                throw new ProcessAbortedException("Error connecting to host.", e);
            } finally {
                // hide progress bar here...
                CertLoaderDialogOld.getInstance().getCertsPanel().removeAll();
                CertLoaderDialogOld.getInstance().getCertsPanel().revalidate();
                CertLoaderDialogOld.getInstance().getCertsPanel().repaint();
//            jDialog.dispose();
                latch.countDown();
            }
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new ProcessAbortedException("Countdown latch interrupted!", e);
        }

    }

    protected void showErrorDialog(String title, String message, Exception e) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(e.getMessage());
        sb.append("\n");
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        sb.append(errors.toString());
        JTextArea jta = new JTextArea(sb.toString());
        JScrollPane jsp = new JScrollPane(jta){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 320);
            }
        };
        JOptionPane.showMessageDialog(
            null, jsp, title, JOptionPane.ERROR_MESSAGE);
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

//    protected void saveCerts(List<X509Certificate> certs, Props props) throws Exception {
//        for (int i = 0; i < certs.size(); i++) {
//            X509Certificate cert = certs.get(i);
//            String alias = props.getHost() + "-" + (i + 1) + "-" + new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSS").format(new Date());
//
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//
//            try (FileInputStream in = new FileInputStream(new File(props.getKeystorePath()))) {
//                keyStore.load(in, props.getKeystorePassword().toCharArray());
//            }
//
//            keyStore.setCertificateEntry(alias, cert);
//            try (OutputStream out = new FileOutputStream(props.getKeystorePath())) {
//                keyStore.store(out, props.getKeystorePassword().toCharArray());
//            }
//
//            System.out.println();
//            System.out.println(cert);
//            System.out.println();
//            System.out.println("Added certificate to keystore " + props.getKeystorePath() + " using alias '" + alias + "'");
//        }
//    }

    protected List<X509Certificate> promptUserToChooseCerts(X509Certificate[] certs) {

        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<X509Certificate> certsChosen = new ArrayList<>();

        CertLoaderDialogOld.getInstance().getCertsPanel().removeAll();

        for (X509Certificate cert : certs) {
            JCheckBox jCheckBox = new JCheckBox();
            jCheckBox.setText(cert.getSubjectDN().toString());
            jCheckBox.setSelected(true);

            CertLoaderDialogOld.getInstance().getCertsPanel().add(jCheckBox);
            checkBoxes.add(jCheckBox);
        }

        CertLoaderDialogOld.getInstance().getCertLoaderDialog().revalidate();

        String[] options = new String[]{"Install Selected Certificates", "Cancel"};

//        int result = JOptionPane.showOptionDialog(null, jPanel, "Choose Certificates",
//            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        // identified by the index of the option
//        if (result != 0) {
//            throw new ProcessAbortedException("User selected to not install the certificate.");
//        }

        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected())
                certsChosen.add(certs[i]);
        }

        return certsChosen;
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

    protected JOptionPane newInstallationPrompt() {

        return null;
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }
}
