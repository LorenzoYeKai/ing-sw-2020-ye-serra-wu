package it.polimi.ingsw;

public class Pan extends Worker {

    public Pan(Player player, World world){
        super(player, world);
    }

    @Override
    public void victory(int x, int y){
        if(this.getWorld().levelDifference(this.getX(), this.getY(), x ,y) >= 2){
            this.setPosition(x, y);
            this.getPlayer().game.endGame();
        }
        else super.victory(x, y);
    }
}
