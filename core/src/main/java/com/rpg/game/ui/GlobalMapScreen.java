package com.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rpg.game.RPGGame;
import com.rpg.game.hex.Hex;
import com.rpg.game.map.GlobalMap;

import java.util.List;

/**
 * Экран глобальной гексагональной карты
 * Реализует перемещение в стиле roguelike с учётом скорости персонажа
 */
public class GlobalMapScreen extends BaseScreen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage stage;
    private BitmapFont font;
    
    private GlobalMap map;
    private float hexSize = 40f;
    private Hex selectedHex;
    private List<Hex> pathToTarget;
    
    // UI элементы
    private Label positionLabel;
    private Label movementLabel;
    private TextButton characterButton;
    private TextButton craftButton;
    private TextButton endTurnButton;
    
    public GlobalMapScreen(RPGGame game) {
        super(game);
        this.map = game.getGlobalMap();
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.stage = new Stage(new ScreenViewport());
        this.font = new BitmapFont();
        
        initializeCamera();
        initializeUI();
    }
    
    private void initializeCamera() {
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0);
        camera.update();
    }
    
    private void initializeUI() {
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().left();
        stage.addActor(uiTable);
        
        // Информация о позиции
        positionLabel = new Label("Position: 0, 0", new Label.LabelStyle(font, null));
        uiTable.add(positionLabel).row();
        
        // Очки перемещения
        movementLabel = new Label("Movement: 5/5", new Label.LabelStyle(font, null));
        uiTable.add(movementLabel).row();
        
        // Кнопки в правом верхнем углу
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.top().right();
        stage.addActor(buttonTable);
        
        // Кнопка персонажа
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        characterButton = new TextButton("Персонаж", buttonStyle);
        characterButton.addListener(click -> {
            game.openCharacterScreen();
            return true;
        });
        buttonTable.add(characterButton).pad(5).row();
        
        // Кнопка крафта
        craftButton = new TextButton("Крафт", buttonStyle);
        craftButton.addListener(click -> {
            game.openCraftingScreen();
            return true;
        });
        buttonTable.add(craftButton).pad(5).row();
        
        // Кнопка завершения хода
        endTurnButton = new TextButton("Завершить ход", buttonStyle);
        endTurnButton.addListener(click -> {
            endTurn();
            return true;
        });
        buttonTable.add(endTurnButton).pad(5);
    }
    
    /**
     * Обработка клика по гексу
     */
    private boolean handleTap(float screenX, float screenY) {
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        Hex clickedHex = screenToHex(worldCoords.x, worldCoords.y);
        
        if (clickedHex != null) {
            selectedHex = clickedHex;
            
            // Попытка найти путь к цели
            Hex currentHex = game.getPlayer().getCurrentHex();
            if (currentHex != null) {
                pathToTarget = map.findPath(currentHex, clickedHex, 
                                           game.getPlayer().getCurrentMovementPoints());
                
                // Если путь найден и это соседний гекс - перемещаемся
                if (!pathToTarget.isEmpty() && pathToTarget.size() <= 2) {
                    movePlayer(clickedHex);
                }
            }
            
            updateUI();
            return true;
        }
        return false;
    }
    
    /**
     * Перемещение игрока к целевому гексу
     */
    private void movePlayer(Hex targetHex) {
        if (game.getPlayer().tryMoveTo(targetHex)) {
            // Успешное перемещение
            centerCameraOnPlayer();
            
            // Проверка на вход в локацию (город, подземелье и т.д.)
            checkLocationEntry(targetHex);
        }
    }
    
    /**
     * Проверка входа в локацию для перехода на локальную карту
     */
    private void checkLocationEntry(Hex hex) {
        // TODO: Проверить тип местности и наличие локации
        // Если это город или подземелье - предложить переход
    }
    
    /**
     * Завершение хода - восстановление очков перемещения
     */
    private void endTurn() {
        game.getPlayer().restoreMovementPoints();
        pathToTarget = null;
        updateUI();
    }
    
    /**
     * Конвертация экранных координат в гексагональные
     */
    private Hex screenToHex(float x, float y) {
        // Упрощённая конвертация - в полной версии нужна точная формула
        int q = (int)(x / (hexSize * 1.5f));
        int r = (int)((y - hexSize * Math.sqrt(3)/2 * q) / (hexSize * Math.sqrt(3)));
        
        return map.getHex(q, r);
    }
    
    /**
     * Центрирование камеры на игроке
     */
    private void centerCameraOnPlayer() {
        Hex playerHex = game.getPlayer().getCurrentHex();
        if (playerHex != null) {
            Vector2 pixelPos = playerHex.toPixelCoordinates(hexSize);
            camera.position.set(pixelPos.x, pixelPos.y, camera.position.z);
            camera.update();
        }
    }
    
    /**
     * Отрисовка гексагональной карты
     */
    private void drawMap() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Отрисовка всех гексов
        for (Hex hex : map.getAllHexes()) {
            drawHex(hex);
        }
        
        // Отрисовка пути
        if (pathToTarget != null) {
            drawPath(pathToTarget);
        }
        
        // Отрисовка игрока
        drawPlayer();
        
        batch.end();
    }
    
    private void drawHex(Hex hex) {
        // TODO: Реализовать отрисовку гекса с учётом типа местности
    }
    
    private void drawPath(List<Hex> path) {
        // TODO: Реализовать отрисовку пути
    }
    
    private void drawPlayer() {
        // TODO: Реализовать отрисовку спрайта игрока
    }
    
    private void updateUI() {
        Hex currentHex = game.getPlayer().getCurrentHex();
        if (currentHex != null) {
            positionLabel.setText(String.format("Position: %d, %d", 
                                               currentHex.getQ(), currentHex.getR()));
        }
        
        movementLabel.setText(String.format("Movement: %d/%d",
                                           game.getPlayer().getCurrentMovementPoints(),
                                           game.getPlayer().getMovementPoints()));
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.2f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        drawMap();
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        centerCameraOnPlayer();
        updateUI();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        font.dispose();
    }
}
