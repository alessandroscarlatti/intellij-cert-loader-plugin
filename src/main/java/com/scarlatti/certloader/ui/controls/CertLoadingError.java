package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.Cursor.HAND_CURSOR;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 2/12/2018
 */
public class CertLoadingError implements UIComponent {
    private JPanel jPanel;
    private JLabel detailsLink;

    private JComponent exceptionViewer;  // this is the exception content
    private String errorTitle;

    public CertLoadingError() {
        addListeners();
    }

    public void init(String title, Exception e) {
        exceptionViewer = Utils.buildExceptionViewer(e);
        errorTitle = title;
    }

    private void addListeners() {
        detailsLink.setCursor(new Cursor(HAND_CURSOR));
        detailsLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(jPanel), exceptionViewer, errorTitle, JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    public JPanel getJPanel() {
        return jPanel;
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
