package com.djavid.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.djavid.game.Actors.MenuItem;
import com.djavid.game.Actors.MenuType;
import com.djavid.game.ChessGame;
import com.djavid.game.ChessLogic.Chess;
import com.djavid.game.GameClient;
import com.djavid.game.Network.Packets;
import com.djavid.game.StartScreen;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.net.ExtendedNet;
import java.util.Locale;


public class MainMenuScreen extends BaseGameScreen {
    private Stage stage;
    public static Sprite backgroundSprite;
    public static Texture backgroundTexture;
    StartScreen game;


    public MainMenuScreen(final StartScreen game, final GameClient client) {
        this.game = game;

        stage = new Stage();

        MenuItem localGameMenuItem = new MenuItem(MenuType.LOCAL_GAME);
        MenuItem multiplayGameMenuItem = new MenuItem(MenuType.MULTIPLAYER_GAME);
        MenuItem exitGameMenuItem = new MenuItem(MenuType.EXIT_GAME);

        localGameMenuItem.setPosition((getScreenSize().x - localGameMenuItem.getWidth()) / 2, (getScreenSize().y / 4) * 3);
        multiplayGameMenuItem.setPosition((getScreenSize().x - multiplayGameMenuItem.getWidth()) / 2, (getScreenSize().y / 4) * 2);
        exitGameMenuItem.setPosition((getScreenSize().x - exitGameMenuItem.getWidth()) / 2, (getScreenSize().y / 4) * 1);


        localGameMenuItem.addListener(new InputListener() {
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

        exitGameMenuItem.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                Gdx.app.exit();
            }
        });

        multiplayGameMenuItem.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                game.setScreen(new MultiplayerScreen(game, client));
            }
        });

        stage.addActor(localGameMenuItem);
        stage.addActor(multiplayGameMenuItem);
        stage.addActor(exitGameMenuItem);

        inputMultiplexer.addProcessor(stage);

        backgroundTexture = new Texture("data/background.jpg");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.scale(0.5f);
    }

    public void setChess(ChessGame g) {
        this.game.setScreen(g);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(237, 238, 240, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        backgroundSprite.draw(stage.getBatch());
//        stage.getBatch().draw(backgroundSprite, 0, 0, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
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
}
