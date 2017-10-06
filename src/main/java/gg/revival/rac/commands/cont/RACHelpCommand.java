package gg.revival.rac.commands.cont;

import gg.revival.rac.RAC;
import gg.revival.rac.commands.RACCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RACHelpCommand extends RACCommand {

    public RACHelpCommand(RAC rac, String label, List<String> aliases, String syntax, String description, String permission, int minArgs, int maxArgs, boolean playerOnly) {
        super(rac, label, aliases, syntax, description, permission, minArgs, maxArgs, playerOnly);
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        for(RACCommand commands : getRac().getCommandManager().getCommands())
            sender.sendMessage(ChatColor.RED + " - " + ChatColor.DARK_RED + commands.getSyntax() + ChatColor.WHITE + " | " + commands.getDescription());
    }
}
