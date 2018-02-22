package com.scarlatti.certloader.config;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.DialogBuilder;
import com.scarlatti.certloader.plugin.AppState;
import com.scarlatti.certloader.plugin.InstallAction;
import com.scarlatti.certloader.plugin.LoadAction;
import com.scarlatti.certloader.plugin.PluginStateWrapper;
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

        Repository<AppState> repository = new IntelliJRepository<>(ServiceManager.getService(PluginStateWrapper.class));

        CertLoaderDialog certLoaderDialog = new CertLoaderDialog(repository);

        certLoaderDialog.getUrlToolbar().setLoadAction(
            new LoadAction(certLoaderDialog.getCertListWrapper())
        );

        certLoaderDialog.getCertListWrapper().getCertList().setInstallCallback(certs -> {
            new InstallAction(certLoaderDialog.getJPanel()).install(certs, certLoaderDialog.getAppManager().getListKeyStores().getCheckedKeyStores());
        });


        DialogBuilder builder = new DialogBuilder();
        builder.setCenterPanel(certLoaderDialog.getJPanel());
        builder.setDimensionServiceKey("CertLoaderPluginDialog");
        builder.setTitle("Install SSL Certificate(s)");
        builder.removeAllActions();
//        builder.addOkAction();
//        builder.addCancelAction();

        builder.show();

        certLoaderDialog.saveOnClose();
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
