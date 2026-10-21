import java.awt.Color;

public abstract class Creature {

    protected int row;
    protected int column;
    protected int energy;

    public Creature(
            int row,
            int column,
            int energy
    ) {
        this.row = row;
        this.column = column;
        this.energy = energy;
    }

    public abstract void makeMove(World world);

    public abstract String getSymbol();

    public abstract Color getColor();

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getEnergy() {
        return energy;
    }

    public void setPosition(
            int row,
            int column
    ) {
        this.row = row;
        this.column = column;
    }

    public boolean isAlive() {
        return energy > 0;
    }

    public void changeEnergy(int change) {
        long result = (long) energy + change;

        energy = (int) Math.max(
                0L,
                Math.min(Integer.MAX_VALUE, result)
        );
    }

    public void addEnergy(int amount) {
        if (amount > 0) {
            changeEnergy(amount);
        }
    }

    public void loseEnergy(int amount) {
        if (amount > 0) {
            changeEnergy(-amount);
        }
    }
}