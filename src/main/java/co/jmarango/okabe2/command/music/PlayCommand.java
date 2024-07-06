package co.jmarango.okabe2.command.music;

import co.jmarango.okabe2.audio.MusicService;
import co.jmarango.okabe2.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class PlayCommand extends SlashCommand {

    private final MusicService musicService;
    public PlayCommand(MusicService musicService) {
        super("play", "Agrega un video (o playlist) a la cola");
        addOption(new OptionData(OptionType.STRING, "canción", "URL o nombre de la canción", true));

        this.musicService=musicService;
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        try {
            String cancion = event.getOption("canción", OptionMapping::getAsString);
            try {
                new URI(cancion);
            } catch (URISyntaxException e) {
                cancion = "ytsearch:"+cancion;
            }

            if (noVoiceChannelCheck(event)) return;


            //event.reply("no").queue();
            musicService.loadAndPlay(event.getChannel().asTextChannel(), cancion, event.getMember()).sendReply(event);
            //musicService.loadAndPlay(event.getChannel().asTextChannel(), event.getOption("canción", OptionMapping::getAsString), event.getMember());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
