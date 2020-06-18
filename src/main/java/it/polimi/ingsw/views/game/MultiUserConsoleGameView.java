package it.polimi.ingsw.views.game;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.views.utils.ConsoleMatrix;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * A console View which should allow multiple players to
 * play the same game from the same console (by turn)
 * <p>
 * NOTE: CURRENTLY, ONLY 2 PLAYERS ARE SUPPORTED
 * AND NO GODS ARE SUPPORTED
 */
public class MultiUserConsoleGameView extends GameView {
    private final PrintStream output;
    private final Scanner input;
    private final SpaceData[][] spaces;
    private final List<PlayerData> players;
    private boolean gameStarted;

    public MultiUserConsoleGameView(GameController controller) {
        super(controller);
        this.output = System.out;
        this.input = new Scanner(System.in);
        this.spaces = new SpaceData[5][5];
        this.players = new ArrayList<>();
        for (int y = 0; y < this.spaces.length; ++y) {
            for (int x = 0; x < this.spaces.length; ++x) {
                this.spaces[y][x] = new Space(null, y, x);
            }
        }

        this.gameStarted = false;
    }

    public void join(String nickname) {
        this.players.add(this.controller.joinGame(nickname, this));
    }

    public void play() {
        this.controller.playGame();
    }

    @Override
    public void notifyGameStatus(GameStatus status) {
        switch (status) {
            case SETUP:
                this.output.println("Setup phase");
                break;
            case PLAYING:
                this.output.println("Game started");
                this.gameStarted = true;
                break;
            case ENDED:
                this.output.println("Game ended");
                break;
            default:
                throw new InternalError("Not implemented yet");
        }
    }

    @Override
    public void notifySpaceChange(SpaceData spaceData) {
        this.spaces[spaceData.getY()][spaceData.getX()] = spaceData;
    }

    @Override
    public void notifyPlayerTurn(PlayerData player) {
        this.output.println("Now it's turn of " + player.getName());

        // TODO: replace true with `player.getAvailableWorkers().isEmpty()`
        while (true)
        {
            this.printMap(player);

            // TODO: Remove this part,
            //  and instead decrease "getAvailableWorkers" through rule
            String next = this.input.next();
            if(next.toUpperCase().equals("END")) {
                break;
            }

            WorkerData worker;
            WorkerActionType type;
            int x;
            int y;
            try {

                worker = player.getAllWorkers().get(Integer.parseInt(next));
                type = WorkerActionType.valueOf(this.input.next().toUpperCase());
                x = this.input.nextInt();
                y = this.input.nextInt();
                if(x >= 5 || y >= 5) {
                    throw new NotExecutedException("Invalid coordinates");
                }
            }
            catch (Exception exception) {
                this.output.println("Exception: " + exception);
                continue;
            }

            try {
                controller.workerAction(worker, type, x, y);
            } catch (NotExecutedException e) {
                this.output.println("Command failed: " + e);
            }
        }
    }

    @Override
    public void notifyPlayerDefeat(PlayerData player) {
        this.output.println(player.getName() + " has lost!");
    }

    private void printMap(PlayerData currentPlayer) {
        String[] levels = new String[]{" ", "1", "2", "3"};
        String dome = "^";

        Map<WorkerData, String> workerSymbols = new HashMap<>();
        String[] symbols = new String[]{"A", "B", "C", "D"};
        int symbolIndex = 0;
        for (PlayerData player : this.players) {
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

        if (!this.gameStarted) {
            info.println("Now is setup phase, move your workers");
        } else {
            info.println("Move / build with your workers");
        }

        info.println("Player " + currentPlayer.getName() + ", your workers: ");
        info.println("0 -> " + workerSymbols.get(currentPlayer.getAllWorkers().get(0)));
        info.println("1 -> " + workerSymbols.get(currentPlayer.getAllWorkers().get(1)));
        if (!this.gameStarted) {
            info.println("Command: `[worker index] place [X] [Y]`");
            info.println("Example: `0 place 0 3`");
        } else {
            info.println("Command: `[worker index] [move/build/build_dome] [X] [Y]`");
            info.println("Example: `0 move 0 3`");
        }
        info.println("Type `end` to end your input");

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

        this.output.print(matrix.toString());
    }
}

