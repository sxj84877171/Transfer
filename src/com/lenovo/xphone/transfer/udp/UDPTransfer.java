package com.lenovo.xphone.transfer.udp;

import java.io.File;

import com.lenovo.xphone.transfer.ITransfer;
import com.lenovo.xphone.transfer.ITransferListener;
import com.lenovo.xphone.transfer.RemoteDeviceInfo;

public class UDPTransfer implements ITransfer{

	@Override
	public int registerListener(ITransferListener listener) {
		return 0;
	}

	@Override
	public int unregisterListener(ITransferListener listener) {
		return 0;
	}

	@Override
	public int connect(RemoteDeviceInfo deviceInfo) {
		return 0;
	}

	@Override
	public int Disconnect() {
		return 0;
	}

	@Override
	public int send(String message) {
		return 0;
	}

	@Override
	public int send(byte[] message) {
		return 0;
	}

	@Override
	public int send(File message) {
		return 0;
	}

	@Override
	public boolean isConnected() {
		return false;
	}

}
