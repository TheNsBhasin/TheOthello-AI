package in.codingninjas.theothello;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class NewGameActivity extends AppCompatActivity {
    private GameMode mGameMode = GameMode.SINGLE_PLAYER;

    private RadioGroup mTypeRadioGroup;

    private Button mStartButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        mTypeRadioGroup = (RadioGroup) findViewById(R.id.type_radio_group);

        mStartButton = (Button) findViewById(R.id.start_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);

        mTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id) {
                    case R.id.computer_radio_button:
                        mGameMode = GameMode.SINGLE_PLAYER;
                        break;
                    case R.id.human_radio_button:
                        mGameMode = GameMode.MULTI_PLAYER;
                        break;
                }
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(NewGameActivity.this, MainActivity.class);
                mainIntent.putExtra("mode", mGameMode);
                mainIntent.putExtra("player", Player.BLACK_PLAYER);
                startActivity(mainIntent);
                finish();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
