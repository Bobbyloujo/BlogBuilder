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
package com.bobbyloujo.blogbuilder.application;

import android.content.Context;
import android.content.Intent;

import com.bobbyloujo.blogbuilder.activity.BlogPostActivity;
import com.bobbyloujo.blogbuilder.activity.EditImageElementActivity;
import com.bobbyloujo.blogbuilder.activity.EditTextElementActivity;
import com.bobbyloujo.blogbuilder.activity.EditPostActivity;
import com.bobbyloujo.blogbuilder.activity.EditVideoElementActivity;
import com.bobbyloujo.blogbuilder.post.ImageElement;
import com.bobbyloujo.blogbuilder.post.TextElement;
import com.bobbyloujo.blogbuilder.post.Post;
import com.bobbyloujo.blogbuilder.post.VideoElement;

/**
 * This class contains helper methods that start Activities. These methods start the Activity and
 * pass required data to them.
 *
 * Created by Benjamin on 3/1/2016.
 */
public class ActivityStarter {

	/**
	 * Starts an Activity to display the blog post saved with the file name postFileName.
	 * @param context Context with which to launch the Activity.
	 * @param postFileName The file name of the post.
	 */
	public static void startBlogPostActivity(Context context, String postFileName) {
		Intent intent = new Intent(context, BlogPostActivity.class);
		intent.putExtra(BlogPostActivity.POST_EXTRA, postFileName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * Starts an Activity to edit the specified Post.
	 * @param context Context with which to launch the Activity.
	 * @param post The Post to edit.
	 */
	public static void startEditPostActivity(Context context, Post post) {
		Intent intent = new Intent(context, EditPostActivity.class);
		PostLoader.getInstance().saveTempPost(post);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * Starts an Activity to edit a TextElement.
	 * @param context Context with which to launch the Activity.
	 * @param post Post containing the TextElement
	 * @param textElement The TextElement to edit. If null, a new TextElement will be created.
	 */
	public static void startEditTextElementActivity(Context context, Post post, TextElement textElement) {
		Intent intent = new Intent(context, EditTextElementActivity.class);
		PostLoader.getInstance().saveTempPost(post);
		intent.putExtra(EditTextElementActivity.ELEMENT_INDEX_EXTRA, post.getElementList().getElementIndex(textElement));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * Starts an Activity to edit a ImageElement.
	 * @param context Context with which to launch the Activity.
	 * @param post Post containing the ImageElement
	 * @param imageElement The ImageElement to edit. If null, a new ImageElement will be created.
	 */
	public static void startEditImageElementActivity(Context context, Post post, ImageElement imageElement) {
		Intent intent = new Intent(context, EditImageElementActivity.class);
		PostLoader.getInstance().saveTempPost(post);
		intent.putExtra(EditImageElementActivity.ELEMENT_INDEX_EXTRA, post.getElementList().getElementIndex(imageElement));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * Starts an Activity to edit a VideoElement.
	 * @param context Context with which to launch the Activity.
	 * @param post Post containing the VideoElement
	 * @param videoElement The VideoElement to edit. If null, a new VideoElement will be created.
	 */
	public static void startEditVideoElementActivity(Context context, Post post, VideoElement videoElement) {
		Intent intent = new Intent(context, EditVideoElementActivity.class);
		PostLoader.getInstance().saveTempPost(post);
		intent.putExtra(EditVideoElementActivity.ELEMENT_INDEX_EXTRA, post.getElementList().getElementIndex(videoElement));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
