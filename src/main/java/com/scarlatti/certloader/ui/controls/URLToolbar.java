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
    private LoadAction loadAction = noOpLoadAction();

    public URLToolbar() {
        setupButton();
    }

    private void setupButton() {
        downloadButton.addActionListener(this::onDownloadButtonClick);
    }

    private void onDownloadButtonClick(ActionEvent e) {
        if (loading) {
            System.out.println("Clicked Cancel.");
            cancel();
        } else {
            System.out.println("Clicked Download.");
            load();
        }
    }

    public URLToolbar(LoadAction loadAction) {
        this.loadAction = loadAction;
    }

    public void load() {
        setLoading(true);

        // ... and do the loading stuff here...
        loadAction.load(url.getText(), () -> {
            setLoading(false);
        });
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

    public static abstract class LoadAction {
        public abstract void load(String url, ActionCompletedCallback callback);
        public abstract void cancel(ActionCompletedCallback callback);

        @FunctionalInterface
        public interface ActionCompletedCallback {
            void callback();
        }
    }

    public static LoadAction noOpLoadAction() {
        return new LoadAction() {
            @Override
            public void load(String url, ActionCompletedCallback callback) {
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

    public LoadAction getLoadAction() {
        return loadAction;
    }

    public void setLoadAction(LoadAction loadAction) {
        this.loadAction = loadAction;
    }
}
