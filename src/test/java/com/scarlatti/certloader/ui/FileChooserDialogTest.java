package com.scarlatti.certloader.ui;

import com.scarlatti.certloader.utils.WindowsFileChooser;
import org.junit.Test;

import java.io.File;
import java.nio.file.NoSuchFileException;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/17/2018
 */
public class FileChooserDialogTest {

    @Test
    public void testFileChooserReturnValue() {
        try {
            File selectedFile = new WindowsFileChooser()
                .withFilter("All Files", "*")
                .withTitle("Choose Keystore")
                .withInitialFile("C:/Users/pc/Desktop/ReactTest1/package.json")
                .withInitialDirectory("C:/Users/pc/Desktop/ReactTest1")
                .prompt();

            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        } catch (NoSuchFileException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testFileChooserReturnValueWithAllowOnlyExistingFiles() {
        try {
            File selectedFile = new WindowsFileChooser()
                .withFilter("All Files", "*")
                .withTitle("Choose Keystore")
                .withInitialFile("C:/Users/pc/Desktop/ReactTest1/package.json")
                .withInitialDirectory("C:/Users/pc/Desktop/ReactTest1")
                .existingFilesOnly()
                .prompt();

            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        } catch (NoSuchFileException e) {
            e.printStackTrace();
        }
    }
}
