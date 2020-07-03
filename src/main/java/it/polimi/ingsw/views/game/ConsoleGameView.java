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
    private final boolean iAmChallenger;
    private int numberOfAvailableGods = 0;
    private GameStatus currentStatus = GameStatus.SETUP;
    private boolean isMyTurn;
    private boolean workerHasBeenSelected;



    public ConsoleGameView(String player,
                           List<String> allPlayers,
                           GameController controller,
                           PrintStream output) throws NotExecutedException, IOException {
        this.player = player;
        this.controller = controller;
        this.output = output;
        this.allPlayers = allPlayers;
        this.playerGods = new HashMap<>();
        this.iAmChallenger = this.allPlayers.get(0).equals(this.player);

        this.controller.joinGame(this.player, this);
        this.output.println("Connected to game");

        for (int y = 0; y < World.SIZE; ++y) {
            for (int x = 0; x < World.SIZE; ++x) {
                spaces[y * World.SIZE + x] = new Space(x, y);
            }
        }
    }

    public void showHelp() {
        if(!this.isMyTurn && (!this.iAmChallenger || this.currentStatus != GameStatus.SETUP)) {
            this.output.println("Wait for your turn.");
            return;
        }
        switch (this.currentStatus) {
            case SETUP -> {
                if(this.iAmChallenger) {
                    if(numberOfAvailableGods != this.allPlayers.size()) {
                        this.output.println("Add gods using `add_god [GOD NAME]`");
                    }
                    else {
                        this.output.println("Choose first player using `set_first [PLAYER NAME]`");
                    }
                }
                else {
                    this.output.println("Wait for the challenger.");
                }
            }
            case CHOOSING_GODS -> {
                this.output.println("Choose your god using `god [GOD NAME]`");
                this.output.println("Finish choosing god: `end turn`");
            }
            case PLACING -> {
                if(!this.workerHasBeenSelected) {
                    this.output.println("Select your worker using `select [INDEX]`");
                }
                else {
                    this.output.println("Place your worker using `place [X] [Y]`");
                    this.output.println("Finish placing: `end turn`");
                }
            }
            case PLAYING -> {
                if(!this.workerHasBeenSelected) {
                    this.output.println("Select your worker using `select [INDEX]`");
                }
                else {
                    this.output.println("Usage: `move/build/build_dome [X] [Y]`");
                    this.output.println("End turn: `end turn`");
                }
            }
        }
    }

    public void executeAction(String input)
            throws NotExecutedException, IOException {

        if (this.currentStatus == GameStatus.SETUP) {
            if (!iAmChallenger) {
                throw new NotExecutedException("Wait the challenger!");
            }
        } else if (!this.isMyTurn) {
            throw new NotExecutedException("Not your turn!");
        }

        Scanner scanner = new Scanner(input.toUpperCase());
        String command = scanner.next();

        switch (this.currentStatus) {
            case SETUP -> {
                if (numberOfAvailableGods != this.allPlayers.size()) {
                    if (!command.equals("ADD_GOD")) {
                        throw new NotExecutedException("You need add god now.");
                    }
                    this.controller.addAvailableGods(GodType.valueOf(scanner.next().trim()));
                } else {
                    if (!command.equals("SET_FIRST")) {
                        throw new NotExecutedException("You need set first player now.");
                    }
                    String playerName = scanner.nextLine().trim();
                    Optional<String> player = this.allPlayers.stream()
                            .filter(x -> x.toUpperCase().equals(playerName))
                            .findAny();
                    if (player.isEmpty()) {
                        throw new NotExecutedException("Invalid player name");
                    }
                    this.controller.setGameStatus(GameStatus.CHOOSING_GODS);
                    this.controller.setCurrentPlayer(this.allPlayers.indexOf(player.get()));
                }
            }
            case CHOOSING_GODS -> {
                if(command.equals("END")) {
                    if(!this.playerGods.containsKey(this.player)) {
                        throw new NotExecutedException("Choose a god first");
                    }
                    if(this.playerGods.size() == this.allPlayers.size()) {
                        this.controller.setGameStatus(GameStatus.BEFORE_PLACING);
                    }
                    this.controller.nextTurn();
                    return;
                }

                if (!command.equals("GOD")) {
                    throw new NotExecutedException("You must choose god now.");
                }

                GodType type;
                try {
                    type = GodType.valueOf(scanner.next().trim());
                } catch (IllegalArgumentException e) {
                    throw new NotExecutedException("Invalid god name");
                }
                this.controller.setPlayerGod(this.player, type);
            }
            case PLACING, PLAYING -> {
                if (command.equals("END")) {
                    if(this.currentStatus == GameStatus.PLACING) {
                        long numberOfAllWorkers = Arrays.stream(this.spaces)
                                .filter(Space::isOccupiedByWorker)
                                .count();
                        // all workers has been placed
                        if(numberOfAllWorkers == this.allPlayers.size() * 2) {
                            this.controller.setGameStatus(GameStatus.BEFORE_PLAYING);
                        }
                    }
                    this.controller.nextTurn();
                }

                if (this.currentStatus == GameStatus.PLAYING) {
                    if (command.equals("UNDO")) {
                        this.controller.undo();
                        this.workerHasBeenSelected = false;
                        return;
                    }
                }

                if (command.equals("SELECT")) {
                    this.controller.selectWorker(this.player, scanner.nextInt());
                    this.workerHasBeenSelected = true;
                    return;
                }


                WorkerActionType type;
                int x;
                int y;
                try {
                    type = WorkerActionType.valueOf(command);
                    x = scanner.nextInt();
                    y = scanner.nextInt();
                } catch (Exception e) {
                    throw new NotExecutedException("Invalid worker action input");
                }

                if (!World.isInWorld(x, y)) {
                    throw new NotExecutedException("Invalid coordinates");
                }

                try {
                    this.controller.workerAction(this.player, type, x, y);
                }
                catch (NotExecutedException e) {
                    if(this.currentStatus != GameStatus.PLAYING) {
                        return;
                    }
                    this.output.println("You tried to " + command + " which was not possible.");
                    List<Vector2> hints = this.controller.getValidActions()
                            .getOrDefault(type, null);
                    if(hints == null) {
                        this.output.println("You can't " + command + " on anywhere of the map.");
                    }
                    else {
                        this.output.println("Hint: ");
                        this.printMap(type, hints);
                    }
                }

            }
        }

    }

    public GameStatus getCurrentStatus() {
        return currentStatus;
    }

    public GameController getGameController() {
        return this.controller;
    }

    @Override
    public void notifyGameStatus(GameStatus status) {
        this.currentStatus = status;
        switch (status) {
            case SETUP -> this.output.println("Setup phase");
            case CHOOSING_GODS -> this.output.println("Choosing gods, use `GOD [GOD NAME]` to choose god");
            case BEFORE_PLACING, PLACING -> {
                this.output.println("Placing workers.");
                this.printMap(null, null);
            }
            case BEFORE_PLAYING, PLAYING -> {
                this.output.println("Game started");
                this.printMap(null, null);
            }
            case ENDED -> this.output.println("Game ended");
            default -> throw new InternalError("Not implemented yet");
        }
    }

    @Override
    public void notifyAvailableGods(Collection<GodType> availableGods) {
        this.output.println("Available gods: ");
        availableGods.forEach(this.output::println);
        numberOfAvailableGods = availableGods.size();
    }

    @Override
    public void notifyPlayerGods(Map<String, GodType> playerAndGods) {
        this.playerGods.clear();
        this.playerGods.putAll(playerAndGods);
        this.output.println("Player gods:");
        this.playerGods.forEach((player, god) -> {
            this.output.println("Player " + player + " has selected " + god);
        });
    }

    @Override
    public void notifySpaceChange(Space space) {
        this.spaces[space.getPosition().getY() * World.SIZE + space.getPosition().getX()] = space;
        this.printMap(null, null);
    }

    @Override
    public void notifyPlayerTurn(String player) {
        if (this.player.equals(player)) {
            this.output.println("Your turn!");
            this.isMyTurn = true;
            this.workerHasBeenSelected = false;
        } else {
            this.isMyTurn = false;
            this.output.println("Turn of " + player);
        }

    }

    @Override
    public void notifyPlayerDefeat(String player) {
        this.output.println(player + " has lost!");
    }

    public void printMap(WorkerActionType hintType, List<Vector2> hints) {
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
            info.println("Now is placing phase, place your workers");
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
            info.print("0 -> " + workerSymbols.get(player + "0") + "; ");
            info.println("1 -> " + workerSymbols.get(player + "1"));
        }

        info.println("Command: `place/move/build/build_dome [X] [Y]`");
        info.println("Example: `move 0 3`");

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

        if(hintType != null && hints != null) {
            char hint = switch (hintType) {
                case BUILD -> 'b';
                case MOVE -> 'm';
                case BUILD_DOME -> 'd';
                case WIN -> 'w';
                default -> '?';
            };
            info.println(hint + " means places where you can " + hintType);
            for (Vector2 position : hints) {
                int x = position.getX() * 3 + 1;
                int y = position.getY() * 2 + 1;
                world.setCharacter(x + 1, y, hint);
            }
        }

        this.output.print(matrix.toString());
    }
}
