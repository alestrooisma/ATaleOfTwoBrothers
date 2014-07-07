package atotb.util;

/**
 * TODO pooling TODO inaccessible terrain TODO full map
 *
 * @author Ale Strooisma
 */
public class PathFinder {

	private final int width;
	private final int height;
	private final double[][] map;
	private final List list;

	public PathFinder(int width, int height) {
		this.width = width;
		this.height = height;
		map = new double[width][height];
		list = new List();
	}

	public double getDistanceTo(int x, int y) {
		return map[x][y];
	}

	public void calculateDistancesFrom(int xi, int yi, double limit) {
		// Reset fields
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				map[i][j] = Double.MAX_VALUE;
			}
		}
		list.clear();

		// Starting point
		map[xi][yi] = 0;
		list.add(xi, yi);

		// Breadth first 'search'
		Item item = list.next();
		int x, y;
		double c;
		while (item != null) {
			x = item.x;
			y = item.y;
			c = map[x][y];
			//TODO repool item here

			if (c + 1 <= limit) {
				explore(x - 1, y, c + 1);
				explore(x + 1, y, c + 1);
				explore(x, y - 1, c + 1);
				explore(x, y + 1, c + 1);
				if (c + 1.5 <= limit) {
					explore(x - 1, y - 1, c + 1.5);
					explore(x - 1, y + 1, c + 1.5);
					explore(x + 1, y - 1, c + 1.5);
					explore(x + 1, y + 1, c + 1.5);
				}
			}

			item = list.next();
		}
	}

	private void explore(int x, int y, double c) {
		if (x >= 0 && x < width && y >= 0 && y < height
				&& map[x][y] == Double.MAX_VALUE) {
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
