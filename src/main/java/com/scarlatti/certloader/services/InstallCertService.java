package com.scarlatti.certloader.services;

import com.scarlatti.certloader.services.dyorgio.runAsRoot.RootExecutor;
import com.scarlatti.certloader.ui.model.Cert;
import com.scarlatti.certloader.ui.model.KeyStore;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Tuesday, 2/20/2018
 */
public class InstallCertService implements Serializable {

    private transient JPanel parent;

    public InstallCertService(JPanel parent) {
        this.parent = parent;
    }

    /**
     * Install the given certs into the given keystores.
     *
     * @param certs the raw certs to install
     * @param keyStores the key stores to use
     */
    public void install(List<Cert> certs, List<KeyStore> keyStores) {
        try {

            if (keyStores.size() > 0) {
//                installAsCurrentUser(certs, keyStores);
                installAsRoot(certs, keyStores);
            }

            // if all were successful show a success message.
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(parent), "Successfully installed " + certs.size() + " certificate(s) to " + keyStores.size() + " key stores.");

        } catch (Exception e) {
            // if all failed print the exception and show a failed message
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                parent , buildExceptionViewer(e), "Error installing certificate(s)", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void installAsCurrentUser(List<Cert> certs, List<KeyStore> keyStores) throws Exception {
        doInstall(certs, keyStores);
    }

    private void installAsRoot(List<Cert> certs, List<KeyStore> keyStores) throws Exception {

        ClassLoader classLoader = getURLClassLoader();

        Thread.currentThread().setContextClassLoader(classLoader);
        RootExecutor rootExecutor = new RootExecutor();
        rootExecutor.call(() -> {
            doInstall(certs, keyStores);
            return "done";
        });
    }

    private URLClassLoader getURLClassLoader() {

        ClassLoader classLoader = this.getClass().getClassLoader();

        System.out.println("this classloader: " + classLoader);
        System.out.println("has class: " + classLoader.getClass().getName());

        if (classLoader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) classLoader).getURLs();
            return new URLClassLoader(urls, classLoader);
        } else {
            try {
                return getIntellijCustomClassLoader(classLoader);
            } catch (Exception e) {
                throw new IllegalStateException(
                    "Cannot provide adequate classloader.  Need java.net.URLClassloader, but current classloader is: " +
                        classLoader.getClass().getName(), e);
            }
        }
    }

    private URLClassLoader getIntellijCustomClassLoader(ClassLoader classLoader) {
        try {
            URL[] urls = ((List<URL>)classLoader.getClass().getMethod("getUrls").invoke(classLoader)).toArray(new URL[]{});
            return new URLClassLoader(urls);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get appropriate classloader!", e);
        }
    }

    private void doInstall(List<Cert> certs, List<KeyStore> keyStores) {
        try {
            for (KeyStore keyStore : keyStores) {

                if (!Files.isWritable(Paths.get(keyStore.getPath()))) {
                    throw new IllegalStateException("User does not possess sufficient privilege to write to keystore at " + keyStore.getPath() + ". Consider restarting this application as an Administrator.");
                }

                for (int i = 0; i < certs.size(); i++) {
                    Cert cert = certs.get(i);
                    String alias = cert.getUrl() + "-" + (i + 1) + "-" + new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSS").format(new Date());

                    java.security.KeyStore localKeyStore = java.security.KeyStore.getInstance(java.security.KeyStore.getDefaultType());

                    try (FileInputStream in = new FileInputStream(new File(keyStore.getPath()))) {
                        localKeyStore.load(in, keyStore.getPassword().toCharArray());
                    }

                    localKeyStore.setCertificateEntry(alias, cert.getRawCert());
                    try (OutputStream out = new FileOutputStream(keyStore.getPath())) {
                        localKeyStore.store(out, keyStore.getPassword().toCharArray());
                    }

                    System.out.println();
                    System.out.println(cert);
                    System.out.println();
                    System.out.println("Added certificate to keystore " + keyStore.getPath() + " using alias '" + alias + "'");
                }
            }
        } catch (Exception e) {
            // if all failed show a failed message
            throw new RuntimeException("Error installing certificate(s).  See exception message for details...", e);
        }
    }

    private JComponent buildExceptionViewer(Exception e) {
        StringBuilder sb = new StringBuilder("");
        sb.append(e.getMessage());
        sb.append("\n");
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        sb.append(errors.toString());
        JTextArea jta = new JTextArea(sb.toString());
        return new JScrollPane(jta) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 320);
            }
        };
    }
}
