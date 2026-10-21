import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class Main extends JFrame {

    private final World world = new World();

    private final SettingsPanel settingsPanel =
            new SettingsPanel();

    private final JPanel fieldPanel = new JPanel();

    private final JLabel statisticsLabel = new JLabel();

    private JButton[][] cells;

    private final JButton startButton = new JButton("Старт");
    private final JButton pauseButton = new JButton("Пауза");
    private final JButton stepButton = new JButton("Шаг");
    private final JButton undoButton = new JButton("← Назад");
    private final JButton redoButton = new JButton("Вперёд →");

    private final Timer timer = new Timer(
            100,
            event -> performStep()
    );

    public Main() {
        super("Живая экосистема");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        createInterface();
        applySettings();

        setSize(1250, 850);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
    }

    private void createInterface() {
        JLabel title = new JLabel(
                "ИСКУССТВЕННАЯ ЖИЗНЬ"
        );

        title.setFont(new Font(
                "Arial",
                Font.BOLD,
                22
        ));

        title.setBorder(
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );

        add(title, BorderLayout.NORTH);

        JButton applyButton = new JButton(
                "Применить и перезапустить"
        );

        applyButton.addActionListener(
                event -> applySettings()
        );

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        JScrollPane settingsScroll =
                new JScrollPane(settingsPanel);

        settingsScroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        leftPanel.add(settingsScroll, BorderLayout.CENTER);
        leftPanel.add(applyButton, BorderLayout.SOUTH);

        JScrollPane fieldScroll =
                new JScrollPane(fieldPanel);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                fieldScroll
        );

        splitPane.setDividerLocation(340);
        splitPane.setResizeWeight(0.0);

        add(splitPane, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout());

        controls.add(startButton);
        controls.add(pauseButton);
        controls.add(stepButton);
        controls.add(undoButton);
        controls.add(redoButton);

        JPanel bottom = new JPanel(new BorderLayout());

        bottom.add(controls, BorderLayout.NORTH);
        bottom.add(statisticsLabel, BorderLayout.SOUTH);

        bottom.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 10)
        );

        add(bottom, BorderLayout.SOUTH);

        startButton.addActionListener(event -> {
            if (world.isEcosystemAlive() || world.canRedo()) {
                timer.start();
                updateButtons();
            }
        });

        pauseButton.addActionListener(event -> {
            timer.stop();
            updateInterface();
        });

        stepButton.addActionListener(event -> {
            timer.stop();
            performStep();
        });

        undoButton.addActionListener(event -> {
            timer.stop();
            world.undo();
            updateInterface();
        });

        redoButton.addActionListener(event -> {
            timer.stop();
            world.redo();
            updateInterface();
        });
    }

    private void applySettings() {
        timer.stop();

        try {
            // Подтверждаем число, введённое с клавиатуры.
            commitSpinners(settingsPanel);

            SimulationSettings settings =
                    settingsPanel.getSettings();

            world.applySettings(settings);
            world.createInitialPopulation();

            timer.setDelay(settings.delay);
            timer.setInitialDelay(settings.delay);

            createField();
            updateInterface();

        } catch (ParseException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage() == null
                            ? "Проверьте введённые значения."
                            : ex.getMessage(),
                    "Ошибка настроек",
                    JOptionPane.ERROR_MESSAGE
            );

            updateInterface();
        }
    }

    private void commitSpinners(Container container)
            throws ParseException {

        for (Component component : container.getComponents()) {
            if (component instanceof JSpinner) {
                ((JSpinner) component).commitEdit();
            } else if (component instanceof Container) {
                commitSpinners((Container) component);
            }
        }
    }

    private void createField() {
        fieldPanel.removeAll();

        int rows = world.getRows();
        int columns = world.getColumns();

        fieldPanel.setLayout(
                new GridLayout(rows, columns, 1, 1)
        );

        cells = new JButton[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                JButton cell = new JButton();

                cell.setPreferredSize(new Dimension(16, 16));
                cell.setMargin(new Insets(0, 0, 0, 0));
                cell.setFont(new Font(
                        "Arial",
                        Font.PLAIN,
                        10
                ));

                cell.setFocusPainted(false);
                cell.setBorderPainted(false);
                cell.setOpaque(true);

                cells[row][column] = cell;
                fieldPanel.add(cell);
            }
        }

        fieldPanel.revalidate();
        fieldPanel.repaint();
    }

    private void performStep() {
        if (world.isEcosystemAlive() || world.canRedo()) {
            world.nextTurnOrRedo();
        }

        if (!world.isEcosystemAlive() && !world.canRedo()) {
            timer.stop();
        }

        updateInterface();
    }

    private void updateInterface() {
        if (cells != null) {
            for (int row = 0; row < cells.length; row++) {
                for (int column = 0;
                     column < cells[row].length;
                     column++) {

                    Creature creature =
                            world.getCreature(row, column);

                    JButton cell = cells[row][column];

                    if (creature == null) {
                        cell.setText("");
                        cell.setBackground(
                                new Color(225, 225, 225)
                        );
                        cell.setToolTipText(null);
                    } else {
                        cell.setText(creature.getSymbol());
                        cell.setBackground(creature.getColor());
                        cell.setForeground(Color.BLACK);

                        cell.setToolTipText(
                                creature.getClass().getSimpleName()
                                        + " | Энергия: "
                                        + creature.getEnergy()
                                        + " | Координаты: "
                                        + row + ", " + column
                        );
                    }
                }
            }
        }

        String status;

        if (!world.isEcosystemAlive()) {
            status = world.getEndReason();
        } else if (timer.isRunning()) {
            status = "Выполняется";
        } else {
            status = "Пауза";
        }

        statisticsLabel.setText(
                "Шаг: " + world.getStep()
                        + " / " + world.getMaxSteps()
                        + " | Растения: " + world.countPlants()
                        + " | Травоядные: " + world.countHerbivores()
                        + " | Хищники: " + world.countPredators()
                        + " | " + status
        );

        updateButtons();
    }

    private void updateButtons() {
        boolean canAdvance =
                world.isEcosystemAlive() || world.canRedo();

        startButton.setEnabled(
                canAdvance && !timer.isRunning()
        );

        pauseButton.setEnabled(timer.isRunning());
        stepButton.setEnabled(canAdvance);
        undoButton.setEnabled(world.canUndo());
        redoButton.setEnabled(world.canRedo());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}