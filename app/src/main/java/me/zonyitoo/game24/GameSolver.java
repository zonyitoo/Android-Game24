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

    private static final Equation.EquationOperatorType[] ops = new Equation.EquationOperatorType[] {
            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS,
            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MINUS,
            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MULTIPLY,
            Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_DIVIDE
    };

    public static boolean isSolutionExists(List<CardDealer.Card> cards, BigFraction target) {
        Set<List<CardDealer.Card>> cardPermutes = permuteCards(cards);
        List<List<Equation.EquationOperatorType>> opProducts =
                operatorProducts(ops, cards.size() - 1);

        return isSolutionExistsSolver(cardPermutes, opProducts, target);
    }

    private static boolean isSolutionExistsSolver(Set<List<CardDealer.Card>> cardPermutes,
                                                  List<List<Equation.EquationOperatorType>> opProducts,
                                                  BigFraction target) {
        for (List<Equation.EquationOperatorType> ops : opProducts) {
            for (List<CardDealer.Card> cards : cardPermutes) {
                Set<BigFraction> result = enumerateAllSolution(ops, cards, 0, cards.size() - 1);
                if (result.contains(target)) {
                    return true;
                }
            }
        }

        return false;
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

    private static Set<List<CardDealer.Card>> permuteCards(List<CardDealer.Card> cards) {
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

    private static List<List<Equation.EquationOperatorType>> operatorProducts(
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
