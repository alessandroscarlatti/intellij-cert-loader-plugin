package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;

import javax.swing.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Sunday, 2/11/2018
 */
public class CertLoaderDialog implements UIComponent {

    private JPanel jPanel;
    private URLToolbar urlToolbar;
    private CertListWrapper certListWrapper;

    public URLToolbar getUrlToolbar() {
        return urlToolbar;
    }

    public CertListWrapper getCertListWrapper() {
        return certListWrapper;
    }

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }
}
