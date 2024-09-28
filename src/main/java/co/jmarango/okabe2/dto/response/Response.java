package co.jmarango.okabe2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class Response {

    private String text;
    private Type type=Type.OK;

    private boolean ephimeral = false;

    public enum Type {
        OK, ERROR, USER_ERROR
    }

    public void editReply(SlashCommandInteractionEvent event) {
        event.getInteraction().getHook().editOriginal(text).queue();
    }

    public void sendReply(SlashCommandInteractionEvent event) {
        event.reply(text).setEphemeral(ephimeral).queue();
    }
}
