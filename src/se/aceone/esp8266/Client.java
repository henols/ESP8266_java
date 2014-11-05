package se.aceone.esp8266;

import java.io.IOException;

public class Client {

	private static final String AT_CIPCLOSE = "AT+CIPCLOSE";
	private static final String SEND_DATA = ">";
	private static final String SEND_OK = "SEND OK";
	private static final String AT_CIPSEND = "AT+CIPSEND";
	private static final int BUFFER_SIZE = 128;
	
	
	private int id;
	private Module module;
	private boolean connected = false;

	int[] buffer = new int[BUFFER_SIZE];
	private int writePos;
	private int readPos;

	Client() {
		id = -1;
	}

	Client(Module module, int id) {
		this.module = module;
		this.id = id;
		connected = true;
	}

	boolean connected() throws IOException {
		if(id < 0){
			return false;
		}
		module.checkConnectionStatus();
		return connected;

	}

	int write(char[] buf, int len) throws IOException {
		if (!connected()) {
			return -1;
		}
		// return write(buf, len, 0);

		module.write(AT_CIPSEND + "=" + id + "," + (len+1));
		int s = module.read(module.buf, new String[] { SEND_DATA });
		System.out.println("---------- "+s);
		module.debugPrint(buf, s);
		module.write(new String(buf, 0, len));
		s = module.read(module.buf, new String[] { SEND_OK });
		System.out.println(s);
		return len;
	}

	int write(char c) throws IOException {
		if (!connected()) {
			return -1;
		}
		return 0;
	}

	int read(char[] buf, int len) throws IOException {
		if (!connected()) {
			return -1;
		}
		// return read(buf, len, 0);
		return 0;

	}

	int close() throws IOException {
		module.write(AT_CIPCLOSE + "=" + id);
		System.out.println(module.read(module.buf,new String[] { Module.OK }));
		connected = false;
		return 0;
	}

	void stop() throws IOException {
		close();
	}

	int read() throws IOException {
		if (!connected) {
			// if(!connected()){
			return -1;
		}

		return buffer[readPos++];
	}

	int available() throws IOException {
		if (!connected) {
			return -1;
		}
		int available = writePos - readPos;
		if (available > 0) {
			return available;
		}
		if (module.available() > 0) {
			module.read(module.buf,new String[] { Module.OK });
		}
		return writePos - readPos;
	}

	void flush() {
		// No flush implementation, unclear if necessary.
	}

	int peek() throws IOException {
		if (!connected) {
			return -1;
		}
		return buffer[readPos];
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	int allocateBuffer(int size) {
		if (writePos - readPos <= 0) {
			this.writePos = 0;
			this.readPos = 0;
		}
		if (size < BUFFER_SIZE - writePos) {
			return size;
		}
		return BUFFER_SIZE - writePos;
	}

	public void addToBuffer(int b) {
		buffer[writePos++] = b;
	}

}
