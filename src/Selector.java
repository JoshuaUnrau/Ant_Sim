import com.sun.tools.javac.util.ArrayUtils;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by work on 2016-11-13.
 */
public class Selector {
    //Selects for colonies where that find the most food in 1 min
    final int coloniesAmount = 5;
    public Species[] genepool = new Species[5];
    public int[] colonyScores = new int[coloniesAmount];
    int index = 0;
    public Selector(){
        for(int i = 0; i < coloniesAmount; i++){
            genepool[i] = new Species(.97,.97,50,0.0001);
        }
    }

    public void select(){
        //sort scores from last to first
        Arrays.sort(colonyScores);

        //pick winners

        //randomise winners
        for(int i = 0; i < coloniesAmount/2; i++){
            genepool[i].randomise();
        }

    }

    public Species selectNext(){
        index++;
        if(index+1 > coloniesAmount){
            select();
            index = 0;
        }
        genepool[index].randomise();
        Game.game.paused = false;
        return genepool[index];
    }

    class Species {
        double EVAP_RATE_HOME;
        double EVAP_RATE_FOOD;
        double TRAVEL_DISAPATION;
        double SCENT_PREFERENCE;
        public Species(double ERH, double ERF, double TD, double SP){
            EVAP_RATE_HOME = ERH;
            EVAP_RATE_FOOD = ERF;
            TRAVEL_DISAPATION = TD;
            SCENT_PREFERENCE = SP;
        }

        public void randomise(){
            Random r = new Random();
            EVAP_RATE_HOME+=r.nextDouble()*0.2-0.1;
            EVAP_RATE_FOOD+=r.nextDouble()*0.2-0.1;
            TRAVEL_DISAPATION+=r.nextDouble()*10-5;
            SCENT_PREFERENCE+=r.nextDouble()*0.0002-0.0001;
        }
    }
}

