/***
 * 
 */
package com.lenovo.xphone.transfer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lenovo.xphone.transfer.rule.Config;

/***
 * 
 * @author Elvis
 * 
 */
public class TransferUtil {

	/**
	 * read small packet.
	 * 
	 * @param inputStream
	 *            read from the inputstream.
	 * @param size
	 *            read small packet size. if need read big packet data,refer
	 * @return data
	 * @throws IOException
	 */
	public static byte[] readBuffer(InputStream inputStream, int size)
			throws IOException {
		byte[] result = new byte[size];
		byte[] temp = new byte[size];
		int len = 0;
		int start = 0;
		while ((len = inputStream.read(temp)) > 0) {
			for (int i = 0; i < len; i++) {
				result[start + i] = temp[i];
			}
			start += len;
			if (start == size) {
				break;
			}
			temp = new byte[size - start];
		}

		return result;
	}
	
	public static byte[] readBuffer(InputStream inputStream, int size,byte[] cache)
			throws IOException {
		int len = 0;
		int start = 0;
		int min = cache.length < size - start ? cache.length : size - start ;
		while ((len = inputStream.read(cache,0,min)) > 0) {
			start += len;
			if (start == size) {
				break;
			}
			min = cache.length < size - start ? cache.length : size - start ;
		}
		return cache;
	}

	/**
	 * read data to file.
	 * 
	 * @param inputStream
	 *            read from the inputstream.
	 * @param size
	 *            file size
	 * @param file
	 *            save the file path.
	 * @return the aim file.
	 * @throws IOException
	 */
	public static File readBuffer(InputStream inputStream, long size, File file)
			throws IOException {
		OutputStream os = null;
		os = new FileOutputStream(file);
		byte[] temp = new byte[Config.PACKAGE_SIZE];
		int len = 0;
		long start = 0;
		if (size - start <= Config.PACKAGE_SIZE) {
			temp = new byte[(int) (size - start)];
		}
		while ((len = inputStream.read(temp)) > 0) {
			os.write(temp);
			os.flush();
			start += len;
			if (start == size) {
				break;
			}
			if (size - start <= Config.PACKAGE_SIZE) {
				temp = new byte[(int) (size - start)];
			}
		}
		os.close();

		return file;
	}
	
	/**
	 * 
	 * @param inputStream
	 * @param size
	 * @param file
	 * @param cache
	 * @return
	 * @throws IOException
	 */
	public static void readBuffer(InputStream inputStream, int size, OutputStream os,byte[] cache)
			throws IOException {
		int len = 0;
		int start = 0;
		int min = cache.length < size - start ? cache.length : size - start ;
		while ((len = inputStream.read(cache,0,min))> 0) {
			os.write(cache,0,len);
			os.flush();
			start += len;
			if (start == size) {
				break;
			}
			min = cache.length < (size - start) ? cache.length : (size - start);
		}
		
	}
	
	public static boolean asyncPackageKey(byte[] source) {
		return asyncPackageData2(source,Config.TCP_KEY_START) == Config.KEY ;
	}
	
	/***
	 * 
	 * @param source
	 * @return
	 */
	public static int asyncPackageLengthData(byte[] source) {
		return asyncPackageData4(source, Config.TCP_LENGTH_START);
	}

	/***
	 * 
	 * @param source
	 * @param start
	 * @return
	 */
	public static long asyncPackageData8(byte[] source, int start) {
		long result = 0;
		result = source[start + 7] & 0xFF | (source[start + 6] & 0xFF) << 8
				| (source[start + 5] & 0xFF) << 16
				| (source[start + 4] & 0xFF) << 24
				| (source[start + 3] & 0xFF) << 32
				| (source[start + 2] & 0xFF) << 40
				| (source[start + 1] & 0xFF) << 48
				| (source[start + 0] & 0xFF) << 56;

		return result;
	}

	/***
	 * 
	 * @param source
	 * @param start
	 * @return
	 */
	public static int asyncPackageData4(byte[] source, int start) {
		int result = 0;
		result = source[start + 3] & 0xFF | (source[start + 2] & 0xFF) << 8
				| (source[start + 1] & 0xFF) << 16
				| (source[start] & 0xFF) << 24;

		return result;
	}

	/***
	 * 
	 * @param source
	 * @param start
	 * @return
	 */
	public static int asyncPackageData2(byte[] source, int start) {
		int result = 0;
		result = source[start + 1] & 0xFF | (source[start] & 0xFF) << 8;
		return result;
	}

	/***
	 * 
	 * @param source
	 * @return
	 */
	public static int asyncPackageGetTypeData(byte[] source) {
		return asyncPackageData2(source, Config.TCP_TYPE_START);
	}

	/***
	 * 
	 * @param type
	 * @param source
	 */
	public static void setPackageTypeData(int type, byte[] source) {
		setPackageData2(type, source, Config.TCP_TYPE_START);
	}

	/***
	 * 
	 * @param length
	 * @param source
	 */
	public static void setPackageLengthData(int length, byte[] source) {
		setPackageData4(length, source, Config.TCP_LENGTH_START);
	}

	/**
	 * 
	 * @param source
	 */
	public static void setPackageKeyData(byte[] source){
		setPackageData2(0xABFE, source, 0);
	}
	
	public static void setPackageVeersionData(byte[] source){
		setPackageData2(Config.VERSION, source, Config.TCP_VERSION_START);
	}
	/**
	 * 
	 * @param data
	 * @param source
	 * @param start
	 */
	public static void setPackageData8(long data, byte[] source, int start) {
		source[start + 7] = (byte) (data & 0xFF);
		source[start + 6] = (byte) (data >> 8 & 0xFF);
		source[start + 5] = (byte) (data >> 16 & 0xFF);
		source[start + 4] = (byte) (data >> 24 & 0xFF);
		source[start + 3] = (byte) (data >> 32 & 0xFF);
		source[start + 2] = (byte) (data >> 40 & 0xFF);
		source[start + 1] = (byte) (data >> 48 & 0xFF);
		source[start + 0] = (byte) (data >> 56 & 0xFF);
	}

	/**
	 * 
	 * @param data
	 * @param source
	 * @param start
	 */
	public static void setPackageData4(int data, byte[] source, int start) {
		source[start + 3] = (byte) (data & 0xFF);
		source[start + 2] = (byte) (data >> 8 & 0xFF);
		source[start + 1] = (byte) (data >> 16 & 0xFF);
		source[start + 0] = (byte) (data >> 24 & 0xFF);
	}

	/**
	 * 
	 * @param data
	 * @param source
	 * @param start
	 */
	public static void setPackageData2(int data, byte[] source, int start) {
		source[start + 1] = (byte) (data & 0xFF);
		source[start + 0] = (byte) (data >> 8 & 0xFF);
	}
	
	/**
	 * 
	 * @param data
	 * @param source
	 * @param start
	 */
	public static void setPackageData1(int data, byte[] source, int start) {
		source[start + 0] = (byte) (data >> 8 & 0xFF);
	}
	/**
	 * 
	 * @param data
	 * @param source
	 * @param start
	 */
	public static void setPackageData1(byte data, byte[] source, int start) {
		source[start + 0] = (byte) (data >> 8 & 0xFF);
	}
	/**
	 * 
	 * @param data
	 * @param source
	 * @param start
	 */
	public static void setPackageData1(char data, byte[] source, int start) {
		source[start + 0] = (byte) (data >> 8 & 0xFF);
	}
}
