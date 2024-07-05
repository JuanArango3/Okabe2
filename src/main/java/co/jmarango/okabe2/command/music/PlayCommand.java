package co.jmarango.okabe2.command.music;

import co.jmarango.okabe2.audio.MusicService;
import co.jmarango.okabe2.command.SlashCommand;
import co.jmarango.okabe2.dto.response.Response;
import co.jmarango.okabe2.dto.response.RichResponse;
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
            if (event.getGuild().getVoiceChannels().parallelStream().filter(vc->vc.getMembers().contains(event.getMember())).findFirst().isEmpty()) {
                RichResponse response = new RichResponse();
                response.setEphimeral(true);
                response.setType(Response.Type.USER_ERROR);
                response.setTitle("No estás en ningún canal de voz");

                response.sendReply(event);
                return;
            }

            //event.reply("no").queue();
            musicService.loadAndPlay(event.getChannel().asTextChannel(), cancion, event.getMember()).sendReply(event);
            //musicService.loadAndPlay(event.getChannel().asTextChannel(), event.getOption("canción", OptionMapping::getAsString), event.getMember());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
