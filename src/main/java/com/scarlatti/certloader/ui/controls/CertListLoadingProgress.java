package com.scarlatti.certloader.ui.controls;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.scarlatti.certloader.ui.UIComponent;

import javax.swing.*;
import java.awt.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class CertListLoadingProgress implements UIComponent {
    private JProgressBar progressBar;
    private JPanel jPanel;

    private Thread progressBarThread;
    private ThreadLocal<Boolean> timedOut;

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }

    public void load(final int timeoutMs, Runnable timeoutCallback) {
        progressBar.setMaximum(timeoutMs);

        // need a variable for this thread so that we can interrupt it later!
        progressBarThread = new Thread(() -> {
            final int REFRESH_RATE_MS = 15;

            for (int i = 0; i * REFRESH_RATE_MS < timeoutMs; i++) {
                try {
                    progressBar.setValue(i * (timeoutMs / (timeoutMs / REFRESH_RATE_MS)));
                    Thread.sleep(REFRESH_RATE_MS);
                } catch (InterruptedException e) {
                    return;
                }
            }

            // now the timer has timed out...
            timeoutCallback.run();
        }, "ProgressBar");

        progressBarThread.start();
    }

    public void stop() {
        progressBarThread.interrupt();
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
