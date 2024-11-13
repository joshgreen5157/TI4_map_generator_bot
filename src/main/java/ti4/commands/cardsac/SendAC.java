package ti4.commands.cardsac;

import java.util.Map;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.commands.GameStateSubcommand;
import ti4.helpers.ButtonHelper;
import ti4.helpers.Constants;
import ti4.helpers.Emojis;
import ti4.helpers.FoWHelper;
import ti4.helpers.Helper;
import ti4.map.Game;
import ti4.map.Player;
import ti4.message.MessageHelper;

public class SendAC extends GameStateSubcommand {

    public SendAC() {
        super(Constants.SEND_AC, "Send an Action Card to a player", true, true);
        addOptions(new OptionData(OptionType.INTEGER, Constants.ACTION_CARD_ID, "Action Card ID that to send").setRequired(true));
        addOptions(new OptionData(OptionType.STRING, Constants.OTHER_FACTION_OR_COLOR, "Faction or Color to send to").setRequired(true).setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Player player = getPlayer();
        int acIndex = event.getOption(Constants.ACTION_CARD_ID).getAsInt();

        String acID = null;
        for (Map.Entry<String, Integer> ac : player.getActionCards().entrySet()) {
            if (ac.getValue().equals(acIndex)) {
                acID = ac.getKey();
            }
        }

        if (acID == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "No such Action Card ID found, please retry");
            return;
        }

        Game game = getGame();
        Player playerToSendTo = Helper.getOtherPlayerFromEvent(game, event);
        if (playerToSendTo == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Player not found");
            return;
        }

        // FoW specific pinging
        if (game.isFowMode()) {
            FoWHelper.pingPlayersTransaction(game, event, player, playerToSendTo, Emojis.ActionCard + " Action Card", null);
        }

        sendActionCard(event, game, player, playerToSendTo, acID);
    }

    public static void sendActionCard(GenericInteractionCreateEvent event, Game game, Player player, Player p2, String acID) {
        Integer handIndex = player.getActionCards().get(acID);
        ButtonHelper.checkACLimit(game, event, p2);
        if (acID == null || handIndex == null) {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Could not find AC in your hand.");
            return;
        }
        if (p2 == null) {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Could not find other player.");
            return;
        }

        player.removeActionCard(handIndex);
        p2.setActionCard(acID);
        ACInfo.sendActionCardInfo(game, player);
        ACInfo.sendActionCardInfo(game, p2);
    }
}
