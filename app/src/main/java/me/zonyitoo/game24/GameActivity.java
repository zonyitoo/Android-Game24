package me.zonyitoo.game24;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameActivity extends ActionBarActivity {

    ImageButton[] button_Cards;
    HashMap<Equation.EquationOperatorType, Button> button_Operators
            = new HashMap<Equation.EquationOperatorType, Button>();

    TextView textView_Equation;

    Button button_BackSpace;
//    Button button_Restart;

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

        button_Operators.put(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS,
                (Button) findViewById(R.id.button_game_op_plus));
        button_Operators.put(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MINUS,
                (Button) findViewById(R.id.button_game_op_minus));
        button_Operators.put(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MULTIPLY,
                (Button) findViewById(R.id.button_game_op_multiply));
        button_Operators.put(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_DIVIDE,
                (Button) findViewById(R.id.button_game_op_divide));
        button_Operators.put(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_LEFT_BRACKET,
                (Button) findViewById(R.id.button_game_op_left_bracket));
        button_Operators.put(Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_RIGHT_BRACKET,
                (Button) findViewById(R.id.button_game_op_right_bracket));

        // button_Cards and button_Operators use the same listener
        CardsOperatorClickListener listener = new CardsOperatorClickListener();
        for (ImageButton c : button_Cards) {
            c.setOnClickListener(listener);
        }
        for (Button op : button_Operators.values()) {
            op.setOnClickListener(listener);
        }

        textView_Equation = (TextView) findViewById(R.id.text_game_equation);

        // Backspace button
        button_BackSpace = (Button) findViewById(R.id.button_game_backspace);
        button_BackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Equation.EquationNode node = currentInputBuffer.get(currentInputBuffer.size() - 1);
                currentInputBuffer.remove(currentInputBuffer.size() - 1);

                if (currentInputBuffer.isEmpty()) {
                    view.setEnabled(false);
                }

                if (node instanceof Equation.EquationIntegerOperand) {
                    // Is a card
                    Equation.EquationIntegerOperand num = (Equation.EquationIntegerOperand) node;

                    for (int i = 0; i < showingCards.size(); ++i) {
                        if (showingCards.get(i).getNumber() == num.getData()) {
                            button_Cards[i].setEnabled(true);
                            break;
                        }
                    }
                } else {

                }

                refreshEquationView();
            }
        });
        button_BackSpace.setLongClickable(true);
        button_BackSpace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (currentInputBuffer.isEmpty()) {
                    return false;
                }

                currentInputBuffer.clear();

                for (ImageButton b : button_Cards) {
                    b.setEnabled(true);
                }

                refreshEquationView();
                view.setEnabled(false);
                return true;
            }
        });

//        button_Restart = (Button) findViewById(R.id.button_game_restart);
//        button_Restart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                restart();
//            }
//        });

        restart();
	}

    void restart() {
        showingCards = dealer.deal();

        for (int i = 0; i < showingCards.size(); ++i) {
            ImageButton curBtn = this.button_Cards[i];
            curBtn.setImageBitmap(showingCards.get(i).getImageBitmap());
        }

        Animation[] anims = new Animation[] {
            AnimationUtils.loadAnimation(this, R.anim.activity_game_card_enter_ul),
            AnimationUtils.loadAnimation(this, R.anim.activity_game_card_enter_ur),
            AnimationUtils.loadAnimation(this, R.anim.activity_game_card_enter_bl),
            AnimationUtils.loadAnimation(this, R.anim.activity_game_card_enter_br)
        };

        for (int i = 0; i < button_Cards.length; ++i) {
            button_Cards[i].startAnimation(anims[i]);
            button_Cards[i].setEnabled(true);
        }

        textView_Equation.setText("");
        currentInputBuffer.clear();
        button_BackSpace.setEnabled(false);
    }

    private void refreshEquationView() {
        StringBuilder b = new StringBuilder();
        for (Equation.EquationNode n : currentInputBuffer) {
            b.append(n.toString());
        }

        textView_Equation.setText(b.toString());
    }

    @Override
    public void onBackPressed() {
        /*
        When pressing the back button, a dialog will pop up and ask for confirmation.
        For preventing hit the button by accident.
         */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);

        MenuItem item_Refresh = menu.findItem(R.id.menu_game_refresh);
        item_Refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                restart();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu);
        return true;
    }

    class CardsOperatorClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            /*
            Each press will insert a `Equation.EquationNode` object into the array.
            Some of them may also disable some button.
             */
            switch (view.getId()) {
                // Selecting cards, which will be translated into integers
                case R.id.button_game_card_1: {
                    int number = showingCards.get(0).getNumber();
                    Equation.EquationIntegerOperand op = new Equation.EquationIntegerOperand(number);
                    currentInputBuffer.add(op);

                    // Disable it
                    view.setEnabled(false);
                    break;
                }
                case R.id.button_game_card_2: {
                    int number = showingCards.get(1).getNumber();
                    Equation.EquationIntegerOperand op = new Equation.EquationIntegerOperand(number);
                    currentInputBuffer.add(op);

                    // Disable it
                    view.setEnabled(false);
                    break;
                }
                case R.id.button_game_card_3: {
                    int number = showingCards.get(2).getNumber();
                    Equation.EquationIntegerOperand op = new Equation.EquationIntegerOperand(number);
                    currentInputBuffer.add(op);

                    // Disable it
                    view.setEnabled(false);
                    break;
                }
                case R.id.button_game_card_4: {
                    int number = showingCards.get(3).getNumber();
                    Equation.EquationIntegerOperand op = new Equation.EquationIntegerOperand(number);
                    currentInputBuffer.add(op);

                    // Disable it
                    view.setEnabled(false);
                    break;
                }

                // Selecting button_Operators
                case R.id.button_game_op_plus:
                    currentInputBuffer.add(
                            new Equation.EquationOperator(
                                    Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS));
                    break;
                case R.id.button_game_op_minus:
                    currentInputBuffer.add(
                            new Equation.EquationOperator(
                                    Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MINUS));
                    break;
                case R.id.button_game_op_multiply:
                    currentInputBuffer.add(
                            new Equation.EquationOperator(
                                    Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MULTIPLY));
                    break;
                case R.id.button_game_op_divide:
                    currentInputBuffer.add(
                            new Equation.EquationOperator(
                                    Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_DIVIDE));
                    break;
                case R.id.button_game_op_left_bracket:
                    currentInputBuffer.add(
                            new Equation.EquationOperator(
                                    Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_LEFT_BRACKET));
                    break;
                case R.id.button_game_op_right_bracket:
                    currentInputBuffer.add(
                            new Equation.EquationOperator(
                                    Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_RIGHT_BRACKET));
                    break;
            }

            // Enable the button
            button_BackSpace.setEnabled(true);

            refreshEquationView();
        }
    }
}
