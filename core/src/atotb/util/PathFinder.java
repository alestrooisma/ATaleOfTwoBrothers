package atotb.util;

/**
 * TODO pooling
 * TODO inaccessible terrain
 * TODO full map
 * @author Ale Strooisma
 */
public class PathFinder {

	private final int width;
	private final int height;
	private final double[][] map;
	private final List list;
	private int targetX;
	private int targetY;
	boolean success = false;

	public PathFinder(int width, int height) {
		this.width = width;
		this.height = height;
		map = new double[width][height];
		list = new List();
	}

	public double findDistance(int x1, int y1, int x2, int y2, double limit) {
		// Reset fields
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				map[x][y] = -1;
			}
		}
		map[x1][y1] = 0;
		list.clear();
		targetX = x2;
		targetY = y2;
		success = false;
		
		// Starting point
		list.add(x1, y1);

		// Breadth first search
		Item item = list.next();
		int x, y;
		double c;
		while (!success && item != null) {
			x = item.x;
			y = item.y;
			//repool item
			c = map[x][y];
			explore(x - 1, y, c + 1);
			explore(x + 1, y, c + 1);
			explore(x, y - 1, c + 1);
			explore(x, y + 1, c + 1);
			explore(x - 1, y - 1, c + 1.5);
			explore(x - 1, y + 1, c + 1.5);
			explore(x + 1, y - 1, c + 1.5);
			explore(x + 1, y + 1, c + 1.5);
			item = list.next();
		}
		return map[targetX][targetY];
	}

	private void explore(int x, int y, double c) {
		if (x == targetX && y == targetY) {
			map[x][y] = c;
			success = true;
		} else if (x >= 0 && x < width && y >= 0 && y < height 
				&& map[x][y] == -1) {
			map[x][y] = c;
			list.add(x, y);
		}
	}

	private class List {

		Item first = null;
		Item last = null;

		public void add(int x, int y) {
			Item item = new Item(); //TODO pooled
			item.x = x;
			item.y = y;
			item.next = null;
			if (first == null) {
				first = item;
				last = item;
			} else {
				last.next = item;
				last = item;
			}
		}

		public Item next() {
			if (first == null) {
				return null;
			}
			Item item = first;
			first = first.next;
			if (first == null) {
				last = null;
			}
			return item;
		}

		public void clear() {
			//TODO pooling
			first = null;
			last = null;
		}
	}

	private class Item {

		int x, y;
		Item next;
	}
}
