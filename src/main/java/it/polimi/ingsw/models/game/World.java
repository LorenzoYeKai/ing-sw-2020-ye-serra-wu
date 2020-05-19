package it.polimi.ingsw.models.game;

import it.polimi.ingsw.Notifiable;
import it.polimi.ingsw.views.utils.ConsoleMatrix;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World implements Serializable, WorldData {
    Space[][] spaces = new Space[5][5];

    /**
     * Creates an empty world
     */
    public World(Notifiable<SpaceData> onSpaceChanged) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                spaces[i][j] = new Space(onSpaceChanged, i, j);
            }
        }
    }

    public World(World copy){
        this.spaces = new Space[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                this.spaces[i][j] = new Space(copy.spaces[i][j]);
            }
        }
    }

    @Override
    public Space getSpaces(int x, int y) {
        return spaces[y][x];
    }

    @Override
    public SpaceData[][] getSpaceData(){
        return this.spaces;
    }


    /**
     * returns null if not in the world
     */
    public Space pushSpace(Space firstSpace, Space secondSpace){
        int x = -1;
        int y = -1;
        if(firstSpace.getX() == secondSpace.getX()){
            x = firstSpace.getX();
            if(firstSpace.getY() < secondSpace.getY()){
                y = firstSpace.getY() + 2;
            }
            if(firstSpace.getY() > secondSpace.getY()){
                y = firstSpace.getY() - 2;
            }
        }
        if(firstSpace.getY() == secondSpace.getY()){
            y = firstSpace.getY();
            if(firstSpace.getX() < secondSpace.getX()){
                x = firstSpace.getX() + 2;
            }
            if(firstSpace.getX() > secondSpace.getX()){
                x = firstSpace.getX() - 2;
            }
        }
        if(firstSpace.getX() < secondSpace.getX() && firstSpace.getY() < secondSpace.getY()){
            x = firstSpace.getX() + 2;
            y = firstSpace.getY() + 2;
        }
        if(firstSpace.getX() > secondSpace.getX() && firstSpace.getY() > secondSpace.getY()){
            x = firstSpace.getX() - 2;
            y = firstSpace.getY() - 2;
        }
        if(firstSpace.getX() < secondSpace.getX() && firstSpace.getY() > secondSpace.getY()){
            x = firstSpace.getX() + 2;
            y = firstSpace.getY() - 2;
        }
        if(firstSpace.getX() > secondSpace.getX() && firstSpace.getY() < secondSpace.getY()){
            x = firstSpace.getX() - 2;
            y = firstSpace.getY() + 2;
        }
        if(y > -1 && y < 5 && x > -1 && x < 5){
            return this.spaces[x][y];
        }

        return null;
    }

    public String printWorld(List<Player> listOfPlayers){
        String[] levels = new String[]{" ", "1", "2", "3"};
        String dome = "^";

        Map<WorkerData, String> workerSymbols = new HashMap<>();
        String[] symbols = new String[]{"A", "B", "C", "D", "E", "F"};
        int symbolIndex = 0;
        for (PlayerData player : listOfPlayers) {
            for (WorkerData worker : player.getAllWorkers()) {
                workerSymbols.put(worker, symbols[symbolIndex]);
                symbolIndex += 1;
            }
        }

        ConsoleMatrix matrix = ConsoleMatrix.newMatrix(64, 12, false);
        ConsoleMatrix[] columns = matrix.splitHorizontal(new int[]{1, 16, 1, 46});
        ConsoleMatrix[] worldRows = columns[1].splitVertical(new int[]{1, 11});
        ConsoleMatrix world = worldRows[1];

        PrintWriter info = columns[3].getPrintWriter();
        columns[3].setAutoLineBreak(true);

        PrintWriter xCoordinates = worldRows[0].getPrintWriter();
        PrintWriter yCoordinates = columns[0].getPrintWriter();
        columns[0].setAutoLineBreak(true);

        xCoordinates.print("|0 |1 |2 |3 |4 |");
        yCoordinates.print(" ─0─1─2─3─4─");

        // Print row separators
        for (int j = 0; j < 11; j += 2) {
            for (int i = 0; i < 16; ++i) {
                world.setCharacter(i, j, '─');
            }
        }

        // Print column separators
        for (int i = 0; i < 16; i += 3) {
            for (int j = 0; j < 11; ++j) {
                if (j % 2 == 0) {
                    world.setCharacter(i, j, '┼');
                } else {
                    world.setCharacter(i, j, '│');
                }

            }
        }

        // Print map
        for (SpaceData[] row : this.spaces) {
            for (SpaceData space : row) {
                int x = space.getX() * 3 + 1;
                int y = space.getY() * 2 + 1;
                world.setCharacter(x, y, levels[space.getLevel()]);
                if (space.isOccupied()) {
                    if (space.isOccupiedByDome()) {
                        world.setCharacter(x + 1, y, dome);
                    } else {
                        world.setCharacter(x + 1, y, workerSymbols.get(space.getWorkerData()));
                    }
                }
            }
        }

        return matrix.toString();
    }

}


