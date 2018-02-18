package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class URLToolbar implements UIComponent {
    private JTextField url;
    private JButton downloadButton;
    private JPanel jPanel;

    private boolean loading = false;
    private AbstractLoadAction loadAction = noOpLoadAction();

    public URLToolbar() {
        setupListeners();
    }

    private void setupListeners() {
        downloadButton.addActionListener(this::doStartDownload);
        url.addActionListener(this::doStartDownload);
    }

    private void doStartDownload(ActionEvent e) {
        if (loading) {
            cancel();
        } else {
            load();
        }
    }

    public URLToolbar(AbstractLoadAction loadAction) {
        this.loadAction = loadAction;
    }

    public void load() {
        setLoading(true);

        new Thread(() -> {
            // ... and do the loading stuff here...
            loadAction.load(url.getText(), () -> {
                setLoading(false);
            }, () -> {
                setLoading(false);
            });
        }).start();
    }

    public void cancel() {

        // ... and do the canceling here...
        loadAction.cancel(() -> {
            setLoading(false);
        });
    }

    public void setLoading(boolean loading) {
        this.loading = loading;

        if (loading) {
            loadingMode();
        } else {
            resetMode();
        }
    }

    private void loadingMode() {
        url.setEnabled(false);
        downloadButton.setText("Cancel");
    }

    private void resetMode() {
        url.setEnabled(true);
        downloadButton.setText("Download Certificate(s)");
    }

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }

    public static abstract class AbstractLoadAction {
        public abstract void load(String url, ActionCompletedCallback callback, ActionCompletedCallback errorCallback);
        public abstract void cancel(ActionCompletedCallback callback);

        @FunctionalInterface
        public interface ActionCompletedCallback {
            void callback();
        }
    }

    public static AbstractLoadAction noOpLoadAction() {
        return new AbstractLoadAction() {
            @Override
            public void load(String url, ActionCompletedCallback callback, ActionCompletedCallback errorCallback) {
                new Thread(() -> {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    callback.callback();
                }).start();
            }

            @Override
            public void cancel(ActionCompletedCallback callback) {
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    callback.callback();
                }).start();
            }
        };
    }

    public AbstractLoadAction getLoadAction() {
        return loadAction;
    }

    public void setLoadAction(AbstractLoadAction loadAction) {
        this.loadAction = loadAction;
    }
}
