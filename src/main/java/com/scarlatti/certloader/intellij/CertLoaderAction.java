package com.scarlatti.certloader.intellij;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.DialogBuilder;
import com.scarlatti.certloader.config.IntelliJConfig;
import com.scarlatti.certloader.config.LocalConfig;
import com.scarlatti.certloader.plugin.AppState;
import com.scarlatti.certloader.plugin.IntelliJAppRunner;
import com.scarlatti.certloader.plugin.LocalAppRunner;
import com.scarlatti.certloader.services.InstallCertService;
import com.scarlatti.certloader.services.LoadCertService;
import com.scarlatti.certloader.intellij.PluginStateWrapper;
import com.scarlatti.certloader.services.IntelliJRepository;
import com.scarlatti.certloader.services.Repository;
import com.scarlatti.certloader.ui.controls.CertLoaderDialog;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Wednesday, 11/8/2017
 */
public class CertLoaderAction extends AnAction {

    private final String NUMBER_PROPERTY_NAME = "com.scarlatti.certLoader.number";

    public CertLoaderAction() {
        super("Install SSL Certificate...");
    }

    /**
     * A very simple experiment to try to show a message pane
     * when the action is performed.
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
//        performActionWithPersistentStateImplementation();
        showDialog();
    }

    private void showDialog() {
        Injector injector = Guice.createInjector(new IntelliJConfig());
        injector.getInstance(IntelliJAppRunner.class).run();
    }

    private void performActionWithPersistentStateImplementation() {
        PluginStateWrapper pluginStateWrapper = ServiceManager.getService(PluginStateWrapper.class);

        String mostRecentUrl = pluginStateWrapper.getState().getMostRecentUrl();

        String newUrl = javax.swing.JOptionPane.showInputDialog("Provide URL: ", mostRecentUrl);

        pluginStateWrapper.getState().setMostRecentUrl(newUrl);
    }

    private void performActionWithProperties() {
        // try to get the stored value if it exists
        Integer val = PropertiesComponent.getInstance().getInt(NUMBER_PROPERTY_NAME, 0);

        javax.swing.JOptionPane.showMessageDialog(null, val);
        PropertiesComponent.getInstance().setValue(NUMBER_PROPERTY_NAME, val + 1, -1);
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }
}
