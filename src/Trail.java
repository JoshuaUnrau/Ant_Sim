import java.awt.*;

/**
 * Created by work on 2016-10-12.
 */
public class Trail {
    public double amount = 0;
    public Trail(int _amount){
        amount = _amount;
    }

    public void evaporate(){
        amount*=Game.game.evapScroller.getValue();
        if(amount < 0.00001){
            amount = 0;
        }
    }
}
