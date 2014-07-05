package atotb.model;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * An implementation of Map, being tiled map.
 *
 * TODO remove negative coords
 * 
 * @author Ale Strooisma
 */
public class BattleMap {

	private int minX, maxX, minY, maxY;
	private Tile[] tiles;

	/**
	 * Creates a new instance of map with the given dimension and tiles.
	 * Requires: tiles.length >= (maxX-minX+1)*(maxY-minY)+(maxX-minX)
	 *
	 * @param minX the smallest allowed x coordinate
	 * @param maxX the largest allowed x coordinate
	 * @param minY the smallest allowed y coordinate
	 * @param maxY the largest allowed y coordinate
	 * @param tiles the tiles to be on the map
	 */
	public BattleMap(int minX, int maxX, int minY, int maxY, Tile[] tiles) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.tiles = tiles;
	}

	/**
	 * Creates an empty map of the given dimensions.
	 *
	 * @param minX the smallest allowed x coordinate
	 * @param maxX the largest allowed x coordinate
	 * @param minY the smallest allowed y coordinate
	 * @param maxY the largest allowed y coordinate
	 */
	public BattleMap(int minX, int maxX, int minY, int maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.tiles = new Tile[transformCoords(maxX, maxY) + 1]; //TODO mins!
	}

	/**
	 * Creates a new instance of map with the given dimensions and tiles, 
	 * setting the lower coordinate bounds to 0.
	 *
	 * @param width the width of the map
	 * @param height the height of the map
	 * @param tiles the tiles to be on the map
	 */
	public BattleMap(int width, int height, Tile[] tiles) {
		this.minX = 0;
		this.maxX = width - 1;
		this.minY = 0;
		this.maxY = height - 1;
		this.tiles = tiles;
	}

	/**
	 * Creates a new instance of map with the given dimensions, setting the 
	 * lower coordinate bounds to 0.
	 *
	 * @param width the width of the map
	 * @param height the height of the map
	 */
	public BattleMap(int width, int height) {
		this.minX = 0;
		this.maxX = width - 1;
		this.minY = 0;
		this.maxY = height - 1;
		this.tiles = new Tile[transformCoords(maxX, maxY) + 1]; //TODO mins!
	}

	/**
	 * Returns the lowest allowed x coordinate of this map.
	 *
	 * @return the lowest x coordinate
	 */
	public int getMinX() {
		return minX;
	}

	/**
	 * Returns the highest allowed x coordinate of this map.
	 *
	 * @return the highest x coordinate
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * Returns the lowest allowed y coordinate of this map.
	 *
	 * @return the lowest y coordinate
	 */
	public int getMinY() {
		return minY;
	}

	/**
	 * Returns the highest allowed y coordinate of this map.
	 *
	 * @return the highest y coordinate
	 */
	public int getMaxY() {
		return maxY;
	}
	
	public int getWidth() {
		return minX + 1 + maxX;
	}
	
	public int getHeight() {
		return minY + 1 + maxY;
	}

	/**
	 * Returns the array of tiles, which basically is the internal
	 * representation of the map.
	 *
	 * @return the array of tiles
	 */
	public Tile[] getTiles() {
		return tiles;
	}

	/**
	 * Returns the tile at the given coordinates.
	 *
	 * @param x the x coordinate of the requested tile
	 * @param y the y coordinate of the requested tile
	 * @return the tile at coordinates (x, y)
	 */
	public Tile getTile(int x, int y) {
		return tiles[transformCoords(x, y)];
	}

	/**
	 * Returns the tile at the given coordinates.
	 *
	 * @param position the position of the requested tile
	 * @return the tile at the position given by position
	 */
	public Tile getTile(Point position) {
		return getTile((int) position.getX(), (int) position.getY());
	}

	/**
	 * Sets the tile at the given coordinates to the given tile.
	 *
	 * @param tile the tile to be placed at the given coordinates
	 * @param x the x coordinate of where the tile should be be set
	 * @param y the y coordinate of where the tile should be be set
	 */
	public void setTile(Tile tile, int x, int y) {
		tiles[transformCoords(x, y)] = tile;
	}

	/**
	 * Sets the tile at the given coordinates to the given tile.
	 *
	 * @param tile the tile to be placed at the given coordinates
	 * @param position where the tile should be be set
	 */
	public void setTile(Tile tile, Point position) {
		setTile(tile, position.x, position.y);
	}

	public boolean addUnit(Unit u, int x, int y) {
		Tile tile = getTile(x,y);
		if (tile.isAccessible()) {
			tile.setUnit(u);
			u.setPosition(new Point(x, y));
			return true;
		} else {
			return false;
		}
	}

	public boolean addUnit(Unit u, Point position) {
		return addUnit(u, position.x, position.y);
	}
	
	/**
	 * Returns true if the point specified by x and y is within the bounds of 
	 * this map.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @return true if the point is within the bounds
	 */
	public boolean contains(double x, double y) {
		return x >= minX && x <= maxX
				&& y >= minY && y <= maxY;
	}
	
	/**
	 * Returns true if the point specified by point is within the bounds of 
	 * this map.
	 * @param point the point to check
	 * @return true if point is within the bounds
	 */
	public boolean contains(Point2D point) {
		return contains(point.getX(), point.getY());
	}

	/**
	 * Transforms 2D coordinates into an array index used by getTile and
	 * setTile.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the transformed coordinates
	 */
	protected final int transformCoords(int x, int y) {
		return (maxX - minX + 1) * (y - minY) + (x - minX);
	}
}
