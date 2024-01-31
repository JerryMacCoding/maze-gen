package ca.mcscert.se2aa4.tools.mazegen;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Maze {

    private static final Logger logger = LogManager.getLogger();

    private final Tile[][] theGrid;
    private Location startPoint;
    private Location endPoint;

    public Maze(int width, int height) {
        this.theGrid = new Tile[height][width];
        fillWithWalls();
        /*MazeCarver carver = new MazeCarver();
        carver.carve(this, new Random());*/
    }

    public void generate(Random random) {
        carve(random);
        initializeStartAndEndPoints();
    }

    private void initializeStartAndEndPoints() {
        this.startPoint = findStartPoint();
        this.endPoint = findEndPoint();
        if (startPoint == null || endPoint == null) {
            throw new IllegalStateException("Failed to create valid start or end points.");
        }
    }

    public Location findStartPoint() {
        for (int x = 0; x < getWidth(); x++) {
            if (tileAt(x, 0) == Tile.EMPTY) {
                return new Location(x, 0);
            }
        }
        return null;
    }

    public Location findEndPoint() {
        
        for (int x = 0; x < getWidth(); x++) {
            if (tileAt(x, getHeight() - 1) == Tile.EMPTY) {
                return new Location(x, getHeight() - 1);
            }
        }
        return null; 
    }

    public static Maze load(BufferedReader reader) throws IOException {
        List<String> lines = reader.lines().collect(Collectors.toList());
        int height = lines.size();
        int width = lines.get(0).length();
        Maze maze = new Maze(width, height);

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                maze.theGrid[y][x] = (ch == '#') ? Tile.WALL : Tile.EMPTY;
            }
        }

        maze.initializeStartAndEndPoints();
        return maze;
    }

    public int getWidth() { return this.theGrid[0].length; }

    public int getHeight() { return this.theGrid.length; }

    public Tile tileAt(int x, int y) { return theGrid[y][x]; }

    public void carve(Random random) {
        MazeCarver carver = new MazeCarver();
        carver.carve(this, random);
    }

    public void export(BufferedWriter out, boolean humanReadable) throws IOException {
        MazeExporter exporter = new MazeExporter(this);
        exporter.export(out, humanReadable);
    }


    public void punch(Location loc) { this.theGrid[loc.y()][loc.x()] = Tile.EMPTY; }

    public void destroyWall(Location from, Location to) {
        logger.trace("Carving tunnel rom " + from + " to " + to);
        punch(from);
        punch(to);
        int deltaX = 0;
        if (to.x() - from.x() < 0) { deltaX = -1; } else if (to.x() - from.x() > 0) { deltaX = 1; }
        int deltaY = 0;
        if (to.y() - from.y() < 0) { deltaY = -1; } else if (to.y() - from.y() > 0) { deltaY = 1; }
        Location tunnel = new Location(from.x() + deltaX, from.y() + deltaY);
        punch(tunnel);
    }

    public Set<Location> neighborsInRange(Location loc) {
        return loc.neighbors().stream()
                .filter(l -> l.x() > 0 && l.x() < getWidth() - 1)
                .filter(l -> l.y() > 0 && l.y() < getHeight() - 1)
                .collect(Collectors.toSet());
    }

    private void fillWithWalls() {
        for(int i = 0; i < theGrid.length; i++) {
            Tile[] row = theGrid[i];
            for(int j = 0; j < row.length; j++) {
                theGrid[i][j] = Tile.WALL;
            }
        }
    }

    
}
