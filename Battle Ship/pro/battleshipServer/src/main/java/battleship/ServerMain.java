package battleship;

import battleship.server.Server;

import java.io.IOException;

public class ServerMain {
	public static void main(String[] args) throws IOException {
		int portNumber = 10000;
		Server server = new Server(portNumber);

		server.connect();
	}
}