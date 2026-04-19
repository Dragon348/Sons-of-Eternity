package com.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rpg.game.RPGGame;
import com.rpg.game.battle.BattleSystem;
import com.rpg.game.battle.CombatAction;
import com.rpg.game.battle.Combatant;

import java.util.List;

/**
 * Экран пошагового боя
 * Реализует тактический бой на локальной карте
 */
public class BattleScreen extends BaseScreen {
    private Stage stage;
    private BitmapFont font;
    
    private Label turnLabel;
    private Label statusLabel;
    private Table actionsTable;
    
    public BattleScreen(RPGGame game) {
        super(game);
        this.stage = new Stage(new ScreenViewport());
        this.font = new BitmapFont();
        
        initializeUI();
    }
    
    private void initializeUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(10);
        stage.addActor(mainTable);
        
        // Заголовок
        Label titleLabel = new Label("БОЙ", new Label.LabelStyle(font, null));
        mainTable.add(titleLabel).padBottom(20).row();
        
        // Информация о ходе
        turnLabel = new Label("Ход: Игрок", new Label.LabelStyle(font, null));
        mainTable.add(turnLabel).row();
        
        // Статус боя
        statusLabel = new Label("", new Label.LabelStyle(font, null));
        mainTable.add(statusLabel).row();
        
        mainTable.add().pad(20).row();
        
        // Таблица действий
        actionsTable = new Table();
        actionsTable.defaults().pad(5);
        mainTable.add(actionsTable);
        
        createActionsButtons();
        
        // Кнопка побега внизу
        mainTable.row().padTop(20);
        TextButton fleeButton = new TextButton("Побежать", new TextButton.TextButtonStyle());
        fleeButton.addListener(click -> {
            tryFlee();
            return true;
        });
        mainTable.add(fleeButton).width(150).height(40);
    }
    
    private void createActionsButtons() {
        actionsTable.clear();
        
        // Кнопка атаки
        TextButton attackButton = new TextButton("Атака", new TextButton.TextButtonStyle());
        attackButton.addListener(click -> {
            performAttack();
            return true;
        });
        actionsTable.add(attackButton).width(120).height(40);
        
        // Кнопка защиты
        TextButton defendButton = new TextButton("Защита", new TextButton.TextButtonStyle());
        defendButton.addListener(click -> {
            performDefend();
            return true;
        });
        actionsTable.add(defendButton).width(120).height(40);
        
        actionsTable.row();
        
        // Кнопка использования предмета
        TextButton itemButton = new TextButton("Предмет", new TextButton.TextButtonStyle());
        itemButton.addListener(click -> {
            useItem();
            return true;
        });
        actionsTable.add(itemButton).width(120).height(40);
        
        // Кнопка навыка
        TextButton skillButton = new TextButton("Навык", new TextButton.TextButtonStyle());
        skillButton.addListener(click -> {
            useSkill();
            return true;
        });
        actionsTable.add(skillButton).width(120).height(40);
    }
    
    /**
     * Обновление информации о бое
     */
    private void updateBattleInfo() {
        BattleSystem battleSystem = game.getBattleSystem();
        
        if (battleSystem.isPlayerTurn()) {
            turnLabel.setText("Ход: ИГРОК");
        } else {
            turnLabel.setText("Ход: ПРОТИВНИК");
        }
        
        // Обновление статуса
        Combatant current = battleSystem.getCurrentCombatant();
        if (current != null) {
            statusLabel.setText(current.getCharacter().getName() + 
                              " (" + current.getCharacter().getCurrentHealth() + "/" + 
                              current.getCharacter().getMaxHealth() + " HP)");
        }
        
        // Проверка окончания боя
        if (!battleSystem.isBattleActive()) {
            handleBattleEnd();
        }
    }
    
    /**
     * Выполнение атаки
     */
    private void performAttack() {
        BattleSystem battleSystem = game.getBattleSystem();
        
        if (!battleSystem.isPlayerTurn()) return;
        
        // Найти ближайшего врага
        Combatant target = findNearestEnemy();
        if (target != null) {
            CombatAction action = CombatAction.attack(target);
            battleSystem.executeAction(action);
            updateBattleInfo();
        }
    }
    
    /**
     * Выполнение защиты
     */
    private void performDefend() {
        BattleSystem battleSystem = game.getBattleSystem();
        
        if (!battleSystem.isPlayerTurn()) return;
        
        CombatAction action = CombatAction.defend();
        battleSystem.executeAction(action);
        updateBattleInfo();
    }
    
    /**
     * Использование предмета
     */
    private void useItem() {
        // TODO: Открыть выбор предметов из инвентаря
    }
    
    /**
     * Использование навыка
     */
    private void useSkill() {
        // TODO: Открыть выбор навыков
    }
    
    /**
     * Поиск ближайшего врага
     */
    private Combatant findNearestEnemy() {
        BattleSystem battleSystem = game.getBattleSystem();
        Combatant current = battleSystem.getCurrentCombatant();
        
        if (current == null) return null;
        
        Combatant nearestEnemy = null;
        int minDistance = Integer.MAX_VALUE;
        
        for (Combatant combatant : battleSystem.getCombatants()) {
            if (!combatant.isPlayer() && combatant.isAlive()) {
                int distance = current.getPosition().distanceTo(combatant.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEnemy = combatant;
                }
            }
        }
        
        return nearestEnemy;
    }
    
    /**
     * Попытка побега
     */
    private void tryFlee() {
        BattleSystem battleSystem = game.getBattleSystem();
        
        if (battleSystem.tryFlee()) {
            // Успешный побег
            game.returnToGlobalMap();
        }
    }
    
    /**
     * Обработка окончания боя
     */
    private void handleBattleEnd() {
        BattleSystem.BattleState state = game.getBattleSystem().getState();
        
        switch (state) {
            case VICTORY:
                statusLabel.setText("ПОБЕДА!");
                // TODO: Показать награду
                break;
            case DEFEAT:
                statusLabel.setText("ПОРАЖЕНИЕ...");
                // TODO: Обработать смерть персонажа
                break;
            case FLED:
                statusLabel.setText("Вы сбежали!");
                break;
        }
        
        // Возврат на глобальную карту через небольшую задержку
        // В полной версии нужно показать экран результатов боя
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        updateBattleInfo();
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        updateBattleInfo();
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
