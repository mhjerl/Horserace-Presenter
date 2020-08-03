package com.othernewspaper.horseracepresenter.app;

public class UIValidation {

    public static String questionValidate(String question, boolean isBlankCheck) {
        String msg = "success";
        if (isBlankCheck) {
            if (question == null || question.isEmpty()) {
                msg = "Question cannot be blank.";
                return msg;
            }
        }
        if (question.length() > 120) {
            msg = "Question cannot be more than 120 chars.";
            return msg;
        }
        return msg;
    }

    public static String optionValidate(String option, boolean isBlankCheck) {
        String msg = "success";
        if (isBlankCheck) {
            if (option == null || option.isEmpty()) {
                msg = "This Option cannot be blank.";
                return msg;
            }
        }
        if (option.length() > 75) {
            msg = "Options cannot be more than 75 chars.";
            return msg;
        }
        return msg;

    }
}
