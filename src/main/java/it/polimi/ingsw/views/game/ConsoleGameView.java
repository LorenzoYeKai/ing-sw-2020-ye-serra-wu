package it.polimi.ingsw.views.game;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.views.utils.ConsoleMatrix;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

public class ConsoleGameView implements GameView {
    private final String player;
    private final GameController controller;
    private final PrintStream output;
    private final Space[] spaces = new Space[World.SIZE * World.SIZE];
    private final List<String> allPlayers;
    private final Map<String, GodType> playerGods;
    private GameStatus currentStatus = GameStatus.SETUP;

    public ConsoleGameView(String player,
                           List<String> allPlayers,
                           GameController controller,
                           PrintStream output) {
        this.player = player;
        this.controller = controller;
        this.output = output;
        this.allPlayers = allPlayers;
        this.playerGods = new HashMap<>();
    }

    public void executeAction(String input)
            throws NotExecutedException, IOException {
        Scanner scanner = new Scanner(input);

        String command = scanner.next().toUpperCase();
        switch (command) {
            case "END" -> this.controller.nextTurn();
            case "GOD" -> this.controller.setPlayerGod(this.player, GodType.valueOf(scanner.next().toUpperCase()));
            case "SELECT" -> this.controller.selectWorker(scanner.nextInt());
            default -> {
                WorkerActionType type;
                int x;
                int y;
                try {
                    type = WorkerActionType.valueOf(command);
                    x = scanner.nextInt();
                    y = scanner.nextInt();
                    if (!World.isInWorld(x, y)) {
                        throw new NotExecutedException("Invalid coordinates");
                    }
                } catch (Exception exception) {
                    this.output.println("Exception: " + exception);
                    return;
                }

                try {
                    this.controller.workerAction(this.player, type, x, y);
                } catch (NotExecutedException | IOException e) {
                    this.output.println("Command failed: " + e);
                }
            }
        }
    }

    @Override
    public void notifyGameStatus(GameStatus status) {
        this.currentStatus = status;
        switch (status) {
            case SETUP -> this.output.println("Setup phase");
            case CHOOSING_GODS -> this.output.println("Choosing gods, use `GOS [GOD NAME]` to choose god");
            case PLACING -> this.output.println("Placing workers");
            case PLAYING -> this.output.println("Game started");
            case ENDED -> this.output.println("Game ended");
            default -> throw new InternalError("Not implemented yet");
        }
    }

    @Override
    public void notifyAvailableGods(Collection<GodType> availableGods) {
        this.output.println("Available gods: ");
        availableGods.forEach(this.output::println);
    }

    @Override
    public void notifyPlayerGods(Map<String, GodType> playerAndGods) {
        this.playerGods.clear();
        this.playerGods.putAll(playerAndGods);
    }

    @Override
    public void notifySpaceChange(Space space) {
        this.spaces[space.getPosition().getY() * World.SIZE + space.getPosition().getX()] = space;
        this.printMap();
    }

    @Override
    public void notifyPlayerTurn(String player) {
        if (this.player.equals(player)) {
            this.output.println("Your turn!");
        } else {
            this.output.println("Turn of " + player);
        }
    }

    @Override
    public void notifyPlayerDefeat(String player) {
        this.output.println(player + " has lost!");
    }

    private void printMap() {
        String[] levels = new String[]{" ", "1", "2", "3"};
        String dome = "^";

        Map<String, String> workerSymbols = new HashMap<>();
        String[] symbols = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        int symbolIndex = 0;
        for (String player : this.allPlayers) {
            for (int i = 0; i < 2; ++i) {
                workerSymbols.put(player + i, symbols[symbolIndex]);
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

        if (this.currentStatus == GameStatus.PLACING) {
            info.println("Now is placing phase, move your workers");
        } else {
            info.println("Move / build with your workers");
        }

        for (String player : allPlayers) {
            String godName = "No God";
            GodType god = playerGods.get(player);
            if (god != null) {
                godName = god.toString();
            }
            info.println("Player " + player + "(" + godName + ")'s workers: ");
            info.println("0 -> " + workerSymbols.get(player + "0"));
            info.println("1 -> " + workerSymbols.get(player + "1"));
        }

        info.println("Command: `[worker index] [place/move/build/build_dome] [X] [Y]`");
        info.println("Example: `0 move 0 3`");

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
        for (Space space : this.spaces) {
            int x = space.getPosition().getX() * 3 + 1;
            int y = space.getPosition().getY() * 2 + 1;
            world.setCharacter(x, y, levels[space.getLevel()]);
            if (space.isOccupied()) {
                if (space.isOccupiedByDome()) {
                    world.setCharacter(x + 1, y, dome);
                } else {
                    WorkerData worker = space.getWorkerData();
                    world.setCharacter(x + 1, y, workerSymbols.get(worker.getPlayer() + worker.getIndex()));
                }
            }
        }

        this.output.print(matrix.toString());
    }
}
