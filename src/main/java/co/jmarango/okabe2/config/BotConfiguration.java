package co.jmarango.okabe2.config;

import co.jmarango.okabe2.audio.MusicService;
import co.jmarango.okabe2.command.SlashCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BotConfiguration extends ListenerAdapter {

    private final List<SlashCommand> slashCommands;

    private final MusicService musicService;

    @Value("${app.token}")
    private String token;

    @Bean
    public JDA jda() {
        JDA jda;
        try {
            jda = JDABuilder
                    .createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .addEventListeners(this)
                    .build().awaitReady();
        } catch (InterruptedException e) {
            log.error("Error al iniciar instancia JDA", e);
            return null;
        }

        Set<CommandData> commands = new HashSet<>();
        slashCommands.parallelStream().forEach(slashCommand -> {
            SlashCommandData data = Commands.slash(slashCommand.getName(), slashCommand.getDescription());

            if (slashCommand.getPermission()!=null) data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(slashCommand.getPermission()));
            data.setGuildOnly(true);
            data.addOptions(slashCommand.getOptionDataList());
            commands.add(data);
        });
        jda.updateCommands().addCommands(commands).complete();

        return jda;
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


    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        Optional<SlashCommand> commandOptional = slashCommands.parallelStream().filter(
                slashCommand -> slashCommand.getName().equalsIgnoreCase(e.getName())
        ).findFirst();

        commandOptional.ifPresent(slashCommand -> slashCommand.onCommand(e));
    }
}
