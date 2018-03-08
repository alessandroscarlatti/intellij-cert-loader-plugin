package com.scarlatti.certloader.plugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scarlatti.certloader.config.LocalConfig;
import org.junit.Test;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 3/7/2018
 */
public class LocalTest {

    @Test
    public void canStartGuiceContextAndRunner() {
        Injector injector = Guice.createInjector(new LocalConfig());
        injector.getInstance(LocalAppRunner.class).run();
    }
}
