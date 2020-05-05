package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.ArrayList;

public class Worker implements WorkerData {
    private Space currentSpace;
    private Space initialSpace;
    private final Player player;
    private final World world;
    private ActualRule rules;

    public Worker(Player player) {
        this.player = player;
        this.world = this.player.getGame().getWorld();
        this.rules = this.player.getGame().getRules();
        this.currentSpace = null;
        this.initialSpace = null;
    }

    /**
     * Used by worker to move
     * The move conditions checked are in the controller
     */
    public void move(Space targetSpace) {
        victory(targetSpace); //Check win condition
        setPosition(targetSpace);
    }

    /**
     * Used by worker to build
     * The build conditions are checked in the controller
     */
    public void buildBlock(Space targetSpace) {
        targetSpace.addLevel();
    }


    /**
     * Used by worker to build a dome
     * The build conditions are checked in the controller
     */
    public void buildDome(Space targetSpace) {
        targetSpace.setDome();
    }

    /**
     * Checks if a movement is satisfying a win condition
     */
    public void victory(Space targetSpace) { //This method is called only after checking that the worker can move to that position
        if (this.rules.winCondition(this.currentSpace, targetSpace)) {
            this.setPosition(targetSpace);
            System.out.println("Victory!!!!"); //For tests
            this.player.getGame().announceVictory(this.player); //If true the game ends
        }
    }

    /**
     * Computes the available spaces for movement
     */
    public ArrayList<Space> computeAvailableSpaces(){
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(rules.canMoveThere(this.currentSpace, this.world.getSpaces(i, j))){
                    availableSpaces.add(this.world.getSpaces(i, j));
                }
            }
        }
        return availableSpaces;
    }

    /**
     * Computes the spaces where the worker is allowed to build a block
     */
    public ArrayList<Space> computeBuildableSpaces(){
        ArrayList<Space> buildableSpaces = new ArrayList<Space>();
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(rules.canBuildThere(this.currentSpace, this.world.getSpaces(i, j))){
                    buildableSpaces.add(this.world.getSpaces(i, j));
                }
            }
        }
        return buildableSpaces;
    }

    /**
     * Computes the spaces where the worker is allowed to build a dome
     */
    public ArrayList<Space> computeDomeSpaces(){
        ArrayList<Space> domeSpaces = new ArrayList<Space>();
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(rules.canBuildDomeThere(this.currentSpace, this.world.getSpaces(i, j))){
                    domeSpaces.add(this.world.getSpaces(i, j));
                }
            }
        }
        return domeSpaces;
    }

    public Space getCurrentSpace() {
        return this.currentSpace;
    }

    @Override
    public Space getFirstBuild(){
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Space getInitialSpace(){
        return this.initialSpace;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public World getWorld(){
        return this.world;
    }

    public ActualRule getRules() {
        return this.rules;
    }

    public int getX () {
        return currentSpace.getX();
    }

    public int getY () {
        return currentSpace.getY();
    }

    private void setPosition(Space targetSpace) {
        this.initialSpace = this.currentSpace;
        if (this.currentSpace != null) {
            this.currentSpace.removeWorker();
        }
        this.currentSpace = targetSpace;
        targetSpace.setWorker(this);
    }

    public void setStartPosition(Space targetSpace){
        if(this.currentSpace == null /*&& !this.player.isDefeated()*/){
            this.currentSpace = targetSpace;
            targetSpace.setWorker(this);
        }
    }
}

