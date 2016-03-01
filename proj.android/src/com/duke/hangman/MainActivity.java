package com.duke.hangman;


import org.json.JSONException;
import org.json.JSONObject;

import com.duke.hangman.net.NetHttpPost;
import com.duke.hangman.util.ShooTaoUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	private Dialog mDialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hangman, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//start game
	public void goToGame(View v){
		Log.d("hangman", "goToGame");

		JSONObject obj = new JSONObject();
	    try {
			obj.put("playerId", "dukecui@foxmail.com");
			obj.put("action", "startGame");  
		} catch (JSONException e) {
			e.printStackTrace();
		}  
	    mDialog = ShooTaoUtil.showProgress(this, "", "Play...", false, false);
	    NetHttpPost.excute(obj, new NetHttpPost.NetHttpResponse() {
			
			@Override
			public void onsuccess(String resultStr) {
				 try {
					 	mDialog.dismiss();
						JSONObject resultJson = new JSONObject(resultStr);
						if (resultJson != null){
							Log.d("hangman", "goToGame:" + resultJson);
							Intent i = new Intent(MainActivity.this, HangmanActivity.class);
							i.putExtra("loginsuccess", resultStr);
							startActivity(i);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				
			}
		});
	}
	
}
