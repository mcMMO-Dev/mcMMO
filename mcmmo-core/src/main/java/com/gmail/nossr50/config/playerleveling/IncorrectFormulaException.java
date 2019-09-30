package com.gmail.nossr50.config.playerleveling;

import com.gmail.nossr50.datatypes.experience.FormulaType;

public class IncorrectFormulaException extends RuntimeException {

    public IncorrectFormulaException(FormulaType formulaType) {
        super("Formula not recognized: " + formulaType.toString());
    }

}
