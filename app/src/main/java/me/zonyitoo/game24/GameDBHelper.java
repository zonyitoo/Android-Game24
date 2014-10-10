package me.zonyitoo.game24;

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
    public static final String SCORE_COLUMN_RESULT = "result";

    public GameDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + SCORE_TABLE_NAME + "(" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                SCORE_COLUMN_START_TIME + " DATETIME NOT NULL," +
                                SCORE_COLUMN_END_TIME + " DATETIME NOT NULL," +
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
                return 1;
            case GAME_RESULT_LOST:
                return 2;
        }
        return 0;
    }

    public static GameManager.GameResult intToScoreResult(int i) {
        switch (i) {
            case 1:
                return GameManager.GameResult.GAME_RESULT_WON;
            case 2:
                return GameManager.GameResult.GAME_RESULT_LOST;
            default:
                return GameManager.GameResult.GAME_RESULT_UNKNOWN;
        }
    }

    public void addNewGameScore(GameManager.ScoreNode node) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + SCORE_TABLE_NAME + " VALUES (null,?,?,?)",
                new Object[] {node.getStartDate(), node.getEndDate(), scoreResultToInt(node.getGameResult())});
    }
}
