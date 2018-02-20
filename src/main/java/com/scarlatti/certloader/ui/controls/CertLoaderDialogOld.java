package com.scarlatti.certloader.ui.controls;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.scarlatti.certloader.plugin.CertInstaller;
import com.scarlatti.certloader.ui.UIComponent;

import javax.swing.*;
import java.awt.*;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Thursday, 1/4/2018
 */
public class CertLoaderDialogOld implements UIComponent {
    private JTextField urlTextBox;
    private JButton getCertsButton;
    private JTextArea thisBoxWouldBeTextArea;
    private JButton installCertificatesButton;
    private JCheckBox intelliJCacertsCheckBox;
    private JCheckBox java8CheckBox;
    private JTextField textField2;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JPanel certLoaderDialog;
    private JCheckBox checkBox1;
    private JCheckBox selectTheCertsYouCheckBox;
    private JPanel certsPanel;
    private JTable table1;

    private static CertLoaderDialogOld instance;

    public CertLoaderDialogOld() {

        instance = this;

        getCertsButton.addActionListener((e -> {
            try {
                new Thread(() -> {
                    CertInstaller.installCert();
                }).start();

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }));
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public JPanel getCertLoaderDialog() {
        return certLoaderDialog;
    }

    public String provideTitle() {
        return "Install SSL Certificate...";
    }

    public JTextField getUrlTextBox() {
        return urlTextBox;
    }

    public JPanel getCertsPanel() {
        return certsPanel;
    }

    public static CertLoaderDialogOld getInstance() {
        return instance;
    }

    @Override
    public JPanel getJPanel() {
        return certLoaderDialog;
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
