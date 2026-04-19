package com.rpg.game.character;

import com.rpg.game.hex.Hex;
import com.rpg.game.item.Item;

import java.util.*;

/**
 * Класс персонажа с характеристиками, инвентарём и экипировкой
 */
public class Character {
    private String name;
    private int level;
    private int experience;
    
    // Характеристики
    private int strength;
    private int agility;
    private int intelligence;
    private int vitality;
    
    // Боевые параметры
    private int maxHealth;
    private int currentHealth;
    private int maxMana;
    private int currentMana;
    private int movementPoints;
    private int currentMovementPoints;
    
    // Позиция на глобальной карте
    private Hex currentHex;
    
    // Инвентарь и экипировка
    private List<Item> inventory;
    private Map<EquipmentSlot, Item> equipment;
    
    // Навыки
    private List<Skill> skills;
    
    public Character(String name) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
        
        // Базовые характеристики
        this.strength = 10;
        this.agility = 10;
        this.intelligence = 10;
        this.vitality = 10;
        
        // Расчёт производных параметров
        recalculateStats();
        this.currentHealth = maxHealth;
        this.currentMana = maxMana;
        this.currentMovementPoints = movementPoints;
        
        this.inventory = new ArrayList<>();
        this.equipment = new EnumMap<>(EquipmentSlot.class);
        this.skills = new ArrayList<>();
    }
    
    /**
     * Пересчёт производных характеристик на основе основных
     */
    public void recalculateStats() {
        this.maxHealth = vitality * 10;
        this.maxMana = intelligence * 8;
        this.movementPoints = 5 + (agility / 2);
        this.currentMovementPoints = movementPoints;
    }
    
    /**
     * Попытка перемещения к целевому гексу с учётом очков перемещения
     */
    public boolean tryMoveTo(Hex targetHex) {
        if (currentHex == null || targetHex == null) return false;
        
        int distance = currentHex.distanceTo(targetHex);
        if (distance <= currentMovementPoints) {
            currentHex = targetHex;
            currentMovementPoints -= distance;
            return true;
        }
        return false;
    }
    
    /**
     * Восстановление очков перемещения в начале хода
     */
    public void restoreMovementPoints() {
        this.currentMovementPoints = movementPoints;
    }
    
    /**
     * Добавить предмет в инвентарь
     */
    public void addItem(Item item) {
        inventory.add(item);
    }
    
    /**
     * Удалить предмет из инвентаря
     */
    public boolean removeItem(Item item) {
        return inventory.remove(item);
    }
    
    /**
     * Экипировать предмет
     */
    public boolean equipItem(Item item, EquipmentSlot slot) {
        if (!inventory.contains(item)) return false;
        
        Item currentEquipped = equipment.get(slot);
        if (currentEquipped != null) {
            inventory.add(currentEquipped);
        }
        
        equipment.put(slot, item);
        inventory.remove(item);
        recalculateStats();
        return true;
    }
    
    /**
     * Снять предмет с экипировки
     */
    public boolean unequipItem(EquipmentSlot slot) {
        Item item = equipment.get(slot);
        if (item == null) return false;
        
        equipment.remove(slot);
        inventory.add(item);
        recalculateStats();
        return true;
    }
    
    /**
     * Получить бонус от экипировки к характеристике
     */
    public int getEquipmentBonus(StatType stat) {
        int bonus = 0;
        for (Item item : equipment.values()) {
            if (item != null) {
                bonus += item.getStatBonus(stat);
            }
        }
        return bonus;
    }
    
    /**
     * Изучить новый навык
     */
    public void learnSkill(Skill skill) {
        if (!skills.contains(skill)) {
            skills.add(skill);
        }
    }
    
    /**
     * Проверка наличия навыка
     */
    public boolean hasSkill(String skillId) {
        for (Skill skill : skills) {
            if (skill.getId().equals(skillId)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Добавить опыт и проверить повышение уровня
     */
    public void addExperience(int exp) {
        this.experience += exp;
        
        int requiredExp = level * 100;
        while (experience >= requiredExp) {
            levelUp();
            requiredExp = (level + 1) * 100;
        }
    }
    
    /**
     * Повышение уровня
     */
    private void levelUp() {
        level++;
        experience -= level * 100;
        
        // Увеличение характеристик
        strength += 2;
        agility += 2;
        intelligence += 2;
        vitality += 3;
        
        recalculateStats();
        currentHealth = maxHealth;
        currentMana = maxMana;
    }
    
    /**
     * Получить итоговую характеристику с учётом бонусов
     */
    public int getTotalStat(StatType stat) {
        int base = getBaseStat(stat);
        int bonus = getEquipmentBonus(stat);
        return base + bonus;
    }
    
    private int getBaseStat(StatType stat) {
        switch (stat) {
            case STRENGTH: return strength;
            case AGILITY: return agility;
            case INTELLIGENCE: return intelligence;
            case VITALITY: return vitality;
            default: return 0;
        }
    }
    
    // Геттеры и сеттеры
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public int getStrength() { return getTotalStat(StatType.STRENGTH); }
    public int getAgility() { return getTotalStat(StatType.AGILITY); }
    public int getIntelligence() { return getTotalStat(StatType.INTELLIGENCE); }
    public int getVitality() { return getTotalStat(StatType.VITALITY); }
    public int getMaxHealth() { return maxHealth; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxMana() { return maxMana; }
    public int getCurrentMana() { return currentMana; }
    public int getMovementPoints() { return movementPoints; }
    public int getCurrentMovementPoints() { return currentMovementPoints; }
    public Hex getCurrentHex() { return currentHex; }
    public void setCurrentHex(Hex hex) { this.currentHex = hex; }
    public List<Item> getInventory() { return Collections.unmodifiableList(inventory); }
    public Map<EquipmentSlot, Item> getEquipment() { return Collections.unmodifiableMap(equipment); }
    public List<Skill> getSkills() { return Collections.unmodifiableList(skills); }
    
    public void takeDamage(int damage) {
        this.currentHealth = Math.max(0, currentHealth - damage);
    }
    
    public void heal(int amount) {
        this.currentHealth = Math.min(maxHealth, currentHealth + amount);
    }
    
    public void restoreMana(int amount) {
        this.currentMana = Math.min(maxMana, currentMana + amount);
    }
    
    public boolean isAlive() {
        return currentHealth > 0;
    }
}
