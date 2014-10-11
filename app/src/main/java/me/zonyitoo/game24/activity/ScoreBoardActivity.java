package me.zonyitoo.game24.activity;

import java.util.ArrayList;
import java.util.Date;

import me.zonyitoo.game24.R;
import me.zonyitoo.game24.utils.GameDBHelper;
import me.zonyitoo.game24.utils.GameManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by zonyitoo on 14/10/9.
 */
public class ScoreBoardActivity extends ActionBarActivity {

    ListView list_Scores;

    TextView text_WinCount;
    TextView text_LoseCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score_board);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        list_Scores = (ListView) findViewById(R.id.list_score_board_scores);

        View headerView = LayoutInflater.from(this)
                .inflate(R.layout.listheader_score_board_statistic, null);

        text_WinCount = (TextView) headerView.findViewById(R.id.text_listheader_score_board_statistic_wins);
        text_LoseCount = (TextView) headerView.findViewById(R.id.text_listheader_score_board_statistic_loses);

        list_Scores.addHeaderView(headerView);

        GameDBHelper helper = new GameDBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor winCountCursor = db.rawQuery("SELECT COUNT(*) FROM "
                        + GameDBHelper.SCORE_TABLE_NAME
                        + " WHERE " + GameDBHelper.SCORE_COLUMN_RESULT + "=" + GameDBHelper.SCORE_RESULT_WON,
                null);
        winCountCursor.moveToFirst();
        int winCount = winCountCursor.getInt(0);
        winCountCursor.close();

        Cursor loseCountCursor = db.rawQuery("SELECT COUNT(*) FROM "
                + GameDBHelper.SCORE_TABLE_NAME
                + " WHERE " + GameDBHelper.SCORE_COLUMN_RESULT + "=" + GameDBHelper.SCORE_RESULT_LOST,
                null);
        loseCountCursor.moveToFirst();
        int loseCount = loseCountCursor.getInt(0);
        loseCountCursor.close();

        text_WinCount.setText(String.valueOf(winCount));
        text_LoseCount.setText(String.valueOf(loseCount));

        Cursor cursor = db.query(GameDBHelper.SCORE_TABLE_NAME,
                null, null, null, null, null, GameDBHelper.SCORE_COLUMN_START_TIME + " DESC", "10");

        ArrayList<GameManager.ScoreNode> scores = new ArrayList<GameManager.ScoreNode>();
        while (cursor.moveToNext()) {
            Date startTime = new Date(cursor.getLong(cursor.getColumnIndex(GameDBHelper.SCORE_COLUMN_START_TIME)));
            Date endTime = new Date(cursor.getLong(cursor.getColumnIndex(GameDBHelper.SCORE_COLUMN_END_TIME)));
            GameManager.GameResult result = GameDBHelper.intToScoreResult(
                    cursor.getInt(cursor.getColumnIndex(GameDBHelper.SCORE_COLUMN_RESULT)));

            scores.add(new GameManager.ScoreNode(startTime, endTime, result));
        }
        cursor.close();

        list_Scores.setAdapter(new ScoreListAdapter(this, scores));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ScoreListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<GameManager.ScoreNode> scores;

        public ScoreListAdapter(Context context, ArrayList<GameManager.ScoreNode> scores) {
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
                view = LayoutInflater.from(context).
                		inflate(R.layout.listitem_score_board_scores, list_Scores);
            }

            TextView text_Id = (TextView) view.findViewById(R.id.text_listitem_score_board_id);
            TextView text_Interval = (TextView) view.findViewById(R.id.text_listitem_score_board_interval);
            TextView text_Result = (TextView) view.findViewById(R.id.text_listitem_score_board_result);

            text_Id.setText(String.valueOf(i + 1));

            GameManager.ScoreNode node = this.scores.get(i);
            long interval = node.getInterval();

            text_Interval.setText(String.valueOf(interval / 1000) + "s");

            String result = null;
            if (node.getGameResult() == GameManager.GameResult.GAME_RESULT_LOST) {
                result = "✖";
            } else if (node.getGameResult() == GameManager.GameResult.GAME_RESULT_WON) {
                result = "✔";
            } else {
                result = "✖";
            }

            text_Result.setText(result);

            return view;
        }
    }

}
