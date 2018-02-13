package com.scarlatti.certloader.ui;

import com.scarlatti.certloader.ui.controls.ListKeyStores;
import com.scarlatti.certloader.ui.model.KeyStore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class ListKeyStoresTest {

    @Test
    public void displayDialog() {
        TestUtils.DisplayJPanel(() ->
            new ListKeyStores(Data.keyStores()).getJPanel()
        );
    }

    public static class Data {

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
