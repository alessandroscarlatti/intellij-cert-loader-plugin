package com.scarlatti.certloader.ui.controls;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.Cert;

import javax.swing.*;
import java.awt.*;
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
    private CertListLoadingProgress progressBar;
    private CertLoadingError errorMessage;
    private CertList certList;
    private WelcomeMessage welcomeMessage;
    private CardLayout layout;

    public CertListWrapper() {
        layout = (CardLayout) jPanel.getLayout();
        layout.show(jPanel, "welcomeMessage");
    }

    public void welcome() {
        layout.show(jPanel, "welcomeMessage");
    }

    /**
     * Display the progress bar.
     * Utilize callbacks to alert when progress complete.
     */
    public void loading(Runnable onTimeout) {
        layout.show(jPanel, "progressBar");

        progressBar.load(10000, onTimeout);
    }

    public void stopLoading() {
        progressBar.stop();
    }

    /**
     * Select certs to install.
     */
    public void listCerts(String url, List<Cert> certs) {
        certList.init(url, certs);
        layout.show(jPanel, "certList");
    }

    public void listCerts(String url, List<Cert> certs, CertList.InstallCallback installCallback) {
        certList.init(url, certs);
        layout.show(jPanel, "certList");
    }

    public void error(String url, Exception e) {
        layout.show(jPanel, "errorMessage");
        errorMessage.init("Error Connecting to <" + url + ">", e);
    }

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }

    public CertList getCertList() {
        return certList;
    }

    public void setCertList(CertList certList) {
        this.certList = certList;
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

}
