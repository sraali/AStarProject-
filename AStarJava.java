import java.util.*;
public class AStarJava {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    private static final int FLOORCOST = 1;
    private static final int MOATCOST = 20;
    private static final int SWAMPCOST = 5;

    private static char[][] map = new char[][]{
            {'F','F','F','F','F','F','F','F','F','F'},
            {'F','W','W','W','W','W','W','W','W','F'},
            {'F','F','F','F','F','F','F','F','F','F'},
            {'T','F','F','F','F','F','F','F','F','T'},
            {'M','M','M','M','M','M','M','M','M','M'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'}};

    private static int t1x = 3;
    private static int t1y = 0;
    private static int t2x = 3;
    private static int t2y = 9;
    private static String characterType;
    private static int startX;
    private static int startY;
    private static int goalX;
    private static int goalY;
    private static int fringeCount;
    private static int treeCount;
    private static int heuristictype;


    /**
     * This is the main method you will modify. It takes an (x, y)
     * coordinate and returns the estimated distance to the goal.
     * The default version uses a simple Manhattan distance.
     * Your custom version should 1) be admissible, and 2) perform
     * better in terms of nodes added to the fringe and tree.
     * Note that you can access the goal location using the global
     * variables goalX, goalY.
     * @param x
     * @param y
     * @return
     */
    public static double heuristic(int x, int y, int gx, int gy) {

        int manhattan = Math.abs(gx - x) + Math.abs(gy - y);
        if (heuristictype == 0) {
            return manhattan;
        }

        else {
            int estimatedCost = 0;

            //Moat
            //We need to check if we cross the 4th col when traversing
            //If x - gx > 0 then we are traveling right to left
            if(x-gx>0){
                for(int i = x; i <= gx; i++){
                    if(i == 4){
                        estimatedCost += MOATCOST;
                        break;
                    }
                }
            }
            //If gx - x > 0 then we are traveling left to right
            if(gx-x>0){
                for(int i = x; i <= gx; i-- ){
                    if(i==4){
                        estimatedCost += MOATCOST;
                        break;
                    }
                }
            }

            //Wall

            //To efficiently deal with traversal around the wall, we need to establish waypoints that
            //represent the top and bottom floor tiles around the wall.
            //We only need to get to the second column.

            if((x == 0 && gx >= 1) || (gx == 0 && x >= 1)){
                if((gx == 1) && (x != 0 || x != 9)){
                    return 10000;
                }
                int w1;
                int w2;
                int distToW1;
                int distToW2;
                if(x == 0 || gx == 0){
                    //If on the bottom half of the map we need to get to (0,9), then to (2,9)
                    if(gy >= 5) {
                        w1 = 9;
                        w2 = 2;
                    }
                    else{
                        w1 = 0;
                        w2 = 2;
                    }
                    distToW1 = Math.abs(y-w1);
                    distToW2 = Math.abs(x-w2);
                    estimatedCost += (distToW1+distToW2);
                }
            }

            //Teleport pads
            if((x>=2 && x < 5)&& gx <= 5){
                int w1;
                int w2;
                int distToW1;
                int distToGoal;
                if(y >= 4){
                    w1 = 0;
                    w2 = 9;
                }
                else{
                    w1 = 9;
                    w2 = 0;
                }
                distToW1 = Math.abs(y-w1)+ Math.abs(x-3);
                distToGoal = Math.abs(gy-w2) + Math.abs(gx-3);
                if(manhattan > (distToW1+distToGoal)){
                    estimatedCost += (distToW1+distToGoal);
                }
                else{
                    estimatedCost += manhattan;
                }

            }

            //Swamp

            //Since the floor tiles have a significantly lower traversal cost, we can break this problem
            //into waypoints. The first waypoint is closest floor tile from our starting
            //x position. The second waypoint is the closest floor tile to gy.
            if(x >= 5){
                int w1;
                int w2;
                int distToW1;
                int distToW2;
                int distW1toW2;
                //If the start and the goal are both in the same swamp, we only need to check if taking
                //a path through the floor tiles is better than directly through the swamp. To do this, we can
                //simply calculate whether the manhattan distance is greater than the sum of the distances to
                // the nearest waypoints.
                if((y <= 3 && gy <= 3) || (y >= 6 && gy >= 6)){
                    if(y<=3){
                        w1 = 4;
                        w2 = 4;
                    }
                    else{
                        w1 = 5;
                        w2 = 5;
                    }
                    distToW1 = Math.abs(w1 - y)-1;
                    distToW2 = Math.abs(w2 - gy)-1;
                    if(manhattan <= (distToW1+distToW2)){
                        estimatedCost += SWAMPCOST*manhattan;
                        return estimatedCost;
                    }
                    distW1toW2 =  Math.abs(gx-x);
                    estimatedCost += ((SWAMPCOST*(distToW1+distToW2))+distW1toW2);
                    return estimatedCost;

                }

                //Otherwise we must cross the middle part of the swamp which contains the floor tiles
                else{
                    if(y > gy ){
                         w1 = 5;
                         w2 = 4;
                    }
                    else{
                         w1 = 4;
                         w2 = 5;
                    }
                     distToW1 = Math.abs(w1 - y)-1;
                     distToW2 = Math.abs(w2 - gy)-1;
                     distW1toW2 = Math.abs(w2-w1) + Math.abs(gx-x);
                        estimatedCost += (distToW1+distToW2)*SWAMPCOST+ distW1toW2;
                    return estimatedCost;
                }
            }
            return estimatedCost;
        }
    }

    public static class Tile {
        private char tileType;
        private int x;
        private int y;
        private int treestate;
        private int parentX;
        private int parentY;
        private double gDist;
        private boolean inPath;

        public Tile() {}

        public double getCost() {
            switch(tileType) {
                case 'F':
                    return FLOORCOST;
                case 'T':
                    return FLOORCOST;
                case 'W':
                    return  10000.0;
                case 'M':
                    return MOATCOST;
                case 'S':
                    return SWAMPCOST;
                default:
                    return 0.0;
            }
        }
    }

    // Returns the tile in the fringe with the lowest f = g + h measure
    public static Tile getBest(Tile[][] graph) {
        double bestH = 100000;
        Tile bestTile = new Tile();
        bestTile.x = -1; // Hack to let caller know that nothing was in fringe (failure)
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (graph[i][j].treestate == 1) {   // Fringe node
                    double h = graph[i][j].gDist + heuristic(i, j, goalX, goalY);
                    if (h < bestH) {
                        bestH = h;
                        bestTile = graph[i][j];
                    }
                }
            }
        }
        return bestTile;
    }


    // When a node is expanded, we check each adjacent node. This method takes the graph,
    // the coordinates of the parent tile (the one expanded) and its child (the adjacent tile).
    public static void checkAdjacent(Tile[][] graph, int parentX, int parentY, int childX, int childY) {

        // If already in tree, exit
        if (graph[childX][childY].treestate == 2) {
            return;
        }
        // If unexplored, add to fringe with path from parent and distance based on
        // distance from start to parent and cost of entering the node.
        if (graph[childX][childY].treestate == 0) {
            graph[childX][childY].treestate = 1;
            graph[childX][childY].gDist = graph[parentX][parentY].gDist + graph[childX][childY].getCost();
            graph[childX][childY].parentX = parentX;
            graph[childX][childY].parentY = parentY;

            // Add to stats of nodes added to fringe
            fringeCount++;
            return;
        }
        // If fringe, reevaluate based on new path
        if (graph[childX][childY].treestate == 1) {
            if (graph[parentX][parentY].gDist + graph[childX][childY].getCost() < graph[childX][childY].gDist) {
                // Shorter path through parent, so change path and cost.
                graph[childX][childY].gDist = graph[parentX][parentY].gDist + graph[childX][childY].getCost();
                graph[childX][childY].parentX = parentX;
                graph[childX][childY].parentY = parentY;
            }
            return;
        }
    }

    // Once the goal has been found, we need the path found to the goal. This method
    // works backward from the goal through the parents of each tile. It also totals
    // up the cost of the path and returns it.
    public static double finalPath(Tile[][] graph) {
        double cost = 0;

        // Start at goal
        int x = goalX;
        int y = goalY;

        // Loop until start reached
        while (x != startX || y != startY) {

            // Add node to path and add to cost
            graph[x][y].inPath = true;
            cost += graph[x][y].getCost();

            // Work backward to parent and continue
            int tempx = graph[x][y].parentX;
            int tempy = graph[x][y].parentY;
            x = tempx;
            y = tempy;
        }
        graph[startX][startY].inPath = true;
        return cost;
    }

    // This method prints the map at the end. Each tile contains the tile type,
    // its tree status (0=unexplored, 1=fringe, 2=tree), and a * if that tile
    // was in the final path.
    public static void printGraph(Tile[][] graph) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                System.out.print(graph[j][i].tileType);
                System.out.print(graph[j][i].treestate);
                if (graph[j][i].inPath) {
                    System.out.print("*");
                }
                else {
                    System.out.print(" ");
                }
                System.out.print("  ");
            }
            System.out.print("\n");
        }
    }

    /**
     * Get inputs from user
     */
    public static void setProperties() {
        Scanner scanner = new Scanner(System.in);

        // Where does character start and end
        System.out.print("X coordinate of start: ");
        startX = scanner.nextInt();
        System.out.print("Y coordinate of start: ");
        startY = scanner.nextInt();
        System.out.print("X coordinate of end: ");
        goalX = scanner.nextInt();
        System.out.print("Y coordinate of end: ");
        goalY = scanner.nextInt();

        // What heuristic to use
        System.out.print("Manhattan (0) or Custom (1) heuristc: ");
        heuristictype = scanner.nextInt();
    }

    public static void makeGraph(Tile[][] graph) {
        // Construct and initialize the tiles.
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Tile t = new Tile();
                graph[i][j] = t;
                graph[i][j].tileType = map[i][j];   // Tile type based on above map
                graph[i][j].treestate = 0; // Initially unexplored
                graph[i][j].x = i;         // Each tile knows its location in array
                graph[i][j].y = j;
                graph[i][j].parentX = -1;  // Initially no parents on path fro start
                graph[i][j].parentY = -1;
                graph[i][j].inPath = false;    // Initially not in path from start
            }
        }
    }


    // Main A* search method. Takes graph as parameter, and returns whether search successful
    public static int search(Tile[][] graph) {
        fringeCount = 1;
        boolean goalFound = false;

        // Add start state to tree, path cost = 0
        graph[startX][startY].treestate = 1;
        graph[startX][startY].gDist = 0;

        // Loop until goal added to tree
        while (!goalFound) {

            // Get the best tile in the fringe
            Tile bestTile = getBest(graph);
            if (bestTile.x == -1) {
                // The default tile was returned, which means the fringe was empty.
                // This means the search has failed.
                System.out.print("Search failed!!!!!!\n");
                printGraph(graph);
                return 0;
            }

            // Otherwise, add that best tile to the tree (removing it from fringe)
            int x = bestTile.x;
            int y = bestTile.y;
            graph[x][y].treestate = 2;
            treeCount++;

            // If it is a goal, done!
            if (x == goalX && y == goalY) {
                goalFound = true;
                System.out.print("Found the goal!!!!!\n");

                // Compute the path taken and its cost, printing the explored graph,
                // the path  cost, and the number of tiles explored (which should be
                // as small as possible!)
                double cost = finalPath(graph);
                printGraph(graph);
                System.out.print("Path cost: " + cost+ "\n");
                System.out.print(treeCount + " tiles added to tree\n");
                System.out.print(fringeCount + " tiles added to fringe\n");

                return 1;
            }

            // Otherwise, we look at the 4 adjacent tiles to the one just added
            // to the tree (making sure each  is in the graph!) and either add it
            // to the tree or recheck its path.

            // Special cases for teleport pads
            if (x == t1x && y == t1y) {
                System.out.println("Teleport checked");
                checkAdjacent(graph, x, y, t2x, t2y);
            }
            if (x == t2x && y == t2y) {
                checkAdjacent(graph, x, y, t1x, t1y);
            }


            if (x > 0) { // Tile to left
                checkAdjacent(graph, x, y, x-1, y);
            }
            if (x < WIDTH-1) { // Tile to right
                checkAdjacent(graph, x, y, x+1, y);
            }
            if (y > 0) { // Tile above
                checkAdjacent(graph, x, y, x, y-1);
            }
            if (y < HEIGHT-1) { // Tile below
                checkAdjacent(graph, x, y, x, y+1);
            }

        }
        return 1;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Tile[][] graph = new Tile[WIDTH][HEIGHT];
        makeGraph(graph);
        setProperties();
        search(graph);

    }

}
