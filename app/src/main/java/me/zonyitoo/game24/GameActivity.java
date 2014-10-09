package me.zonyitoo.game24;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GameActivity extends ActionBarActivity {

    static final int MAX_BRACKET_LEVEL = 3;

    ImageButton[] button_Cards;
    HashMap<Equation.EquationOperatorType, Button> button_Operators
            = new HashMap<Equation.EquationOperatorType, Button>();

    TextView textView_Equation;

    Button button_BackSpace;
    Button button_Evaluate;

    ArrayList<View> pressedButtonBuffer = new ArrayList<View>();
    Equation equation = new Equation(MAX_BRACKET_LEVEL);

    List<CardDealer.Card> showingCards;
    CardDealer dealer;
    int nSelectedCards = 0;

    Animation[] anim_Cards;

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
                Equation.EquationNode node = equation.pop();

                if (equation.isEmpty()) {
                    view.setEnabled(false);
                }

                if (node instanceof Equation.EquationOperand) {
                    View cardButton =
                            pressedButtonBuffer.get(pressedButtonBuffer.size() - 1);
                    cardButton.setEnabled(true);
                    --nSelectedCards;
                    button_Evaluate.setEnabled(false);
                }
                pressedButtonBuffer.remove(pressedButtonBuffer.size() - 1);

                refreshEquationView();
            }
        });
        button_BackSpace.setLongClickable(true);
        button_BackSpace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (equation.isEmpty()) {
                    return false;
                }

                equation.clear();
                pressedButtonBuffer.clear();

                for (ImageButton b : button_Cards) {
                    b.setEnabled(true);
                }

                nSelectedCards = 0;
                refreshEquationView();
                view.setEnabled(false);
                button_Evaluate.setEnabled(false);
                return true;
            }
        });

        anim_Cards = new Animation[] {
                AnimationUtils.loadAnimation(this, R.anim.activity_game_card_enter_ul),
                AnimationUtils.loadAnimation(this, R.anim.activity_game_card_enter_ur),
                AnimationUtils.loadAnimation(this, R.anim.activity_game_card_enter_bl),
                AnimationUtils.loadAnimation(this, R.anim.activity_game_card_enter_br)
            };

        anim_Cards[0].setStartOffset(100);
        anim_Cards[3].setStartOffset(200);
        anim_Cards[2].setStartOffset(300);

        // TODO: Set the translation animation deltaX, deltaY depending on the screen resolution

        button_Evaluate = (Button) findViewById(R.id.button_game_evaluate);
        button_Evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    double result = equation.evaluate();
                    String s = textView_Equation.getText().toString();
                    textView_Equation.setText(s + "=" + String.valueOf(result));
                } catch (Equation.MalformedEquationException e) {
                    Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        restart();
	}

    void restart() {
        showingCards = dealer.deal();

        for (int i = 0; i < showingCards.size(); ++i) {
            ImageButton curBtn = this.button_Cards[i];

            StateListDrawable states = new StateListDrawable();
            states.addState(new int[] {android.R.attr.state_enabled},
                    showingCards.get(i).getImageDrawable());
            states.addState(new int[] {},
                    getResources().getDrawable(R.drawable.card_back));
            if (Build.VERSION.SDK_INT >= 16) {
                curBtn.setBackground(states);
            } else {
                curBtn.setBackgroundDrawable(states); // For backward capability.
            }
        }

        Random random = new Random();
        for (int i = 0; i < button_Cards.length; ++i) {
            button_Cards[i].startAnimation(anim_Cards[i]);
            button_Cards[i].setEnabled(true);
            // Rotate random angle
            button_Cards[i].setRotation(random.nextFloat() * 360.0f);
        }

        textView_Equation.setText("");
        equation.clear();
        pressedButtonBuffer.clear();
        button_BackSpace.setEnabled(false);
        button_Evaluate.setEnabled(false);
        nSelectedCards = 0;
    }

    private void refreshEquationView() {
        textView_Equation.setText(equation.toString());
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

        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_game_refresh:
                restart();
                break;
            case R.id.menu_game_leader_board:
                Intent intent = new Intent(this, LeaderBoardActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        super.onOptionsItemSelected(item);
        return true;
    }

    class CardsOperatorClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            /*
            Each press will insert a `Equation.EquationNode` object into the array.
            Some of them may also disable some button.
             */
            try {
                switch (view.getId()) {
                    // Selecting cards, which will be translated into integers
                    case R.id.button_game_card_1: {
                        int number = showingCards.get(0).getNumber();
                        Equation.EquationOperand<Integer> op = new Equation.EquationOperand<Integer>(number);
                        equation.add(op);

                        ++nSelectedCards;

                        // Disable it
                        view.setEnabled(false);
                        break;
                    }
                    case R.id.button_game_card_2: {
                        int number = showingCards.get(1).getNumber();
                        Equation.EquationOperand<Integer> op = new Equation.EquationOperand<Integer>(number);
                        equation.add(op);

                        ++nSelectedCards;

                        // Disable it
                        view.setEnabled(false);
                        break;
                    }
                    case R.id.button_game_card_3: {
                        int number = showingCards.get(2).getNumber();
                        Equation.EquationOperand<Integer> op = new Equation.EquationOperand<Integer>(number);
                        equation.add(op);

                        ++nSelectedCards;

                        // Disable it
                        view.setEnabled(false);
                        break;
                    }
                    case R.id.button_game_card_4: {
                        int number = showingCards.get(3).getNumber();
                        Equation.EquationOperand<Integer> op = new Equation.EquationOperand<Integer>(number);
                        equation.add(op);

                        ++nSelectedCards;

                        // Disable it
                        view.setEnabled(false);
                        break;
                    }

                    // Selecting button_Operators
                    case R.id.button_game_op_plus:
                        equation.add(
                                new Equation.EquationOperator(
                                        Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_PLUS, 1));
                        break;
                    case R.id.button_game_op_minus:
                        equation.add(
                                new Equation.EquationOperator(
                                        Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MINUS, 1));
                        break;
                    case R.id.button_game_op_multiply:
                        equation.add(
                                new Equation.EquationOperator(
                                        Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_MULTIPLY, 2));
                        break;
                    case R.id.button_game_op_divide:
                        equation.add(
                                new Equation.EquationOperator(
                                        Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_DIVIDE, 2));
                        break;
                    case R.id.button_game_op_left_bracket:
                        equation.add(
                                new Equation.EquationOperator(
                                        Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_LEFT_BRACKET, 0));
                        break;
                    case R.id.button_game_op_right_bracket:
                        equation.add(
                                new Equation.EquationOperator(
                                        Equation.EquationOperatorType.EQUATION_OPERATOR_TYPE_RIGHT_BRACKET, 0));
                        break;
                }
            } catch (Equation.MalformedEquationException e) {
                Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Record the pressed button for re-enabling after backspace
            // It contains all buttons, including operators and cards
            // Just for convenience
            pressedButtonBuffer.add(view);

            // Enable the backspace button
            button_BackSpace.setEnabled(true);

            if (nSelectedCards == 4) {
                button_Evaluate.setEnabled(true);
            }

            refreshEquationView();
        }
    }
}
