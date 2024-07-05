package co.jmarango.okabe2.dto;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class VideoInfo {
    private final AudioTrackInfo info;

    public String durationToReadable() {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(info.length),
                TimeUnit.MILLISECONDS.toSeconds(info.length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(info.length)));
    }
}
