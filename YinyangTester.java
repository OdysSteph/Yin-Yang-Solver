import java.util.Random;

public class YinyangTester {
    public static void main(String[] args) throws Exception {
        // Initialize file readers and writers
        java.util.Scanner sc = new java.util.Scanner(new java.io.File("test.txt"));
        java.util.Scanner paramScanner = new java.util.Scanner(new java.io.File("params.txt"));
        java.io.FileWriter fw = new java.io.FileWriter("output.txt");
        
        Random randomizer = new Random(2);

        // Read genetic algorithm parameters (global for all test cases)
        int maxGenerations = paramScanner.nextInt();
        int populationSize = paramScanner.nextInt();
        double elitismPercentage = paramScanner.nextDouble();
        double crossoverRate = paramScanner.nextDouble();
        double mutationRate = paramScanner.nextDouble();

        // Read number of test cases
        int testCases = sc.nextInt();
        int accurateCount = 0;

        for (int testCase = 0; testCase < testCases; testCase++) {
            fw.write("Test Case #" + (testCase + 1) + "\n");

            // Read board size and constraints
            int n = sc.nextInt();
            int[][] board = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    board[i][j] = sc.nextInt();
                }
            }

            // Initialize population
            YinyangPopulation population = new YinyangPopulation(
                n, populationSize, board, randomizer, 
                elitismPercentage, crossoverRate, mutationRate
            );

            population.generateRandom();
            population.computeAllFitness();

            YinYangCell fittest = population.getFittest();
            int generationCount = 0;

            // Evolution process
            while (generationCount < maxGenerations && fittest.getHeuristic() > 0) {
                generationCount++;
                
                // Generate new offspring population
                population = population.makeOffspring();
                population.computeAllFitness();
                fittest = population.getFittest();
            }

            // Output results
            fw.write("Fittest Solution:\n");
            fw.write(fittest.toString() + "\n");
            if (fittest.getHeuristic() == 0) {
                fw.write("Solution found in " + generationCount + " generations\n");
                accurateCount++;
            } else {
                fw.write("No perfect solution. Best heuristic: " + fittest.getHeuristic() + 
                         " in " + generationCount + " generations\n");
            }
            fw.write("\n");
        }

        // Write accuracy results
        double accuracy = accurateCount * 1.0 / testCases;
        fw.write("Accuracy = " + accuracy + "\n");
        fw.write("Correct solutions: " + accurateCount + " out of " + testCases + "\n");
        
        // Close file handlers
        fw.close();
        sc.close();
        paramScanner.close();
    }
}