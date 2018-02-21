package com.scarlatti.certloader.ui.controls;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.KeyStore;
import com.scarlatti.certloader.utils.WindowsFileChooser;

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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        jPanel = new JPanel();
        jPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        jPanel.setMaximumSize(new Dimension(1000, 164));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        jPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        panel2.add(saveButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        panel2.add(cancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        jPanel.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Name:");
        CellConstraints cc = new CellConstraints();
        panel3.add(label1, cc.xy(1, 1));
        nameField = new JTextField();
        nameField.setMinimumSize(new Dimension(200, 24));
        panel3.add(nameField, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("Path:");
        panel3.add(label2, cc.xy(1, 3));
        pathField = new JTextField();
        pathField.setMinimumSize(new Dimension(200, 24));
        panel3.add(pathField, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        label3.setText("Password:");
        panel3.add(label3, cc.xy(1, 5));
        chooseKeystoreButton = new JButton();
        chooseKeystoreButton.setText("Choose Keystore...");
        panel3.add(chooseKeystoreButton, cc.xy(5, 3));
        passwordField = new JPasswordField();
        passwordField.setMinimumSize(new Dimension(200, 25));
        panel3.add(passwordField, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jPanel;
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
