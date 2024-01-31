package ca.mcscert.se2aa4.tools.mazegen;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            logger.info("Starting Generate the Maze");
            Configuration config = Configuration.load(args);
            Maze theMaze = loadOrGenMaze(config);
            solveAndExpoMaze(theMaze, config);
        } catch (Exception e) {
            logger.error("An error occurred: ", e);
        }
    }

    private static Random buildReproducibleRandomGenerator(Configuration config) {
        Random random = new Random();
        random.setSeed(config.seed());
        return random;
    }

    private static Maze loadOrGenMaze(Configuration config) throws IOException {
        if (config.inputFile() != null && !config.inputFile().isEmpty()) {
            logger.info("Loading Maze From Your File: " + config.inputFile());
            try (BufferedReader reader = new BufferedReader(new FileReader(config.inputFile()))) {
                return Maze.load(reader);
            }
        } else {
            logger.info("No Input Maze file provided, generating a random new maze.");
            Random random = buildReproducibleRandomGenerator(config);
            Maze maze = new Maze(config.width(), config.height());
            maze.generate(random);
            return maze;
        }
    }

    private static void solveAndExpoMaze(Maze theMaze, Configuration config) throws IOException {
       if (theMaze.findStartPoint() == null || theMaze.findEndPoint() == null) {
            logger.error("Not having a start or end point.");
            return;
        }
        MazeSolver solver = new MazeSolver(theMaze);
        String solution = solver.solver(); 
        logger.info("The Path for the Maze is: " + solution);

        MazeExporter exporter = new MazeExporter(theMaze);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
            exporter.export(writer, config.humanReadable());
        }
        logger.info("End of generation");
    }
}

