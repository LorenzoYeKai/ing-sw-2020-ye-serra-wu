package it.polimi.ingsw;

import java.util.ArrayList;

public class ActualRule {

    private ArrayList<Rule> ruleSets;

    public ActualRule(int numberOfPlayers, World world){
        this.ruleSets = new ArrayList<Rule>();
        for(int i = 0; i < numberOfPlayers; i++){
            ruleSets.add(new DefaultRule(world));
        }
    }

    public boolean canMoveThere(int currentX, int currentY, int x, int y){
        for(Rule r : this.ruleSets){
            if(!r.canMoveThere(currentX, currentY, x, y)){
                return false;
            }
        }
        return true;
    }

    public boolean canBuildThere(int currentX, int currentY, int x, int y){
        for(Rule r : this.ruleSets){
            if(!r.canBuildThere(currentX, currentY, x, y)){
                return false;
            }
        }
        return true;
    }

    public boolean canMove(int currentX, int currentY){
        for(Rule r : this.ruleSets){
            if(!r.canMove(currentX, currentY)){
                return false;
            }
        }
        return true;
    }

    public void setRuleSets(int index, Rule ruleSet){
        this.ruleSets.set(index, ruleSet);
    }
}
