package me.zonyitoo.game24;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SplashActivity extends Activity implements OnClickListener {
	
	private Button buttonStartGame;
    private ProgressDialog dialogLoading = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        buttonStartGame = (Button) findViewById(R.id.button_splash_start_game);
        buttonStartGame.setOnClickListener(this);
    }

	@Override
	public void onClick(View arg0) {
        Button b = (Button) arg0;
        b.setEnabled(false);

        ProgressDialog d = new ProgressDialog(this);
        dialogLoading = d;
        d.setTitle(R.string.string_Splash_Loading);
        d.setIndeterminate(true);
        d.setCancelable(false);
        d.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        d.show();

		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);

        finish();
	}

    @Override
    protected void onStop() {
        if (dialogLoading != null) {
            dialogLoading.dismiss();
        }

        super.onStop();
    }
}
