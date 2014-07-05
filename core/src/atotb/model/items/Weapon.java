package atotb.model.items;

/**
 *
 * @author Ale Strooisma
 */
public abstract class Weapon extends Item {

	private final double power;

	public Weapon(String name, String summary, String description, double power) {
		super(name, summary, description);
		this.power = power;
	}

	public double getPower() {
		return power;
	}
}
