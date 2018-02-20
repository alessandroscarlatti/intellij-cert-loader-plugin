package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;

import javax.swing.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 2/19/2018
 */
public class AppManager implements UIComponent {
    private JTabbedPane tabbedPane1;
    private JPanel jPanel;

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }

}
