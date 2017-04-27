package com.djavid.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.djavid.game.Screens.MainMenuScreen;
import com.djavid.game.Network.Packets.*;


public class StartScreen extends Game {

    GameClient gameClient;
    private boolean exitToMenuFlag;
    private boolean startGameFlag;
    private GameSetup startGameInfo;


    public void create() {
        gameClient = new GameClient(this);
        setMenuScreen();
    }

    public void render() {
        super.render();

        //If another thread has flagged the game to begin, then swap to the game screen
        if(startGameFlag)
        {
            setGameScreen(startGameInfo);
            startGameFlag = false;
        }

        //If another thread has flagged the game to exit to menu, then exit to the main menu
        if(exitToMenuFlag)
        {
            setMenuScreen();
            exitToMenuFlag = false;
        }
    }

    private void setMenuScreen()
    {
        setScreen(new MainMenuScreen(this, gameClient));
    }

    private void setGameScreen(GameSetup setupInfo)
    {
        ChessGame screen = new ChessGame(this, gameClient, setupInfo);
        gameClient.setCurrentScreen(screen);
        setScreen(screen);
    }

    /**
     * Other threads can call this to allow the start of games from outside the rendering thread
     */
    public void setGameInfo(GameSetup setupInfo)
    {
        startGameFlag = true;
        startGameInfo = setupInfo;
    }

    /**
     * Other threads can call this to allow returning to the menu after a game has ended
     */
    public void exitToMenu()
    {
        exitToMenuFlag = true;
    }

    public void dispose() {

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


}
