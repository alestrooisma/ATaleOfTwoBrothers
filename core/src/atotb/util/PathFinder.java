package atotb.util;

import atotb.model.BattleMap;

/**
 * TODO pooling TODO inaccessible terrain TODO full map
 *
 * @author Ale Strooisma
 */
public class PathFinder {

	private final BattleMap bmap;
	private final double[][] map;
	private final List list;

	public PathFinder(BattleMap bmap) {
		this.bmap = bmap;
		map = new double[bmap.getWidth()][bmap.getHeight()];
		list = new List();
	}

	public double getDistanceTo(int x, int y) {
		return map[x][y];
	}

	public void calculateDistancesFrom(int xi, int yi, double limit) {
		// Reset fields
		for (int i = 0; i < bmap.getWidth(); i++) {
			for (int j = 0; j < bmap.getHeight(); j++) {
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
		if (x >= 0 && x < bmap.getWidth() && y >= 0 && y < bmap.getHeight()
				&& map[x][y] == Double.MAX_VALUE
				&& bmap.getTile(x, y).isAccessible()) {
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
