package in.codingninjas.theothello;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by nsbhasin on 10/08/17.
 */

public class Board extends View {
    public static final int SIZE = 8;

    private int BOARD_SCREEN_SIZE;
    int CELL_SIZE = BOARD_SCREEN_SIZE / SIZE;
    int PIECE_RADIUS = 4 * CELL_SIZE / 10;
    int CELL_PADDING = (CELL_SIZE) / 2;

    private Context mContext;
    private GameState mState;
    private Paint mPaint = new Paint();

    public Board(Context context) {
        super(context);
        init(context, null);
    }

    public Board(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Board(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    int x = (int) (motionEvent.getX() * SIZE / BOARD_SCREEN_SIZE);
                    int y = (int) (motionEvent.getY() * SIZE / BOARD_SCREEN_SIZE);
                    if (x >= SIZE || y >= SIZE || x < 0 || y < 0) {
                        return false;
                    }
                    handleUserMove(x, y);
                    return true;
                }
                return false;
            }
        });
    }

    public void initState(GameState state) {
        mState = state;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (parentHeight > parentWidth) {
            BOARD_SCREEN_SIZE = parentWidth;
        } else {
            BOARD_SCREEN_SIZE = parentHeight;
        }

        this.setMeasuredDimension(BOARD_SCREEN_SIZE, BOARD_SCREEN_SIZE);
        CELL_SIZE = BOARD_SCREEN_SIZE / SIZE;
        PIECE_RADIUS = 4 * CELL_SIZE / 10;
        CELL_PADDING = CELL_SIZE / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);
        for (int i = 0; i < SIZE; i++) {
            canvas.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, BOARD_SCREEN_SIZE, mPaint);
        }
        canvas.drawLine(BOARD_SCREEN_SIZE, 0, BOARD_SCREEN_SIZE, BOARD_SCREEN_SIZE, mPaint);

        for (int i = 0; i < SIZE; i++) {
            canvas.drawLine(0, i * CELL_SIZE, BOARD_SCREEN_SIZE, i * CELL_SIZE, mPaint);
        }
        canvas.drawLine(0, BOARD_SCREEN_SIZE, BOARD_SCREEN_SIZE, BOARD_SCREEN_SIZE, mPaint);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Player piece = mState.getPlayerAt(i, j);
                if (piece == Player.WHITE_PLAYER || piece == Player.BLACK_PLAYER) {
                    if (piece == Player.WHITE_PLAYER) {
                        mPaint.setColor(Color.WHITE);
                    }
                    if (piece == Player.BLACK_PLAYER) {
                        mPaint.setColor(Color.BLACK);
                    }
                    canvas.drawCircle(
                            (i * CELL_SIZE) + CELL_PADDING,
                            (j * CELL_SIZE) + CELL_PADDING,
                            PIECE_RADIUS,
                            mPaint);
                } else if (mState.isEmptySlot(i, j) && mState.isFlippable(i, j)) {
                    if (mState.getCurrentPlayer().equals(Player.BLACK_PLAYER)) {
                        mPaint.setColor(Color.BLACK);
                        Log.i("PossibleTAG", "BLACK -> " + i + ", " + j);
                    } else if (mState.getCurrentPlayer().equals(Player.WHITE_PLAYER)) {
                        mPaint.setColor(Color.WHITE);
                        Log.i("PossibleTAG", "WHITE -> " + i + ", " + j);
                    }
                    canvas.drawCircle(
                            (i * CELL_SIZE) + CELL_PADDING,
                            (j * CELL_SIZE) + CELL_PADDING,
                            PIECE_RADIUS/4,
                            mPaint);
                }
            }
        }
    }

    private void handleUserMove(int x, int y) {
        if (mState.getGameMode().equals(GameMode.SINGLE_PLAYER) && !mState.getCurrentPlayer().equals(mState.getUserPlayer())) {
            Toast.makeText(this.mContext, "It's not your turn!", Toast.LENGTH_LONG).show();
            return;
        }

        if (mState.makeMove(x, y, mState.getCurrentPlayer())) {
            mState.nextTurn();
            mState.updateScoreboard();
        }
    }
}