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

import com.bobbyloujo.blogbuilder.post.Post;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * This class handles data persistence between app sessions and retrieving posts
 * for Activities.
 *
 * Created by Benjamin on 2/19/2016.
 */
public class PostLoader {
	private static final String POST_LIST_FILE_NAME = "post-file-names";  // The name of the file containing a list of filenames of the Posts
	private static final String TEMP_POST_NAME = "temp";                  // The filename of the temporary copy of a post that is currently being edited.

	private static PostLoader postLoader;                                 // The sole instance of this class.

	private Post tempPost;                     // The temporary copy of a post that is currently being edited.
	private Context context;                   // The Context used for CRUDing files.
	private ArrayList<String> postFileNames;   // The list of Post filenames

	/**
	 * Create a new PostLoader
	 * @param context Context used for saving, loading, and deleting files.
	 */
	private PostLoader(Context context) {
		this.context = context;
	}

	/**
	 * Initialize the PostLoader.
	 * @param context Context used for saving, loading, and deleting files.
	 */
	static void init(Context context) {
		postLoader = new PostLoader(context);
	}

	/**
	 * Get an instance of PostLoader for CRUDing posts.
	 * @return an instance of PostLoader.
	 */
	public static PostLoader getInstance() {
		if (postLoader == null) {
			throw new NullPointerException("PostLoader not initialized. Is the application a BlogBuilderApplication?");
		}

		if (postLoader.postFileNames == null) {
			postLoader.loadPostList();
		}

		return postLoader;
	}

	/**
	 * Generate a filename for the specified post and add it to the list of posts.
	 * @param post The post to add.
	 */
	private void addPost(Post post) {
		if (post.getFileName().equals(Post.NEW_FILE)) {
			post.setFileName(generateFileName(post));
		}

		if (!postFileNames.contains(post.getFileName())) {
			postFileNames.add(post.getFileName());
			savePost(post);
			savePostList();
		}
	}

	/**
	 * Determine if the specified post is contained in the list of posts.
	 * @param post The Post to search for.
	 * @return True if post is in the post list, false otherwise.
	 */
	public boolean contains(Post post) {
		return postFileNames.contains(post.getFileName());
	}

	/**
	 * Returns the post at index i in the post list.
	 * @param i The index of the post to return.
	 * @return The post at index i.
	 */
	public Post getPost(int i) {
		return loadPost(postFileNames.get(i));
	}

	/**
	 * Get the number of posts in the post list.
	 * @return The number of posts in the post list.
	 */
	public int getSize() {
		return postFileNames.size();
	}

	/**
	 * Generate a file name for the given Post.
	 * @param post The Post for which to generate a filename.
	 * @return A unique filename for the Post.
	 */
	private String generateFileName(Post post) {
		String fileName = post.getTitle().replaceAll(" ","");
		File file = new File(context.getFilesDir().getPath() + "/" + fileName);
		int i = 0;

		while (file.exists()) {
			i++;
			file = new File(context.getFilesDir().getPath() + "/" + fileName.concat(Integer.toString(i)));
		}

		if (i > 0) {
			fileName = fileName.concat(Integer.toString(i));
		}

		return fileName;
	}

	/**
	 * Saves the specified post so that it may be retrieved later or during
	 * a different app session.
	 * @param post The Post to save.
	 */
	public void savePost(Post post) {
		FileOutputStream outputStream;
		ObjectOutputStream objectOutputStream;

		if (!contains(post)) {
			addPost(post);
		}

		try {
			outputStream = context.openFileOutput(post.getFileName(), Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(post);

			outputStream.close();
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads a saved Post.
	 * @param fileName The filename of the post to load.
	 * @return The post saved with the specified filename or null if it doesn't exist.
	 */
	public Post loadPost(String fileName) {
		Post post = null;
		FileInputStream fileInputStream;
		ObjectInputStream objectInputStream;

		try {
			fileInputStream = context.openFileInput(fileName);
			objectInputStream = new ObjectInputStream(fileInputStream);
			post = (Post) objectInputStream.readObject();

			fileInputStream.close();
			objectInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return post;
	}

	/**
	 * Deletes the specified post from the post list and the file system.
	 * @param post The Post to delete.
	 */
	public void deletePost(Post post) {
		if (postFileNames.contains(post.getFileName())) {
			File file = new File(context.getFilesDir().getPath() + "/" + post.getFileName());
			boolean deleted = false;
			deleted = file.delete();

			if (deleted) {
				postFileNames.remove(post.getFileName());
				savePostList();
			}
		}
	}

	/**
	 * Save the specified Post as the temp Post. This post should be a copy
	 * of the Post that is currently being edited.
	 * @param post A copy of the post being edited.
	 */
	public void saveTempPost(Post post) {
		FileOutputStream outputStream;
		ObjectOutputStream objectOutputStream;

		tempPost = post.copy();

		try {
			outputStream = context.openFileOutput(TEMP_POST_NAME, Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(post);

			outputStream.close();
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the temporary post. This is a copy of the post being edited.
	 * @return A copy of the post being edited.
	 */
	public Post getTempPost() {
		if (tempPost != null) {
			return tempPost;
		}

		Post post = null;
		FileInputStream fileInputStream;
		ObjectInputStream objectInputStream;

		try {
			fileInputStream = context.openFileInput(TEMP_POST_NAME);
			objectInputStream = new ObjectInputStream(fileInputStream);
			post = (Post) objectInputStream.readObject();

			fileInputStream.close();
			objectInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		tempPost = post;

		return post;
	}

	/**
	 * Save the list of post filenames.
	 */
	public void savePostList() {
		FileOutputStream outputStream;
		ObjectOutputStream objectOutputStream;

		try {
			outputStream = context.openFileOutput(POST_LIST_FILE_NAME, Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(postFileNames);

			outputStream.close();
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load the list of post filenames.
	 */
	private void loadPostList() {
		FileInputStream fileInputStream;
		ObjectInputStream objectInputStream;

		try {
			fileInputStream = context.openFileInput(POST_LIST_FILE_NAME);
			objectInputStream = new ObjectInputStream(fileInputStream);
			postFileNames = (ArrayList<String>) objectInputStream.readObject();

			fileInputStream.close();
			objectInputStream.close();
		} catch(FileNotFoundException e) {
			postFileNames = new ArrayList<>();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
