package com.duke.hangman;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.duke.hangman.net.NetHttpPost;
import com.duke.hangman.util.ArrayListAdapter;
import com.duke.hangman.util.GameLetter;
import com.duke.hangman.util.ShooTaoUtil;
import com.duke.hangman.util.ViewHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author duke
 *
 */
public class HangmanActivity extends Activity {

    private LinearLayout letterHolder;
	private String sessionId;
	private int totalWord, restTime;
	private int triedWord;
	
	private static boolean letterIsR;
	private boolean isComplete;
	
	private TextView totalText, triedText, restText, correctText, scoreText;
	private ImageView hangedMan;
	private Dialog mDialog;
	
	private ArrayList<Button> invisib = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hangman);
		
		Intent i = getIntent();
		
	    String result = i.getStringExtra("loginsuccess");
	    try {
			JSONObject resultJson = new JSONObject(result);
			if (resultJson != null){
				
				sessionId = resultJson.optString("sessionId");
				JSONObject data = resultJson.optJSONObject("data");
				totalWord = data.optInt("numberOfWordsToGuess");
				restTime = data.optInt("numberOfGuessAllowedForEachWord");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
		initview();
		
		buttonGenerator();
		
		getANewWord();

		
		
	}
	
	private void initview(){
		totalText = (TextView) findViewById(R.id.total);
		triedText = (TextView) findViewById(R.id.tried);
		restText = (TextView) findViewById(R.id.rest);
		correctText = (TextView) findViewById(R.id.correct);
		scoreText = (TextView) findViewById(R.id.scorenum);
		
		letterHolder = (LinearLayout) findViewById(R.id.llHorizontal);
        hangedMan = (ImageView) findViewById(R.id.imageView);
		
        hangedMan.setBackgroundResource(getResources().getIdentifier("hangmanbilde" + 0, "drawable", getPackageName()));
		totalText.setText(getResources().getString(R.string.total) + ":" + totalWord);
		restText.setText(getResources().getString(R.string.rest) + ":" + restTime);
		correctText.setText(getResources().getString(R.string.correct) + ":0");
	}
	
	private void getANewWord(){
		JSONObject obj = new JSONObject();
	    try {
			obj.put("sessionId", sessionId);
			obj.put("action", "nextWord");  
		} catch (JSONException e) {
			e.printStackTrace();
		}  
	    showLoading("get a new word...");
	    NetHttpPost.excute(obj, new NetHttpPost.NetHttpResponse() {
			
			@Override
			public void onsuccess(String resultStr) {
				 try {
					 	hideLoading();
						JSONObject resultJson = new JSONObject(resultStr);
						if (resultJson != null) {
							Log.d("hangman", "getNewWord:" + resultJson);
							JSONObject data = resultJson.optJSONObject("data");
							String newWord = data.optString("word");
							triedWord = data.optInt("totalWordCount");
							restTime -= data.optInt("wrongGuessCountOfCurrentWord");
					        char[] c = newWord.toCharArray();
					        final ArrayList<GameLetter> letters = new ArrayList<GameLetter>();
					        for(int i = 0; i < c.length; i++){
					            letters.add(new GameLetter(c[i]+"", false));
					        }
					        runOnUiThread(new Runnable() {
								public void run() {
									letterHolder.removeAllViews();
							        ArrayListAdapter.addViews(HangmanActivity.this, letters, letterHolder);	
							        triedText.setText(getResources().getString(R.string.tried) + ":" + triedWord);
							        restText.setText(getResources().getString(R.string.rest) + ":" + restTime);
								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} 
				
			}
		});
	}
	
    private void buttonGenerator(){
        String keyboardString = getResources().getString(R.string.keyboard);
        int buttonCount = 0;

        Log.d("hangman", "keyboard count:" + keyboardString.length());
        for(int i = 0; i < keyboardString.length(); i++){
            Button letterButton = ViewHandler.generateButton(this, Character.toString(keyboardString.charAt(i)));
            
            //disable sound, to make room for our own effects on button
            letterButton.setSoundEffectsEnabled(false);
            letterButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	makeAGuess(v);
                }
            });
            
            LinearLayout addToLayout;

            if(buttonCount < 10) addToLayout = (LinearLayout) findViewById(R.id.llTopKeyboard);
            else if(buttonCount < 19) addToLayout = (LinearLayout) findViewById(R.id.llMidKeyboard);
            else  addToLayout = (LinearLayout) findViewById(R.id.llBotKeyboard);

            Log.d("ButtonGenerator", buttonCount + " ");
            addToLayout.addView(letterButton);

            //inflates empty space between buttons
            TextView space = (TextView) LayoutInflater.from(this).inflate(R.layout.keyboard_space, null);
            addToLayout.addView(space);

            buttonCount++;
        }
    }
    
    private void makeAGuess(View view) {
//        if(STATE != 0) return;

        final Button button = (Button) view;
        view.setEnabled(false);
        invisib.add(button);
        
        JSONObject obj = new JSONObject();
	    try {
			obj.put("sessionId", sessionId);
			obj.put("action", "guessWord");
			obj.put("guess", button.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}  
	    Log.d("hangman", "make a guess obj:" + obj);
	    showLoading("make guess");
        NetHttpPost.excute(obj, new NetHttpPost.NetHttpResponse() {
			
			@Override
			public void onsuccess(String resultStr) {
				 try {
					 	hideLoading();
						JSONObject resultJson = new JSONObject(resultStr);
						if (resultJson != null) {
							Log.d("hangman", "make a guess:" + resultJson);
							JSONObject data = resultJson.optJSONObject("data");
							String newWord = data.optString("word");
							triedWord = data.optInt("totalWordCount");
							restTime =10 - data.optInt("wrongGuessCountOfCurrentWord");

					        char[] c = newWord.toCharArray();
					        final ArrayList<GameLetter> letters = new ArrayList<GameLetter>();
					        for(int i = 0; i < c.length; i++){
								//*对应的ASCII表 的值
					        	if(c[i] != 42 ){
					        		letterIsR = true;
					        	}
					            letters.add(new GameLetter(c[i]+"", false));
					        }

					        if(!newWord.contains("*")){
					        	isComplete = true;
					        }
					        runOnUiThread(new Runnable() {
								public void run() {
									letterHolder.removeAllViews();
							        ArrayListAdapter.addViews(HangmanActivity.this, letters, letterHolder);	
							        triedText.setText(getResources().getString(R.string.tried) + ":" + triedWord);
							        restText.setText(getResources().getString(R.string.rest) + ":" + restTime);
							        
							        if (letterIsR) {
							        	button.setTextColor(getResources().getColor(R.color.correct));
									} else {
										button.setTextColor(getResources().getColor(R.color.wrong));
										hangedMan.setBackgroundResource(getResources().getIdentifier("hangmanbilde" + (10-restTime), "drawable", getPackageName()));
									}
							        
							        if(isComplete || restTime == 0){
							        	getResult();
							        	for(Button button:invisib){
							        		button.setEnabled(true);
							        		button.setTextColor(getResources().getColor(R.color.black));
							        	}
							        	hangedMan.setBackgroundResource(getResources().getIdentifier("hangmanbilde" + 0, "drawable", getPackageName()));
							        }

								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} 
				
			}
		});
        
    }
    
    private void getResult(){
    	JSONObject obj = new JSONObject();
	    try {
			obj.put("sessionId", sessionId);
			obj.put("action", "getResult");  
		} catch (JSONException e) {
			e.printStackTrace();
		}  
	    showLoading("get result...");
	    NetHttpPost.excute(obj, new NetHttpPost.NetHttpResponse() {
			
			@Override
			public void onsuccess(String resultStr) {
				 try {
					 	hideLoading();
						JSONObject resultJson = new JSONObject(resultStr);
						if (resultJson != null) {
							Log.d("hangman", "getResult:" + resultJson);
							JSONObject data = resultJson.optJSONObject("data");
							final String correctWordCount = data.optString("correctWordCount");
							final String score = data.optString("score");

					        runOnUiThread(new Runnable() {
								public void run() {
									correctText.setText(getResources().getString(R.string.correct) + ":" + correctWordCount);
									scoreText.setText(score);
									getANewWord();
								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} 
				
			}
		});
    }
    
    public void rePlay(View v){
		Log.d("hangman", "rePlay");

		JSONObject obj = new JSONObject();
	    try {
			obj.put("playerId", "dukecui@foxmail.com");
			obj.put("action", "startGame");  
		} catch (JSONException e) {
			e.printStackTrace();
		}  
	    showLoading("replay...");
	    NetHttpPost.excute(obj, new NetHttpPost.NetHttpResponse() {
			
			@Override
			public void onsuccess(String resultStr) {
				 try {
					 	hideLoading();
						JSONObject resultJson = new JSONObject(resultStr);
						if (resultJson != null){
							Log.d("hangman", "replayGame:" + resultJson);
							sessionId = resultJson.optString("sessionId");
							JSONObject data = resultJson.optJSONObject("data");
							totalWord = data.optInt("numberOfWordsToGuess");
							restTime = data.optInt("numberOfGuessAllowedForEachWord");
							runOnUiThread(new Runnable() {
								public void run() {
						        	for(Button button:invisib){
						        		button.setEnabled(true);
						        		button.setTextColor(getResources().getColor(R.color.black));
						        	}
						        	totalText.setText(getResources().getString(R.string.total) + ":" + totalWord);
						        	correctText.setText(getResources().getString(R.string.correct) + ":0");
						        	scoreText.setText("0");
						        	hangedMan.setBackgroundResource(getResources().getIdentifier("hangmanbilde" + 0, "drawable", getPackageName()));
						        	getANewWord();
								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				
			}
		});
    }
    
    public void submit(View v){
    	Log.d("hangman", "submit");
    	ShooTaoUtil.warnDialog(this, "Are you want to submit?", "Yes", true, new ShooTaoUtil.WarnDialogButtonCallback() {
			
			@Override
			public void isClick() {
				submitHttp();
				
			}
		});
    }
    
    public void submitHttp(){
    	
    	JSONObject obj = new JSONObject();
	    try {
			obj.put("sessionId", sessionId);
			obj.put("action", "submitResult"); 
		} catch (JSONException e) {
			e.printStackTrace();
		}  
	    showLoading("submit...");
	    NetHttpPost.excute(obj, new NetHttpPost.NetHttpResponse() {
			
			@Override
			public void onsuccess(String resultStr) {
				 try {
					 	hideLoading();
						JSONObject resultJson = new JSONObject(resultStr);
						if (resultJson != null) {
							Log.d("hangman", "getResult:" + resultJson);
							JSONObject data = resultJson.optJSONObject("data");
							final String correctWordCount = data.optString("correctWordCount");
							final String score = data.optString("score");
							final String datetime = data.optString("datetime");
							
							String message = "Submit success! Your grade is Here.\n" + "Correct Count:" + correctWordCount + "\n" 
									+ "Score:" + score + "\n" + "Date:" + datetime + "\n";

							ShooTaoUtil.warnDialog(HangmanActivity.this, message, "ok", false, new ShooTaoUtil.WarnDialogButtonCallback() {
								
								@Override
								public void isClick() {
									finish();
									
								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				
			}
		});
    }
    
	private void showLoading(final String text) {
		mDialog = ShooTaoUtil.showProgress(this, "", text, false, false);	

	}
	
	private void hideLoading() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}	
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		ShooTaoUtil.warnDialog(this, "Are you want to exit?", "Yes", true, new ShooTaoUtil.WarnDialogButtonCallback() {
			
			@Override
			public void isClick() {
				finish();
				
			}
		});
		return false;
	}
    
}
