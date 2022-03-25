package ti4.commands.cards;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.generator.Mapper;
import ti4.helpers.Constants;
import ti4.map.Map;
import ti4.map.Player;
import ti4.message.MessageHelper;

import java.util.LinkedHashMap;

public class ShowSOToAll extends CardsSubcommandData {
    public ShowSOToAll() {
        super(Constants.SHOW_SO_TO_ALL, "Show Secret Objective to table");
        addOptions(new OptionData(OptionType.INTEGER, Constants.SECRET_OBJECTIVE_ID, "Secret objective ID that is sent between ()"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Map activeMap = getActiveMap();
        Player player = activeMap.getPlayer(getUser().getId());
        if (player == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Player could not be found");
            return;
        }
        OptionMapping option = event.getOption(Constants.SECRET_OBJECTIVE_ID);
        if (option == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Please select what Secret Objective to show to All");
            return;
        }

        int soIndex = option.getAsInt();
        String soID = null;
        boolean scored = false;
        for (java.util.Map.Entry<String, Integer> so : player.getSecrets().entrySet()) {
            if (so.getValue().equals(soIndex)) {
                soID = so.getKey();
                break;
            }
        }
        if (soID == null){
            for (java.util.Map.Entry<String, Integer> so : player.getSecretsScored().entrySet()) {
                if (so.getValue().equals(soIndex)) {
                    soID = so.getKey();
                    scored = true;
                    break;
                }
            }
        }

        if (soID == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "No such Secret Objective ID found, please retry");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Game: ").append(activeMap.getName()).append("\n");
        sb.append("Player: ").append(player.getUserName()).append("\n");
        if (scored){
            sb.append("Showed Scored Secret Objectives:").append("\n");
        }else {
            sb.append("Showed Secret Objectives:").append("\n");
        }
        sb.append(Mapper.getSecretObjective(soID)).append("\n");
        if (!scored) {
            player.setSecret(soID);
        }
        MessageHelper.sendMessageToChannel(event.getChannel(), sb.toString());
        CardsInfo.sentUserCardInfo(event, activeMap, player);
    }
}
