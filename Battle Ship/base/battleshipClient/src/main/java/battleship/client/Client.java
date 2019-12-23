package battleship.client;

import battleship.model.Model;
import battleship.view.View;

import java.net.Socket;
import java.net.InetSocketAddress;

import java.util.Vector;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Client {

	//private boolean SET_DEFAULT = true; 

	private int ID = 0;
	private int opponentID = -100;

	private boolean move = false;
	private boolean isBoardSaved = false;

	private Model model = new Model();
	private View view = new View();

	private int portNumber = 10000;
	private String IP = "127.0.0.1";

    private Socket clientSocket;	

	public Client() throws IOException {
		this.startMenuLaunch();
	}

	private void startMenuLaunch() throws IOException {
        Vector<String> answer = this.view.startMenu();

		if (answer.get(0).equals("1"))
			return;

		if (!answer.get(1).isEmpty())
			portNumber = Integer.valueOf(answer.get(1));

		if (!answer.get(2).isEmpty())
			IP = answer.get(2);
        
        clientSocket = new Socket(IP, portNumber);
        ID = (new DataInputStream(clientSocket.getInputStream())).readInt();

        this.startGameMenuLaunch();
	}

	private void startGameMenuLaunch() throws IOException {
		DataInputStream in = new DataInputStream(this.clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(this.clientSocket.getOutputStream());

        int answer = this.view.startGameMenu(ID);
        if (answer == -1) {
        	out.writeInt(-200);
        	return;
        }

        if (answer == 1) {
        	if (!isBoardSaved/* && !SET_DEFAULT*/) {
        		this.shipsArrangementLaunch();
        	}
        	/*if (SET_DEFAULT) {
        		model = new Model(1); 
        	}*/
        	move = true;        	
        	out.writeInt(-100);
        	this.view.println();
        	this.view.printlnMessage("Waiting for other player...");
        	this.view.println();
        	this.opponentID = in.readInt();
        	out.writeInt(this.opponentID);
        	this.startGame();
        }
        else {
			if (!isBoardSaved/* && !SET_DEFAULT*/) {
        		this.shipsArrangementLaunch();
        	}
        	/*if (SET_DEFAULT) {
        		model = new Model(1); 
        	}*/
        	out.writeInt(-1);
        	this.opponentID = in.readInt();
        	if (this.opponentID == -50) {
        		if (!view.arrangmentSaving())
        			model = new Model();
        		this.startGameMenuLaunch();
        		return;
        		
        	}
        	this.startGame();
        }
	}

	private void startGame() throws IOException {
		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

		boolean endOfGameFlag = false;

		while (true) {
			if(move) {
				Vector<Integer> answer = this.view.makeMove();
				while(this.model.getOpponentBoard()[answer.get(1)][answer.get(0)] != 0) {
					this.view.println();
					this.view.printlnMessage("Wrong input: You have already shot at these coordinates. Try again.");
					answer = this.view.makeMove();
				}
				out.writeInt(answer.get(0));
				out.writeInt(answer.get(1));
				int resultOfShot = in.readInt();
				out.writeInt(resultOfShot);
				this.model.doShot(answer.get(0), answer.get(1), resultOfShot);
				if (resultOfShot != 2 && resultOfShot != 5) {
					this.view.println();
					this.view.printlnMessage("Miss!");
					this.view.println();
					move = false;
				}
				else {
					this.view.println();
					this.view.printlnMessage("Hit!");
					this.view.println();
					move = true;
				}

				if (resultOfShot == 5)
					endOfGameFlag = true;
				out.writeBoolean(endOfGameFlag);
			}
			if(!move) {
				this.view.println();
				this.view.printlnMessage("Waiting for opponent to make move...");
				this.view.println();

				int x = in.readInt(), y = in.readInt();

				int resultOfShot = this.model.getShot(x, y);
				System.out.println(resultOfShot);
				if (resultOfShot == 2 || resultOfShot == 5) {
					this.view.println();
					this.view.printlnMessage("Your ship is shot down at: " + x + " " + y + ".");
					this.view.println();
					this.move = false;
				}
				else {
					this.view.println();
					this.view.printlnMessage("Opponent has missed. Your turn.");
					this.view.println();
					this.move = true;
				}

				out.writeInt(resultOfShot);
				if (resultOfShot == 5)
					endOfGameFlag = true;
				out.writeBoolean(endOfGameFlag);
			}

			this.view.println();
			this.view.printlnMessage("Your board:");
			this.view.println();
			this.view.printBoard(this.model.getBoard());
			this.view.println();

			this.view.println();
			this.view.printlnMessage("Opponent's board:");
			this.view.println();    
			this.view.printBoard(this.model.getOpponentBoard());
			this.view.println();

			if (endOfGameFlag) {
				if (move) {
					this.view.println();
					this.view.printlnMessage("Congratulations! You won!");
					this.view.println();
				}
				else {
					this.view.println();
					this.view.printlnMessage("You lose.");
					this.view.println();
				}
				break;
			}
		}
		model = new Model();
		this.startGameMenuLaunch();
	}

	private void shipsArrangementLaunch() {
		this.view.println();
        this.view.printlnMessage("Now you need to arrange your ships:");
        this.view.println();
        this.view.printlnMessage("1 ship - 4 cells;");
        this.view.printlnMessage("2 ships - 3 cells;");
        this.view.printlnMessage("3 shis - 2 cells;");
        this.view.printlnMessage("4 ships - 1 cell;");
        this.view.println();
        this.view.printlnMessage("You can not place the ship near the other ship.");
        this.view.println();
        this.view.println();

        while(this.model.getTotalShipCounter() != 10) {
        	this.view.println();
        	this.view.printBoard(this.model.getBoard());
        	this.view.println();
            Vector<Integer> coordinates = this.view.getShipCoordinates();

            if (!this.model.setShip(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3), coordinates.get(4))) {
            	this.view.println();
                this.view.printlnMessage("Some parameters are wrong. Try again.");
                this.view.println();
            }
        }
	}
}