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
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.bobbyloujo.blogbuilder.R;
import com.bobbyloujo.blogbuilder.application.ActivityStarter;
import com.bobbyloujo.blogbuilder.post.Element;
import com.bobbyloujo.blogbuilder.post.ImageElement;
import com.bobbyloujo.blogbuilder.post.TextElement;
import com.bobbyloujo.blogbuilder.post.VideoElement;

/**
 *
 * Created by Benjamin on 2/20/2016.
 */
public class ElementModuleViewController {

	private Element element;        // Element do display/edit/delete/move

	private TextView typeTextView;  // TextView that displays what type of Element element is.
	private Button upButton;        // Button that moves the element up in the list.
	private Button downButton;      // Button that moves the element down in the list.
	private Button editButton;      // Button that opens an edit Activity for the Element.
	private Button removeButton;    // Button that removes the element from the ElementList
	private WebView webView;        // The WebView that displays the contents of the element.

	private Context context;                  // Activity context
	private ElementModuleListAdapter adapter; // The ListAdapter for the list containing the module view

	public ElementModuleViewController(Context context, ElementModuleListAdapter adapter, View elementModule) {
		this.context = context;
		this.adapter = adapter;

		typeTextView = (TextView) elementModule.findViewById(R.id.typeTextView);
		upButton = (Button) elementModule.findViewById(R.id.upButton);
		downButton = (Button) elementModule.findViewById(R.id.downButton);
		editButton = (Button) elementModule.findViewById(R.id.editButton);
		removeButton = (Button) elementModule.findViewById(R.id.removeButton);
		webView = (WebView) elementModule.findViewById(R.id.elementWebView);

		webView.setWebChromeClient(new WebChromeClient());

		upButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View view) {
				onUpButtonPressed();
			}
		});

		downButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View view) {
				onDownButtonPressed();
			}
		});

		editButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View view) {
				onEditButtonPressed();
			}
		});

		removeButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View view) {
				onRemoveButtonPressed();
			}
		});
	}

	/**
	 * Change the Element.
	 * @param element The new Element.
	 */
	public void setElement(Element element) {
		this.element = element;
		refresh();
	}

	/**
	 * Refresh the contents of the module view.
	 */
	public void refresh() {
		String type = "Unknown Type";

		if (element instanceof TextElement) {
			type = context.getString(R.string.text);
		} else if (element instanceof ImageElement) {
			type = context.getString(R.string.image);
		} else if (element instanceof VideoElement) {
			type = context.getString(R.string.video);
		}

		typeTextView.setText(type);

		webView.loadDataWithBaseURL("", element.getHtml(), "text/html", "UTF-8", "");
	}

	private void onUpButtonPressed() {
		adapter.getElementList().moveElementUp(element);
		adapter.notifyDataSetChanged();
	}

	private void onDownButtonPressed() {
		adapter.getElementList().moveElementDown(element);
		adapter.notifyDataSetChanged();
	}

	private void onEditButtonPressed() {
		if (element instanceof TextElement) {
			ActivityStarter.startEditTextElementActivity(context, element.getElementList().getPost(), (TextElement) element);
		} else if (element instanceof ImageElement) {
			ActivityStarter.startEditImageElementActivity(context, element.getElementList().getPost(), (ImageElement) element);
		} else if (element instanceof VideoElement) {
			ActivityStarter.startEditVideoElementActivity(context, element.getElementList().getPost(), (VideoElement) element);
		}
	}

	public void onRemoveButtonPressed() {
		adapter.getElementList().removeElement(element);
		adapter.notifyDataSetChanged();
	}
}
