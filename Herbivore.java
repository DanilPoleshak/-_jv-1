import java.awt.Color;

public class Herbivore extends Creature {

    public Herbivore(int row, int column) {
        this(row, column, 50);
    }

    public Herbivore(int row, int column, int energy) {
        super(row, column, energy);
    }

    @Override
    public void makeMove(World world) {
        if (!isAlive()) {
            return;
        }

        changeEnergy(
                world.getSettings().herbivoreEnergyChange
        );

        if (!isAlive()) {
            return;
        }

        world.moveHerbivore(this);

        world.tryReproduction(this);
    }

    @Override
    public String getSymbol() {
        return "T";
    }

    @Override
    public Color getColor() {
        return new Color(253, 193, 0);
    }
}