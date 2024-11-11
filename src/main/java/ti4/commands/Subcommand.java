package ti4.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public abstract class Subcommand extends SubcommandData {

    public Subcommand(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public boolean accept(SlashCommandInteractionEvent event) {
        return name.equals(event.getInteraction().getSubcommandName());
    }

    public void preExecute(SlashCommandInteractionEvent event) {}

    public abstract void execute(SlashCommandInteractionEvent event);

    public void postExecute(SlashCommandInteractionEvent event) {}
}
