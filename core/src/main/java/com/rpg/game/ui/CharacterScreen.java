package com.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rpg.game.RPGGame;
import com.rpg.game.character.Character;
import com.rpg.game.item.Item;

import java.util.Map;

/**
 * Экран персонажа
 * Отображает характеристики, инвентарь и экипировку
 */
public class CharacterScreen extends BaseScreen {
    private Stage stage;
    private BitmapFont font;
    private ScrollPane scrollPane;
    
    // UI элементы
    private Label nameLabel;
    private Label levelLabel;
    private Label expLabel;
    private Label healthLabel;
    private Label manaLabel;
    
    private Label strengthLabel;
    private Label agilityLabel;
    private Label intelligenceLabel;
    private Label vitalityLabel;
    
    private Table equipmentTable;
    private Table inventoryTable;
    
    public CharacterScreen(RPGGame game) {
        super(game);
        this.stage = new Stage(new ScreenViewport());
        this.font = new BitmapFont();
        
        initializeUI();
        updateCharacterInfo();
    }
    
    private void initializeUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(10);
        stage.addActor(mainTable);
        
        // Заголовок
        Label titleLabel = new Label("Персонаж", new Label.LabelStyle(font, null));
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();
        
        // Левая колонка - информация о персонаже
        Table infoTable = createInfoTable();
        mainTable.add(infoTable).width(300).padRight(10).top();
        
        // Правая колонка - экипировка и инвентарь
        Table rightTable = new Table();
        
        // Экипировка
        Label equipTitle = new Label("Экипировка", new Label.LabelStyle(font, null));
        rightTable.add(equipTitle).padBottom(10).row();
        
        equipmentTable = new Table();
        equipmentTable.defaults().pad(5);
        rightTable.add(equipmentTable).width(250).padBottom(20).row();
        
        // Инвентарь
        Label invTitle = new Label("Инвентарь", new Label.LabelStyle(font, null));
        rightTable.add(invTitle).padBottom(10).row();
        
        inventoryTable = new Table();
        inventoryTable.defaults().pad(3);
        ScrollPane inventoryScroll = new ScrollPane(inventoryTable);
        inventoryScroll.setOverscroll(false, false);
        rightTable.add(inventoryScroll).width(250).height(300);
        
        mainTable.add(rightTable).top();
        
        // Кнопка закрытия внизу
        mainTable.row().colspan(2).padTop(20);
        TextButton closeButton = new TextButton("Закрыть", new TextButton.TextButtonStyle());
        closeButton.addListener(click -> {
            game.returnToGlobalMap();
            return true;
        });
        mainTable.add(closeButton).width(200).height(40);
    }
    
    private Table createInfoTable() {
        Table table = new Table();
        table.defaults().left().pad(5);
        
        // Имя и уровень
        table.add(new Label("Имя:", new Label.LabelStyle(font, null))).row();
        nameLabel = new Label("", new Label.LabelStyle(font, null));
        table.add(nameLabel).row();
        
        table.add(new Label("Уровень:", new Label.LabelStyle(font, null))).row();
        levelLabel = new Label("", new Label.LabelStyle(font, null));
        table.add(levelLabel).row();
        
        table.add(new Label("Опыт:", new Label.LabelStyle(font, null))).row();
        expLabel = new Label("", new Label.LabelStyle(font, null));
        table.add(expLabel).row();
        
        table.add().pad(10).row();
        
        // Здоровье и мана
        table.add(new Label("Здоровье:", new Label.LabelStyle(font, null))).row();
        healthLabel = new Label("", new Label.LabelStyle(font, null));
        table.add(healthLabel).row();
        
        table.add(new Label("Мана:", new Label.LabelStyle(font, null))).row();
        manaLabel = new Label("", new Label.LabelStyle(font, null));
        table.add(manaLabel).row();
        
        table.add().pad(10).row();
        
        // Характеристики
        table.add(new Label("Характеристики:", new Label.LabelStyle(font, null))).row();
        
        table.add(new Label("Сила:", new Label.LabelStyle(font, null)));
        strengthLabel = new Label("", new Label.LabelStyle(font, null));
        table.add(strengthLabel).row();
        
        table.add(new Label("Ловкость:", new Label.LabelStyle(font, null)));
        agilityLabel = new Label("", new Label.LabelStyle(font, null));
        table.add(agilityLabel).row();
        
        table.add(new Label("Интеллект:", new Label.LabelStyle(font, null)));
        intelligenceLabel = new Label("", new Label.LabelStyle(font, null));
        table.add(intelligenceLabel).row();
        
        table.add(new Label("Жизнестойкость:", new Label.LabelStyle(font, null)));
        vitalityLabel = new Label("", new Label.LabelStyle(font, null));
        table.add(vitalityLabel).row();
        
        return table;
    }
    
    /**
     * Обновление информации о персонаже
     */
    public void updateCharacterInfo() {
        Character player = game.getPlayer();
        
        // Основная информация
        nameLabel.setText(player.getName());
        levelLabel.setText(String.valueOf(player.getLevel()));
        expLabel.setText(player.getExperience() + " / " + (player.getLevel() * 100));
        healthLabel.setText(player.getCurrentHealth() + " / " + player.getMaxHealth());
        manaLabel.setText(player.getCurrentMana() + " / " + player.getMaxMana());
        
        // Характеристики
        strengthLabel.setText(String.valueOf(player.getStrength()));
        agilityLabel.setText(String.valueOf(player.getAgility()));
        intelligenceLabel.setText(String.valueOf(player.getIntelligence()));
        vitalityLabel.setText(String.valueOf(player.getVitality()));
        
        // Экипировка
        updateEquipmentTable();
        
        // Инвентарь
        updateInventoryTable();
    }
    
    private void updateEquipmentTable() {
        equipmentTable.clear();
        
        Map<com.rpg.game.character.EquipmentSlot, Item> equipment = game.getPlayer().getEquipment();
        
        for (com.rpg.game.character.EquipmentSlot slot : com.rpg.game.character.EquipmentSlot.values()) {
            Item item = equipment.get(slot);
            String itemName = item != null ? item.getName() : "[Пусто]";
            
            equipmentTable.add(new Label(slot.name() + ":", new Label.LabelStyle(font, null))).left();
            equipmentTable.add(new Label(itemName, new Label.LabelStyle(font, null))).left().row();
        }
    }
    
    private void updateInventoryTable() {
        inventoryTable.clear();
        
        for (Item item : game.getPlayer().getInventory()) {
            TextButton itemButton = new TextButton(item.getName(), new TextButton.TextButtonStyle());
            itemButton.addListener(click -> {
                // TODO: Показать детали предмета или использовать его
                return true;
            });
            inventoryTable.add(itemButton).left().row();
        }
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        updateCharacterInfo();
    }
    
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
