package atotb.model;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * The part of the model representing the game map of a battle.
 * 
 * @author Ale Strooisma
 */
public class BattleMap {

	private final Tile[][] tiles;

	/**
	 * Creates a new instance of BattleMap with the given tiles.
	 *
	 * @param tiles the tiles to be on the map
	 */
	public BattleMap(Tile[][] tiles) {
		this.tiles = tiles;
	}

	/**
	 * Creates an empty BattleMap of the given dimensions.
	 *
	 * @param width the width of the map
	 * @param height the height of the map
	 */
	public BattleMap(int width, int height) {
		this.tiles = new Tile[width][height];
	}
	
	/**
	 * Returns the width of the map.
	 * @return the width of the map
	 */
	public int getWidth() {
		return tiles.length;
	}
	
	/**
	 * Returns the height of the map.
	 * @return the height of the map
	 */
	public int getHeight() {
		return tiles[0].length;
	}

	/**
	 * Returns an array of tiles, which is the internal representation of the 
	 * map.
	 *
	 * @return the array of tiles
	 */
	public Tile[][] getTiles() {
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
		return tiles[x][y];
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
	 * Sets the tile at its coordinates.
	 *
	 * @param tile the tile to set on the map
	 */
	public void setTile(Tile tile) {
		tiles[tile.getPosition().x][tile.getPosition().y] = tile;
	}

	/**
	 * Add a unit to the map. 
	 * Puts the unit on the tile and sets the unit's position.
	 * 
	 * @param u the unit to add
	 * @param x the x coordinate to put it at
	 * @param y the y coordinate to put it at
	 * @return true if the unit can be placed at that position.
	 */
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

	/**
	 * Add a unit to the map. 
	 * Puts the unit on the tile and sets the unit's position.
	 * 
	 * @param u the unit to add
	 * @param position the coordinates to put it at
	 * @return true if the unit can be placed at that position.
	 */
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
		return x >= 0 && x < getWidth()	&& y >= 0 && y < getHeight();
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
}
