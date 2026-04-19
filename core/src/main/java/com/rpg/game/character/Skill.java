package com.rpg.game.character;

import java.util.HashMap;
import java.util.Map;

/**
 * Навык персонажа
 */
public class Skill {
    private String id;
    private String name;
    private String description;
    private int manaCost;
    private int cooldown;
    private SkillType type;
    
    public Skill(String id, String name, String description, int manaCost, int cooldown, SkillType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.type = type;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getManaCost() { return manaCost; }
    public int getCooldown() { return cooldown; }
    public SkillType getType() { return type; }
    
    /**
     * Типы навыков
     */
    public enum SkillType {
        ACTIVE,      // Активный навык (требует активации)
        PASSIVE,     // Пассивный навык (всегда активен)
        COMBAT,      // Боевой навык
        CRAFTING,    // Навык крафта
        EXPLORATION  // Навык исследования
    }
}
