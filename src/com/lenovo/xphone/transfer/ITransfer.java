/**
 * 
 */
package com.lenovo.xphone.transfer;

import java.io.File;

/**
 * @author Elvis
 *
 */
public interface ITransfer {

	/**
	 * 
	 * @param listener
	 * @return
	 */
	public int registerListener(ITransferListener listener);
	
	/**
	 * 
	 * @param listener
	 * @return
	 */
	public int unregisterListener(ITransferListener listener) ;
	
	/**
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public int connect(RemoteDeviceInfo deviceInfo) ;
	
	/**
	 * 
	 * @return
	 */
	public int Disconnect();
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public int send(String message);
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public int send(byte[] message);
	/**
	 * 
	 * @param message
	 * @return
	 */
	public int send(File message);
	
	/**
	 * 
	 * @return
	 */
	public boolean isConnected();
}
