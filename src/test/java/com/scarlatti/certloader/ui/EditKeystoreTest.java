package com.scarlatti.certloader.ui;

import com.scarlatti.certloader.ui.controls.EditKeystore;
import com.scarlatti.certloader.ui.controls.Testasdf;
import org.junit.Test;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class EditKeystoreTest {

    @Test
    public void displayDialogDefault() {
        TestUtils.DisplayJPanel(() ->
            new EditKeystore().getJPanel()
        );
    }

    @Test
    public void displayDialogWithDataPassedIn() {
        TestUtils.DisplayJPanel(() ->
            new EditKeystore(ListKeyStoresTest.Data.KeyStores.sample2()).getJPanel()
        );
    }

    @Test
    public void displayDialogAndEditDataPassedIn() {
        TestUtils.DisplayJPanel(() ->
            new EditKeystore(ListKeyStoresTest.Data.KeyStores.sample2()).getJPanel()
        );
    }

    @Test
    public void displayDialogAndEditDataPassedInWithClosing() {
        TestUtils.setDarculaLaf();
        EditKeystore dialog = new EditKeystore();
        dialog.pack();
        dialog.setVisible(true);
    }



}
