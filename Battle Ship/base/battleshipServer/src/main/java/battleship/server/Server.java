package battleship.server;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;

public class Server {
	private int idCounter = 0;
	private ServerSocket serverSocket;

	private HashMap<Integer, Socket> clients = new HashMap<Integer, Socket>();
	private HashMap<Integer, Integer> IDs = new HashMap<Integer, Integer>();

	private DataOutputStream logFile = new DataOutputStream(new FileOutputStream("logfile.log"));

	public Server(int portNumber) throws IOException {
		serverSocket = new ServerSocket(portNumber);
	}

	public void connect() throws IOException {
		while (true) {
			Socket clientSocket = serverSocket.accept();
			logFile.writeChars((new Date()) + ": client with ID=" + idCounter + " is connected\n");
		    clients.put(idCounter, clientSocket);
		    (new DataOutputStream(clientSocket.getOutputStream())).writeInt(idCounter);

		    ClientThread clientThread = new ClientThread(clientSocket, idCounter);
		    idCounter++;
		    clientThread.start();
		}
	}

	class ClientThread extends Thread {
		private Socket clientSocket;
		private int ID = -100;
		private int opponentID = -100;

		private boolean move = false;

		public ClientThread(Socket clientSocket, int ID) {
			this.clientSocket = clientSocket;
			this.ID = ID;
		}

		public void run() {
			try {
				while(true) {
					DataInputStream in = new DataInputStream(clientSocket.getInputStream());
					DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

					this.opponentID = in.readInt();

					if (this.opponentID == -200) {
						synchronized (clients) {
							clients.remove(ID);
						}
						synchronized (logFile) {
							logFile.writeChars((new Date()) + ": client with ID=" + ID + " is unconnected\n");
						}
						return;
					}				

					if (this.opponentID == -100) {
						synchronized (IDs) {
							IDs.put(ID, -100);
						}
						synchronized (logFile) {
							logFile.writeChars((new Date()) + ": client with ID=" + ID + " have created a new board\n");
						}
						this.move = true;

						this.opponentID = in.readInt();
						synchronized(logFile) {
							logFile.writeChars((new Date()) + ": clients with ID=" + ID + "and ID=" + opponentID + " have started playing a new game\n");
						}
						synchronized (IDs) {
							IDs.remove(ID);
							IDs.put(ID, opponentID);
						}
					}
					else {
						synchronized (logFile) {
							logFile.writeChars((new Date()) + ": client with ID=" + ID + " is searhing a free board\n");
						}
						this.opponentID = -100;
						synchronized (IDs) {
							for (Map.Entry<Integer, Integer> it : IDs.entrySet()) {
								if (it.getValue() == -100) {
									this.opponentID = it.getKey();
									break;
								}
							}
						}
						if (this.opponentID == -100) {
							synchronized(logFile) {
								logFile.writeChars((new Date()) + ": no free boards found for client with ID=" + ID + "\n");
							}
							out.writeInt(-50);
							continue;
						}
						else {
							synchronized (IDs) {
								IDs.put(ID, opponentID);
							}
							out.writeInt(opponentID);
							(new DataOutputStream(clients.get(this.opponentID).getOutputStream())).writeInt(ID);
						}
					}

					DataOutputStream opponentOutputStream = new DataOutputStream(clients.get(opponentID).getOutputStream());
					DataInputStream opponentInputStream = new DataInputStream(clients.get(opponentID).getInputStream());
					boolean endOfGameFlag = false;

					while (true) {
						if (move) {
							int x = in.readInt();
							int y = in.readInt();

							synchronized (logFile) {
								logFile.writeChars((new Date()) + ": client with ID=" + ID + " made a move on a client with ID=" + opponentID + "; x=" + x + "; y=" + y + "\n");
							}

							opponentOutputStream.writeInt(x);
							opponentOutputStream.writeInt(y);
							int resultOfShot = in.readInt();
							if (resultOfShot == 3) {
								synchronized (logFile) {
									logFile.writeChars((new Date()) + ": client with ID=" + ID + " missed on x=" + x + "; y=" + y + "; opponent's ID=" + opponentID + "\n");
								}
								move = false;
							} else {
								synchronized (logFile) {
									logFile.writeChars((new Date()) + ": client with ID=" + ID + " hit on x=" + x + "; y=" + y + "; opponent's ID=" + opponentID + "\n");
								}
								move = true;
							}
						}
						else {
							int resultOfShot = in.readInt();
							opponentOutputStream.writeInt(resultOfShot);
							if (resultOfShot == 3) {
								move = true;
							} else {
								synchronized (logFile) {
									logFile.writeChars((new Date()) + ": ship of client with ID=" + ID + " is shot; opponent's ID=" + opponentID + "\n");
								}
								move = false;
							}
						}

						endOfGameFlag = in.readBoolean();
						if (endOfGameFlag) {
							synchronized (logFile) {
								logFile.writeChars((new Date()) + ": ID=" + ID + ": game is finished\n");
							}
							synchronized (IDs) {
								this.move = false;
								IDs.remove(ID);
								break;
							}
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}