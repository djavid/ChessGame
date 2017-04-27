package com.djavid.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.djavid.game.ChessLogic.Chess;
import com.djavid.game.ChessLogic.Coord;
import com.djavid.game.ChessLogic.Figure;
import com.djavid.game.Screens.Actors;
import com.djavid.game.Screens.BaseGameScreen;
import com.djavid.game.Screens.NetScreen;
import com.djavid.game.Network.Packets.*;


public class ChessGame implements InputProcessor, Screen, NetScreen {
    public interface Shape {
        public abstract boolean isVisible(Matrix4 transform, Camera cam);

        /* return -1 on no intersection, or when there is an intersection: the squared distance between the center of this
           object and the point on the ray closest to this object when there is intersection. */
        public abstract float intersects(Matrix4 transform, Ray ray);
    }

    public static class GameObject extends ModelInstance {
        public Shape shape;
        public String id;
        public boolean blocked;

        public GameObject(Model model, String rootNode, boolean mergeTransform) {
            super(model, rootNode, mergeTransform);
        }

        public GameObject(Model model) {
            super(model);
        }

        public boolean isVisible(Camera cam) {
            return shape == null ? false : shape.isVisible(transform, cam);
        }

        public float intersects(Ray ray) {
            if (blocked) {
                return -1f;
            } else {
                return shape == null ? -1f : shape.intersects(transform, ray);
            }
        }
    }

    public static class Box implements Shape { //TODO: better shape for chess figures
        protected final static Vector3 position = new Vector3();
        public final Vector3 center = new Vector3();
        public final Vector3 dimensions = new Vector3();

        public Box(BoundingBox bounds) {
            bounds.getCenter(center);
            bounds.getDimensions(dimensions);
        }

        public boolean isVisible(Matrix4 transform, Camera cam) {
            return cam.frustum.boundsInFrustum(transform.getTranslation(position).add(center), dimensions);
        }

        public float intersects(Matrix4 transform, Ray ray) {
            transform.getTranslation(position).add(center);
            if (Intersector.intersectRayBoundsFast(ray, position, dimensions)) {
                final float len = ray.direction.dot(position.x - ray.origin.x, position.y -
                        ray.origin.y, position.z - ray.origin.z);
                return position.dst2(ray.origin.x + ray.direction.x * len, ray.origin.y +
                        ray.direction.y * len, ray.origin.z + ray.direction.z * len);
            }
            return -1f;
        }
    }


    // 3D environment variables
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    private CameraInputController camController;
    public AssetManager assets;

    // models
    private Model model;
    private Array<GameObject> instances = new Array<GameObject>();
    private GameObject board;
    public ModelInstance space;
    protected Shape bishopShape, kingShape, knightShape, pawnShape, queenShape, rookShape;

    // etc
    private Stage stage;
    private Label label;
    public Label labelInfo;
    public BitmapFont font;
    private StringBuilder stringBuilder;
    private int selected = -1, selecting = -1;
    private Material selectionMaterial;
    private Material redMaterial;
    private Material originalMaterial;
    private BoundingBox bounds = new BoundingBox();
    private boolean loading;

    // game
    public Chess game;
    public boolean my_color;
    private boolean whichTurnColor;
    List<GameObject> circles;
    Figure selectedFigure;
    List<Figure.AllowedMove> moves;

    //server
    GameClient gameClient;
    final StartScreen startScreen;
    public int gameID;


    public ChessGame(final StartScreen g, GameClient gameClient, GameSetup gameSetup) {
        my_color = gameSetup.color;
        this.gameClient = gameClient;
        startScreen = g;

        stage = new Stage();
        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));

        labelInfo = new Label(" ", new Label.LabelStyle(font, Color.GOLD));
        labelInfo.setPosition(Gdx.graphics.getWidth() - labelInfo.getWidth() * 2.3f,
                Gdx.graphics.getHeight() - labelInfo.getHeight() * 2.3f);
        Label.LabelStyle style = new Label.LabelStyle();
        FreeTypeFontGenerator mainFontGen = new FreeTypeFontGenerator(Gdx.files.internal("UI/gameFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 50;
        font = mainFontGen.generateFont(fontParameter);
        style.font = font;

        labelInfo.setStyle(style);
        labelInfo.setVisible(true);
        labelInfo.setColor(Color.RED);

        stage.addActor(label);
        stage.addActor(labelInfo);
        stringBuilder = new StringBuilder();
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); //67
        camera.position.set(0f, 11f, 10f);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(new InputMultiplexer(this, camController));

        assets = new AssetManager();
        assets.load("chess_board.g3db", Model.class);
        assets.load("spacesphere.obj", Model.class);
        loading = true;

        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
        redMaterial = new Material();
        redMaterial.set(ColorAttribute.createDiffuse(Color.RED));
        originalMaterial = new Material();

        game = new Chess(my_color);

        if (gameClient == null) {
            game = new Chess(true);
            whichTurnColor = true;
            changeCam(whichTurnColor);
        } else {
            gameID = gameSetup.gameID;
        }
    }

    @Override
    public void render(float delta) {

        if (loading && assets.update()) {
            doneLoading();
        }
        camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        for (final GameObject instance : instances) {
            if (instance.isVisible(camera)) {
                if (!instance.id.startsWith("circle")) {
                    byte X = game.getFigureByName(instance.id).coord.X;
                    byte Y = game.getFigureByName(instance.id).coord.Y;

                    float x = (X - 3.5f) * 2;
                    float z = -((Y - 3.5f) * 2);

                    Vector3 pos = new Vector3();
                    instance.transform.getTranslation(pos);
                    instance.transform.setTranslation(x, pos.y, z);
                }
                modelBatch.render(instance, environment);
            }
        }
        if (board != null) {
            modelBatch.render(board);
        }
        if (space != null) {
            modelBatch.render(space);
        }
        modelBatch.end();

        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        label.setText(stringBuilder);
        stage.draw();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        selecting = getObject(screenX, screenY);

        if (selecting >= 0) {
            if (instances.get(selecting).id.startsWith("circle")) {
                setSelected(selecting);
            }
        }

        return selecting >= 0;
    }

    public void drawCircle(Move move) { //TODO
        ModelBuilder mb = new ModelBuilder();
        mb.begin();

        float x = (move.figureX - 3.5f) * 2;
        float y = (move.figureY - 3.5f) * 2;

        mb.node().id = "circle_check";

        MeshPartBuilder part = mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position |
                VertexAttributes.Usage.Normal, redMaterial);

        EllipseShapeBuilder.build(part, 1.5f, 1.5f, 20,
                x, board.transform.getScaleY() + 0.1f, -y,
                0, board.transform.getScaleY() + 0.1f, 0);

        Model m = mb.end();
        GameObject obj = new GameObject(m, "circle_check", true);
        if (circles == null) {
            circles = new ArrayList<GameObject>();
            circles.add(obj);
        }
        instances.add(obj);
        obj.id = "circle_check";
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if (selecting >= 0) {
            if (selecting == getObject(screenX, screenY)) {

                if (selectedFigure != null &&
                        instances.get(selecting).id.equals(selectedFigure.Name)) {
                    selectedFigure = null;
                    removeCircles();
                } else {
                    String id = instances.get(selecting).id;

                    if (!id.startsWith("circle")) {
                        if (!instances.get(selecting).blocked) {
                            if (circles != null) removeCircles();

                            ModelBuilder mb = new ModelBuilder();
                            mb.begin();

                            selectedFigure = game.getFigureByName(id);

                            int num = 0;
                            moves = game.getFigureByName(id).getAllowedMoves(game, true);
                            for (Figure.AllowedMove move : moves) {
                                float x = (move.dest.X - 3.5f) * 2;
                                float y = (move.dest.Y - 3.5f) * 2;

                                mb.node().id = "circle_" + num;
                                num++;
                                MeshPartBuilder part = mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position |
                                        VertexAttributes.Usage.Normal, selectionMaterial);

                                EllipseShapeBuilder.build(part, 1.5f, 1.5f, 20,
                                        x, board.transform.getScaleY() + 0.1f, -y,
                                        0, board.transform.getScaleY() + 0.1f, 0);
                            }
                            Model m = mb.end();

                            circles = new ArrayList<GameObject>();
                            for (int i = 0; i < num; i++) {
                                GameObject obj = new GameObject(m, "circle_" + i, true);
                                circles.add(obj);
                                instances.add(obj);

                                obj.calculateBoundingBox(bounds);
                                obj.shape = new Box(bounds);
                                obj.id = "circle_" + i;
                            }
                        }
                    } else {
                        removeCircles();
                        int b = Byte.parseByte(id.split("_")[1]);

                        //sending a move to the server
                        Figure.AllowedMove move = moves.get(b);
                        if (gameClient != null) {
                            gameClient.makeMove(move, selectedFigure);
                            setBlocked(my_color);

                        } else {
                            whichTurnColor = !whichTurnColor;
                            changeCam(whichTurnColor);
                            setBlocked(!whichTurnColor);
                            unsetBlocked(whichTurnColor);
                        }

                        //making a move on board
                        selectedFigure.move(move, game, true);
                        System.out.println(move);
                        if (move.eats) cleanEatedFigures();
                        selectedFigure = null;

                        if (game.Checkmate(my_color)) {
                            stringBuilder.setLength(0);
                            stringBuilder.append("You lost!\nESC to exit");
                            labelInfo.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
                            labelInfo.setText(stringBuilder);
                            labelInfo.setVisible(true);
                        } else if (game.Checkmate(!my_color)) {
                            gameClient.finishGame();
                            stringBuilder.setLength(0);
                            stringBuilder.append("You won!\nESC to exit");
                            labelInfo.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
                            labelInfo.setText(stringBuilder);
                            labelInfo.setVisible(true);
                        } else {
                            labelInfo.setVisible(false);
                        }
                    }

                }
            }

            selecting = -1;
            return true;
        }
        return false;
    }

    private void doneLoading() {
        model = assets.get("chess_board.g3db", Model.class);

        for (int i = 0; i < model.nodes.size; i++) {
            String id = model.nodes.get(i).id;
            GameObject instance = new GameObject(model, id, true);
            instance.id = id;

            if (!id.equals("chessboard")) {
                instances.add(instance);
            } else {
                board = new GameObject(model, id, true);
            }

            if (id.startsWith("bishop")) {
                if (bishopShape == null) {
                    instance.calculateBoundingBox(bounds);
                    bishopShape = new Box(bounds);
                }
                instance.shape = bishopShape;
            } else if (id.startsWith("king")) {
                if (kingShape == null) {
                    instance.calculateBoundingBox(bounds);
                    kingShape = new Box(bounds);
                }
                instance.shape = kingShape;
            } else if (id.startsWith("knight")) {
                if (knightShape == null) {
                    instance.calculateBoundingBox(bounds);
                    knightShape = new Box(bounds);
                }
                instance.shape = knightShape;

                if (my_color) { //instance.transform.getScaleX() / 2
                    if (id.split("_")[1].equals("b")) {
                        instance.transform.rotate(0, 0, 10, 180);
                    } else {
                        instance.transform.rotate(0, 0, 10, -180);
                    }
                }
            } else if (id.startsWith("pawn")) {
                if (pawnShape == null) {
                    instance.calculateBoundingBox(bounds);
                    pawnShape = new Box(bounds);
                }
                instance.shape = pawnShape;
            } else if (id.startsWith("queen")) {
                if (queenShape == null) {
                    instance.calculateBoundingBox(bounds);
                    queenShape = new Box(bounds);
                }
                instance.shape = queenShape;
            } else if (id.startsWith("rook")) {
                if (rookShape == null) {
                    instance.calculateBoundingBox(bounds);
                    rookShape = new Box(bounds);
                }
                instance.shape = rookShape;
            }

        }

        if (gameClient == null) {
            setBlocked(!whichTurnColor);
        } else {
            System.out.println("I am here!");
            setBlocked(!my_color); //sets blocked opponent
            if (!my_color) setBlocked(my_color);
        }

        space = new ModelInstance(assets.get("spacesphere.obj", Model.class));
        loading = false;
    }

    public void cleanEatedFigures() {
        for (int i = instances.size - 1; i >= 0; i--) {
            if (!instances.get(i).id.startsWith("circle")) {
                if (game.getFigureByName(instances.get(i).id).isEated()) {
                    instances.removeIndex(i);
                }
            }
        }
    }

    private void changeCam(boolean color) {
        System.out.println(color);
        float y = 11f;
        float z = 10f;

        if (color) {
            camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); //67
            camera.position.set(0f, y, z); //if white - look at white 0 11 10 best 0 10 12
            camera.lookAt(0, 0, 0);
            camera.near = 0.1f;
            camera.far = 300f;
            camera.update();
        } else {
            camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); //67
            camera.position.set(0f, y, -z); //else - look at black
            camera.lookAt(0, 0, 0);
            camera.near = 0.1f;
            camera.far = 300f;
            camera.update();
        }
    }

    private void removeCircles() {
        for (int i = instances.size - 1; i >= 0; i--) {
            if (instances.get(i).id.startsWith("circle")) {
                instances.removeIndex(i);
            }
        }
    }

    private void setSelected(int value) {
        selected = value;
        if (selected >= 0) {
            Material mat = instances.get(selected).materials.get(0);
            originalMaterial.clear();
            originalMaterial.set(mat);
            mat.clear();
            Material red_mat = new Material();
            red_mat.set(selectionMaterial);
            mat.set(red_mat);
        }
    }

    private void unsetSelected() {
        Material mat = instances.get(selected).materials.get(0);
        mat.clear();
        mat.set(originalMaterial);
        selected = 0;
    }

    private int getObject(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;
        float distance = -1;

        for (int i = 0; i < instances.size; ++i) {
            final float dist2 = instances.get(i).intersects(ray);

            if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }

    private void setBlocked(String id) {
        for (GameObject instance : instances) {
            if (instance.id.equals(id)) instance.blocked = true;
        }
    }

    private void setBlocked(boolean colorToBlock) {
        for (GameObject instance : instances) {

            if ((instance.id.contains("_w_") || instance.id.contains("_w")) && colorToBlock)
                instance.blocked = true;
            if ((instance.id.contains("_b_") || instance.id.contains("_b")) && !colorToBlock)
                instance.blocked = true;
        }
    }

    public void unsetBlocked(boolean colorToBlock) {
        for (int i = 0; i < instances.size; i++) {

            if ((instances.get(i).id.contains("_w_") || instances.get(i).id.contains("_w")) && colorToBlock)
                instances.get(i).blocked = false;
            if ((instances.get(i).id.contains("_b_") || instances.get(i).id.contains("_b")) && !colorToBlock)
                instances.get(i).blocked = false;
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (selecting < 0)
            return false;
        if (selected == selecting) { // TODO: selected figure
            //instances.get(selected).transform.getTranslation(position);
//            Ray ray = camera.getPickRay(screenX, screenY);
//            final float distance = (position.y - ray.origin.y) / ray.direction.y;
//            position.set(ray.direction).scl(distance).add(ray.origin);
//
//            instances.get(selected).transform.setTranslation(position);
        }
        return true;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        assets.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                if (gameClient != null) {
                    gameClient.abortConnection("");
                }

                startScreen.exitToMenu();

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
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void updateConnectionInfo(boolean connected) {
        if (!connected) {
            startScreen.exitToMenu();
        }
    }

    @Override
    public void displayErrorMessage(String message) {

    }
}

