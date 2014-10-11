package me.zonyitoo.game24.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.github.kiprobinson.util.BigFraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.zonyitoo.game24.utils.Equation;

/**
 * Testing the Equation class
 *
 * Created by zonyitoo on 14/10/11.
 */
public class EquationTest extends AndroidTestCase {

    public static final String LOG_TAG = EquationTest.class.getSimpleName();

    private List<Equation.EquationNode> parseExpression(String input) throws Exception {
        ArrayList<Equation.EquationNode> result = new ArrayList<Equation.EquationNode>();

        for (int idx = 0; idx < input.length(); ) {
            System.out.println(idx + " " + result);
            switch (input.charAt(idx)) {
                case '+':
                    result.add(new Equation.EquationOperator(
                            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS));
                    ++idx;
                    break;
                case '-':
                    result.add(new Equation.EquationOperator(
                            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MINUS));
                    ++idx;
                    break;
                case '*':
                    result.add(new Equation.EquationOperator(
                            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MULTIPLY));
                    ++idx;
                    break;
                case '/':
                    result.add(new Equation.EquationOperator(
                            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_DIVIDE));
                    ++idx;
                    break;
                case '(':
                    result.add(new Equation.EquationOperator(
                            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_LEFT_BRACKET));
                    ++idx;
                    break;
                case ')':
                    result.add(new Equation.EquationOperator(
                            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_RIGHT_BRACKET));
                    ++idx;
                    break;
                default:
                    int beginIdx = idx;
                    OUTER_LOOP:
                    while (idx < input.length()) {
                        if (Character.isDigit(input.charAt(idx))) {
                            ++idx;
                        } else {
                            switch (input.charAt(idx)) {
                                case '+':
                                case '-':
                                case '*':
                                case '/':
                                case '(':
                                case ')':
                                    break OUTER_LOOP;
                                default:
                                    throw new Exception("Invalid character");
                            }
                        }
                    }

                    String number = input.substring(beginIdx, idx);
                    result.add(new Equation.EquationOperand<Integer>(Integer.valueOf(number)));
                    break;
            }
        }

        return result;
    }

    public void testParsingAlgorithm() throws Exception {
        List<Equation.EquationNode> expr = parseExpression("1+2*(3-4)/5-6");

        Equation.EquationNode[] expected = new Equation.EquationNode[] {
                new Equation.EquationOperand<Integer>(1),
                new Equation.EquationOperator(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS),
                new Equation.EquationOperand<Integer>(2),
                new Equation.EquationOperator(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MULTIPLY),
                new Equation.EquationOperator(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_LEFT_BRACKET),
                new Equation.EquationOperand<Integer>(3),
                new Equation.EquationOperator(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MINUS),
                new Equation.EquationOperand<Integer>(4),
                new Equation.EquationOperator(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_RIGHT_BRACKET),
                new Equation.EquationOperator(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_DIVIDE),
                new Equation.EquationOperand<Integer>(5),
                new Equation.EquationOperator(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MINUS),
                new Equation.EquationOperand<Integer>(6)
        };

        assertEquals(Arrays.asList(expected), expr);
    }

    public void testEvaluationAlgorithm() throws Exception {
        List<Equation.EquationNode> expr = parseExpression("1+2*(3-4)/5-6");

        Equation equation = new Equation();
        for (Equation.EquationNode node : expr) {
            equation.add(node);
        }

        BigFraction result = equation.evaluate();
        assertEquals(BigFraction.valueOf(-27, 5), result);
    }
}
