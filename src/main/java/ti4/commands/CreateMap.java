package ti4.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import ti4.helpers.Constants;
import ti4.map.Map;
import ti4.map.MapManager;
import ti4.map.MapSaveLoadManager;
import ti4.message.MessageHelper;

public class CreateMap implements Command {


    @Override
    public boolean accept(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(Constants.CREATE_MAP)) {
            return false;
        }
        String mapName = event.getOptions().get(0).getAsString();
        if (!MapManager.getInstance().getMapList().containsKey(mapName)) {
            MessageHelper.replyToMessage(event, "Map with such name exist already, choose different name");
            return false;
        }
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        Member member = event.getInteraction().getMember();
        if (member == null) {
            MessageHelper.replyToMessage(event, "Caller ID not found");
            return;
        }
        Map map = new Map();
        String ownerID = event.getInteraction().getId();
        map.setOwnerID(ownerID);
        String mapName = event.getOptions().get(0).getAsString();
        map.setName(mapName);

        MapManager mapManager = MapManager.getInstance();
        mapManager.addMap(map);
        boolean setMapSuccessful = mapManager.setMapForUser(ownerID, mapName);
        if (!setMapSuccessful) {
            MessageHelper.replyToMessage(event, "Could not assign active map " + mapName);
        }
        MessageHelper.replyToMessage(event, "Map created with name: " + mapName);
        MapSaveLoadManager.saveMap(map);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void registerCommands(CommandListUpdateAction commands) {
        // Moderation commands with required options
        commands.addCommands(
                Commands.slash(Constants.CREATE_MAP, "Shows selected map")
                        .addOptions(new OptionData(OptionType.STRING, Constants.MAP_NAME, "Map name")
                                .setRequired(true))
        );
    }
}
