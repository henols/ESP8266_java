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
//			Enumeration e = CommPortIdentifier.getPortIdentifiers();
			portid = CommPortIdentifier.getPortIdentifier("COM12");
//			while (e.hasMoreElements()) {
//				portid = (CommPortIdentifier) e.nextElement();
//				System.out.println("found " + portid.getName());
//			}
			if (portid != null) {
				System.out.println("use " + portid.getName());
				SerialPort port = (SerialPort) portid.open("PureJavaCommDemo", 1000);
				port.notifyOnDataAvailable(true);
				port.notifyOnOutputEmpty(true);
//				port.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN + SerialPort.FLOWCONTROL_XONXOFF_OUT);
				// (int baudRate, int dataBits, int stopBits, int parity)
				port.setSerialPortParams(115200,  SerialPort.DATABITS_8,  SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				Module module = new Module();
				module.begin(port);
//				module.reset();
				module.version();
//				module.query(Module.AT_CIPMUX);
				module.connectToAP("c64", "feelgood", 5);
				module.getListAPs();
				module.query(Module.AT_CWJAP);
				module.getIPAddress();
//				module.connectTCP("220.181.111.85", 80);
				module.connectTCP("aceone.se", 80);
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
