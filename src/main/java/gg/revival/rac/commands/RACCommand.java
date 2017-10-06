package gg.revival.rac.commands;

import gg.revival.rac.RAC;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RACCommand {

    @Getter private RAC rac;
    @Getter String label, syntax, description, permission;
    @Getter List<String> aliases;
    @Getter int minArgs, maxArgs;
    @Getter boolean playerOnly;

    public RACCommand(RAC rac, String label, List<String> aliases, String syntax, String description, String permission, int minArgs, int maxArgs, boolean playerOnly) {
        this.rac = rac;
        this.label = label;
        this.aliases = aliases;
        this.syntax = syntax;
        this.description = description;
        this.permission = permission;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.playerOnly = playerOnly;
    }

    public boolean validate(CommandSender sender, String args[]) {
        if(!(sender instanceof Player) && isPlayerOnly()) {
            sender.sendMessage(ChatColor.RED + "This command can not be ran through console");
            return false;
        }

        if(permission != null && sender instanceof Player) {
            Player player = (Player)sender;

            if(!player.hasPermission(permission)) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return false;
            }
        }

        if(args.length < minArgs || args.length > maxArgs) {
            sender.sendMessage(ChatColor.RED + syntax);
            return false;
        }

        return true;
    }

    public void onCommand(CommandSender sender, String args[]) {}
}
