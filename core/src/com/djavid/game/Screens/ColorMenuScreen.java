package com.djavid.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.djavid.game.Actors.MenuItem;
import com.djavid.game.Actors.MenuType;
import com.djavid.game.ChessGame;
import com.djavid.game.GameClient;
import com.djavid.game.Network.Packets;
import com.djavid.game.StartScreen;


public class ColorMenuScreen extends BaseGameScreen implements NetScreen{
    private Stage stage;
    public static Sprite backgroundSprite;
    public static Texture backgroundTexture;
    GameClient gameClient;


    public ColorMenuScreen(final StartScreen game, final GameClient client) {
        stage = new Stage();

        gameClient = client;
        gameClient.setCurrentScreen(this);
        gameClient.updateCurrentScreen();

        MenuItem whiteMenuItem = new MenuItem(MenuType.COLOR_WHITE);
        MenuItem blackMenuItem = new MenuItem(MenuType.COLOR_BLACK);

        whiteMenuItem.setPosition(getScreenSize().x / 4 - whiteMenuItem.getWidth() / 2, (getScreenSize().y - whiteMenuItem.getHeight()) / 2);
        blackMenuItem.setPosition(getScreenSize().x / 2 + (getScreenSize().x / 4 - blackMenuItem.getWidth() / 2), (getScreenSize().y - blackMenuItem.getHeight()) / 2);

        whiteMenuItem.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                Packets.GameSetup gameSetup = new Packets.GameSetup();
                gameSetup.color = true;
                ChessGame chess = new ChessGame(game, null, gameSetup);
                game.setScreen(chess);
            }
        });

        blackMenuItem.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                Packets.GameSetup gameSetup = new Packets.GameSetup();
                gameSetup.color = false;
                ChessGame chess = new ChessGame(game, null, gameSetup);
                game.setScreen(chess);
            }
        });

        stage.addActor(whiteMenuItem);
        stage.addActor(blackMenuItem);
        inputMultiplexer.addProcessor(stage);

        backgroundTexture = new Texture("data/background.jpg");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.scale(0.5f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(237, 238, 240, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().getProjectionMatrix().setToOrtho2D(0, 0, (int)getScreenSize().x, (int)getScreenSize().y);
        stage.getViewport().update((int)getScreenSize().x, (int)getScreenSize().y, true);

        stage.getBatch().begin();
        backgroundSprite.draw(stage.getBatch());
        stage.getBatch().end();

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
    public void updateConnectionInfo(boolean connected) {

    }

    @Override
    public void displayErrorMessage(String message) {

    }
}
