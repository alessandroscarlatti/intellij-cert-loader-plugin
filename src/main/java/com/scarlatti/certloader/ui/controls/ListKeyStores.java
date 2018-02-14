package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.KeyStore;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 2/12/2018
 */
public class ListKeyStores implements UIComponent {
    private JPanel jPanel;
    private JTable table;
    private JButton selectNoneButton;
    private JButton selectAllButton;
    private JButton restoreDefaultsButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton editButton;
    private JButton testInstallButton;
    private JPanel editToolbar;

    private DefaultTableModel model;
    private List<KeyStore> keyStores = new ArrayList<>();

    private List<String> envs = new ArrayList<>();

    public ListKeyStores(List<KeyStore> keyStores) {
        setupTable();
        addKeyStores(keyStores);
        setupButtons();
    }

    public ListKeyStores(List<KeyStore> keyStores, List<String> envs) {
        this.envs = envs;
        setupTable();
        addKeyStores(keyStores);
        setupButtons();
    }

    private void setupTable() {

        model = new DefaultTableModel(0, 4) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            public DefaultTableModel init() {
                addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent e) {
                        if (e.getColumn() == 0 && e.getFirstRow() == e.getLastRow()) {
                            int actualIndex = table.convertRowIndexToModel(e.getFirstRow());
                            keyStores.get(actualIndex).setSelected(
                                (Boolean) model.getValueAt(actualIndex, e.getColumn())
                            );

                        }
                    }
                });
                return this;
            }

        }.init();  // not sure why the default constructor causes an intellij exception?!?
        table.setModel(model);

        TableColumnModel columnModel = new DefaultTableColumnModel();
        columnModel.addColumn(new TableColumn(0));
        columnModel.addColumn(new TableColumn(1));
        columnModel.addColumn(new TableColumn(2));
        columnModel.getColumn(0).setHeaderValue("Use");
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setHeaderValue("Key Store");
        columnModel.getColumn(2).setHeaderValue("Path");

        table.setColumnModel(columnModel);
        table.putClientProperty("terminateEditOnFocusLost", true);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }


    public void normalMode() {
        for (Component component : jPanel.getComponents()) {
            component.setEnabled(false);
        }

        for (Component component : editToolbar.getComponents()) {
            component.setEnabled(true);
        }

        table.setEnabled(true);
    }

    public void disabledMode() {
        for (Component component : jPanel.getComponents()) {
            component.setEnabled(false);
        }

        for (Component component : editToolbar.getComponents()) {
            component.setEnabled(false);
        }

        table.setEnabled(false);
    }

    private void selectNone(ActionEvent e) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(false, i, 0);
        }
    }

    private void selectAll(ActionEvent e) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(true, i, 0);
        }
    }

    private void addNewKeyStore(ActionEvent e) {
        // would call new EditKeystore right now...
        KeyStore newKeyStore = getNewKeyStore();

        // now display the new keystore
        addKeyStore(newKeyStore);
    }

    private KeyStore getNewKeyStore() {
        return new KeyStore(
            true,
            "JDK 8.1",
            "C:/Users/pc/Desktop/cacerts",
            "changeit"
        );
    }

    private void editSelectedKeyStore(ActionEvent e) {
        new Thread(() -> {
            // need to test if selection is a single row...
            List<TableModelKeyStoreWrapper> selectedKeyStores = getSelectedKeyStores();

            if (selectedKeyStores.size() == 1) {
                KeyStore editedKeyStore = getEditedKeyStore(selectedKeyStores.get(0).getKeyStore(), (JFrame)this.jPanel.getRootPane().getParent());
                updateKeyStore(selectedKeyStores.get(0).getModelIndex(), editedKeyStore);
            }
        }).start();
    }

    private void updateKeyStore(int selectionIndex, KeyStore keyStore) {
        int actualIndex = table.convertRowIndexToModel(selectionIndex);
        replaceKeyStore(keyStore, actualIndex);
    }

    private void restoreDefaults(ActionEvent e) {
        try {
            List<KeyStore> defaultKeyStores = getDefaultKeyStores();
            removeAllKeyStores();
            addKeyStores(defaultKeyStores);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void setupButtons() {
        selectNoneButton.addActionListener(this::selectNone);
        selectAllButton.addActionListener(this::selectAll);
        addButton.addActionListener(this::addNewKeyStore);
        removeButton.addActionListener(this::removeSelectedKeyStores);
        editButton.addActionListener(this::editSelectedKeyStore);
        restoreDefaultsButton.addActionListener(this::restoreDefaults);

        if (!envs.contains("test")) {
            testInstallButton.getParent().remove(testInstallButton);
        } else {
            testInstallButton.addActionListener(this::testAskForKeyStores);
        }

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
//                JTable table =(JTable) mouseEvent.getSource();
//                Point point = mouseEvent.getPoint();
//                int viewRowIndex = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2) {
                    // your valueChanged overridden method
                    editSelectedKeyStore(null);
                }
            }
        });
    }

    public void addKeyStores(List<KeyStore> keyStores) {
        for (KeyStore keyStore : keyStores) {
            addKeyStore(keyStore);
        }
    }

    public void addKeyStore(KeyStore keyStore) {
        keyStores.add(keyStore);
        model.addRow(new Object[]{
            keyStore.isSelected(),
            keyStore.getName(),
            keyStore.getPath()
        });
    }

    public void replaceKeyStore(KeyStore keyStore, int modelIndex) {
        keyStores.remove(modelIndex);
        keyStores.add(modelIndex, keyStore);
        model.setValueAt(keyStore.isSelected(), modelIndex, 0);
        model.setValueAt(keyStore.getName(), modelIndex, 1);
        model.setValueAt(keyStore.getPath(), modelIndex, 2);
    }

    private void removeAllKeyStores() {
        for (int i = model.getRowCount() - 1; i > -1; i--) {
            int actualIndex = table.convertRowIndexToView(i);
            model.removeRow(actualIndex);
            keyStores.remove(actualIndex);
        }
    }

    private void removeSelectedKeyStores(ActionEvent e) {
        if (!table.getSelectionModel().isSelectionEmpty()) {

            // confirm intention!
            boolean confirmed = ConfirmationService.getYesOrNo(
                ConfirmationService.defaultDialog()
                    .parent(jPanel)
                    .title("Confirm Delete Keystore(s)...")
                    .message("Are you sure you want to remove the selected keystore(s)?")
                    .yesOption("Yes, Delete")
                    .noOption("No, Don't Delete").build()
            );

            if (confirmed) {
                doRemoveSelectedKeyStores();
            }

        }
    }

    private void doRemoveSelectedKeyStores() {
        ListSelectionModel selectionModel = table.getSelectionModel();

        for (int i = model.getRowCount() - 1; i > -1; i--) {
            int actualIndex = table.convertRowIndexToView(i);

            boolean selected = (actualIndex >= selectionModel.getMinSelectionIndex() && actualIndex <= selectionModel.getMaxSelectionIndex());
            if (selected) {
                model.removeRow(actualIndex);
                keyStores.remove(actualIndex);
            }
        }
    }

    private void testAskForKeyStores(ActionEvent e) {
        System.out.println(getCheckedKeyStores());
    }

    public List<TableModelKeyStoreWrapper> getSelectedKeyStores() {
        List<TableModelKeyStoreWrapper> selectedKeyStores = new ArrayList<>();

        ListSelectionModel selectionModel = table.getSelectionModel();

        if (!selectionModel.isSelectionEmpty()) {
            for (int i = 0; i < model.getRowCount(); i++) {
                int actualIndex = table.convertRowIndexToView(i);
                boolean selected = (actualIndex >= selectionModel.getMinSelectionIndex() && actualIndex <= selectionModel.getMaxSelectionIndex());
                if (selected) {
                    selectedKeyStores.add(
                        new TableModelKeyStoreWrapper(keyStores.get(i), i));
                }
            }
        }

        return selectedKeyStores;
    }

    public List<KeyStore> getCheckedKeyStores() {
        List<KeyStore> keyStoresToUse = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            int actualIndex = table.convertRowIndexToView(i);
            boolean selected = (Boolean) model.getValueAt(actualIndex, 0);

            if (selected) {
                keyStoresToUse.add(keyStores.get(i));
            }
        }

        return keyStoresToUse;
    }

    public KeyStore getEditedKeyStore(KeyStore keyStore) {
        return EditKeystore.editKeyStore(keyStore, null);
    }

    public KeyStore getEditedKeyStore(KeyStore keyStore, JFrame parentFrame) {
        return EditKeystore.editKeyStore(keyStore, parentFrame);
    }

    // TODO later call the real service
    public List<KeyStore> getDefaultKeyStores() {
        return Data.keyStores();
    }

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }

    private static class TableModelKeyStoreWrapper {

        private KeyStore keyStore;
        private int modelIndex;

        public TableModelKeyStoreWrapper(KeyStore keyStore, int modelIndex) {
            this.keyStore = keyStore;
            this.modelIndex = modelIndex;
        }

        public KeyStore getKeyStore() {
            return keyStore;
        }

        public void setKeyStore(KeyStore keyStore) {
            this.keyStore = keyStore;
        }

        public int getModelIndex() {
            return modelIndex;
        }

        public void setModelIndex(int modelIndex) {
            this.modelIndex = modelIndex;
        }
    }

    private static class Data {

        public static List<KeyStore> keyStores() {
            return Arrays.asList(KeyStores.sample1(), KeyStores.sample2());
        }

        public static class KeyStores {

            public static KeyStore sample1() {
                return new KeyStore(
                    true,
                    "JDK 8.1",
                    "C:/Users/pc/Desktop/cacerts",
                    "changeit"
                );
            }

            public static KeyStore sample2() {
                return new KeyStore(
                    true,
                    "JDK 7.2",
                    "C:/Users/pc/Desktop/cacerts",
                    "changeit"
                );
            }
        }
    }
}
