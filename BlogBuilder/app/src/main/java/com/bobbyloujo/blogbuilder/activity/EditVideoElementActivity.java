/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Benjamin Blaszczak
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.bobbyloujo.blogbuilder.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bobbyloujo.blogbuilder.R;
import com.bobbyloujo.blogbuilder.application.PathUtil;
import com.bobbyloujo.blogbuilder.application.PostLoader;
import com.bobbyloujo.blogbuilder.post.Post;
import com.bobbyloujo.blogbuilder.post.VideoElement;

/**
 * An Activity for editing a VideoElement.
 */
public class EditVideoElementActivity extends AppCompatActivity {
	public static final String ELEMENT_INDEX_EXTRA = "video element index";  // The name of the extra used to pass through the local of the VideoElement in the ElementList.
	private static final String WEB_PATH_START = "http://";                  // The beginning of a web path. Will be added if the user leaves it off.
	private static final int GET_LOCAL_VIDEO = 0;                            // Activity request code for opening the file system browser.
	private static final int REQUEST_READ_EXTERNAL_STORAGE_CODE = 0;         // Request code for requesting external storage read permission on Android Marshmallow

	private Post post;                 // The Post that the VideoElement belongs to.
	private VideoElement videoElement; // The VideoElement to edit.

	private String videoPath;            // The path to the video file

	private RadioButton localButton;     // The local storage radio button
	private RadioButton internetButton;  // The internet radio button
	private TextView urlTextView;        // The TextView that displays the path of the local video
	private EditText pathEditText;       // The text field for web url entry.
	private Button browseButton;         // The button to browse the file system.
	private Button saveButton;           // The button to save changes
	private Button cancelButton;         // The button to cancel changes and exit the activity.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_video_element);
		int index = getIntent().getIntExtra(ELEMENT_INDEX_EXTRA, -1);

		post = PostLoader.getInstance().getTempPost();                               // Get the post

		if (index != -1) {                                                           // Editing an existing element
			videoElement = (VideoElement) post.getElementList().getElement(index);   // So get the element from the ElementList
		} else {                                                                     // Adding a new element
			videoElement = new VideoElement();                                       // So initialize the new element.
		}

		localButton = (RadioButton) findViewById(R.id.local);
		internetButton = (RadioButton) findViewById(R.id.internet);
		urlTextView = (TextView) findViewById(R.id.srcTextView);
		pathEditText = (EditText) findViewById(R.id.pathEditText);
		browseButton = (Button) findViewById(R.id.browseButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);

		localButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onLocalButtonPressed();
			}
		});

		internetButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onInternetButtonPressed();
			}
		});

		browseButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onBrowseButtonPressed();
			}
		});

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

		if (videoElement != null) {
			if (videoElement.getLocation() == VideoElement.LOCAL) {                            // Show the appropriate views for a local video
				localButton.setChecked(true);
				browseButton.setVisibility(View.VISIBLE);
				urlTextView.setVisibility(View.GONE);
				pathEditText.setVisibility(View.GONE);
				findViewById(R.id.pathTextView).setVisibility(View.VISIBLE);
			} else if (videoElement.getLocation() == VideoElement.INTERNET) {                  // Show the appropriate views for a video from the internet
				internetButton.setChecked(true);
				browseButton.setVisibility(View.GONE);
				urlTextView.setVisibility(View.VISIBLE);
				findViewById(R.id.pathTextView).setVisibility(View.GONE);
				pathEditText.setVisibility(View.VISIBLE);

				pathEditText.setText(videoElement.getSrcURL());
			}
		}
	}

	/**
	 * Starts the browse file system activity.
	 */
	private void startBrowseActivity() {
		Intent videoBrowser = new Intent();
		videoBrowser.setType("video/*");
		videoBrowser.setAction(Intent.ACTION_GET_CONTENT);
		videoBrowser.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		startActivityForResult(Intent.createChooser(videoBrowser, "Select Video"), GET_LOCAL_VIDEO);
	}

	/**
	 * The local storage radio button was pressed.
	 */
	private void onLocalButtonPressed() {
		browseButton.setVisibility(View.VISIBLE);
		urlTextView.setVisibility(View.GONE);
		pathEditText.setVisibility(View.GONE);
		findViewById(R.id.pathTextView).setVisibility(View.VISIBLE);
	}

	/**
	 * The internet radio button was pressed.
	 */
	private void onInternetButtonPressed() {
		browseButton.setVisibility(View.GONE);
		urlTextView.setVisibility(View.VISIBLE);
		pathEditText.setVisibility(View.VISIBLE);
		findViewById(R.id.pathTextView).setVisibility(View.GONE);
	}

	/**
	 * The "Browse" button was pressed.
	 */
	private void onBrowseButtonPressed() {
		// Get permissions if on Marshmallow. The start browse activity.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_CODE);
			} else {
				startBrowseActivity();
			}
		} else {
			startBrowseActivity();
		}
	}

	/**
	 * The "Save" button was pressed.
	 */
	private void onSaveButtonPressed() {
		if (videoElement != null) {
			if (localButton.isChecked() && videoPath != null) {
				videoElement.setLocation(VideoElement.LOCAL);
			} else if (internetButton.isChecked()) {
				videoPath = pathEditText.getText().toString();
				videoElement.setLocation(VideoElement.INTERNET);

				// Add http:// if the user left it out.
				if (videoPath.length() > WEB_PATH_START.length() && !videoPath.substring(0, WEB_PATH_START.length()).equals(WEB_PATH_START)) {
					videoPath = WEB_PATH_START + videoPath;
				}
			}

			if (videoPath != null) {
				videoElement.setSrcURL(videoPath);
			}

			if (!post.getElementList().contains(videoElement)) {
				post.getElementList().addElement(videoElement);
			}
		}

		PostLoader.getInstance().saveTempPost(post);

		finish();
	}

	/**
	 * The "Cancel" button was pressed.
	 */
	private void onCancelButtonPressed() {
		finish();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_READ_EXTERNAL_STORAGE_CODE: {  // Requested external read permission.
				// If request is cancelled, the result arrays are empty.
				if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
					Toast.makeText(this, "Read permission denied. Can't browse file system.", Toast.LENGTH_LONG).show();
				} else {
					startBrowseActivity();
				}
				break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case GET_LOCAL_VIDEO:  // Browse file system result.
				if (data != null && resultCode == RESULT_OK) {
					videoPath = PathUtil.getPath(this, data.getData());
					((TextView) findViewById(R.id.pathTextView)).setText(videoPath);
					findViewById(R.id.pathTextView).setVisibility(View.VISIBLE);
				}
				break;
		}
	}
}
