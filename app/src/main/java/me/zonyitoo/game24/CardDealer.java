package me.zonyitoo.game24;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by zonyitoo on 14/10/7.
 */
public class CardDealer {

    private static final int LRU_MAX_CAPACITY = 20;

    Context context;
    LRUCardCache cache = new LRUCardCache(LRU_MAX_CAPACITY);

    List<Card> cards;

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
        Collections.shuffle(cards);
    }

    public List<Card> deal() {
        Card[] c = new Card[4];
        Random r = new Random();

        for (int nPickle = 0, nLeft = cards.size(), idx = 0;
             nPickle < 4 && idx < cards.size(); --nLeft, ++idx) {
            int rand = r.nextInt(nLeft);
            if (rand < 4 - nPickle) {
                c[nPickle++] = cards.get(idx);
            }
        }

        return Arrays.asList(c);
    }

    public class Card {
        private int number;
        private int resourceId;

        private CardDealer dealer;

        public Card(CardDealer dealer, int resourceId, int number) {
            this.dealer = dealer;
            this.resourceId = resourceId;
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public Bitmap getImageBitmap() {
            return dealer.getCardImageBitmapById(this.resourceId);
        }
    }

    public Bitmap getCardImageBitmapById(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
            cache.put(id, bitmap);
            return bitmap;
        }
    }

    protected class LRUCardCache extends LinkedHashMap<Integer, Bitmap> {

        private int maxCapacity;

        private static final float DEFAULT_LOAD_FACTOR = 0.75f;

        public LRUCardCache(int maxCapacity) {
            super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
            this.maxCapacity = maxCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Entry<Integer, Bitmap> eldest) {
            return this.size() > maxCapacity;
        }

        @Override
        public Bitmap get(Object key) {
            synchronized (this) {
                return super.get(key);
            }
        }

        @Override
        public Bitmap put(Integer key, Bitmap b) {
            synchronized (this) {
                return super.put(key, b);
            }
        }
    }
}
