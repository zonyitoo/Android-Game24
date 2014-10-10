package me.zonyitoo.game24;

import android.support.annotation.FractionRes;
import android.util.Log;

import com.github.kiprobinson.util.BigFraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

        Log.d(LOG_TAG, "Cannot found any solutions");

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

    private static Set<BigFraction> enumerateAllSolution(List<Equation.EquationOperatorType> ops,
                                                    List<CardDealer.Card> cards,
                                                    int from, int to) {
        Set<BigFraction> results = new HashSet<BigFraction>();
        if (from == to) {
            results.add(BigFraction.valueOf(cards.get(from).getNumber()));
            return results;
        } else if (from > to) {
            results.add(BigFraction.ZERO);
            return results;
        }

        for (int i = from; i < to; ++i) {
            Set<BigFraction> leftres = enumerateAllSolution(ops, cards, from, i - 1);
            Set<BigFraction> rightres = enumerateAllSolution(ops, cards, i + 2, to);

            Equation.EquationOperatorType
                    leftop = (i != 0) ? ops.get(i - 1)
                                      : Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS,
                    rightop = (i != ops.size() - 1) ? ops.get(i + 1)
                                      : Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS,
                    curop = ops.get(i);

            BigFraction operand1 = BigFraction.valueOf(cards.get(i).getNumber());
            BigFraction operand2 = BigFraction.valueOf(cards.get(i + 1).getNumber());
            BigFraction curres = evaluateValue(operand1, operand2, curop);

            for (BigFraction lresult : leftres) {
                for (BigFraction rresult : rightres) {
                    try {
                        BigFraction r = evaluateValue(lresult, curres, leftop);
                        r = evaluateValue(r, rresult, rightop);
                        results.add(r);
                    } catch (Exception ignore) {
                        // Just ignore the error case
                    }

                    try {
                        BigFraction r = evaluateValue(curres, rresult, rightop);
                        r = evaluateValue(lresult, r, leftop);
                        results.add(r);
                    } catch (Exception ignore) {
                        // Just ignore the error case
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
