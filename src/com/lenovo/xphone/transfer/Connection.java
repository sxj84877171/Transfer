/***
 * 
 */
package com.lenovo.xphone.transfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.lenovo.xphone.transfer.file.FileUtils;
import com.lenovo.xphone.transfer.log.FLog;
import com.lenovo.xphone.transfer.rule.Config;
import com.lenovo.xphone.transfer.util.TransferUtil;

/***
 * 
 * @author Elvis
 * 
 */
public class Connection implements IConnection {

	private String TAG = Connection.class.getSimpleName();

	private Socket socket;
	private boolean stop = false;
	private IClientReceiverData receiver;
	private OutputStream outputStream;
	private InputStream inputStream = null;
	private boolean isClose = false;
	private Map<String,String> nameMap = new HashMap<String,String>();

	public Connection(Socket socket) {
		this.socket = socket;
		stop = false;
		writeThread.start();
		readThread.start();
		handler = new Handler(writeThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				byte[] packet = msg.getData().getByteArray("packet");
				int length = msg.getData().getInt("length");
				if (packet != null) {
					try {
						if (outputStream != null) {
							outputStream.write(packet, 0, length);
							outputStream.flush();
							handler.removeCallbacks(run);
							handler.postDelayed(run, Config.HEARTBEAT_TIME);
						} else {

						}
					} catch (IOException e) {
						onClose(e);
					}
				}
			}
		};
		handler.postDelayed(run, Config.HEARTBEAT_TIME);
	}

	/**
	 * 
	 */
	private Thread readThread = new Thread() {
		public void run() {
			try {
				inputStream = socket.getInputStream();
				byte[] cache = new byte[Config.PACKAGE_SIZE];
				while (!stop) {
					byte[] header = TransferUtil.readBuffer(inputStream,
							Config.TCP_HEADER_SIZE, cache);
					boolean isInvald = TransferUtil.asyncPackageKey(header);
					if (!isInvald) {
						onClose(new IOException("Invalued package"));
						break;
					}
					int bodyLength = TransferUtil
							.asyncPackageLengthData(header);
					int type = TransferUtil.asyncPackageGetTypeData(header);
					if (type == Config.PACKAGE_TYPE_BYTE) {
						byte[] body = TransferUtil.readBuffer(inputStream,
								bodyLength, cache);
						if (receiver != null) {
							receiver.onReceiver(body);
						}
					} else if (type == Config.PACKAGE_TYPE_STRING) {
						byte[] body = TransferUtil.readBuffer(inputStream,
								bodyLength, cache);
						if (receiver != null) {
							receiver.onReceiver(new String(body));
						}
					} else if (type == Config.PACKAGE_TYPE_FILE) {
						header = TransferUtil.readBuffer(inputStream,
								Config.TCP_HEADER_SIZE, cache);
						int fileNameLenght = TransferUtil.asyncPackageData2(header, Config.FILE_NAME_START);
						int length =  TransferUtil.asyncPackageData2(header, Config.FILE_LENGTH_START);
						int curPos = TransferUtil.asyncPackageData2(header, Config.FILE_CURRENT_LENGTH_START);
						int curLength = TransferUtil.asyncPackageData2(header, Config.FILE_SIZE_LENGTH_START);
						int pkgIndex = TransferUtil.asyncPackageData2(header, Config.FILE_PACKAGE_INDEX_START);
						String fileName = new String(TransferUtil.readBuffer(inputStream, fileNameLenght, cache));
						String tempName =FileUtils.getTempFilename(fileName);
//						if(nameMap.get(fileName))
//						File tempFile = new File(fileName);
//						if (receiver != null) {
//							receiver.onReceiver(tempFile);
//						}
					}
				}

			} catch (IOException e) {
				onClose(e);
			}
		}
	};

	/**
	 * 
	 */
	private HandlerThread writeThread = new HandlerThread("SendData") {
		@Override
		protected void onLooperPrepared() {
			try {
				outputStream = socket.getOutputStream();
			} catch (IOException e) {
				onClose(e);
			}
			super.onLooperPrepared();
		}
	};

	/**
	 * 
	 */
	private Handler handler;

	/**
	 * 
	 * @param data
	 */
	public synchronized void send(byte[] data) {
		TransferUtil.setPackageKeyData(header);
		TransferUtil.setPackageVeersionData(header);
		TransferUtil.setPackageLengthData(data.length, header);
		TransferUtil.setPackageTypeData(Config.PACKAGE_TYPE_BYTE, header);
		if (Config.TCP_HEADER_SIZE + data.length > cacheLenght) {
			cacheLenght = Config.TCP_HEADER_SIZE + data.length;
			sendPacket = new byte[cacheLenght];
		}
		System.arraycopy(header, 0, sendPacket, 0, Config.TCP_HEADER_SIZE);
		System.arraycopy(data, 0, sendPacket, Config.TCP_HEADER_SIZE,
				data.length);
		Message msg = new Message();
		Bundle extras = new Bundle();
		extras.putByteArray("packet", sendPacket);
		extras.putInt("length", Config.TCP_HEADER_SIZE + data.length);
		msg.setData(extras);
		if (handler != null) {
			handler.sendMessage(msg);
		}

	}

	private void sendHeartBeat() {
		if (hearbeat == null) {
			hearbeat = new byte[Config.TCP_HEADER_SIZE];
			TransferUtil.setPackageKeyData(hearbeat);
			TransferUtil.setPackageVeersionData(hearbeat);
			TransferUtil.setPackageLengthData(0, hearbeat);
			TransferUtil.setPackageTypeData(Config.PACKAGE_TYPE_HEARTBEAT,
					hearbeat);
		}
		Message hearbeatMessage = new Message();
		Bundle extras = new Bundle();
		extras.putByteArray("packet", hearbeat);
		extras.putInt("length", Config.TCP_HEADER_SIZE);
		hearbeatMessage.setData(extras);
		if (handler != null) {
			handler.sendMessage(hearbeatMessage);
		}
	}

	private byte[] hearbeat;

	private Runnable run = new Runnable() {

		@Override
		public void run() {
			sendHeartBeat();
			if (handler != null) {
				handler.postDelayed(run, Config.HEARTBEAT_TIME);
			}
		}
	};

	private int cacheLenght = Config.PACKAGE_SIZE;
	private byte[] sendPacket = new byte[Config.PACKAGE_SIZE];
	private byte[] header = new byte[Config.TCP_HEADER_SIZE];

	/**
	 * 
	 * @param data
	 */
	public synchronized void send(String data) {
		TransferUtil.setPackageKeyData(header);
		TransferUtil.setPackageVeersionData(header);
		TransferUtil.setPackageLengthData(data.getBytes().length, header);
		TransferUtil.setPackageTypeData(Config.PACKAGE_TYPE_STRING, header);
		byte[] bData = data.getBytes();
		if (Config.TCP_HEADER_SIZE + bData.length > cacheLenght) {
			cacheLenght = Config.TCP_HEADER_SIZE + bData.length;
			sendPacket = new byte[cacheLenght];
		}
		System.arraycopy(header, 0, sendPacket, 0, Config.TCP_HEADER_SIZE);
		System.arraycopy(bData, 0, sendPacket, Config.TCP_HEADER_SIZE,
				bData.length);
		Message msg = new Message();
		Bundle extras = new Bundle();
		extras.putByteArray("packet", sendPacket);
		extras.putInt("length", Config.TCP_HEADER_SIZE + bData.length);
		msg.setData(extras);
		if (handler != null) {
			handler.sendMessage(msg);
		}
	}

	/**
	 * 
	 * @param data
	 */
	public synchronized void send(File data) {
//		TransferUtil.setPackageKeyData(header);
//		TransferUtil.setPackageVeersionData(header);
//		TransferUtil.setPackageLengthData(data.length(), header);
//		TransferUtil.setPackageTypeData(Config.PACKAGE_TYPE_FILE, header);
	}

	public void close() throws IOException {
		stop = true;
		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
		if (outputStream != null) {
			outputStream.close();
			outputStream = null;
		}

		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

	public void setReceiver(IClientReceiverData receiver) {
		this.receiver = receiver;
	}

	private void onClose(IOException e) {
		if (!isClose) {
			isClose = true;
			stop = true;
			if (receiver != null) {
				receiver.onClose();
			}
			try {
				close();
			} catch (IOException e1) {
				FLog.e(TAG, e1);
			}
			handler.removeCallbacks(run);
			writeThread.quit();
		}
		FLog.e(TAG, e);
	}

	@Override
	public void connect() throws IOException {

	}
}
