package se.aceone.esp8266;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**********************************************************************/

	private int id;
	private Module esp8266;
	Client() {
		id = -1;
	}

	Client(Module esp8266, int id) {
		this.esp8266 = esp8266;
		this.id = id;

	//  id = s;
	//  bufsiz = 0;
	//  _rx_buf_idx = 0;
	}



	int connect(String destIP, int destPort) {
	//  bufsiz = 0;
	//  _rx_buf_idx = 0;
	//  sockaddr      socketAddress;
	//  int32_t       tcpid;
	//
	//  // Create the socket(s)
	//  //if (CC3KPrinter != 0) CC3KPrinter->print(F("Creating socket ... "));
	//  tcpid = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	//  if (-1 == tcpid)
	//  {
//	    CHECK_PRINTER {
//	      CC3KPrinter->println(F("Failed to open socket"));
//	    }
//	    return 0;
	//  }
	//  //CC3KPrinter->print(F("DONE (socket ")); CC3KPrinter->print(tcpid); CC3KPrinter->println(F(")"));
	//
	//  // Try to open the socket
	//  memset(&socketAddress, 0x00, sizeof(socketAddress));
	//  socketAddress.sa_family = AF_INET;
	//  socketAddress.sa_data[0] = (destPort & 0xFF00) >> 8;  // Set the Port Number
	//  socketAddress.sa_data[1] = (destPort & 0x00FF);
	//  socketAddress.sa_data[2] = destIP >> 24;
	//  socketAddress.sa_data[3] = destIP >> 16;
	//  socketAddress.sa_data[4] = destIP >> 8;
	//  socketAddress.sa_data[5] = destIP;
	//
	//  CHECK_PRINTER {
//	    CC3KPrinter->print(F("\n\rConnect to "));
//	    CC3KPrinter->print(destIP);
//	    CC3KPrinter->print(':');
//	    CC3KPrinter->println(destPort);
	//  }
	//
	//  //printHex((byte *)&socketAddress, sizeof(socketAddress));
	//  //if (CC3KPrinter != 0) CC3KPrinter->print(F("Connecting socket ... "));
	//  if (-1 == ::connect(tcpid, &socketAddress, sizeof(socketAddress)))
	//  {
//	    CHECK_PRINTER {
//	      CC3KPrinter->println(F("Connection error"));
//	    }
//	    closesocket(tcpid);
//	    return 0;
	//  }
	//  // if (CC3KPrinter != 0) CC3KPrinter->println(F("DONE"));
	//
	//  id = tcpid;
	//  return 1;
		return 0;
	}

	int connected() {
	//  if (id < 0) return false;
	//
	//  if (! available() && closedids[id] == true) {
//	    //if (CC3KPrinter != 0) CC3KPrinter->println("No more data, and closed!");
//	    closesocket(id);
//	    closedids[id] = false;
//	    id = -1;
//	    return false;
	//  }
	//
	//  else return true;
		return 0;

	}

	int write(char[] buf, int len) {
	//  return write(buf, len, 0);
		return 0;

	}

	int write(char c) {
	//  int32_t r;
	//  r = send(id, &c, 1, 0);
	//  if ( r < 0 ) return 0;
	//  return r;
		return 0;
	}

	int read(char[] buf, int len) {
	//  return read(buf, len, 0);
		return 0;

	}

	int close() {
	//  int32_t x = closesocket(id);
	//  id = -1;
	//  return x;
		return 0;
	}

	void stop() {
		close();
	}

	int read() {
	//  while ((bufsiz <= 0) || (bufsiz == _rx_buf_idx)) {
//	    cc3k_int_poll();
//	    // buffer in some more data
//	    bufsiz = recv(id, _rx_buf, sizeof(_rx_buf), 0);
//	    if (bufsiz == -57) {
//	      close();
//	      return 0;
//	    }
//	    //if (CC3KPrinter != 0) { CC3KPrinter->println("Read "); CC3KPrinter->print(bufsiz); CC3KPrinter->println(" bytes"); }
//	    _rx_buf_idx = 0;
	//  }
	//  uint8_t ret = _rx_buf[_rx_buf_idx];
	//  _rx_buf_idx++;
	//  //if (CC3KPrinter != 0) { CC3KPrinter->print("("); CC3KPrinter->write(ret); CC3KPrinter->print(")"); }
	//  return ret;
		return 0;
	}

	int available() {
		// not open!
	//  if (id < 0) return 0;
	//
	//  if ((bufsiz > 0) // we have some data in the internal buffer
//	      && (_rx_buf_idx < bufsiz)) {  // we havent already spit it all out
//	    return (bufsiz - _rx_buf_idx);
	//  }
	//
	//  // do a select() call on this socket
	//  timeval timeout;
	//  fd_set fd_read;
	//
	//  memset(&fd_read, 0, sizeof(fd_read));
	//  FD_SET(id, &fd_read);
	//
	//  timeout.tv_sec = 0;
	//  timeout.tv_usec = 5000; // 5 millisec
	//
	//  int16_t s = select(id+1, &fd_read, NULL, NULL, &timeout);
	//  //if (CC3KPrinter != 0) } CC3KPrinter->print(F("Select: ")); CC3KPrinter->println(s); }
	//  if (s == 1) return 1;  // some data is available to read
	//  else return 0;  // no data is available
		return 0;  // no data is available
	}

	void flush() {
		// No flush implementation, unclear if necessary.
	}

	int peek() {
	//  while ((bufsiz <= 0) || (bufsiz == _rx_buf_idx)) {
//	    cc3k_int_poll();
//	    // buffer in some more data
//	    bufsiz = recv(id, _rx_buf, sizeof(_rx_buf), 0);
//	    if (bufsiz == -57) {
//	      close();
//	      return 0;
//	    }
//	    //if (CC3KPrinter != 0) { CC3KPrinter->println("Read "); CC3KPrinter->print(bufsiz); CC3KPrinter->println(" bytes"); }
//	    _rx_buf_idx = 0;
	//  }
	//  uint8_t ret = _rx_buf[_rx_buf_idx];
	//
	//  //if (CC3KPrinter != 0) { CC3KPrinter->print("("); CC3KPrinter->write(ret); CC3KPrinter->print(")"); }
	//  return ret;
		return 0;
	}


}
