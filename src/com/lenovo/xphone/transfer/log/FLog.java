package com.lenovo.xphone.transfer.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class FLog {
	// format: 2014-10-01 12:00:01 DEBUG|TAG msg
	private static FileWriter fileWriter = null;
	private static FileReader fileReader;
	private static BufferedReader br;

	public static final String LOG_PATH = "/sdcard/";
	public static final String LOG_ALL_TAG = "All";

	private static String logname = "log.log";

	public static void init(String filename) {
		logname = filename;
		try {
			if (logname != null) {
				if (fileWriter == null) {
					fileWriter = new FileWriter(new File(LOG_PATH + logname),
							true);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void uninit() {
		try {
			if (fileWriter != null) {
				fileWriter.close();
			}
			fileWriter = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String readLogByLine() {
		String ret = null;
		try {
			if (br != null) {
				ret = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static List<String> getLogTagAndPid(String filename) {
		try {
			fileReader = new FileReader(new File(LOG_PATH + filename));
			br = new BufferedReader(fileReader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> list = new ArrayList<String>();
		list.add(LOG_ALL_TAG);
		while (true) {
			String aline = readLogByLine();
			if (aline == null) {
				break;
			}
			try {
				String tag = aline.substring(aline.indexOf("|") + 1,
						aline.indexOf(" ", aline.indexOf("|") + 1));
				boolean found = false;
				for (String str : list) {
					if (str.equals(tag)) {
						found = true;
						break;
					}
				}
				if (!found) {
					list.add(tag);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	private synchronized static void writeLine(String msg) {
		try {
			init(logname);
			if (fileWriter != null) {
				fileWriter.write(msg + "\r\n");
				fileWriter.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void logMessageFormat(String level, String tag, String msg) {
		String logmsg = getTimeStamp() + " " + Thread.currentThread().getId();

		logmsg += " " + level + "|" + tag + " " + msg;
		writeLine(logmsg);
	}

	private static String getTimeStamp() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss:SSS");

		return sDateFormat.format(new java.util.Date());
	}

	private static String getFilenameStamp() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");

		return sDateFormat.format(new java.util.Date());
	}

	public static void verbose(String tag, String msg) {
		v(tag, msg);
	}

	public static void v(String tag, String msg) {
		Log.v(tag, msg);
		logMessageFormat("VERBOSE", tag, msg);
	}

	public static void debug(String tag, String msg) {
		d(tag, msg);
	}

	public static void d(String tag, String msg) {
		Log.d(tag, msg);
		logMessageFormat("DEBUG", tag, msg);
	}

	public static void info(String tag, String msg) {
		i(tag, msg);
	}

	public static void i(String tag, String msg) {
		Log.i(tag, msg);
		logMessageFormat("INFO", tag, msg);
	}

	public static void warn(String tag, String msg) {
		w(tag, msg);
	}

	public static void w(String tag, String msg) {
		w(tag, msg,null);
	}

	public static void w(String tag, String msg, Throwable e) {
		msg = msg + "\r\n" + getStackTraceMessage(e);
		Log.w(tag, msg);
		logMessageFormat("WARN", tag, msg);
	}

	public static void error(String tag, String msg) {
		e(tag, msg);
	}

	public static void e(String tag, String msg) {
		e(tag, msg,null);
	}

	public static void e(String tag, Throwable e) {
		e(tag, "ERROR", e);
	}

	public static void e(String tag, String msg, Throwable e) {
		msg = msg + "\r\n" + getStackTraceMessage(e);
		Log.e(tag, msg);
		logMessageFormat("ERROR", tag, msg);
	}

	public static void success(String tag, String msg) {
		Log.i(tag + "-SUCCESS", msg);
		logMessageFormat("SUCCESS", tag, msg);
	}

	public static void s(String tag, String msg) {
		Log.i(tag + "-SUCCESS", msg);
		logMessageFormat("SUCCESS", tag, msg);
	}

	private static String getStackTraceMessage(Throwable e) {
		String message = Log.getStackTraceString(e);
		return message + "\n" + e.getMessage();
	}
}
