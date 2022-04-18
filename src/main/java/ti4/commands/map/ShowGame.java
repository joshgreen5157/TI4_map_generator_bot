package ti4.commands.map;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import ti4.commands.Command;
import ti4.generator.GenerateMap;
import ti4.helpers.Constants;
import ti4.map.Map;
import ti4.map.MapManager;
import ti4.message.MessageHelper;

import java.io.File;

public class ShowGame implements Command {

    @Override
    public String getActionID() {
        return Constants.SHOW_GAME;
    }

    @Override
    public boolean accept(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(getActionID())) {
            return false;
        }
        OptionMapping option = event.getOption(Constants.GAME_NAME);
        if (option != null) {
            String mapName = option.getAsString();
            if (!MapManager.getInstance().getMapList().containsKey(mapName)) {
                MessageHelper.replyToMessage(event, "Game with such name does not exists, use /list_games");
                return false;
            }
        } else {
            Map userActiveMap = MapManager.getInstance().getUserActiveMap(event.getUser().getId());
            if (userActiveMap == null){
                MessageHelper.replyToMessage(event, "No active game set, need to specify what map to show");
                return false;
            }
        }
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        Map map;
        OptionMapping option = event.getOption(Constants.GAME_NAME);
        MapManager mapManager = MapManager.getInstance();
        if (option != null) {
            String mapName = option.getAsString().toLowerCase();
            map = mapManager.getMap(mapName);
        } else {
            map = mapManager.getUserActiveMap(event.getUser().getId());
        }

        boolean statsOnly = false;
        OptionMapping statsOption = event.getOption(Constants.STATS_ONLY);
        if (statsOption != null) {
            statsOnly = statsOption.getAsBoolean();
        }
        File file = GenerateMap.getInstance().saveImage(map, statsOnly);
        MessageHelper.replyToMessage(event, file);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void registerCommands(CommandListUpdateAction commands) {
        // Moderation commands with required options
        commands.addCommands(
                Commands.slash(getActionID(), "Shows selected map")
                        .addOptions(new OptionData(OptionType.STRING, Constants.GAME_NAME, "Map name to be shown"))
                        .addOptions(new OptionData(OptionType.BOOLEAN, Constants.STATS_ONLY, "Show only stats for map")));
    }
}
