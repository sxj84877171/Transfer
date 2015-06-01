/**
 *  TCP/IP  
        Transmission Control Protocol
        Internet Protocol
 */
package com.lenovo.xphone.transfer.tcp;

import java.io.File;
import java.io.IOException;

import com.lenovo.xphone.transfer.Connection;
import com.lenovo.xphone.transfer.ConnectionListener;
import com.lenovo.xphone.transfer.IClientReceiverData;
import com.lenovo.xphone.transfer.IConnection;
import com.lenovo.xphone.transfer.ISideConnection;
import com.lenovo.xphone.transfer.ITransfer;
import com.lenovo.xphone.transfer.ITransferListener;
import com.lenovo.xphone.transfer.RemoteDeviceInfo;
import com.lenovo.xphone.transfer.Role;
import com.lenovo.xphone.transfer.log.FLog;

/**
 * 
 * @author Elvis
 * 
 */
public class TCPTransfer implements ITransfer {
	
	static{
		FLog.init("tcp.log");
	}

	private Role role;
	private ISideConnection client;
	private Connection connection ;
	private boolean connected = false;

	private ITransferListener transferListener;

	/***
	 * 
	 * @param ip
	 * @param port
	 * @param role
	 */
	public TCPTransfer(Role role) {
		this.role = role;
	}

	@Override
	public int registerListener(ITransferListener listener) {
		transferListener = listener;
		return 0;
	}

	/**
	 * 
	 */
	@Override
	public int unregisterListener(ITransferListener listener) {
		transferListener = null;
		return 0;
	}

	/***
	 * 
	 */
	@Override
	public int connect(RemoteDeviceInfo deviceInfo) {
		if (this.role == Role.Client) {
			client = new TcpClient(deviceInfo.ip, deviceInfo.port);
		} else if (this.role == Role.Server) {
			client = new TcpServer(deviceInfo.port);
		}
		client.setConnectListener(connectListener);
		try {
			client.connect();
		} catch (IOException e) {
			return 1;
		}
		return 0;
	}

	private ConnectionListener connectListener = new ConnectionListener() {

		@Override
		public void onNewConnection(IConnection connection) {
			TCPTransfer.this.connection = (Connection)connection ;
			TCPTransfer.this.connection.setReceiver(new IClientReceiverData() {
				
				@Override
				public void onReceiver(File data) {
					if(transferListener != null){
					}
				}
				
				@Override
				public void onReceiver(String data) {
					if(transferListener != null){
						transferListener.onRecv(data);
					}
				}
				
				@Override
				public void onReceiver(byte[] data) {
					if(transferListener != null){
						transferListener.onRecv(data);
					}
				}

				@Override
				public void onClose() {
					if(transferListener != null){
						transferListener.onDisconnect(0);
					}
					connected = false ;
				}
			});
			if(transferListener != null){
				transferListener.onConnect(0);
			}
			connected = true ;
		}

		@Override
		public void onError() {
			if(transferListener != null){
				transferListener.onDisconnect(0);
			}
			connected = false ;
		}
	};

	/***
	 * 
	 */
	@Override
	public int Disconnect() {
		try {
			if (client != null) {
				client.close();
			}
			client = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/***
	 * 
	 */
	@Override
	public int send(String message) {
		if(connection != null){
			connection.send(message);
		}
		return 0;
	}

	@Override
	public int send(byte[] message) {
		if(connection != null){
			connection.send(message);
		}
		return 0;
	}

	@Override
	public int send(File message) {
		if(connection != null){
			connection.send(message);
		}
		return 0;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

}
