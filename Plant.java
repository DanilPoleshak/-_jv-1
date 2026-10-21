import java.awt.Color;

public class Plant extends Creature {

    public Plant(int row, int column) {
        this(row, column, 3);
    }

    public Plant(int row, int column, int energy) {
        super(row, column, energy);
    }

    @Override
    public void makeMove(World world) {
        if (!isAlive()) {
            return;
        }

        changeEnergy(
                world.getSettings().plantEnergyChange
        );

        if (isAlive()) {
            world.tryReproduction(this);
        }
    }

    @Override
    public String getSymbol() {
        return "P";
    }

    @Override
    public Color getColor() {
        return new Color(55, 246, 6);
    }
}