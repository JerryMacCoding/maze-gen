package ca.mcscert.se2aa4.tools.mazegen;

public class MazeSolver {//Based on Right Hand Version
    private Maze mazeS;
    private Location currentLocation;
    private Direction currentDirection;

    public MazeSolver(Maze maze){ //Initializing 
        this.mazeS = maze;
        this.currentLocation = mazeS.findStartPoint();
        if (this.currentLocation == null) {
        throw new IllegalStateException("Start point of the maze cannot be null");
    }
        this.currentDirection = Direction.East;
    }
    
    public String solver() {
        StringBuilder path = new StringBuilder();

        while (!currentLocation.equals(mazeS.findEndPoint())){
            if (ableMoveFoward()){
                moveFoward();
                path.append("F");
            }
            else if (ableMoveRight()){
                turnRight();
                path.append("R");
                moveFoward();
                path.append("F");
            }
            else {
                turnLeft();
                path.append("L");
            }
        }
        return path.toString();
    }

    private boolean ableMoveFoward(){
        Location nextLocation = getDirection(currentDirection);
        return isValidLocation(nextLocation) && mazeS.tileAt(nextLocation.x(), nextLocation.y()) == Tile.EMPTY;
    }

    private boolean ableMoveRight(){
        Direction correctDirection = currentDirection.turnRight();
        Location nextLocation = getDirection(correctDirection);
        return isValidLocation(nextLocation) && mazeS.tileAt(nextLocation.x(), nextLocation.y()) == Tile.EMPTY;
    }

    private void moveFoward(){
        currentLocation = getDirection(currentDirection);
    }

    private void turnRight(){
        currentDirection = currentDirection.turnRight();
    }

    private void turnLeft(){
        currentDirection = currentDirection.turnLeft();
    }

    public enum Direction{
        North, East, South, West;
        public Direction turnRight(){
            return Direction.values()[(this.ordinal() + 1) % Direction.values().length];
        }
        public Direction turnLeft(){
            return Direction.values()[(this.ordinal() + 3) % Direction.values().length];
        }
    }

    private boolean isValidLocation(Location loc) {
    return loc.x() >= 0 && loc.x() < mazeS.getWidth() && loc.y() >= 0 && loc.y() < mazeS.getHeight();
}

    private Location getDirection(Direction direction){
        switch (direction){
            case North : return new Location(currentLocation.x(), currentLocation.y() - 1);
            case East : return new Location(currentLocation.x() + 1, currentLocation.y());
            case South : return new Location(currentLocation.x(), currentLocation.y() + 1);
            case West : return new Location(currentLocation.x() - 1, currentLocation.y());
            default: throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }
}
