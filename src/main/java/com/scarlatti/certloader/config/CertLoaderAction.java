package com.scarlatti.certloader.config;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * ~     _____                                    __
 * ~    (, /  |  /)                /)         (__/  )      /)        ,
 * ~      /---| // _ _  _  _ __  _(/ __ ___     / _ _  __ // _ _/_/_
 * ~   ) /    |(/_(//_)/_)(_(/ ((_(_/ ((_)   ) / (_(_(/ ((/_(_((_(__(_
 * ~  (_/                                   (_/
 * ~  Wednesday, 11/8/2017
 */
public class CertLoaderAction extends AnAction {

    public CertLoaderAction() {
        super("XSD Highlighting");
    }

    /**
     * A very simple experiment to try to show a message pane
     * when the action is performed.
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        javax.swing.JOptionPane.showMessageDialog(null, "What!");
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }
}
