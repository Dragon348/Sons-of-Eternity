package com.rpg.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rpg.game.map.GlobalMap;
import com.rpg.game.character.Character;
import com.rpg.game.crafting.CraftingSystem;
import com.rpg.game.battle.BattleSystem;
import com.rpg.game.ui.MainMenuScreen;
import com.rpg.game.ui.BattleScreen;
import com.rpg.game.ui.CharacterScreen;
import com.rpg.game.ui.CraftingScreen;
import com.rpg.game.ui.GlobalMapScreen;

/**
 * Основной класс игры libGDX
 * Управляет состояниями игры и основными системами
 */
public class RPGGame extends Game {
    public SpriteBatch batch;
    
    // Игровые системы
    private GlobalMap globalMap;
    private Character player;
    private CraftingSystem craftingSystem;
    private BattleSystem battleSystem;
    
    // Текущее состояние игры
    private GameState currentState;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        
        // Инициализация систем
        globalMap = new GlobalMap(15); // Карта радиусом 15 гексов
        player = new Character("Hero");
        craftingSystem = new CraftingSystem();
        battleSystem = new BattleSystem();
        
        // Установка начальной позиции игрока в центр карты
        player.setCurrentHex(globalMap.getHex(0, 0));
        
        // Переход к главному меню
        setScreen(new MainMenuScreen(this));
        currentState = GameState.MENU;
    }
    
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
    
    /**
     * Начать бой с врагами на текущей локации
     */
    public void startBattle() {
        // TODO: Создать врагов на основе текущей локации
        battleSystem.initializeBattle(globalMap, player, null);
        currentState = GameState.BATTLE;
        setScreen(new BattleScreen(this));
    }
    
    /**
     * Открыть окно персонажа
     */
    public void openCharacterScreen() {
        currentState = GameState.CHARACTER;
        setScreen(new CharacterScreen(this));
    }
    
    /**
     * Открыть окно крафта
     */
    public void openCraftingScreen() {
        currentState = GameState.CRAFTING;
        setScreen(new CraftingScreen(this));
    }
    
    /**
     * Вернуться к глобальной карте из локации/боя
     */
    public void returnToGlobalMap() {
        currentState = GameState.GLOBAL_MAP;
        setScreen(new GlobalMapScreen(this));
    }
    
    // Геттеры
    public GlobalMap getGlobalMap() { return globalMap; }
    public Character getPlayer() { return player; }
    public CraftingSystem getCraftingSystem() { return craftingSystem; }
    public BattleSystem getBattleSystem() { return battleSystem; }
    public GameState getCurrentState() { return currentState; }
    
    /**
     * Состояния игры
     */
    public enum GameState {
        MENU,           // Главное меню
        GLOBAL_MAP,     // Глобальная карта
        LOCAL_LOCATION, // Локальная локация
        BATTLE,         // Бой
        CHARACTER,      // Экран персонажа
        CRAFTING,       // Экран крафта
        INVENTORY       // Инвентарь
    }
}
