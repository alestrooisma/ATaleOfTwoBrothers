package atotb.util;

import atotb.model.BattleMap;
import atotb.util.Enum.Direction;
import static atotb.util.Enum.Direction.*;
import com.badlogic.gdx.utils.Array;
import java.awt.Point;

/**
 * @TODO pooling
 * @TODO inaccessible terrain
 * @TODO full map
 *
 * @author Ale Strooisma
 */
public class PathFinder {

	public final static double INACCESSIBLE = Double.MAX_VALUE;
	private final BattleMap bmap;
	private final double[][] map;
	private final List list;
	private int xi;
	private int yi;

	public PathFinder(BattleMap bmap) {
		this.bmap = bmap;
		map = new double[bmap.getWidth()][bmap.getHeight()];
		list = new List();
	}

	public double getDistanceTo(int x, int y) {
		return map[x][y];
	}

	public double getDistanceTo_safe(int x, int y) {
		if (bmap.contains(x, y)) {
			return getDistanceTo(x, y);
		} else {
			return INACCESSIBLE;
		}
	}

	public Array<Point> getPathTo(int x, int y) {
		if (map[x][y] == INACCESSIBLE) {
			return null;
		}
		Array<Point> path = new Array<Point>((int) map[x][y]);
		Point start = bmap.getTile(xi, yi).getPosition();
		Point target = bmap.getTile(x, y).getPosition();
		Direction dir, bestDir;
		double dist, bestDist;
		while (!target.equals(start)) {
			dir = N;
			bestDir = dir;
			bestDist = map[dir.getX(x)][dir.getY(y)];
			dir = nextDir(dir);
			do {
				dist = map[dir.getX(x)][dir.getY(y)];
				if (dist < bestDist) {
					bestDir = dir;
					bestDist = dist;
				}
				dir = nextDir(dir);
			} while (dir != null);

			path.add(target);
			x = bestDir.getX(x);
			y = bestDir.getY(y);
			target = new Point(x, y);
		}
		return path;
	}

	private Direction nextDir(Direction dir) {
		switch (dir) {
			case N:
				return E;
			case E:
				return S;
			case S:
				return W;
			case W:
				return NE;
			case NE:
				return SE;
			case SE:
				return SW;
			case SW:
				return NW;
			default: // NW
				/* If this is reached, range numbering has gone wrong, because 
				 * an accessible, non-zero spot does not have neighbours closer
				 * to the starting point, that is no neighbours a lower number.
				 */
//				throw new RuntimeException("Impossible pathfinding error!");
				return null;
		}
	}

	public void calculateDistancesFrom(int xi, int yi) {
		calculateDistancesFrom(xi, yi, INACCESSIBLE);
	}

	public void calculateDistancesFrom(int xi, int yi, double limit) {
		// Reset fields
		for (int i = 0; i < bmap.getWidth(); i++) {
			for (int j = 0; j < bmap.getHeight(); j++) {
				map[i][j] = INACCESSIBLE;
			}
		}
		list.clear();

		this.xi = xi;
		this.yi = yi;

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
				&& map[x][y] == INACCESSIBLE
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
