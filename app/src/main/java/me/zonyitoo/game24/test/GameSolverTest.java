package me.zonyitoo.game24.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.github.kiprobinson.util.BigFraction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import me.zonyitoo.game24.utils.CardDealer;
import me.zonyitoo.game24.utils.Equation;
import me.zonyitoo.game24.utils.GameSolver;

/**
 * Created by zonyitoo on 14/10/12.
 */
public class GameSolverTest extends AndroidTestCase {

    public static final String LOG_TAG = GameSolverTest.class.getSimpleName();

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

                new Integer[] {1, 1, 2, 2},
                new Integer[] {1, 1, 2, 3},
                new Integer[] {1, 1, 2, 4},
                new Integer[] {1, 1, 2, 5},
                new Integer[] {1, 1, 2, 6},
                new Integer[] {1, 1, 2, 7},
                new Integer[] {1, 1, 2, 8},
                new Integer[] {1, 1, 2, 9},
                new Integer[] {1, 1, 2, 10},
                new Integer[] {1, 1, 2, 11},
                new Integer[] {1, 1, 2, 12},
                new Integer[] {1, 1, 2, 13}
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

                false,
                false,
                false,
                false,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
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

    public void testPerformanceOfValidation() {
        final int NUMBER_OF_TESTS = 10;
        long accumulates = 0L;

        List<List<Equation.EquationOperatorType>> opProducts =
                GameSolver.operatorProducts(GameSolver.AVAILABLE_OPERATORS, 3);

        Random random = new Random();
        int[] pockerCards = new int[] {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
            };

        CardDealer dealer = new CardDealer(getContext());

        Integer[] cards = new Integer[4];
        for (int cs = 0; cs < NUMBER_OF_TESTS; ++cs) {

            int cidx;
            do {
                cidx = 0;
                for (int i = 0; i < pockerCards.length && cidx < 4; ++i) {
                    int randint = random.nextInt(pockerCards.length - i);
                    if (randint < cards.length - cidx) {
                        cards[cidx++] = pockerCards[i];
                    }
                }
            } while (cidx != 4);

            List<CardDealer.Card> clist = parseCards(dealer, cards);
            final Date beginTime = new Date();
            GameSolver.isSolutionExists(clist, opProducts, BigFraction.valueOf(24));
            final Date endTime = new Date();

            final long elapsed = endTime.getTime() - beginTime.getTime();
            Log.d(LOG_TAG, clist.toString() + " " + elapsed + "ms");
            accumulates += elapsed;
        }

        Log.i(LOG_TAG, "GameSolver.isSolutionExists average cost time: "
                    + accumulates / NUMBER_OF_TESTS + "ms");

        // Should not greater than 3 seconds
        assertTrue(accumulates / NUMBER_OF_TESTS < 3000);
    }
}
