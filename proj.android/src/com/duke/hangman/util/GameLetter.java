package com.duke.hangman.util;


public class GameLetter extends Letter{

	private boolean visible;
	
	public GameLetter(String letter, boolean visible) {
		super(letter);
		this.visible = visible;

	}
	
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
