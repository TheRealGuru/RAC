package gg.revival.rac.commands.cont;

import gg.revival.rac.RAC;
import gg.revival.rac.commands.RACCommand;
import gg.revival.rac.modules.Cheat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RACSearchCommand extends RACCommand {

    public RACSearchCommand(RAC rac, String label, List<String> aliases, String syntax, String description, String permission, int minArgs, int maxArgs, boolean playerOnly) {
        super(rac, label, aliases, syntax, description, permission, minArgs, maxArgs, playerOnly);
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;
        Cheat cheatToSearch = null;

        for(Cheat cheatTypes : Cheat.values()) {
            if(args[1].equalsIgnoreCase(cheatTypes.toString()))
                cheatToSearch = cheatTypes;
        }

        if(cheatToSearch != null) {
            getRac().getCheckManager().showCheckViolations(player, getRac().getCheckManager().getCheckByCheat(cheatToSearch));
            return;
        }

        if(Bukkit.getPlayer(args[1]) == null || !Bukkit.getPlayer(args[1]).isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        getRac().getCheckManager().showPlayerViolations(player, Bukkit.getPlayer(args[1]));
    }
}
