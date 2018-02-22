package com.scarlatti.certloader.ui.controls;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.KeyStore;
import com.scarlatti.certloader.ui.controls.filechooser.WindowsFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class EditKeystore extends JDialog implements UIComponent {
    private JPanel jPanel;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField nameField;
    private JButton chooseKeystoreButton;
    private JTextField pathField;
    private JPasswordField passwordField;

    private KeyStore keyStore;
    private SaveKeyStoreCallback callback = noOpSaveKeyStoreCallback();

    public EditKeystore() {
        keyStore = defaultKeyStore();

        initializeUI();
        initializeValues();
    }

    public EditKeystore(KeyStore keyStore) {
        this.keyStore = keyStore;

        initializeUI();
        initializeValues();
    }

    public EditKeystore(KeyStore keyStore, SaveKeyStoreCallback callback) {
        this.keyStore = keyStore;
        this.callback = callback;

        initializeUI();
        initializeValues();
    }

    public EditKeystore(JFrame parent, KeyStore keyStore, SaveKeyStoreCallback callback) {
        super(parent);

        this.keyStore = keyStore;
        this.callback = callback;

        initializeUI();
        initializeValues();
    }

    public EditKeystore(Dialog parent, KeyStore keyStore, SaveKeyStoreCallback callback) {
        super(parent);

        this.keyStore = keyStore;
        this.callback = callback;

        initializeUI();
        initializeValues();
    }

    private void initializeUI() {
        setContentPane(jPanel);
        setModal(true);
        getRootPane().setDefaultButton(saveButton);
        pack();
        setMinimumSize(new Dimension(getWidth(), getHeight()));
        setLocationRelativeTo(getOwner());
        setTitle("Edit Keystore...");

        setupActions();
    }

    /**
     * Return a new keystore edited from the given keystore.
     *
     * @param original the original keystore
     * @return the edited keystore, may be the same data as the original.
     * always a different instance.
     */
    public static KeyStore editKeyStore(KeyStore original, ParentProvider parentProvider) {

        AtomicReference<KeyStore> editedKeyStore = new AtomicReference<>(original);
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            EditKeystore dialog = null;

            Object parent = parentProvider.provideParent();

            if (parent instanceof JFrame) {
                dialog = new EditKeystore((JFrame) parent, new KeyStore(original), (KeyStore newKeyStore) -> {
                    editedKeyStore.set(newKeyStore);
                    latch.countDown();
                });
            }

            if (parent instanceof Dialog) {
                dialog = new EditKeystore((Dialog) parent, new KeyStore(original), (KeyStore newKeyStore) -> {
                    editedKeyStore.set(newKeyStore);
                    latch.countDown();
                });
            }

            if (dialog == null) { // throw new RuntimeException("Parent is not a JFrame or Dialog");
                dialog = new EditKeystore(new KeyStore(original), (KeyStore newKeyStore) -> {
                    editedKeyStore.set(newKeyStore);
                    latch.countDown();
                });
            }

            dialog.setVisible(true);
            dialog.dispose();
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return editedKeyStore.get();
    }

    private void setupActions() {
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());
        chooseKeystoreButton.addActionListener(e -> chooseKeystorePath());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        jPanel.registerKeyboardAction(
            e -> onCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
    }

    private void initializeValues() {
        nameField.setText(keyStore.getName());
        pathField.setText(keyStore.getPath());
        passwordField.setText(keyStore.getPassword());
    }

    private KeyStore buildKeyStoreFromFields() {
        return new KeyStore(
            keyStore.isSelected(),
            nameField.getText(),
            pathField.getText(),
            new String(passwordField.getPassword())
        );
    }

    private void onSave() {
        // add your code here
        callback.callback(buildKeyStoreFromFields());
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        callback.callback(keyStore);
        dispose();
    }

    private void chooseKeystorePath() {

        try {

            jPanel.setEnabled(false);

            File selectedFile = new WindowsFileChooser()
                .withFilter("All Files", "*")
                .withTitle("Choose Keystore")
                .withInitialFile(pathField.getText())
                .withInitialDirectory(new File(pathField.getText()).getParent())
                .withParent(this)
                .existingFilesOnly()
                .prompt();

            jPanel.setEnabled(true);

            if (selectedFile != null) {
                pathField.setText(selectedFile.getAbsolutePath());
            }
        } catch (NoSuchFileException e) {
            e.printStackTrace();
        }
    }

    private KeyStore defaultKeyStore() {
        return new KeyStore(
            true,
            "JDK 1.5",
            "C:/Users/pc/Desktop/cacerts",
            "changeit"
        );
    }

    public static SaveKeyStoreCallback noOpSaveKeyStoreCallback() {
        return (KeyStore keystore) -> {
            System.out.printf("Saving keystore: %s%n", keystore);
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

    @FunctionalInterface
    public interface SaveKeyStoreCallback {
        void callback(KeyStore newKeyStore);
    }

    @FunctionalInterface
    public interface ParentProvider {
        Object provideParent();
    }
}
