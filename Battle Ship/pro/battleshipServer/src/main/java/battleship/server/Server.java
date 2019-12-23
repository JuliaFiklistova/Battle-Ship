package battleship.server;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;
import java.util.Vector;
import java.util.Map;
import java.util.Date;
import java.util.Arrays;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Server {
	private int idCounter = 0;
	private ServerSocket serverSocket;

	private HashMap<Integer, Socket> clients = new HashMap<Integer, Socket>();
	private HashMap<Integer, Integer> IDs = new HashMap<Integer, Integer>();
	private HashMap<String, Vector<String>> listOfUsers = new HashMap<String, Vector<String>>();

	private DataOutputStream logFile = new DataOutputStream(new FileOutputStream("logfile.log"));

	public Server(int portNumber) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("users.txt")));
		String[] subStr;
        while(reader.ready()) {
        	Vector<String> passRate = new Vector<>();
        	subStr = reader.readLine().split(" ");
        	passRate.add(subStr[1]);
        	passRate.add(subStr[2]);
        	listOfUsers.put(subStr[0], passRate);
        }
        reader.close();
        serverSocket = new ServerSocket(portNumber);
	}

	public void connect() throws IOException {
		while (true) {
			Socket clientSocket = serverSocket.accept();

			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			String login;
			byte[] buf = new byte[1024];
			int answer = in.readInt();
			int rating = 0;

			if (answer == 1) {
				in.read(buf);
				login = (new String(buf)).trim();
				buf = new byte[1024];
				in.read(buf);
				String password = (new String(buf)).trim();
				buf = new byte[1024];
				if (!listOfUsers.containsKey(login) || !listOfUsers.get(login).get(0).equals(password)) {
					out.writeInt(-1);
					out.writeInt(-1);
					continue;
				}
				else {
					out.writeInt(0);
					out.writeInt(Integer.parseInt(listOfUsers.get(login).get(1)));
					rating = Integer.parseInt(listOfUsers.get(login).get(1));
				}
			}
			else if (answer == 2) {
				in.read(buf);
				login = (new String(buf)).trim();
				buf = new byte[1024];
				in.read(buf);
				String password = (new String(buf)).trim();
				buf = new byte[1024];
				//System.out.println(login);
				if (listOfUsers.containsKey(login)) {
					out.writeInt(-1);
					continue;
				}
				else {
					out.writeInt(0);
					Vector<String> passRate = new Vector<String>();
					passRate.add(password);
					passRate.add("0");
					listOfUsers.put(login, passRate);
					rating = Integer.parseInt(listOfUsers.get(login).get(1));
				}
			}
			else
				continue;
			logFile.writeChars((new Date()) + ": client with ID=" + idCounter + " is connected\n");
		    clients.put(idCounter, clientSocket);
		    out.writeInt(idCounter);

		    ClientThread clientThread = new ClientThread(clientSocket, idCounter, login, rating);
		    idCounter++;
		    clientThread.start();
		}
	}

	class ClientThread extends Thread {
		private Socket clientSocket;
		private int ID = -100;
		private int opponentID = -100;

		private String login;

		private boolean move = false;
		private int rating = 0;

		public ClientThread(Socket clientSocket, int ID, String login, int rating) {
			this.clientSocket = clientSocket;
			this.ID = ID;
			this.login = login;
			this.rating = rating;
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
							int rating = in.readInt();
							BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("users.txt")));
							synchronized (writer) {
								for (Map.Entry<String, Vector<String>> it : listOfUsers.entrySet()) {
									if (it.getKey().equals(this.login)) 
										writer.write(this.login + " " + it.getValue().get(0) + " " + rating + "\n", 0, (this.login + " " + it.getValue().get(0) + " " + rating + "\n").length());
									else
										writer.write(it.getKey() + " " + it.getValue().get(0) + " " + it.getValue().get(1) + "\n", 0, (this.login + " " + it.getValue().get(0) + " " + it.getValue().get(1) + "\n").length());
								}
							}
							writer.close();
						}
						synchronized (logFile) {
							logFile.writeChars((new Date()) + ": client with ID=" + ID + " is unconnected\n");
						}
						return;
					}
					else if (this.opponentID == -100) {
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
					else if (this.opponentID == -60) {
						BufferedWriter bfOut = new BufferedWriter(new OutputStreamWriter(out));
						for (Map.Entry<String, Vector<String>> it : listOfUsers.entrySet()) {
							bfOut.write(it.getKey() + "\n", 0, it.getKey().length() + 1);
							bfOut.write(it.getValue().get(1) + "\n", 0, it.getValue().get(1).length() + 1);
						}
						bfOut.write("\n", 0, 1);
						bfOut.flush();
						continue;
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
								rating = in.readInt();
								Vector<String> passRate = new Vector<String>();
								passRate.add(listOfUsers.get(login).get(0));
								passRate.add((new Integer(rating)).toString());	
								listOfUsers.put(login, passRate);
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