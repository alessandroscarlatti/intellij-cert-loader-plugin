package com.scarlatti.certloader.ui;

import com.scarlatti.certloader.ui.controls.CertList;
import com.scarlatti.certloader.ui.controls.CertLoaderDialog;
import com.scarlatti.certloader.ui.controls.CertLoaderDialogOld;
import com.scarlatti.certloader.ui.controls.URLToolbar;
import com.scarlatti.certloader.ui.model.Cert;
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
public class CertLoaderDialogTest {

    @Test
    public void displayDialog() {
        TestUtils.DisplayJPanel(() -> {

            List<Cert> certs = Arrays.asList(
                CertListTest.Data.Certs.sample1(),
                CertListTest.Data.Certs.sample2(),
                CertListTest.Data.Certs.sample3(),
                CertListTest.Data.Certs.sample4()
            );

            CertLoaderDialog certLoaderDialog = new CertLoaderDialog();
            certLoaderDialog.getUrlToolbar().setLoadAction(new URLToolbar.LoadAction() {
                @Override
                public void load(String url, ActionCompletedCallback callback) {
                    // do loading here...

                    new Thread(() -> {
                        certLoaderDialog.getCertListWrapper().loading();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        certLoaderDialog.getCertListWrapper().listCerts(url, certs, CertList.noOpInstallCallback());
                    }).start();

                }

                @Override
                public void cancel(ActionCompletedCallback callback) {
                    // cancel loading here...
                }
            });

            return certLoaderDialog.getJPanel();
        });
    }
}
