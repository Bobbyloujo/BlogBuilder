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

import java.io.Serializable;

/**
 * An element in a blog post. For example: some text, an image, or a video.
 * Created by Ben on 2/17/2016.
 */
public class Element implements Serializable {
    private String html = "";                  // The HTML used to display the content of this element in a WebView
    private ElementList list;                  // ElementList that this Element belongs to.

	/**
	 * Set HTML used to display the content of this element in a WebView.
	 * @param html The HTML used to display the content of this element in a WebView.
	 */
    protected void setHtml(String html) {
        this.html = html;
    }

	/**
	 * Get the HTML used to display the content of this element in a WebView.
	 * @return The HTML used to display the content of this element in a WebView.
	 */
    public String getHtml() {
        return html;
    }

	/**
	 * Set the ElementList that this Element belongs to.
	 * @param elementList The ElementList that this Element belongs to.
	 */
	void setElementList(ElementList elementList) {
		this.list = elementList;
	}

	/**
	 * Get the ElementList that this Element belongs to.
	 * @return The ElementList that this Element belongs to.
	 */
	public ElementList getElementList() {
		return list;
	}

	/**
	 * Get an exact copy of this Element.
	 * @return A copy of this Element.
	 */
	public Element copy() {
		Element copy = new Element();
		copy.setHtml(html);

		return copy;
	}
}
