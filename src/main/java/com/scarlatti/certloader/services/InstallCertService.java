package com.scarlatti.certloader.services;

import com.scarlatti.certloader.exceptions.AccessDeniedException;
import com.scarlatti.certloader.services.dyorgio.runAsRoot.RootExecutor;
import com.scarlatti.certloader.services.dyorgio.runAsRoot.UserCanceledException;
import com.scarlatti.certloader.ui.model.Cert;
import com.scarlatti.certloader.ui.model.KeyStore;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.scarlatti.certloader.services.CertLoaderUtils.resourceStr;
import static java.lang.String.format;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Tuesday, 2/20/2018
 */
public class InstallCertService implements Serializable {

    private transient JPanel parent;
    private transient Path workingDir = Paths.get(System.getProperty("user.home"), ".CertLoader");

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
                try {
                    installWithBat(certs, keyStores);
//                    installAsCurrentUser(certs, keyStores);
                } catch (AccessDeniedException e) {
                    installAsRoot(certs, keyStores);
                }
            }

            // if all were successful show a success message.
//            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(parent), "Successfully installed " + certs.size() + " certificate(s) to " + keyStores.size() + " key stores.");

        } catch (UserCanceledException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // if all failed print the exception and show a failed message
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                parent , buildExceptionViewer(e), "Error installing certificate(s)", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Save the certs as files, and generate a bat file installer.
     * @param certs the certs to install
     * @param keyStores the key stores to install the certs into
     */
    private void installWithBat(List<Cert> certs, List<KeyStore> keyStores) {
        // build the install script
        try {
            String correlationId = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSS").format(new Date());
            String installScript = buildInstallerBat(certs, keyStores, correlationId);
            System.out.println("INSTALL SCRIPT:");
            System.out.println(installScript);

            Path installDir = workingDir.resolve("CertLoader-" + correlationId);
            System.out.println("Saving files to " + installDir);
            Files.createDirectories(installDir);

            // save the cert files
            for (int i = 0; i < certs.size(); i++) {
                Cert cert = certs.get(i);
                try {
                    Files.write(installDir.resolve(buildCertFileName(cert, i, correlationId)), cert.getRawCert().getEncoded());
                } catch (Exception e) {
                    throw new RuntimeException("Error writing cert file for cert: " + cert, e);
                }
            }

            // save the bat file
            Files.write(installDir.resolve("InstallCerts.bat"), installScript.getBytes());

            // save the readme file
            Files.write(installDir.resolve("README.TXT"), resourceStr("/README.TXT").getBytes());

            Desktop.getDesktop().open(installDir.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Error installing certs with .bat file", e);
        }
    }

    private String buildCertFileName(Cert cert, int index, String correlationId) {
        String alias = getAlias(cert, index, correlationId);
        return alias.replace("https://", "").replace("/", ".").replace("\\", ".").replace(":", ".") + ".cer";
    }

    private String buildInstallerBat(List<Cert> certs, List<KeyStore> keyStores, String correlationId) {
        StringBuilder sbInstallScript = new StringBuilder(resourceStr("/InstallCerts.template.bat"));
        Path installDir = workingDir.resolve("CertLoader-" + correlationId);

        StringBuilder sbCommands = new StringBuilder();
        for (KeyStore keyStore : keyStores) {
            StringBuilder sbKeyStoreScript = new StringBuilder(resourceStr("/InstallCert.template.bat"));
            StringBuilder sbCertsList = new StringBuilder();
            sbKeyStoreScript.append("\n");
            for (int i = 0; i < certs.size(); i++) {
                Cert cert = certs.get(i);
                String alias = getAlias(cert, i, correlationId);
                String fileName = buildCertFileName(cert, i, correlationId);
                Path certFile = installDir.resolve(fileName);
                sbCertsList.append("@rem - " + cert.getUrl() + "\n");
                sbKeyStoreScript.append(format("\"%%KEYTOOL%%\" -import -alias \"%s\" -file \"%s\" -keystore \"%s\" -storepass \"%s\" -noprompt & %%ERRORCHECK%% %n", alias, certFile, keyStore.getPath(), keyStore.getPassword()));
            }
            sbCertsList.append("@rem");
            String strKeyStoreScript = sbKeyStoreScript.toString();
            strKeyStoreScript = strKeyStoreScript.replace("{KEYSTORE_NAME}", keyStore.getName());
            strKeyStoreScript = strKeyStoreScript.replace("{KEYSTORE_PATH}", keyStore.getPath());
            strKeyStoreScript = strKeyStoreScript.replace("@rem {CERTS}", sbCertsList);
            sbCommands.append("\n\n");
            sbCommands.append(strKeyStoreScript);
        }

        String strInstallScript = sbInstallScript.toString();
        strInstallScript = strInstallScript.replace("{COMMANDS}", sbCommands);

        StringBuilder sbCertsList = new StringBuilder();
        for (int i = 0; i < certs.size(); i++) {
            Cert cert = certs.get(i);
            sbCertsList.append("@rem - " + cert.getUrl() + "\n");
        }
        sbCertsList.append("@rem");
        strInstallScript = strInstallScript.replace("@rem {CERTS}", sbCertsList);

        StringBuilder sbKeyStores = new StringBuilder();
        for (KeyStore keyStore : keyStores) {
            sbKeyStores.append("@rem - " + keyStore.getName() + ": " + keyStore.getPath() + "\n");
        }
        sbKeyStores.append("@rem");
        strInstallScript = strInstallScript.replace("@rem {KEYSTORES}", sbKeyStores);

        return strInstallScript;
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
                    throw new AccessDeniedException("User does not possess sufficient privilege to write to keystore at " + keyStore.getPath() + ". Consider restarting this application as an Administrator.");
                }

                for (int i = 0; i < certs.size(); i++) {
                    Cert cert = certs.get(i);
                    String alias = getAlias(cert, i, new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSS").format(new Date()));

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
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            // if all failed show a failed message
            throw new RuntimeException("Error installing certificate(s).  See exception message for details...", e);
        }
    }

    private static String getAlias(Cert cert, int certIndex, String correlationId) {
        return cert.getUrl() + "-" + (certIndex + 1) + "-" + correlationId;
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
