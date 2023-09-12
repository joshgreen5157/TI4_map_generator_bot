package ti4.commands.cardsac;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.AsyncBot;
import ti4.helpers.Constants;
import ti4.helpers.Emojis;
import ti4.helpers.FoWHelper;
import ti4.helpers.Helper;
import ti4.map.Game;
import ti4.map.Player;
import ti4.message.MessageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class SentACRandom extends ACCardsSubcommandData {
    public SentACRandom() {
        super(Constants.SEND_AC_RANDOM, "Send a random Action Card to a player");
        addOptions(new OptionData(OptionType.STRING, Constants.FACTION_COLOR, "Faction or Color").setRequired(true).setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game activeGame = getActiveGame();
        Player player = activeGame.getPlayer(getUser().getId());
        player = Helper.getGamePlayer(activeGame, player, event, null);
        if (player == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Player could not be found");
            return;
        }
        LinkedHashMap<String, Integer> actionCardsMap = player.getActionCards();
        List<String> actionCards = new ArrayList<>(actionCardsMap.keySet());
        if (actionCards.isEmpty()) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "No Action Cards in hand");
        }
        Collections.shuffle(actionCards);
        String acID = actionCards.get(0);
        Player player_ = Helper.getPlayer(activeGame, null, event);
        if (player_ == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Player not found");
            return;
        }

        User user = AsyncBot.jda.getUserById(player_.getUserID());
        if (user == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "User for faction not found. Report to ADMIN");
            return;
        }

		// FoW specific pinging
		if (activeGame.isFoWMode()) {
			FoWHelper.pingPlayersTransaction(activeGame, event, player, player_, Emojis.ActionCard + " Action Card", null);
		}

        player.removeActionCard(actionCardsMap.get(acID));
        player_.setActionCard(acID);
        ACInfo.sendActionCardInfo(activeGame, player_);
        ACInfo.sendActionCardInfo(activeGame, player);
    }
}
