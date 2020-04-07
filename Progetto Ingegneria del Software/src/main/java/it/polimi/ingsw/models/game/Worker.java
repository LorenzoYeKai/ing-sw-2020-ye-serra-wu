package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.game.rules.ActualRule;

public class Worker {
    private int x;
    private int y;
    private final Player player;
    private final World world;
    private ActualRule rules;

    public Worker(Player player) {
        this.player = player;
        this.world = this.player.game.getWorld();
        rules = this.player.game.getRules();
    }

    public void startTurn(){
        player.getGod().performActions(this);
    }

    /**
     * Uses ActualRule.canMoveThere to check if this worker can move in a particular space according to all the active rules
     */
    public void move(int x, int y) { //Movable spaces display not implemented yet
        if (rules.canMoveThere(this.x, this.y, x, y)) { //Check coordinates validity
            System.out.println("Your worker moved form " + "[" + this.x + "][" + this.y + "] to " + "[" + x + "][" + y + "].");
            victory(x, y); //Check win condition
            this.world.getSpaces(this.x, this.y).removeWorker();
            setPosition(x, y);
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Uses ActualRule.canBuildThere to check if this worker can build in a particular space according to all the active rules
     */
    public void build (int x, int y) { //Buildable spaces display not implemented yet
        if (rules.canBuildThere(this.x, this.y, x, y)) { //Check coordinates validity
            if (world.getSpaces(x, y).getLevel() == 3) {
                world.getSpaces(x, y).setDome();
                System.out.println("Your worker built a dome in " + "[" + x + "][" + y + "].");
            } else { //level > 3 cannot exist due to previous control
                world.getSpaces(x, y).addLevel();
                System.out.println("Your worker built a level " + world.getSpaces(x, y).getLevel() + " block in " + "[" + x + "][" + y + "].");
            }
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Uses ActualRule.winCondition to check if the player wins by moving this worker into a particular space according to all the active rules
     */
    public void victory ( int x, int y){ //This method is called only after checking that the worker can move to that position
        if (rules.winCondition(this.x, this.y, x, y)) {
            this.world.getSpaces(this.x, this.y).removeWorker();
            setPosition(x, y);
            this.player.game.endGame(); //If true the game ends
        }
    }

    public int getY () {
        return y;
    }

    public int getX () {
        return x;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
        this.world.getSpaces(x, y).setWorker(this);
    }

    public Player getPlayer(){
        return this.player;
    }

    public World getWorld(){
        return this.world;
    }

    public ActualRule getRules(){
        return this.rules;
    }

        //abstract void printPosition();
}

