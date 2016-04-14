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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bobbyloujo.blogbuilder.R;
import com.bobbyloujo.blogbuilder.application.ActivityStarter;
import com.bobbyloujo.blogbuilder.application.PostLoader;
import com.bobbyloujo.blogbuilder.post.Post;
import com.bobbyloujo.blogbuilder.view.ElementModuleListAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * An Activity for editing a Post. Allows post elements to be rearranged and
 * edited by launching their respective Activities.
 */
public class EditPostActivity extends AppCompatActivity {
	private static final int REQUEST_READ_EXTERNAL_STORAGE_CODE = 0; // Request code for requesting external storage read permissions

	private PostLoader postLoader;                  // Reference to PostLoader for loading and saving the Post.
	private Post post;                              // The Post to be edited.
	private ElementModuleListAdapter listAdapter;   // The ListAdapter for the ListView

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_post);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		postLoader = PostLoader.getInstance();
		post = postLoader.getTempPost();

		((TextView) findViewById(R.id.titleEditText)).setText(post.getTitle());

		FloatingActionButton addTextFab = (FloatingActionButton) findViewById(R.id.add_paragraph);
		addTextFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onAddTextPressed();
			}
		});

		FloatingActionButton addImageFab = (FloatingActionButton) findViewById(R.id.add_image);
		addImageFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onAddImagePressed();
			}
		});

		FloatingActionButton addVideoFab = (FloatingActionButton) findViewById(R.id.add_video);
		addVideoFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onAddVideoPressed();
			}
		});

		findViewById(R.id.savePostButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onSavePostButtonPressed();
			}
		});

		findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onCancelButtonPressed();
			}
		});

		// Get read permission if on Android Marshmallow. Then load the post.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_CODE);
			} else {
				loadPost();
			}
		} else {
			loadPost();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		FloatingActionsMenu fam = (FloatingActionsMenu) findViewById(R.id.add_element_fab);
		fam.collapse();
	}

	@Override
	public void onResume() {
		super.onResume();
		loadPost();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_READ_EXTERNAL_STORAGE_CODE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					loadPost();
				} else {
					Toast.makeText(this, "Read permission denied. Local resources (images, videos) will not show correctly.", Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	}

	/**
	 * Loads the post and updates the ListView
	 */
	private void loadPost() {
		post = postLoader.getTempPost();

		if (listAdapter == null) {
			listAdapter = new ElementModuleListAdapter(getApplicationContext(), post.getElementList());
			((ListView) findViewById(R.id.elementListView)).setAdapter(listAdapter);
		} else {
			listAdapter.setElementList(post.getElementList());
			listAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Save button pressed. Saves the post and closes this Activity.
	 */
	private void onSavePostButtonPressed() {
		EditText title = (EditText) findViewById(R.id.titleEditText);

		post.setTitle(title.getText().toString());

		postLoader.savePost(post);
		finish();
	}

	/**
	 * Cancel button pressed. Closes this Activity without saving.
	 */
	private void onCancelButtonPressed() {
		finish();
	}

	/**
	 * Add Text floating action button pressed. Start an Activity to edit and add a new TextElement.
	 */
	private void onAddTextPressed() {
		ActivityStarter.startEditTextElementActivity(this, post, null);
	}

	/**
	 * Add Image floating action button pressed. Start an Activity to edit and add a new ImageElement.
	 */
	private void onAddImagePressed() {
		ActivityStarter.startEditImageElementActivity(this, post, null);
	}

	/**
	 * Add Video floating action button pressed. Start an Activity to edit and add a new VideoElement.
	 */
	private void onAddVideoPressed() {
		ActivityStarter.startEditVideoElementActivity(this, post, null);
	}
}
