package com.scarlatti.certloader.dialog;

import javax.swing.*;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Thursday, 1/4/2018
 */
public class CertLoaderDialog {
    private JTextField textField1;
    private JButton getCertificatesCancelButton;
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("CertLoaderDialog");
        frame.setContentPane(new CertLoaderDialog().certLoaderDialog);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
