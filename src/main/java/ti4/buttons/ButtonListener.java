package ti4.buttons;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;
import ti4.helpers.Helper;
import ti4.map.Map;
import ti4.map.MapManager;
import ti4.map.Player;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonID = event.getButton().getId();
        if (buttonID == null){
            return;
        }
        ButtonInteraction interaction = event.getInteraction();
        interaction.deferReply().queue();
        switch (buttonID) {
            case "sabotage" -> addReactionForSabo(event, true);
            case "no_sabotage" -> addReactionForSabo(event, false);
            default -> event.getHook().sendMessage("Button " + buttonID + " pressed.").queue();
        }
    }

    private void addReactionForSabo(@NotNull ButtonInteractionEvent event, boolean sabotage) {
        JDA jda = event.getJDA();
        String id = event.getUser().getId();
        Map activeMap = MapManager.getInstance().getUserActiveMap(id);
        Player player = Helper.getGamePlayer(activeMap, null, event.getMember(), id);
        if (player == null) {
            event.getHook().sendMessage("Your not a player of the game").queue();
            return;
        }
        String playerFaction = player.getFaction();
        Emote emoteToUse = null;
        for (Emote emote : jda.getEmotes()) {
            if (emote.getName().toLowerCase().contains(playerFaction.toLowerCase())) {
                emoteToUse = emote;
                break;
            }
        }
        if (!sabotage) {
            if (emoteToUse == null) {
                event.getHook().sendMessage("Could not find faction (" + playerFaction + ") symbol for reaction").queue();
                return;
            }
            event.getChannel().addReactionById(event.getInteraction().getMessage().getId(), emoteToUse).queue();
        }
        if (sabotage) {
            String text = Helper.getFactionIconFromDiscord(playerFaction) + " Sabotaging Action Card Play";
            event.getHook().sendMessage(text).queue();
            event.getChannel().sendMessage(Helper.getGamePing(event.getGuild(), activeMap) + ".").queue();
        } else {
            String text = Helper.getFactionIconFromDiscord(playerFaction) + " No Sabotage";
            event.getHook().sendMessage(text).queue();
        }
    }
}
