package com.djavid.game;

import com.badlogic.gdx.Screen;
import com.djavid.game.ChessLogic.Figure;
import com.djavid.game.Screens.MultiplayerScreen;
import com.djavid.game.Screens.NetScreen;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import com.djavid.game.Screens.MainMenuScreen;
import com.djavid.game.Network.Packets;
import com.djavid.game.Network.Packets.*;
import java.io.IOException;


public class GameClient {
    private Client client;
    private Connection serverConnection;

    private NetScreen currentScreen;
    private StartScreen  startScreen;


    public GameClient(StartScreen startScreen)
    {
        this.startScreen = startScreen;

        //Create the client and start running it in another thread
        client = new Client();

        Packets.register(client);

        //Add the event listener to the client thread
        client.addListener(new ClientListener(this));
    }

    /**
     * Attempts to connect to a server with the given name and IP address
     * @param inputIP The user input IP
     */
    public void attemptConnection(final String inputIP)
    {
        new Thread("Connect")
        {
            public void run()
            {
                //Attempt to connect to the server
                try
                {
                    client.start();
                    client.connect(10000, inputIP, Packets.port);
                }
                catch (IOException ex)
                {
                    updateCurrentScreen();
                    displayErrorMessage("Could not find server...");
                }
            }
        }.start();
    }

    /**
     * Used to disconnect from the server manually
     * @param reason The reason for disconnecting
     */
    public void abortConnection(String reason)
    {
        if(client.isConnected())
        {
            client.stop();
        }

        displayErrorMessage(reason);
        updateCurrentScreen();
    }

    public void requestGameStart()
    {
        if(serverConnection != null)
        {
            GameStartRequest request = new GameStartRequest();
            serverConnection.sendTCP(request);
            System.out.println("Send request to start game!");
        }
        else
        {
            Log.error("Tried to contact server when there was no server connection!");
        }
    }

    /**
     * Used to move to the game screen when the server has found a match for a player
     * @param setupInfo Information about the match from the server
     */
    public void startGame(GameSetup setupInfo)
    {
        ((MultiplayerScreen)currentScreen).getGame().setGameInfo(setupInfo);
    }

    public void finishGame() {
        serverConnection.sendTCP(new GameEndDisconnect());
    }

    public void makeMove(Figure.AllowedMove move, Figure figure)
    {
        if(!(currentScreen instanceof ChessGame)) return;

        //Prepare the packet to send to the server
        Move m = new Move();

        m.gameID = ((ChessGame)currentScreen).gameID;
        m.destX = move.dest.X;
        m.destY = move.dest.Y;
        m.dist = move.dist;
        m.moveName = move.moveName;
        m.figureX = figure.coord.X;
        m.figureY = figure.coord.Y;
        m.eats = move.eats;
        //m.checkmate = checkmate;

        //Send the packet to the server
        serverConnection.sendTCP(m);
    }

    public void setCurrentScreen(NetScreen screen)
    {
        currentScreen = screen;
    }

    public void setServerConnection(Connection con)
    {
        serverConnection = con;
        updateCurrentScreen();
    }

    public void updateCurrentScreen()
    {
        Boolean connected = client.isConnected();

        currentScreen.updateConnectionInfo(connected);
    }

    public Screen getCurrentScreen()
    {
        return currentScreen;
    }

    public StartScreen getStartScreen() {
        return startScreen;
    }

    public void displayErrorMessage(String message)
    {
        currentScreen.displayErrorMessage(message);
    }
}
