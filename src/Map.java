import java.awt.*;
import java.util.ArrayList;

/**
 * Created by work on 2016-10-12.
 */
public class Map {
    //stores all of the tiles in the map
    public Point size;
    public int tilesize = Game.game.tileSize;
    ArrayList<ArrayList<Tile>> map = new ArrayList<ArrayList<Tile>>();
    public Map(Point _size){
        size = _size;
        for(int i = 0; i < (int)(size.getX()); i++){
            ArrayList<Tile> mapRow = new ArrayList<Tile>();
            for(int j = 0; j < (int)(size.getY()); j++){
                Tile mapTile = new Tile(new Point(i,j));
                mapRow.add(mapTile);
            }
            map.add(mapRow);
        }
    }

    public Tile[] search(Point location){
        Tile[] tiles = new Tile[8];
        //coordinates in the y axis are flipped
        boolean[] insideMap = Utils.isInsideMapBounds(location);
        for(int i = 0; i<8; i++){
            if(insideMap[i]){
                tiles[i] = Utils.getMapRelativeTile(location,i);
            }
        }
        return tiles;
    }

    public class Tile {
        //stores things that are on the tile
        public Point location;
        public int food;
        public ArrayList<Ant> ants = new ArrayList<>();
        public Trail searchTrail;
        public Trail homeTrail;
        public Obstacle obstacle;

        public Tile(Point _location){
            location = _location;
            food = 0;
            searchTrail = new Trail(0);
            obstacle = new Obstacle(false,false);
        }

        public void update(){
            searchTrail.evaporate();
            ants.clear();
        }

        public void render(Graphics g){
            if(food > 0) {
                g.setColor(Color.ORANGE);
                g.drawString(food+"",(int)location.getX()*tilesize,(int)location.getY()*tilesize);
                g.drawRect((int)location.getX()*tilesize,(int)location.getY()*tilesize,tilesize,tilesize);
            }
            if(searchTrail.amount != 0) {
                Color color = new Color(255, 0, 0, (int)Utils.less((255 * searchTrail.amount/5)));
                g.setColor(color);
                g.fillRect((int) location.getX() * tilesize, (int) location.getY() * tilesize, tilesize, tilesize);
            }
            if(obstacle.active){
                Color color = new Color(137, 137, 137, 255);
                g.setColor(color);
                g.fillRect((int) location.getX() * tilesize, (int) location.getY() * tilesize, tilesize, tilesize);
            }
        }
    }

    public class Obstacle {
        //stores things that are on the tile
        public boolean active;

        public Obstacle( boolean _active, boolean _lethal){
            active = _active;
        }
    }
}
