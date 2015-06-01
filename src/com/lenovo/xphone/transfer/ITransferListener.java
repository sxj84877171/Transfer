/***
 * 
 */
package com.lenovo.xphone.transfer;


/**
 * @author Elvis
 *
 */
public interface ITransferListener {
	
	/***
	 * 
	 */
	public static final int ERROR_SUCCESS = 0 ;

	/***
	 * 
	 * @param errorCode
	 */
	public void onConnect(int errorCode);
	
	/***
	 * 
	 * @param disconnectReason
	 */
	public void onDisconnect(int disconnectReason) ;
	
	/***
	 * 
	 * @param errorCode
	 */
	public void onError(int errorCode);
	
	/***
	 * 
	 * @param message
	 */
	public void onRecv(String message);

	/**
	 * 
	 * @param message
	 */
	public void onRecv(byte[] message);
	
}
