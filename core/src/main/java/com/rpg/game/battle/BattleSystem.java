package com.rpg.game.battle;

import com.rpg.game.character.Character;
import com.rpg.game.hex.Hex;
import com.rpg.game.map.GlobalMap;

import java.util.*;

/**
 * Менеджер пошагового боя
 * Управляет боевой сценой, порядком ходов и действиями участников
 */
public class BattleSystem {
    private List<Combatant> combatants;
    private List<Combatant> turnOrder;
    private int currentTurnIndex;
    private BattleState state;
    private Hex[][] battleMap;
    private int mapWidth;
    private int mapHeight;
    
    public BattleSystem() {
        this.combatants = new ArrayList<>();
        this.turnOrder = new ArrayList<>();
        this.currentTurnIndex = 0;
        this.state = BattleState.NOT_STARTED;
    }
    
    /**
     * Инициализация боя на основе глобальной карты
     */
    public void initializeBattle(GlobalMap globalMap, Character player, List<Character> enemies) {
        // Создание боевой карты (уменьшенная версия локации)
        createBattleMap();
        
        // Добавление игрока
        Hex playerStart = getSpawnPosition(true);
        Combatant playerCombatant = new Combatant(player, true, playerStart);
        combatants.add(playerCombatant);
        player.setCurrentHex(playerStart);
        
        // Добавление врагов
        for (int i = 0; i < enemies.size(); i++) {
            Hex enemyStart = getSpawnPosition(false);
            Combatant enemyCombatant = new Combatant(enemies.get(i), false, enemyStart);
            combatants.add(enemyCombatant);
            enemies.get(i).setCurrentHex(enemyStart);
        }
        
        // Расчёт порядка ходов
        calculateTurnOrder();
        
        state = BattleState.IN_PROGRESS;
        currentTurnIndex = 0;
    }
    
    /**
     * Создание боевой карты
     */
    private void createBattleMap() {
        mapWidth = 10;
        mapHeight = 10;
        battleMap = new Hex[mapWidth][mapHeight];
        
        // Генерация простой боевой карты
        // В полной версии здесь будет загрузка реальной локации
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                // Заглушка - в реальности нужно создавать реальные Hex объекты
                battleMap[x][y] = null; // Будет заменено на реальные гексы
            }
        }
    }
    
    /**
     * Получить позицию появления для игроков или врагов
     */
    private Hex getSpawnPosition(boolean isPlayer) {
        // Упрощённая логика - вернуть первый доступный гекс
        if (isPlayer) {
            return new Hex(0, 0, null);
        } else {
            return new Hex(mapWidth - 1, mapHeight - 1, null);
        }
    }
    
    /**
     * Расчёт порядка ходов на основе инициативы
     */
    private void calculateTurnOrder() {
        turnOrder.clear();
        turnOrder.addAll(combatants);
        
        // Сортировка по инициативе (убывание)
        turnOrder.sort((a, b) -> Integer.compare(b.getInitiative(), a.getInitiative()));
    }
    
    /**
     * Получить текущего участника, чей сейчас ход
     */
    public Combatant getCurrentCombatant() {
        if (turnOrder.isEmpty() || currentTurnIndex >= turnOrder.size()) {
            return null;
        }
        return turnOrder.get(currentTurnIndex);
    }
    
    /**
     * Проверить является ли текущий ход игрока
     */
    public boolean isPlayerTurn() {
        Combatant current = getCurrentCombatant();
        return current != null && current.isPlayer();
    }
    
    /**
     * Выполнить действие текущего участника
     */
    public boolean executeAction(CombatAction action) {
        Combatant current = getCurrentCombatant();
        if (current == null || current.hasActed()) {
            return false;
        }
        
        Combatant target = action.getTarget();
        
        // Валидация действия
        if (!isValidAction(current, action)) {
            return false;
        }
        
        // Выполнение действия
        current.takeTurn(action, target);
        
        // Проверка смерти целей
        checkCasualties();
        
        // Переход к следующему ходу
        nextTurn();
        
        return true;
    }
    
    /**
     * Проверка валидности действия
     */
    private boolean isValidAction(Combatant combatant, CombatAction action) {
        switch (action.getType()) {
            case ATTACK:
                return action.getTarget() != null && 
                       action.getTarget().isAlive() &&
                       isInRange(combatant.getPosition(), action.getTarget().getPosition(), 2);
            case MOVE:
                return action.getTargetPosition() != null;
            case SKILL:
                return action.getSkill() != null;
            case USE_ITEM:
                return action.getItem() != null;
            default:
                return true;
        }
    }
    
    /**
     * Проверка находится ли цель в диапазоне
     */
    private boolean isInRange(Hex from, Hex to, int range) {
        if (from == null || to == null) return false;
        return from.distanceTo(to) <= range;
    }
    
    /**
     * Переход к следующему ходу
     */
    private void nextTurn() {
        currentTurnIndex++;
        
        // Если все походили, начинаем новый раунд
        if (currentTurnIndex >= turnOrder.size()) {
            startNewRound();
        }
        
        // Пропуск мёртвых участников
        while (getCurrentCombatant() != null && !getCurrentCombatant().isAlive()) {
            currentTurnIndex++;
            if (currentTurnIndex >= turnOrder.size()) {
                startNewRound();
            }
        }
    }
    
    /**
     * Начало нового раунда
     */
    private void startNewRound() {
        currentTurnIndex = 0;
        
        // Сброс состояния всех живых участников
        for (Combatant combatant : combatants) {
            if (combatant.isAlive()) {
                combatant.resetTurn();
            }
        }
        
        // Пересчёт порядка ходов (опционально, если инициатива меняется)
        // calculateTurnOrder();
    }
    
    /**
     * Проверка погибших участников
     */
    private void checkCasualties() {
        for (Combatant combatant : combatants) {
            if (!combatant.isAlive()) {
                // Обработка смерти
                onCombatantDefeated(combatant);
            }
        }
    }
    
    /**
     * Обработка победы/поражения
     */
    private void onCombatantDefeated(Combatant combatant) {
        if (!combatant.isPlayer()) {
            // Враг повержен - дать опыт игроку
            for (Combatant c : combatants) {
                if (c.isPlayer() && c.isAlive()) {
                    c.getCharacter().addExperience(10); // Пример опыта
                }
            }
        }
        
        checkBattleEnd();
    }
    
    /**
     * Проверка условий окончания боя
     */
    private void checkBattleEnd() {
        long alivePlayers = combatants.stream()
            .filter(Combatant::isPlayer)
            .filter(Combatant::isAlive)
            .count();
            
        long aliveEnemies = combatants.stream()
            .filter(c -> !c.isPlayer())
            .filter(Combatant::isAlive)
            .count();
        
        if (alivePlayers == 0) {
            state = BattleState.DEFEAT;
        } else if (aliveEnemies == 0) {
            state = BattleState.VICTORY;
        }
    }
    
    /**
     * Попытка побега из боя
     */
    public boolean tryFlee() {
        Combatant current = getCurrentCombatant();
        if (current != null && current.isPlayer()) {
            // Шанс побега 50%
            if (Math.random() < 0.5) {
                state = BattleState.FLED;
                return true;
            }
            nextTurn();
        }
        return false;
    }
    
    // Геттеры
    public BattleState getState() { return state; }
    public List<Combatant> getCombatants() { return new ArrayList<>(combatants); }
    public List<Combatant> getTurnOrder() { return new ArrayList<>(turnOrder); }
    public int getCurrentTurnIndex() { return currentTurnIndex; }
    
    public boolean isBattleActive() {
        return state == BattleState.IN_PROGRESS;
    }
    
    /**
     * Состояния боя
     */
    public enum BattleState {
        NOT_STARTED,
        IN_PROGRESS,
        VICTORY,
        DEFEAT,
        FLED
    }
}
