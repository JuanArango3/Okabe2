package co.jmarango.okabe2.command.music;

import co.jmarango.okabe2.audio.MusicService;
import co.jmarango.okabe2.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class SkipCommand extends SlashCommand {
    private final MusicService musicService;

    public SkipCommand(MusicService musicService) {
        super("skip", "Skip la cancion actual");

        this.musicService=musicService;
    }
    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        musicService.skipTrack(event.getChannel().asTextChannel()).sendReply(event);
    }
}
