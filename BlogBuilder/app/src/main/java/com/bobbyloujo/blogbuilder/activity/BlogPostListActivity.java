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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bobbyloujo.blogbuilder.application.ActivityStarter;
import com.bobbyloujo.blogbuilder.post.Post;
import com.bobbyloujo.blogbuilder.R;
import com.bobbyloujo.blogbuilder.view.PostSummaryListAdapter;

/**
 * An Activity that displays a list of the saved blog Posts.
 */
public class BlogPostListActivity extends AppCompatActivity {

    private PostSummaryListAdapter postSummaryListAdapter;  // The adapter for the ListView.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFABPressed();
            }
        });

        ListView summaryList = (ListView) findViewById(R.id.summarylistview);
        postSummaryListAdapter = new PostSummaryListAdapter(this);
        summaryList.setAdapter(postSummaryListAdapter);
    }

	@Override
	public void onResume() {
		super.onResume();
		postSummaryListAdapter.notifyDataSetChanged();
		setTitle(getSharedPreferences(BlogSettingsActivity.PREFERENCES, MODE_PRIVATE).getString(BlogSettingsActivity.BLOG_TITLE, getResources().getString(R.string.defBlogTitle)));
	}

	@Override
	public void onPause() {
		super.onPause();
		postSummaryListAdapter.savePostList();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blog_post_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
			Intent settings = new Intent(this, BlogSettingsActivity.class);
			startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	/**
	 * The Floating Action Button was pressed. Create a new Post!
	 */
    private void onFABPressed() {
		Post post = new Post();
		ActivityStarter.startEditPostActivity(this, post);
    }
}
