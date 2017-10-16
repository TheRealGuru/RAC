package gg.revival.rac.commands.cont;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import gg.revival.rac.RAC;
import gg.revival.rac.commands.RACCommand;
import gg.revival.rac.learning.DataSetType;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RACLearnCommand extends RACCommand {

    public RACLearnCommand(RAC rac, String label, List<String> aliases, String syntax, String description, String permission, int minArgs, int maxArgs, boolean playerOnly) {
        super(rac, label, aliases, syntax, description, permission, minArgs, maxArgs, playerOnly);
    }

    // TODO: Finish this, not done yet

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(args.length == 3) {
            if(args[1].equalsIgnoreCase("cancel")) {
                String namedPlayer = args[2];

                if(Bukkit.getPlayer(namedPlayer) == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }

                Player learningPlayer = Bukkit.getPlayer(namedPlayer);

                if(getRac().getLearningManager().getReachLearning().getTrackedPlayers().containsKey(learningPlayer.getUniqueId())) {
                    getRac().getLearningManager().getReachLearning().getTrackedPlayers().remove(player.getUniqueId());
                    getRac().getLearningManager().getReachLearning().getSprintDistances().remove(player.getUniqueId());
                    getRac().getLearningManager().getReachLearning().getWalkDistances().remove(player.getUniqueId());

                    player.sendMessage(getRac().getNotifications().getPrefix() + ChatColor.YELLOW + " Stopped learning " + player.getName() + " for " + ChatColor.RED + "Reach");
                }

                return;
            }
        }

        if(args.length == 5) {
            if(args[1].equalsIgnoreCase("start")) {
                String namedPlayer = args[2];
                String namedType = args[3];
                String namedSampleCount = args[4];

                if(Bukkit.getPlayer(namedPlayer) == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }

                Player learningPlayer = Bukkit.getPlayer(namedPlayer);
                DataSetType type = null;

                for(DataSetType types : DataSetType.values()) {
                    if(types.toString().equalsIgnoreCase(namedType))
                        type = types;
                }

                if(type == null) {
                    List<String> validTypes = Lists.newArrayList();

                    for(DataSetType types : DataSetType.values())
                        validTypes.add(types.toString());

                    player.sendMessage(ChatColor.RED + "Invalid data set type. Here is a list of valid types: " + ChatColor.BLUE + Joiner.on(ChatColor.YELLOW + ", " + ChatColor.BLUE).join(validTypes));

                    return;
                }

                if(!NumberUtils.isNumber(namedSampleCount)) {
                    player.sendMessage(ChatColor.RED + "Invalid sample count");
                    return;
                }

                int sampleCount = NumberUtils.toInt(namedSampleCount);

                if(type.equals(DataSetType.REACH))
                    getRac().getLearningManager().getReachLearning().getTrackedPlayers().put(learningPlayer.getUniqueId(), sampleCount);

                player.sendMessage(getRac().getNotifications().getPrefix() + ChatColor.YELLOW + " Now learning " + ChatColor.BLUE + learningPlayer.getName() + ChatColor.YELLOW + " for " + ChatColor.RED + type.toString());

                return;
            }
        }

        player.sendMessage(ChatColor.RED + "/rac learn start <player> <type> <samplecount>");
        player.sendMessage(ChatColor.RED + "/rac learn cancel <player>");
        player.sendMessage(ChatColor.RED + "/rac learn add <type> <info>");
    }

}
