import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by work on 2016-10-12.
 */
public class Ant {

    Ant thisAnt = this;
    int colony;
    int health = 5;//fixed for now
    double SCENTPREFERENCE;
    double TRAVELDISAPATION;
    double scentRate = 1;
    public double travelTime;
    public Point location;
    public int direction;
    int pathTicker = 0;

    public boolean atHome = true;
    public boolean hasFood = false;
    public boolean foundFood = false;
    public boolean foundEnemy = false;

    public Action act = new Action();
    public Move move = new Move();
    public Search search = new Search();
    public ArrayList<Point>pathList= new ArrayList<>();
    Point lastLocation = new Point(0,0);

    Point foodLoc;
    Ant enemyAnt;

    public Ant(Point _location, int _colony){
        location = _location;
        Random rand = new Random();
        direction = rand.nextInt(7);
        TRAVELDISAPATION = Game.game.colonies.get(_colony).TRAVELDISAPATION;
        SCENTPREFERENCE = Game.game.colonies.get(_colony).SCENTPREFERENCE;
        colony = _colony;
        pathList.add(new Point(location));
    }
    class Search{
        public void forEnemies(){
            Map.Tile[] tiles = Game.game.map.search(location);
            for(int i = 0; i < tiles.length; i++){
                if(tiles[i] == null){
                    continue;
                }
                if(tiles[i].ants.size() > 0){
                    //FIX: this returns first food found not the biggest.
                    for(Ant ant: tiles[i].ants){
                        if(ant.colony != colony){
                            foundEnemy = true;
                            enemyAnt = ant;
                        }
                    }
                }
            }
        }
        public void forFood(){
            Map.Tile[] tiles = Game.game.map.search(location);
            for(int i = 0; i < tiles.length; i++){
                if(tiles[i] == null){
                    continue;
                }
                if(tiles[i].food > 0){
                    //FIX: this returns first food found not the biggest.
                    foodLoc = new Point(tiles[i].location);
                    foundFood = true;
                    break;
                }
            }
        }
        public double[] forSearchScents(){
            Map.Tile[] tiles = Game.game.map.search(location);
            double[] scentMap = new double[8];
            int i = 0;
            for(Map.Tile tile:tiles){
                if(tiles[i] == null){continue;}
                scentMap[i] = tile.searchTrail.amount;
                i++;
            }
            return scentMap;
        }
        public boolean[] forObstacles(){
            Map.Tile[] tiles = Game.game.map.search(location);
            boolean[] obstacleMap = new boolean[8];
            for(int i = 0; i<obstacleMap.length;i++){
                if(tiles[i] == null){
                    continue;
                }
                if(tiles[i].obstacle.active){
                    obstacleMap[i] = true;
                }
                else{
                    obstacleMap[i] = false;
                }
            }
            boolean[] insideMap = Utils.isInsideMapBounds(location);
            for(int i = 0; i<8; i++){
                if(!(insideMap[i])){
                    obstacleMap[i] = true;
                }
            }
            return obstacleMap;
        }
    }

    class Action{
        public void dropOffFood(){
            Game.game.colonies.get(colony).foodStore += 1;
            double[] tile = search.forSearchScents();
            int largest = 0;
            for(int i = 0; i < 8; i++){
                if(tile[i] > tile[largest]){
                    largest = i;
                }
            }
            direction = largest;
            travelTime = 0;
            hasFood = false;
            pathList.clear();
            pathList.add(new Point(location));
        }
        public void deploySearchTrail(){
            double scentAmount = scentRate;
            Utils.pointToTile(location).searchTrail.amount+=scentAmount;
        }
        public void rememberPath(){
            pathTicker++;
            if(pathTicker > 0){
                pathList.add(new Point(location));
                pathTicker = 0;
            }
        }
        public void fightEnemy(){
            enemyAnt.health-=1;
            if(enemyAnt.health < 0){
                enemyAnt = null;
                foundEnemy = false;
            }
        }
    }

    class Move{
        public void explore(){
            atHome = false;
            if(Game.game.colonies.get(colony).isInColony(location)){
                travelTime = 0;
                pathList.clear();
                pathList.add(new Point(location));
            }
            move(search.forSearchScents());
            act.rememberPath();
        }

        public void toHome(){
            //move towards last entered point
            if(Game.game.colonies.get(colony).isInColony(location)){
                atHome = true;
                act.dropOffFood();
                return;
            }
            moveHome();
            if(hasFood){
                act.deploySearchTrail();
            }
        }

        public void toFood(){
            location.setLocation(foodLoc.x,foodLoc.y);//this only works if search radius is one tile
            Game.game.map.map.get(foodLoc.x).get(foodLoc.y).food--;
            scentRate = Game.game.travelDisipation.getValue()/(travelTime*travelTime/15+Game.game.travelDisipation.getValue());
            pathList = Utils.removeLoops(pathList);
            foundFood = false;
            travelTime = 0;
            hasFood = true;
        }

        private void moveHome(){
            if(pathList.size()-1 < 0){
                return;
            }
            Point pathLocation = new Point(pathList.get(pathList.size()-1));
            double x = pathLocation.x - location.x;
            double y = pathLocation.y - location.y;
            double distance = Math.sqrt(x*x + y*y);
            direction = Utils.xyToDirection(x/distance,y/distance);
            Random r = new Random();
            int rand = r.nextInt(2);
            if(rand == 0){
                direction--;
            }
            if(rand == 2){
                direction++;
            }
            /*
            if(Game.game.map.map.get((int)(location.x+x/distance)).get((int)(location.y+y/distance)).obstacle.active == false){
            }
            */
            location.setLocation(location.x + x / distance, location.y + y / distance);
            if(location.x == pathLocation.x && location.y == pathLocation.y){
                pathList.remove(pathList.size()-1);
            }
            //if roundtrip is faster increase scent
        }

        private void move(double[] scentMap){
            travelTime++;
            boolean[] obstacles = search.forObstacles();
            int j = 0;
            double[] weighting = new double[8];
            double total = 0;
            for(int i = -1; i <= 1; i++){
                if(obstacles[Utils.range(direction+i)]){
                    weighting[Utils.range(direction+i)] = 0;
                    continue;
                }
                weighting[Utils.range(direction+i)] += SCENTPREFERENCE+scentMap[Utils.range(direction+i)];
                total += weighting[Utils.range(direction+i)];
            }
            if(total == 0){
                //cannot go forward
                direction = Utils.range(direction-4); // try going backwards
                return;
            }
            Random rand = new Random();
            double n = rand.nextDouble()*total;
            double runningTotal = 0;
            for(int i = 0; i < weighting.length; i++){
                runningTotal+=weighting[i];
                if(n <= runningTotal){
                    j = i;
                    break;
                }
            }
            switch (j) {
                case 0: location.y-=1; // north
                    break;
                case 1: location.x+=1; location.y-=1; // north east
                    break;
                case 2: location.x+=1; // east
                    break;
                case 3: location.x+=1; location.y+=1; // south east
                    break;
                case 4: location.y+=1; // south
                    break;
                case 5: location.x-=1; location.y+=1; // south west
                    break;
                case 6: location.x-=1; // west
                    break;
                case 7: location.x-=1; location.y-=1; // north west
                    break;
            }
            direction = j;
        }
    }

    public void updateStatus(){
        Utils.pointToTile(location).ants.add(thisAnt);
    }

    public void render(Graphics g){
        if(hasFood) {
            g.setColor(Game.game.colonies.get(colony).color);
            g.fillOval(location.x*Game.game.map.tilesize, location.y*Game.game.map.tilesize,Game.game.map.tilesize,Game.game.map.tilesize);
        }
        else {
            g.setColor(Game.game.colonies.get(colony).color);
            g.drawOval(location.x*Game.game.map.tilesize, location.y*Game.game.map.tilesize,Game.game.map.tilesize,Game.game.map.tilesize);
        }
    }

}
