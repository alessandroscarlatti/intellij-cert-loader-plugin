package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.plugin.AppState;
import com.scarlatti.certloader.services.Repository;
import com.scarlatti.certloader.ui.UIComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    private AppManager appManager;
    private Repository<AppState> repository;
    private AppState appState;

    public CertLoaderDialog() {
        jPanel.setPreferredSize(new Dimension(640, 580));
        appState = AppState.defaultState();
        loadState();
    }

    public CertLoaderDialog(Repository<AppState> repository) {
        jPanel.setPreferredSize(new Dimension(640, 580));
        this.repository = repository;

        appState = repository.retrieve();

        loadState();

        // setup app closing hooks
        setupHooks();
    }

    private void loadState() {
        if (appState != null) {
            if (appState.getKeyStores() != null) {
                appManager.getListKeyStores().setCurrent(appState.getKeyStores());
            }

            if (appState.getMostRecentUrl() != null) {
                urlToolbar.setUrl(appState.getMostRecentUrl());
            }
        }
    }

    public void saveOnClose() {
        if (repository != null) {

            appState.setKeyStores(appManager.getListKeyStores().getCurrent());
            appState.setMostRecentUrl(certListWrapper.getCertList().getUrl());

            repository.save(appState);
        }
    }

    private void setupHooks() {
//        SwingUtilities.getWindowAncestor(jPanel);
    }

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

    public AppManager getAppManager() {
        return appManager;
    }

    public void setAppManager(AppManager appManager) {
        this.appManager = appManager;
    }

}
