package co.jmarango.okabe2.audio;

import co.jmarango.okabe2.dto.response.Response;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class MusicService {

    private final AudioPlayerManager audioManager;

    private final Map<Long, GuildMusicManager> managers=new HashMap<>();

    public MusicService(@Lazy AudioPlayerManager audioManager) {
        this.audioManager = audioManager;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        GuildMusicManager musicManager = managers.computeIfAbsent(guild.getIdLong(), a -> new GuildMusicManager(audioManager));

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public Response loadAndPlay(final TextChannel channel, final String trackUrl, Member member) throws ExecutionException, InterruptedException {
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

        AudioResultHandler audioResult = new AudioResultHandler(this, channel.getGuild(), member);
        audioManager.loadItemOrdered(musicManager, trackUrl, audioResult).get();

        return audioResult.getResponse();
    }

    public Response skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());
        musicManager.getScheduler().nextTrack();

        return new Response("Skipeado", Response.Type.OK, false);
    }

    public Response clear(Guild guild) {
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        musicManager.getScheduler().clear();

        return new Response("Limpiado", Response.Type.OK, false);
    }

    public void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, Member member) {
        connectToChannel(guild.getAudioManager(), member);
        track.setUserData(member);
        musicManager.getScheduler().queue(track);
    }

    public void playPlaylist(Guild guild, GuildMusicManager musicManager, AudioPlaylist playlist, Member member) {
        connectToChannel(guild.getAudioManager(), member);

        for (AudioTrack track : playlist.getTracks()) {
            track.setUserData(member);
            musicManager.getScheduler().queue(track);
        }

    }

    public boolean connectToChannel(AudioManager audioManager, Member member) {
        if (audioManager.isConnected()) return false;

        try {
            audioManager.openAudioConnection(audioManager.getGuild().getVoiceChannels().stream().filter(voiceChannel -> voiceChannel.getMembers().contains(member)).findFirst().orElseThrow());
            return true;
        } catch (Exception ex) {
            log.error("Error uniendose al canal: ",ex);
            return false;
        }

    }

}
