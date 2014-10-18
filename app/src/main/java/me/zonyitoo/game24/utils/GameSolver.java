package me.zonyitoo.game24.utils;

import android.util.Log;

import com.github.kiprobinson.util.BigFraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zonyitoo on 14/10/10.
 */
public class GameSolver {

    public static final String LOG_TAG = GameSolver.class.getSimpleName();

    public static final Equation.EquationOperatorType[] AVAILABLE_OPERATORS = new Equation.EquationOperatorType[] {
            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS,
            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MINUS,
            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MULTIPLY,
            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_DIVIDE
    };

    public static boolean isSolutionExists(List<CardDealer.Card> cards,
                                           List<List<Equation.EquationOperatorType>> opProducts, BigFraction target) {
        ArrayList<CardDealer.Card> tmpCards = new ArrayList<CardDealer.Card>(cards);
        Collections.sort(tmpCards); // Should be removed, because the cards should have been in order!

        Log.d(LOG_TAG, "Finding solutions for " + cards);

        // TODO: Display the one solution

        do {
            for (List<Equation.EquationOperatorType> ops : opProducts) {
                Set<BigFraction> result = enumerateAllSolution(ops, tmpCards, 0, cards.size() - 1);
                if (result.contains(target)) {
                    Log.d(LOG_TAG, "Found!");
                    return true;
                }
            }
        } while (nextPermutation(tmpCards));

        Log.d(LOG_TAG, "Cannot find any solutions");

        return false;
    }

    private static boolean nextPermutation(List<CardDealer.Card> cards) {
        if (cards.size() <= 1) {
            return false;
        }

        int i = cards.size() - 1;
        for (;;) {
            int ii = i--;

            if (cards.get(i).compareTo(cards.get(ii)) < 0) {
                int j = cards.size();
                while (cards.get(i).compareTo(cards.get(--j)) >= 0);

                Collections.swap(cards, i, j);
                for (int idx1 = ii, idx2 = cards.size() - 1; idx1 < idx2; ++idx1, --idx2) {
                    Collections.swap(cards, idx1, idx2);
                }
                return true;
            }

            if (i == 0) {
                for (int idx1 = 0, idx2 = cards.size() - 1; idx1 < idx2; ++idx1, --idx2) {
                    Collections.swap(cards, idx1, idx2);
                }
                return false;
            }
        }
    }

    /**
     * This is the main method for finding all possible solutions of the given card combination.
     *
     * Basic idea of the method is
     *
     * 1. If the expression is empty, the only value should be ZERO
     * 2. If the expression contains only one operand, the value should equal to the operand
     * 3. Otherwise, for every operators in the expression
     *     1. Divide the expression from the operator
     *     2. Calculate all the values of the left expression
     *     3. Calculate all the values of the right expression
     *     4. For every combinations of left values and right values, calculate all possible
     *        values of the expression
     *
     * @param ops Operators
     * @param cards Cards
     * @param from begin index
     * @param to end index
     * @return possible values of the expression
     */
    private static Set<BigFraction> enumerateAllSolution(List<Equation.EquationOperatorType> ops,
                                                    List<CardDealer.Card> cards,
                                                    int from, int to) {
        Set<BigFraction> results = new HashSet<BigFraction>();
        if (from == to) {
            // Only one operand
            results.add(BigFraction.valueOf(cards.get(from).getNumber()));
            return results;
        } else if (from > to) {
            // Empty expression
            results.add(BigFraction.ZERO);
            return results;
        } else if (from + 1 == to) {
            // Two operands
            BigFraction r = evaluateValue(
                    BigFraction.valueOf(cards.get(from).getNumber()),
                    BigFraction.valueOf(cards.get(to).getNumber()),
                    ops.get(from));
            results.add(r);
            return results;
        }

        boolean hasMetPlus = false, hasMetMultiply = false;

        for (int opidx = from; opidx < to; ++opidx) {
            if (ops.get(opidx) == Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS) {
                if (hasMetPlus) {
                    continue;
                }
                hasMetPlus = true;
            } else if (ops.get(opidx) == Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MULTIPLY) {
                if (hasMetMultiply) {
                    continue;
                }
                hasMetMultiply = true;
            }

            // (...) op (...)
            Set<BigFraction> leftResults = enumerateAllSolution(ops, cards, from, opidx);
            Set<BigFraction> rightResults = enumerateAllSolution(ops, cards, opidx + 1, to);

            for (BigFraction l : leftResults) {
                for (BigFraction r : rightResults) {
                    try {
                        BigFraction ans = evaluateValue(l, r, ops.get(opidx));
                        results.add(ans);
                    } catch (ArithmeticException ignored) {
                        // Ignore the error cases
                    }
                }
            }
        }

        return results;
    }

    private static BigFraction evaluateValue(BigFraction a, BigFraction b,
                                    Equation.EquationOperatorType t) throws ArithmeticException {
        switch (t) {
            case EQUATION_OPERATOR_TYPE_PLUS:
                return a.add(b);
            case EQUATION_OPERATOR_TYPE_MINUS:
                return a.subtract(b);
            case EQUATION_OPERATOR_TYPE_MULTIPLY:
                return a.multiply(b);
            case EQUATION_OPERATOR_TYPE_DIVIDE:
                return a.divide(b);
            default:
            	break;
        }
        throw new ArithmeticException("Invalid operator");
    }

    public static Set<List<CardDealer.Card>> permuteCards(List<CardDealer.Card> cards) {
        Set<List<CardDealer.Card>> result = new HashSet<List<CardDealer.Card>>();
        permuteCardsSolver(cards, result, 0);
        return result;
    }

    private static void permuteCardsSolver(List<CardDealer.Card> cards,
                                     Set<List<CardDealer.Card>> result, int k) {
        if (k == cards.size()) {
            List<CardDealer.Card> r = new ArrayList<CardDealer.Card>(cards);
            result.add(r);
            return;
        }

        for (int i = k; i < cards.size(); ++i) {
            Collections.swap(cards, i, k);
            permuteCardsSolver(cards, result, k + 1);
            Collections.swap(cards, i, k);
        }

    }

    public static List<List<Equation.EquationOperatorType>> operatorProducts(
            final Equation.EquationOperatorType[] ops, int repeat) {
        List<List<Equation.EquationOperatorType>> result = new ArrayList<List<Equation.EquationOperatorType>>();
        List<Equation.EquationOperatorType> stack = new ArrayList<Equation.EquationOperatorType>();
        operatorProductsSolver(ops, result, repeat, stack);
        return result;
    }

    private static void operatorProductsSolver(final Equation.EquationOperatorType[] ops, List<List<Equation.EquationOperatorType>> result,
                                               int repeat, List<Equation.EquationOperatorType> stack) {
        if (stack.size() == repeat) {
            result.add(new ArrayList<Equation.EquationOperatorType>(stack));
            return;
        }

        for (Equation.EquationOperatorType t : ops) {
            stack.add(t);
            operatorProductsSolver(ops, result, repeat, stack);
            stack.remove(stack.size() - 1);
        }
    }
}
