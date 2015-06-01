/***
 * 
 */
package com.lenovo.xphone.transfer.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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
public class TcpClient implements ISideConnection{

	private static final String TAG = "TcpClient";

	private String ip;
	private int port;
	private Socket socket;
	private Thread thread;
	private IConnection connection;
	private ConnectionListener connectListener ;

	public TcpClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	private Runnable run = new Runnable() {
		@Override
		public void run() {
			try {
				socket = new Socket(ip, port);
				socket.setSoTimeout(Config.TCP_CONNECTION_TIMEOUT);
				connection = new Connection(socket);
				if(connectListener != null){
					connectListener.onNewConnection(connection);
				}
			} catch (UnknownHostException e) {
				if(connectListener != null){
					connectListener.onError();
				}
				FLog.e(TAG, e);
			} catch (IOException e) {
				if(connectListener != null){
					connectListener.onError();
				}
				FLog.e(TAG, e);
			} catch (Exception e) {
				if(connectListener != null){
					connectListener.onError();
				}
				FLog.e(TAG, e);
			}
		}
	};
	
	public void setConnectListener(ConnectionListener listener){
		this.connectListener = listener ;
	}

	public List<IConnection> getConnection() {
		List<IConnection> result = new ArrayList<IConnection>();
		result.add(connection);
		return result;
	}

	@Override
	public void connect() {
		thread = new Thread(run);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	@Override
	public void close() throws IOException {
		if(connection != null){
			connection.close();
		}
	}

}
