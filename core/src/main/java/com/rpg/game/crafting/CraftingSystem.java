package com.rpg.game.crafting;

import com.rpg.game.character.Character;
import com.rpg.game.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Система крафта для создания предметов
 */
public class CraftingSystem {
    private List<CraftingRecipe> recipes;
    
    public CraftingSystem() {
        this.recipes = new ArrayList<>();
        initializeRecipes();
    }
    
    /**
     * Инициализация базовых рецептов
     */
    private void initializeRecipes() {
        // Примеры рецептов будут добавлены через внешний код или загрузку из файлов
    }
    
    /**
     * Добавить рецепт в систему
     */
    public void addRecipe(CraftingRecipe recipe) {
        recipes.add(recipe);
    }
    
    /**
     * Получить все доступные рецепты
     */
    public List<CraftingRecipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }
    
    /**
     * Получить рецепты, доступные персонажу (с учётом навыков)
     */
    public List<CraftingRecipe> getAvailableRecipes(Character character) {
        List<CraftingRecipe> available = new ArrayList<>();
        
        for (CraftingRecipe recipe : recipes) {
            if (recipe.getRequiredSkill() == null || 
                character.hasSkill(recipe.getRequiredSkill())) {
                available.add(recipe);
            }
        }
        
        return available;
    }
    
    /**
     * Попытка создания предмета
     * @return созданный предмет или null если крафт не удался
     */
    public Item craft(CraftingRecipe recipe, Character character) {
        // Проверка наличия ингредиентов
        if (!recipe.canCraft(character.getInventory())) {
            return null;
        }
        
        // Проверка навыка если требуется
        if (recipe.getRequiredSkill() != null && 
            !character.hasSkill(recipe.getRequiredSkill())) {
            return null;
        }
        
        // Изъятие ингредиентов
        recipe.consumeIngredients(character.getInventory());
        
        // Создание результата
        return recipe.getResult();
    }
    
    /**
     * Проверка может ли персонаж создать предмет по рецепту
     */
    public boolean canCraft(CraftingRecipe recipe, Character character) {
        return recipe.canCraft(character.getInventory()) &&
               (recipe.getRequiredSkill() == null || 
                character.hasSkill(recipe.getRequiredSkill()));
    }
}
