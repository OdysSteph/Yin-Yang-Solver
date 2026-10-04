import java.util.ArrayList;
import java.util.Random;

public class YinyangPopulation {
    public ArrayList<YinYangCell> population;
    public Random randomizer;
    public int populationSize;
    public int n;
    public int[][] board;
    public double elitismPct;
    public double crossoverRate;
    public double mutationRate;

    // Constructor
    public YinyangPopulation(int n, int size, int[][] board, Random randomizer,
            double elitismPct, double crossoverRate, double mutationRate) {
        this.population = new ArrayList<>();
        this.populationSize = size;
        this.randomizer = randomizer;
        this.elitismPct = elitismPct;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.n = n;
        this.board = board;
    }

    // Compute fitness for all population members
    public void computeAllFitness() {
        for (YinYangCell cell : population) {
            cell.calculateHeuristic();
        }
        population.sort(YinYangCell::compareTo);
    }

    // Generate random initial population
    public void generateRandom() {
        for (int i = 0; i < populationSize; i++) {
            population.add(new YinYangCell(n, board, this.randomizer));
        }
    }

    // Get the fittest individual
    public YinYangCell getFittest() {
        return population.get(0);
    }

    // Add a single cell to population
    public void addCell(YinYangCell target) {
        population.add(target);
    }

    // Parent selection using roulette wheel
    public YinYangCell[] selectParent() {
        YinYangCell[] res = new YinYangCell[2];
        int maxHeuristic = population.get(population.size() - 1).getHeuristic();
        double[] probabilities = new double[population.size()];
        double total = 0;

        // Calculate probabilities
        for (int i = 0; i < population.size(); i++) {
            int curr = maxHeuristic + 1 - population.get(i).getHeuristic();
            probabilities[i] = curr;
            total += curr;
        }

        for (int i = 0; i < population.size(); i++) {
            probabilities[i] /= total;
        }

        // Select two parents
        for (int i = 0; i < 2; i++) {
            int j = -1;
            double prob = this.randomizer.nextDouble();
            double sum = 0.0;
            do {
                j++;
                sum += probabilities[j];
            } while (sum < prob);
            res[i] = population.get(j);
        }
        return res;
    }

    // Modified crossover to return two children
    public YinYangCell[] crossOver(YinYangCell parent1, YinYangCell parent2) {
        int boardSize = parent1.getCellBoard().length;
        int[][] child1 = new int[boardSize][boardSize];
        int[][] child2 = new int[boardSize][boardSize];

        double chooseCrossover = this.randomizer.nextDouble();

        // Multiple crossover strategies
        if (chooseCrossover < 0.4) {
            // Two-point crossover
            int crossPoint1 = randomizer.nextInt(boardSize * boardSize);
            int crossPoint2 = randomizer.nextInt(boardSize * boardSize);

            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    int flatIndex = i * boardSize + j;
                    if (flatIndex < Math.min(crossPoint1, crossPoint2) ||
                            flatIndex > Math.max(crossPoint1, crossPoint2)) {
                        child1[i][j] = parent1.getCellBoard()[i][j];
                        child2[i][j] = parent2.getCellBoard()[i][j];
                    } else {
                        child1[i][j] = parent2.getCellBoard()[i][j];
                        child2[i][j] = parent1.getCellBoard()[i][j];
                    }
                }
            }
        } else if (chooseCrossover < 0.7) {
            // Horizontal row crossover
            int crossoverPoint = randomizer.nextInt(boardSize);
            for (int i = 0; i < boardSize; i++) {
                if (i <= crossoverPoint) {
                    child1[i] = parent1.getCellBoard()[i].clone();
                    child2[i] = parent2.getCellBoard()[i].clone();
                } else {
                    child1[i] = parent2.getCellBoard()[i].clone();
                    child2[i] = parent1.getCellBoard()[i].clone();
                }
            }
        } else {
            // Checkerboard crossover
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if ((i + j) % 2 == 0) {
                        child1[i][j] = parent1.getCellBoard()[i][j];
                        child2[i][j] = parent2.getCellBoard()[i][j];
                    } else {
                        child1[i][j] = parent2.getCellBoard()[i][j];
                        child2[i][j] = parent1.getCellBoard()[i][j];
                    }
                }
            }
        }

        return new YinYangCell[] {
                new YinYangCell(parent1.getBoard(), child1, this.randomizer),
                new YinYangCell(parent2.getBoard(), child2, this.randomizer)
        };
    }

    public YinyangPopulation makeOffspring() {
        YinyangPopulation population2 = new YinyangPopulation(n, populationSize, board,
                randomizer, elitismPct, crossoverRate, mutationRate);
        int eliteCount = (int) (elitismPct * populationSize);

        // Add elite individuals
        for (int i = 0; i < eliteCount; i++) {
            population2.addCell(population.get(i));
        }

        // Reproduction (selection, crossover, mutation)
        while (population2.population.size() < populationSize) {
            YinYangCell[] parents = selectParent();
            YinYangCell[] children = crossOver(parents[0], parents[1]);

            for (YinYangCell child : children) {
                if (randomizer.nextDouble() < mutationRate) {
                    child.doMutation();
                }
                if (population2.population.size() < populationSize) {
                    population2.addCell(child);
                }
            }
        }

        population2.computeAllFitness();
        return population2;
    }
}