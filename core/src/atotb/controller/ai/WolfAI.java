package atotb.controller.ai;

import atotb.TwoBrothersGame;
import atotb.model.Army;
import atotb.model.Battle;
import atotb.model.Unit;
import atotb.util.Enum.Direction;
import atotb.util.PathFinder;
import com.badlogic.gdx.utils.Array;
import java.awt.Point;

/**
 * Very simple AI intended to be used for a pack of wolves.
 * 
 * @todo units fail to move if there is no enemy unit with a neighbouring accessible tile 
 * 
 * @author Ale Strooisma
 */
public class WolfAI implements ArtificialIntelligence {

	@Override
	public void playTurn(TwoBrothersGame game) {
		System.out.println("Start AI");

		// Getting some useful objects
		int player = game.getModel().getBattle().getCurrentPlayer();
		Battle battle = game.getModel().getBattle();
		Army army = battle.getArmy(player);
		Army enemyArmy = battle.getArmy((player + 1) % 2); //TODO multiple enemy armies
		PathFinder pf = game.getPathFinder();

		// Iterate over all units
		for (Unit unit : army.getUnits()) {
			if (unit.isAlive()) {
				game.selectUnit(unit);
				pf.calculateDistancesFrom(
						unit.getPosition().x, unit.getPosition().y);

				// Get nearest enemy
				boolean charge = true;
				Unit nearestEnemy = getNearestEnemy(game, enemyArmy, pf, charge);
				if (nearestEnemy == null) {
					charge = false;
					nearestEnemy = getNearestEnemy(game, enemyArmy, pf, charge);
				}

				// Act
				if (nearestEnemy != null) {
					int tx = nearestEnemy.getPosition().x;
					int ty = nearestEnemy.getPosition().y;
					Direction dir = game.getChargingDirection(tx, ty, pf);
					double d = game.getChargingDistance(tx, ty, pf, dir);

					if (charge && d <= unit.getTotalMovesRemaining()) {
						game.targetUnit(unit, nearestEnemy, pf);
					} else {
						Array<Point> path = pf.getPathTo(
								dir.getX(tx),
								dir.getY(ty),
								unit.getTotalMovesRemaining());
						if (path.first() != null) {
							game.moveUnit(unit, path.first().x, path.first().y);
						}
					}
					System.out.println("Targeting " + nearestEnemy.getName());
				} else {
					System.out.println("Targeting no-one!");
				}
			}
		}
		System.out.println("End AI");
	}

	private Unit getNearestEnemy(TwoBrothersGame game, Army enemyArmy, PathFinder pf, boolean charge) {
		Unit nearestEnemy = null;
		double minDistance = Double.MAX_VALUE;
		for (Unit enemyUnit : enemyArmy.getUnits()) {
			if (enemyUnit.isAlive() && !(charge && enemyUnit.isLockedIntoCombat())) {
				int tx = enemyUnit.getPosition().x;
				int ty = enemyUnit.getPosition().y;
				Direction dir = game.getChargingDirection(tx, ty, pf);
				if (dir != null) {
					double d = game.getChargingDistance(tx, ty, pf, dir);
					if (d < minDistance) {
						nearestEnemy = enemyUnit;
						minDistance = d;
					}
				}
			}
		}
		return nearestEnemy;
	}
}
