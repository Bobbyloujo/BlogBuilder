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

import java.util.ArrayList;
import java.util.Collections;

/**
 * A list of Elements.
 * Created by Ben on 2/17/2016.
 */
public class ElementList extends Element {
    private ArrayList<Element> elements; // The Elements contained in this list.
	private Post post;                   // The Post that this list belongs to.

	/**
	 * Create a new ElementList.
	 */
    public ElementList() {
        elements = new ArrayList<Element>();
    }

	/**
	 * Get the Element at the specified index.
	 * @param index Index of the Element.
	 * @return The Element at the index.
	 */
    public Element getElement(int index) {
        return elements.get(index);
    }

	/**
	 * Determine if this ElementList contains the specified Element.
	 * @param element The Element to search for.
	 * @return True if the Element element is contained in this list, false otherwise.
	 */
	public boolean contains(Element element) {
		return elements.contains(element);
	}

	/**
	 * Returns the index of the specified Element in this list. If e is null or
	 * not contained in this list, this method will return -1.
	 * @param e The Element to search for.
	 * @return The index of Element e if it is not null and found in the list, -1 otherwise.
	 */
	public int getElementIndex(Element e) {
		if (e == null) return -1;
		return elements.indexOf(e);
	}

	/**
	 * Get the number of Elements in this ElementList.
	 * @return The number of Elements in this ElementList.
	 */
	public int getSize() {
		return elements.size();
	}

	/**
	 * Add an Element to this ElementList.
	 * @param e The Element to add to this list.
	 */
    public void addElement(Element e) {
        elements.add(e);
		e.setElementList(this);

		if (e instanceof ElementList) {
			((ElementList) e).setPost(this.post);
		}
    }

	/**
	 * Set the Post that this list belongs to.
	 * @param post
	 */
	void setPost(Post post) {
		this.post = post;
	}

	/**
	 * Get the Post that this ElementList belongs to.
	 * @return The Post that this ElementList belongs to.
	 */
	public Post getPost() {
		return post;
	}

	/**
	 * Remove an Element from this list.
	 * @param e The Element to remove.
	 */
    public void removeElement(Element e) {
        elements.remove(e);
    }

	/**
	 * Move an Element up in the order of this List.
	 * @param e The Element to move up.
	 */
	public void moveElementUp(Element e) {
		int index = elements.indexOf(e);

		if (index > 0 && index < elements.size()) {
			Collections.swap(elements, index, index - 1);
		}
	}

	/**
	 * Move an Element down in the order of this list.
	 * @param e The Element to move down.
	 */
	public void moveElementDown(Element e) {
		int index = elements.indexOf(e);

		if (index >= 0 && index < elements.size() - 1) {
			Collections.swap(elements, index, index + 1);
		}
	}

    @Override
    public String getHtml(){
        String html = "";

        for (Element e : elements) {
            html = html.concat(e.getHtml());
        }

        return html;
    }

	@Override
	public ElementList copy() {
		ElementList copy = new ElementList();

		for (Element e : elements) {
			copy.addElement(e.copy());
		}

		return copy;
	}
}
