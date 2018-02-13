package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.Cert;

import javax.swing.*;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class CertListWrapper implements UIComponent {
    private JPanel jPanel;

    public CertListWrapper() {
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
    }

    public void hidden() {
        jPanel.removeAll();
        jPanel.revalidate();
    }

    /**
     * Display the progress bar.
     * Utilize callbacks to alert when progress complete.
     */
    public void loading() {
        jPanel.removeAll();
        CertListLoadingProgress progressBar = new CertListLoadingProgress();
        jPanel.add(progressBar.getJPanel());
        jPanel.revalidate();

        progressBar.load(5000);
    }

    /**
     * Select certs to install.
     */
    public void listCerts(String url, List<Cert> certs, CertList.InstallCallback installCallback) {
        jPanel.removeAll();
        jPanel.add(new CertList(url, certs, installCallback).getJPanel());
        jPanel.revalidate();
    }

    public void error(String url, Exception e) {
        jPanel.removeAll();
        jPanel.add(new CertLoadingError("Error Connecting to <" + url + ">", e).getJPanel());
        jPanel.revalidate();
    }

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }
}
