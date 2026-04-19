package com.rpg.game.hex;

/**
 * Типы местности для гексагональной карты
 */
public enum TerrainType {
    GRASS(1.0f, false),
    FOREST(2.0f, true),
    MOUNTAIN(3.0f, true),
    WATER(999.0f, false), // непроходимая
    DESERT(1.5f, false),
    SWAMP(2.5f, true),
    ROAD(0.5f, false),
    CITY(1.0f, false);
    
    private final float movementCost;
    private final boolean providesCover;
    
    TerrainType(float movementCost, boolean providesCover) {
        this.movementCost = movementCost;
        this.providesCover = providesCover;
    }
    
    public float getMovementCost() {
        return movementCost;
    }
    
    public boolean providesCover() {
        return providesCover;
    }
}
