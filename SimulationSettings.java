public class SimulationSettings {

    public int rows = 50;
    public int columns = 50;

    public int maxSteps = 20_000;
    public int delay = 100;

    public int plants = 1200;
    public int herbivores = 100;
    public int predators = 12;

    public int plantInitialEnergy = 3;
    public int herbivoreInitialEnergy = 50;
    public int predatorInitialEnergy = 80;

    public int plantReproductionThreshold = 8;
    public int herbivoreReproductionThreshold = 100;
    public int predatorReproductionThreshold = 160;

    public int plantEnergyChange = 1;
    public int herbivoreEnergyChange = -1;
    public int predatorEnergyChange = -1;

    public int plantEnergyFromFood = 0;
    public int herbivoreEnergyFromFood = 20;
    public int predatorEnergyFromFood = 40;

    public int herbivoreViewDistance = 5;
    public int predatorViewDistance = 5;

    public int plantReproductionChance = 20;
    public int herbivoreReproductionChance = 10;
    public int predatorReproductionChance = 5;
}