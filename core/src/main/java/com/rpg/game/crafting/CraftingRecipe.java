package com.rpg.game.crafting;

import com.rpg.game.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Рецепт крафта
 */
public class CraftingRecipe {
    private String id;
    private String name;
    private Item result;
    private int resultCount;
    private Map<Item, Integer> ingredients;
    private int craftingTime; // Время создания в секундах
    private String requiredSkill; // Требуемый навык (опционально)
    private int requiredSkillLevel; // Требуемый уровень навыка
    
    public CraftingRecipe(String id, String name, Item result, int resultCount, int craftingTime) {
        this.id = id;
        this.name = name;
        this.result = result;
        this.resultCount = resultCount;
        this.ingredients = new HashMap<>();
        this.craftingTime = craftingTime;
        this.requiredSkill = null;
        this.requiredSkillLevel = 0;
    }
    
    /**
     * Добавить ингредиент в рецепт
     */
    public void addIngredient(Item ingredient, int quantity) {
        ingredients.put(ingredient, quantity);
    }
    
    /**
     * Проверить наличие всех ингредиентов в списке предметов
     */
    public boolean canCraft(List<Item> availableItems) {
        Map<String, Integer> itemCounts = new HashMap<>();
        
        for (Item item : availableItems) {
            String itemId = item.getId();
            itemCounts.put(itemId, itemCounts.getOrDefault(itemId, 0) + 1);
        }
        
        for (Map.Entry<Item, Integer> entry : ingredients.entrySet()) {
            Item ingredient = entry.getKey();
            int required = entry.getValue();
            int available = itemCounts.getOrDefault(ingredient.getId(), 0);
            
            if (available < required) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Изъять ингредиенты из списка предметов
     */
    public void consumeIngredients(List<Item> inventory) {
        for (Map.Entry<Item, Integer> entry : ingredients.entrySet()) {
            Item ingredient = entry.getKey();
            int quantity = entry.getValue();
            
            for (int i = 0; i < quantity && inventory.contains(ingredient); i++) {
                inventory.remove(ingredient);
            }
        }
    }
    
    // Геттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public Item getResult() { return result; }
    public int getResultCount() { return resultCount; }
    public Map<Item, Integer> getIngredients() { return new HashMap<>(ingredients); }
    public int getCraftingTime() { return craftingTime; }
    public String getRequiredSkill() { return requiredSkill; }
    public int getRequiredSkillLevel() { return requiredSkillLevel; }
    
    public void setRequiredSkill(String skillId, int level) {
        this.requiredSkill = skillId;
        this.requiredSkillLevel = level;
    }
    
    @Override
    public String toString() {
        return String.format("Recipe[%s] %s -> %d x %s", id, name, resultCount, result.getName());
    }
}
