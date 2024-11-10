package ti4.commands.explore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import ti4.commands.Command;
import ti4.helpers.Constants;
import ti4.map.Game;
import ti4.map.GameManager;
import ti4.map.GameSaveLoadManager;
import ti4.map.UserGameContextManager;

public class ExploreCommand implements Command {

    private final Collection<ExploreSubcommandData> subcommandData = getSubcommands();

    @Override
    public String getActionID() {
        return Constants.EXPLORE;
    }

    public String getActionDescription() {
        return "Explore";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String subcommandName = event.getInteraction().getSubcommandName();
        for (ExploreSubcommandData subcommand : subcommandData) {
            if (Objects.equals(subcommand.getName(), subcommandName)) {
                subcommand.preExecute(event);
                subcommand.execute(event);
                break;
            }
        }
        String userID = event.getUser().getId();
        String gameName = UserGameContextManager.getContextGame(userID);
        Game game = GameManager.getGame(gameName);
        GameSaveLoadManager.saveGame(game, event);
    }

    private Collection<ExploreSubcommandData> getSubcommands() {
        Collection<ExploreSubcommandData> subcommands = new HashSet<>();
        subcommands.add(new ExploreDiscardFromDeck());
        subcommands.add(new ExploreShuffleIntoDeckFromHand());
        subcommands.add(new ExploreDrawAndDiscard());
        subcommands.add(new ExploreRemoveFromGame());
        subcommands.add(new ExploreShuffleBackIntoDeck());
        subcommands.add(new ExploreInfo());
        subcommands.add(new ExplorePlanet());
        subcommands.add(new ExploreReset());
        subcommands.add(new ExploreFrontier());
        subcommands.add(new ExploreUse());
        subcommands.add(new ExploreLookAtTop());

        return subcommands;
    }

    @Override
    public void registerCommands(CommandListUpdateAction commands) {
        commands.addCommands(
            Commands.slash(getActionID(), getActionDescription())
                .addSubcommands(getSubcommands()));
    }
}
