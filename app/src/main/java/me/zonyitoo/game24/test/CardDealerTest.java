package me.zonyitoo.game24.test;

import android.test.AndroidTestCase;

import java.util.List;

import me.zonyitoo.game24.utils.CardDealer;

/**
 * Created by zonyitoo on 14/10/12.
 */
public class CardDealerTest extends AndroidTestCase {

    public void testDealingCards() throws Exception {
        CardDealer dealer = new CardDealer(this.getContext());
        List<CardDealer.Card> cards = dealer.deal();

        assertEquals(4, cards.size());
    }
}
