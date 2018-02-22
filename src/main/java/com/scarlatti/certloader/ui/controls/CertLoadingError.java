package com.scarlatti.certloader.ui.controls;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.scarlatti.certloader.ui.UIComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

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

    private JComponent jsp;  // this is the exception content
    private String errorTitle;

    public CertLoadingError() {
        addListeners();
    }

    public void init(String title, Exception e) {
        jsp = buildExceptionViewer(e);
        errorTitle = title;
    }

    private void addListeners() {
        detailsLink.setCursor(new Cursor(HAND_CURSOR));
        detailsLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                    null, jsp, errorTitle, JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JComponent buildExceptionViewer(Exception e) {
        StringBuilder sb = new StringBuilder("");
        sb.append(e.getMessage());
        sb.append("\n");
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        sb.append(errors.toString());
        JTextArea jta = new JTextArea(sb.toString());
        return new JScrollPane(jta) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 320);
            }
        };
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
