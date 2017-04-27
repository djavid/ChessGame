package com.djavid.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.djavid.game.GameClient;
import com.djavid.game.StartScreen;


public class Actors extends Table implements Disposable {
    private final BaseGameScreen multiplayerScreen;

    private final Skin skin;
    private final TextureAtlas uiAtlas;
    private final BitmapFont smallFont;
    private final BitmapFont bigFont;
    private final BitmapFont inputFont;

    private final TextButton.TextButtonStyle smallButtonStyle;
    private final Label.LabelStyle smallLabelStyle;
    private final Label.LabelStyle bigLabelStyle;
    private final TextField.TextFieldStyle textFieldStyle;

    private final Label logoText;
    private final Label disconnectedText;
    private final Label connectionErrorText;

    private final Label welcomeText;
    private final Label matchmakingText;
    private final Label ipRequestText;

    private final TextButton connectButton;
    private final TextButton backButton;

    private final Table disconnectedWidgets;
    private final Table menuWidgets;
    private final Table matchmakingWidgets;

    private final TextField ipInput;

    private final StartScreen game;
    private final GameClient client;


    public Actors(BaseGameScreen menuScreen, final StartScreen game, final GameClient client)
    {
        //Assign the main menu screen to its variable
        multiplayerScreen = menuScreen;
        this.client = client;
        this.game = game;

        //Create a freetype generator and parameter for text generation
        FreeTypeFontGenerator mainFontGen = new FreeTypeFontGenerator(Gdx.files.internal("UI/gameFont.ttf"));
        FreeTypeFontGenerator inputFontGen = new FreeTypeFontGenerator(Gdx.files.internal("UI/inputFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        //Create bitmap fonts from the generators and parameter
        fontParameter.size = 24;
        bigFont = mainFontGen.generateFont(fontParameter);
        fontParameter.size = 16;
        smallFont = mainFontGen.generateFont(fontParameter);
        inputFont = inputFontGen.generateFont(fontParameter);
        mainFontGen.dispose();
        inputFontGen.dispose();

        //Create a skin from the UI Atlas file
        skin = new Skin();
        uiAtlas = new TextureAtlas(Gdx.files.internal("UI/uiElements.atlas"));
        skin.addRegions(uiAtlas);

        //Create small button style
        smallButtonStyle = new TextButton.TextButtonStyle();
        smallButtonStyle.font = smallFont;
        smallButtonStyle.up = skin.getDrawable("greenButtonUp");
        smallButtonStyle.down = skin.getDrawable("greenButtonDown");
        smallButtonStyle.over = skin.getDrawable("greenButtonOver");
        smallButtonStyle.disabled = skin.getDrawable("greenButtonDisabled");

        //Create Connect button
        connectButton = new TextButton("Connect", smallButtonStyle);
        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                disableButton(connectButton);
                //multiplayerScreen.connectButtonPressed(ipInput.getText());
                client.attemptConnection(ipInput.getText());
                matchmakingWidgets.setVisible(true);
                menuWidgets.setVisible(true);
                disconnectedWidgets.setVisible(false);
            }
        });

        //Create Quit button
        backButton = new TextButton("<-", smallButtonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(mainScreen);
                game.setScreen(new MainMenuScreen(game, client));
                client.abortConnection("");
            }
        });

        //Create label styles
        smallLabelStyle = new Label.LabelStyle();
        smallLabelStyle.font = smallFont;
        bigLabelStyle = new Label.LabelStyle();
        bigLabelStyle.font = bigFont;

        //Create Labels
        logoText = new Label("Chess multiplayer", bigLabelStyle);
        disconnectedText = new Label("Not connected", smallLabelStyle);
        ipRequestText = new Label("Server IP: ", smallLabelStyle);
        welcomeText = new Label("Welcome!", smallLabelStyle);
        matchmakingText = new Label("Searching for an opponent... ", bigLabelStyle);
        connectionErrorText = new Label("", smallLabelStyle);
        connectionErrorText.setColor(Color.RED);

        //Create input field styles
        textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = inputFont;
        textFieldStyle.cursor = skin.getDrawable("inputCursor");
        textFieldStyle.background = skin.getDrawable("textFieldBackground");
        textFieldStyle.background.setLeftWidth(7);
        textFieldStyle.background.setRightWidth(7);
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.selection = skin.getDrawable("inputSelection");

        //Create input fields
        ipInput = new TextField("localhost", textFieldStyle);
        ipInput.setMaxLength(100);

        //Create the menu widgets
        menuWidgets = new Table();
        menuWidgets.setFillParent(true);

        //Add the actors to the menu widgets
        menuWidgets.left();
        menuWidgets.top();
        menuWidgets.add(logoText).padBottom(25);
        menuWidgets.row();
        menuWidgets.add(backButton).width(50).height(50).padRight(150);
        //menuWidgets.add(backButton);

        //Create the disconnected widgets
        disconnectedWidgets = new Table();
        disconnectedWidgets.setFillParent(true);

        //Add actors to the disconnected widget
        disconnectedWidgets.center();
        disconnectedWidgets.center();
        disconnectedWidgets.padLeft(60);
        disconnectedWidgets.add(disconnectedText).left().colspan(2);
        disconnectedWidgets.row();
        disconnectedWidgets.add(ipRequestText).left();
        disconnectedWidgets.add(ipInput);
        disconnectedWidgets.row();
        disconnectedWidgets.add(connectButton).padTop(20).width(200).height(50).center();
        disconnectedWidgets.row();
        //disconnectedWidgets.add(backButton).padTop(10).width(200).height(50).center();
        disconnectedWidgets.row();
        disconnectedWidgets.add(connectionErrorText).left().colspan(2);

        //Create the matchmaking widgets
        matchmakingWidgets = new Table();
        matchmakingWidgets.setFillParent(true);
        matchmakingWidgets.setVisible(false);

        //Add the actors to the connected widget
        matchmakingWidgets.add(matchmakingText);
    }

    private void updateWelcomeText(String text)
    {
        welcomeText.setText(text);
    }

    public void enableButton(TextButton button)
    {
        button.setTouchable(Touchable.enabled);
        button.setDisabled(false);
    }

    public void disableButton(TextButton button)
    {
        button.setTouchable(Touchable.disabled);
        button.setDisabled(true);
    }

    /**
     * Updates any actors with relevant connection information
     * @param isConnected If the client is still connected to the server
     */
    public void updateConnectionInfo(boolean isConnected)
    {
        if(isConnected)
        {
            updateWelcomeText("Connected!");
            disconnectedWidgets.setVisible(false);
        }
        else
        {
            enableButton(connectButton);
            disconnectedWidgets.setVisible(true);
            matchmakingWidgets.setVisible(false);
            menuWidgets.setVisible(true);
        }
    }

    public void displayErrorMessage(String message)
    {
        connectionErrorText.setText(message);
    }

    public Table getDisconnectedWidgets()
    {
        return disconnectedWidgets;
    }

    public Table getMenuWidgets() { return menuWidgets; }

    public Table getMatchmakingWidgets() { return matchmakingWidgets; }

    @Override
    public void dispose()
    {
        uiAtlas.dispose();
    }
}
