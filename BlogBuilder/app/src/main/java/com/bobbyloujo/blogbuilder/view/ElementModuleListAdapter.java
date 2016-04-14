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

import com.bobbyloujo.blogbuilder.R;
import com.bobbyloujo.blogbuilder.post.ElementList;

/**
 * ListAdapter for the ListView in the EditPostActivity that displays a list of
 * Element "modules" with the content of the element and buttons to edit, delete,
 * and rearrange them.
 * Created by Benjamin on 2/20/2016.
 */
public class ElementModuleListAdapter extends BaseAdapter {

	private Context context;          // Activity Context
	private LayoutInflater inflater;  // Element module view inflater.
	private ElementList elementList;  // List of Elements to display in the ListView.

	/**
	 * Create a new ElementModuleListAdapter
	 * @param context Context
	 * @param elementList The ElementList to display in the ListView
	 */
	public ElementModuleListAdapter(Context context, ElementList elementList) {
		this.context = context;
		this.elementList = elementList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Change the ElementList
	 * @param elementList The new ElementList
	 */
	public void setElementList(ElementList elementList) {
		this.elementList = elementList;
	}

	@Override public int getCount() {
		return elementList.getSize();
	}

	@Override public Object getItem(int i) {
		return elementList.getElement(i);
	}

	@Override public long getItemId(int i) {
		return i;
	}

	@Override public View getView(int i, View view, ViewGroup viewGroup) {
		if (view == null) {
			view = inflater.inflate(R.layout.element_edit_module, null);
		}

		new ElementModuleViewController(context, this, view).setElement(elementList.getElement(i));

		return view;
	}

	public ElementList getElementList() {
		return elementList;
	}
}
