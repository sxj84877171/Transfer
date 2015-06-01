package com.lenovo.xphone.transfer;

import java.io.IOException;

public interface IConnection {

	public void connect() throws IOException;

	public void close() throws IOException;
}
