package co.jmarango.okabe2.config;

import co.jmarango.okabe2.audio.MusicService;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AudioConfig extends ListenerAdapter {

    private final MusicService musicService;

    @Bean
    public AudioPlayerManager setupAudioSources() {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        YoutubeAudioSourceManager ytSourceManager = new YoutubeAudioSourceManager();

        playerManager.registerSourceManager(ytSourceManager);

        //noinspection deprecation
        AudioSourceManagers.registerRemoteSources(playerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);

        return playerManager;
    }


    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent e) {
        if (e.getChannelLeft()==null) return;
        GuildVoiceState state = e.getGuild().getSelfMember().getVoiceState();
        assert state != null;
        if (state.getChannel()==null) return;

        AudioChannelUnion channel = state.getChannel();
        if ( channel.getId().equals(e.getChannelLeft().getId()) ) {
            if (channel.getMembers().size()==1) {
                musicService.getGuildMusicManager(e.getGuild()).getScheduler().clear();
                e.getGuild().getAudioManager().closeAudioConnection();
            }
        }

    }
}
