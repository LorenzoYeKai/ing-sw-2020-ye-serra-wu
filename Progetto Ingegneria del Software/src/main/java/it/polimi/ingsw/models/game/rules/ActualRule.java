package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.World;

import java.util.ArrayList;

/**
 * Used by Worker
 * Merges all the active rules
 */
public class ActualRule {

    private ArrayList<Rule> ruleSets;

    /**
     * The constructor creates the default rule
     */
    public ActualRule(World world){
        this.ruleSets = new ArrayList<Rule>();
        ruleSets.add(new DefaultRule(world));
    }

    /**
     * Merges all the canMoveThere methods of all the active rules
     * Used in Worker.move
     */
    public boolean canMoveThere(int currentX, int currentY, int x, int y){
        for(Rule r : this.ruleSets){
            if(!r.canMoveThere(currentX, currentY, x, y)){
                return false;
            }
        }
        return true;
    }

    /**
     * Merges all the canBuildThere methods of all the active rules
     * Used in Worker.build
     */
    public boolean canBuildThere(int currentX, int currentY, int x, int y){
        for(Rule r : this.ruleSets){
            if(!r.canBuildThere(currentX, currentY, x, y)){
                return false;
            }
        }
        return true;
    }

    /**
     * Merges all the canMove methods of all the active rules
     */
    public boolean canMove(int currentX, int currentY){
        for(Rule r : this.ruleSets){
            if(!r.canMove(currentX, currentY)){
                return false;
            }
        }
        return true;
    }

    /**
     * Merges all the winCondition methods of all the active rules
     * Used in Worker.victory
     */
    public boolean winCondition(int currentX, int currentY, int x, int y){
        for(Rule r : this.ruleSets){
            if(!r.winCondition(currentX, currentY, x, y)){
                return false;
            }
        }
        return true;
    }

    public ArrayList<Rule> getRuleSets(){
        return this.ruleSets;
    }

}
