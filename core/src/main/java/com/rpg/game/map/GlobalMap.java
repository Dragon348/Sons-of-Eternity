package com.rpg.game.map;

import com.rpg.game.hex.Hex;
import com.rpg.game.hex.TerrainType;

import java.util.*;

/**
 * Глобальная гексагональная карта мира
 */
public class GlobalMap {
    private Map<String, Hex> hexes;
    private int mapRadius;
    
    public GlobalMap(int mapRadius) {
        this.mapRadius = mapRadius;
        this.hexes = new HashMap<>();
        generateMap();
    }
    
    /**
     * Генерация карты в форме шестиугольника заданного радиуса
     */
    private void generateMap() {
        for (int q = -mapRadius; q <= mapRadius; q++) {
            int r1 = Math.max(-mapRadius, -q - mapRadius);
            int r2 = Math.min(mapRadius, -q + mapRadius);
            
            for (int r = r1; r <= r2; r++) {
                // Простая генерация местности на основе координат
                TerrainType terrain = determineTerrain(q, r);
                Hex hex = new Hex(q, r, terrain);
                hexes.put(getHexKey(q, r), hex);
            }
        }
    }
    
    /**
     * Определение типа местности на основе координат (упрощённая версия)
     */
    private TerrainType determineTerrain(int q, int r) {
        // Пример простой процедурной генерации
        if (Math.abs(q) + Math.abs(r) > mapRadius - 2) {
            return TerrainType.WATER; // Океан по краям
        }
        
        int noise = (q * 7 + r * 13) % 100;
        if (noise < 40) return TerrainType.GRASS;
        if (noise < 60) return TerrainType.FOREST;
        if (noise < 75) return TerrainType.DESERT;
        if (noise < 85) return TerrainType.SWAMP;
        if (noise < 95) return TerrainType.ROAD;
        return TerrainType.MOUNTAIN;
    }
    
    private String getHexKey(int q, int r) {
        return q + "," + r;
    }
    
    /**
     * Получить гекс по координатам
     */
    public Hex getHex(int q, int r) {
        return hexes.get(getHexKey(q, r));
    }
    
    /**
     * Получить все соседние проходимые гексы
     */
    public List<Hex> getPassableNeighbors(Hex hex) {
        List<Hex> passable = new ArrayList<>();
        for (Hex neighbor : Hex.getNeighbors(hex)) {
            Hex actualHex = getHex(neighbor.getQ(), neighbor.getR());
            if (actualHex != null && actualHex.getTerrain().getMovementCost() < 999) {
                passable.add(actualHex);
            }
        }
        return passable;
    }
    
    /**
     * Поиск пути алгоритмом A* для гексагональной сетки
     */
    public List<Hex> findPath(Hex start, Hex end, float maxMovementPoints) {
        if (start == null || end == null) return new ArrayList<>();
        
        PriorityQueue<PathNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Set<String> closedSet = new HashSet<>();
        Map<String, PathNode> allNodes = new HashMap<>();
        
        PathNode startNode = new PathNode(start, null, 0, heuristic(start, end));
        openSet.add(startNode);
        allNodes.put(getHexKey(start.getQ(), start.getR()), startNode);
        
        float movementPointsUsed = 0;
        
        while (!openSet.isEmpty()) {
            PathNode current = openSet.poll();
            
            if (current.hex.equals(end)) {
                return reconstructPath(current);
            }
            
            closedSet.add(getHexKey(current.hex.getQ(), current.hex.getR()));
            
            for (Hex neighbor : getPassableNeighbors(current.hex)) {
                if (closedSet.contains(getHexKey(neighbor.getQ(), neighbor.getR()))) {
                    continue;
                }
                
                float terrainCost = neighbor.getTerrain().getMovementCost();
                float tentativeGScore = current.gScore + terrainCost;
                
                // Проверка лимита очков перемещения
                if (tentativeGScore > maxMovementPoints) {
                    continue;
                }
                
                String neighborKey = getHexKey(neighbor.getQ(), neighbor.getR());
                PathNode neighborNode = allNodes.get(neighborKey);
                
                if (neighborNode == null || tentativeGScore < neighborNode.gScore) {
                    PathNode newNode = new PathNode(
                        neighbor,
                        current,
                        tentativeGScore,
                        tentativeGScore + heuristic(neighbor, end)
                    );
                    
                    if (neighborNode != null) {
                        openSet.remove(neighborNode);
                    }
                    
                    openSet.add(newNode);
                    allNodes.put(neighborKey, newNode);
                }
            }
        }
        
        return new ArrayList<>(); // Путь не найден
    }
    
    /**
     * Эвристика для A* (расстояние до цели)
     */
    private float heuristic(Hex a, Hex b) {
        return a.distanceTo(b);
    }
    
    /**
     * Восстановление пути из конечной точки
     */
    private List<Hex> reconstructPath(PathNode node) {
        List<Hex> path = new ArrayList<>();
        PathNode current = node;
        
        while (current != null) {
            path.add(current.hex);
            current = current.parent;
        }
        
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Внутренний класс для хранения узлов пути
     */
    private static class PathNode {
        Hex hex;
        PathNode parent;
        float gScore;
        float fScore;
        
        PathNode(Hex hex, PathNode parent, float gScore, float fScore) {
            this.hex = hex;
            this.parent = parent;
            this.gScore = gScore;
            this.fScore = fScore;
        }
    }
    
    public int getMapRadius() {
        return mapRadius;
    }
    
    public Collection<Hex> getAllHexes() {
        return hexes.values();
    }
}
