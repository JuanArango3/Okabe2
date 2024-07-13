package co.jmarango.okabe2.config;

import co.jmarango.okabe2.command.SlashCommand;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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

import java.util.*;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BotConfiguration extends ListenerAdapter {

    private final List<SlashCommand> slashCommands;

    private final List<ListenerAdapter> listeners;

    @Value("${app.token}")
    private String token;

    @Bean
    public JDA jda() {
        log.info("Starting Bot");

        if (token.equals("default")) throw new IllegalArgumentException("Bot token not specified in environment / application.properties");

        if (!listeners.contains(this)) listeners.add(this);

        JDA jda;
        try {

            jda = JDABuilder
                    .createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build().awaitReady();

            listeners.forEach(jda::addEventListener);
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

        log.info("Bot started");
        return jda;
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down");

        try {
            jda().awaitShutdown();
        } catch (InterruptedException e) {
            log.error("Error during bot shutdown", e);
        }
    }


    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        Optional<SlashCommand> commandOptional = slashCommands.parallelStream().filter(
                slashCommand -> slashCommand.getName().equalsIgnoreCase(e.getName())
        ).findFirst();

        commandOptional.ifPresent(slashCommand -> slashCommand.onCommand(e));
    }
}
