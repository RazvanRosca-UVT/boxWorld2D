package com.sonic19260.boxworld2d.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BoxWorld2DHighscores.db";
    public static final String TABLE_NAME = "highscores_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "USERNAME";
    public static final String COL_3 = "SCORE";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, SCORE INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, score);
        long result = db.insert(TABLE_NAME, null, contentValues);

        db.close();
        return result != -1;
    }

    public List<ScoreModel> getTop10Scores() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_3 + " DESC LIMIT 10", null);

        ArrayList<ScoreModel> top10Scores = new ArrayList<ScoreModel>();

        while (cursor.moveToNext()) {
            int scoreID = cursor.getInt(0);
            String username = cursor.getString(1);
            int score = cursor.getInt(2);

            ScoreModel scoreModel = new ScoreModel(scoreID, username, score);
            top10Scores.add(scoreModel);
        }

        cursor.close();
        db.close();
        return top10Scores;
    }
}
