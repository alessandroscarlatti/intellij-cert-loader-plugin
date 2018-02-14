package com.scarlatti.certloader.ui.controls;

import javax.swing.*;
import java.awt.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Tuesday, 2/13/2018
 */
public class ConfirmationService {

    public static ConfirmationDialog.Builder defaultDialog() {
        return ConfirmationDialog.newConfirmationDialog()
            .parent(null)
            .title("Confirm...")
            .yesOption("Yes")
            .noOption("No")
            .preference(true);
    }

    public static boolean getYesOrNo(ConfirmationDialog dialog) {
        return getYesOrNo(
            dialog.getParent(),
            dialog.getTitle(),
            dialog.getMessage(),
            dialog.getYesOption(),
            dialog.getNoOption(),
            dialog.getPreference()
        );
    }

    public static boolean getYesOrNo(
        Component parent,
        final String title,
        final String message,
        final String yesOption,
        final String noOption,
        final Boolean preference) {
        String[] options = new String[] {yesOption, noOption};
        String preferredOption = null;

        if (preference != null) {
            if (preference) {
                preferredOption = yesOption;
            } else {
                preferredOption = noOption;
            }
        }

        int chosen = JOptionPane.showOptionDialog(
            parent,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            preferredOption
        );

        return (chosen == 0);
    }

    public static class ConfirmationDialog {
        private Component parent;
        private String title;
        private String message;
        private String yesOption;
        private String noOption;
        private Boolean preference;

        private ConfirmationDialog(Builder builder) {
            this.parent = builder.parent;
            this.title = builder.title;
            this.message = builder.message;
            this.yesOption = builder.yesOption;
            this.noOption = builder.noOption;
            this.preference = builder.preference;
        }

        public static Builder newConfirmationDialog() {
            return new Builder();
        }

        public Component getParent() {
            return parent;
        }

        public void setParent(Component parent) {
            this.parent = parent;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getYesOption() {
            return yesOption;
        }

        public void setYesOption(String yesOption) {
            this.yesOption = yesOption;
        }

        public String getNoOption() {
            return noOption;
        }

        public void setNoOption(String noOption) {
            this.noOption = noOption;
        }

        public Boolean getPreference() {
            return preference;
        }

        public void setPreference(Boolean preference) {
            this.preference = preference;
        }


        public static final class Builder {
            private Component parent;
            private String title;
            private String message;
            private String yesOption;
            private String noOption;
            private Boolean preference;

            private Builder() {
            }

            public ConfirmationDialog build() {
                return new ConfirmationDialog(this);
            }

            public Builder parent(Component parent) {
                this.parent = parent;
                return this;
            }

            public Builder title(String title) {
                this.title = title;
                return this;
            }

            public Builder message(String message) {
                this.message = message;
                return this;
            }

            public Builder yesOption(String yesOption) {
                this.yesOption = yesOption;
                return this;
            }

            public Builder noOption(String noOption) {
                this.noOption = noOption;
                return this;
            }

            public Builder preference(Boolean preference) {
                this.preference = preference;
                return this;
            }
        }
    }


}
