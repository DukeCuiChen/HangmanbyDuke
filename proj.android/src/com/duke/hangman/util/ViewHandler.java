package com.duke.hangman.util;

import com.duke.hangman.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;

public class ViewHandler {

	@SuppressLint("InflateParams") 
	public static Button generateButton(Context context, String textValue) {
		Button a = (Button) LayoutInflater.from(context).inflate(R.layout.keyboard_button, null);
		a.setText(textValue);
		return a;
	}

	// updates image of hangman according to number of errors commited
//	public static void hang(Context context, ImageView hangman, int faults) {
//		int id;
//		if (faults < Hangman.LIMIT)
//			id = context.getResources().getIdentifier("hangman_" + faults, "drawable", context.getPackageName());
//		else
//			id = context.getResources().getIdentifier("hangman_" + Hangman.LIMIT, "drawable", context.getPackageName());
//		
//		hangman.setBackgroundResource(id);
//	}
}
