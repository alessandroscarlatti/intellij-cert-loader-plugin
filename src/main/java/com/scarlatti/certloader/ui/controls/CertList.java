package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.ui.UIComponent;
import com.scarlatti.certloader.ui.model.Cert;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
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
 * Saturday, 2/10/2018
 */
public class CertList implements UIComponent {
    private JTable table;
    private JPanel jPanel;
    private JLabel title;
    private JButton selectNoneButton;
    private JButton selectAllButton;
    private JButton installButton;
    private DefaultTableModel model;

    private List<Cert> certs = new ArrayList<>();
    private InstallCallback installCallback = noOpInstallCallback();
    private boolean enabled = true;
    private boolean installEnabled = false;

    public CertList() {
        setupTable();
        setupTitle("<url>");
        setupButtons();
    }

    public CertList(String url, List<Cert> certs) {
        setupTable();
        setupTitle(url);
        addCerts(certs);
        setupButtons();
    }

    public CertList(String url, List<Cert> certs, InstallCallback installCallback) {
        setupTable();
        setupTitle(url);
        addCerts(certs);
        setupButtons();
        installCallback = this.installCallback;
    }

    private void setupTitle(String url) {
        title.setText("SSL Certificates | " + url);
    }

    private void setupTable() {

        model = new DefaultTableModel(0, 3) {
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
                        evaluateInstallEnabled();
                    }
                });
                return this;
            }

        }.init();  // not sure why the default constructor causes an intellij exception?!?
        table.setModel(model);

        TableColumnModel columnModel = new DefaultTableColumnModel();
        columnModel.addColumn(new TableColumn(0));
        columnModel.addColumn(new TableColumn(1));
        columnModel.getColumn(0).setHeaderValue("Install");
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setHeaderValue("Name");

        table.setColumnModel(columnModel);
        table.putClientProperty("terminateEditOnFocusLost", true);


    }

    private void setupButtons() {
        selectNoneButton.addActionListener(this::selectNone);
        selectAllButton.addActionListener(this::selectAll);
        installButton.addActionListener(this::installCerts);
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

    private void installCerts(ActionEvent e) {

        List<Cert> certsToInstall = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            int actualIndex = table.convertRowIndexToView(i);
            boolean selected = (Boolean) model.getValueAt(actualIndex, 0);

            if (selected) {
                certsToInstall.add(certs.get(i));
            }
        }

        installCallback.installCerts(certsToInstall);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    private void enable() {
        table.setEnabled(true);
        selectNoneButton.setEnabled(true);
        selectAllButton.setEnabled(true);
        evaluateInstallEnabled();
    }

    private void disable() {
        table.setEnabled(false);
        selectNoneButton.setEnabled(false);
        selectAllButton.setEnabled(false);
        setInstallEnabled(false);
    }

    public void setInstallEnabled(boolean enabled) {
        installButton.setEnabled(enabled);
        this.enabled = enabled;
    }

    public void clear() {

    }

    public void addCerts(List<Cert> certs) {
        for (Cert cert : certs) {
            addCert(cert);
        }
    }

    public void addCert(Cert cert) {
        certs.add(cert);
        model.addRow(new Object[]{
            cert.isSelected(),
            cert.getCompanyName(),
            cert.getLocationName()
        });

        setInstallEnabled(true);
    }

    private void evaluateInstallEnabled() {
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, 0)) {
                setInstallEnabled(true);
                return;
            }
        }

        setInstallEnabled(false);
    }

    @Override
    public JPanel getJPanel() {
        return jPanel;
    }

    public JTable getTable() {
        return table;
    }

    @FunctionalInterface
    public interface InstallCallback {
        void installCerts(List<Cert> certs);
    }

    public static InstallCallback noOpInstallCallback() {
        return (List<Cert> certs) -> {
            System.out.println(certs);
        };
    }
}
