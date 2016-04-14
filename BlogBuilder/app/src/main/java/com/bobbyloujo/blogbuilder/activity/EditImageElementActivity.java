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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bobbyloujo.blogbuilder.R;
import com.bobbyloujo.blogbuilder.application.PathUtil;
import com.bobbyloujo.blogbuilder.application.PostLoader;
import com.bobbyloujo.blogbuilder.post.ImageElement;
import com.bobbyloujo.blogbuilder.post.Post;

import java.io.File;

/**
 * An Activity for editing an ImageElement.
 */
public class EditImageElementActivity extends AppCompatActivity {
	public static final String ELEMENT_INDEX_EXTRA = "image element index extra"; // Name of the extra used to pass the location of the ImageElement in the Post's ElementList

	private static final int GET_LOCAL_IMAGE = 0;                         // Activity request code for getting a local image
	private static final int CAPTURE_IMAGE = 1;                           // Activity request code for capturing an image with the camera

	private static final String WEB_PATH_START = "http://";               // The beginning of a web path. Will be added in if left out by the user.
	private static final String CAPTURED_IMAGE_DIR = "Pictures";          // The directory that new images are saved to.

	private static final int REQUEST_READ_EXTERNAL_STORAGE_CODE = 0;      // Request code for requesting external storage read permission on Android Marshmallow

	private Post post;                  // The Post that the ImageElement belongs to.
	private ImageElement imageElement;  // The ImageElement being edited.

	private String imagePath;           // The path of the captured image.

	private RadioButton localButton;    // The local storage radio button
	private RadioButton internetButton; // The internet radio button
	private TextView urlTextView;       // The TextView that labels the path text field
	private EditText pathEditText;      // The text field used to enter the path to the image
	private Button browseButton;        // The browse file system button
	private Button takeButton;          // The take new picture button
	private Button saveButton;          // The save button
	private Button cancelButton;        // The cancel button
	private ImageView imageView;        // The image view for displaying local images

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_image_element);
		int index = getIntent().getIntExtra(ELEMENT_INDEX_EXTRA, -1);

		post = PostLoader.getInstance().getTempPost();

		if (index != -1) {
			imageElement = (ImageElement) post.getElementList().getElement(index);
		} else {
			imageElement = new ImageElement();
		}

		localButton = (RadioButton) findViewById(R.id.local);
		internetButton = (RadioButton) findViewById(R.id.internet);
		urlTextView = (TextView) findViewById(R.id.srcTextView);
		pathEditText = (EditText) findViewById(R.id.pathEditText);
		browseButton = (Button) findViewById(R.id.browseButton);
		takeButton = (Button) findViewById(R.id.takeButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		imageView = (ImageView) findViewById(R.id.imageView);

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

		takeButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onTakeButtonPressed();
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

		if (imageElement != null) {
			if (imageElement.getLocation() == ImageElement.LOCAL) {  // Show and hide the correct views for a local image
				localButton.setChecked(true);
				browseButton.setVisibility(View.VISIBLE);
				takeButton.setVisibility(View.VISIBLE);
				urlTextView.setVisibility(View.GONE);
				pathEditText.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);

				if (imageElement.getSrc() != null) {
					imageView.setImageBitmap(BitmapFactory.decodeFile(imageElement.getSrc()));
				}
			} else if (imageElement.getLocation() == ImageElement.INTERNET) {  // Show and hide the correct views for a web image
				internetButton.setChecked(true);
				browseButton.setVisibility(View.GONE);
				takeButton.setVisibility(View.GONE);
				urlTextView.setVisibility(View.VISIBLE);
				pathEditText.setVisibility(View.VISIBLE);
				imageView.setVisibility(View.GONE);

				pathEditText.setText(imageElement.getSrc());
			}
		}
	}

	/**
	 * Starts the file browser activity.
	 */
	private void startBrowseActivity() {
		Intent imageBrowser = new Intent();
		imageBrowser.setType("image/*");
		imageBrowser.setAction(Intent.ACTION_GET_CONTENT);
		imageBrowser.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		startActivityForResult(Intent.createChooser(imageBrowser, "Select Picture"), GET_LOCAL_IMAGE);
	}

	/**
	 * Local storage radio button pressed. Show and hide the correct views for a local image.
	 */
	private void onLocalButtonPressed() {
		browseButton.setVisibility(View.VISIBLE);
		takeButton.setVisibility(View.VISIBLE);
		urlTextView.setVisibility(View.GONE);
		pathEditText.setVisibility(View.GONE);
		imageView.setVisibility(View.VISIBLE);
	}

	/**
	 * Internet radio button pressed. Show and hide the correct views for a web image.
	 */
	private void onInternetButtonPressed() {
		browseButton.setVisibility(View.GONE);
		takeButton.setVisibility(View.GONE);
		urlTextView.setVisibility(View.VISIBLE);
		pathEditText.setVisibility(View.VISIBLE);
		imageView.setVisibility(View.GONE);
	}

	/**
	 * The browse file system button was pressed. Tries to start the browse file system activity
	 * but will request permission first if on Android Marshmallow or higher.
	 */
	private void onBrowseButtonPressed() {
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
	 * The take picture button was pressed. Starts the Activity to take a new picture.
	 */
	private void onTakeButtonPressed() {
		Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imagePath = CAPTURED_IMAGE_DIR + File.separator + "capture" + Long.toString(SystemClock.currentThreadTimeMillis()) + ".jpg";
		Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imagePath));
		imagePath = Environment.getExternalStorageDirectory() + File.separator + imagePath;
		captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(captureImage, CAPTURE_IMAGE);
	}

	/**
	 * Save button pressed. Save changes and close Activity.
	 */
	private void onSaveButtonPressed() {
		if (imageElement != null) {
			if (localButton.isChecked() && imagePath != null) {
				imageElement.setLocation(ImageElement.LOCAL);
			} else if (internetButton.isChecked()) {
				imagePath = pathEditText.getText().toString();
				imageElement.setLocation(ImageElement.INTERNET);

				if (imagePath.length() > WEB_PATH_START.length() && !imagePath.substring(0, WEB_PATH_START.length()).equals(WEB_PATH_START)) {
					imagePath = WEB_PATH_START + imagePath;
				}
			}

			if (imagePath != null) {
				imageElement.setSrcURL(imagePath);
			}

			if (!post.getElementList().contains(imageElement)) {
				post.getElementList().addElement(imageElement);
			}
		}

		PostLoader.getInstance().saveTempPost(post);

		finish();
	}

	/**
	 * Cancel button pressed. Just close the Activity.
	 */
	private void onCancelButtonPressed() {
		finish();
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_READ_EXTERNAL_STORAGE_CODE: {
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
			case GET_LOCAL_IMAGE:                                   // Browse file system activity finished
				if (data != null && resultCode == RESULT_OK) {
					imagePath = PathUtil.getPath(this, data.getData());
					imageView.setImageURI(data.getData());
				}
				break;
			case CAPTURE_IMAGE:                                // A new image may have been taken.
				if (resultCode == RESULT_OK) {                 // Change the path to the new image if a new image was taken
					imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
				} else {
					imagePath = null;
					Toast.makeText(this, "Failed to take picture.", Toast.LENGTH_LONG).show();
				}
				break;
		}
	}
}