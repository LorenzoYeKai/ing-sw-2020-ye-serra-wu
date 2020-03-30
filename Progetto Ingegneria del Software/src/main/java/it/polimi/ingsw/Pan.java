package it.polimi.ingsw;

public class Pan extends Worker {

    public Pan(Player player){
        super(player);
    }

    @Override
    public void victory(int x, int y){
        if(World.levelDifference(this.getX(), this.getY(), x ,y) >= 2){
            this.setPosition(x, y);
            this.player.game.endGame();
        }
        else super.victory(x, y);
    }
}
