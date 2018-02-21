package com.scarlatti.certloader.ui.controls;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
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
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Cursor.HAND_CURSOR;

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
    private JLabel urlLink;
    private DefaultTableModel model;

    private String url;
    private List<Cert> certs = new ArrayList<>();
    private InstallCallback installCallback = noOpInstallCallback();
    private boolean enabled = true;
    private boolean installEnabled = false;

    public CertList() {
        setupTable();
        setupTitle("www.google.com");
        setupButtons();
    }

    public CertList(String url, List<Cert> certs) {
        setupTable();
        setupTitle(url);
        addCerts(certs);
        setupButtons();
    }

    public void init(String url, List<Cert> certs, InstallCallback installCallback) {
        setupTable();
        setupTitle(url);
        addCerts(certs);
        this.installCallback = installCallback;
    }

    private void setupTitle(String url) {
        this.url = url;
        urlLink.setText("<html><u>" + url + "</u></html>");
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
        columnModel.getColumn(1).setHeaderValue("Certificate");

        table.setColumnModel(new DefaultTableColumnModel());
        table.setColumnModel(columnModel);
        table.putClientProperty("terminateEditOnFocusLost", true);
    }

    private void setupButtons() {
        selectNoneButton.addActionListener(this::selectNone);
        selectAllButton.addActionListener(this::selectAll);
        installButton.addActionListener(this::installCerts);

        urlLink.setCursor(new Cursor(HAND_CURSOR));
        urlLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        });
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

    public void addCerts(List<Cert> certs) {
        this.certs.clear();
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
        jPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        jPanel.setMinimumSize(new Dimension(397, 50));
        jPanel.setPreferredSize(new Dimension(200, 100));
        final JScrollPane scrollPane1 = new JScrollPane();
        jPanel.add(scrollPane1, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table = new JTable();
        scrollPane1.setViewportView(table);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        jPanel.add(panel1, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectNoneButton = new JButton();
        selectNoneButton.setText("Select None");
        panel1.add(selectNoneButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectAllButton = new JButton();
        selectAllButton.setText("Select All");
        panel1.add(selectAllButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        installButton = new JButton();
        installButton.setDefaultCapable(true);
        installButton.setText("Install Selected Certificate(s)");
        panel1.add(installButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        jPanel.add(panel2, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        title = new JLabel();
        title.setRequestFocusEnabled(false);
        title.setText("SSL Certificates | ");
        panel2.add(title, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(88, 16), null, 0, false));
        urlLink = new JLabel();
        urlLink.setForeground(new Color(-14114049));
        urlLink.setText("what.com");
        panel2.add(urlLink, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        jPanel.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jPanel;
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
