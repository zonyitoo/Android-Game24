package me.zonyitoo.game24.test;

import android.test.AndroidTestCase;

import com.github.kiprobinson.util.BigFraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.zonyitoo.game24.utils.Equation;

/**
 * Testing the Equation class
 *
 * Created by zonyitoo on 14/10/11.
 */
public class EquationTest extends AndroidTestCase {

    public static final String LOG_TAG = EquationTest.class.getSimpleName();

    /**
     * Parse an expression from String to <code>List&lt;Equation.EquationNode&gt;</code>
     *
     * @param input expression
     * @return List of EquationNodes
     * @throws Exception Parsing errors
     */
    private List<Equation.EquationNode> parseExpression(String input) throws Exception {
        ArrayList<Equation.EquationNode> result = new ArrayList<Equation.EquationNode>();

        for (int idx = 0; idx < input.length(); ) {
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

        HashMap<String, BigFraction> expectedValues = new HashMap<String, BigFraction>();
        expectedValues.put("1+2*(3-4)/5-6", BigFraction.valueOf(-27, 5));
        expectedValues.put("1", BigFraction.ONE);
        expectedValues.put("1+1", BigFraction.valueOf(2));
        expectedValues.put("2*2", BigFraction.valueOf(4));
        expectedValues.put("3-2", BigFraction.ONE);
        expectedValues.put("4/2", BigFraction.valueOf(2));
        expectedValues.put("1+2*3", BigFraction.valueOf(7));
        expectedValues.put("(1+2)*3", BigFraction.valueOf(9));
        expectedValues.put("1+2+3", BigFraction.valueOf(6));
        expectedValues.put("", BigFraction.ZERO);
        expectedValues.put("(((1)))", BigFraction.ONE);

        for (String exprStr : expectedValues.keySet()) {
            List<Equation.EquationNode> expr = parseExpression(exprStr);

            Equation equation = new Equation(expr, 3);

            BigFraction result = equation.evaluate();
            assertEquals(exprStr, expectedValues.get(exprStr), result);
        }

        try {
            List<Equation.EquationNode> wrongExpr = parseExpression("((((1234+1111))))");
            Equation equation = new Equation(wrongExpr, 3);
            assertTrue(false);
        } catch (Equation.MalformedEquationException e) {

        }
    }
}
