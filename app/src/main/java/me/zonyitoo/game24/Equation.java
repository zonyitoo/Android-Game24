package me.zonyitoo.game24;

import java.util.Stack;

/**
 * Created by zonyitoo on 14/10/7.
 */
public class Equation {

    private StringBuilder equationBuffer = new StringBuilder();

    private Stack<Character> operatorStack = new Stack<Character>();
    private Stack<Integer> valueStack = new Stack<Integer>();

    private int bracketLevel = 0;
    private int maxBracketLevel = 3;

    Equation() {

    }

    Equation(int maxBracketLevel) {
        this.maxBracketLevel = maxBracketLevel;
    }

    public class MalformedEquationException extends Exception {

    }

    public enum EquationOperatorType {
        EQUATION_OPERATOR_TYPE_PLUS,
        EQUATION_OPERATOR_TYPE_MINUS,
        EQUATION_OPERATOR_TYPE_MULTIPLY,
        EQUATION_OPERATOR_TYPE_DIVIDE,
        EQUATION_OPERATOR_TYPE_LEFT_BRACKET,
        EQUATION_OPERATOR_TYPE_RIGHT_BRACKET
    }

    public abstract class EquationNode {
    }

    public class EquationOperator extends EquationNode {

        private EquationOperatorType type = EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS;

        public EquationOperator(EquationOperatorType t) {
            this.type = t;
        }

        public EquationOperatorType getType() {
            return type;
        }
    }

    public class EquationIntegerOperand extends EquationNode {

        private Integer data = 0;

        public EquationIntegerOperand(Integer i) {
            data = i;
        }

        public Integer getData() {
            return data;
        }
    }
}
