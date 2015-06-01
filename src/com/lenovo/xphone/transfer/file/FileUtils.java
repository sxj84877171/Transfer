package com.lenovo.xphone.transfer.file;

import java.io.File;

public class FileUtils {

	public static String getTempFilename(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return path;
		}

		String absolutePath = file.getAbsolutePath();
		String name = getNameByFilename(getFilenameByPath(path));
		String type = getFileTypeByFilename(getFileTypeByFilename(path));
		for (int i = 1; i <= Integer.MAX_VALUE; i++) {
			String temp = absolutePath + name + "(" + i + ")" + type;
			if (!isFileExist(temp)) {
				return temp;
			}
		}
		return null;
	}

	public static boolean isFileExist(String filename) {
		return new File(filename).exists();
	}

	public static String getFilenameByPath(String path) {
		if (path == null) {
			return null;
		}

		int start = path.lastIndexOf('\\');
		if (start != -1) {
			return path.substring(start);
		}
		return null;
	}

	public static String getFileTypeByFilename(String filename) {
		int start = filename.lastIndexOf('.');
		if (start >= 0) {
			return filename.substring(start);
		}
		return null;
	}

	public static String getNameByFilename(String filename) {
		int start = filename.lastIndexOf('.');
		if (start >= 0) {
			return filename.substring(0, start);
		}
		return null;
	}

}
