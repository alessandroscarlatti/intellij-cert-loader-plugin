package com.scarlatti.certloader.ui;

import com.intellij.ide.ui.laf.darcula.DarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class Utils {

    public static void displayJPanel(JPanelProvider jPanelProvider, JPanelDisplayedCallback... jPanelDisplayedCallbacks) {

        setDarculaLaf();

        JFrame frame = new JFrame("JPanel Wrapper");
        JPanel jPanel = jPanelProvider.provideJPanel();


        frame.setContentPane(jPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        if (jPanelDisplayedCallbacks.length > 0) {
            jPanelDisplayedCallbacks[0].callback(jPanel);
        }

        CountDownLatch latch = new CountDownLatch(1);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error awaiting window close.", e);
        }
    }

    public static void setDarculaLaf() {
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
            UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(1,0,0,0));
            UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface JPanelProvider {
        JPanel provideJPanel();
    }

    @FunctionalInterface
    public interface JPanelDisplayedCallback {
        void callback(JPanel jPanel);
    }
}
