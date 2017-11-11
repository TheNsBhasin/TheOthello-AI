package in.codingninjas.theothello;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by nsbhasin on 12/08/17.
 */

public class AIPlayer {
    private Move bestMove;          //Stores the row and col value for best move
    private int positons;
    private int numberOfWhite;      //Store the Number or White Discs on the Board
    private int numberOfBlack;      //Store the Number or Black Discs on the Board
    private int nofwins;            //Stores the Number of Won Matches
    private int nooflosses;         //Store the Number of Lost Matches
    private int noofdraws;
    private int size;

    public AIPlayer() {
        this.positons = 0;
        this.nofwins = 0;
        this.nooflosses = 0;
        this.noofdraws = 0;
    }

    private int MiniMax(GameState board, Player player, int maxDepth, int currentDepth, int alpha, int beta) {
        Move bestMove = new Move(-1, -1);
        int bestScore = 0;
        int score = 0;

        ArrayList<Move> moveList = new ArrayList<>();
        ArrayList<Move> moveDirection = new ArrayList<>();
        ArrayList<Move> moveListNoDuplicates = new ArrayList<>();

        if (currentDepth == maxDepth)
            return evaluate(board, player);

        if (player.equals(Player.BLACK_PLAYER))
            bestScore = alpha;
        else
            bestScore = beta;

        if (player.equals(Player.BLACK_PLAYER)) {
            GenerateMoveList(board, Player.WHITE_PLAYER, moveList, moveDirection);
        } else
            GenerateMoveList(board, Player.BLACK_PLAYER, moveList, moveDirection);

        MoveListWithOutDuplicates(moveList, moveListNoDuplicates);

        GameState tempBoard = new GameState(board);
        for (int index = 0; index < moveListNoDuplicates.size(); index++) {

            positons++;
            MakeMove(tempBoard, player, moveListNoDuplicates.get(index), moveList, moveDirection);

            if (player.equals(Player.WHITE_PLAYER)) {
                score = MiniMax(tempBoard, Player.BLACK_PLAYER, maxDepth, currentDepth + 1, alpha, beta);
            } else {
                score = MiniMax(tempBoard, Player.WHITE_PLAYER, maxDepth, currentDepth + 1, alpha, beta);
            }

            if (player.equals(Player.BLACK_PLAYER)) {
                if (score > bestScore) {
                    alpha = bestScore = score;
                    bestMove = moveListNoDuplicates.get(index);
                }
            } else {
                if (score < bestScore) {
                    beta = bestScore = score;
                    bestMove = moveListNoDuplicates.get(index);
                }
            }
            if (alpha >= beta) {
                return bestScore;
            }
        }
        if (moveList.size() == 0) {
            bestScore = evaluate(board, player);
        }
        if (currentDepth != 0) {
            return bestScore;
        } else {
            bestScore = score;
            this.bestMove = bestMove;
            board.makeMove(bestMove.getRow(), bestMove.getCol(), player);
            return 1;
        }
    }

    private void MakeMove(GameState board, Player player, Move move, ArrayList<Move> moveList, ArrayList<Move> moveDirection) {
        for (int i = 0; i < moveList.size(); i++) {
            if (moveList.get(i).getRow() == move.getRow() && moveList.get(i).getCol() == move.getCol()) {
                int r = moveDirection.get(i).getRow() - move.getCol();
                int c = moveDirection.get(i).getCol() - move.getCol();
                int row = move.getRow();
                int col = move.getCol();
                while (true) {
                    row += r;
                    col += c;
                    if (row < 0 || col < 0 || row >= Board.SIZE || col >= Board.SIZE || board.getPlayerAt(row, col).equals(player)) {
                        break;
                    }
                    board.placePlayer(row, col, player);
                }
            }
        }
        board.placePlayer(move.getRow(), move.getCol(), player);
    }

    private void GenerateMoveList(GameState board, Player player, ArrayList<Move> moveList, ArrayList<Move> moveDirection) {
        for (int row = 0; row <= Board.SIZE; row++) {
            for (int col = 0; col <= Board.SIZE; col++) {
                if (board.getPlayerAt(row, col).equals(player)) {
                    LookAround(board, player, moveList, moveDirection, row, col);
                }
            }
        }
    }

    private void LookAround(GameState board, Player player, ArrayList<Move> moveList, ArrayList<Move> moveDirection, int row, int col) {
        for (int r = -1; r < 2; r++) {
            if (row + r < 0 || row + r >= Board.SIZE) {
                continue;
            }

            for (int c = -1; c < 2; c++) {
                if (col + c < 0 || col + c >= Board.SIZE) {
                    continue;
                }

                if (board.getPlayerAt(row + r, col + c).equals(Player.NO_PLAYER)) {
                    if (isMovePossible(board, player, row + r, col + c, -r, -c)) {
                        Move move = new Move(row + r, col + c);
                        moveList.add(move);
                        move = new Move(row, col);
                        moveDirection.add(move);
                    }
                }
            }
        }
    }

    private boolean isMovePossible(GameState board, Player player, int row, int col, int r, int c) {
        int temp_row = row;
        int temp_col = col;
        while (true) {
            temp_row += r;
            temp_col += c;
            if (temp_row < 0 || temp_row >= Board.SIZE || temp_col < 0 || temp_col >= Board.SIZE || board.getPlayerAt(temp_row, temp_col).equals(Player.NO_PLAYER)) {
                return false;
            }

            if (player.equals(Player.WHITE_PLAYER) && board.getPlayerAt(temp_row, temp_col).equals(Player.BLACK_PLAYER)) {
                return true;

            }
            if (player.equals(Player.BLACK_PLAYER) && board.getPlayerAt(temp_row, temp_col).equals(Player.WHITE_PLAYER)) {
                return true;
            }
        }
    }

    private int evaluate(GameState board, Player player) {
        int corners = 0;

        numberOfBlack = board.countScore(Player.BLACK_PLAYER);
        numberOfWhite = board.countScore(Player.WHITE_PLAYER);

        if (numberOfBlack > numberOfWhite) {
            if (board.getPlayerAt(0, 0).equals(Player.BLACK_PLAYER)) {
                corners++;
            }
            if (board.getPlayerAt(0, Board.SIZE - 1).equals(Player.BLACK_PLAYER)) {
                corners++;
            }
            if (board.getPlayerAt(Board.SIZE - 1, 0).equals(Player.BLACK_PLAYER)) {
                corners++;
            }
            if (board.getPlayerAt(Board.SIZE - 1, Board.SIZE - 10).equals(Player.BLACK_PLAYER)) {
                corners++;
            }

            nofwins++;
            return 1 + corners;
        } else if (numberOfWhite > numberOfBlack) {
            if (board.getPlayerAt(0, 0).equals(Player.WHITE_PLAYER)) {
                corners--;
            }
            if (board.getPlayerAt(0, Board.SIZE - 1).equals(Player.WHITE_PLAYER)) {
                corners--;
            }
            if (board.getPlayerAt(Board.SIZE - 1, 0).equals(Player.WHITE_PLAYER)) {
                corners--;
            }
            if (board.getPlayerAt(Board.SIZE - 1, Board.SIZE - 1).equals(Player.WHITE_PLAYER)) {
                corners--;
            }
            nooflosses++;
            return -1 + corners;
        } else {
            noofdraws++;
            return 0;
        }
    }

    private void MoveListWithOutDuplicates(ArrayList<Move> moveList, ArrayList<Move> moveListNoDuplicates) {
        boolean found;
        for (int i = 0; i < moveList.size(); i++) {
            found = false;

            for (int j = 0; j < moveListNoDuplicates.size(); j++) {
                if (moveList.get(i).getRow() == moveListNoDuplicates.get(j).getCol() && moveList.get(i).getCol() == moveListNoDuplicates.get(j).getCol()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                moveListNoDuplicates.add(moveList.get(i));
            }

        }
    }

    public int GetComputerMove(GameState board, Player player) {
        positons = 0;
        noofdraws = 0;
        nofwins = 0;
        nooflosses = 0;
        return MiniMax(board, player, 7, 0, -100, +100);
    }
}
