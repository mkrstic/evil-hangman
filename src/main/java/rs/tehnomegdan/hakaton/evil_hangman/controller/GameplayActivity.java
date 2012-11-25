package rs.tehnomegdan.hakaton.evil_hangman.controller;


import rs.tehnomegdan.hakaton.evil_hangman.R;
import rs.tehnomegdan.hakaton.evil_hangman.model.Game;
import rs.tehnomegdan.hakaton.evil_hangman.model.GameSettings;
import rs.tehnomegdan.hakaton.evil_hangman.util.GameOverException;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class GameplayActivity extends Activity {

	private String[][] keyboardLetters = {
			{ "Љ", "Њ", "Е", "Р", "Т", "З", "У", "И", "О", "П", "Ш", "Ђ" },
			{ "А", "С", "Д", "ф", "Г", "Х", "Ј", "К", "Л", "Ч", "Ћ" },
			{ "Ж", "Џ", "Ц", "В", "Б", "Н", "М" } };
	private TextView lettersView;
	private ProgressBar attemptsBar;
	private Game game;
	private View.OnClickListener letterClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Button button = (Button) v;
			try {
				game.play(button.getText().toString());
				button.setEnabled(false);
				button.setTextColor(Color.BLACK);
				if (attemptsBar.getProgress() > game.getRemainingAttempts()) {
					attemptsBar.setProgress(attemptsBar.getProgress() - 1);
					if (game.getRemainingAttempts() == 0) {
						Toast.makeText(GameplayActivity.this,
								"Игра је завршена. Реч је: " + game.revealWord(), Toast.LENGTH_SHORT).show();
						showResult();
					}
				} else if (game.isGameOver()) {
					Toast.makeText(GameplayActivity.this, "Честитамо!", Toast.LENGTH_LONG).show();
					showResult();
				}
				lettersView.setText(addLetterSpacing(game.getCurrentWordMask()));
			} catch (GameOverException ex) {
				showResult();
				//Toast.makeText(GameplayActivity.this, "", Toast.LENGTH_SHORT).show();
			}
		}
	};
	public void showResult() {
		lettersView.setText(addLetterSpacing(game.revealWord()));
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		}, 5000);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGame();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Toast.makeText(this, "New orienttaion: " + newConfig.orientation,
				Toast.LENGTH_SHORT).show();
	}

	
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		Intent intent = getIntent();
		finish();
		startActivity(intent);
		return true;
	}

	private void initGame() {
		setContentView(R.layout.main);
		GameSettings settings = new GameSettings(GameplayActivity.this);
		int wordLen = settings.findWordLength();
		int maxAttempts = settings.findMaxAttempts();
		game = new Game(GameplayActivity.this, wordLen, maxAttempts);
		lettersView = (TextView) findViewById(R.id.lettersView);
		lettersView.setText(addLetterSpacing(game.getCurrentWordMask()));
		attemptsBar = (ProgressBar) findViewById(R.id.attemptsBar);
		attemptsBar.setMax(maxAttempts);
		attemptsBar.setProgress(maxAttempts);
		TableLayout keyboardTable = (TableLayout) findViewById(R.id.keyboardTable);
		for (String[] row : keyboardLetters) {
			TableRow tableRow = (TableRow) getLayoutInflater().inflate(
					R.layout.partial_table_row, null);
			for (int i = 0; i < row.length; i++) {
				Button letter = new Button(GameplayActivity.this, null,
						R.style.letter);
				letter.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.black_button));
				letter.setText(row[i]);
				letter.setOnClickListener(letterClickListener);
				tableRow.addView(letter);
			}
			keyboardTable.addView(tableRow);
		}
	}
	private String addLetterSpacing(String str) {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < str.length(); i++) {
			sb.append(str.charAt(i)).append(" ");
		}
		return sb.toString();
	}
}
