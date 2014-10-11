package me.zonyitoo.game24.test;

import android.test.AndroidTestCase;

import com.github.kiprobinson.util.BigFraction;

import java.util.ArrayList;
import java.util.List;

import me.zonyitoo.game24.utils.CardDealer;
import me.zonyitoo.game24.utils.Equation;
import me.zonyitoo.game24.utils.GameSolver;

/**
 * Created by zonyitoo on 14/10/12.
 */
public class GameSolverTest extends AndroidTestCase {

    private List<CardDealer.Card> parseCards(CardDealer dealer, Integer[] cards) {
        ArrayList<CardDealer.Card> result = new ArrayList<CardDealer.Card>();
        for (Integer i : cards) {
            result.add(new CardDealer.Card(dealer, 0, i));
        }
        return result;
    }

    public void testValidation() throws Exception {
        // Refer to http://gottfriedville.net/games/24/index.shtml

        Integer[][] combinations = new Integer[][] {
                new Integer[] {1, 1, 1, 1},
                new Integer[] {1, 1, 1, 2},
                new Integer[] {1, 1, 1, 3},
                new Integer[] {1, 1, 1, 4},
                new Integer[] {1, 1, 1, 5},
                new Integer[] {1, 1, 1, 6},
                new Integer[] {1, 1, 1, 7},
                new Integer[] {1, 1, 1, 8},
                new Integer[] {1, 1, 1, 9},
                new Integer[] {1, 1, 1, 10},
                new Integer[] {1, 1, 1, 11},
                new Integer[] {1, 1, 1, 12},
                new Integer[] {1, 1, 1, 13},
        };

        boolean[] expectedResults = new boolean[] {
                false, // 1
                false, // 2
                false, // 3
                false, // 4
                false, // 5
                false, // 6
                false, // 7
                true, // 8
                false, // 9
                false, // 10
                true, // 11
                true, // 12
                true, // 13
        };

        List<List<Equation.EquationOperatorType>> opPerm =
                GameSolver.operatorProducts(GameSolver.AVAILABLE_OPERATORS, 3);

        CardDealer dealer = new CardDealer(getContext());

        for (int i = 0; i < combinations.length; ++i) {
            List<CardDealer.Card> cards = parseCards(dealer, combinations[i]);
            assertEquals(cards.toString(), expectedResults[i],
                    GameSolver.isSolutionExists(cards, opPerm, BigFraction.valueOf(24)));
        }

    }
}
