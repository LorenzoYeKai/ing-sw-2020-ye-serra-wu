package it.polimi.ingsw;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.List;

/**
 * A fake {@link Worker} with specified {@link World} and {@link ActualRule}.
 * Useful for simple testing where a complete {@link Game} is unnecessary.
 */
public class MockWorker extends Worker {
    private static class MockPlayer extends Player {
        private static class MockGame extends Game {
            private final World world;
            private final ActualRule rules;

            public MockGame(World world, ActualRule rules) {
                super(List.of("a", "b"));
                this.world = world;
                this.rules = rules;
            }

            @Override
            public World getWorld() {
                return this.world;
            }

            @Override
            public ActualRule getRules() {
                return this.rules;
            }
        }

        public MockPlayer(World world, ActualRule rules) {
            super(new MockGame(world, rules), "a");
        }
    }

    public MockWorker(World world, ActualRule rules) {
        super(new MockPlayer(world, rules), 0);
    }
}
