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

    public CertLoadingError(String title, Exception e) {
        jsp = buildExceptionViewer(e);
        addListeners(title);
    }

    private void addListeners(String errorTitle) {
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
}
