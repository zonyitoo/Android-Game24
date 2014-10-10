package me.zonyitoo.game24;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zonyitoo on 14/10/10.
 */
public class GameDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "game24.db";
    private static final int DATABASE_VERSION = 1;

    public static final String SCORE_TABLE_NAME = "game24_score";
    public static final String SCORE_COLUMN_START_TIME = "start_time";
    public static final String SCORE_COLUMN_END_TIME = "end_time";
    public static final String SCORE_COLUMN_INTERVAL = "interval";
    public static final String SCORE_COLUMN_RESULT = "result";

    public static final int SCORE_RESULT_UNKNOWN = 0;
    public static final int SCORE_RESULT_WON = 1;
    public static final int SCORE_RESULT_LOST = 2;

    public GameDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + SCORE_TABLE_NAME + "(" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                SCORE_COLUMN_START_TIME + " DATETIME NOT NULL," +
                                SCORE_COLUMN_END_TIME + " DATETIME NOT NULL," +
                                SCORE_COLUMN_INTERVAL + " INTEGER NOT NULL," +
                                SCORE_COLUMN_RESULT + " INTEGER NOT NULL" +
                                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE " + SCORE_TABLE_NAME + " IF EXISTS");
    }

    public static int scoreResultToInt(GameManager.GameResult result) {
        switch (result) {
            case GAME_RESULT_WON:
                return SCORE_RESULT_WON;
            case GAME_RESULT_LOST:
                return SCORE_RESULT_LOST;
        }
        return SCORE_RESULT_UNKNOWN;
    }

    public static GameManager.GameResult intToScoreResult(int i) {
        switch (i) {
            case SCORE_RESULT_WON:
                return GameManager.GameResult.GAME_RESULT_WON;
            case SCORE_RESULT_LOST:
                return GameManager.GameResult.GAME_RESULT_LOST;
            default:
                return GameManager.GameResult.GAME_RESULT_UNKNOWN;
        }
    }

    public void addNewGameScore(GameManager.ScoreNode node) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GameDBHelper.SCORE_COLUMN_START_TIME, node.getStartDate().getTime());
        values.put(GameDBHelper.SCORE_COLUMN_END_TIME, node.getEndDate().getTime());
        values.put(GameDBHelper.SCORE_COLUMN_RESULT, scoreResultToInt(node.getGameResult()));
        values.put(GameDBHelper.SCORE_COLUMN_INTERVAL, node.getInterval());

        db.insert(GameDBHelper.SCORE_TABLE_NAME, null, values);

        db.close();
    }
}
