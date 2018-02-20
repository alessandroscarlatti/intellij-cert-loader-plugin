package com.scarlatti.certloader.ui.controls;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.Cert;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
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

    public CertListWrapper() {
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        hidden();
    }

    public void hidden() {
        jPanel.removeAll();
        jPanel.add(new CertList("", Collections.emptyList(), certs -> {
        }).getJPanel());
//        jPanel.setPreferredSize(new Dimension(500, 300));
        jPanel.revalidate();
    }

    /**
     * Display the progress bar.
     * Utilize callbacks to alert when progress complete.
     */
    public void loading(Runnable onTimeout) {
        jPanel.removeAll();
        progressBar = new CertListLoadingProgress();
        jPanel.add(progressBar.getJPanel(), new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, /*GridConstraints.SIZEPOLICY_CAN_SHRINK |*/ GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        jPanel.revalidate();
        jPanel.getParent().revalidate();
        SwingUtilities.getWindowAncestor(jPanel).pack();

        progressBar.load(1000, onTimeout);
    }

    public void stopLoading() {
        progressBar.stop();
    }

    /**
     * Select certs to install.
     */
    public void listCerts(String url, List<Cert> certs, CertList.InstallCallback installCallback) {
        jPanel.removeAll();
        jPanel.add(new CertList(url, certs, installCallback).getJPanel(), new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
//        jPanel.setMinimumSize(new Dimension(-1, -1));
        jPanel.setPreferredSize(new Dimension(jPanel.getParent().getWidth(), 150));
        jPanel.revalidate();
        jPanel.getParent().revalidate();
        SwingUtilities.getWindowAncestor(jPanel).pack();
    }

    public void error(String url, Exception e) {
        jPanel.removeAll();
        jPanel.add(new CertLoadingError("Error Connecting to <" + url + ">", e).getJPanel(), new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        jPanel.revalidate();
        jPanel.getParent().revalidate();
        SwingUtilities.getWindowAncestor(jPanel).pack();
    }

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }
}
