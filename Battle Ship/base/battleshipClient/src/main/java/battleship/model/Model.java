package battleship.model;

import java.util.Vector;

public class Model {
	private int[][] board = new int[10][10];
	private int[][] boardOpponent = new int[10][10];

	private int killCounter = 0;
	private int[] shipCounters = new int[4];
	private int totalShipCounter = 0;

	public Model() {
		board = new int[10][10];
	}
	/*public Model(int v) {
		int[][]ar = {{1, 1, 1, 1, 0, 1, 1, 1, 0, 1},
		          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		          {1, 1, 1, 0, 1, 0, 1, 0, 1, 0},
		          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		          {1, 1, 0, 1, 1, 0, 1, 1, 0, 0},
		          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                  {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                  {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                  {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                  {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
        this.board = ar;          
	}*/

	/*private boolean checkNearCells(int xCoord, int yCoord, boolean isfirstLast) {
		if (xCoord > 0 && !isfirstLast)
			if (board[yCoord][xCoord - 1] != 0)
				return false;

		if (xCoord < 9)
			if (board[yCoord][xCoord + 1] != 0)
				return false;

	}*/

	private boolean checkCells(int firstXCoord, int secondXCoord, int firstYCoord, int secondYCoord) {
		while (firstYCoord <= secondYCoord) {
			if (board[firstYCoord][firstXCoord] != 0)
				return false;
			if (firstYCoord > 0 && board[firstYCoord - 1][firstXCoord] != 0)
				return false;
			if (firstYCoord > 0 && firstXCoord > 0 && board[firstYCoord - 1][firstXCoord - 1] != 0)
				return false;
			if (firstYCoord > 0 && firstXCoord < 9 && board[firstYCoord - 1][firstXCoord + 1] != 0)
				return false;
			if (firstYCoord < 9 && board[firstYCoord + 1][firstXCoord] != 0)
				return false;
			if (firstYCoord < 9 && firstXCoord < 9 && board[firstYCoord + 1][firstXCoord + 1] != 0)
				return false;
			if (firstYCoord < 9 && firstXCoord > 0 && board[firstYCoord + 1][firstXCoord - 1] != 0)
				return false;
			if (firstXCoord > 0 && board[firstYCoord][firstXCoord - 1] != 0)
				return false;
			if (firstXCoord < 9 && board[firstYCoord][firstXCoord + 1] != 0)
				return false;

			if (firstYCoord == secondYCoord)
				break;
			firstYCoord++;
		}

		while (secondYCoord <= firstYCoord) {
			if (board[secondYCoord][secondXCoord] != 0)
				return false;
			if (secondYCoord > 0 && board[secondYCoord - 1][secondXCoord] != 0)
				return false;
			if (secondYCoord > 0 && secondXCoord > 0 && board[secondYCoord - 1][secondXCoord - 1] != 0)
				return false;
			if (secondYCoord > 0 && secondXCoord < 9 && board[secondYCoord - 1][secondXCoord + 1] != 0)
				return false;
			if (secondYCoord < 9 && board[secondYCoord + 1][secondXCoord] != 0)
				return false;
			if (secondYCoord < 9 && secondXCoord < 9 && board[secondYCoord + 1][secondXCoord + 1] != 0)
				return false;
			if (secondYCoord < 9 && secondXCoord > 0 && board[secondYCoord + 1][secondXCoord - 1] != 0)
				return false;
			if (secondXCoord > 0 && board[secondYCoord][secondXCoord - 1] != 0)
				return false;
			if (secondXCoord < 9 && board[secondYCoord][secondXCoord + 1] != 0)
				return false;

			//board[firstYCoord][secondXCoord] = 1;

			if (firstYCoord == secondYCoord)
				break;
			secondYCoord++;
		}

		return true;
	}

	public boolean setShip(int shipLength, int firstXCoord, int firstYCoord, int secondXCoord, int secondYCoord) {
		int[] shipMaxAmounts = {4, 3, 2, 1};
		if (shipLength < 1 || shipLength > shipCounters.length || shipCounters[shipLength - 1] >= shipMaxAmounts[shipLength - 1]
			|| firstXCoord >= 10 || firstXCoord < 0 || firstYCoord >= 10 || firstYCoord < 0
			|| secondXCoord >= 10 || secondXCoord < 0 || secondYCoord >= 10 || secondYCoord < 0
			|| !(firstXCoord == secondXCoord || firstYCoord == secondYCoord) || totalShipCounter > 10 
			|| (Math.pow(firstXCoord - secondXCoord, 2) + Math.pow(firstYCoord - secondYCoord, 2)) != Math.pow(shipLength - 1, 2))
			return false;

		if (firstYCoord == secondYCoord) {
			if (!this.checkCells(firstXCoord, secondXCoord, firstYCoord, secondYCoord))
				return false;
			while (firstXCoord <= secondXCoord) {
				board[firstYCoord][firstXCoord] = 1;
				if (firstXCoord == secondXCoord)
					break;
				firstXCoord++;
			}
			while (secondXCoord <= firstXCoord) {
				board[firstYCoord][secondXCoord] = 1;
				if (firstXCoord == secondXCoord)
					break;
				secondXCoord++;
			}
		}
		else if (firstXCoord == secondXCoord) {
			if (!this.checkCells(firstYCoord, secondYCoord, firstXCoord, secondXCoord))
				return false;
			while (firstYCoord <= secondYCoord) {
				board[firstYCoord][firstXCoord] = 1;
				if (firstYCoord == secondYCoord)
					break;
				firstYCoord++;
			}
			while (secondYCoord <= firstYCoord) {
				board[secondYCoord][secondXCoord] = 1;
				if (firstYCoord == secondYCoord)
					break;
				secondYCoord++;
			}
		}
		shipCounters[shipLength - 1]++;

		totalShipCounter++;

		return true;
	}

	public int getShot(int xCoord, int yCoord) {		
		if (xCoord < 0 || xCoord >= 10 || yCoord < 0 || yCoord >= 10)
			return -2;

		if (board[yCoord][xCoord] == 0) {
			board[yCoord][xCoord] = 3;
			return 3;
		}

		if (board[yCoord][xCoord] == 1) {
			board[yCoord][xCoord] = 2;
			killCounter++;

			if (killCounter == 20)
				return 5;
			else {
				return 2;
			}
		}

		return -1;
	}

	public void doShot(int xCoord, int yCoord, int resultOfShot) {
		boardOpponent[yCoord][xCoord] = resultOfShot;
	}

	public int[][] getBoard() {
		return board;
	}

	public int[][] getOpponentBoard() {
		return boardOpponent;
	}

	public int getTotalShipCounter() {
		return totalShipCounter;
	}
}