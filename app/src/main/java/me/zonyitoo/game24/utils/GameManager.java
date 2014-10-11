package me.zonyitoo.game24.utils;

import android.content.Context;

import java.util.Date;

/**
 * Just a simple class for remembering the user's score. <br/>
 *
 * Asking for some bonus mark. LOL
 *
 * Created by zonyitoo on 14/10/10.
 */
public class GameManager {

    public enum GameResult {
        GAME_RESULT_UNKNOWN,
        GAME_RESULT_WON,
        GAME_RESULT_LOST
    }

    public static class ScoreNode {
        private Date startDate;
        private Date endDate;
        private GameResult result;

        public ScoreNode(Date startDate) {
            this.startDate = startDate;
            this.endDate = null;
            this.result = GameResult.GAME_RESULT_UNKNOWN;
        }

        public ScoreNode(Date startDate, Date endDate, GameResult result) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.result = result;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public GameResult getGameResult() {
            return result;
        }

        public long getInterval() {
            return endDate.getTime() - startDate.getTime();
        }
    }

    private ScoreNode currentNode = null;
    private int nWonGame = 0;
    private int nLostGame = 0;

    private GameDBHelper gameDBHelper;

    public GameManager(Context context) {
        this.gameDBHelper = new GameDBHelper(context);
    }

    private void addScoreIntoDB(ScoreNode node) {
        this.gameDBHelper.addNewGameScore(node);
    }

    public void startGame() {
        /*
        if (currentNode != null) {
            currentNode.endDate = new Date();
            currentNode.result = GameResult.GAME_RESULT_LOST;
            addScoreIntoDB(currentNode);
        }*/

        currentNode = new ScoreNode(new Date());
    }

    public void endGame(GameResult result) {
        if (currentNode == null) {
            return;
        }

        switch (result) {
            case GAME_RESULT_WON:
                nWonGame++;
                break;
            case GAME_RESULT_LOST:
                nLostGame++;
                break;
			default:
				break;
        }

        currentNode.endDate = new Date();
        currentNode.result = result;
        addScoreIntoDB(currentNode);

        currentNode = null;
    }

    public int getNumberOfWonGame() {
        return nWonGame;
    }

    public int getNumberOfLostGame() {
        return nLostGame;
    }

}
