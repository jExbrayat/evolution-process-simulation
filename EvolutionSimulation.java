import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.List;
import java.util.Random;

class EvolutionaryMaths {

    // Constructor
    public EvolutionaryMaths(int genotype_dimensionality, double fitness_landscape_kurtosis) {
        this.genotype_dimensionality = genotype_dimensionality;
        this.fitness_landscape_kurtosis = fitness_landscape_kurtosis;
        this.optimum_genotype = new double[genotype_dimensionality];

        // Initialize optimum_genotype with 0.5 values
        for (int i = 0; i < genotype_dimensionality; i++) {
            optimum_genotype[i] = 0.5;
        }
    }

    // Private class members
    private int genotype_dimensionality;
    private double fitness_landscape_kurtosis;
    private double[] optimum_genotype;

    /**
     * @brief Normalize elements of input vector such that sum(normalized_probas) =
     *        1
     * @param probas Vector of probabilities
     * @return
     */
    public static double[] normalizeProbas(double[] probas) {
        double[] normalizedProbas = new double[probas.length];

        double sum = 0;
        for (double proba : probas) {
            sum += proba;
        }

        for (int i = 0; i < probas.length; i++) {
            double normalized = probas[i] / sum;
            normalizedProbas[i] = normalized;
        }

        return normalizedProbas;
    }

    /**
     * @brief Compute fitness for a individual's genotype
     * @return Fitness score
     */
    public double Fitness(double[] genotype) {
        double euclidian_dist = 0;
        for (int i = 0; i < genotype.length; i++) {
            euclidian_dist += Math.pow(genotype[i] - optimum_genotype[i], 2);
        }
        euclidian_dist = Math.sqrt(euclidian_dist);

        double fitness = Math.exp(-fitness_landscape_kurtosis * Math.pow(euclidian_dist, 2));

        return fitness;
    }

    /**
     * @brief Mutate an individual's genotype
     * @param genotype
     * @return Mutated genotype
     */
    public static double[] mutate(double[] genotype, double mutation_rate) {
        // Init Random object for generating gaussian variables
        Random rand = new Random();
        for (int i = 0; i < genotype.length; i++) {
            genotype[i] += rand.nextGaussian() * mutation_rate;
        }
        return genotype;
    }

    /**
     * 
     * @param total_nb_individuals
     * @return The dying probability of an individual, which is the same for everyone
     * 
     */
    public static double computeDyingProbability(int total_nb_individuals) {
        return 1 / Math.pow(total_nb_individuals, 2);
    }

    /**
     * Compute the reproduction probability of an individual given by:
     * alpha * fitness + beta * age + randn(0, 1) * epsilon
     * @param fitness Fitness score of the individual
     * @param age Float, age of the individual 
     * @param alpha 
     * @param beta
     * @param stochasticity_std
     * @return Non normalized reproduction probability
     * 
     */
    public static double computeReproductionProbability(double fitness, double age, double alpha, double beta,
            double stochasticity_std) {
        // Init Random object for generating gaussian variables
        Random rand = new Random();
        double epsilon = rand.nextGaussian();
        double reprod_proba = alpha * fitness + beta * age + stochasticity_std * epsilon;
        return reprod_proba;
    }
}

class Individual {
    private int type;
    private double[] phenotype;
    private float fitness;

    public Individual(int traitCount) {
        phenotype = new double[traitCount];
        Random random = new Random();
        for (int i = 0; i < traitCount; i++) {
            phenotype[i] = random.nextDouble(); // Random phenotype value between 0 and 1
        }
    }

    public void randomizePhenotype() {
        Random random = new Random();
        for (int i = 0; i < phenotype.length; i++) {
            phenotype[i] = random.nextDouble(); // Random phenotype value between 0 and 1
        }
    }

    public Color getColor() {
        int colorValue = (int) (phenotype[0] * 255);
        return new Color(colorValue, colorValue, 255 - colorValue);
    }
}

class Population extends JPanel {
    private final int gridSize; // Number of cells in one dimension
    private final int cellSize; // Size of each cell in pixels
    private final Individual[][] grid;

    public Population(int gridSize, int cellSize, int traitCount) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.grid = new Individual[gridSize][gridSize];
        initializeGrid(traitCount);
        setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
    }

    private void initializeGrid(int traitCount) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = new Individual(traitCount);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int x = i * cellSize;
                int y = j * cellSize;
                g.setColor(grid[i][j].getColor());
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    public void updateGrid() {
        // Randomize the phenotype of a random individual to simulate change
        Random random = new Random();
        int x = random.nextInt(gridSize);
        int y = random.nextInt(gridSize);
        grid[x][y].randomizePhenotype();
        repaint(); // Repaint the panel to reflect updates
    }
}

//
public class EvolutionSimulation {
    public static void main(String[] args) {
        int gridSize = 50; // Fixed number of cells in one dimension
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int cellSize = Math.min(screenSize.width, screenSize.height) / gridSize; // Calculate cell size to fit the
                                                                                 // screen

        JFrame frame = new JFrame("Evolution Simulation");
        Population pop = new Population(gridSize, cellSize, 3);

        frame.add(pop);
        frame.pack(); // Automatically sizes the frame to fit the panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);

        int n = 5000; // Set the number of random updates you want
        for (int i = 0; i < n; i++) {
            // Sleep for a brief time to simulate updates over time
            try {
                Thread.sleep(100); // Delay 100 ms between updates
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pop.updateGrid(); // Randomly update the grid
        }
    }
}
