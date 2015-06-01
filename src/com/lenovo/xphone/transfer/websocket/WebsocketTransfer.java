package com.lenovo.xphone.transfer.websocket;

import java.io.File;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.lenovo.xphone.transfer.ITransfer;
import com.lenovo.xphone.transfer.ITransferListener;
import com.lenovo.xphone.transfer.RemoteDeviceInfo;
import com.lenovo.xphone.transfer.log.FLog;

public class WebsocketTransfer implements ITransfer {

	public WebsocketTransfer() {

	}

	public static final String TAG = "WebsocketTransfer";

	private WebSocketClient websocket = null;
	private ITransferListener listener;
	private boolean connected = false;

	@Override
	public int registerListener(ITransferListener listener) {
		this.listener = listener;
		return 0;
	}

	@Override
	public int unregisterListener(ITransferListener listener) {
		this.listener = null;
		return 0;
	}

	@Override
	public int connect(final RemoteDeviceInfo deviceInfo) {
		if (websocket == null) {
			websocket = new WebSocketClient(deviceInfo.websocketUri) {

				@Override
				public void onClose(int arg0, String arg1, boolean arg2) {
					if (listener != null){
						listener.onDisconnect(0);
					}
					connected = false;
				}

				@Override
				public void onError(Exception arg0) {
					FLog.e(TAG, arg0);
				}

				@Override
				public void onMessage(String message) {
					if (listener != null){
						listener.onRecv(message);
					}
				}

				@Override
				public void onOpen(ServerHandshake arg0) {
					if (listener != null){
						listener.onConnect(0);
					}
					connected = true;
				}
			};
			websocket.connect();
			return 0;
		}
		return 1;
	}

	@Override
	public int Disconnect() {
		websocket.close();
		return 0;
	}

	@Override
	public int send(String message) {
		websocket.send(message);
		return 0;
	}

	@Override
	public int send(byte[] message) {
		websocket.send(message);
		return 0;
	}

	@Override
	public int send(File message) {
		return 0;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

}
