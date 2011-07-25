package it.polimi.ingsw.models.game;

import java.io.Serializable;

public class WorkerData implements Serializable {
    private final String player;
    private final int index;

    public WorkerData(Player player, int index) {
        this.player = player.getName();
        this.index = index;
    }
    public String getPlayer() { return this.player; }
    public int getIndex() { return this.index; }
}
