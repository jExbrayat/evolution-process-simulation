import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.List;

public static ArrayList<Double> normalizeProbas(ArrayList<Double> probas) {
    // Create a copy of the probas list
    ArrayList<Double> normalizedProbas = new ArrayList<>(probas);
    
    // Calculate the sum of the probabilities
    double sum = 0;
    for (double proba : probas) {
        sum += proba;
    }
    
    // Normalize each probability and store it in normalizedProbas
    for (int i = 0; i < probas.size(); i++) {
        double normalized = probas.get(i) / sum;
        normalizedProbas.set(i, normalized);
    }
    
    return normalizedProbas;
}

class Individual {
    private double[] phenotype;

    public Individual(int traitCount) {
        phenotype = new double[traitCount];
        Random random = new Random();
        for (int i = 0; i < traitCount; i++) {
            phenotype[i] = random.nextDouble(); // Random phenotype value between 0 and 1
        }
    }

    public double[] getPhenotype() {
        return phenotype;
    }

    public Color getColor() {
        int colorValue = (int) (phenotype[0] * 255);
        return new Color(colorValue, colorValue, 255 - colorValue);
    }
}

class GridPanel extends JPanel {
    private final int gridSize; // Number of cells in one dimension
    private final int cellSize; // Size of each cell in pixels
    private final Individual[][] grid;

    public GridPanel(int gridSize, int cellSize, int traitCount) {
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
        // Mutation logic can be added here
        repaint();
    }
}

// 
public class EvolutionSimulation {
    public static void main(String[] args) {
        int gridSize = 50; // Fixed number of cells in one dimension
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int cellSize = Math.min(screenSize.width, screenSize.height) / gridSize; // Calculate cell size to fit the screen

        JFrame frame = new JFrame("Evolution Simulation");
        GridPanel gridPanel = new GridPanel(gridSize, cellSize, 3);

        frame.add(gridPanel);
        frame.pack(); // Automatically sizes the frame to fit the panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);

        new Timer(1000, e -> gridPanel.updateGrid()).start();
    }
}
