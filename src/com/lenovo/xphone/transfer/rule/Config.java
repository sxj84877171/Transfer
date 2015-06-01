package com.lenovo.xphone.transfer.rule;

public class Config {

	public static final int TCP_CONNECTION_TIMEOUT = 50000;//tcp连接超时时间 50s
	public static final int TCP_PACKAGE_READ_SIZE = 256;
	public static final int TCP_HEADER_SIZE = 10;//协议包头大小

	public static final int TCP_KEY_START = 0 ;//协议校验码位置
	public static final int TCP_VERSION_START = 2;//协议版本位置
	public static final int TCP_TYPE_START = 4;//协议类型位置
	public static final int TCP_LENGTH_START = 6;//数据包长度位置

	public static final int PACKAGE_SIZE = 1024 * 64;//缓冲区大小

	public static final int PACKAGE_TYPE_HEARTBEAT = 0;//协议类型 心跳标志
	public static final int PACKAGE_TYPE_BYTE = 1;//协议类型 数据标志
	public static final int PACKAGE_TYPE_STRING = 10001;//扩增详细协议  字符串类型
	public static final int PACKAGE_TYPE_FILE = 10002;//扩增详细协议 文件类型
	
	public static final int HEARTBEAT_TIME = 30 ;//心跳间隔时间
	
	public static final int KEY = 0xABFE ;//key 关键字
	
	public static final int VERSION = 1 ;//当前版本号
	
	public static final int FILE_NAME_START = 0 ;
	public static final int FILE_LENGTH_START = 2;
	public static final int FILE_CURRENT_LENGTH_START = 4 ;
	public static final int FILE_SIZE_LENGTH_START = 6 ;
	public static final int FILE_PACKAGE_INDEX_START = 8 ;

}
