package com.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rpg.game.RPGGame;
import com.rpg.game.crafting.CraftingRecipe;
import com.rpg.game.crafting.CraftingSystem;
import com.rpg.game.item.Item;

import java.util.List;

/**
 * Экран крафта
 * Позволяет создавать предметы из ресурсов
 */
public class CraftingScreen extends BaseScreen {
    private Stage stage;
    private BitmapFont font;
    
    private Table recipesTable;
    private Label selectedRecipeLabel;
    private Label ingredientsLabel;
    private Label resultLabel;
    private TextButton craftButton;
    
    private CraftingRecipe currentRecipe;
    
    public CraftingScreen(RPGGame game) {
        super(game);
        this.stage = new Stage(new ScreenViewport());
        this.font = new BitmapFont();
        
        initializeUI();
        updateRecipesList();
    }
    
    private void initializeUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(10);
        stage.addActor(mainTable);
        
        // Заголовок
        Label titleLabel = new Label("Крафт", new Label.LabelStyle(font, null));
        mainTable.add(titleLabel).colspan(3).padBottom(20).row();
        
        // Левая колонка - список рецептов
        Label recipesTitle = new Label("Рецепты:", new Label.LabelStyle(font, null));
        mainTable.add(recipesTitle).top().row();
        
        recipesTable = new Table();
        recipesTable.defaults().pad(5);
        ScrollPane recipesScroll = new ScrollPane(recipesTable);
        recipesScroll.setOverscroll(false, false);
        mainTable.add(recipesScroll).width(200).height(400).top().padRight(10);
        
        // Правая колонка - информация о рецепте
        Table infoTable = new Table();
        infoTable.defaults().left().pad(5);
        
        infoTable.add(new Label("Выбранный рецепт:", new Label.LabelStyle(font, null))).row();
        selectedRecipeLabel = new Label("Не выбран", new Label.LabelStyle(font, null));
        infoTable.add(selectedRecipeLabel).row();
        
        infoTable.add().pad(10).row();
        
        infoTable.add(new Label("Ингредиенты:", new Label.LabelStyle(font, null))).row();
        ingredientsLabel = new Label("", new Label.LabelStyle(font, null));
        infoTable.add(ingredientsLabel).row();
        
        infoTable.add().pad(10).row();
        
        infoTable.add(new Label("Результат:", new Label.LabelStyle(font, null))).row();
        resultLabel = new Label("", new Label.LabelStyle(font, null));
        infoTable.add(resultLabel).row();
        
        infoTable.add().pad(20).row();
        
        // Кнопка крафта
        craftButton = new TextButton("Создать", new TextButton.TextButtonStyle());
        craftButton.addListener(click -> {
            craftItem();
            return true;
        });
        craftButton.setEnabled(false);
        infoTable.add(craftButton).width(200).height(50);
        
        mainTable.add(infoTable).top();
        
        // Кнопка закрытия внизу
        mainTable.row().colspan(3).padTop(20);
        TextButton closeButton = new TextButton("Закрыть", new TextButton.TextButtonStyle());
        closeButton.addListener(click -> {
            game.returnToGlobalMap();
            return true;
        });
        mainTable.add(closeButton).width(200).height(40);
    }
    
    /**
     * Обновление списка доступных рецептов
     */
    private void updateRecipesList() {
        recipesTable.clear();
        
        CraftingSystem craftingSystem = game.getCraftingSystem();
        List<CraftingRecipe> recipes = craftingSystem.getAvailableRecipes(game.getPlayer());
        
        for (CraftingRecipe recipe : recipes) {
            TextButton recipeButton = new TextButton(recipe.getName(), new TextButton.TextButtonStyle());
            recipeButton.addListener(click -> {
                selectRecipe(recipe);
                return true;
            });
            recipesTable.add(recipeButton).left().row();
        }
    }
    
    /**
     * Выбор рецепта
     */
    private void selectRecipe(CraftingRecipe recipe) {
        currentRecipe = recipe;
        
        selectedRecipeLabel.setText(recipe.getName());
        
        // Отображение ингредиентов
        StringBuilder ingredientsText = new StringBuilder();
        for (var entry : recipe.getIngredients().entrySet()) {
            ingredientsText.append(entry.getKey().getName())
                          .append(": ")
                          .append(entry.getValue())
                          .append("\n");
        }
        ingredientsLabel.setText(ingredientsText.toString());
        
        // Отображение результата
        resultLabel.setText(String.format("%d x %s", 
                                         recipe.getResultCount(),
                                         recipe.getResult().getName()));
        
        // Проверка возможности крафта
        boolean canCraft = game.getCraftingSystem().canCraft(recipe, game.getPlayer());
        craftButton.setEnabled(canCraft);
    }
    
    /**
     * Создание предмета
     */
    private void craftItem() {
        if (currentRecipe == null) return;
        
        CraftingSystem craftingSystem = game.getCraftingSystem();
        Item result = craftingSystem.craft(currentRecipe, game.getPlayer());
        
        if (result != null) {
            // Успешный крафт
            game.getPlayer().addItem(result);
            
            // Обновление UI
            updateRecipesList();
            selectRecipe(currentRecipe);
        } else {
            // Ошибка крафта
            // TODO: Показать сообщение об ошибке
        }
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.15f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        updateRecipesList();
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
