package ca.mcscert.se2aa4.tools.mazegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main_0 {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        try{
        logger.info("Starting Generate the Maze");
        Configuration config = Configuration.load(args); // 
        
        Maze theMaze;

        if (config.inputFile() != null && !config.inputFile().isEmpty()){
            logger.info("Loading Maze From Your File:  " + config.inputFile());
            try (BufferedReader reader = new BufferedReader(new FileReader(config.inputFile()))){
                theMaze =Maze.load(reader);
            } catch(IOException e) {
                logger.error("Unable to load File: " + config.inputFile(),e );
                return;
            }
        
        
        }else{
            
            logger.info("No Input Maze file provide, generating a random new maze.");
            Random random = buildReproducibleRandomGenerator(config);
            theMaze = new Maze(config.width(), config.height());
            theMaze.carve(random);
        }
            //Solve The Maze
            MazeSolver solver = new MazeSolver(theMaze);
            String solution = solver.solver();
            logger.info("The Path for the Maze is: " + solution);
        
        
            //Export the Maze
            MazeExporter exporter = new MazeExporter(theMaze);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))){
                exporter.export(writer, config.humanReadable());
            } logger.info("End of generation");

        } catch (Exception e) {
            logger.error("An error occurred: ", e);
        }
    }
    

    private static Random buildReproducibleRandomGenerator(Configuration config) {
        Random random = new Random();
        random.setSeed(config.seed());
        return random;
    }

}
