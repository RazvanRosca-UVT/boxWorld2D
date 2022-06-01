package com.sonic19260.boxworld2d;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.sonic19260.boxworld2d.engine.Game;

public class MainActivity extends AppCompatActivity {

    public static String username = "Guest";
    private Button newGameBtn;
    private Button viewHighscoresBtn;
    private EditText usernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // No status and title bar
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        newGameBtn = (Button) findViewById(R.id.startGameButton);
        viewHighscoresBtn = (Button) findViewById(R.id.viewHighscoresButton);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGame();
            }
        });

        viewHighscoresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHighscoresMenu();
            }
        });
    }

    private void createNewGame() {
        if (!usernameEditText.getText().toString().isEmpty()) {
            username = usernameEditText.getText().toString();
        }

        // Set content view to Game, so that we may draw the entities and buttons.
        Intent switchActivityIntent = new Intent(this, GameActivity.class);
        startActivity(switchActivityIntent);
    }

    private void goToHighscoresMenu() {
        Intent switchActivityIntent = new Intent(this, HighscoreActivity.class);
        startActivity(switchActivityIntent);
    }
}