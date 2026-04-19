package com.rpg.game.battle;

import com.rpg.game.character.Character;
import com.rpg.game.hex.Hex;

import java.util.ArrayList;
import java.util.List;

/**
 * Участник боя (персонаж или враг)
 */
public class Combatant {
    private Character character;
    private boolean isPlayer;
    private Hex position;
    private int initiative;
    private boolean hasActed;
    
    public Combatant(Character character, boolean isPlayer, Hex position) {
        this.character = character;
        this.isPlayer = isPlayer;
        this.position = position;
        this.hasActed = false;
        calculateInitiative();
    }
    
    /**
     * Расчёт инициативы на основе характеристик
     */
    private void calculateInitiative() {
        // Инициатива = ловкость + случайный фактор
        this.initiative = character.getAgility() + (int)(Math.random() * 10);
    }
    
    /**
     * Выполнить ход
     */
    public void takeTurn(CombatAction action, Combatant target) {
        if (hasActed) return;
        
        switch (action.getType()) {
            case ATTACK:
                performAttack(target);
                break;
            case MOVE:
                moveTo(action.getTargetPosition());
                break;
            case SKILL:
                performSkill(action.getSkill(), target);
                break;
            case USE_ITEM:
                useItem(action.getItem(), target);
                break;
            case DEFEND:
                performDefend();
                break;
        }
        
        hasActed = true;
    }
    
    /**
     * Атака цели
     */
    private void performAttack(Combatant target) {
        int damage = calculateDamage();
        target.character.takeDamage(damage);
    }
    
    /**
     * Расчёт урона
     */
    private int calculateDamage() {
        int baseDamage = character.getStrength() / 2;
        // TODO: Добавить бонусы от оружия и критические удары
        return baseDamage;
    }
    
    /**
     * Перемещение на позицию
     */
    private void moveTo(Hex newPosition) {
        this.position = newPosition;
        character.setCurrentHex(newPosition);
    }
    
    /**
     * Использование навыка
     */
    private void performSkill(Object skill, Combatant target) {
        // TODO: Реализация навыков в бою
    }
    
    /**
     * Использование предмета
     */
    private void useItem(Object item, Combatant target) {
        // TODO: Реализация использования предметов
    }
    
    /**
     * Защита (увеличение защиты до следующего хода)
     */
    private void performDefend() {
        // TODO: Реализация защитной стойки
    }
    
    /**
     * Сброс состояния хода
     */
    public void resetTurn() {
        hasActed = false;
        character.restoreMovementPoints();
    }
    
    // Геттеры
    public Character getCharacter() { return character; }
    public boolean isPlayer() { return isPlayer; }
    public Hex getPosition() { return position; }
    public int getInitiative() { return initiative; }
    public boolean hasActed() { return hasActed; }
    public boolean isAlive() { return character.isAlive(); }
    
    public void setPosition(Hex position) {
        this.position = position;
    }
}
