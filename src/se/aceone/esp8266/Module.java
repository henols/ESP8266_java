package se.aceone.esp8266;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import purejavacomm.SerialPort;

public class Module {

	private static final String AT_CIPSTATUS = "AT+CIPSTATUS";
	private static final String AT_GMR = "AT+GMR";
	public static final String AT_CWLAP = "AT+CWLAP";
	public static final String AT_CIPSTART = "AT+CIPSTART";
	public static final String AT_CIFSR = "AT+CIFSR";
	public static final String AT_CWMODE = "AT+CWMODE";
	public static final String AT_CWJAP = "AT+CWJAP";
	public static final String AT = "AT";
	public static final String AT_CIPMUX = "AT+CIPMUX";
	public static final String AT_RST = "AT+RST";

	private boolean initialised = false;
	private boolean connected = false;
	private SerialPort port;
	private PrintWriter output;
	private BufferedInputStream input;

	private final static int TIME_OUT = 10000;

	private static final String BUSY = "busy";
	private static final String ERROR = "ERROR";
	private static final String ERROR_1 = "error";
//	private static final String[] ERRORS = {  ERROR, BUSY, ERROR_1 };
	private static final String[] ERRORS = {  ERROR,  ERROR_1 };

	static final String READY_1 = "ready";
	static final String READY_2 = "Ready";
	static final String READY[] = {READY_1, READY_2};
	static final String OK = "OK";
	static final String ALREAY_CONNECT = "ALREAY CONNECT";
	static final String LINKED = "Linked";
	static final String NO_CHANGE = "no change";

//	static final String[] GOOD = { OK, READY };

	char[] buf = new char[128];

	/* *********************************************************************** */
	/*                                                                         */
	/* CONSTRUCTORS */
	/*                                                                         */
	/* *********************************************************************** */

	public Module() {
		initialised = false;
	}

	/* *********************************************************************** */
	/*                                                                         */
	/* PUBLIC FUNCTIONS */
	/*                                                                         */
	/* *********************************************************************** */

	boolean begin(SerialPort port) throws IOException {
		debugPrintln(("begin " + port.getBaudRate()).toCharArray());
		if (initialised) {
			return true;
		}

		this.port = port;
		output = new PrintWriter(port.getOutputStream());
		input = new BufferedInputStream(port.getInputStream());
		initialised = true;
		String cmd = AT;
		write(cmd);
		int s = read(buf, new String[] { OK });
		if (s <= 0) {
			return false;
		}
		write(AT_CWMODE + "=1");
		s = read(buf, new String[] { OK, NO_CHANGE });
		if (s <= 0) {
			return false;
		}

		return true;
	}

	boolean reset() throws IOException {
		if (!initialised) {
			return false;
		}
		write(AT_RST);
		int s = 0;
		int[] found = new int[1];
		while (found[0] == 0) {
			int ss = read(buf,  READY , found);
			System.out.println("Size: " + ss);
			if(ss >0){
				System.out.println(new String(buf, 0, ss));
			}
			s += ss;
		}
		System.out.println("Total Size: " + s);
		return s >= 0;
	}

	boolean version(int[] version) throws Exception {
		if (!initialised) {
			return false;
		}
		write(AT_GMR);
		int s = read(buf, new String[] { OK });
		if (s <= 0) {
			return false;
		}
		System.out.println(new String(buf, 0, s));

		int ind = indexOf(buf, s, AT_GMR);
		for (int i = 0; i < 5; i++) {
			int index = ind + 9 + i * 2;
			// strtol(buf[index],buf[index+2],10);
			char[] cc = new char[2];
			for (int j = 0; j < 2; j++) {
				cc[j] = buf[index + j];
			}
			String s2 = new String(cc);
			version[i] = Integer.parseInt(s2);
		}

		return true;
	}

	private int indexOf(char[] buf, int len, String val) {
		return indexOf(buf, len, val, 0);
	}

	private int indexOf(char[] buf, int len, String val, int pos) {
		int valLen = val.length();
		char[] v = val.toCharArray();
		for (int i = pos; i < len - valLen; i++) {
			int j;
			for (j = 0; j < valLen; j++) {
				if (buf[i + j] != v[j]) {
					j = -1;
					break;
				}
			}
			if (j == valLen) {
				return i;
			}
		}

		return -1;
	}

	void stop() {
		if (!initialised) {
			return;
		}

	}

	boolean disconnect() {
		if (!initialised) {
			return false;
		}
		return true;
	}

	boolean getMacAddress(char[] address) throws IOException {
		if (!initialised) {
			return false;
		}
		return true;
	}

	void getListAPs() throws IOException {
		write(AT_CWLAP);
		int[] found = new int[1];
		while(found[0]==0){
		int pos = read(buf, new String[] { OK }, found);
		debugPrint(buf, pos);
		}
		System.out.println();;
	}

	boolean getIPAddress(int[] ip) throws IOException {
		write(AT_CIFSR);
		int s = read(buf, new String[] { "." });
		if (s <= 0) {
			return false;
		}
		while (available() > 0) {
			buf[s++] = (char) input.read();
		}
		int ind = indexOf(buf, s, AT_CIFSR) + 11;

		for (int i = 0; i < 4; i++) {
			int lastInd = indexOf(buf, s, ".", ind);
			if (lastInd < 0) {
				lastInd = s - 8;
			}
			// strtol(buf[ind],buf[LastInd],10);
			ip[i] = Integer.parseInt(new String(buf, ind, lastInd - ind));
			ind = lastInd + 1;
		}
		return true;
	}

	boolean connectToAP(String ssid, String key, int attempts) throws Exception {
		if (!initialised) {
			return false;
		}

		String cmd = AT_CWJAP + "=\"";
		cmd += new String(ssid);
		cmd += "\",\"";
		cmd += new String(key);
		cmd += "\"";
		write(cmd);
		int s = read(buf, new String[] { OK });
		// System.out.println(s);
		if (s <= 0) {
			return false;
		}

		cmd = AT_CIPMUX + "=1";
		write(cmd);
		s = read(buf, new String[] { OK });
		if (s <= 0) {
			return false;
		}
		long timeOut = System.currentTimeMillis() + TIME_OUT;
		while (!getIPAddress(new int[4])) {
			if (timeOut < System.currentTimeMillis()) {
				return false;
			}
			Thread.sleep(1000);
		}
		connected = true;
		return true;
	}

	void checkConnectionStatus() throws IOException {
		// AT+CIPSTATUS
		//
		// STATUS:3
		// +CIPSTATUS:1,"TCP","77.53.152.17",80,0
		// +CIPSTATUS:2,"TCP","109.105.111.14",80,0
		// +CIPSTATUS:3,"TCP","80.76.152.133",80,0
		// +CIPSTATUS:4,"TCP","144.63.250.10",80,0
		//
		// OK

		String cmd = AT_CIPSTATUS;
		write(cmd);

		boolean[] con = new boolean[4];
		while (true) {
			int s = read(buf, new String[] { OK, "+CIPSTATUS:" });
			if (endsWith(buf, s, "+CIPSTATUS:")) {
				con[input.read() - '0' -1] = true;
			} else {
				break;
			}
		}

		for (int i = 0; i < 4; i++) {
			if (clients[i] != null) {
				clients[i].setConnected(con[i]);
			}
		}
	}

	boolean checkConnected() throws IOException {
		return connected;
	}

	Client[] clients = new Client[4];
	private boolean bufferToSmal;
	private int clientId;
	private int remainingSize;

	Client connectTCP(String destIP, int destPort) throws IOException {
		return connect(destIP, destPort, "TCP");
	}

	Client connectUDP(String destIP, int destPort) throws IOException {
		return connect(destIP, destPort, "UDP");
	}

	Client connect(String destIP, int destPort, String type) throws IOException {
		if (!connected) {
			return null;
		}
		int clientNr = 0;
		for (int i = 0; i < 4; i++) {
			if (clients[i] == null || !clients[i].connected()) {
				clientNr = i;
				break;
			}
		}
		String cmd = AT_CIPSTART + "=" + (clientNr + 1) + ",\"" + type + "\",\"";
		cmd += destIP;
		cmd += "\"," + destPort;
		write(cmd);
		int s = read(buf, new String[] { LINKED });
		if (s <= 0) {
			return null;
		}
		// System.out.println(s);
		clients[clientNr] = new Client(this, clientNr + 1);
		return clients[clientNr];
	}

	int read(char[] res, String[] good) throws IOException {
		return read(res, good, new int[1]);
	}

	int read(char[] res, String[] good, int[] found) throws IOException {
		int rSise = res.length;
		int pos = 0;
		long timeOut = System.currentTimeMillis() + TIME_OUT;

		while (timeOut > System.currentTimeMillis()) {
			while (input.available() > 0) {
				if(bufferToSmal){
					if(readToClientBuffer()){
						found[0]=0;
						return 0;
					};
				}
				
				res[pos++] = (char) input.read();
				if (endsWith(res, pos, "+IPD,") && input.available() > 3) {
					clientId = ((char) input.read()) - '0' - 1;
					input.read();
					remainingSize = 0;
					char c;
					while ((c = (char) input.read()) != ':') {
						remainingSize *= 10;
						remainingSize += (c - '0');
					}
					if(readToClientBuffer()){
						found[0]=0;
						return pos;
					};
				}
				if (endsWith(res, pos, ERRORS)) {
					debugPrintln(res, pos);
					return -1;
				}
				if (endsWith(res, pos, good) /* || rSise-1 <= pos */) {
					found[0] = 1;
					return pos;
				}
				if (rSise - 1 <= pos) {
					found[0] = 0;
					return pos;
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
		debugPrint("Time out ".toCharArray());
		debugPrint(res, pos);
		return -2;
	}

	boolean readToClientBuffer() throws IOException {
		System.out.println();
		System.out.println("=========================");
		System.out.println("remainingSize: "+remainingSize);
		System.out.println("=========================");
		int bufSize = clients[clientId].allocateBuffer(remainingSize);
		bufferToSmal = bufSize < remainingSize;
		remainingSize = remainingSize - bufSize;
		for (int i = 0; i < bufSize; i++) {
			clients[clientId].addToBuffer(input.read());
		}
		return bufferToSmal;
	}

	private boolean endsWith(char[] res, int pos, String[] texts) {
		for (String text : texts) {
			if (endsWith(res, pos, text)) {
				return true;
			}
		}
		return false;
	}

	private boolean endsWith(char[] res, int pos, String text) {
		String r = new String(res, 0, pos);
		return r.endsWith(text);
	}

	public void write(String cmd) {
		debugPrintln(cmd.toCharArray());
		output.println(cmd);
		output.flush();
	}

	public void query(String cmd) throws IOException {
		write(cmd + '?');
		int ind = read(buf, new String[] { OK });
		debugPrintln(buf,ind);
	}

	public int available() throws IOException {
		return input.available();
	}

	void debugPrintln(char[] str) {
		debugPrintln(str, str.length);
	}

	void debugPrintln(char[] str, int pos) {
		System.out.println("d: " + new String(str, 0, pos));
	}

	void debugPrint(char[] str) {
		debugPrint(str, str.length);
	}

	void debugPrint(char[] str, int pos) {
		System.out.print(new String(str, 0, pos));
	}

}
