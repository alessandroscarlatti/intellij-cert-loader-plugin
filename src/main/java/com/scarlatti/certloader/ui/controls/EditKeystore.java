package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.KeyStore;

import javax.swing.*;
import java.awt.*;
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
    public static KeyStore editKeyStore(KeyStore original, JFrame parentFrame) {

        AtomicReference<KeyStore> editedKeyStore = new AtomicReference<>(original);
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            EditKeystore dialog = new EditKeystore(parentFrame, new KeyStore(original), (KeyStore newKeyStore) -> {
                editedKeyStore.set(newKeyStore);
                latch.countDown();
            });

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
