package com.djavid.game.Screens;

import com.badlogic.gdx.Screen;


public interface NetScreen extends Screen {

    void updateConnectionInfo(boolean connected);

    void displayErrorMessage(String message);
}
