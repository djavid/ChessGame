package com.djavid.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.djavid.game.GameClient;
import com.djavid.game.StartScreen;


public class MultiplayerScreen extends BaseGameScreen implements NetScreen, InputProcessor {
    private Stage stage;
    public static SpriteBatch backgroundSprite;
    public static Texture backgroundTexture;
    StartScreen game;

    Actors mainMenuActors;
    GameClient gameClient;


    public MultiplayerScreen(final StartScreen game, final GameClient client) {
        this.game = game;

        //Assign SpriteBatch and textures
        backgroundSprite = new SpriteBatch();
        backgroundTexture = new Texture("data/background.jpg");
        stage = new Stage(new ScreenViewport(), backgroundSprite);

        //Setup the actors
        mainMenuActors = new Actors(this, game, client);
        stage.addActor(mainMenuActors.getDisconnectedWidgets());
        stage.addActor(mainMenuActors.getMenuWidgets());
        stage.addActor(mainMenuActors.getMatchmakingWidgets());

        //Assign GameClient
        gameClient = client;
        gameClient.setCurrentScreen(this);
        gameClient.updateCurrentScreen();

        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(237, 238, 240, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().getProjectionMatrix().setToOrtho2D(0, 0, (int)getScreenSize().x, (int)getScreenSize().y);
        stage.getViewport().update((int)getScreenSize().x, (int)getScreenSize().y, true);

        backgroundSprite.begin();
        backgroundSprite.draw(backgroundTexture,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundSprite.end();

        stage.act(delta);
        stage.draw();
        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getBatch().getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void updateConnectionInfo(boolean connected)
    {
        if (connected)
        {
            mainMenuActors.updateConnectionInfo(true);
        } else
        {
            mainMenuActors.updateConnectionInfo(false);
        }
    }

    @Override
    public void displayErrorMessage(String message)
    {
        mainMenuActors.displayErrorMessage(message);
    }

    /**
     * Called when the connect button is pressed
     * Attempts to connect to the remote server through the gameClient with the input address and player name
     */
    public void connectButtonPressed(String ipAdress)
    {
        displayErrorMessage("");
        gameClient.attemptConnection(ipAdress);
    }

    @Override
    public void dispose () {
        backgroundSprite.dispose();
        backgroundTexture.dispose();
    }

    public StartScreen getGame()
    {
        return game;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode)
        {
            case Input.Keys.ESCAPE:
                game.exitToMenu();
                //dsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvsdsvs

                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
