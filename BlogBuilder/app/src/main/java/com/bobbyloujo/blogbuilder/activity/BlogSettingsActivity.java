package com.bobbyloujo.blogbuilder.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bobbyloujo.blogbuilder.R;

/**
 *
 */
public class BlogSettingsActivity extends AppCompatActivity {

	public static final String PREFERENCES = "preferences";   // Name of the shared preferences
	public static final String BLOG_TITLE = "blog title";     // Key for retrieving and saving the blog title to the shared preferences

	private EditText titleText;   // Text field for editing the title
	private Button saveButton;    // Save button for saving the new title and exiting the settings.
	private Button cancelButton;  // Cancel button for exiting the activity without saving changes.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_settings);
		titleText = (EditText) findViewById(R.id.editText);
		saveButton = (Button) findViewById(R.id.saveButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);

		String currentTitle = getSharedPreferences(PREFERENCES, MODE_PRIVATE).getString(BLOG_TITLE, "");
		titleText.setText(currentTitle);

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onSaveButtonPressed();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onCancelButtonPressed();
			}
		});
	}

	private void onSaveButtonPressed() {
		SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit();
		editor.putString(BLOG_TITLE, titleText.getText().toString());
		editor.apply();
		finish();
	}

	private void onCancelButtonPressed() {
		finish();
	}
}
