package com.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import com.rpg.game.hex.TerrainType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    // Текстуры для типов местности
    private Map<TerrainType, Texture> terrainTextures;
    
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
        
        initializeTerrainTextures();
        initializeCamera();
        initializeUI();
    }
    
    /**
     * Создание текстур для разных типов местности программно
     */
    private void initializeTerrainTextures() {
        terrainTextures = new HashMap<>();
        
        // GRASS - зеленый
        terrainTextures.put(TerrainType.GRASS, createHexTexture(new Color(0.2f, 0.6f, 0.2f, 1.0f)));
        // FOREST - темно-зеленый
        terrainTextures.put(TerrainType.FOREST, createHexTexture(new Color(0.1f, 0.4f, 0.1f, 1.0f)));
        // MOUNTAIN - серый
        terrainTextures.put(TerrainType.MOUNTAIN, createHexTexture(new Color(0.5f, 0.5f, 0.5f, 1.0f)));
        // WATER - синий
        terrainTextures.put(TerrainType.WATER, createHexTexture(new Color(0.2f, 0.4f, 0.8f, 1.0f)));
        // DESERT - желтый
        terrainTextures.put(TerrainType.DESERT, createHexTexture(new Color(0.9f, 0.8f, 0.3f, 1.0f)));
        // SWAMP - болотный
        terrainTextures.put(TerrainType.SWAMP, createHexTexture(new Color(0.4f, 0.5f, 0.3f, 1.0f)));
        // ROAD - светло-серый
        terrainTextures.put(TerrainType.ROAD, createHexTexture(new Color(0.7f, 0.7f, 0.6f, 1.0f)));
        // CITY - коричневый
        terrainTextures.put(TerrainType.CITY, createHexTexture(new Color(0.6f, 0.4f, 0.2f, 1.0f)));
    }
    
    /**
     * Создает текстуру гекса заданного цвета
     */
    private Texture createHexTexture(Color color) {
        int size = (int)(hexSize * 2);
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Рисуем гексагон
        pixmap.setColor(color);
        float center = size / 2f;
        float radius = hexSize * 0.9f;
        
        // Заполняем фон прозрачным
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        
        // Рисуем гексагон (упрощенно - круг для начала)
        pixmap.setColor(color);
        pixmap.fillCircle((int)center, (int)center, (int)(radius * 0.8f));
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
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
        Vector2 pixelPos = hex.toPixelCoordinates(hexSize);
        Texture texture = terrainTextures.get(hex.getTerrain());
        
        if (texture != null) {
            float originX = pixelPos.x - hexSize;
            float originY = pixelPos.y - hexSize;
            batch.draw(texture, originX, originY, hexSize * 2, hexSize * 2);
        }
    }
    
    private void drawPath(List<Hex> path) {
        // Отрисовка пути линией другого цвета
        for (Hex hex : path) {
            Vector2 pixelPos = hex.toPixelCoordinates(hexSize);
            float originX = pixelPos.x - hexSize * 0.5f;
            float originY = pixelPos.y - hexSize * 0.5f;
            
            // Рисуем маленький круг в центре гекса для обозначения пути
            batch.setColor(1.0f, 1.0f, 0.0f, 0.8f); // Желтый цвет
            batch.draw(terrainTextures.get(TerrainType.ROAD), originX, originY, hexSize, hexSize);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f); // Сброс цвета
        }
    }
    
    private void drawPlayer() {
        Hex playerHex = game.getPlayer().getCurrentHex();
        if (playerHex != null) {
            Vector2 pixelPos = playerHex.toPixelCoordinates(hexSize);
            float originX = pixelPos.x - hexSize * 0.3f;
            float originY = pixelPos.y - hexSize * 0.3f;
            
            // Рисуем красный круг для игрока
            batch.setColor(1.0f, 0.0f, 0.0f, 1.0f);
            batch.draw(terrainTextures.get(TerrainType.CITY), originX, originY, hexSize * 0.6f, hexSize * 0.6f);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f); // Сброс цвета
        }
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
        // Устанавливаем обработчик ввода: сначала Stage для кнопок, потом наш обработчик для клавиатуры
        InputMultiplexer multiplexer = new InputMultiplexer(stage, new KeyboardInputHandler());
        Gdx.input.setInputProcessor(multiplexer);
        centerCameraOnPlayer();
        updateUI();
    }
    
    /**
     * Обработчик нажатий клавиш для перемещения персонажа
     */
    private class KeyboardInputHandler extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            Hex currentHex = game.getPlayer().getCurrentHex();
            if (currentHex == null) return false;
            
            Hex targetHex = null;
            int q = currentHex.getQ();
            int r = currentHex.getR();
            
            // Управление стрелками или WASD
            switch (keycode) {
                case Input.Keys.UP:
                case Input.Keys.W:
                    targetHex = map.getHex(q, r - 1);
                    break;
                case Input.Keys.DOWN:
                case Input.Keys.S:
                    targetHex = map.getHex(q, r + 1);
                    break;
                case Input.Keys.LEFT:
                case Input.Keys.A:
                    targetHex = map.getHex(q - 1, r);
                    break;
                case Input.Keys.RIGHT:
                case Input.Keys.D:
                    targetHex = map.getHex(q + 1, r);
                    break;
                case Input.Keys.NUMPAD_7:
                    targetHex = map.getHex(q - 1, r - 1);
                    break;
                case Input.Keys.NUMPAD_9:
                    targetHex = map.getHex(q + 1, r - 1);
                    break;
                case Input.Keys.NUMPAD_1:
                    targetHex = map.getHex(q - 1, r + 1);
                    break;
                case Input.Keys.NUMPAD_3:
                    targetHex = map.getHex(q + 1, r + 1);
                    break;
                case Input.Keys.SPACE:
                    endTurn();
                    return true;
            }
            
            if (targetHex != null) {
                movePlayer(targetHex);
                return true;
            }
            
            return false;
        }
        
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            // Обработка кликов мыши/тача
            if (button == Input.Buttons.LEFT) {
                return handleTap(screenX, screenY);
            }
            return false;
        }
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
        // Освобождаем текстуры местности
        for (Texture texture : terrainTextures.values()) {
            texture.dispose();
        }
    }
}
