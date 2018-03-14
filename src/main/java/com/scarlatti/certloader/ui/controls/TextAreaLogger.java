package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 3/12/2018
 */
public class TextAreaLogger implements UIComponent {
    private JTextArea logger;
    private JPanel jPanel;
    private JButton clearButton;

    public static PrintStream originalStdOut;
    public static PrintStream originalStdErr;

    private PrintStream capturingStdOut;
    private PrintStream capturingStdErr;

    public TextAreaLogger() {
        initLogger();
        setupClearAction();
    }

    public void initLogger() {
        originalStdOut = System.out;
        originalStdErr = System.err;

        capturingStdOut = capturingOutputStream(originalStdOut);
        System.setOut(capturingStdOut);

        capturingStdErr = capturingOutputStream(originalStdErr);
        System.setErr(capturingStdErr);
    }

    public void setupClearAction() {
        clearButton.addActionListener((actionEvent) -> {
            logger.setText("");
        });
    }

    public PrintStream capturingOutputStream(PrintStream originalStream) {
        return new PrintStream(
            new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    originalStream.print((char) b);
                    TextAreaLogger.this.write(b);
                }
            }
        );
    }

    private void write(int b) {
        SwingUtilities.invokeLater(() -> {
            logger.append(String.valueOf((char) b));
        });
    }

    public void destroyLogger() {
        try {
            // capturingStdOut.close();
            // capturingStdErr.close();
            // we don't want to actually close the source of std out...

        } finally {
            capturingStdOut = null;
            capturingStdErr = null;
            System.setOut(originalStdOut);
            System.setErr(originalStdErr);
        }
    }

    @Override
    public JPanel getJPanel() {
        return null;
    }
}
