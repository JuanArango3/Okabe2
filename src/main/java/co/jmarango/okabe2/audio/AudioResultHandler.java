package co.jmarango.okabe2.audio;

import co.jmarango.okabe2.dto.VideoInfo;
import co.jmarango.okabe2.dto.response.Response;
import co.jmarango.okabe2.dto.response.RichResponse;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class AudioResultHandler implements AudioLoadResultHandler {
    public AudioResultHandler(MusicService musicService, Guild guild, Member member) {
        this.musicService = musicService;
        this.guild = guild;
        this.member = member;
    }


    @Getter
    private Response response;

    private final MusicService musicService;
    private final Guild guild;
    private final Member member;

    @Override
    public void trackLoaded(AudioTrack track) {
        RichResponse r = new RichResponse();
        r.setColor(new Color(0, 51, 102));
        r.setTitle("Canción añadida a la cola");
        r.setText(String.format("[%s](%s) de `%s`", track.getInfo().title, track.getInfo().uri, track.getInfo().author));

        List<MessageEmbed.Field> fields = new ArrayList<>();
        fields.add(new MessageEmbed.Field("Duración", new VideoInfo(track.getInfo()).durationToReadable(), false));

        int size = musicService.getGuildMusicManager(guild).getScheduler().getQueueSize()+1;
        fields.add(new MessageEmbed.Field("En cola", String.format(size==1?"%d canción":"%d canciones", size), false));

        r.setFields(fields);

        r.setFooter(new RichResponse.Footer(String.format("Agregada por %s", member.getEffectiveName()), member.getEffectiveAvatarUrl()));


        response = r;
        musicService.play(guild, musicService.getGuildMusicManager(guild), track, member);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        if (playlist.isSearchResult()) {
            trackLoaded(firstTrack);
            return;
        }

        response=new Response(String.format("Agregada la playlist %s con %s canciones", playlist.getName(), playlist.getTracks().size()), Response.Type.OK, false);
        musicService.play(guild, musicService.getGuildMusicManager(guild), firstTrack, member);
    }

    @Override
    public void noMatches() {
        response=new Response("No se encontró nada", Response.Type.USER_ERROR, false);
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        response=new Response("Error interno", Response.Type.ERROR, false);
        log.error("Error cargando el track", exception);
    }
}
