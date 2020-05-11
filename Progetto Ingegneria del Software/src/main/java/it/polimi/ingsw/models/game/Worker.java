package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.ArrayList;

public class Worker implements WorkerData {
    private Space currentSpace;
    private Space initialSpace;
    private final Player player;
    private final World world;
    private ActualRule rules;
    private int index;

    public Worker(Player player, int index) {
        this.player = player;
        this.world = this.player.getGame().getWorld();
        this.rules = this.player.getGame().getRules();
        this.currentSpace = null;
        this.initialSpace = null;
        this.index = index;
    }

    public Worker(Worker copy){
        this.player = copy.player;
        this.world = copy.player.getGame().getPreviousWorld();
        this.rules = copy.rules;
        this.currentSpace = this.world.getSpaces(copy.currentSpace.getX(), copy.currentSpace.getY());
        this.initialSpace = null;
        this.index = copy.index;
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

    private void force(Space s){
        this.currentSpace = s;
        s.setWorker(this);
    }

    public void swap(Worker opponent, Space targetSpace){
        this.move(targetSpace);
        opponent.force(this.previousSpace());
    }

    public void push(Worker opponent, Space targetSpace){
        opponent.force(this.world.pushSpace(this.currentSpace, targetSpace));
        this.move(targetSpace);
    }

    public boolean hasMoved(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Worker w = this.player.getGame().getPreviousWorld().getSpaces(i, j).getWorker();
                if(w != null){
                    if(this.player.equals(w.player) && this.index == w.index && (this.getX() != w.getX() || this.getY() != w.getY())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasBuiltBlock(){ //this will be called only when the worker is already unchangeable
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(player.getGame().getPreviousWorld().getSpaces(i, j).getLevel() != player.getGame().getWorld().getSpaces(i, j).getLevel()){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasBuiltDome(){ //this will be called only when the worker is already unchangeable
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(!player.getGame().getPreviousWorld().getSpaces(i, j).isOccupiedByDome() && player.getGame().getWorld().getSpaces(i, j).isOccupiedByDome()){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasBuilt(){
        return hasBuiltBlock() || hasBuiltDome();
    }

    public Space previousSpace(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Worker w = this.player.getGame().getPreviousWorld().getSpaces(i, j).getWorker();
                if(w != null) {
                    if (this.player.equals(w.player) && this.index == w.index && (this.getX() != w.getX() || this.getY() != w.getY())) {
                        //has moved
                        return this.player.getGame().getWorld().getSpaces(i, j);
                    }
                }
            }
        }
        //has NOT moved
        return this.currentSpace;
    }

    public Space previousBuild(){ //this will be called only after the selected worker has built
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(player.getGame().getPreviousWorld().getSpaces(i, j).getLevel() != player.getGame().getWorld().getSpaces(i, j).getLevel()){
                    return this.player.getGame().getWorld().getSpaces(i, j);
                }
            }
        }
        return null;
    }

}

