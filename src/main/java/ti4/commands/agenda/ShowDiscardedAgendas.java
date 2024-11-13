package ti4.commands.agenda;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ti4.buttons.Buttons;
import ti4.commands.GameStateSubcommand;
import ti4.commands.cardsac.ShowDiscardActionCards;
import ti4.commands.cardsso.ShowUnScoredSOs;
import ti4.commands.explore.ExploreInfo;
import ti4.commands.relic.RelicShowRemaining;
import ti4.commands.tech.TechShowDeck;
import ti4.generator.Mapper;
import ti4.helpers.Constants;
import ti4.listeners.annotations.ButtonHandler;
import ti4.map.Game;
import ti4.map.Player;
import ti4.message.MessageHelper;

import static ti4.helpers.ButtonHelper.deleteMessage;

public class ShowDiscardedAgendas extends GameStateSubcommand {

    public ShowDiscardedAgendas() {
        super(Constants.SHOW_DISCARDED, "Show discarded Agendas", false, false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        showDiscards(getGame(), event);
    }

    private static void showDiscards(Game game, GenericInteractionCreateEvent event) {
        StringBuilder sb2 = new StringBuilder();
        String sb = "### __**Discarded Agendas:**__";
        Map<String, Integer> discardAgendas = game.getDiscardAgendas();
        List<MessageEmbed> agendaEmbeds = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : discardAgendas.entrySet()) {
            agendaEmbeds.add(Mapper.getAgenda(entry.getKey()).getRepresentationEmbed());
            sb2.append(Mapper.getAgenda(entry.getKey()).getName()).append(" (ID: ").append(entry.getValue()).append(")\n");
        }
        MessageHelper.sendMessageToChannelWithEmbeds(event.getMessageChannel(), sb, agendaEmbeds);
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), sb2.toString());
    }

    @ButtonHandler("showDeck_")
    public static void resolveDeckChoice(Game game, ButtonInteractionEvent event, String buttonID, Player player) {
        String deck = buttonID.replace("showDeck_", "");
        switch (deck) {
            case "ac" -> ShowDiscardActionCards.showDiscard(game, event, false);
            case "agenda" -> ShowDiscardedAgendas.showDiscards(game, event);
            case "relic" -> RelicShowRemaining.showRemaining(event, false, game, player);
            case "unscoredSO" -> ShowUnScoredSOs.showUnscored(game, event);
            case Constants.PROPULSION, Constants.WARFARE, Constants.CYBERNETIC, Constants.BIOTIC, Constants.UNIT_UPGRADE -> TechShowDeck.displayTechDeck(game, event, deck);
            case Constants.CULTURAL, Constants.INDUSTRIAL, Constants.HAZARDOUS, Constants.FRONTIER, "all" -> {
                List<String> types = new ArrayList<>();
                String msg = "You may click this button to get the full text.";
                List<Button> buttons = new ArrayList<>();
                buttons.add(Buttons.green("showTextOfDeck_" + deck, "Show full text"));
                buttons.add(Buttons.red("deleteButtons", "No Thanks"));
                if ("all".equalsIgnoreCase(deck)) { // Show all explores
                    types.add(Constants.CULTURAL);
                    types.add(Constants.INDUSTRIAL);
                    types.add(Constants.HAZARDOUS);
                    types.add(Constants.FRONTIER);
                } else {
                    types.add(deck);
                }
                ExploreInfo.secondHalfOfExpInfo(types, event, player, game, false);
                MessageHelper.sendMessageToChannelWithButtons(event.getMessageChannel(), msg, buttons);
            }
            default -> MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Deck Button Not Implemented: " + deck);
        }
        deleteMessage(event);
    }
}
