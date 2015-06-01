package com.lenovo.xphone.transfer.file;

import java.io.OutputStream;

public class TransferFileBean {

	private String filename;
	private long length;
	private OutputStream os ;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

}
