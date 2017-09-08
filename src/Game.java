import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.embed.swing.JFXPanel;
import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import javax.swing.*;
/**
 * Main class for the game 
 */

//TODO: Fix colony array out of bound exception (prolly fix lag)
public class Game extends JFrame
{
    public Map map;
    public static Game game;
    final JFXPanel fxPanel = new JFXPanel(); //need for audio

    public int AntAmount = 0;

    boolean isRunning = true;
    boolean paused = true;
    boolean reset = false;
    boolean moveFrame = false;
    double FOODEVAP = 0.99;
    double TRAVELDISAPATION;
    double SCENTPREFERENCE;
    int fps = 30;
    int windowWidth = 1250;
    int windowHeight = 750;
    int tileSize = 8;
    int tick = 0;
    Utils.Timer timer;
    public static DoubleScroller evapScroller = new DoubleScroller("Scent evaporation ",0,1,0.001,.99);
    public static DoubleScroller antScroller = new DoubleScroller("Amount of Ants  = ",1,2000,1,100);
    public static DoubleScroller travelDisipation = new DoubleScroller("travel disipation = ",0,10000,1,750);
    public  static DoubleScroller scentPreference = new DoubleScroller("Scent preference = ",0,1,0.0001,0.0001);
    public static DoubleScroller tileScroller = new DoubleScroller("Tile size = ",1,36,1,8);
    public static DoubleScroller foodValue = new DoubleScroller("Food amount = ",1,9999,1,10);
    static Button resetButton = new Button(" Reset ");
    static Button startButton = new Button(" Start ");
    String death = "/Users/work/Downloads/antdeath.wav";
    static Panel sliderPanel = new Panel();

    //Media hit = new Media(new File(death).toURI().toString());

    BufferStrategy bs;

    Point mouseLocation = new Point(0,0);
    Insets insets;

    ArrayList<Colony> colonies = new ArrayList<>();

    public static void main(String[] args)
    {
        game = new Game();
        game.run();
        System.exit(0);
    }
    /**
     * This method starts the game and runs it in a loop 
     */
    public void run()
    {
        initialize();
        while(isRunning)
        {
            game.requestFocus();
            if(reset) {
                reset();
                reset=false;
            }
            long time = System.currentTimeMillis();
            if(!paused || moveFrame) {
                update();
                moveFrame = false;
                draw();
                tick++;
            }
            //  delay for each frame  -   time it took for one frame
            time = (1000 / fps) - (System.currentTimeMillis()-time);
            if (time > 0)
            {
                try{
                    Thread.sleep(time);
                }
                catch(Exception e){}
            }
            /*if(tick > 3500){
                paused = true;
                System.out.println(colonies.get(0).foodStore);
                //System.out.println(timer.getTime());
                reset();
            }*/
        }
        setVisible(false);
    }

    public void reset(){
        tileSize = (int)tileScroller.getValue();
        map = new Map(new Point(windowWidth/tileSize,windowHeight/tileSize));
        colonies.clear();
        timer = new Utils.Timer();
        timer.start();
        timer.pause();
        AntAmount = (int)antScroller.getValue();
        TRAVELDISAPATION = travelDisipation.getValue();
        SCENTPREFERENCE = scentPreference.getValue();
        FOODEVAP = evapScroller.getValue();
        paused = false;
    }
    /**
     * This method will set up everything need for the game to run 
     */
    void initialize()
    {
        setTitle("Ant Sim");
        setSize(windowWidth, windowHeight);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        sliderPanel.setLayout(new GridLayout(0,1));
        sliderPanel.add(evapScroller);
        sliderPanel.add(antScroller);
        sliderPanel.add(travelDisipation);
        sliderPanel.add(scentPreference);
        sliderPanel.add(tileScroller);
        sliderPanel.add(foodValue);
        insets = getInsets();
        setSize(insets.left + windowWidth + insets.right,
                    insets.top + windowHeight + insets.bottom);
        bs = this.getBufferStrategy();
        setFocusable(true);
        game.add(sliderPanel);
        game.add(sliderPanel,BorderLayout.NORTH);
        addMouseListener(new mouseListener());
        addMouseMotionListener(new mouseListener());
        addKeyListener(new keyListener());
        reset();
    }

    public class keyListener extends KeyAdapter{
        public void keyTyped(KeyEvent e) {
            System.out.println("you typed a key");
        }

        @Override
        public void keyPressed(KeyEvent e) {
            char keyChar = e.getKeyChar();
            if(keyChar=='r'){
                reset = true;
            }
            if(keyChar=='p'){
                paused = !paused;
                if(paused) {
                    timer.pause();
                }
                else{
                    timer.resume();
                }
            }
            if(keyChar=='f'){
                moveFrame = true;
            }
            if(keyChar=='n'){
                int index = colonies.size();
                Colony newColony = new Colony(Utils.MouseToTile(mouseLocation),7,TRAVELDISAPATION,0.0001,index);
                colonies.add(newColony);
                colonies.get(index).addAnts(AntAmount);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public class mouseListener extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent e)
        {
            mouseLocation=e.getPoint();
            int mouseX = (int)e.getPoint().getX();
            int mouseY = (int)e.getPoint().getY();
            if(SwingUtilities.isRightMouseButton(e)){
                //Ant newAnt = new Ant(new Point(mouseX/tileSize,mouseY/tileSize));
                //ants.add(newAnt);
                game.map.map.get(mouseX/tileSize).get(mouseY/tileSize).food = (int)foodValue.getValue();
            }
            else {
                game.map.map.get(mouseX/tileSize).get(mouseY/tileSize).obstacle.active = !game.map.map.get(mouseX/tileSize).get(mouseY/tileSize).obstacle.active;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e){
            int mouseX = (int)e.getPoint().getX();
            int mouseY = (int)e.getPoint().getY();
            if(SwingUtilities.isRightMouseButton(e)){
                //Ant newAnt = new Ant(new Point(mouseX/tileSize,mouseY/tileSize));
                //ants.add(newAnt);
                game.map.map.get(mouseX/tileSize).get(mouseY/tileSize).food = (int)foodValue.getValue();
            }
            else {
                game.map.map.get(e.getX() / tileSize).get(e.getY() / tileSize).obstacle.active = true;
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            //this is never called
            mouseLocation = e.getPoint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
        }
    }

    /**
     * This method updates all things before drawing begins
     **/
    private void update() {
        for(ArrayList<Map.Tile> list: map.map){
            for(Map.Tile tile: list){
                tile.update();
            }
        }
        try {
            for(Colony colony: colonies) {
                ArrayList<Ant>deadAnts=new ArrayList<>();
                for (Ant ant : colony.ants) {
                    ant.updateStatus();
                    if(ant.health < 0){
                        deadAnts.add(ant);
                        Utils.pointToTile(ant.location).food += 3;
                        //MediaPlayer mediaPlayer = new MediaPlayer(hit);
                        //mediaPlayer.play()
                    }
                }
                colony.ants.removeAll(deadAnts);
            }
            for(Colony colony: colonies) {
                colony.makeAnts();
                for (Ant ant : colony.ants) {
                    if (ant.hasFood) {
                        if (ant.atHome) {
                            ant.act.dropOffFood();
                        } else {
                            ant.move.toHome();
                        }
                    } else {
                        ant.search.forFood();
                        ant.search.forEnemies();
                        if(ant.foundEnemy) {
                            ant.act.fightEnemy(); //stops ant, removes one health from other ant (must be adjacent)
                        }
                        else if (ant.foundFood) {
                            ant.move.toFood();
                        } else {
                            ant.move.explore();
                        }
                    }
                }
            }
        }
        catch (ConcurrentModificationException e){
            System.out.println("Something fucked up: "+e);
        }
    }
    /**
     * This method draws everything
     */
    void draw()
    {
        BufferStrategy bs = getBufferStrategy();
        if (bs== null){
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        //clear map
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, windowWidth, windowHeight);
        g.setColor(Color.BLACK);
        for(ArrayList<Map.Tile> list: map.map){
            for(Map.Tile tile: list){
                tile.render(g);
            }
        }
        try {
            for(Colony colony: colonies){
                colony.render(g);
                g.drawString(colony.ants.size()+"",50,50);
                g.drawString(colony.foodStore+"",50,65);
                for (Ant ant : colony.ants) {
                    ant.render(g);
                }
            }
        }catch (ConcurrentModificationException e){
            System.out.println("Something fucked up: "+e);
        }
        g.setColor(Color.BLACK);
        //g.drawString("Time: "+(timer.getTime())/1000,50,80);
        g.drawString("FPS: "+fps,50,95);
        g.drawString("Ticks: "+tick,50,110);
        if(paused){
            g.drawString("Paused",windowWidth/2,windowHeight/2);
        }
        g.dispose();
        bs.show();
    }
}