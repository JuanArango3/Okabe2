package co.jmarango.okabe2.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class SlashCommand {
    private final String name;
    private final String description;
    private Permission permission;
    private List<OptionData> optionDataList=new ArrayList<>();

    protected void addOption(OptionData optionData) {
        optionDataList.add(optionData);
    }

    public abstract void onCommand(SlashCommandInteractionEvent event);
}
