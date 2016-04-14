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
package com.bobbyloujo.blogbuilder.post;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.bobbyloujo.blogbuilder.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

/**
 * A blog post made of Elements.
 * Created by Ben on 2/17/2016.
 */
public class Post implements Serializable {
	public static final String NEW_FILE = "new file";   // The temporary filename of a new file.
	private static final int SUMMARY_LENGTH = 50;       // The number of characters to include in the post summary.

	private String fileName = NEW_FILE;  // The filename of this Post on the filesystem.
    private String title;                // The title of this post.
    private ElementList elementList;     // The list of Elements that this post is composed of.

	/**
	 * Create a new empty Post.
	 */
	public Post() {
		title = "New Post";
		setElementList(new ElementList());
	}

	/**
	 * Change the title of this post.
	 * @param title The title of this post.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the title of this post.
	 * @return The title of this post.
	 */
    public String getTitle() {
        return title;
    }

	/**
	 * Get a short summary of this post if this post has at least
	 * one TextElement.
	 * @return A short portion of the text from the first TextElement in post if there is one.
	 *         Otherwise, an empty string is returned.
	 */
    public String getSummary() {
        String summary = "";         // The summary text
		boolean foundText = false;   // Flag that indicates a TextElement has been found.

        for (int i = 0; i < elementList.getSize() && !foundText; i++) {
            if (elementList.getElement(i) instanceof TextElement) {
				TextElement p = (TextElement) elementList.getElement(i);
				summary = p.getText().toString();

				if (summary.length() > SUMMARY_LENGTH) {
					summary = summary.substring(0, SUMMARY_LENGTH);
					summary = summary.concat("...");
				}

				foundText = true;
			}
        }

        return summary;
    }

	/**
	 * Populates the given ImageView with the image from the first ImageElement found. If there is
	 * no ImageElement, the ImageView is populated with a text icon instead.
	 * @param imageView The ImageView to populate with an image.
	 */
	public void populateWithFirstImage(final ImageView imageView) {
		ImageElement imageElement = null;
		boolean foundImage = false;

		for (int i = 0; i < elementList.getSize() && !foundImage; i++) {
			if (elementList.getElement(i) instanceof ImageElement) {
				imageElement = (ImageElement) elementList.getElement(i);
				foundImage = true;
			}
		}

		if (imageElement != null) {
			if (imageElement.getLocation() == ImageElement.LOCAL) {
				Bitmap bmp = null;
				File local = new File(imageElement.getSrc());

				try {
					FileInputStream fis = new FileInputStream(local);
					BitmapFactory.Options ops = new BitmapFactory.Options();
					ops.inSampleSize = 10;
					bmp = BitmapFactory.decodeStream(fis, null, ops);
					imageView.setImageBitmap(bmp);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else if (imageElement.getLocation() == ImageElement.INTERNET) {
				new AsyncTask<String, Void, Bitmap>() {
					@Override protected Bitmap doInBackground(String... urls) {
						String url = urls[0];
						Bitmap bmp = null;

						try {
							InputStream in = new URL(url).openStream();
							BitmapFactory.Options ops = new BitmapFactory.Options();
							ops.inSampleSize = 10;
							bmp = BitmapFactory.decodeStream(in, null, ops);
							in.close();
						} catch (Exception e) {
							Log.e("Error", e.getMessage());
							e.printStackTrace();
						}

						return bmp;
					}

					@Override protected void onPostExecute(Bitmap bmp) {
						imageView.setImageBitmap(bmp);
					}
				}.execute(imageElement.getSrc());
			}
		}

		if (!foundImage) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				imageView.setImageDrawable(imageView.getContext().getDrawable(R.drawable.text));
			} else {
				imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.text));
			}
		}
	}

	/**
	 * Get HTML code that will display this blog Post.
	 * @return A String of HTML to display in a WebView.
	 */
    public String getHtml() {
        String html = "<html>";
        html = html.concat(elementList.getHtml());
        html = html.concat("</html>");

        return html;
    }

	/**
	 * Change the filename of this Post on the filesystem.
	 * @param fileName The new filename for this Post
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Get the filename of this Post on the filesystem.
	 * @return The filename of this Post.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Change this Post's ElementList.
	 * @param elementList The new ElementList.
	 */
	public void setElementList(ElementList elementList) {
		this.elementList = elementList;
		this.elementList.setPost(this);
	}

	/**
	 * Get the list of Elements that are in this Post.
	 * @return The ElementList belonging to this Post.
	 */
	public ElementList getElementList() {
		return elementList;
	}

	/**
	 * Get a new instance of Post that is a copy of this one.
	 * @return A copy of this Post.
	 */
	public Post copy() {
		Post copy = new Post();

		copy.fileName = fileName;
		copy.title = title;
		copy.setElementList(elementList.copy());

		return copy;
	}
}
