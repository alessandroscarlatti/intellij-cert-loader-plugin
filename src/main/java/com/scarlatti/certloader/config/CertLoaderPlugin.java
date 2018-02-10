package com.scarlatti.certloader.config;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class CertLoaderPlugin implements ApplicationComponent {

    private final String TOOLS_MENU_NAME = "ToolsMenu";

    /**
     * Get the action manager, and register the action in it.
     */
    @Override
    public void initComponent() {
        System.out.println("initComponent called");
        ActionManager actionManager = ActionManager.getInstance();
        CertLoaderAction certLoaderAction = new CertLoaderAction();

        // Passes an instance of your custom action class to the registerAction method of the ActionManager class.
        actionManager.registerAction("CertLoaderAction", certLoaderAction);

        // Gets an instance of the WindowMenu action group.
        // "WindowMenu" is exact!
        DefaultActionGroup viewMenu = (DefaultActionGroup) actionManager.getAction(TOOLS_MENU_NAME);

        // Adds a separator and a new menu command to the WindowMenu group on the main menu.
        viewMenu.addSeparator();
        viewMenu.add(certLoaderAction);
    }

    @NotNull
    @Override
    public String getComponentName() {
        System.out.println("getting component name");
        return "CertLoader";
    }

    /**
     * don't need to fill this one in
     */
    @Override
    public void disposeComponent() {
    }
}