package com.scarlatti.certloader.ui;

import com.scarlatti.certloader.ui.controls.CertList;
import com.scarlatti.certloader.ui.controls.CertListWrapper;
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
public class CertListWrapperTest {

    @Test
    public void displayLoading() {
        TestUtils.displayJPanel(() -> {
            CertListWrapper wrapper = new CertListWrapper();

            wrapper.loading(() -> {
                System.out.println("Timeout!");
            });

            return wrapper.getJPanel();
        });
    }

    @Test
    public void displayCertList() {
        TestUtils.displayJPanel(() -> {
            CertListWrapper wrapper = new CertListWrapper();

            List<Cert> certs = Arrays.asList(
                CertListTest.Data.Certs.sample1(),
                CertListTest.Data.Certs.sample2(),
                CertListTest.Data.Certs.sample3(),
                CertListTest.Data.Certs.sample4()
            );

            wrapper.listCerts("what.com", certs, CertList.noOpInstallCallback());

            return wrapper.getJPanel();
        });
    }
}
