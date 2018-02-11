package com.scarlatti.certloader.plugin;

import com.scarlatti.certloader.ui.controls.CertList;
import com.scarlatti.certloader.ui.controls.CertListWrapper;
import com.scarlatti.certloader.ui.controls.URLToolbar;
import com.scarlatti.certloader.ui.model.Cert;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Sunday, 2/11/2018
 */
public class LoadAction extends URLToolbar.AbstractLoadAction {

    private Thread loadingThread;
    private CertListWrapper certListWrapper;

    public LoadAction(CertListWrapper certListWrapper) {
        this.certListWrapper = certListWrapper;
    }

    @Override
    public void load(String url, ActionCompletedCallback callback, ActionCompletedCallback errorCallback) {
        // do loading here...

        loadingThread = new Thread(() -> {

            try {
                certListWrapper.loading();

                List<X509Certificate> rawCerts = CertDownloader.downloadCerts(url);
                List<Cert> certs = buildViewModelCerts(rawCerts);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    callback.callback();
                    return;
                }

                certListWrapper.listCerts(url, certs, CertList.noOpInstallCallback());
                callback.callback();
            } catch (ProcessAbortedException e) {
                e.printStackTrace();  // TODO when this is caught, we should fire off the cancel action
                errorCallback.callback();
                certListWrapper.hidden();
            } catch (SSLConnectionException e) {
                e.printStackTrace();

                new Thread(() -> {
                    showErrorDialog("Error", e.getMessage(), e);
                }).start();

                errorCallback.callback();
                certListWrapper.hidden();
            }
        });

        loadingThread.start();
    }

    @Override
    public void cancel(ActionCompletedCallback callback) {
        // cancel loading here...
        loadingThread.interrupt();
        certListWrapper.hidden();
        callback.callback();
    }

    public static void showErrorDialog(String title, String message, Exception e) {
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


    /**
     * Convert to View Model certs.  TODO get the correct info!
     * @param rawCerts
     * @return
     */
    public static List<Cert> buildViewModelCerts(List<X509Certificate> rawCerts) {
        List<Cert> certs = new ArrayList<>();

        int i = 0;
        for (X509Certificate cert : rawCerts) {
            certs.add(new Cert(
                i,
                true,
                cert.getIssuerDN().getName(),
                cert.getIssuerDN().getName()
            ));
            i++;
        }

        return certs;
    }


}
