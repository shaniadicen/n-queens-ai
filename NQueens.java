import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Shania Dicen
 *
 */
public class NQueens {

	public static void main(String[] args) {
		NQueens program = new NQueens();
		program.run();

	}

	public void run() {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter an integer for N-Queens (N>3): ");
		int size = Integer.parseInt(sc.nextLine());
		sc.close();

		if (size <= 3) {
			System.out.println("Board size must be greater than 3.");
		} else {
			Board startBoard = new Board(size);

			System.out.println("=================Board Specification=================\n" + "N-Queens Board Size: "
					+ size + "\nEvaluation Function: " + startBoard.getSafeQueenPairs()
					+ "\nStart Board Configuration:\n" + startBoard.toString());

			System.out.println("--Results for Hill Climbing Algorithm--\n");
			hillClimbing(startBoard);

			System.out.println("\n\n--Results for Random Restart Algorithm--\n");
			randomRestart(startBoard);
		}
	}

	/**
	 * Implementation of the Hill Climbing Algorithm for N-Queens Problem.
	 *
	 * @param board
	 */
	public void hillClimbing(Board board) {
		boolean isLocalMax = false, continueSearch = true;
		Board currentBoard = new Board(board.getBoard()); // copy of the board
		int iterations = 0;
		int globalMax = getGoalValue(currentBoard.getBoard().length);

		while (continueSearch) {
			if (currentBoard.getSafeQueenPairs() == globalMax) { // check if the board configuration is the goal board
				System.out.println("=================Solution Found=================" + "\nNumber of iterations: "
						+ iterations + "\nEvaluation Function: " + currentBoard.getSafeQueenPairs()
						+ "\nBoard Configuration:\n" + currentBoard.toString());
				continueSearch = false;
				break;
			} else {
				for (int i = 0; i < currentBoard.getBoard().length; i++) {
					Board bestSuccessor = generateSuccessor(currentBoard, i);
					if (bestSuccessor.getSafeQueenPairs() > currentBoard.getSafeQueenPairs()) {
						currentBoard = bestSuccessor;
						iterations++;
						isLocalMax = false;
					} else {
						isLocalMax = true;
					}
				}

				if (isLocalMax) {
					System.out.println(
							"=================Local Maximum Encoutered=================" + "\nNumber of iterations: "
									+ iterations + "\nEvaluation Function: " + currentBoard.getSafeQueenPairs()
									+ "\nLocal Maximum Board Configuration:\n" + currentBoard.toString());
					continueSearch = false;
					break;
				}
			}
		}
	}

	/**
	 * Implementation of the Random Restart Algorithm for N-Queens Problem.
	 *
	 * @param board
	 */
	public void randomRestart(Board board) {
		Board currentBoard = new Board(board.getBoard());
		int iterations = 0, numRestarts = 0, numLocalMax = 0, totalIterations = 0, average = 0;
		int globalMax = getGoalValue(currentBoard.getBoard().length);
		boolean isLocalMax = false, continueSearch = true;

		while (continueSearch) {
			totalIterations += iterations;
			if (currentBoard.getSafeQueenPairs() == globalMax) { // check if the board configuration is the goal board

				try {
					average = totalIterations / numLocalMax;
				} catch (Exception e) {

				}

				System.out.println("=================Solution Found=================" + "\nEvaluation Function: "
						+ currentBoard.getSafeQueenPairs() + "\nNumber of Restarts: " + numRestarts
						+ "\nAverage length of each 'run': " + average + "\nNumber of Iterations: " + iterations
						+ "\nBoard Configuration:\n" + currentBoard.toString());
				continueSearch = false;
				break;
			} else {
				for (int x = 0; x < currentBoard.getBoard().length; x++) {
					Board bestSuccessor = generateSuccessor(currentBoard, x);
					if (bestSuccessor.getSafeQueenPairs() > currentBoard.getSafeQueenPairs()) {
						currentBoard = bestSuccessor;
						iterations++;
						isLocalMax = false;
					} else {
						isLocalMax = true;
					}
				}

				if (isLocalMax) {
					numRestarts++;
					numLocalMax++;
					currentBoard = new Board(currentBoard.getBoard().length);
				}
			}
		}
	}

	/**
	 * Generates the best successor based from the given board configuration. Moves
	 * the Queen on a specified row to an unoccupied column
	 *
	 * @return
	 */
	public Board generateSuccessor(Board board, int row) {
		ArrayList<Board> children = new ArrayList<Board>();
		Board bestChild;

		for (int col = 0; col < board.getBoard().length; col++) {
			if (board.getBoard()[row][col] != 1) { // the element is not a queen
				int child[][] = new int[board.getBoard().length][board.getBoard().length];
				child[row][col] = 1; // move queen to this column

				for (int i = 0; i < child.length; i++) {
					if (i != row) {
						child[i] = board.getBoard()[i];
					}
				}
				children.add(new Board(child)); // create board object from the generated new board
				// add successor to children list
			}
		}

		bestChild = children.get(0);

		for (int z = 1; z < children.size(); z++) {
			int bestEv = bestChild.getSafeQueenPairs();
			int nextEv = children.get(z).getSafeQueenPairs();

			if (nextEv > bestEv) {
				bestChild = children.get(z);
			} else if (nextEv == bestEv) {
				Random rand = new Random();
				int choose = (int) (rand.nextInt(2));
				if (choose == 1) {
					bestChild = children.get(z);
				}
			}
		}

		return bestChild;

	}

	/**
	 * Returns the sum of the integers from 1 up to (N-1) [sum(1,2,3,...,N-1)]
	 *
	 * @return
	 */
	public int getGoalValue(int size) {
		int sum = 0;
		for (int i = 0; i < size; i++) {
			sum += i;
		}
		return sum;
	}

}

class Board {
	private int board[][];
	private int safeQueenPairs; // number of non-attacking pair of queens

	/**
	 * Generates a board object from a given 2 dimensional board array
	 *
	 * @param b
	 */
	public Board(int[][] b) {
		this.board = b;
		ev();
	}

	/**
	 * Generates a board with random placement of queens on each row given the board
	 * size.
	 *
	 * @param size
	 */
	public Board(int size) {
		board = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = 0;
			}
		}
		generateRandomQueens(size);
		ev();
	}

	/**
	 * Generates a random queen on each row of the matrix given the board size.
	 *
	 * @param size
	 */
	public void generateRandomQueens(int size) {
		Random rand = new Random();
		for (int i = 0; i < size; i++) {
			board[i][rand.nextInt(size - 1)] = 1;
		}
	}

	public int ev() {
		safeQueenPairs = 0;
		ArrayList<Integer> pos = getQueenPositions();

		for (int i = 0; i < board.length - 1; i++) {
			safeQueenPairs += countSafe(pos, i);
		}

		return safeQueenPairs;
	}

	public int countSafe(ArrayList<Integer> pos, int row) {
		int i, safePairQ = 0, count = 0;

		for (i = row; i < board.length - 1; i++) {
			count = 0;
			if (pos.get(row) == pos.get(i + 1)) { // check if same column
				count++;
			}

			if ((pos.get(row) + row) == (pos.get(i + 1) + (i + 1))) { // check diagonal
				count++;
			}

			if ((pos.get(row) - row) == (pos.get(i + 1) - (i + 1))) { // check diagonal
				count++;
			}

			if (count <= 0) {
				safePairQ++;
			}
		}
		return safePairQ;

	}

	public ArrayList<Integer> getQueenPositions() {
		ArrayList<Integer> pos = new ArrayList<Integer>();
		for (int i = 0; i < board.length; i++) {
			pos.add(getColumn(i));
		}
		return pos;
	}

	/**
	 * @return the board
	 */
	public int[][] getBoard() {
		return board;
	}

	/**
	 * @param board
	 *            the board to set
	 */
	public void setBoard(int[][] board) {
		this.board = board;
	}

	/**
	 * @return the safeQueenPairs
	 */
	public int getSafeQueenPairs() {
		return safeQueenPairs;
	}

	/**
	 * @param safeQueenPairs
	 *            the safeQueenPairs to set
	 */
	public void setSafeQueenPairs(int safeQueenPairs) {
		this.safeQueenPairs = safeQueenPairs;
	}

	/**
	 * Returns the index of a column given a row (index of queen on a given row)
	 *
	 * @param row
	 * @return index
	 */
	public int getColumn(int row) {
		int index = 0;
		for (int i = 0; i < board.length; i++) {
			if (board[row][i] == 1) {
				index = i;
			}
		}

		return index;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String b = "";
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i][j] == 0) {
					b += " * ";
				} else {
					b += " Q ";
				}

			}
			b += "\n";
		}
		return b;
	}

}
