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

import android.text.Html;

/**
 * A post Element that contains text.
 * Created by Ben on 2/17/2016.
 */
public class TextElement extends Element {

	/**
	 * Change this TextElement's text.
	 * @param text The new text
	 */
	public void setText(String text) {
        super.setHtml(text);
    }

	/**
	 * Get this TextElement's text.
	 * @return This TextElement's text.
	 */
    public CharSequence getText() {
        CharSequence text = Html.fromHtml(getHtml());
        int i = text.length() - 1;

        while (i >= 0 && Character.isWhitespace(text.charAt(i))) {
            i--;
        }

        return text.subSequence(0, i+1);
    }

    @Override
	public TextElement copy() {
		TextElement copy = new TextElement();

		copy.setHtml(getHtml());

		return copy;
	}
}
