package me.zonyitoo.game24.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import me.zonyitoo.game24.R;

public class SplashActivity extends Activity implements OnClickListener {
	
	private Button buttonStartGame;
    private ProgressDialog dialogLoading = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Splash screen don't need the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);
        
        buttonStartGame = (Button) findViewById(R.id.button_splash_start_game);
        buttonStartGame.setOnClickListener(this);
    }

	@Override
	public void onClick(View arg0) {
        Button b = (Button) arg0;
        b.setEnabled(false);
        new SwitchingActivityAsyncTask().execute();
	}

    private class SwitchingActivityAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Intent intent = new Intent(SplashActivity.this, GameActivity.class);
            startActivity(intent);

            return null;
        }

        @Override
        protected void onPreExecute() {
            ProgressDialog d = new ProgressDialog(SplashActivity.this);
            dialogLoading = d;
            d.setTitle(R.string.string_Splash_Loading);
            d.setIndeterminate(true);
            d.setCancelable(false);
            d.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            d.show();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialogLoading.dismiss();
            SplashActivity.this.finish();

            super.onPostExecute(aVoid);
        }
    }
}
