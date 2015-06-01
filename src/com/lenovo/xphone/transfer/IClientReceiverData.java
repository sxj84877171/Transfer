/***
 * 
 */
package com.lenovo.xphone.transfer;

import java.io.File;

/***
 * 
 * @author Elvis
 *
 */
public interface IClientReceiverData {

	/***
	 * 
	 * @param data
	 */
	public void onReceiver(byte[] data);
	
	/***
	 * 
	 * @param data
	 */
	public void onReceiver(String data);
	
	/***
	 * 
	 * @param data
	 */
	public void onReceiver(File data);
	
	public void onClose();
	
}
