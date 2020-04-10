package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.ArrayList;

public class Worker implements WorkerData {
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
        player.getGod().workerActionOrder(this);
    }

    /**
     * Uses ActualRule.canMoveThere to check if this worker can move in a particular space according to all the active rules
     */
    public void move(Space targetSpace) {
        victory(targetSpace); //Check win condition
        this.world.getSpaces(this.x, this.y).removeWorker();
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
        if (rules.winCondition(world.getSpaces(this.x, this.y), targetSpace)) {
            this.world.getSpaces(this.x, this.y).removeWorker();
            setPosition(targetSpace);
            this.player.game.endGame(); //If true the game ends
        }
    }

    public int getY () {
        return y;
    }

    public int getX () {
        return x;
    }

    public void setPosition(Space targetSpace){
        this.x = targetSpace.getX();
        this.y = targetSpace.getY();
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

        //abstract void printPosition();
}

