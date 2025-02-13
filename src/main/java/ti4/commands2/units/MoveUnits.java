package ti4.commands2.units;

import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.commands2.CommandHelper;
import ti4.commands2.GameStateCommand;
import ti4.helpers.Constants;
import ti4.map.Game;
import ti4.map.Tile;
import ti4.message.BotLogger;
import ti4.message.MessageHelper;
import ti4.service.combat.StartCombatService;
import ti4.service.unit.AddUnitService;
import ti4.service.unit.RemoveUnitService;

public class MoveUnits extends GameStateCommand {

    public MoveUnits() {
        super(true, true);
    }

    @Override
    public String getName() {
        return Constants.MOVE_UNITS;
    }

    @Override
    public String getDescription() {
        return "Move units from one system to another system";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
            new OptionData(OptionType.STRING, Constants.TILE_NAME, "System/Tile to move units from")
                .setRequired(true)
                .setAutoComplete(true),
            new OptionData(OptionType.STRING, Constants.UNIT_NAMES, "Comma separated list of '{count} unit {planet}' Eg. 2 infantry primor, carrier, 2 fighter, mech pri")
                .setRequired(true),
            new OptionData(OptionType.STRING, Constants.TILE_NAME_TO, "System/Tile to move units to")
                .setAutoComplete(true)
                .setRequired(true),
            new OptionData(OptionType.STRING, Constants.UNIT_NAMES_TO, "Comma separated list of '{count} unit {planet}' Eg. 2 infantry primor, carrier, 2 fighter, mech pri")
                .setRequired(true),
            new OptionData(OptionType.STRING, Constants.FACTION_COLOR, "Faction or Color for unit")
                .setAutoComplete(true),
            new OptionData(OptionType.STRING, Constants.CC_USE, "\"t\"/\"tactic\" to add a token from tactic pool, \"r\"/\"retreat\" to add a token from reinforcements")
                .setAutoComplete(true),
            new OptionData(OptionType.BOOLEAN, Constants.PRIORITIZE_DAMAGED, "Prioritize moving damaged units. Default false."),
            new OptionData(OptionType.BOOLEAN, Constants.NO_MAPGEN, "'True' to not generate a map update with this command")
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();

        Tile tileFrom = CommandHelper.getTile(event, game);
        if (tileFrom == null) {
            BotLogger.log("Could not find the tile you're moving from.");
            return;
        }

        Tile tileTo = CommandHelper.getTile(event, game, event.getOption(Constants.TILE_NAME_TO).getAsString());
        if (tileTo == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Could not find the tile you're moving to.");
            return;
        }

        String color = getPlayer().getColor();
        boolean prioritizeDamaged = event.getOption(Constants.PRIORITIZE_DAMAGED, false, OptionMapping::getAsBoolean);
        String fromUnitList = event.getOption(Constants.UNIT_NAMES).getAsString();
        RemoveUnitService.removeUnits(event, tileFrom, game, color, fromUnitList, prioritizeDamaged);

        String toUnitList = event.getOption(Constants.UNIT_NAMES_TO).getAsString();
        AddUnitService.addUnits(event, tileTo, game, color, toUnitList);

        StartCombatService.combatCheck(game, event, tileTo);
        UnitCommandHelper.handleCcUseOption(event, tileTo, color, game);
        UnitCommandHelper.handleGenerateMapOption(event, game);
    }
}
