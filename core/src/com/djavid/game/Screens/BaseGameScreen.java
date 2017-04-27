package com.djavid.game.Screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;


public class BaseGameScreen implements Screen {
    BitmapFont font;
    InputMultiplexer inputMultiplexer;
    Vector2 screenSize;

    public InputMultiplexer getInputMultiplexer() {
        return inputMultiplexer;
    }

    public Vector2 getScreenSize() {
        return screenSize;
    }

    public BaseGameScreen() {

        screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font = new BitmapFont();
        inputMultiplexer = new InputMultiplexer();

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
