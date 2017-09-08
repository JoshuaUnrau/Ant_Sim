import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by work on 2016-11-06.
 */
public class Colony {

    public ArrayList<Ant> ants = new ArrayList<>();
    Point location;
    Random random = new Random();
    Color color = new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
    int length;
    int foodStore;
    int index;
    public double TRAVELDISAPATION;
    public double SCENTPREFERENCE;
    //colony is a box
    public Colony(Point _location, int _length, double TD, double SP,int _index){
        location = _location;
        length = _length;
        foodStore = 0;
        TRAVELDISAPATION = TD;
        SCENTPREFERENCE = SP;
        index = _index;
    }

    public void addAnts(int amount){
        for(int i = 0;i<amount;i++) {
            //new Point(Game.game.windowWidth/(2*Game.game.tileSize),Game.game.windowHeight/(2*Game.game.tileSize)
            Ant newAnt = new Ant(location.getLocation(),index);
            ants.add(newAnt);
        }
    }

    public void makeAnts(){
        if(foodStore > 5){
            addAnts(1);
            foodStore -= 6;
        }
    }

    public boolean isInColony(Point _location){
       Rectangle colony = new Rectangle(length,length);
       Point point = new Point(_location.x-location.x,_location.y-location.y);
       if(colony.contains(point)){
           return true;
       }
       return false;
    }

    public void render(Graphics g){
        g.setColor(color);
        g.drawString(""+foodStore,location.x*Game.game.tileSize+20,location.y*Game.game.tileSize-10);
        g.drawString(""+ants.size(),location.x*Game.game.tileSize+20,location.y*Game.game.tileSize+20);
        g.drawRect(location.x*Game.game.tileSize,location.y*Game.game.tileSize,length*Game.game.tileSize,length*Game.game.tileSize);
    }
}
