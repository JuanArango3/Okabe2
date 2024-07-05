package co.jmarango.okabe2.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;

@Getter
public class GuildMusicManager {
    public final AudioPlayer player;
    public final TrackScheduler scheduler;

    public GuildMusicManager(AudioPlayerManager manager) {
        player=manager.createPlayer();
        scheduler=new TrackScheduler(player);
        player.addListener(scheduler);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }
}
