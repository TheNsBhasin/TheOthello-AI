package in.codingninjas.theothello;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static in.codingninjas.theothello.Board.SIZE;

/**
 * Created by nsbhasin on 13/08/17.
 */

public class GameState {
    private Player[][] mBoard;

    private Player mCurrentPlayer;
    private Player mUserPlayer;
    private AIPlayer mAIPlayer;
    private GameMode mGameMode;
    private Board mBoardView;

    private ImageView mBlackTurn;
    private ImageView mWhiteTurn;
    private TextView mBlackScore;
    private TextView mWhiteScore;

    public GameState(Board mBoardView) {
        this.mBoardView = mBoardView;
        this.mBoard = new Player[SIZE][SIZE];
        mAIPlayer = new AIPlayer();

        clearBoard();
    }

    public GameState(GameState gameState) {
        this.mCurrentPlayer = gameState.mCurrentPlayer;
        this.mAIPlayer = gameState.mAIPlayer;
        this.mBoard = new Player[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (gameState.mBoard[i][j].equals(Player.BLACK_PLAYER)) {
                    this.mBoard[i][j] = Player.BLACK_PLAYER;
                } else if (gameState.mBoard[i][j].equals(Player.WHITE_PLAYER)) {
                    this.mBoard[i][j] = Player.WHITE_PLAYER;
                } else {
                    this.mBoard[i][j] = Player.NO_PLAYER;
                }
            }
        }
    }

    public void clearBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                this.mBoard[i][j] = Player.NO_PLAYER;
            }
        }

        placePlayer(SIZE / 2 - 1, SIZE / 2, Player.BLACK_PLAYER);
        placePlayer( SIZE / 2 - 1,SIZE / 2 - 1, Player.WHITE_PLAYER);
        placePlayer(SIZE / 2, SIZE / 2 - 1, Player.BLACK_PLAYER);
        placePlayer(SIZE / 2, SIZE / 2, Player.WHITE_PLAYER);


    }

    public void placePlayer(int x, int y, Player player) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
            return;
        }

        mBoard[x][y] = player;
    }

    public void nextTurn() {
        this.mCurrentPlayer = this.mCurrentPlayer.equals(Player.BLACK_PLAYER) ? Player.WHITE_PLAYER : Player.BLACK_PLAYER;
        Log.i("Turn", this.mCurrentPlayer.equals(Player.BLACK_PLAYER) ? "BLACK" : "WHITE");
        mBoardView.postInvalidate();
        if (checkWin()) {
            determineWinner();
        } else if (mGameMode.equals(GameMode.SINGLE_PLAYER) && !mCurrentPlayer.equals(mUserPlayer)) {
            new AITask().execute(this);
        }
    }

    public void setUpScoreboard(View scoreboardLayout) {
        mBlackScore = scoreboardLayout.findViewById(R.id.black_score);
        mWhiteScore = scoreboardLayout.findViewById(R.id.white_score);
        mBlackTurn = scoreboardLayout.findViewById(R.id.black_image);
        mWhiteTurn = scoreboardLayout.findViewById(R.id.white_image);
        updateScoreboard();
    }

    public void setUpGame(GameMode mode, Player player) {
        mGameMode = mode;
        mUserPlayer = player;
        mCurrentPlayer = mUserPlayer.equals(Player.BLACK_PLAYER) ? Player.WHITE_PLAYER : Player.BLACK_PLAYER;
        nextTurn();
        updateScoreboard();
    }

    void updateScoreboard() {
        if (mBlackScore == null || mWhiteScore == null || mBlackTurn == null || mWhiteTurn == null) {
            return;
        }
        mBlackScore.setText(String.valueOf(countScore(Player.BLACK_PLAYER)));
        mWhiteScore.setText(String.valueOf(countScore(Player.WHITE_PLAYER)));
        if (mCurrentPlayer.equals(Player.BLACK_PLAYER)) {
            mBlackTurn.setVisibility(View.VISIBLE);
            mWhiteTurn.setVisibility(View.INVISIBLE);
        } else if (mCurrentPlayer.equals(Player.WHITE_PLAYER)) {
            mBlackTurn.setVisibility(View.INVISIBLE);
            mWhiteTurn.setVisibility(View.VISIBLE);
        }

    }

    public boolean makeMove(int x, int y, Player player) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
            return false;
        }
        if (isEmptySlot(x, y) && isFlippable(x, y)) {
            placePlayer(x, y, player);
            flipDisc(x, y);
            return true;
        }
        return false;
    }

    public Player getCurrentPlayer() {
        return mCurrentPlayer;
    }

    public Player getUserPlayer() {
        return mUserPlayer;
    }

    public GameMode getGameMode() {
        return mGameMode;
    }

    public Player getPlayerAt(int i, int j) {
        if (i < 0 || j < 0 || i >= SIZE || j >= SIZE) {
            return Player.NO_PLAYER;
        }
        return mBoard[i][j];
    }

    public boolean isEmptySlot(int x, int y) {
        return mBoard[x][y].equals(Player.NO_PLAYER);
    }

    public boolean isFlippable(int row, int col) {
        if (row < 0 || col < 0 || row >= SIZE || col >= SIZE) {
            return false;
        }

        boolean flippable = false;

        for (int dirRow = -1; dirRow < 2; dirRow++) {
            for (int dirCol = -1; dirCol < 2; dirCol++) {

                if (dirRow == 0 && dirCol == 0) {
                    continue;
                }

                int newRow = row + dirRow;
                int newCol = col + dirCol;

                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {

                    Player oppoPlayer = mCurrentPlayer.equals(Player.WHITE_PLAYER) ? Player.BLACK_PLAYER : Player.WHITE_PLAYER;
                    if (mBoard[newRow][newCol].equals(oppoPlayer)) {
                        for (int range = 1; range < 8; range++) {

                            int nRow = row + range * dirRow;
                            int nCol = col + range * dirCol;

                            if (nRow < 0 || nRow > 7 || nCol < 0 || nCol > 7) {
                                continue;
                            }

                            if (mBoard[nRow][nCol].equals(Player.NO_PLAYER)) {
                                break;
                            }

                            if (mBoard[nRow][nCol].equals(mCurrentPlayer)) {
                                flippable = true;
                                Log.i("FlipTAG", newRow + ", " + newCol);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return flippable;
    }

    private void flipDisc(int row, int col) {
        for (int dirRow = -1; dirRow < 2; dirRow++) {
            for (int dirCol = -1; dirCol < 2; dirCol++) {

                if (dirRow == 0 && dirCol == 0) {
                    continue;
                }

                int newRow = row + dirRow;
                int newCol = col + dirCol;

                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                    Player oppoPlayer = mCurrentPlayer.equals(Player.WHITE_PLAYER) ? Player.BLACK_PLAYER : Player.WHITE_PLAYER;

                    if (mBoard[newRow][newCol].equals(oppoPlayer)) {
                        for (int range = 1; range < 8; range++) {

                            int nRow = row + range * dirRow;
                            int nCol = col + range * dirCol;

                            if (nRow < 0 || nRow > 7 || nCol < 0 || nCol > 7) {
                                continue;
                            }

                            if (mBoard[nRow][nCol].equals(mCurrentPlayer)) {
                                boolean canFlip = true;
                                for (int dist = 1; dist < range; dist++) {

                                    int testRow = row + dist * dirRow;
                                    int testCol = col + dist * dirCol;

                                    if (!mBoard[testRow][testCol].equals(oppoPlayer)) {
                                        canFlip = false;
                                    }
                                }

                                if (canFlip) {
                                    for (int flipDist = 1; flipDist < range; flipDist++) {

                                        int finalRow = row + flipDist * dirRow;
                                        int finalCol = col + flipDist * dirCol;

                                        if (mBoard[finalRow][finalCol].equals(oppoPlayer)) {
                                            mBoard[finalRow][finalCol] = mCurrentPlayer;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isFreeSlotLeft() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (mBoard[row][col].equals(Player.NO_PLAYER)) {
                    return true;
                }
            }
        }
        Log.i("WINTag", "No free slots left");
        return false;
    }

    private boolean movesExist(Player player) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!isEmptySlot(row, col)) {
                    continue;
                } else if (isFlippable(row, col)) {
                    return true;
                }
            }
        }
        Log.i("WINTag", "No moves Exist");
        return false;
    }

    public int countScore(Player player) {
        int num = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (mBoard[row][col].equals(player)) {
                    num++;
                }
            }
        }
        return num;
    }

    private boolean checkWin() {
        if (!isFreeSlotLeft()) {
            Log.i("WINTag", "No free slots left");
            return true;
        } else if (mCurrentPlayer.equals(Player.BLACK_PLAYER) && !movesExist(Player.WHITE_PLAYER)) {
            Log.i("WINTag", "No moves exists for WHITE");
            return true;
        } else if (mCurrentPlayer.equals(Player.WHITE_PLAYER) && !movesExist(Player.BLACK_PLAYER)) {
            Log.i("WINTag", "No moves exists for BLACK");
            return true;
        } else {
            return false;
        }
    }

    private void determineWinner() {
        mBlackTurn.setVisibility(View.VISIBLE);
        mWhiteTurn.setVisibility(View.VISIBLE);
        int blackCount = countScore(Player.BLACK_PLAYER);
        int whiteCount = countScore(Player.WHITE_PLAYER);

        AlertDialog.Builder builder = new AlertDialog.Builder(mBoardView.getContext());
        if (blackCount > whiteCount) {
            builder.setTitle("Black Wins !!");
        } else if (blackCount < whiteCount) {
            builder.setTitle("White Wins !!");
        } else {
            builder.setTitle("Match Draw !!");
        }

        builder.setMessage("The final score was " + blackCount + " to " + whiteCount);

        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearBoard();
                setUpGame(mGameMode, mUserPlayer);
            }
        });

        builder.create().show();
        this.mCurrentPlayer = Player.NO_PLAYER;
    }

    private class AITask extends AsyncTask<GameState, Player, Integer> {
        @Override
        protected Integer doInBackground(GameState... gameStates) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("AITask", "processing");
            return mAIPlayer.GetComputerMove(gameStates[0], mUserPlayer.equals(Player.BLACK_PLAYER) ? Player.WHITE_PLAYER : Player.BLACK_PLAYER);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            mBoardView.postInvalidate();
            Log.i("AITask", "invalidated");
            nextTurn();
            updateScoreboard();
        }
    }
}
