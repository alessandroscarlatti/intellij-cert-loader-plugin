package com.scarlatti.certloader.plugin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.scarlatti.certloader.interfaces.*;
import com.scarlatti.certloader.services.InstallCertService;
import com.scarlatti.certloader.services.LoadCertService;
import com.scarlatti.certloader.ui.Utils;
import com.scarlatti.certloader.ui.controls.CertLoaderDialog;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 3/7/2018
 */
@Singleton
public class LocalAppRunner implements Runnable {

    private UrlInterface urlInterface;
    private CertDownloadingInterface certDownloadingInterface;
    private CertSelectionInterface certSelectionInterface;
    private KeyStoreSelectionInterface keyStoreSelectionInterface;
    private SettingsInterface settingsInterface;
    private CertLoaderDialog certLoaderDialog;

    @Inject
    public LocalAppRunner(UrlInterface urlInterface,
                          CertDownloadingInterface certDownloadingInterface,
                          CertSelectionInterface certSelectionInterface,
                          KeyStoreSelectionInterface keyStoreSelectionInterface,
                          SettingsInterface settingsInterface,
                          CertLoaderDialog certLoaderDialog) {
        this.urlInterface = urlInterface;
        this.certDownloadingInterface = certDownloadingInterface;
        this.certSelectionInterface = certSelectionInterface;
        this.keyStoreSelectionInterface = keyStoreSelectionInterface;
        this.settingsInterface = settingsInterface;
        this.certLoaderDialog = certLoaderDialog;
    }

    @Override
    public void run() {
        certLoaderDialog.getUrlToolbar().setLoadAction(
            new LoadCertService(certLoaderDialog.getCertListWrapper())
        );

        certLoaderDialog.getCertListWrapper().getCertList().setInstallCallback(certs -> {
            new InstallCertService(certLoaderDialog.getJPanel()).install(certs, certLoaderDialog.getAppManager().getListKeyStores().getCheckedKeyStores());
        });

        Utils.displayJPanel(certLoaderDialog::getJPanel);

        certLoaderDialog.saveOnClose();
    }
}
