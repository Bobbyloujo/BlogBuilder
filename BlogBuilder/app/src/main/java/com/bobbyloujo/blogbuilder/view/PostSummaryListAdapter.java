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
package com.bobbyloujo.blogbuilder.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobbyloujo.blogbuilder.application.ActivityStarter;
import com.bobbyloujo.blogbuilder.application.PostLoader;
import com.bobbyloujo.blogbuilder.post.Post;
import com.bobbyloujo.blogbuilder.R;

/**
 * ListAdapter for the ListView in the BlogPostListActivity.
 * Created by Ben on 2/18/2016.
 */
public class PostSummaryListAdapter extends BaseAdapter {

    private Context context;         // Activity context
    private LayoutInflater inflater; // Inflater to inflate the blog summary views.

	private PostLoader postLoader;   // Reference to the PostLoader to get the saved posts.

    public PostSummaryListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		postLoader = PostLoader.getInstance();
    }

	public void savePostList() {
		postLoader.savePostList();
	}

    @Override
    public int getCount() {
        return postLoader.getSize();
    }

    @Override
    public Object getItem(int position) {
        return postLoader.getPost(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		TextView title;      // TextView to display the title.
        TextView summary;    // TextView to display the summary text.
		ImageView imageView; // ImageView to display the icon.

		final Post post = postLoader.getPost(position); // The post to summarize
		String titleString;                             // The title text
		String summaryString;                           // The summary text

		if (post != null) {
			titleString = post.getTitle();
			summaryString = post.getSummary();
		} else {
			titleString = "Post File Corrupted or Serializable Class Changed.";
			summaryString = "Unable to load the blog post.";
		}

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.blog_post_summary, null);
        }

		title = (TextView) convertView.findViewById(R.id.summarytitle);
		title.setText(titleString);

        summary = (TextView) convertView.findViewById(R.id.summarytextview);
        summary.setText(summaryString);

		imageView = (ImageView) convertView.findViewById(R.id.summaryicon);

		if (post != null) {
			post.populateWithFirstImage(imageView);

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ActivityStarter.startBlogPostActivity(context, post.getFileName());
				}
			});
		}

        return convertView;
    }
}
