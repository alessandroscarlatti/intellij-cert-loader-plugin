package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.App;
import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.controls.filechooser.WindowsFileChooser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 3/19/2018
 */
public class AppSettings implements UIComponent {

    private JTextField settingsFileText;
    private JButton selectSettingsFileButton;
    private JPanel jPanel;

    public AppSettings() {
        if (App.isPlugin) {
            settingsFileText.setEnabled(false);
            selectSettingsFileButton.setEnabled(false);
        } else {
            addListeners();
        }
    }

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }

    void addListeners() {
        selectSettingsFileButton.addActionListener(this::doChooseFile);
    }

    private void doChooseFile(ActionEvent event) {
        String filePath = chooseFile();

        if (filePath != null) {
            // TODO change the filepath
        }
    }

    private String chooseFile() {
        try {
            File selectedFile = new WindowsFileChooser()
                .withFilter("All Files", "*")
                .withTitle("Choose Settings File")
                .withInitialFile(settingsFileText.getText())
                .withParent(jPanel)
                .existingFilesOnly()
                .prompt();

            if (selectedFile != null) {
                return selectedFile.getAbsolutePath();
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JTextField getSettingsFileText() {
        return settingsFileText;
    }
}
