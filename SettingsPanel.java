import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class SettingsPanel extends JPanel {

    private final JSpinner rowsSpinner =
            spinner(
                    50,
                    5,
                    200,
                    1
            );

    private final JSpinner columnsSpinner =
            spinner(
                    50,
                    5,
                    200,
                    1
            );

    private final JSpinner maxStepsSpinner =
            spinner(
                    20_000,
                    1,
                    1_000_000,
                    100
            );

    private final JSpinner delaySpinner =
            spinner(
                    100,
                    10,
                    10_000,
                    10
            );

    private final JSpinner plantsSpinner =
            spinner(
                    1200,
                    1,
                    20_000,
                    1
            );

    private final JSpinner herbivoresSpinner =
            spinner(
                    12,
                    1,
                    20_000,
                    1
            );

    private final JSpinner predatorsSpinner =
            spinner(
                    12,
                    1,
                    20_000,
                    1
            );

    private final JSpinner plantEnergySpinner =
            spinner(
                    3,
                    1,
                    100_000,
                    1
            );

    private final JSpinner herbivoreEnergySpinner =
            spinner(
                    50,
                    1,
                    100_000,
                    1
            );

    private final JSpinner predatorEnergySpinner =
            spinner(
                    80,
                    1,
                    100_000,
                    1
            );

    private final JSpinner plantReproductionSpinner =
            spinner(
                    8,
                    2,
                    100_000,
                    1
            );

    private final JSpinner herbivoreReproductionSpinner =
            spinner(
                    100,
                    2,
                    100_000,
                    1
            );

    private final JSpinner predatorReproductionSpinner =
            spinner(
                    160,
                    2,
                    100_000,
                    1
            );

    private final JSpinner plantChangeSpinner =
            spinner(
                    1,
                    0,
                    100,
                    1
            );

    private final JSpinner herbivoreChangeSpinner =
            spinner(
                    1,
                    0,
                    100,
                    1
            );

    private final JSpinner predatorChangeSpinner =
            spinner(
                    1,
                    0,
                    100,
                    1
            );

    private final JSpinner plantChanceSpinner =
            spinner(
                    100,
                    1,
                    100,
                    1
            );

    private final JSpinner herbivoreChanceSpinner =
            spinner(
                    100,
                    1,
                    100,
                    1
            );

    private final JSpinner predatorChanceSpinner =
            spinner(
                    100,
                    1,
                    100,
                    1
            );

    private final JSpinner herbivoreViewSpinner =
            spinner(
                    5,
                    1,
                    50,
                    1
            );

    private final JSpinner predatorViewSpinner =
            spinner(
                    5,
                    1,
                    50,
                    1
            );

    public SettingsPanel() {
        setBorder(
                BorderFactory.createTitledBorder(
                        "Настройки симуляции"
                )
        );

        setLayout(
                new GridBagLayout()
        );

        addSettings();
    }

    private void addSettings() {
        int row = 0;

        addSection(
                "Симуляция",
                row++
        );

        addRow(
                "Высота поля",
                rowsSpinner,
                row++
        );

        addRow(
                "Ширина поля",
                columnsSpinner,
                row++
        );

        addRow(
                "Макс. итераций",
                maxStepsSpinner,
                row++
        );

        addRow(
                "Задержка кадра, мс",
                delaySpinner,
                row++
        );

        addSection(
                "Начальная популяция",
                row++
        );

        addRow(
                "Растения",
                plantsSpinner,
                row++
        );

        addRow(
                "Травоядные",
                herbivoresSpinner,
                row++
        );

        addRow(
                "Хищники",
                predatorsSpinner,
                row++
        );

        addSection(
                "Начальная энергия",
                row++
        );

        addRow(
                "Растение",
                plantEnergySpinner,
                row++
        );

        addRow(
                "Травоядное",
                herbivoreEnergySpinner,
                row++
        );

        addRow(
                "Хищник",
                predatorEnergySpinner,
                row++
        );

        addSection(
                "Порог размножения",
                row++
        );

        addRow(
                "Растение",
                plantReproductionSpinner,
                row++
        );

        addRow(
                "Травоядное",
                herbivoreReproductionSpinner,
                row++
        );

        addRow(
                "Хищник",
                predatorReproductionSpinner,
                row++
        );

        addSection(
                "Изменение энергии за шаг",
                row++
        );

        addRow(
                "Растение",
                plantChangeSpinner,
                row++
        );

        addRow(
                "Травоядное",
                herbivoreChangeSpinner,
                row++
        );

        addRow(
                "Хищник",
                predatorChangeSpinner,
                row++
        );

        addSection(
                "Шанс размножения, %",
                row++
        );

        addRow(
                "Растение",
                plantChanceSpinner,
                row++
        );

        addRow(
                "Травоядное",
                herbivoreChanceSpinner,
                row++
        );

        addRow(
                "Хищник",
                predatorChanceSpinner,
                row++
        );

        addSection(
                "Радиус зрения",
                row++
        );

        addRow(
                "Травоядное",
                herbivoreViewSpinner,
                row++
        );

        addRow(
                "Хищник",
                predatorViewSpinner,
                row
        );
    }

    private void addSection(
            String title,
            int row
    ) {
        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        constraints.anchor =
                GridBagConstraints.CENTER;
        constraints.insets =
                new Insets(
                        8,
                        2,
                        3,
                        2
                );

        add(
                new JLabel(
                        "<html><b>"
                                + title
                                + "</b></html>"
                ),
                constraints
        );
    }

    private void addRow(
            String label,
            JSpinner spinner,
            int row
    ) {
        GridBagConstraints labelConstraints =
                new GridBagConstraints();

        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor =
                GridBagConstraints.WEST;
        labelConstraints.insets =
                new Insets(
                        2,
                        5,
                        2,
                        15
                );

        GridBagConstraints spinnerConstraints =
                new GridBagConstraints();

        spinnerConstraints.gridx = 1;
        spinnerConstraints.gridy = row;
        spinnerConstraints.anchor =
                GridBagConstraints.EAST;
        spinnerConstraints.insets =
                new Insets(
                        2,
                        5,
                        2,
                        5
                );

        add(
                new JLabel(label),
                labelConstraints
        );

        add(
                spinner,
                spinnerConstraints
        );
    }

    private JSpinner spinner(
            int value,
            int minimum,
            int maximum,
            int step
    ) {
        JSpinner spinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                value,
                                minimum,
                                maximum,
                                step
                        )
                );

        spinner.setPreferredSize(
                new java.awt.Dimension(
                        75,
                        25
                )
        );

        return spinner;
    }

    public SimulationSettings getSettings() {
        SimulationSettings settings =
                new SimulationSettings();

        settings.rows =
                getInt(rowsSpinner);

        settings.columns =
                getInt(columnsSpinner);

        settings.maxSteps =
                getInt(maxStepsSpinner);

        settings.delay =
                getInt(delaySpinner);

        settings.plants =
                getInt(plantsSpinner);

        settings.herbivores =
                getInt(herbivoresSpinner);

        settings.predators =
                getInt(predatorsSpinner);

        settings.plantInitialEnergy =
                getInt(plantEnergySpinner);

        settings.herbivoreInitialEnergy =
                getInt(herbivoreEnergySpinner);

        settings.predatorInitialEnergy =
                getInt(predatorEnergySpinner);

        settings.plantReproductionThreshold =
                getInt(plantReproductionSpinner);

        settings.herbivoreReproductionThreshold =
                getInt(herbivoreReproductionSpinner);

        settings.predatorReproductionThreshold =
                getInt(predatorReproductionSpinner);

        settings.plantEnergyChange =
                getInt(plantChangeSpinner);

        settings.herbivoreEnergyChange =
                -getInt(herbivoreChangeSpinner);

        settings.predatorEnergyChange =
                -getInt(predatorChangeSpinner);

        settings.plantReproductionChance =
                getInt(plantChanceSpinner);

        settings.herbivoreReproductionChance =
                getInt(herbivoreChanceSpinner);

        settings.predatorReproductionChance =
                getInt(predatorChanceSpinner);

        settings.herbivoreViewDistance =
                getInt(herbivoreViewSpinner);

        settings.predatorViewDistance =
                getInt(predatorViewSpinner);

        return settings;
    }

    private int getInt(
            JSpinner spinner
    ) {
        return (Integer)
                spinner.getValue();
    }
}
