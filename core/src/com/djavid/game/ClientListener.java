package com.djavid.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.djavid.game.ChessLogic.Chess;
import com.djavid.game.ChessLogic.Coord;
import com.djavid.game.Screens.MultiplayerScreen;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.djavid.game.Network.Packets.*;


public class ClientListener extends Listener
{
    GameClient gameClient;


    public ClientListener(GameClient g)
    {
        super();

        gameClient = g;
    }

    public void connected(Connection connection)
    {
        System.out.println("Connected");
        gameClient.setServerConnection(connection);
        gameClient.requestGameStart();
    }

    public void disconnected(Connection connection)
    {
        gameClient.getStartScreen().exitToMenu();
        gameClient.updateCurrentScreen();
        gameClient.displayErrorMessage("You have disconnected!");
    }

    public void received(Connection connection, Object o)
    {
        if(gameClient.getCurrentScreen() instanceof MultiplayerScreen)
        {
            if (o instanceof GameSetup)
            {
                System.out.println("Got packet GameSetup");
                gameClient.startGame((GameSetup) o);
            }
        }
        else if(gameClient.getCurrentScreen() instanceof ChessGame)
        {
            if(o instanceof GameEndDisconnect)
            {
                disconnected(connection);
                System.out.println("Got packet GameEndDisconnect");
                gameClient.displayErrorMessage("Opponent disconnected!");
            }
            else if (o instanceof Move) {
                System.out.println("Got packet Move");

                Move move = (Move)o;
                System.out.println(move.toString());
                Coord figureToMove = new Coord(move.figureX, move.figureY, false).invert();

                Chess game = ((ChessGame) gameClient.getCurrentScreen()).game;

                game.getPieceByCoord(figureToMove).move(move, game, true);
                if (move.eats) {
                    ((ChessGame) gameClient.getCurrentScreen()).cleanEatedFigures();
                }

                Label label = ((ChessGame) gameClient.getCurrentScreen()).labelInfo;

                if (game.Check(!game.getPieceByCoord(figureToMove).color)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.setLength(0);
                    stringBuilder.append("Check;");
                    label.setPosition(Gdx.graphics.getWidth() / 2 - label.getWidth() * 2.5f,
                            Gdx.graphics.getHeight() / 2 - label.getHeight() * 2.5f);
                    label.setText(stringBuilder);
                    label.setVisible(true);
                } else {
                    label.setVisible(false);
                }

                if (move.checkmate) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.setLength(0);
                    stringBuilder.append("You lost!\nESC to exit");
                    label.setPosition(Gdx.graphics.getWidth() / 2,
                            Gdx.graphics.getHeight() / 2);
                    label.setText(stringBuilder);
                    label.setVisible(true);
                }

                boolean color = ((ChessGame) gameClient.getCurrentScreen()).my_color;
                ((ChessGame) gameClient.getCurrentScreen()).unsetBlocked(color);
            }
        }
    }
}
