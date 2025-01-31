import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

class EvolutionaryMaths {

    public EvolutionaryMaths(int genotype_dimensionality, double fitness_landscape_kurtosis) {
        this.genotype_dimensionality = genotype_dimensionality;
        this.fitness_landscape_kurtosis = fitness_landscape_kurtosis;
        this.optimum_genotype = new double[genotype_dimensionality];

        for (int i = 0; i < genotype_dimensionality; i++) {
            optimum_genotype[i] = 0.5;
        }
    }

    private int genotype_dimensionality;
    private double fitness_landscape_kurtosis;
    private double[] optimum_genotype;

    /**
     * @brief Normalize elements of input vector such that sum(normalized_probas) =
     *        1
     * @param probas Vector of probabilities
     * @return
     */
    public double[] normalizeProbas(double[] probas) {
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
    public double[] mutate(double[] genotype, double mutation_rate) {
        Random rand = new Random();
        for (int i = 0; i < genotype.length; i++) {
            genotype[i] += rand.nextGaussian() * mutation_rate;
            genotype[i]  = Math.max(0, Math.min(1, genotype[i]));
            
        }
        return genotype;
    }

    /**
     * 
     * @param total_nb_individuals
     * @return The dying probability of an individual, which is the same for
     *         everyone
     * 
     */
    public double computeDyingProbability(int total_nb_individuals) {
        return 1 / (double) total_nb_individuals;
    }

    /**
     * Compute the reproduction probability of an individual given by:
     * exp(alpha * fitness)
     * 
     * @param fitness           Fitness score of the individual
     * @param age               Float, age of the individual
     * @param alpha
     * @param beta
     * @param stochasticity_std
     * @return Non normalized reproduction probability
     * 
     */
    public double computeReproductionProbability(double fitness, double age, double alpha, double beta,
            double stochasticity_std) {

        Random rand = new Random();
        double epsilon = rand.nextGaussian();
        double reprod_proba = Math.exp(2 * fitness);
        return reprod_proba;
    }

    // Méthode pour effectuer une sélection pondérée
    /**
     * 
     * @param probabilities
     * @return Integer representing the flat index of the selected individual
     * 
     */
    public int weightedRandomSelection(double[] probabilities) {
        Random random = new Random();
        double rand = random.nextDouble(); 
        double cumulative = 0.0;

        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (rand < cumulative) {
                return i; 
            }
        }

        return probabilities.length - 1;
    }
}

class Individual {
    private int type;
    private double[] phenotype;
    private double fitness;
    private Color color;
    private double mu;
    public Color type0_color = new Color(0, 0, 147);
    public Color type1_color = new Color(147, 147, 0);
    public double mu_type0 = 10E-5;
    public double mu_type1 = 10E-1;
    


    /**
     * Constructor of Individual class. Create an individual with a phenotype vector of length traitCount
     * and a type.
     * @param traitCount
     * @param type
     * 
     */
    public Individual(int traitCount, int type) {
        this.phenotype = new double[traitCount];
        this.type = type;

        if (type == 0) {
            this.color = type0_color;
            this.mu = mu_type0;
            for (int i = 0; i < traitCount; i++) {
                this.phenotype[i] = 0;
            }
        }
        if (type == 1) {
            this.color = type1_color;
            this.mu = mu_type1;
            for (int i = 0; i < traitCount; i++) {
                this.phenotype[i] = 0;
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

    public double getMu() {
        return this.mu;
    }

    public void setMu(double new_mu) {
        this.mu = new_mu;
    }

    public void becomeMutator(int increasing_factor) {
        this.mu = this.mu + 2;
    }

    public void reverseMutator(int decreasing_factor) {
        this.mu = this.mu / decreasing_factor;
    }
}

class Population extends JPanel {
    private final int gridSize; 
    private final int cellSize; 
    private final Individual[][] grid;
    private final int d;
    private EvolutionaryMaths math;

    public Population(int gridSize, int cellSize, int traitCount, String config) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.grid = new Individual[gridSize][gridSize];
        this.d = traitCount;
        this.math = new EvolutionaryMaths(this.d, 1);
        initializeGrid(traitCount,config);
        setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
    }

    public void initializeGrid(int traitCount, String config) {
        switch (config) {
            case "split": 
                int target_nb0 = (int) Math.floor(gridSize * gridSize * 0.5);
                int count0 = 0;
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (count0 < target_nb0) {
                            grid[i][j] = new Individual(traitCount, 0);
                            count0++;
                        } else {
                            grid[i][j] = new Individual(traitCount, 1);
                        }
                    }
                }
                break;
            case "checkerboard": 
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        grid[i][j] = new Individual(traitCount, (i + j) % 2);
                    }
                }
                break;
            case "random": 
                Random rand = new Random();
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        grid[i][j] = new Individual(traitCount, rand.nextInt(2));
                    }
                }
                break;
            case "circles": 
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        grid[i][j] = new Individual(traitCount, 0);
                    }
                }
                int radius = 3;
                for (int ci = gridSize / 10; ci < gridSize; ci += gridSize / 10) {
                    for (int cj = gridSize / 10; cj < gridSize; cj += gridSize / 10) {
                        for (int i = -radius; i <= radius; i++) {
                            for (int j = -radius; j <= radius; j++) {
                                int ni = ci + i;
                                int nj = cj + j;
                                if (ni >= 0 && ni < gridSize && nj >= 0 && nj < gridSize && (i * i + j * j <= radius * radius)) {
                                    grid[ni][nj] = new Individual(traitCount, 1);
                                }
                            }
                        }
                    }
                }
                break;
            case "diagonal": 
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (Math.abs(i - j) < 25) {
                            grid[i][j] = new Individual(traitCount, 1);
                        } else {
                            grid[i][j] = new Individual(traitCount, 0);
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown configuration: " + config);
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

    public void makeMutator(int individualType) {     
        for (int i=0; i < gridSize; i++) { 
            for (int j = 0; j < gridSize; j++) {
                Individual individual = grid[i][j];
                if (individual.getType() == individualType) {
                    individual.becomeMutator(5);
                } 
            }
        }
    }

    public void cancelMutator(int individualType) {

        for (int i=0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Individual individual = grid[i][j]; 
                if (individual.getType() == individualType) {
                    individual.reverseMutator(5);
                }
            }
        }
    }

    /**
     * Shrink the population by 50%
     * @param individualType
     */
    public void makeCatastrophy(int individualType) {
        int deathCounter = 0;
        int deathTarget = 200;
        for (int i=0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Individual individual = grid[i][j];
                if (deathCounter < deathTarget && individualType == individual.getType()) {
                    individual.setType(1 - individualType);
                    individual.setMu(individual.mu_type0);
                    deathCounter++;
                }
            }
        }
    }

    public int[] countIndividuals() {
        int countClass0 = 0;
        int countClass1 = 0;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid[i][j].getType() == 0) {
                    countClass0++;
                } else if (grid[i][j].getType() == 1) {
                    countClass1++;
                }
            }
        }
        return new int[]{countClass0, countClass1};
    }


    public void updateGrid() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j].setPhenotype(this.math.mutate(grid[i][j].getPhenotype(), grid[i][j].getMu()));
                grid[i][j].setFitness(this.math.Fitness(grid[i][j].getPhenotype()));

            }
        }
        int dim_vector = gridSize * gridSize;
        double[] proba_repro = new double[dim_vector];
        double[] proba_death = new double[dim_vector];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                proba_repro[i * gridSize + j] = math.computeReproductionProbability(grid[i][j].getFitness(), 50, 1, 0,
                        0);
                proba_death[i * gridSize + j] = math.computeDyingProbability(dim_vector);
            }
        }

        double[] proba_repro_normed = math.normalizeProbas(proba_repro);
        
        int death = this.math.weightedRandomSelection(proba_death);
        int born = this.math.weightedRandomSelection(proba_repro_normed);

        int i_death = death / gridSize;
        int j_death = death % gridSize;

        int i_born = born / gridSize;
        int j_born = born % gridSize;

        grid[i_death][j_death].setColor(grid[i_born][j_born].getColor());
        grid[i_death][j_death].setPhenotype(grid[i_born][j_born].getPhenotype());
        grid[i_death][j_death].setType(grid[i_born][j_born].getType());
        grid[i_death][j_death].setMu(grid[i_born][j_born].getMu());
        repaint();
    }

    public double[] computeAverageFitness() {
        double totalFitnessClass0 = 0, totalFitnessClass1 = 0;
        int countClass0 = 0, countClass1 = 0;
        
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid[i][j].getType() == 0) {
                    totalFitnessClass0 += grid[i][j].getFitness();
                    countClass0++;
                } else {
                    totalFitnessClass1 += grid[i][j].getFitness();
                    countClass1++;
                }
            }
        }
        double avgFitness0 = countClass0 > 0 ? totalFitnessClass0 / countClass0 : 0;
        double avgFitness1 = countClass1 > 0 ? totalFitnessClass1 / countClass1 : 0;
        
        return new double[]{avgFitness0, avgFitness1};
    }

    public void updateGrid2() {
        int death = new Random().nextInt(gridSize * gridSize);
        int i_death = death / gridSize;
        int j_death = death % gridSize;
    
        List<int[]> neighbors = new ArrayList<>();
        List<Double> fitnessValues = new ArrayList<>();
        double fitnessSum = 0.0;
    
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                int ni = i_death + di;
                int nj = j_death + dj;
                if (ni >= 0 && ni < gridSize && nj >= 0 && nj < gridSize && !(ni == i_death && nj == j_death)) {
                    neighbors.add(new int[]{ni, nj});
                    double fit = grid[ni][nj].getFitness();
                    fitnessValues.add(fit);
                    fitnessSum += fit;
                }
            }
        }
    
        double[] probabilities = new double[neighbors.size()];
        for (int k = 0; k < neighbors.size(); k++) {
            probabilities[k] = fitnessValues.get(k) / fitnessSum;
        }
    
        int selectedIdx = math.weightedRandomSelection(probabilities);
        int[] parentCoords = neighbors.get(selectedIdx);
        int i_born = parentCoords[0];
        int j_born = parentCoords[1];
    
        
        grid[i_death][j_death].setColor(grid[i_born][j_born].getColor());
        grid[i_death][j_death].setPhenotype(math.mutate(grid[i_born][j_born].getPhenotype(), grid[i_born][j_born].getMu()));
        grid[i_death][j_death].setType(grid[i_born][j_born].getType());
        grid[i_death][j_death].setMu(grid[i_born][j_born].getMu());
        grid[i_death][j_death].setFitness(math.Fitness(grid[i_death][j_death].getPhenotype()));
        
        repaint();
    }
    
}

//

public class EvolutionSimulation {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java EvolutionSimulation <visualization_flag (0 or 1)> <m>");
            return;
        }
        
        int visualizationFlag = Integer.parseInt(args[0]);
        int m = Integer.parseInt(args[1]);
        int gridSize = 70;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int cellSize = Math.min(screenSize.width, screenSize.height) / gridSize;

        List<String> csvData = new ArrayList<>();
        csvData.add("Experiment,Time Step,Class 0,Class 1,Avg Fitness Class 0,Avg Fitness Class 1");

        for (int exp = 0; exp < m; exp++) {
            String config = "split";
            Population pop = new Population(gridSize, cellSize, 3,config);
            JFrame frame = null;
            if (visualizationFlag == 1) {
                frame = new JFrame("Evolution Simulation - Run " + (exp + 1));
                frame.add(pop);
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }

            int n = 600000;
            
            for (int i = 0; i < n; i++) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pop.updateGrid2();

                // if (i == 5000) {
                //     pop.makeMutator(1);
                // }
                
                int[] counts = pop.countIndividuals();
                double[] avgFitness = pop.computeAverageFitness();
                
                // System.out.println("Experiment " + (exp + 1) + " - Time step " + i + ": Class 0 = " + counts[0] + ", Class 1 = " + counts[1] + ", Avg Fitness Class 0 = " + avgFitness[0] + ", Avg Fitness Class 1 = " + avgFitness[1]);
                csvData.add((exp + 1) + "," + i + "," + counts[0] + "," + counts[1] + "," + avgFitness[0] + "," + avgFitness[1]);
            }
        }

        saveToCSV("./class_counts.csv", csvData);
    }

    private static void saveToCSV(String filename, List<String> data) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String line : data) {
                writer.write(line + "\n");
            }
            System.out.println("Data saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




