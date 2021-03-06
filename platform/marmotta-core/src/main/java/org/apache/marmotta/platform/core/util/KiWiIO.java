/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.marmotta.platform.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.base.Preconditions;

/**
 * Add file description here!
 * <p/>
 * Author: Sebastian Schaffert
 */
public class KiWiIO {

	/**
	 * Deletes all the files within a directory. Does not delete the directory
	 * itself.
	 * 
	 * <p>
	 * If the file argument is a symbolic link or there is a symbolic link in
	 * the path leading to the directory, this method will do nothing. Symbolic
	 * links within the directory are not followed.
	 * 
	 * @param directory
	 *            the directory to delete the contents of
	 * @throws IllegalArgumentException
	 *             if the argument is not a directory
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see #deleteRecursively
	 */
	public static void deleteDirectoryContents(File directory)
			throws IOException {
		Preconditions.checkArgument(directory.isDirectory(),
				"Not a directory: %s", directory);
		File[] files = directory.listFiles();
		if (files == null) {
			throw new IOException("Error listing files for " + directory);
		}
		for (File file : files) {
			deleteRecursively(file);
		}
	}

	/**
	 * Deletes a file or directory and all contents recursively.
	 * 
	 * <p>
	 * If the file argument is a symbolic link the link will be deleted but not
	 * the target of the link. If the argument is a directory, symbolic links
	 * within the directory will not be followed.
	 * 
	 * @param file
	 *            the file to delete
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see #deleteDirectoryContents
	 */
	public static void deleteRecursively(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectoryContents(file);
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}

	public static String md5sum(File file) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(file);
		return md5sum(fis);
	}

	public static String md5sum(InputStream input) throws IOException {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			DigestInputStream dis = new DigestInputStream(input, md);
			try {
				byte[] buff = new byte[1024];
				// just read to get the Digest filled...
				while (dis.read(buff) > 0)
					;
				return new BigInteger(1, md.digest()).toString(16);
			} finally {
				dis.close();
			}
		} catch (NoSuchAlgorithmException e) {
			// this should not happen
			return null;
		}
	}
}
