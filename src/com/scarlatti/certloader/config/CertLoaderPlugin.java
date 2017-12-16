package com.scarlatti.certloader.config;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class CertLoaderPlugin implements ApplicationComponent {

    /**
     * Get the action manager, and register the action in it.
     */
    @Override
    public void initComponent() {
        System.out.println("initComponent called");
        ActionManager am = ActionManager.getInstance();
        CertLoaderAction certLoaderAction = new CertLoaderAction();

        // Passes an instance of your custom action class to the registerAction method of the ActionManager class.
        am.registerAction("MyPluginAction", certLoaderAction);

        // Gets an instance of the WindowMenu action group.
        // "WindowMenu" is exact!
        DefaultActionGroup windowM = (DefaultActionGroup) am.getAction("ViewMenu");

        // Adds a separator and a new menu command to the WindowMenu group on the main menu.
        windowM.addSeparator();
        windowM.add(certLoaderAction);
    }

    @NotNull
    @Override
    public String getComponentName() {
        System.out.println("getting component name");
        return "XSDSyntaxHighlighter";
    }

    /**
     * don't need to fill this one in
     */
    @Override
    public void disposeComponent() {
    }
}