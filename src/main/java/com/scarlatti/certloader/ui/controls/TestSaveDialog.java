package com.scarlatti.certloader.ui.controls;

import com.scarlatti.certloader.services.Repository;
import com.scarlatti.certloader.ui.model.KeyStore;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class TestSaveDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private ListKeyStores listKeyStores;

    private Repository<List<KeyStore>> keyStoreRepo;

    public TestSaveDialog(Repository<List<KeyStore>> keyStoreRepo) {

        this.keyStoreRepo = keyStoreRepo;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        listKeyStores.setCurrent(keyStoreRepo.retrieve());
    }

    private void onOK() {
        // add your code here
        // save...
        keyStoreRepo.save(listKeyStores.getCurrent());
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        // don't save...
        dispose();
    }

}
