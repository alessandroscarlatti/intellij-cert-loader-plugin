package com.scarlatti.certloader.ui.controls;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.scarlatti.certloader.ui.UIComponent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 2/19/2018
 */
public class AppManager implements UIComponent {
    private JPanel jPanel;
    private ListKeyStores listKeyStores;
    private TextAreaLogger textAreaLogger;
    private AppSettings settings;

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }

    public ListKeyStores getListKeyStores() {
        return listKeyStores;
    }

    public void setListKeyStores(ListKeyStores listKeyStores) {
        this.listKeyStores = listKeyStores;
    }

    public TextAreaLogger getTextAreaLogger() {
        return textAreaLogger;
    }

    public AppSettings getSettings() {
        return settings;
    }

    public void setSettings(AppSettings settings) {
        this.settings = settings;
    }


}
