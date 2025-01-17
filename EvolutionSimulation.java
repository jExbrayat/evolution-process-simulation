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
            genotype[i] += rand.nextGaussian() * mutation_rate; ////////////// TODO : Thresholding
        }
        return genotype;
    }

    public static double computeDyingProbability(int total_nb_individuals) {
        return 1 / Math.pow(total_nb_individuals, 2);
    }

    public static double computeReproductionProbability(double fitness, double age, double alpha, double beta,
            double stochasticity_std) {
        // Init Random object for generating gaussian variables
        Random rand = new Random();
        double epsilon = rand.nextGaussian();
        double reprod_proba = alpha * fitness + beta * age + stochasticity_std * epsilon;
        return reprod_proba;
    }

    // Méthode pour effectuer une sélection pondérée
    public static int weightedRandomSelection(double[] probabilities) {
        Random random = new Random();
        double rand = random.nextDouble(); // Génère un nombre entre 0 et 1
        double cumulative = 0.0;

        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (rand < cumulative) {
                return i; // Retourne l'index sélectionné
            }
        }
        // Par sécurité (ne devrait pas se produire si les probabilités sont normalisées)
        return probabilities.length - 1;
    }
}

class Individual {
    private int type;
    private double[] phenotype;
    private double fitness;
    private Color color;

    public Individual(int traitCount, int type) {
        this.phenotype = new double[traitCount];
        this.type = type;

        if(type == 0){
            this.color = new Color(0, 0, 147);
            for(int i = 0; i < traitCount;i++){
                this.phenotype[i] = 0.3;
            }
        }
        if(type == 1){
            this.color = new Color(147, 147, 0);
            for(int i = 0; i < traitCount;i++){
                this.phenotype[i] = 0.3;
            }
        }
    }


    public Color getColor() {
        return this.color;
    }

    public void setColor(Color new_color) {
        this.color = new_color;
    }

    public double[] getPhenotype() {
        return this.phenotype;
    }

    public void setPhenotype(double[] new_phenotype) {
        this.phenotype = new_phenotype;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int new_type) {
        this.type = new_type;
    }

    public double getFitness() {
        return this.fitness;
    }

    public void setFitness(double new_fit) {
        this.fitness = new_fit;
    }
}

class Population extends JPanel {
    private final int gridSize; // Number of cells in one dimension
    private final int cellSize; // Size of each cell in pixels
    private final Individual[][] grid;
    private final int d;
    private EvolutionaryMaths math;

    public Population(int gridSize, int cellSize, int traitCount) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.grid = new Individual[gridSize][gridSize];
        this.d = traitCount;
        this.math = new EvolutionaryMaths(this.d,1);
        initializeGrid(traitCount);
        setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
    }

    private void initializeGrid(int traitCount) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if(i % 2 == 0){
                    grid[i][j] = new Individual(traitCount,0);
                }
                else{
                    grid[i][j] = new Individual(traitCount,1);
                }                
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
        // Mutation aléatoire pour tout le monde.
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j].setPhenotype(this.math.mutate(grid[i][j].getPhenotype(),0.02));
                grid[i][j].setFitness(this.math.Fitness(grid[i][j].getPhenotype()));
            }
        }
        // Calcul des probas
        int dim_vector = gridSize*gridSize;
        double[] proba_repro = new double[dim_vector];
        double[] proba_death = new double[dim_vector];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                proba_repro[i*gridSize + j] = math.computeReproductionProbability(grid[i][j].getFitness(), 1, 1, 1,0.0001);
                proba_death[i*gridSize + j] = math.computeDyingProbability(dim_vector);
            }
        }


        // Normalisation
        double[] proba_repro_normed = math.normalizeProbas(proba_repro);
        System.out.println(proba_repro_normed);
        // Tirage aléatoire d'un individu
        int death = this.math.weightedRandomSelection(proba_death);
        int born = this.math.weightedRandomSelection(proba_repro_normed);

        // Update matrix
        int i_death = death / gridSize;
        int j_death = death % gridSize;

        int i_born = born / gridSize;
        int j_born = born % gridSize;

        System.out.println(i_death);
        System.out.println(j_death);

        grid[i_death][j_death].setColor(grid[i_born][j_born].getColor());
        grid[i_death][j_death].setPhenotype(grid[i_born][j_born].getPhenotype());
        grid[i_death][j_death].setType(grid[i_born][j_born].getType()); 

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
