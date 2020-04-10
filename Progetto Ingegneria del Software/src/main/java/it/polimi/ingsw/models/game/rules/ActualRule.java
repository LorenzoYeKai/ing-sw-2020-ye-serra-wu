package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Used by Worker
 * Merges all the active rules
 */
public class ActualRule {

    private Map<String, BiPredicate<Space, Space>> movementRules;
    private Map<String, BiPredicate<Space, Space>> buildRules;
    private Map<String, BiPredicate<Space, Space>> winConditions;
    private int domeLevel;
    private Rule defaultRules;
    private GodPower godPower;
    private World world;

    /**
     * The constructor creates the default rule
     */
    public ActualRule(World world){
        this.movementRules = new HashMap<>();
        this.buildRules  =new HashMap<>();
        this.winConditions = new HashMap<>();
        this.domeLevel = 3;
        this.world = world;
        this.defaultRules = new Rule(world);
        this.godPower = new GodPower(world);
        resetDefaultRules();
    }

    /**
     * Merges all the canMoveThere methods of all the active rules
     * Used in Worker.move
     */
    public boolean canMoveThere(Space originalSpace, Space targetSpace){
        for(BiPredicate<Space, Space> r : this.movementRules.values()){
            if(!r.test(originalSpace, targetSpace)){
                return false;
            }
        }
        return true;
    }

    /**
     * Merges all the canBuildThere methods of all the active rules
     * Used in Worker.build
     */
    public boolean canBuildThere(Space originalSpace, Space targetSpace){
        for(BiPredicate<Space, Space> r : this.buildRules.values()){
            if(!r.test(originalSpace, targetSpace)){
                return false;
            }
        }
        return true;
    }

    public ArrayList<Space> getAvailableSpaces(Space originalSpace){
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(canMoveThere(originalSpace, this.world.getSpaces(i, j))){
                    availableSpaces.add(this.world.getSpaces(i, j));
                }
            }
        }
        return availableSpaces;
    }

    public ArrayList<Space> getBuildableSpaces(Space originalSpace){
        ArrayList<Space> buildableSpaces = new ArrayList<Space>();
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(canBuildThere(originalSpace, this.world.getSpaces(i, j))){
                    buildableSpaces.add(this.world.getSpaces(i, j));
                }
            }
        }
        return buildableSpaces;
    }

    /**
     * Merges all the winCondition methods of all the active rules
     * Used in Worker.victory
     */
    public boolean winCondition(Space originalSpace, Space targetSpace){
        for(BiPredicate<Space, Space> r : this.winConditions.values()){
            if(!r.test(originalSpace, targetSpace)){
                return false;
            }
        }
        return true;
    }

    private void resetDefaultRules(){
        this.movementRules.clear();
        this.buildRules.clear();
        this.winConditions.clear();
        this.movementRules.put("defaultIsNeighbor", Rule::defaultIsNeighbor);
        this.movementRules.put("defaultLevelDifference", Rule::defaultLevelDifference);
        this.movementRules.put("defaultIsOccupiedByWorker", Rule::defaultIsOccupiedByWorker);
        this.movementRules.put("defaultIsOccupiedByDome", Rule::defaultIsOccupiedByDome);
        this.movementRules.put("defaultIsInWorld", Rule::defaultIsInWorld);
        this.buildRules.put("defaultIsNeighbor", Rule::defaultIsNeighbor);
        this.buildRules.put("defaultIsInWorld", Rule::defaultIsInWorld);
        this.buildRules.put("defaultIsOccupied", Rule::defaultIsOccupied);
        this.winConditions.put("defaultWinCondition", Rule::defaultWinCondition);
    }

    public Map<String, BiPredicate<Space, Space>> getMovementRules(){
        return this.movementRules;
    }

    public Map<String, BiPredicate<Space, Space>> getBuildRules(){
        return this.buildRules;
    }

    public Map<String, BiPredicate<Space, Space>> getWinConditions(){
        return this.winConditions;
    }

    public GodPower getGodPower(){
        return this.godPower;
    }

    public int getDomeLevel(){
        return this.domeLevel;
    }

    public void addMovementRules(String key, BiPredicate<Space, Space> value){
        this.movementRules.put(key, value);
    }

    public void addBuildRules(String key, BiPredicate<Space, Space> value){
        this.buildRules.put(key, value);
    }

    public void addWinConditions(String key, BiPredicate<Space, Space> value){
        this.winConditions.put(key, value);
    }

    public void setDomeLevel(int level){
        this.domeLevel = level;
    }

}
