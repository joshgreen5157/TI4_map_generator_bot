package ti4.commands.user;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ti4.commands.CommandHelper;
import ti4.commands.ParentCommand;
import ti4.commands.Subcommand;
import ti4.helpers.Constants;

public class UserCommand implements ParentCommand {

    private final Map<String, Subcommand> subcommands = Stream.of(
                    new ShowUserSettings(),
                    new SetPreferredColourList(),
                    new SetPersonalPingInterval())
            .collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public String getName() {
        return Constants.USER;
    }

    @Override
    public String getDescription() {
        return "User";
    }

    @Override
    public boolean accept(SlashCommandInteractionEvent event) {
        return ParentCommand.super.accept(event) &&
                CommandHelper.acceptIfPlayerInGame(event);
    }

    @Override
    public Map<String, Subcommand> getSubcommands() {
        return subcommands;
    }
}
