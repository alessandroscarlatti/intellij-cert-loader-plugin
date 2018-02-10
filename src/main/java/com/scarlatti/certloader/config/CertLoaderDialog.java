package com.scarlatti.certloader.config;

import com.scarlatti.certloader.plugin.CertInstaller;

import javax.swing.*;
import java.awt.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Friday, 2/9/2018
 */
public class CertLoaderDialog extends JPanel {
    private JTextField urlTextBox;
    private JPanel certsPanel;

    private static CertLoaderDialog instance;

    /**
     * Create the panel.
     */
    public CertLoaderDialog() {

        instance = this;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        add(panel);

        JLabel lblLoadSslCertificate = new JLabel("Install SSL Certificate");
        lblLoadSslCertificate.setFont(new Font("Dialog", Font.BOLD, 24));
        panel.add(lblLoadSslCertificate);

        JPanel panel_1 = new JPanel();
        add(panel_1);
        SpringLayout sl_panel_1 = new SpringLayout();
        panel_1.setLayout(sl_panel_1);

        JLabel lblNewLabel = new JLabel("URL:");
        sl_panel_1.putConstraint(SpringLayout.NORTH, lblNewLabel, 14, SpringLayout.NORTH, panel_1);
        sl_panel_1.putConstraint(SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, panel_1);
        panel_1.add(lblNewLabel);

        urlTextBox = new JTextField();
        sl_panel_1.putConstraint(SpringLayout.NORTH, urlTextBox, -4, SpringLayout.NORTH, lblNewLabel);
        sl_panel_1.putConstraint(SpringLayout.WEST, urlTextBox, 6, SpringLayout.EAST, lblNewLabel);
        sl_panel_1.putConstraint(SpringLayout.EAST, urlTextBox, -197, SpringLayout.EAST, panel_1);
        panel_1.add(urlTextBox);
        urlTextBox.setColumns(10);

        JButton btnNewButton = new JButton("Download Certificate(s)");
        sl_panel_1.putConstraint(SpringLayout.NORTH, btnNewButton, -8, SpringLayout.NORTH, lblNewLabel);
        sl_panel_1.putConstraint(SpringLayout.WEST, btnNewButton, 6, SpringLayout.EAST, urlTextBox);
        sl_panel_1.putConstraint(SpringLayout.EAST, btnNewButton, -10, SpringLayout.EAST, panel_1);
        panel_1.add(btnNewButton);

        certsPanel = new JPanel();
        add(certsPanel, BorderLayout.SOUTH);
        certsPanel.setLayout(new BoxLayout(certsPanel, BoxLayout.Y_AXIS));

//        JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
//        panel_2.add(chckbxNewCheckBox);
//
//        JCheckBox chckbxNewCheckBox_1 = new JCheckBox("New check box");
//        panel_2.add(chckbxNewCheckBox_1);
//
//        JCheckBox chckbxNewCheckBox_2 = new JCheckBox("New check box");
//        panel_2.add(chckbxNewCheckBox_2);

        btnNewButton.addActionListener((e -> {
            CertInstaller.installCert();
        }));

    }

    public JTextField getUrlTextBox() {
        return urlTextBox;
    }

    public JPanel getCertsPanel() {
        return certsPanel;
    }

    public static CertLoaderDialog getInstance() {
        return instance;
    }
}
