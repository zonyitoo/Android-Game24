package me.zonyitoo.game24.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

import com.github.kiprobinson.util.BigFraction;

/**
 * Created by zonyitoo on 14/10/7.
 */
public class Equation {

    public static final String LOG_TAG = Equation.class.getSimpleName();

    private int maxBracketLevel = -1;
    private int curBracketLevel = 0;
    private ArrayList<EquationNode> currentInputBuffer = new ArrayList<Equation.EquationNode>();

    private enum State {
        STATE_INIT,
        STATE_OPERATOR,
        STATE_OPERAND,
        STATE_LEFT_BRACKET,
        STATE_RIGHT_BRACKET
    }

    private State parsingState = State.STATE_INIT;

    public Equation() {

    }

    /**
     * Construct a new Equation object.
     *
     * @param maxBracketLevel max bracket level. -1 indicates infinite level
     */
    public Equation(int maxBracketLevel) {
        this.maxBracketLevel = maxBracketLevel;
    }

    public void clear() {
        currentInputBuffer.clear();
        parsingState = State.STATE_INIT;
        curBracketLevel = 0;
    }

    /**
     * Generate a formula string to represent the equation.
     *
     * @return Formula string, such as (1+2)*3+4
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Equation.EquationNode n : currentInputBuffer) {
            b.append(n.toString());
        }
        return b.toString();
    }

    /**
     * Add a EquationNode into the Equation. <br/>
     *
     * It uses a simple state machine for ensuring the equation is in valid
     * form.
     *
     * @param node the node for appending into the equation
     * @throws MalformedEquationException if the node is not expected
     */
    public void add(EquationNode node) throws MalformedEquationException {
        if (node instanceof EquationOperator) {
            switch (((EquationOperator) node).getType()) {
                case EQUATION_OPERATOR_TYPE_LEFT_BRACKET:
                    if (parsingState == State.STATE_INIT
                            || parsingState == State.STATE_OPERATOR
                            || parsingState == State.STATE_LEFT_BRACKET) {

                        if (maxBracketLevel != -1 && curBracketLevel == maxBracketLevel) {
                            throw new MalformedEquationException("Exceeded max bracket level");
                        }

                        parsingState = State.STATE_LEFT_BRACKET;
                        curBracketLevel++;
                    } else {
                        throw new MalformedEquationException("Not expecting a left bracket");
                    }
                    break;
                case EQUATION_OPERATOR_TYPE_RIGHT_BRACKET:
                    if (parsingState == State.STATE_OPERAND
                            || parsingState == State.STATE_RIGHT_BRACKET) {
                        if (curBracketLevel == 0) {
                            throw new MalformedEquationException("Bracket mismatch");
                        }

                        parsingState = State.STATE_RIGHT_BRACKET;
                        curBracketLevel--;
                    } else {
                        throw new MalformedEquationException("Not expecting a right bracket");
                    }
                    break;
                default:
                    if (parsingState == State.STATE_RIGHT_BRACKET
                            || parsingState == State.STATE_OPERAND) {
                        parsingState = State.STATE_OPERATOR;
                    } else {
                        throw new MalformedEquationException("Not expecting an operator");
                    }
                    break;
            }
        } else if (node instanceof EquationOperand) {
            if (parsingState == State.STATE_INIT
                    || parsingState == State.STATE_LEFT_BRACKET
                    || parsingState == State.STATE_OPERATOR) {
                parsingState = State.STATE_OPERAND;
            } else {
                throw new MalformedEquationException("Not expecting an operand");
            }
        }

        currentInputBuffer.add(node);
    }

    /**
     * Pop the last node of the equation.
     *
     * @return the popped node
     */
    public EquationNode pop() {
        EquationNode n = currentInputBuffer.get(currentInputBuffer.size() - 1);
        currentInputBuffer.remove(currentInputBuffer.size() - 1);

        if (n instanceof EquationOperator) {
            switch (((EquationOperator) n).getType()) {
                case EQUATION_OPERATOR_TYPE_LEFT_BRACKET:
                    --curBracketLevel;
                    break;
                case EQUATION_OPERATOR_TYPE_RIGHT_BRACKET:
                    ++curBracketLevel;
                    break;
                default:
                	break;
            }
        }

        if (currentInputBuffer.isEmpty()) {
            parsingState = State.STATE_INIT;
        } else {
            EquationNode lastone = currentInputBuffer.get(currentInputBuffer.size() - 1);
            if (lastone instanceof EquationOperator) {
                switch (((EquationOperator) lastone).getType()) {
                    case EQUATION_OPERATOR_TYPE_LEFT_BRACKET:
                        parsingState = State.STATE_LEFT_BRACKET;
                        break;
                    case EQUATION_OPERATOR_TYPE_RIGHT_BRACKET:
                        parsingState = State.STATE_RIGHT_BRACKET;
                        break;
                    default:
                        parsingState = State.STATE_OPERATOR;
                        break;
                }
            } else if (lastone instanceof EquationOperand) {
                parsingState = State.STATE_OPERAND;
            }
        }
        return n;
    }

    public int size() {
        return currentInputBuffer.size();
    }

    public boolean isEmpty() {
        return currentInputBuffer.isEmpty();
    }

    /**
     * Evaluate the equation and get the result. <br/>
     *
     * This function current use the following steps to evaluate the equation: <br/>
     *
     *     1. Convert the equation from Infix notation to Reverse Polish notation <br/>
     *     2. Evaluate the result by using the Reverse Polish notation <br/>
     *
     * And it uses <code>BigFraction</code> for calculation, which means that it will not
     * lose precision during calculation.
     *
     * @return the result of the equation
     * @throws MalformedEquationException if equation is malformed
     */
    public BigFraction evaluate() throws MalformedEquationException {
        if (parsingState == State.STATE_INIT)
            return BigFraction.ZERO;

        if (parsingState == State.STATE_OPERATOR
                || parsingState == State.STATE_LEFT_BRACKET) {
            // Impossible cases
            throw new MalformedEquationException("Malformed equation");
        } else if (curBracketLevel != 0) {
            throw new MalformedEquationException("Bracket mismatch");
        }

        // TODO: Testing
        // Convert the equation into Reverse Polish notation
        ArrayList<EquationNode> rpn = new ArrayList<EquationNode>();
        Stack<EquationOperator> rpn_opstack = new Stack<EquationOperator>();
        for (EquationNode node : currentInputBuffer) {
            if (node instanceof EquationOperand) {
                // Operand
                rpn.add(node);
            } else {
                // Operator
                EquationOperator op = (EquationOperator) node;
                switch (op.getType()) {
                    case EQUATION_OPERATOR_TYPE_PLUS:
                    case EQUATION_OPERATOR_TYPE_MINUS:
                    case EQUATION_OPERATOR_TYPE_MULTIPLY:
                    case EQUATION_OPERATOR_TYPE_DIVIDE:
                        if (!rpn_opstack.empty()) {
                            while (!rpn_opstack.empty()
                                    && rpn_opstack.peek().getPriority() >= op.getPriority()) {
                                rpn.add(rpn_opstack.pop());
                            }
                        }
                        rpn_opstack.push(op);
                        break;
                    case EQUATION_OPERATOR_TYPE_LEFT_BRACKET:
                        rpn_opstack.push(op);
                        break;
                    case EQUATION_OPERATOR_TYPE_RIGHT_BRACKET:
                        while (!rpn_opstack.empty()
                                && rpn_opstack.peek().getType()
                                    != EquationOperatorType.EQUATION_OPERATOR_TYPE_LEFT_BRACKET) {
                            rpn.add(rpn_opstack.pop());
                        }
                        if (rpn_opstack.empty()) {
                            throw new MalformedEquationException("Bracket mismatch");
                        }
                        rpn_opstack.pop(); // pop the "("
                        break;
                }
            }
        }

        while (!rpn_opstack.empty()) {
            EquationOperator op = rpn_opstack.pop();
            if (op.getType() == EquationOperatorType.EQUATION_OPERATOR_TYPE_LEFT_BRACKET) {
                throw new MalformedEquationException("Bracket mismatch");
            }

            rpn.add(op);
        }

        // Evaluate the RPN
        Stack<BigFraction> rpn_valstack = new Stack<BigFraction>();
        for (EquationNode node : rpn) {
            if (node instanceof EquationOperand) {
                Number val = ((EquationOperand<?>) node).getData();
                rpn_valstack.push(BigFraction.valueOf(val));
            } else {
                if (rpn_valstack.size() < 2) {
                    throw new MalformedEquationException("Operand number mismatch");
                }

                BigFraction val2 = rpn_valstack.pop();
                BigFraction val1 = rpn_valstack.pop();
                BigFraction result = null;

                switch (((EquationOperator) node).getType()) {
                    case EQUATION_OPERATOR_TYPE_PLUS:
                        result = val1.add(val2);
                        break;
                    case EQUATION_OPERATOR_TYPE_MINUS:
                        result = val1.subtract(val2);
                        break;
                    case EQUATION_OPERATOR_TYPE_MULTIPLY:
                        result = val1.multiply(val2);
                        break;
                    case EQUATION_OPERATOR_TYPE_DIVIDE:
                        if (val2.equals(BigDecimal.ZERO)) {
                            throw new MalformedEquationException("Divide zero error");
                        }
                        result = val1.divide(val2);
                        break;
                    default:
                    	break;
                }
                rpn_valstack.push(result);
            }
        }

        if (rpn_valstack.size() != 1) {
            throw new MalformedEquationException("Operand number mismatch");
        }

        return rpn_valstack.pop();
    }

    public static class MalformedEquationException extends Exception {
        /**
		 * Make the compiler happy.
		 */
		private static final long serialVersionUID = 1L;

		public MalformedEquationException(String message) {
            super(message);
        }
    }

    public enum EquationOperatorType {
        EQUATION_OPERATOR_TYPE_PLUS,
        EQUATION_OPERATOR_TYPE_MINUS,
        EQUATION_OPERATOR_TYPE_MULTIPLY,
        EQUATION_OPERATOR_TYPE_DIVIDE,
        EQUATION_OPERATOR_TYPE_LEFT_BRACKET,
        EQUATION_OPERATOR_TYPE_RIGHT_BRACKET
    }

    /**
     * Base class for representing the basic component of an equation.
     */
    public static abstract class EquationNode {}

    /**
     * Operator node
     */
    public static class EquationOperator extends EquationNode {

        private EquationOperatorType type = EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS;
        private int priority;

        public EquationOperator(EquationOperatorType t) {
            this.type = t;

            switch (t) {
                case EQUATION_OPERATOR_TYPE_PLUS:
                case EQUATION_OPERATOR_TYPE_MINUS:
                    this.priority = 1;
                    break;
                case EQUATION_OPERATOR_TYPE_MULTIPLY:
                case EQUATION_OPERATOR_TYPE_DIVIDE:
                    this.priority = 2;
                    break;
                case EQUATION_OPERATOR_TYPE_LEFT_BRACKET:
                case EQUATION_OPERATOR_TYPE_RIGHT_BRACKET:
                    this.priority = 0;
            }
        }

        public EquationOperatorType getType() {
            return type;
        }

        public int getPriority() {
            return priority;
        }

        public String toString() {
            switch (getType()) {
                case EQUATION_OPERATOR_TYPE_PLUS:
                    return "+";
                case EQUATION_OPERATOR_TYPE_MINUS:
                    return "-";
                case EQUATION_OPERATOR_TYPE_MULTIPLY:
                    return "\u00D7"; // The multiply symbol
                case EQUATION_OPERATOR_TYPE_DIVIDE:
                    return "\u00F7"; // The divide symbol
                case EQUATION_OPERATOR_TYPE_LEFT_BRACKET:
                    return "(";
                case EQUATION_OPERATOR_TYPE_RIGHT_BRACKET:
                    return ")";
            }

            return null;
        }

        @Override
        public boolean equals(Object op) {
            return op instanceof EquationOperator && getType().equals(((EquationOperator) op).getType());
        }
    }

    /**
     * Operand node
     * @param <T> is the real numerical type of the operand
     */
    public static class EquationOperand<T extends Number> extends EquationNode {
        private T data;
        public EquationOperand(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }

        public String toString() {
            return String.valueOf(data);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof EquationOperand) {
                return getData().equals(((EquationOperand) o).getData());
            }
            return false;
        }
    }

}
