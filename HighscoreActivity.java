package com.sonic19260.boxworld2d;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sonic19260.boxworld2d.database.DatabaseHelper;
import com.sonic19260.boxworld2d.database.ScoreModel;
import com.sonic19260.boxworld2d.engine.Game;

import java.util.ArrayList;
import java.util.List;

public class HighscoreActivity extends AppCompatActivity {

    private Button goBackBtn;
    private ArrayList<TextView> top10ScoresText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        // No status and title bar
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        goBackBtn = (Button) findViewById(R.id.goBackButton);

        top10ScoresText = new ArrayList<TextView>();
        top10ScoresText.add((TextView) findViewById(R.id.highscore1Text));
        top10ScoresText.add((TextView) findViewById(R.id.highscore2Text));
        top10ScoresText.add((TextView) findViewById(R.id.highscore3Text));
        top10ScoresText.add((TextView) findViewById(R.id.highscore4Text));
        top10ScoresText.add((TextView) findViewById(R.id.highscore5Text));
        top10ScoresText.add((TextView) findViewById(R.id.highscore6Text));
        top10ScoresText.add((TextView) findViewById(R.id.highscore7Text));
        top10ScoresText.add((TextView) findViewById(R.id.highscore8Text));
        top10ScoresText.add((TextView) findViewById(R.id.highscore9Text));
        top10ScoresText.add((TextView) findViewById(R.id.highscore10Text));

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dbHelper = new DatabaseHelper(this);
        ArrayList<ScoreModel> top10Scores = (ArrayList) dbHelper.getTop10Scores();

        for (int i = 0; i < top10Scores.size(); i++) {
            top10ScoresText.get(i).setText(Integer.toString(i + 1) + ". " + top10Scores.get(i).COL_USERNAME + ": " + top10Scores.get(i).COL_SCORE);
        }
    }
}
