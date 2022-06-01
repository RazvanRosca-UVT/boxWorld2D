package com.sonic19260.boxworld2d.database;

public class ScoreModel {
    public final int COL_ID;
    public final String COL_USERNAME;
    public final int COL_SCORE;

    public ScoreModel(int COL_ID, String COL_USERNAME, int COL_SCORE) {
        this.COL_ID = COL_ID;
        this.COL_USERNAME = COL_USERNAME;
        this.COL_SCORE = COL_SCORE;
    }
}
