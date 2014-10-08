package me.zonyitoo.game24;

import java.util.ArrayList;

/**
 * Created by zonyitoo on 14/10/7.
 */
public class EquationValidator {

    public static boolean validate(ArrayList<Equation.EquationNode> equation) {
        return false;
    }

    public class ValidationErrorException extends Exception {

        private int index;

        public ValidationErrorException(int index, String message) {
            super(message);
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }


}
