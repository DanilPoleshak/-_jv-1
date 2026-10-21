import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class World {

    // Только для совместимости со старым Main.
    // Для динамического поля используй getRows()/getColumns().
    public static final int ROWS = 50;
    public static final int COLUMNS = 50;

    private static final int HISTORY_LIMIT = 1000;

    private static final int[][] DIRECTIONS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    private SimulationSettings settings =
            new SimulationSettings();

    private Creature[][] field =
            new Creature[settings.rows][settings.columns];

    private final List<Creature> creatures =
            new ArrayList<>();

    private final Deque<WorldState> past =
            new ArrayDeque<>();

    private final Deque<WorldState> future =
            new ArrayDeque<>();

    private final Random random = new Random();

    private int step = 0;

    private boolean ecosystemAlive = false;

    private String endReason =
            "Начальная популяция ещё не создана";

    // ==================== НАСТРОЙКИ ====================

    public World() {
    }

    public World(SimulationSettings settings) {
        applySettings(settings);
    }

    public void applySettings(SimulationSettings newSettings) {
        validateSettings(newSettings);

        Creature[][] newField =
                new Creature[
                        newSettings.rows
                        ][
                        newSettings.columns
                        ];

        settings = newSettings;
        field = newField;

        creatures.clear();
        past.clear();
        future.clear();

        step = 0;
        ecosystemAlive = false;
        endReason = "Начальная популяция ещё не создана";
    }

    private void validateSettings(SimulationSettings s) {
        if (s == null) {
            throw new IllegalArgumentException(
                    "Настройки не могут быть null"
            );
        }

        long capacity = (long) s.rows * s.columns;

        if (s.rows <= 0
                || s.columns <= 0
                || capacity > Integer.MAX_VALUE) {

            throw new IllegalArgumentException(
                    "Недопустимые размеры поля"
            );
        }

        validatePopulation(
                s.predators,
                s.herbivores,
                s.plants,
                capacity
        );

        if (s.maxSteps <= 0) {
            throw new IllegalArgumentException(
                    "Максимальное число шагов должно быть больше нуля"
            );
        }

        if (s.delay < 0) {
            throw new IllegalArgumentException(
                    "Задержка не может быть отрицательной"
            );
        }

        if (s.plantInitialEnergy <= 0
                || s.herbivoreInitialEnergy <= 0
                || s.predatorInitialEnergy <= 0) {

            throw new IllegalArgumentException(
                    "Начальная энергия должна быть больше нуля"
            );
        }

        if (s.plantReproductionThreshold < 2
                || s.herbivoreReproductionThreshold < 2
                || s.predatorReproductionThreshold < 2) {

            throw new IllegalArgumentException(
                    "Порог размножения должен быть не меньше 2"
            );
        }

        if (s.herbivoreViewDistance < 0
                || s.predatorViewDistance < 0) {

            throw new IllegalArgumentException(
                    "Радиус зрения не может быть отрицательным"
            );
        }

        if (s.herbivoreEnergyFromFood < 0
                || s.predatorEnergyFromFood < 0) {

            throw new IllegalArgumentException(
                    "Энергия за пищу не может быть отрицательной"
            );
        }

        validateChance(s.plantReproductionChance);
        validateChance(s.herbivoreReproductionChance);
        validateChance(s.predatorReproductionChance);
    }

    private void validateChance(int chance) {
        if (chance < 0 || chance > 100) {
            throw new IllegalArgumentException(
                    "Шанс размножения должен быть от 0 до 100"
            );
        }
    }

    private void validatePopulation(
            int predatorsCount,
            int herbivoresCount,
            int plantsCount,
            long capacity
    ) {
        if (predatorsCount < 0
                || herbivoresCount < 0
                || plantsCount < 0) {

            throw new IllegalArgumentException(
                    "Численность не может быть отрицательной"
            );
        }

        long total = (long) predatorsCount
                + herbivoresCount
                + plantsCount;

        if (total > capacity) {
            throw new IllegalArgumentException(
                    "Начальная популяция превышает число клеток"
            );
        }
    }

    // ==================== СОЗДАНИЕ ПОПУЛЯЦИИ ====================

    public void createInitialPopulation() {
        createInitialPopulation(
                settings.predators,
                settings.herbivores,
                settings.plants
        );
    }

    public void createInitialPopulation(
            int predatorsCount,
            int herbivoresCount,
            int plantsCount
    ) {
        validateSettings(settings);

        int capacity = getRows() * getColumns();

        validatePopulation(
                predatorsCount,
                herbivoresCount,
                plantsCount,
                capacity
        );

        clear();

        past.clear();
        future.clear();

        int[] positions = new int[capacity];

        for (int i = 0; i < capacity; i++) {
            positions[i] = i;
        }

        for (int i = capacity - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            int temporary = positions[i];
            positions[i] = positions[j];
            positions[j] = temporary;
        }

        int index = 0;

        index = placePopulation(
                positions,
                index,
                plantsCount,
                0,
                settings.plantInitialEnergy
        );

        index = placePopulation(
                positions,
                index,
                herbivoresCount,
                1,
                settings.herbivoreInitialEnergy
        );

        placePopulation(
                positions,
                index,
                predatorsCount,
                2,
                settings.predatorInitialEnergy
        );

        step = 0;

        checkEcosystemState();
    }

    private int placePopulation(
            int[] positions,
            int index,
            int count,
            int type,
            int energy
    ) {
        for (int i = 0; i < count; i++) {
            int position = positions[index++];

            Creature creature = createCreature(
                    type,
                    position / getColumns(),
                    position % getColumns(),
                    energy
            );

            addCreature(creature);
        }

        return index;
    }

    private Creature createCreature(
            int type,
            int row,
            int column,
            int energy
    ) {
        switch (type) {
            case 0:
                return new Plant(row, column, energy);

            case 1:
                return new Herbivore(row, column, energy);

            case 2:
                return new Predator(row, column, energy);

            default:
                throw new IllegalArgumentException(
                        "Неизвестный тип существа"
                );
        }
    }

    private int typeOf(Creature creature) {
        if (creature instanceof Plant) {
            return 0;
        }

        if (creature instanceof Herbivore) {
            return 1;
        }

        if (creature instanceof Predator) {
            return 2;
        }

        throw new IllegalArgumentException(
                "Неподдерживаемый класс существа"
        );
    }

    // ==================== ШАГ СИМУЛЯЦИИ ====================

    public void nextTurn() {
        if (!ecosystemAlive) {
            return;
        }

        saveHistory(past, createState());

        future.clear();

        List<Creature> turnOrder =
                new ArrayList<>(creatures);

        Collections.shuffle(turnOrder, random);

        for (Creature creature : turnOrder) {
            if (!isPresent(creature)) {
                continue;
            }

            // Энергия и поведение рассчитываются в классе вида.
            creature.makeMove(this);

            if (!creature.isAlive()) {
                removeCreature(creature);
            }
        }

        removeDeadCreatures();

        step++;

        checkEcosystemState();
    }

    public void nextTurnOrRedo() {
        if (canRedo()) {
            redo();
        } else {
            nextTurn();
        }
    }

    private void checkEcosystemState() {
        if (countPlants() == 0) {
            ecosystemAlive = false;
            endReason = "Исчезли растения";

        } else if (countHerbivores() == 0) {
            ecosystemAlive = false;
            endReason = "Исчезли травоядные";

        } else if (countPredators() == 0) {
            ecosystemAlive = false;
            endReason = "Исчезли хищники";

        } else if (step >= settings.maxSteps) {
            ecosystemAlive = false;
            endReason = "Достигнуто максимальное число шагов";

        } else {
            ecosystemAlive = true;
            endReason = "";
        }
    }

    // ==================== РАЗМНОЖЕНИЕ ====================

    public boolean tryReproduction(Creature parent) {
        if (!isPresent(parent)) {
            return false;
        }

        int threshold;
        double chance;

        if (parent instanceof Plant) {
            threshold = settings.plantReproductionThreshold;

            // Замедление роста при высокой плотности растений.
            double plantCapacity =
                    (double) getRows() * getColumns() * 0.65;

            double densityFactor = Math.max(
                    0.0,
                    1.0 - countPlants() / plantCapacity
            );

            chance = settings.plantReproductionChance
                    * densityFactor;

        } else if (parent instanceof Herbivore) {
            threshold =
                    settings.herbivoreReproductionThreshold;

            chance =
                    settings.herbivoreReproductionChance;

        } else if (parent instanceof Predator) {
            threshold =
                    settings.predatorReproductionThreshold;

            chance =
                    settings.predatorReproductionChance;

        } else {
            return false;
        }

        if (parent.getEnergy() < threshold) {
            return false;
        }

        if (random.nextDouble() * 100.0 >= chance) {
            return false;
        }

        List<int[]> emptyCells = new ArrayList<>();

        for (int[] direction : DIRECTIONS) {
            int row = parent.getRow() + direction[0];
            int column = parent.getColumn() + direction[1];

            if (isInside(row, column)
                    && field[row][column] == null) {

                emptyCells.add(new int[]{row, column});
            }
        }

        if (emptyCells.isEmpty()) {
            return false;
        }

        int childEnergy = parent.getEnergy() / 2;

        if (childEnergy <= 0) {
            return false;
        }

        int[] position = emptyCells.get(
                random.nextInt(emptyCells.size())
        );

        Creature child = createCreature(
                typeOf(parent),
                position[0],
                position[1],
                childEnergy
        );

        parent.loseEnergy(childEnergy);

        addCreature(child);

        return true;
    }

    // ==================== ПОВЕДЕНИЕ ЖИВОТНЫХ ====================

    public void moveHerbivore(Herbivore herbivore) {
        if (!isPresent(herbivore)) {
            return;
        }

        // Побег всегда важнее питания.
        if (findNearestPredator(herbivore) != null) {
            flee(herbivore);
            return;
        }

        boolean moved = moveToReachableFood(
                herbivore,
                Plant.class,
                settings.herbivoreViewDistance
        );

        if (!moved) {
            moveRandomly(herbivore);
        }
    }

    public void movePredator(Predator predator) {
        if (!isPresent(predator)) {
            return;
        }

        boolean moved = moveToReachableFood(
                predator,
                Herbivore.class,
                settings.predatorViewDistance
        );

        if (!moved) {
            moveRandomly(predator);
        }
    }

    /*
     * Побег только на пустую соседнюю клетку.
     * Оценивается расстояние до ближайшего
     * из всех замеченных хищников.
     */
    private void flee(Herbivore herbivore) {
        List<Creature> threats = new ArrayList<>();

        for (Creature creature : creatures) {
            if (creature instanceof Predator
                    && creature.isAlive()
                    && distance(herbivore, creature)
                    <= settings.herbivoreViewDistance) {

                threats.add(creature);
            }
        }

        if (threats.isEmpty()) {
            return;
        }

        int bestSafety = safety(
                herbivore.getRow(),
                herbivore.getColumn(),
                threats
        );

        List<int[]> bestMoves = new ArrayList<>();

        for (int[] direction : DIRECTIONS) {
            int row = herbivore.getRow() + direction[0];
            int column = herbivore.getColumn() + direction[1];

            if (!isInside(row, column)
                    || field[row][column] != null) {
                continue;
            }

            int score = safety(row, column, threats);

            if (score > bestSafety) {
                bestSafety = score;
                bestMoves.clear();
                bestMoves.add(new int[]{row, column});

            } else if (score == bestSafety) {
                bestMoves.add(new int[]{row, column});
            }
        }

        if (!bestMoves.isEmpty()) {
            int[] destination = bestMoves.get(
                    random.nextInt(bestMoves.size())
            );

            moveCreature(
                    herbivore,
                    destination[0],
                    destination[1]
            );
        }
    }

    private int safety(
            int row,
            int column,
            List<Creature> threats
    ) {
        int minimum = Integer.MAX_VALUE;

        for (Creature threat : threats) {
            int current =
                    Math.abs(row - threat.getRow())
                            + Math.abs(
                            column - threat.getColumn()
                    );

            minimum = Math.min(minimum, current);
        }

        return minimum;
    }

    // ==================== ПОИСК ПУТИ ====================

    /*
     * Находит ближайшую по длине доступного пути пищу.
     * Цель должна находиться в радиусе зрения по координатам.
     */
    private boolean moveToReachableFood(
            Creature creature,
            Class<?> foodType,
            int radius
    ) {
        return findPathAndMove(
                creature,
                null,
                foodType,
                radius
        );
    }

    /*
     * Метод оставлен для совместимости с вызовами
     * moveTowards(creature, target).
     */
    private boolean moveTowards(
            Creature creature,
            Creature target
    ) {
        if (!isPresent(target)
                || !canEat(creature, target)) {
            return false;
        }

        return findPathAndMove(
                creature,
                target,
                null,
                0
        );
    }

    private boolean findPathAndMove(
            Creature creature,
            Creature fixedTarget,
            Class<?> foodType,
            int radius
    ) {
        if (!isPresent(creature)
                || creature instanceof Plant) {
            return false;
        }

        int columns = getColumns();

        int start =
                creature.getRow() * columns
                        + creature.getColumn();

        int[] previous =
                new int[getRows() * columns];

        Arrays.fill(previous, -1);

        Deque<Integer> queue = new ArrayDeque<>();

        previous[start] = start;
        queue.addLast(start);

        int goal = -1;

        while (!queue.isEmpty() && goal == -1) {
            int current = queue.removeFirst();

            int row = current / columns;
            int column = current % columns;

            for (int[] direction : DIRECTIONS) {
                int nextRow = row + direction[0];
                int nextColumn = column + direction[1];

                if (!isInside(nextRow, nextColumn)) {
                    continue;
                }

                int next = nextRow * columns + nextColumn;

                if (previous[next] != -1) {
                    continue;
                }

                Creature occupant =
                        field[nextRow][nextColumn];

                boolean targetFound;

                if (fixedTarget != null) {
                    targetFound =
                            occupant == fixedTarget;

                } else {
                    int distanceFromStart =
                            Math.abs(
                                    nextRow - creature.getRow()
                            )
                                    + Math.abs(
                                    nextColumn
                                            - creature.getColumn()
                            );

                    targetFound =
                            occupant != null
                                    && foodType.isInstance(occupant)
                                    && distanceFromStart <= radius;
                }

                if (targetFound
                        && canEat(creature, occupant)) {

                    previous[next] = current;
                    goal = next;
                    break;
                }

                // Занятые клетки нельзя использовать как проход.
                if (occupant != null) {
                    continue;
                }

                previous[next] = current;
                queue.addLast(next);
            }
        }

        if (goal == -1) {
            return false;
        }

        int firstStep = goal;

        while (previous[firstStep] != start) {
            firstStep = previous[firstStep];
        }

        return moveCreature(
                creature,
                firstStep / columns,
                firstStep % columns
        );
    }

    // ==================== ПЕРЕМЕЩЕНИЕ И ПИТАНИЕ ====================

    public boolean moveCreature(
            Creature creature,
            int newRow,
            int newColumn
    ) {
        if (!isPresent(creature)
                || creature instanceof Plant
                || !isInside(newRow, newColumn)) {
            return false;
        }

        int moveDistance =
                Math.abs(newRow - creature.getRow())
                        + Math.abs(
                        newColumn - creature.getColumn()
                );

        // Только один ортогональный переход.
        if (moveDistance != 1) {
            return false;
        }

        Creature occupant = field[newRow][newColumn];

        if (occupant != null) {
            if (!canEat(creature, occupant)) {
                return false;
            }

            int foodEnergy =
                    creature instanceof Herbivore
                            ? settings.herbivoreEnergyFromFood
                            : settings.predatorEnergyFromFood;

            creature.changeEnergy(foodEnergy);

            removeCreature(occupant);
        }

        field[creature.getRow()][creature.getColumn()] = null;

        creature.setPosition(newRow, newColumn);

        field[newRow][newColumn] = creature;

        return true;
    }

    private boolean canEat(
            Creature eater,
            Creature victim
    ) {
        return eater != null
                && eater.isAlive()
                && victim != null
                && victim.isAlive()
                && ((eater instanceof Herbivore
                && victim instanceof Plant)
                || (eater instanceof Predator
                && victim instanceof Herbivore));
    }

    public void moveRandomly(Creature creature) {
        if (!isPresent(creature)
                || creature instanceof Plant) {
            return;
        }

        List<int[]> moves = new ArrayList<>();

        for (int[] direction : DIRECTIONS) {
            int row = creature.getRow() + direction[0];
            int column = creature.getColumn() + direction[1];

            if (isInside(row, column)
                    && field[row][column] == null) {

                moves.add(new int[]{row, column});
            }
        }

        if (!moves.isEmpty()) {
            int[] destination = moves.get(
                    random.nextInt(moves.size())
            );

            moveCreature(
                    creature,
                    destination[0],
                    destination[1]
            );
        }
    }

    // ==================== ПОИСК СУЩЕСТВ ====================

    public Creature findNearestPredator(Herbivore source) {
        return findNearest(
                source,
                Predator.class,
                settings.herbivoreViewDistance
        );
    }

    public Creature findNearestPlant(Herbivore source) {
        return findNearest(
                source,
                Plant.class,
                settings.herbivoreViewDistance
        );
    }

    public Creature findNearestHerbivore(Predator source) {
        return findNearest(
                source,
                Herbivore.class,
                settings.predatorViewDistance
        );
    }

    private Creature findNearest(
            Creature source,
            Class<?> type,
            int radius
    ) {
        if (!isPresent(source)) {
            return null;
        }

        Creature nearest = null;
        int minimum = Integer.MAX_VALUE;

        for (Creature candidate : creatures) {
            if (candidate == source
                    || !candidate.isAlive()
                    || !type.isInstance(candidate)) {
                continue;
            }

            int current = distance(source, candidate);

            if (current <= radius && current < minimum) {
                minimum = current;
                nearest = candidate;
            }
        }

        return nearest;
    }

    private int distance(
            Creature first,
            Creature second
    ) {
        return Math.abs(
                first.getRow() - second.getRow()
        )
                + Math.abs(
                first.getColumn() - second.getColumn()
        );
    }

    private boolean isInside(
            int row,
            int column
    ) {
        return row >= 0
                && row < getRows()
                && column >= 0
                && column < getColumns();
    }

    private boolean isPresent(Creature creature) {
        return creature != null
                && creature.isAlive()
                && isInside(
                creature.getRow(),
                creature.getColumn()
        )
                && field[
                creature.getRow()
                ][
                creature.getColumn()
                ] == creature;
    }

    // ==================== ДОБАВЛЕНИЕ И УДАЛЕНИЕ ====================

    private void addCreature(Creature creature) {
        int row = creature.getRow();
        int column = creature.getColumn();

        if (!isInside(row, column)
                || field[row][column] != null) {

            throw new IllegalStateException(
                    "Нельзя разместить существо в этой клетке"
            );
        }

        field[row][column] = creature;
        creatures.add(creature);
    }

    private void removeCreature(Creature creature) {
        if (creature == null) {
            return;
        }

        int row = creature.getRow();
        int column = creature.getColumn();

        if (isInside(row, column)
                && field[row][column] == creature) {

            field[row][column] = null;
        }

        creatures.remove(creature);
    }

    private void removeDeadCreatures() {
        Iterator<Creature> iterator =
                creatures.iterator();

        while (iterator.hasNext()) {
            Creature creature = iterator.next();

            if (!creature.isAlive()) {
                int row = creature.getRow();
                int column = creature.getColumn();

                if (field[row][column] == creature) {
                    field[row][column] = null;
                }

                iterator.remove();
            }
        }
    }

    private void clear() {
        creatures.clear();

        for (Creature[] row : field) {
            Arrays.fill(row, null);
        }
    }

    // ==================== ИСТОРИЯ ====================

    public boolean canUndo() {
        return !past.isEmpty();
    }

    public boolean canRedo() {
        return !future.isEmpty();
    }

    public boolean undo() {
        if (!canUndo()) {
            return false;
        }

        saveHistory(future, createState());

        restoreState(past.pop());

        return true;
    }

    public boolean redo() {
        if (!canRedo()) {
            return false;
        }

        saveHistory(past, createState());

        restoreState(future.pop());

        return true;
    }

    private void saveHistory(
            Deque<WorldState> history,
            WorldState state
    ) {
        history.push(state);

        while (history.size() > HISTORY_LIMIT) {
            history.removeLast();
        }
    }

    private WorldState createState() {
        int[] data = new int[creatures.size() * 4];

        int index = 0;

        for (Creature creature : creatures) {
            data[index++] = typeOf(creature);
            data[index++] = creature.getRow();
            data[index++] = creature.getColumn();
            data[index++] = creature.getEnergy();
        }

        return new WorldState(
                data,
                step,
                ecosystemAlive,
                endReason
        );
    }

    private void restoreState(WorldState state) {
        clear();

        for (int i = 0; i < state.data.length; i += 4) {
            Creature creature = createCreature(
                    state.data[i],
                    state.data[i + 1],
                    state.data[i + 2],
                    state.data[i + 3]
            );

            addCreature(creature);
        }

        step = state.step;
        ecosystemAlive = state.ecosystemAlive;
        endReason = state.endReason;
    }

    private static class WorldState {

        private final int[] data;
        private final int step;
        private final boolean ecosystemAlive;
        private final String endReason;

        private WorldState(
                int[] data,
                int step,
                boolean ecosystemAlive,
                String endReason
        ) {
            this.data = data;
            this.step = step;
            this.ecosystemAlive = ecosystemAlive;
            this.endReason = endReason;
        }
    }

    // ==================== СТАТИСТИКА И ГЕТТЕРЫ ====================

    public Creature getCreature(
            int row,
            int column
    ) {
        if (!isInside(row, column)) {
            return null;
        }

        return field[row][column];
    }

    public List<Creature> getCreatures() {
        return new ArrayList<>(creatures);
    }

    public int countPlants() {
        return countType(Plant.class);
    }

    public int countHerbivores() {
        return countType(Herbivore.class);
    }

    public int countPredators() {
        return countType(Predator.class);
    }

    private int countType(Class<?> type) {
        int count = 0;

        for (Creature creature : creatures) {
            if (creature.isAlive()
                    && type.isInstance(creature)) {
                count++;
            }
        }

        return count;
    }

    public SimulationSettings getSettings() {
        return settings;
    }

    public int getRows() {
        return settings.rows;
    }

    public int getColumns() {
        return settings.columns;
    }

    public int getStep() {
        return step;
    }

    public int getMaxSteps() {
        return settings.maxSteps;
    }

    public int getDelay() {
        return settings.delay;
    }

    public boolean isEcosystemAlive() {
        return ecosystemAlive;
    }

    public String getEndReason() {
        return endReason;
    }
}