package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.KeyStore;

import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.*;
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
        setContentPane(jPanel);
        setModal(true);
        getRootPane().setDefaultButton(saveButton);

        setupActions();

        keyStore = defaultKeyStore();
        initializeValues();
    }

    public EditKeystore(KeyStore keyStore) {
        this();
        this.keyStore = keyStore;
        initializeValues();
    }

    public EditKeystore(KeyStore keyStore, SaveKeyStoreCallback callback) {
        this();
        this.keyStore = keyStore;
        this.callback = callback;
        initializeValues();
    }

    /**
     * Return a new keystore edited from the given keystore.
     *
     * @param original the original keystore
     * @return the edited keystore, may be the same data as the original.
     * always a different instance.
     */
    public static KeyStore editKeyStore(KeyStore original) {

        AtomicReference<KeyStore> editedKeyStore = new AtomicReference<>(original);
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            new EditKeystore(new KeyStore(original), (KeyStore newKeyStore) -> {
                editedKeyStore.set(newKeyStore);
                latch.countDown();
            });
        }).start();

        return editedKeyStore.get();
    }

    private void setupActions() {
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());

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

    @FunctionalInterface
    public interface SaveKeyStoreCallback {
        void callback(KeyStore newKeyStore);
    }
}
