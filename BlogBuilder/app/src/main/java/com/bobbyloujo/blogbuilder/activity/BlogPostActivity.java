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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bobbyloujo.blogbuilder.R;
import com.bobbyloujo.blogbuilder.application.ActivityStarter;
import com.bobbyloujo.blogbuilder.application.PostLoader;
import com.bobbyloujo.blogbuilder.post.Post;
import com.bobbyloujo.blogbuilder.view.PostView;

/**
 * An Activity that displays the contents of a Post. From the options
 * menu the user can choose to edit or delete the Post.
 *
 * Created by Benjamin.
 */
public class BlogPostActivity extends AppCompatActivity {
	public static final String POST_EXTRA = "POST";                      // Name of the extra retrieved from the intent containing the filename of the Post.

	private static final int REQUEST_READ_EXTERNAL_STORAGE_CODE = 0;     // Request code for getting external storage read permission on Android Marshmallow.

	private PostLoader postLoader;       // Reference to the PostLoader for loading, saving, and deleting posts.
	private String postFileName;         // The filename of the Post being displayed by this Activity.
	private Post post;                   // The Post being displayed by this Activity.
	private PostView postView;           // Modified WebView for displaying Post content.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		postView = (PostView) findViewById(R.id.postcontent);

		postLoader = PostLoader.getInstance();
		postFileName = getIntent().getStringExtra(POST_EXTRA);

		/* Get read permission if on Marshmallow or greater. Then load the post. */
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
	public void onResume() {
		super.onResume();
		loadPost();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blog_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit) {                                   // The Edit button was pressed.
			ActivityStarter.startEditPostActivity(this, post);   // Start an Activity to edit the post.
            return true;
        }
        else if (id == R.id.delete) {                            // The delete button was pressed.
			postLoader.deletePost(post);                         // Delete the post
			finish();                                            // and close this Activity.
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onBackPressed() {
		PostView view = (PostView) findViewById(R.id.postcontent);

		if (view.canGoBack()) {         // If a link was followed in the PostView and thus the PostView has history
			view.goBack();              // Go back in the PostView instead of closing this Activity.
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_READ_EXTERNAL_STORAGE_CODE: {
				// If request is cancelled, the result arrays are empty.
				if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
					Toast.makeText(this, "Read permission denied. Local resources (images, video) will not show correctly.", Toast.LENGTH_LONG).show();
				}

				loadPost();
				break;
			}
		}
	}

	/**
	 * Loads the post and displays it.
	 */
	private void loadPost() {
		post = postLoader.loadPost(postFileName);

		setTitle(post.getTitle());
		postView.setPost(post);
		postView.load();
	}
}
