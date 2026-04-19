package com.rpg.game.item;

import com.rpg.game.character.StatType;

import java.util.HashMap;
import java.util.Map;

/**
 * Предмет в инвентаре
 */
public class Item {
    private String id;
    private String name;
    private String description;
    private ItemType type;
    private int value; // Стоимость
    private int weight; // Вес
    private Map<StatType, Integer> statBonuses;
    private boolean isConsumable;
    
    public Item(String id, String name, String description, ItemType type, int value, int weight) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.weight = weight;
        this.statBonuses = new HashMap<>();
        this.isConsumable = false;
    }
    
    /**
     * Добавить бонус к характеристике
     */
    public void addStatBonus(StatType stat, int bonus) {
        statBonuses.put(stat, bonus);
    }
    
    /**
     * Получить бонус к характеристике
     */
    public int getStatBonus(StatType stat) {
        return statBonuses.getOrDefault(stat, 0);
    }
    
    /**
     * Установить предмет как расходуемый
     */
    public void setConsumable(boolean consumable) {
        isConsumable = consumable;
    }
    
    public boolean isConsumable() {
        return isConsumable;
    }
    
    // Геттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemType getType() { return type; }
    public int getValue() { return value; }
    public int getWeight() { return weight; }
    public Map<StatType, Integer> getStatBonuses() { return new HashMap<>(statBonuses); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Item)) return false;
        Item other = (Item) obj;
        return id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Item[%s] %s", id, name);
    }
    
    /**
     * Типы предметов
     */
    public enum ItemType {
        WEAPON,         // Оружие
        ARMOR,          // Броня
        ACCESSORY,      // Аксессуар
        CONSUMABLE,     // Расходуемый предмет
        MATERIAL,       // Материал для крафта
        QUEST,          // Квестовый предмет
        TOOL            // Инструмент
    }
}
