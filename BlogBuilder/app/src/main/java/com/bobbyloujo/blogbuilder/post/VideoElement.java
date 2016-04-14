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

/**
 * A post Element that contains a video
 * Created by Benjamin on 2/27/2016.
 */
public class VideoElement extends Element {
	public static final int LOCAL = 0;          // Used to specify that the location of the video is on local storage.
	public static final int INTERNET = 1;       // Used to specify that the location of the video is on the internet.

	private int location = LOCAL;  // The location of the video file, either LOCAL or INTERNET
	private String src = "";       // The source path or URL of the video file
	private String width = "75%";  // The width of the video as HTML.
	private String height = "";    // The height of the video as HTML.

	/**
	 * Set the location of the video file.
	 * @param location Either LOCAL for the local file system or INTERNET for a web address.
	 */
	public void setLocation(int location) {
		if (location != LOCAL && location != INTERNET) {
			throw new IllegalArgumentException("Video source location not either LOCAL or INTERNET.");
		}

		this.location = location;
	}

	/**
	 * Set the URL or real filepath of the video file.
	 * @param src The URL or real filepath of the video file.
	 */
	public void setSrcURL(String src) {
		if (src != null) {
			this.src = src.replace('\\', '/');
		}
	}

	/**
	 * Set the width of the image.
	 * @param width The height of the image as it would be written in HTML (Ex. 75%, 100px, etc.).
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * Set the height of the video.
	 * @param height The height of the video as it would be written in HTML (Ex. 75%, 100px, etc.).
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * Get the location of the video file, either LOCAL or INTERNET.
	 * @return The location of the video file.
	 */
	public int getLocation() {
		return location;
	}

	/**
	 * Get the source filepath or URL of the video file.
	 * @return The real filepath or URL of the video file.
	 */
	public String getSrcURL() {
		return src;
	}

	/**
	 * Get the height of the video as HTML.
	 * @return The height of the video as HTML.
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * Get the width of the video as HTML.
	 * @return The width of the video as HTML.
	 */
	public String getWidth() {
		return width;
	}

	@Override
	public String getHtml() {
		String fullSrc = "";
		String html;

		if (location == LOCAL) {
			fullSrc = "file:///" + src;
		} else if (location == INTERNET) {
			fullSrc = src;
		}

		html =	"<center><video poster preload=\"true\" controls autoplay width=\"" + width + "\" height=\"" + height + "\">" +
				"<source src=\"" + fullSrc + "\" type=\"video/mp4\"/>" +
				"</video></center>";

		return html;
	}

	@Override
	public VideoElement copy() {
		VideoElement copy = new VideoElement();

		copy.setLocation(location);
		copy.setSrcURL(src);
		copy.setHeight(height);
		copy.setWidth(width);

		return copy;
	}
}
