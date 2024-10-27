package ti4.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ti4.helpers.Emojis;
import ti4.model.Source.ComponentSource;

@Data
public class PromissoryNoteModel implements ColorableModelInterface<PromissoryNoteModel>, EmbeddableModel {
    private String alias;
    private String name;
    private String faction;
    private String color;
    private Boolean playArea;
    private Boolean playImmediately;
    private String attachment;
    private ComponentSource source;
    private String text;
    private String homebrewReplacesID;
    private String imageURL;
    private List<String> searchTags = new ArrayList<>();
    private boolean dupe = false;

    public boolean isDupe() {
        return dupe;
    }

    public boolean isColorable() {
        return color != null && color.equals("<color>");
    }

    @Override
    public PromissoryNoteModel duplicateAndSetColor(ColorModel newColor) {
        PromissoryNoteModel pn = new PromissoryNoteModel();
        pn.setAlias(this.alias.replaceAll("<color>", newColor.getName()));
        pn.setName(this.name);
        pn.setFaction(this.faction);
        pn.setColor(newColor.getName());
        pn.setPlayArea(this.playArea);
        pn.setAttachment(this.attachment);
        pn.setSource(this.source);
        String newText = getText().replaceAll("<color>", newColor.getName());
        pn.setText(newText);
        pn.setHomebrewReplacesID(this.homebrewReplacesID);
        pn.setSearchTags(new ArrayList<>(searchTags));
        pn.setDupe(true);
        return pn;
    }

    public boolean isValid() {
        return alias != null
            && name != null
            && (faction != null || color != null)
            && text != null
            && source != null;
    }

    public Optional<String> getFaction() {
        return Optional.ofNullable(faction);
    }

    public Optional<String> getColor() {
        return Optional.ofNullable(color);
    }

    public String getFactionOrColor() {
        if (!StringUtils.isBlank(getFaction().orElse(""))) return faction;
        if (!StringUtils.isBlank(getColor().orElse(""))) {
            if (color.equals("<color>")) return "generic";
            return color;
        }
        return faction + "_" + color;
    }

    public Optional<String> getAttachment() {
        return Optional.ofNullable(attachment);
    }

    public Optional<String> getHomebrewReplacesID() {
        return Optional.ofNullable(homebrewReplacesID);
    }

    public String getOwner() {
        if (faction == null || faction.isEmpty()) return color;
        return faction;
    }

    public boolean getPlayArea() {
        return Optional.ofNullable(playArea).orElse(false);
    }

    public boolean isPlayedDirectlyToPlayArea() {
        if (playArea == null) {
            return false;
        }
        if (playImmediately != null) return playArea && playImmediately;

        return playArea;
    }

    public MessageEmbed getRepresentationEmbed() {
        return getRepresentationEmbed(false, false, false);
    }

    public MessageEmbed getRepresentationEmbed(boolean justShowName, boolean includeID, boolean includeHelpfulText) {
        EmbedBuilder eb = new EmbedBuilder();

        //TITLE
        StringBuilder title = new StringBuilder();
        title.append(Emojis.PN);
        if (!StringUtils.isBlank(getFaction().orElse(""))) title.append(Emojis.getFactionIconFromDiscord(getFaction().get()));
        title.append("__**").append(getName()).append("**__");
        if (!StringUtils.isBlank(getColor().orElse(""))) {
            title.append(" (");
            if (color.equals("<color>")) {
                title.append("generic");
            } else {
                title.append(color);
            }
            title.append(")");
        }
        title.append(getSource().emoji());
        eb.setTitle(title.toString());

        if (justShowName) return eb.build();

        //DESCRIPTION
        eb.setDescription(getText());

        //FOOTER
        StringBuilder footer = new StringBuilder();
        if (includeHelpfulText) {
            if (!StringUtils.isBlank(getAttachment().orElse(""))) footer.append("Attachment: ").append(getAttachment().orElse("")).append("\n");
            if (getPlayArea()) {
                footer.append("Play area card. ");
                if (isPlayedDirectlyToPlayArea()) {
                    footer.append("Sent directly to play area when received.");
                } else {
                    footer.append("Must be played from hand to enter play area.");
                }
                footer.append("\n");
            }
        }
        if (includeID) {
            footer.append("ID: ").append(getAlias()).append("    Source: ").append(getSource()).append("\n");
        }
        eb.setFooter(footer.toString());

        eb.setColor(Color.blue);
        return eb.build();
    }

    public String getNameRepresentation() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isBlank(getFaction().orElse(""))) sb.append(Emojis.getFactionIconFromDiscord(getFaction().get()));
        sb.append(Emojis.PN);
        sb.append(" ").append(getName());
        if (!StringUtils.isBlank(getColor().orElse(""))) {
            sb.append(" (");
            if (color.equals("<color>")) {
                sb.append("generic");
            } else {
                sb.append(color);
            }
            sb.append(")");
        }
        sb.append(getSource().emoji());
        return sb.toString();
    }

    public boolean isNotWellKnown() {
        return getFaction().isPresent()
            || (getSource() != ComponentSource.base && getSource() != ComponentSource.pok);
    }

    /**
     * @deprecated This only exists to simulate the old text based promissory note .property files
     */
    @Deprecated
    public String getShortText() {
        String promStr = getText();
        // if we would break trying to split the note, just return whatever is there
        if (promStr == null || !promStr.contains(";")) {
            return promStr;
        }
        return getName() + ";" + getFaction() + getColor();
    }

    public boolean search(String searchString) {
        return getAlias().toLowerCase().contains(searchString) || getName().toLowerCase().contains(searchString) || getFactionOrColor().toLowerCase().contains(searchString)
            || getSearchTags().contains(searchString);
    }

    public String getAutoCompleteName() {
        return getName() + " (" + getFactionOrColor() + ") [" + getSource() + "]";
    }
}
