package co.jmarango.okabe2.command.music;

import co.jmarango.okabe2.audio.MusicService;
import co.jmarango.okabe2.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class ClearCommand extends SlashCommand {
    private final MusicService musicService;
    public ClearCommand(MusicService musicService) {
        super("clear", "Limpia la lista de reproducción");

        this.musicService=musicService;
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        if (noVoiceChannelCheck(event)) return;

        musicService.clear(event.getGuild()).sendReply(event);
    }
}
