package com.djavid.game.Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class MenuItem extends Actor {
    private MenuType type;
    private Texture texture;

    public MenuItem(MenuType type) {

        this.type = type;

        switch (type) {
            case LOCAL_GAME:
                texture = new Texture(Gdx.files.internal("data/local_game.png"));
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                break;
            case MULTIPLAYER_GAME:
                texture = new Texture(Gdx.files.internal("data/multi_game.png"));
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                break;
            case EXIT_GAME:
                texture = new Texture(Gdx.files.internal("data/exit_game.png"));
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                break;
            case COLOR_WHITE:
                texture = new Texture(Gdx.files.internal("data/white_pawn.png"));
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                break;
            case COLOR_BLACK:
                texture = new Texture(Gdx.files.internal("data/black_pawn.png"));
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                break;
            default:
                texture = null;
                break;
        }

        setName(this.getClass().getSimpleName());
        setSize(texture.getWidth(), texture.getHeight());
    }

    public MenuType getMenuType() {
        return type;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(texture, getX(), getY());
    }
}
