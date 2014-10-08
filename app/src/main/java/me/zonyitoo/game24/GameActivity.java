package me.zonyitoo.game24;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends Activity {

    ImageButton[] button_Cards;
    HashMap<Character, Button> operators = new HashMap<Character, Button>();

    TextView textView_Equation;

    Button button_BackSpace;
    Button button_Restart;

    ArrayList<Equation.EquationNode> currentInputBuffer = new ArrayList<Equation.EquationNode>();

    List<CardDealer.Card> showingCards;
    CardDealer dealer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dealer = new CardDealer(this);

        button_Cards = new ImageButton[]{
            (ImageButton) findViewById(R.id.button_game_card_1),
            (ImageButton) findViewById(R.id.button_game_card_2),
            (ImageButton) findViewById(R.id.button_game_card_3),
            (ImageButton) findViewById(R.id.button_game_card_4)
        };

        operators.put('+', (Button) findViewById(R.id.button_game_op_plus));
        operators.put('-', (Button) findViewById(R.id.button_game_op_minus));
        operators.put('*', (Button) findViewById(R.id.button_game_op_multiply));
        operators.put('/', (Button) findViewById(R.id.button_game_op_divide));
        operators.put('(', (Button) findViewById(R.id.button_game_op_left_bracket));
        operators.put(')', (Button) findViewById(R.id.button_game_op_right_bracket));

        // Cards and operators use the same listener
        CardsOperatorClickListener listener = new CardsOperatorClickListener();
        for (ImageButton c : button_Cards) {
            c.setOnClickListener(listener);
        }
        for (Button op : operators.values()) {
            op.setOnClickListener(listener);
        }

        textView_Equation = (TextView) findViewById(R.id.text_game_equation);
        button_BackSpace = (Button) findViewById(R.id.button_game_backspace);
        button_Restart = (Button) findViewById(R.id.button_game_restart);

        button_Restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        refresh();
	}

    void refresh() {
        showingCards = dealer.deal();

        for (int i = 0; i < showingCards.size(); ++i) {
            ImageButton curBtn = this.button_Cards[i];
            curBtn.setImageBitmap(showingCards.get(i).getImageBitmap());
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitAlertBuilder = new AlertDialog.Builder(this);
        exitAlertBuilder.setTitle(R.string.string_Confirm_Exit_Title);
        exitAlertBuilder.setPositiveButton(R.string.string_Confirm_Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                GameActivity.this.finish();
            }
        });
        exitAlertBuilder.setNegativeButton(R.string.string_Confirm_No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        exitAlertBuilder.create().show();
    }


    class CardsOperatorClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                // Selecting cards, which will be translated into integers
                case R.id.button_game_card_1:
                    break;
                case R.id.button_game_card_2:
                    break;
                case R.id.button_game_card_3:
                    break;
                case R.id.button_game_card_4:
                    break;

                // Selecting operators
                case R.id.button_game_op_plus:
                    break;
                case R.id.button_game_op_minus:
                    break;
                case R.id.button_game_op_multiply:
                    break;
                case R.id.button_game_op_divide:
                    break;
                case R.id.button_game_op_left_bracket:
                    break;
                case R.id.button_game_op_right_bracket:
                    break;
            }
        }
    }
}
