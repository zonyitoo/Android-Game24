package me.zonyitoo.game24.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.github.kiprobinson.util.BigFraction;

import me.zonyitoo.game24.R;


/**
 * Created by zonyitoo on 14/10/7.
 */
public class CardDealer {

    public static final String LOG_TAG = CardDealer.class.getSimpleName();

    private static final int LRU_MAX_CAPACITY = 20;

    Context context;
    LRUCardCache cache = new LRUCardCache(LRU_MAX_CAPACITY);

    List<Card> cards;

    final List<List<Equation.EquationOperatorType>> OPERATOR_PRODUCTS =
            GameSolver.operatorProducts(GameSolver.AVAILABLE_OPERATORS, 3);

    /**
     * Construct a new card dealer. <br/>
     *
     * This constructor will enumerate 52 poker cards' resource id and
     * store its' real representing number in 24 game. But it will not
     * load the image right now. <br/>
     *
     * @param context android context
     */
    public CardDealer(Context context) {
        this.context = context;

        // Define all 52 cards
        Card[] _cards = new Card[] {
            new Card(this, R.drawable.bordered_c_a, 1),
            new Card(this, R.drawable.bordered_c_2, 2),
            new Card(this, R.drawable.bordered_c_3, 3),
            new Card(this, R.drawable.bordered_c_4, 4),
            new Card(this, R.drawable.bordered_c_5, 5),
            new Card(this, R.drawable.bordered_c_6, 6),
            new Card(this, R.drawable.bordered_c_7, 7),
            new Card(this, R.drawable.bordered_c_8, 8),
            new Card(this, R.drawable.bordered_c_9, 9),
            new Card(this, R.drawable.bordered_c_10, 10),
            new Card(this, R.drawable.bordered_c_j, 11),
            new Card(this, R.drawable.bordered_c_q, 12),
            new Card(this, R.drawable.bordered_c_k, 13),

            new Card(this, R.drawable.bordered_d_a, 1),
            new Card(this, R.drawable.bordered_d_2, 2),
            new Card(this, R.drawable.bordered_d_3, 3),
            new Card(this, R.drawable.bordered_d_4, 4),
            new Card(this, R.drawable.bordered_d_5, 5),
            new Card(this, R.drawable.bordered_d_6, 6),
            new Card(this, R.drawable.bordered_d_7, 7),
            new Card(this, R.drawable.bordered_d_8, 8),
            new Card(this, R.drawable.bordered_d_9, 9),
            new Card(this, R.drawable.bordered_d_10, 10),
            new Card(this, R.drawable.bordered_d_j, 11),
            new Card(this, R.drawable.bordered_d_q, 12),
            new Card(this, R.drawable.bordered_d_k, 13),

            new Card(this, R.drawable.bordered_h_a, 1),
            new Card(this, R.drawable.bordered_h_2, 2),
            new Card(this, R.drawable.bordered_h_3, 3),
            new Card(this, R.drawable.bordered_h_4, 4),
            new Card(this, R.drawable.bordered_h_5, 5),
            new Card(this, R.drawable.bordered_h_6, 6),
            new Card(this, R.drawable.bordered_h_7, 7),
            new Card(this, R.drawable.bordered_h_8, 8),
            new Card(this, R.drawable.bordered_h_9, 9),
            new Card(this, R.drawable.bordered_h_10, 10),
            new Card(this, R.drawable.bordered_h_j, 11),
            new Card(this, R.drawable.bordered_h_q, 12),
            new Card(this, R.drawable.bordered_h_k, 13),

            new Card(this, R.drawable.bordered_s_a, 1),
            new Card(this, R.drawable.bordered_s_2, 2),
            new Card(this, R.drawable.bordered_s_3, 3),
            new Card(this, R.drawable.bordered_s_4, 4),
            new Card(this, R.drawable.bordered_s_5, 5),
            new Card(this, R.drawable.bordered_s_6, 6),
            new Card(this, R.drawable.bordered_s_7, 7),
            new Card(this, R.drawable.bordered_s_8, 8),
            new Card(this, R.drawable.bordered_s_9, 9),
            new Card(this, R.drawable.bordered_s_10, 10),
            new Card(this, R.drawable.bordered_s_j, 11),
            new Card(this, R.drawable.bordered_s_q, 12),
            new Card(this, R.drawable.bordered_s_k, 13),
        };

        cards = Arrays.asList(_cards);
//        Collections.sort(cards);
        Collections.shuffle(cards);
    }

    /**
     * Sampling 4 cards randomly from the card set. <br/>
     *
     * This function will also try to validate the 4 cards. If there is no
     * solution, it will repeat the procedure again until finding a card set
     * that has at least one solutions. <br/>
     *
     * @see me.zonyitoo.game24.utils.CardDealer.Card
     *
     * @return 4 cards
     */
    public List<Card> deal() {
        ArrayList<Card> c = new ArrayList<Card>();
        Random r = new Random();

        do {

            c.clear();

            for (int i = 0; c.size() < 4 && i < cards.size(); ++i) {
                int randint = r.nextInt(cards.size() - i);
                if (randint < 4 - c.size()) {
                    c.add(cards.get(i));
                }
            }

        } while (!validateCardsCombination(c));

        return c;
    }

    /**
     * Validate the sampled 4 cards by finding at least one 24 game solutions.
     *
     * @param c 4 cards for validation
     * @return has solutions or not
     */
    private boolean validateCardsCombination(ArrayList<Card> c) {
        if (c.size() != 4) {
            return false;
        }

        Log.d(LOG_TAG, "validateCardsCombination cards=" + c.toString());

        boolean validateResult = GameSolver.isSolutionExists(c, OPERATOR_PRODUCTS,
                BigFraction.valueOf(24));
        Log.d(LOG_TAG, "validateCardsCombination result=" + validateResult);

        return validateResult;
    }

    public class Card implements Comparable<Card> {
        /**
         * Real card number
         */
        private int number;
        /**
         * Card image's resource ID
         */
        private int resourceId;

        /**
         * Reference of the CardDealer
         */
        private CardDealer dealer;

        public Card(CardDealer dealer, int resourceId, int number) {
            this.dealer = dealer;
            this.resourceId = resourceId;
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        /**
         * Get drawable from the LRU cache
         *
         * @return Card image
         */
        public Drawable getImageDrawable() {
            return dealer.getCardImageDrawableById(this.resourceId);
        }

        @Override
        public String toString() {
            if (getNumber() < 11) {
                return "Card{" + getNumber() + "}";
            } else {
                switch (getNumber()) {
                    case 11:
                        return "Card{J}";
                    case 12:
                        return "Card{Q}";
                    case 13:
                        return "Card{K}";
                    default:
                        return "Card{Unknown}";
                }
            }
        }

        @Override
        public int compareTo(Card card) {
            return this.getNumber() - card.getNumber();
        }
    }

    /**
     * Get card drawable from the LRU cache
     *
     * @param id resource ID
     * @return drawable
     */
    public Drawable getCardImageDrawableById(int id) {
        if (cache.containsKey(id)) {
            return (Drawable) cache.get(id);
        } else {
            Drawable d = context.getResources().getDrawable(id);
            cache.put(id, d);
            return d;
        }
    }

    /**
     * A LRU cache for storing card image.
     */
    protected class LRUCardCache extends LinkedHashMap<Integer, Object> {

        /**
		 * Make the compiler happy.
		 */
		private static final long serialVersionUID = 42L;

		private int maxCapacity;

        private static final float DEFAULT_LOAD_FACTOR = 0.75f;

        public LRUCardCache(int maxCapacity) {
            super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
            this.maxCapacity = maxCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Entry<Integer, Object> eldest) {
            return this.size() > maxCapacity;
        }

        @Override
        public Object get(Object key) {
            synchronized (this) {
                return super.get(key);
            }
        }

        @Override
        public Object put(Integer key, Object b) {
            synchronized (this) {
                return super.put(key, b);
            }
        }
    }
}
