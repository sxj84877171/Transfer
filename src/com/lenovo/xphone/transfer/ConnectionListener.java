package com.lenovo.xphone.transfer;


public interface ConnectionListener {

	public void onNewConnection(IConnection connection);
	
	public void onError();
}
