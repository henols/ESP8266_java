package se.aceone.esp8266;

import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("PureJavaCommDemo");
			CommPortIdentifier portid = null;
			// Enumeration e = CommPortIdentifier.getPortIdentifiers();
			portid = CommPortIdentifier.getPortIdentifier("COM13");
			// while (e.hasMoreElements()) {
			// portid = (CommPortIdentifier) e.nextElement();
			// System.out.println("found " + portid.getName());
			// }
			if (portid != null) {
				System.out.println("use " + portid.getName());
				SerialPort port = (SerialPort) portid.open("PureJavaCommDemo", 1000);
				port.notifyOnDataAvailable(true);
				port.notifyOnOutputEmpty(true);
				// port.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN +
				// SerialPort.FLOWCONTROL_XONXOFF_OUT);
				// (int baudRate, int dataBits, int stopBits, int parity)
				port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//				port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				Module module = new Module();
				if (!module.begin(port)) {
					System.out.println("Failed to start module.");
					return;
				}
				System.out.println("Module started.");
				if (!module.reset()) {
					System.out.println("Failed to reset module.");
					return;
				}
				module.checkConnected();
				System.out.println("Module reset.");
				int[] version = new int[5];
				if (!module.version(version)) {
					System.out.println("No module version.");
				} else {
					System.out.println("Version: " + version[0] + "." + version[1] + " " + version[2] + "." + version[3]+ "." + version[4]);
				}
				// module.query(Module.AT_CIPMUX);
//				module.getListAPs();
				if (!module.connectToAP("C64", "feelgood")) {
					System.out.println("Failed connect to AP.");
					return;
				}
				System.out.println("Connected to AP.");

				module.checkConnected();
				int[] ip = new int[4];
				if (!module.getIPAddress(ip)) {
					System.out.println("Failed to get IP.");
					return;
				}
				System.out.println("Got ip: " + ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3]);
				// module.query(Module.AT_CWJAP);
				// module.connectTCP("220.181.111.85", 80);
//				String destIP = "hackaday.com";
				String destIP = "aceone.se";
				Client client = module.connectTCP(destIP, 80);
				String cmd = "GET /status HTTP/1.0\r\nHost: ";
				cmd += destIP;
				cmd += "\r\n\r\n";

				client.write(cmd.toCharArray(), cmd.length());
				System.out.println("--------------");

				while (client.available() > 0) {
					System.out.print((char) client.read());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
