package com.duke.hangman.util;

import java.util.ArrayList;

import com.duke.hangman.R;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ArrayListAdapter {

    public static ArrayAdapter<GameLetter> addViews(Context c, ArrayList<GameLetter> letters, LinearLayout letterHolder){
        ArrayAdapter<GameLetter> adapter = new ArrayAdapter<GameLetter>(c, R.layout.tv_layout, letters);
        final int adapterCount = adapter.getCount();

        for(int i = 0; i < adapterCount;i++){
            TextView textView = (TextView) adapter.getView(i, null, null);
            GameLetter letter = letters.get(i);
                   
            textView.setText(letter.toString());

            letterHolder.addView(textView);
        }
        return adapter;
    }
}
