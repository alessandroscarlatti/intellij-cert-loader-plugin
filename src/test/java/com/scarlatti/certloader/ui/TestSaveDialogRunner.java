package com.scarlatti.certloader.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scarlatti.certloader.services.LocalRepository;
import com.scarlatti.certloader.services.Repository;
import com.scarlatti.certloader.ui.controls.TestSaveDialog;
import com.scarlatti.certloader.ui.model.KeyStore;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 2/14/2018
 */
public class TestSaveDialogRunner {
    @SuppressWarnings("unchecked")
    @Test
    public void runTestSaveDialog() {

        List<KeyStore> keyStores = new ArrayList<>();

        Repository<List<KeyStore>> repo = new LocalRepository<>(
            "C:/Users/pc/Desktop/cert-loader-test/settings.json",
            new ObjectMapper(),
            new TypeReference<List<KeyStore>>() {},
            Collections.emptyList()
        );

        TestUtils.setDarculaLaf();

        TestSaveDialog dialog = new TestSaveDialog(repo);
        dialog.pack();
        dialog.setVisible(true);
    }
}
