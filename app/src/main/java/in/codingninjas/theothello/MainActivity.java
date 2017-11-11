package in.codingninjas.theothello;

import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private FrameLayout mContainerLayout;
    private Board mBoardLayout;
    private ConstraintLayout mScoreboardLayout;

    public GameState mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScoreboardLayout = (ConstraintLayout) findViewById(R.id.scoreboard_layout);
        mContainerLayout = (FrameLayout) findViewById(R.id.container_layout);
        mBoardLayout = new Board(this);
        mContainerLayout.addView(mBoardLayout);

        setUpBoard();
    }

    private void setUpBoard() {
        GameMode gameMode = (GameMode) getIntent().getSerializableExtra("mode");
        Player player = (Player) getIntent().getSerializableExtra("player");
        mState = new GameState(mBoardLayout);
        mState.clearBoard();
        mState.setUpGame(gameMode, player);
        mState.setUpScoreboard(mScoreboardLayout);
        mBoardLayout.initState(mState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_game) {
            mContainerLayout.removeAllViews();
            startActivity(new Intent(MainActivity.this, NewGameActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
