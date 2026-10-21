import java.awt.Color;

public class Predator extends Creature {

    public Predator(int row, int column) {
        this(row, column, 80);
    }

    public Predator(int row, int column, int energy) {
        super(row, column, energy);
    }

    @Override
    public void makeMove(World world) {
        if (!isAlive()) {
            return;
        }

        changeEnergy(
                world.getSettings().predatorEnergyChange
        );

        if (!isAlive()) {
            return;
        }

        world.movePredator(this);

        world.tryReproduction(this);
    }

    @Override
    public String getSymbol() {
        return "X";
    }

    @Override
    public Color getColor() {
        return new Color(255, 0, 0);
    }
}