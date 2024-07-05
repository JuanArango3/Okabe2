package co.jmarango.okabe2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RichResponse extends Response {

    private String title;
    private Color color;
    private List<MessageEmbed.Field> fields;

    private String image;
    private String thumbnail;

    private Author author;
    private Footer footer;

    @Override
    public void sendReply(SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        if (title != null) eb.setTitle(title);
        if (color != null) eb.setColor(color);
        else {
            if (getType()==Type.ERROR) eb.setColor(Color.RED);
            if (getType()==Type.USER_ERROR) eb.setColor(Color.YELLOW);
        }
        if (fields != null) fields.forEach(eb::addField);

        if (image != null) eb.setImage(image);
        if (thumbnail != null) eb.setThumbnail(thumbnail);

        if (author != null) eb.setAuthor(author.getName(), author.getAvatarUrl(), author.getUrl());
        if (footer != null) eb.setFooter(footer.getText(), footer.getImageUrl());

        eb.setDescription(getText());

        event.replyEmbeds(eb.build()).setEphemeral(isEphimeral()).queue();
    }

    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Author {
        private final String name;
        private String url;
        private String avatarUrl;
    }

    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Footer {
        private final String text;
        private String imageUrl;
    }
}
