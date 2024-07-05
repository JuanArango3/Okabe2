package co.jmarango.okabe2.audio;

import co.jmarango.okabe2.dto.response.Response;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
@Slf4j
public class MusicService {
    private final Map<Long, GuildMusicManager> managers=new HashMap<>();

    @Bean
    public AudioPlayerManager audioPlayerManager() {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        return playerManager;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        GuildMusicManager musicManager = managers.computeIfAbsent(guild.getIdLong(), a -> new GuildMusicManager(audioPlayerManager()));

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public Response loadAndPlay(final TextChannel channel, final String trackUrl, Member member) throws ExecutionException, InterruptedException {
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

        AudioResultHandler audioResult = new AudioResultHandler(this, channel.getGuild(), member);
        audioPlayerManager().loadItemOrdered(musicManager, trackUrl, audioResult).get();

        return audioResult.getResponse();
    }

    public Response skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());
        musicManager.scheduler.nextTrack();

        return new Response("Skipeado", Response.Type.OK, false);
    }

    public void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, Member member) {
        log.info("Reproduciendo {} en {} pedida por {}", track.getInfo().title, member.getGuild().getName(), member.getEffectiveName());
        connectToChannel(guild.getAudioManager(), member);

        musicManager.scheduler.queue(track);
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
