package battleship.view;

import java.util.Vector;
import java.util.Scanner;

public class View {
	public void printBoard(int[][] board) {
		System.out.print("   ");
		for (int i = 0; i < board.length; i++)
			System.out.print(i + " ");

		for (int i = 0; i < board.length; i++) {
			System.out.println();
			System.out.print(i + "  ");
			for (int j = 0; j < board[i].length; j++) {
				char characterToPrint = 0;
				if (board[i][j] == 0)
					characterToPrint = '~';
				else if (board[i][j] == 1)
					characterToPrint = 'o';
				else if (board[i][j] == 2 || board[i][j] == 5)
					characterToPrint = 'x';
				else if (board[i][j] == 3)
					characterToPrint = '*';
				System.out.print(characterToPrint + " ");
			}
		}
	}

	public Vector<String> startMenu() {
		System.out.println();
		System.out.println("Welcome to Battle Ship!");
		System.out.println();
		System.out.println("Input necessary values to do something.");
		System.out.println();

		int input = 0;
		Vector<String> answer = new Vector<String>();

		for (int i = 0; i < 3; i++) {
			answer.add("");
		}
		
		answer.set(0, new String("heh"));

		Scanner scanner = new Scanner(System.in);

		while (input == 0) {
			System.out.println("1 - Connect;");
			System.out.println("2 - Change connection settings;");
			System.out.print("3 - Exit: ");

			input = scanner.nextInt();

			switch(input) {
				case 1:

				break;

				case 2:

				System.out.println();
				System.out.println("1 - Change port;");
				System.out.print("2 - Change server IP: ");

				input = scanner.nextInt();

				if (input == 1) {
				    answer.set(1, scanner.next());
					input = 0;
				}
                else if (input == 2) {
                  	input = 0; 
                  	answer.set(2, scanner.next());					 
                }
                else {  
                    System.out.println("Wrong input. Try again.");
				}

				System.out.println();

				break;

				case 3:

				answer.set(0, "1");
				break;

				default:

				System.out.println("Wrong input. Try again.");
				System.out.println();

				input = 0;
				break;
			}
		}

		return answer;
	}

	public int startGameMenu(int ID) {
		Scanner scanner = new Scanner(System.in);
		System.out.println();
		System.out.println("Your ID: " + ID);
		System.out.println();

		int answer = 0, input = 0;

		while (input == 0) {
			System.out.println("1 - Create new board;");
			System.out.println("2 - Join to existing board;");
			System.out.print("3 - Exit: ");

			input = scanner.nextInt();

			switch(input) {
				case 1:

				answer = 1;
				break;

				case 2:

				answer = 2;
				break;

				case 3:

				answer = -1;
				break;

				default:

				System.out.println("Wrong input. Try again.");
				System.out.println();

				input = 0;
				break;
			}
		}
		return answer;
	}

	public boolean arrangmentSaving() {
		Scanner scanner = new Scanner(System.in);
		String input = "";
		System.out.println("Sorry, no free boards.");

		while (input.isEmpty()) {
			System.out.print("Do you want to save your ship arrangment to use it at the next game? (y/n): ");
			input = scanner.next();
			if (input.equals("y") || input.equals("Y"))
				return true;
			else if (input.equals("n") || input.equals("n"))
				return false;
			else {
				System.out.println();
				System.out.println("Wrong input. Try again.");
				input = "";
			}
		}
		return false;
	}

	public Vector<Integer> getShipCoordinates() {
		Vector<Integer> result = new Vector<Integer>();
		Scanner scanner = new Scanner(System.in);

		int length = 0, firstXCoord = 0, firstYCoord = 0, secondXCoord = 0, secondYCoord = 0;
		System.out.println();
		System.out.print("Input length of ship: ");
		length = scanner.nextInt();
		System.out.print("Input first x coordinate: ");
		firstXCoord = scanner.nextInt();
		System.out.print("Input first y coordinate ");
		firstYCoord = scanner.nextInt();
		System.out.print("Input second x coordinate ");
		secondXCoord = scanner.nextInt();
		System.out.print("Input second y coordinate ");
		secondYCoord = scanner.nextInt();

		result.add(length);
		result.add(firstXCoord);
		result.add(firstYCoord);
		result.add(secondXCoord);
		result.add(secondYCoord);
		
		return result;	
	}

	public Vector<Integer> makeMove() {
		Vector<Integer> answer = new Vector<Integer>();
		Scanner scanner = new Scanner(System.in);

		int input = -1;
		System.out.println();
		System.out.println();
		System.out.println("Input coordinates to shot.");
		System.out.println();
		while (input == -1) {
			System.out.print("Input x coordinate: ");
			input = scanner.nextInt();

			if (input < 0 || input >= 10) {
				System.out.println("Wrong x coordinate: x coordinate must be integer from 0 to 9. Try again.");
				System.out.println();
				input = -1;
				continue;
			}
			answer.add(input);

			System.out.print("Input y coordinate: ");
			input = scanner.nextInt();

			if (input < 0 || input >= 10) {
				System.out.println("Wrong y coordinate: y coordinate must be integer from 0 to 9. Try again.");
				System.out.println();
				input = -1;
				continue;
			}
			answer.add(input);
		}
		return answer;
	} 

	public void printMessage(String message) {
		System.out.print(message);
	}

	public void printlnMessage(String message) {
		System.out.println(message);
	}

	public void println() {
		System.out.println();
	}
}