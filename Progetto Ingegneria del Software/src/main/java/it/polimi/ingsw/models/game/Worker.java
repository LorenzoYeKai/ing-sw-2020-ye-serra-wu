package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.ArrayList;

public class Worker implements WorkerData {
    private Space currentSpace;
    private final Player player;
    private final World world;
    private ActualRule rules;
    private ArrayList<Space> availableSpaces;

    public Worker(Player player) {
        this.player = player;
        this.world = this.player.game.getWorld();
        rules = this.player.game.getRules();
        this.availableSpaces = new ArrayList<Space>();
    }

    public void startTurn(){
        player.getGod().workerActionOrder(this);
    }

    /**
     * Uses ActualRule.canMoveThere to check if this worker can move in a particular space according to all the active rules
     */
    public void move(Space targetSpace) {
        victory(targetSpace); //Check win condition
        this.currentSpace.removeWorker();
        setPosition(targetSpace);
    }

    /**
     * Uses ActualRule.canBuildThere to check if this worker can build in a particular space according to all the active rules
     */
    public void buildBlock(Space targetSpace) {
        targetSpace.addLevel();
    }

    public void buildDome(Space targetSpace){
        targetSpace.setDome();
    }

    /**
     * Uses ActualRule.winCondition to check if the player wins by moving this worker into a particular space according to all the active rules
     */
    public void victory (Space targetSpace){ //This method is called only after checking that the worker can move to that position
        if (rules.winCondition(this.currentSpace, targetSpace)) {
            this.currentSpace.removeWorker();
            setPosition(targetSpace);
            this.player.game.endGame(); //If true the game ends
        }
    }

    public int getX () {
        return currentSpace.getX();
    }

    public int getY () {
        return currentSpace.getY();
    }

    public Space getCurrentSpace(){
        return this.currentSpace;
    }

    public void setPosition(Space targetSpace){
        this.currentSpace = targetSpace;
        targetSpace.setWorker(this);
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


    /**
     * This will be called at the beginning of each turn
      */
    public void computeAvailableSpaces(){
        this.availableSpaces.clear();
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(rules.canMoveThere(this.currentSpace, this.world.getSpaces(i, j))){
                    availableSpaces.add(this.world.getSpaces(i, j));
                }
            }
        }
    }

    public ArrayList<Space> getAvailableSpaces(){
        return this.availableSpaces;
    }
}

