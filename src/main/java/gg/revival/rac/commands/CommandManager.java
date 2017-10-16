package gg.revival.rac.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gg.revival.rac.RAC;
import gg.revival.rac.commands.cont.RACHelpCommand;
import gg.revival.rac.commands.cont.RACLearnCommand;
import gg.revival.rac.commands.cont.RACSearchCommand;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

public class CommandManager {

    @Getter RAC rac;
    @Getter public Set<RACCommand> commands = Sets.newHashSet();

    public CommandManager(RAC rac) {
        this.rac = rac;

        // rac, label, aliases, syntax, description, permission, minArgs, maxArgs, playerOnly

        RACHelpCommand helpCommand = new RACHelpCommand(rac, "help", Lists.newArrayList(), "/rac help", "View RAC commands", Permissions.RAC_COMMANDS, 1, 1, false);
        commands.add(helpCommand);

        RACSearchCommand searchCommand = new RACSearchCommand(rac, "search", Arrays.asList("lookup", "find"), "/rac search <cheat/player>", "Lookup a specified player or cheat type", Permissions.RAC_COMMANDS, 2, 2, true);
        commands.add(searchCommand);

        RACLearnCommand learnCommand = new RACLearnCommand(rac, "learn", null, "/rac learn", "Machine learning", Permissions.LEARNING_ACCESS, 2, 5, true);
        commands.add(learnCommand);

        rac.getLog().log("Loaded " + commands.size() + " commands");
    }
}
