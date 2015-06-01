package com.lenovo.xphone.transfer.tcp;

/***
 * 
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.lenovo.xphone.transfer.Connection;
import com.lenovo.xphone.transfer.ConnectionListener;
import com.lenovo.xphone.transfer.IConnection;
import com.lenovo.xphone.transfer.ISideConnection;
import com.lenovo.xphone.transfer.log.FLog;
import com.lenovo.xphone.transfer.rule.Config;

/***
 * 
 * @author Elvis
 * 
 */
public class TcpServer implements ISideConnection {
	private String TAG = TcpServer.class.getSimpleName();
	private int port;
	private ServerSocket sSocket = null;
	private Thread thread;
	private List<IConnection> connectList = new ArrayList<IConnection>();
	private Runnable run = new Runnable() {

		@Override
		public void run() {
			try {
				sSocket = new ServerSocket(port);
				while (!sSocket.isClosed() && null != sSocket) {
					Socket socket = sSocket.accept();
					socket.setSoTimeout(Config.TCP_CONNECTION_TIMEOUT);
					Connection connection = new Connection(
							socket);
					connectList.add(connection);
					if (connectListener != null) {
						connectListener.onNewConnection(connection);
					}
				}
			} catch (IOException e) {
				if(connectListener != null){
					connectListener.onError();
				}
				FLog.e(TAG, e);
			}catch (Exception e) {
				if(connectListener != null){
					connectListener.onError();
				}
				FLog.e(TAG, e);
			}
		}
	};

	public TcpServer(int port) {
		this.port = port;
	}

	public void close() throws IOException {
		if (sSocket != null) {
			sSocket.close();
			sSocket = null;
		}
	}

	public List<IConnection> getConnection() {
		return connectList;
	}

	private ConnectionListener connectListener;

	public void setConnectListener(ConnectionListener listener) {
		this.connectListener = listener;
	}

	@Override
	public void connect() {
		thread = new Thread(run);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
}
