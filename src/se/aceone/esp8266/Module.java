package se.aceone.esp8266;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import purejavacomm.SerialPort;

public class Module {

	public static final String AT_CWLAP = "AT+CWLAP";
	public static final String AT_CIPSTART = "AT+CIPSTART";
	public static final String AT_CIFSR = "AT+CIFSR";
	public static final String AT_CWMODE = "AT+CWMODE";
	public static final String AT_CWJAP = "AT+CWJAP";
	public static final String AT = "AT";
	public static final String AT_CIPMUX = "AT+CIPMUX";
	public static final String AT_RST = "AT+RST";

	enum Status {
		STATUS_DISCONNECTED, STATUS_SCANNING, STATUS_CONNECTING, STATUS_CONNECTED
	}

	//
	// #define CC3000_SUCCESS (0)
	// #define CHECK_SUCCESS(func,Notify,errorCode) {if ((func) !=
	// CC3000_SUCCESS) { CHECK_PRINTER CC3KPrinter->println(F(Notify)); return
	// errorCode;}}
	//
	// #define MAXSSID (32)
	// #define MAXLENGTHKEY (32) /* Cleared for 32 bytes by TI engineering
	// 29/08/13 */
	//
	// #define MAXidS 32 // can change this
	// boolean closedids[MAXidS] = { false, false, false, false };

	/* *********************************************************************** */
	/*                                                                         */
	/* PRIVATE FIELDS (SmartConfig) */
	/*                                                                         */
	/* *********************************************************************** */

	// class ESP8266BitSet {
	// public:
	// static const byte IsSmartConfigFinished = 0x01;
	// static const byte IsConnected = 0x02;
	// static const byte HasDHCP = 0x04;
	// static const byte OkToShutDown = 0x08;
	//
	// void clear() {
	// flags = 0;
	// }
	//
	// bool test(const byte flag) {
	// return (flags & flag) != 0;
	// }
	//
	// void set(const byte flag) {
	// flags |= flag;
	// }
	//
	// void reset(const byte flag) {
	// flags &= ~flag;
	// }
	// private:
	// volatile byte flags;
	// } esp8266Bitset;
	private boolean _initialised = false;
	private SerialPort port;
	private PrintWriter output;
	private BufferedInputStream input;

	private final static int RETRIES = 20;
	private static final String BUSY_NOW = "busy now ...";

	/* *********************************************************************** */
	/*                                                                         */
	/* CONSTRUCTORS */
	/*                                                                         */
	/* *********************************************************************** */

	public Module() {
		_initialised = false;
	}

	/* *********************************************************************** */
	/*                                                                         */
	/* PUBLIC FUNCTIONS */
	/*                                                                         */
	/* *********************************************************************** */

	boolean begin(SerialPort port) throws IOException {
		debugPrintln("begin " + port.getBaudRate());
		if (_initialised) {
			return true;
		}

		this.port = port;
		output = new PrintWriter(port.getOutputStream());
		input = new BufferedInputStream(port.getInputStream());
		_initialised = true;
		String cmd = AT;
		write(cmd);
		System.out.println(read());
		query(AT_CIPMUX);
		return true;
	}

	boolean reset() throws IOException {
		if (!_initialised) {
			return false;
		}
		write(AT_RST);
		System.out.println(read());
		System.out.println(read());
		System.out.println(read());
		return true;

		// wlan_stop();
		// delay(5000);
		// wlan_start(patch);
	}

	boolean version() throws IOException {
		if (!_initialised) {
			return false;
		}
		write("AT+GMR");
		System.out.println(read());
		return true;

		// wlan_stop();
		// delay(5000);
		// wlan_start(patch);
	}

	/**************************************************************************/
	/*
	 * !
	 * 
	 * @brief Stop CC3000
	 */
	/**************************************************************************/
	void stop() {
		if (!_initialised) {
			return;
		}

		// wlan_stop();
	}

	/**************************************************************************/
	/*
	 * !
	 * 
	 * @brief Disconnects from the network
	 * 
	 * @returns False if an error occured!
	 */
	/**************************************************************************/
	boolean disconnect() {
		if (!_initialised) {
			return false;
		}

		// long retVal = wlan_disconnect();

		// return retVal != 0 ? false : true;
		return true;
	}

	boolean getMacAddress(char[] address) throws IOException {
		if (!_initialised) {
			return false;
		}
		// CHECK_SUCCESS(nvmem_read(NVMEM_MAC_FILEID, 6, 0, address),
		// "Failed reading MAC address!", false);

		return true;
	}

	void getListAPs() throws IOException {
		write(AT_CWLAP);
		System.out.println(read());
	}
	void getIPAddress() throws IOException {
		write(AT_CIFSR);
		System.out.println(read());
	}

	// bool getIPAddress(uint32_t *retip, uint32_t *netmask, uint32_t *gateway,
	// uint32_t *dhcpserv, uint32_t *dnsserv) {
	// if (!_initialised)
	// return false;
	// if (!esp8266Bitset.test(ESP8266BitSet::IsConnected))
	// return false;
	// if (!esp8266Bitset.test(ESP8266BitSet::HasDHCP))
	// return false;
	//
	// // tNetappIpconfigRetArgs ipconfig;
	// // netapp_ipconfig(&ipconfig);
	// //
	// // /* If byte 1 is 0 we don't have a valid address */
	// // if (ipconfig.aucIP[3] == 0) return false;
	// //
	// // memcpy(retip, ipconfig.aucIP, 4);
	// // memcpy(netmask, ipconfig.aucSubnetMask, 4);
	// // memcpy(gateway, ipconfig.aucDefaultGateway, 4);
	// // memcpy(dhcpserv, ipconfig.aucDHCPServer, 4);
	// // memcpy(dnsserv, ipconfig.aucDNSServer, 4);
	//
	// return true;
	// }

	boolean getFirmwareVersion(char[] major, char[] minor) {
		char fwpReturn[] = new char[2];

		if (!_initialised) {
			return false;
		}

		// CHECK_SUCCESS(nvmem_read_sp_version(fwpReturn),
		// "Unable to read the firmware version", false);

		major[0] = fwpReturn[0];
		minor[0] = fwpReturn[1];

		return true;
	}

	/**************************************************************************/
	/*
	 * !
	 * 
	 * @Brief Prints out the current status flag of the CC3000
	 * 
	 * @note This command isn't available when the CC3000 is configured in
	 * 'CC3000_TINY_DRIVER' mode
	 */
	/**************************************************************************/
	Status getStatus() {
		if (!_initialised) {
			return Status.STATUS_DISCONNECTED;
		}

		int results = 0; // wlan_ioctl_statusget();

		switch (results) {
		case 1:
			return Status.STATUS_SCANNING;
		case 2:
			return Status.STATUS_CONNECTING;
		case 3:
			return Status.STATUS_CONNECTED;
		case 0:
		default:
			return Status.STATUS_DISCONNECTED;
		}
	}

	/**************************************************************************/
	/*
	 * !
	 * 
	 * @brief Calls listSSIDs and then displays the results of the SSID scan
	 * 
	 * For the moment we only list these via CC3KPrinter->print since this can
	 * consume a lot of memory passing all the data back with a buffer
	 * 
	 * @note This command isn't available when the CC3000 is configured in
	 * 'CC3000_TINY_DRIVER' mode
	 * 
	 * @returns False if an error occured!
	 */
	/**************************************************************************/

	/**************************************************************************/
	/*
	 * ! Connect to an unsecured SSID/AP(security)
	 * 
	 * @param ssid The named of the AP to connect to (max 32 chars)
	 * 
	 * @param ssidLen The size of the ssid name
	 * 
	 * @returns False if an error occured!
	 */
	/**************************************************************************/
	boolean connectOpen(String ssid) {
		if (!_initialised) {
			return false;
		}

		// #ifndef CC3000_TINY_DRIVER
		// CHECK_SUCCESS(wlan_ioctl_set_connection_policy(0, 0, 0),
		// "Failed to set connection policy", false);
		// delay(500);
		// CHECK_SUCCESS(wlan_connect(WLAN_SEC_UNSEC,
		// (const char*)ssid, strlen(ssid),
		// 0 ,NULL,0),
		// "SSID connection failed", false);
		// #else
		// wlan_connect(ssid, ssidLen);
		// #endif

		return true;
	}

	/**************************************************************************/
	/*
	 * ! Connect to an SSID/AP(security)
	 * 
	 * @note This command isn't available when the CC3000 is configured in
	 * 'CC3000_TINY_DRIVER' mode
	 * 
	 * @returns False if an error occured!
	 */
	/**************************************************************************/
	boolean connectSecure(String ssid, String key) {
		if (!_initialised) {
			return false;
		}

		// if ( (secMode < 0) || (secMode > 3)) {
		// CHECK_PRINTER {
		// CC3KPrinter->println(F("Security mode must be between 0 and 3"));
		// }
		// return false;
		// }
		//
		// if (strlen(ssid) > MAXSSID) {
		// CHECK_PRINTER {
		// CC3KPrinter->print(F("SSID length must be < "));
		// CC3KPrinter->println(MAXSSID);
		// }
		// return false;
		// }
		//
		// if (strlen(key) > MAXLENGTHKEY) {
		// CHECK_PRINTER {
		// CC3KPrinter->print(F("Key length must be < "));
		// CC3KPrinter->println(MAXLENGTHKEY);
		// }
		// return false;
		// }
		//
		// CHECK_SUCCESS(wlan_ioctl_set_connection_policy(0, 0, 0),
		// "Failed setting the connection policy",
		// false);
		// delay(500);
		// CHECK_SUCCESS(wlan_connect(secMode, (char *)ssid, strlen(ssid),
		// NULL,
		// (unsigned char *)key, strlen(key)),
		// "SSID connection failed", false);
		//
		// /* Wait for 'HCI_EVNT_WLAN_UNSOL_CONNECT' in CC3000_UsynchCallback */

		return true;
	}

	// Connect with timeout
	boolean connectToAP(String ssid, String key, int attempts) throws IOException {
		if (!_initialised) {
			return false;
		}

		write(AT_CWMODE + "=1");
		System.out.println(read());
		String cmd = AT_CWJAP + "=\"";
		cmd += new String(ssid);
		cmd += "\",\"";
		cmd += new String(key);
		cmd += "\"";
		write(cmd);
		System.out.println(read());

		cmd = AT_CIPMUX + "=1";
		write(cmd);
		System.out.println(read());

		// if (r.contains("OK")) {
		// debugPrintln("OK, Connected to WiFi.");
		// return true;
		// } else {
		// debugPrintln("Can not connect to the WiFi.");
		// return false;
		// }
		// int16_t timer;
		//
		// // If attempts is zero interpret that as no limit on number of
		// retries.
		// bool retryForever = attempts == 0;
		//
		// do {
		// // Stop if the max number of attempts have been tried.
		// if (!retryForever) {
		// if (attempts == 0) {
		// return checkConnected();
		// }
		// attempts -= 1;
		// }
		//
		// cc3k_int_poll();
		// /* MEME: not sure why this is absolutely required but the cc3k freaks
		// if you dont. maybe bootup delay? */
		// // Setup a 4 second SSID scan
		// scanSSIDs(4000);
		// // Wait for results
		// delay(4500);
		// scanSSIDs(0);
		//
		// /* Attempt to connect to an access point */
		// CHECK_PRINTER {
		// CC3KPrinter->print(F("\n\rConnecting to "));
		// CC3KPrinter->print(ssid);
		// CC3KPrinter->print(F("..."));
		// }
		// if ((secmode == 0) || (strlen(key) == 0)) {
		// /* Connect to an unsecured network */
		// if (! connectOpen(ssid)) {
		// CHECK_PRINTER {
		// CC3KPrinter->println(F("Failed!"));
		// }
		// continue;
		// }
		// } else {
		// /* NOTE: Secure connections are not available in 'Tiny' mode! */
		// #ifndef CC3000_TINY_DRIVER
		// /* Connect to a secure network using WPA2, etc */
		// if (! connectSecure(ssid, key, secmode)) {
		// CHECK_PRINTER {
		// CC3KPrinter->println(F("Failed!"));
		// }
		// continue;
		// }
		// #endif
		// }
		//
		// timer = WLAN_CONNECT_TIMEOUT;
		//
		// /* Wait around a bit for the async connected signal to arrive or
		// timeout */
		// CHECK_PRINTER {
		// CC3KPrinter->print(F("Waiting to connect..."));
		// }
		// while ((timer > 0) && !checkConnected())
		// {
		// cc3k_int_poll();
		// delay(10);
		// timer -= 10;
		// }
		// if (timer <= 0) {
		// CHECK_PRINTER {
		// CC3KPrinter->println(F("Timed out!"));
		// }
		// }
		// } while (!checkConnected());

		return true;
	}

	boolean checkConnected() {
		return false;// esp8266Bitset.test(ESP8266BitSet::IsConnected);
	}

	Client connectTCP(String destIP, int destPort) throws IOException {

		String cmd = AT_CIPSTART + "=1,\"TCP\",\"";
		cmd += destIP;
		cmd += "\"," + destPort;
		write(cmd);
		System.out.println(read());

		// sockaddr socketAddress;
		// int32_t tcpid;
		//
		// // Create the socket(s)
		// //if (CC3KPrinter != 0)
		// CC3KPrinter->print(F("Creating socket ... "));
		// tcpid = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
		// if (-1 == tcpid)
		// {
		// CHECK_PRINTER {
		// CC3KPrinter->println(F("Failed to open socket"));
		// }
		// return ESP8266_Client();
		// }
		// //CC3KPrinter->print(F("DONE (socket ")); CC3KPrinter->print(tcpid);
		// CC3KPrinter->println(F(")"));
		//
		// closedids[tcpid] = false; // Clear any previous closed event
		//
		// // Try to open the socket
		// memset(&socketAddress, 0x00, sizeof(socketAddress));
		// socketAddress.sa_family = AF_INET;
		// socketAddress.sa_data[0] = (destPort & 0xFF00) >> 8; // Set the Port
		// Number
		// socketAddress.sa_data[1] = (destPort & 0x00FF);
		// socketAddress.sa_data[2] = destIP >> 24;
		// socketAddress.sa_data[3] = destIP >> 16;
		// socketAddress.sa_data[4] = destIP >> 8;
		// socketAddress.sa_data[5] = destIP;
		//
		// CHECK_PRINTER {
		// CC3KPrinter->print(F("\n\rConnect to "));
		// printIPdotsRev(destIP);
		// CC3KPrinter->print(':');
		// CC3KPrinter->println(destPort);
		// }
		//
		// //printHex((byte *)&socketAddress, sizeof(socketAddress));
		// //if (CC3KPrinter != 0)
		// CC3KPrinter->print(F("Connecting socket ... "));
		// if (-1 == ::connect(tcpid, &socketAddress, sizeof(socketAddress)))
		// {
		// CHECK_PRINTER {
		// CC3KPrinter->println(F("Connection error"));
		// }
		// closesocket(tcpid);
		// return ESP8266_Client();
		// }
		// //if (CC3KPrinter != 0) CC3KPrinter->println(F("DONE"));
		return new Client(this, 0);
	}

	Client connectUDP(String destIP, int destPort) {

		// sockaddr socketAddress;
		// int32_t udpid;
		//
		// // Create the socket(s)
		// // socket = SOCK_STREAM, SOCK_DGRAM, or SOCK_RAW
		// // protocol = IPPROTO_TCP, IPPROTO_UDP or IPPROTO_RAW
		// //if (CC3KPrinter != 0) CC3KPrinter->print(F("Creating socket... "));
		// udpid = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
		// if (-1 == udpid)
		// {
		// CHECK_PRINTER {
		// CC3KPrinter->println(F("Failed to open socket"));
		// }
		// return ESP8266_Client();
		// }
		// //if (CC3KPrinter != 0) { CC3KPrinter->print(F("DONE (socket "));
		// CC3KPrinter->print(udpid); CC3KPrinter->println(F(")")); }
		//
		// // Try to open the socket
		// memset(&socketAddress, 0x00, sizeof(socketAddress));
		// socketAddress.sa_family = AF_INET;
		// socketAddress.sa_data[0] = (destPort & 0xFF00) >> 8; // Set the Port
		// Number
		// socketAddress.sa_data[1] = (destPort & 0x00FF);
		// socketAddress.sa_data[2] = destIP >> 24;
		// socketAddress.sa_data[3] = destIP >> 16;
		// socketAddress.sa_data[4] = destIP >> 8;
		// socketAddress.sa_data[5] = destIP;
		//
		// CHECK_PRINTER {
		// CC3KPrinter->print(F("Connect to "));
		// printIPdotsRev(destIP);
		// CC3KPrinter->print(':');
		// CC3KPrinter->println(destPort);
		// }
		//
		// //printHex((byte *)&socketAddress, sizeof(socketAddress));
		// if (-1 == ::connect(udpid, &socketAddress, sizeof(socketAddress)))
		// {
		// CHECK_PRINTER {
		// CC3KPrinter->println(F("Connection error"));
		// }
		// closesocket(udpid);
		// return ESP8266_Client();
		// }
		//
		return new Client();
	}

	void debugPrintln(String str) {
		System.out.println("d: " + str);
	}

	void debugPrint(String str) {
		System.out.print(str);
	}

	String read() throws IOException {
		String r = "";

		int i = 0;
		while (input.available() <= 0 && i < RETRIES) {
			i++;
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}

		if (i == RETRIES) {
			return null;
		}

		while (input.available() > 0) {
			r += (char) input.read();
		}
		if (r.contains(BUSY_NOW)) {
			debugPrint(r);
			return null;
		}
		return r;
	}

	private void write(String cmd) {
		debugPrintln(cmd);
		output.println(cmd);
		output.flush();
	}

	public void query(String cmd) throws IOException {
		write(cmd + '?');
		debugPrint(read());
	}

}
