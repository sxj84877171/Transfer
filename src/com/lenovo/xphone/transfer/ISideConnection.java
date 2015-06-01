package com.lenovo.xphone.transfer;

import java.io.IOException;
import java.util.List;


public interface ISideConnection extends IConnection{

	public void connect()throws IOException;
	
	public void close() throws IOException;
	
	public void setConnectListener(ConnectionListener listener);

	public List<IConnection> getConnection();
}
