package me.zonyitoo.game24;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zonyitoo on 14/10/9.
 */
public class ScoreBoardActivity extends ActionBarActivity {

    ListView list_Scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score_board);

        list_Scores = (ListView) findViewById(R.id.list_score_board_scores);

        GameDBHelper helper = new GameDBHelper(this);
        Cursor cursor = helper.getReadableDatabase().query(GameDBHelper.SCORE_TABLE_NAME,
                null, null, null, null, null, GameDBHelper.SCORE_COLUMN_START_TIME, null);

        ArrayList<GameManager.ScoreNode> scores = new ArrayList<GameManager.ScoreNode>();
        while (cursor.moveToNext()) {
            Date startTime = new Date(cursor.getLong(cursor.getColumnIndex(GameDBHelper.SCORE_COLUMN_START_TIME)));
            Date endTime = new Date(cursor.getLong(cursor.getColumnIndex(GameDBHelper.SCORE_COLUMN_END_TIME)));
            GameManager.GameResult result = GameDBHelper.intToScoreResult(
                    cursor.getInt(cursor.getColumnIndex(GameDBHelper.SCORE_COLUMN_RESULT)));

            scores.add(new GameManager.ScoreNode(startTime, endTime, result));
        }

        list_Scores.setAdapter(new ScoreListAdapter(this, scores));
    }

    class ScoreListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<GameManager.ScoreNode> scores;

        ScoreListAdapter(Context context, ArrayList<GameManager.ScoreNode> scores) {
            this.context = context;
            this.scores = scores;
        }

        @Override
        public int getCount() {
            return scores.size();
        }

        @Override
        public Object getItem(int i) {
            return scores.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.listitem_score_board_scores, null);
            }

            TextView text_Interval = (TextView) view.findViewById(R.id.text_listitem_score_board_interval);
            TextView text_Result = (TextView) view.findViewById(R.id.text_listitem_score_board_result);

            GameManager.ScoreNode node = this.scores.get(i);
            text_Interval.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(node.getInterval()));

            String result = null;
            if (node.getGameResult() == GameManager.GameResult.GAME_RESULT_LOST) {
                result = "LOST";
            } else if (node.getGameResult() == GameManager.GameResult.GAME_RESULT_WON) {
                result = "WON";
            } else {
                result = "UNKNOWN";
            }

            text_Result.setText(result);

            return view;
        }
    }

}
