package com.scarlatti.certloader.ui.controls;

import com.google.inject.Inject;
import com.scarlatti.certloader.services.LoadCertService;
import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.controls.filechooser.WindowsFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

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
    private JButton chooseCertFileButton;

    private boolean loading = false;
    private AbstractLoadAction loadAction = noOpLoadAction();

    @Inject
    public URLToolbar(LoadCertService loadCertService) {
        loadAction = loadCertService;
    }

    public URLToolbar() {
        setupListeners();
    }

    private void setupListeners() {
        downloadButton.addActionListener(this::doStartDownload);
        url.addActionListener(this::doStartDownload);
        chooseCertFileButton.addActionListener(this::doChooseFile);
    }

    private void doStartDownload(ActionEvent e) {
        if (loading) {
            cancel();
        } else {
            loadUrl();
        }
    }

    private void doChooseFile(ActionEvent event) {
        if (!loading) {
            String filePath = chooseFile();
            loadFile(filePath);
        }
    }

    private String chooseFile() {
        try {
            File selectedFile = new WindowsFileChooser()
                .withFilter("All Files", "*")
                .withTitle("Choose Certificate File")
                .withInitialFile(url.getText())
                .withParent(jPanel)
                .existingFilesOnly()
                .prompt();

            if (selectedFile != null) {
                return selectedFile.getAbsolutePath();
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public URLToolbar(AbstractLoadAction loadAction) {
        this.loadAction = loadAction;
    }

    public void loadUrl() {
        setLoading(true);

        if (isLocal(url.getText())) {
            loadFile(url.getText());
            return;
        }

        new Thread(() -> {
            // ... and do the loading stuff here...
            loadAction.load(url.getText(), () -> {
                setLoading(false);
            }, () -> {
                setLoading(false);
            });
        }).start();
    }

    public boolean isLocal(String url) {
        return url.startsWith("\\") || url.startsWith("//") || url.toUpperCase().startsWith("C:");
    }

    public void loadFile(String path) {
        setLoading(true);

        setUrl(path);

        new Thread(() -> {
            // ... and do the loading stuff here...
            loadAction.loadFromFile(path, null, () -> {
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

    public static abstract class AbstractLoadAction {
        public abstract void load(String url, ActionCompletedCallback callback, ActionCompletedCallback errorCallback);

        public abstract void loadFromFile(String path, byte[] bytes, ActionCompletedCallback callback, ActionCompletedCallback errorCallback);

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
            public void loadFromFile(String path, byte[] bytes, ActionCompletedCallback callback, ActionCompletedCallback errorCallback) {

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

    public void setUrl(String url) {
        this.url.setText(url);
    }
}
