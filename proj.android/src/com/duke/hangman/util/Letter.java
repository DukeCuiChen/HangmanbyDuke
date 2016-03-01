package com.duke.hangman.util;


public abstract class Letter {
    private String letter;

    public Letter(String letter){
    	this.letter = letter;
    }
    
    //used for storing the data onDestroy. Ex: screen flip
    public char getCharLetter() {
        return letter.charAt(0);
    }
    
   
    public void setLetter(String letter) {
        this.letter = letter;
    }
   
    public String toString() {
    	return letter;
    }

}
