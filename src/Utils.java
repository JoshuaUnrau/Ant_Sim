import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by work on 2016-10-12.
 */
public class Utils {

    static boolean isWithin(Point location1, Point location2, int distance) {

        double x = location1.getX() - location2.getX();
        double y = location1.getY() - location2.getY();
        x = x * x;
        y = y * y;
        double h = Math.sqrt(x + y);
        if ((int) h <= distance) {
            return true;
        } else {
            return false;
        }
    }

    static Map.Tile pointToTile(Point point){
        return Game.game.map.map.get(point.x).get(point.y);
    }

    static Map.Tile getMapRelativeTile(Point location, int relativeTile){
        Map.Tile tile = null;
        ArrayList<ArrayList<Map.Tile>> map = Game.game.map.map;
        switch (relativeTile) {
            case 0:
                tile = map.get(location.x).get(location.y-1); // north
                break;
            case 1:
                tile = map.get(location.x+1).get(location.y-1);// north east
                break;
            case 2:
                tile = map.get(location.x+1).get(location.y); // east
                break;
            case 3:
                tile =  map.get(location.x+1).get(location.y+1);// south east
                break;
            case 4:
                tile = map.get(location.x).get(location.y+1); // south
                break;
            case 5:
                tile = map.get(location.x-1).get(location.y+1);// south west
                break;
            case 6:
                tile = map.get(location.x-1).get(location.y);// west
                break;
            case 7:
                tile = map.get(location.x-1).get(location.y-1);// north west
                break;
        }
        return tile;
    }

    static boolean[] isInsideMapBounds(Point location) {
        boolean[] inMapBounds = new boolean[8];
        for(int i = 0; i < 8; i++) {
            inMapBounds[i] = false;
        }
        if (inMap(new Point(location.x, location.y - 1))) { //north
            inMapBounds[0] = true;
        }
        if (inMap(new Point(location.x + 1, location.y - 1))) {// north east
            inMapBounds[1] = true;
        }
        if (inMap(new Point(location.x + 1, location.y))) { //east
            inMapBounds[2] = true;
        }
        if (inMap(new Point(location.x + 1, location.y + 1))) { //south east
            inMapBounds[3] = true;
        }
        if (inMap(new Point(location.x, location.y + 1))) {// south
            inMapBounds[4] = true;
        }
        if (inMap(new Point(location.x - 1, location.y + 1))) { // south west
            inMapBounds[5] = true;
        }
        if (inMap(new Point(location.x - 1, location.y))) { // west
            inMapBounds[6] = true;
        }
        if(inMap(new Point(location.x - 1, location.y - 1))) { //north west
            inMapBounds[7] = true;
        }
        return inMapBounds;
    }

    private static boolean inMap(Point location) {
        if (location.x < 0 || location.y < 0 || location.x >= Game.game.map.size.x - 1 || location.y >= Game.game.map.size.y - 1)
        {
            return false;
        }
        return true;
    }

    static int range(int number){
        //number = -1..8
        int rangedNumber = number;
        if (number < 0) {
            rangedNumber = 8+number;
        }
        if(number > 7){
            rangedNumber = number-8;
        }
        return rangedNumber;
    }

    public static class Timer{
        long time;
        long pausedTime;
        public long timePaused;
        boolean paused;
        Timer(){
        }

        public void start(){
            time = System.currentTimeMillis();
        }

        public void pause(){
            pausedTime = System.currentTimeMillis();
            timePaused += pausedTime - time;
            paused = true;
        }
        public void resume(){
            time = System.currentTimeMillis();
            paused = false;
        }

        public long getTime(){
            if(paused) {
                return timePaused;
            }
            else{
                return System.currentTimeMillis() - time + timePaused;
            }
        }
    }

    public static double less(double num){
        if(num < 255){
            return num;
        }
        else{
            return 255.0;
        }
    }

    public static double smallest(double num1, double num2){
        if(num1<=num2) {
            return num1;
        }
        else {
            return num2;
        }
    }

    public static Point moveToDirection(int direction){
        Point point = new Point(0,0);
        switch (direction) {
            case 0:
                point = new Point(0,-1); // north
                break;
            case 1:
                point = new Point(1,-1);// north east
                break;
            case 2:
                point = new Point(1,0);
                break;
            case 3:
                point = new Point(1,1);
                break;
            case 4:
                point = new Point(0,1);
                break;
            case 5:
                point = new Point(-1,1);
                break;
            case 6:
                point = new Point(1,0);
                break;
            case 7:
                point = new Point(-1,-1);
                break;
        }
        return point;
    }


    public static int xyToDirection(double x, double y){
        x = (int)x;
        y = (int)y;
        if(y==1) {
            return 0;
        }
        if(x==1 && y==-1){
            return 1;
        } // north east
        if(x==1){
            return 2;
        } // east
        if(x==1 && y==1){
            return 3;
        } // south east
        if(y==1){
            return 4;
        } // south
        if(x==-1 && y==1){
            return 5;
        } // south west
        if(x==-1){
            return 6;
        } // west
        if(x==-1 && y ==-1){
            return 7;
        } // north west
        return 0;
    }

    public static ArrayList<Point> removeLoops(ArrayList<Point> path){
        ArrayList<Point> itemsToDelete = new ArrayList<>();
        for(int i = 0; i < path.size(); i++){
            for(int j = 0; j < path.size(); j++){
                if(path.get(i).getLocation().equals(path.get(j).getLocation())){
                    for(int k = i; k < j; k++) {
                        itemsToDelete.add(path.get(k));
                    }
                }
            }
        }
        path.removeAll(itemsToDelete);
        return path;
    }

    public static Point MouseToTile(Point mouseLoc){
        return new Point(mouseLoc.x/(Game.game.tileSize),mouseLoc.y/(Game.game.tileSize));
    }
}

