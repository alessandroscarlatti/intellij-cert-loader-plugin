package com.scarlatti.certloader.ui;

import com.scarlatti.certloader.ui.controls.ConfirmationService;
import org.junit.Test;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class ConfirmationServiceTest {

    @Test
    public void displayDialog() {
        TestUtils.setDarculaLaf();
        boolean result = ConfirmationService.getYesOrNo(
            ConfirmationService.defaultDialog()
                .title("Confirm Delete")
                .message("Are you sure you want to delete this keystore?")
                .yesOption("Yes, Delete")
                .noOption("No, Don't Delete").build()
        );

        System.out.printf("result was %s%n", result);
    }
}
