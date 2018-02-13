package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.Cert;
import com.scarlatti.certloader.ui.model.KeyStore;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

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

    private DefaultTableModel model;
    private List<KeyStore> keyStores = new ArrayList<>();

    public ListKeyStores(List<KeyStore> keyStores) {
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
                if (column == 1) {
                    return false;
                }

                return super.isCellEditable(row, column);
            }

            public DefaultTableModel init() {
                addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent e) {
//                        evaluateInstallEnabled();
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

    private void setupButtons() {
        selectNoneButton.addActionListener(this::selectNone);
        selectAllButton.addActionListener(this::selectAll);
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

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }
}
