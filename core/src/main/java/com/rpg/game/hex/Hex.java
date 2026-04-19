package com.rpg.game.hex;

import com.badlogic.gdx.math.Vector2;

/**
 * Представляет гексагональную ячейку на глобальной карте.
 * Использует кубические координаты (q, r, s) где q + r + s = 0
 */
public class Hex {
    private int q, r, s;
    private TerrainType terrain;
    private int elevation;
    private boolean isExplored;
    
    public Hex(int q, int r, TerrainType terrain) {
        this.q = q;
        this.r = r;
        this.s = -q - r; // Кубическое свойство: q + r + s = 0
        this.terrain = terrain;
        this.elevation = 0;
        this.isExplored = false;
    }
    
    /**
     * Расстояние до другого гекса в шагах
     */
    public int distanceTo(Hex other) {
        return (Math.abs(this.q - other.q) + 
                Math.abs(this.r - other.r) + 
                Math.abs(this.s - other.s)) / 2;
    }
    
    /**
     * Получить соседние гексы (6 направлений)
     */
    public static Hex[] getNeighbors(Hex hex) {
        int[][] directions = {
            {1, 0}, {1, -1}, {0, -1},
            {-1, 0}, {-1, 1}, {0, 1}
        };
        
        Hex[] neighbors = new Hex[6];
        for (int i = 0; i < 6; i++) {
            neighbors[i] = new Hex(
                hex.q + directions[i][0],
                hex.r + directions[i][1],
                hex.terrain
            );
        }
        return neighbors;
    }
    
    /**
     * Конвертация кубических координат в пиксельные для отрисовки
     */
    public Vector2 toPixelCoordinates(float hexSize) {
        float x = hexSize * (3f/2 * q);
        float y = hexSize * (Math.sqrt(3)/2 * q + Math.sqrt(3) * r);
        return new Vector2(x, y);
    }
    
    // Геттеры и сеттеры
    public int getQ() { return q; }
    public int getR() { return r; }
    public int getS() { return s; }
    public TerrainType getTerrain() { return terrain; }
    public void setTerrain(TerrainType terrain) { this.terrain = terrain; }
    public int getElevation() { return elevation; }
    public void setElevation(int elevation) { this.elevation = elevation; }
    public boolean isExplored() { return isExplored; }
    public void setExplored(boolean explored) { isExplored = explored; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Hex)) return false;
        Hex other = (Hex) obj;
        return q == other.q && r == other.r && s == other.s;
    }
    
    @Override
    public int hashCode() {
        return 31 * (31 * q + r) + s;
    }
    
    @Override
    public String toString() {
        return String.format("Hex(%d, %d, %d) - %s", q, r, s, terrain);
    }
}
