package com.rpg.game.battle;

import com.rpg.game.hex.Hex;

/**
 * Боевое действие в пошаговом бою
 */
public class CombatAction {
    private ActionType type;
    private Combatant target;
    private Hex targetPosition;
    private Object skill;
    private Object item;
    
    public CombatAction(ActionType type) {
        this.type = type;
    }
    
    /**
     * Создать действие атаки
     */
    public static CombatAction attack(Combatant target) {
        CombatAction action = new CombatAction(ActionType.ATTACK);
        action.target = target;
        return action;
    }
    
    /**
     * Создать действие перемещения
     */
    public static CombatAction move(Hex position) {
        CombatAction action = new CombatAction(ActionType.MOVE);
        action.targetPosition = position;
        return action;
    }
    
    /**
     * Создать действие использования навыка
     */
    public static CombatAction useSkill(Object skill, Combatant target) {
        CombatAction action = new CombatAction(ActionType.SKILL);
        action.skill = skill;
        action.target = target;
        return action;
    }
    
    /**
     * Создать действие использования предмета
     */
    public static CombatAction useItem(Object item, Combatant target) {
        CombatAction action = new CombatAction(ActionType.USE_ITEM);
        action.item = item;
        action.target = target;
        return action;
    }
    
    /**
     * Создать действие защиты
     */
    public static CombatAction defend() {
        return new CombatAction(ActionType.DEFEND);
    }
    
    // Геттеры
    public ActionType getType() { return type; }
    public Combatant getTarget() { return target; }
    public Hex getTargetPosition() { return targetPosition; }
    public Object getSkill() { return skill; }
    public Object getItem() { return item; }
    
    /**
     * Типы боевых действий
     */
    public enum ActionType {
        ATTACK,     // Атака
        MOVE,       // Перемещение
        SKILL,      // Использование навыка
        USE_ITEM,   // Использование предмета
        DEFEND,     // Защита
        WAIT,       // Пропуск хода
        FLEE        // Побег
    }
}
