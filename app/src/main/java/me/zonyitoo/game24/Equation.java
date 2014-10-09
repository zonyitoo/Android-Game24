package me.zonyitoo.game24;

import com.github.kiprobinson.util.BigFraction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by zonyitoo on 14/10/7.
 */
public class Equation {

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

    public Equation(int maxBracketLevel) {
        this.maxBracketLevel = maxBracketLevel;
    }

    public void clear() {
        currentInputBuffer.clear();
        parsingState = State.STATE_INIT;
        curBracketLevel = 0;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Equation.EquationNode n : currentInputBuffer) {
            b.append(n.toString());
        }
        return b.toString();
    }

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
                        throw new MalformedEquationException("Not expecting a operator");
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
                Number val = ((EquationOperand) node).getData();
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

    public static abstract class EquationNode {}

    public static class EquationOperator extends EquationNode {

        private EquationOperatorType type = EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS;
        private int priority;

        public EquationOperator(EquationOperatorType t, int priority) {
            this.type = t;
            this.priority = priority;
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
                    return "×";
                case EQUATION_OPERATOR_TYPE_DIVIDE:
                    return "÷";
                case EQUATION_OPERATOR_TYPE_LEFT_BRACKET:
                    return "(";
                case EQUATION_OPERATOR_TYPE_RIGHT_BRACKET:
                    return ")";
            }

            return null;
        }
    }

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
    }

}
