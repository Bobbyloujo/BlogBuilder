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

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bobbyloujo.blogbuilder.application.PostLoader;
import com.bobbyloujo.blogbuilder.post.Post;
import com.bobbyloujo.blogbuilder.R;
import com.bobbyloujo.blogbuilder.post.TextElement;

import java.util.ArrayList;

/**
 * An Activity for editing TextElements.
 */
public class EditTextElementActivity extends AppCompatActivity {
	public static final String ELEMENT_INDEX_EXTRA = "paragraph element index";  // The name of the extra used to pass the position of the TextElement in the ElementList

	private EditText editText;           // The text field used to edit the text.
	private Post post;                   // The Post that the TextElement belongs to.
	private TextElement textElement;     // The TextElement to be edited.

	private ArrayList<Style> styles;     // A list of style objects used to keep track of bold and italicized portions of the text.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_text_element);
		int index = getIntent().getIntExtra(ELEMENT_INDEX_EXTRA, -1);

		styles = new ArrayList<>();
		editText = (EditText) findViewById(R.id.editor);

		post = PostLoader.getInstance().getTempPost();                            // Get the post

		if (index != -1) {                                                        // We're editing an existing TextElement
			textElement = (TextElement) post.getElementList().getElement(index);  // So get it from the ElementList
		} else {                                                                  // We're adding a new TextElement
			textElement = new TextElement();                                      // So initialize a new one
		}

		editText.setText(textElement.getText());

		// Add the current bold and italicized portions to the style list.
		StyleSpan styleSpans[] = editText.getText().getSpans(0, editText.length(), StyleSpan.class);
		for (StyleSpan styleSpan : styleSpans) {
			int start = editText.getText().getSpanStart(styleSpan);
			int end = editText.getText().getSpanEnd(styleSpan);
			styles.add(new Style(styleSpan, start, end));
		}

		final Button bold = (Button) findViewById(R.id.boldButton);
		bold.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onBoldButtonPressed();
			}
		});

		final Button italic = (Button) findViewById(R.id.italicButton);
		italic.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onItalicButtonPressed();
			}
		});

		final Button save = (Button) findViewById(R.id.saveButton);
		save.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onSaveButtonPressed();
			}
		});

		final Button cancel = (Button) findViewById(R.id.cancelButton);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				onCancelButtonPressed();
			}
		});
	}

	/**
	 * The "Bold" button was pressed.
	 */
	private void onBoldButtonPressed() {
		int s = editText.getSelectionStart();  // The start of the current text selection
		int e = editText.getSelectionEnd();    // The end of the current text selection

		Spannable text = editText.getText();                       // The text in the text field
		StyleSpan[] spans = text.getSpans(s, e, StyleSpan.class);  // A list of the existing spans in the text field text
		boolean spanFound = false;                                 // Flag indicating that at least one existing bold span was found in the range of the selection

		// Iterate through the existing spans to determine if any exist in range of the selection.
		for (StyleSpan ss : spans) {
			// If a bold span is found within the current selection, we should un-bold the portion within the selection.
			if (ss.getStyle() == Typeface.BOLD) {
				if (text.getSpanStart(ss) < s) { // The existing span starts before the selection.
					// Create a new span that covers only the portion of the existing span outside of the selection.
					StyleSpan bold = new StyleSpan(Typeface.BOLD);
					styles.add(new Style(bold, text.getSpanStart(ss), s));
					text.setSpan(bold, text.getSpanStart(ss), s, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				if (text.getSpanEnd(ss) > e) {   // The existing ends after the end of the current selection.
					// Create a new span that covers only the portion of the existing span outside of the selection.
					StyleSpan bold = new StyleSpan(Typeface.BOLD);
					styles.add(new Style(bold, e, text.getSpanEnd(ss)));
					text.setSpan(bold, e, text.getSpanEnd(ss), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				// We need to remove the old existing style from the list so that un-bolded portions will not be bolded.
				Object copy[] = styles.toArray();
				for (Object style : copy) {
					if (((Style) style).style == ss) {
						styles.remove(style);
					}
				}

				// Remove the old span from the text field so that un-bolded portions appear un-bolded.
				text.removeSpan(ss);
				spanFound = true; // Indicate that an existing span was found and has been handled.
			}
		}

		if (!spanFound) {         // Only create a new span/style to bold the selection if no existing span was found and un-bolded.
			if (s < e) {
				StyleSpan bold = new StyleSpan(Typeface.BOLD);
				styles.add(new Style(bold, s, e));
				text.setSpan(bold, s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				StyleSpan bold = new StyleSpan(Typeface.BOLD);
				styles.add(new Style(bold, e, s));
				text.setSpan(bold, e, s, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	/**
	 * The "Italic" button was pressed.
	 */
	private void onItalicButtonPressed() {
		int s = editText.getSelectionStart();
		int e = editText.getSelectionEnd();

		Spannable text = editText.getText();
		StyleSpan[] spans = text.getSpans(s, e, StyleSpan.class);
		boolean spanFound = false;

		for (StyleSpan ss : spans) {
			if (ss.getStyle() == Typeface.ITALIC) {
				if (text.getSpanStart(ss) < s) {
					StyleSpan italic = new StyleSpan(Typeface.ITALIC);
					styles.add(new Style(italic, text.getSpanStart(ss), s));
					text.setSpan(italic, text.getSpanStart(ss), s, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				if (text.getSpanEnd(ss) > e) {
					StyleSpan italic = new StyleSpan(Typeface.ITALIC);
					styles.add(new Style(italic, e, text.getSpanEnd(ss)));
					text.setSpan(italic, e, text.getSpanEnd(ss), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				Object copy[] = styles.toArray();
				for (Object style : copy) {
					if (((Style) style).style == ss) {
						styles.remove(style);
					}
				}

				text.removeSpan(ss);
				spanFound = true;
			}
		}

		if (!spanFound) {
			if (s < e) {
				StyleSpan italic = new StyleSpan(Typeface.ITALIC);
				styles.add(new Style(italic, s, e));
				text.setSpan(italic, s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				StyleSpan italic = new StyleSpan(Typeface.ITALIC);
				styles.add(new Style(italic, e, s));
				text.setSpan(italic, e, s, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	/**
	 * The "Save" button was pressed.
	 */
	private void onSaveButtonPressed() {
		if (textElement != null) {
			editText.getText().clearSpans(); // Clear all current spans. This is required to remove the underline that the EditText adds to the word under the cursor.

			// Add all of the saved spans in the style list to the text.
			for (Style s : styles) {
				editText.getText().setSpan(s.style, s.start, s.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			textElement.setText(Html.toHtml(editText.getText()));

			if (!post.getElementList().contains(textElement)) {
				post.getElementList().addElement(textElement);
			}
		}

		PostLoader.getInstance().saveTempPost(post);

		editText.setSelection(editText.getText().length(), editText.getText().length()); // Fixes SpannableStringBuilder bug on some devices.
		finish();
	}

	/**
	 * The "Cancel" button was pressed.
	 */
	private void onCancelButtonPressed() {
		finish();
	}

	/**
	 * This class holds information required to create a Span of a specific style
	 * over a specific range on a piece of spannable text.
	 */
	private static class Style {
		CharacterStyle style; // The style of the span
		int start;            // The first index of the range
		int end;              // The last index of the range.

		/**
		 * Create a new Style.
		 * @param style The span style
		 * @param s The first index of the range of the style
		 * @param e The last index of the range of the style
		 */
		Style(CharacterStyle style, int s, int e) {
			this.style = style;
			this.start = s;
			this.end = e;
		}
	}
}
