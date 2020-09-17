package poker;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class Main extends JavaPlugin implements Listener {
    public int count = 4;
    public int sch;
    public boolean isGaming,p1giveup,p2giveup,p1call,p2call = false;
    public Player p1;
    public Player p2;
    public int betting1 = 0;
    public int betting2 = 0;
    public int card1, card2, betting = 0;
    @Override
    public void onEnable() {
        this.getCommand("poker").setExecutor(this);
        PluginDescriptionFile file = this.getDescription();
        System.out.println(file.getName() + "version:" + file.getVersion() + " loaded");
        this.getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] strings){
        if ((cmd.getName().equals("poker"))){
            p1 = Bukkit.getPlayer(strings[0]);
            p2 = Bukkit.getPlayer(strings[1]);

            count = 4;
            sch = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    if (count == 4){
                        p1.sendTitle(ChatColor.GREEN + "게임이 시작됩니다","", 20,20,20);
                        p2.sendTitle(ChatColor.GREEN + "게임이 시작됩니다","", 20,20,20);
                        Random random = new Random();
                        card1 = random.nextInt(10);
                        card2 = random.nextInt(10);
                        count--;
                    }else if (count > 0 && count != 4) {
                        p1.sendTitle(ChatColor.GREEN + "" + count, "", 20, 1, 20);
                        p2.sendTitle(ChatColor.GREEN + "" + count, "", 20, 1, 20);
                        count--;
                    }else if (count == 0){
                        isGaming = true;
                        p1.sendMessage(ChatColor.RED + "상대패:" + card2);
                        p2.sendMessage(ChatColor.RED + "상대패:" + card1);
                        count--;
                    }else  {
                        if (p1giveup) {
                            p1.sendTitle(ChatColor.GREEN + "p2:" + card2, "p1:" + card1, 20, 1, 20);
                            p2.sendTitle(ChatColor.GREEN + "p2:" + card2, "p1:" + card1, 20, 1, 20);
                            isGaming = false;
                            p2call = false;
                            p1call = false;
                            p2giveup = false;
                            p1giveup = false;
                            betting1 = 0;
                            betting2 = 0;
                            count = 4;
                            Bukkit.getScheduler().cancelTask(sch);
                        }else  if (p2giveup){
                            p1.sendTitle(ChatColor.GREEN + "p1:" + card1, "p2:" + card2, 20, 1, 20);
                            p2.sendTitle(ChatColor.GREEN + "p1:" + card1, "p2:" + card2, 20, 1, 20);
                            isGaming = false;
                            p2call = false;
                            p1call = false;
                            p2giveup = false;
                            p1giveup = false;
                            betting1 = 0;
                            betting2 = 0;
                            count = 4;
                            Bukkit.getScheduler().cancelTask(sch);
                        }else if(isGaming == false){
                            if (card1 > card2){
                                p1.sendTitle(ChatColor.GREEN + p1.getName() + " WIN!!!", card1 + " > " + card2, 20, 1, 20);
                                p2.sendTitle(ChatColor.GREEN + p1.getName() + " WIN!!!", card1 + " > " + card2, 20, 1, 20);
                                isGaming = false;
                                p2call = false;
                                p1call = false;
                                p2giveup = false;
                                p1giveup = false;
                                betting1 = 0;
                                betting2 = 0;
                                count = 4;
                                Bukkit.getScheduler().cancelTask(sch);
                            }else if (card1 < card2){
                                p1.sendTitle(ChatColor.GREEN + p2.getName() + " WIN!!!", card1 + " < " + card2, 20, 1, 20);
                                p2.sendTitle(ChatColor.GREEN + p2.getName() + " WIN!!!", card1 + " < " + card2, 20, 1, 20);
                                isGaming = false;
                                p2call = false;
                                p1call = false;
                                p2giveup = false;
                                p1giveup = false;
                                betting1 = 0;
                                betting2 = 0;
                                count = 4;
                                Bukkit.getScheduler().cancelTask(sch);
                            }else {
                                p1.sendTitle(ChatColor.GREEN + "BET", "", 20, 1, 20);
                                p2.sendTitle(ChatColor.GREEN + "BET", "", 20, 1, 20);
                                isGaming = false;
                                p2call = false;
                                p1call = false;
                                p2giveup = false;
                                p1giveup = false;
                                betting1 = 0;
                                betting2 = 0;
                                count = 4;
                                Bukkit.getScheduler().cancelTask(sch);
                            }
                        }
                    }
                }
            },0L,20L);

        }
        return false;
    }

    @EventHandler
    public void onPlayerInterect(PlayerInteractEvent e){
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
            if (isGaming == true){
                if (e.getPlayer() == p1){
                    if (e.getPlayer().getInventory().contains(Material.DIAMOND) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)) {
                        if (betting1 < betting2) {
                            p1call = false;
                            betting1 += 2;
                            e.getPlayer().getInventory().removeItem(new ItemStack(Material.DIAMOND, 2));
                            p1.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "", 10, 10, 10);
                            p2.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "", 10, 10, 10);
                        }else if (betting1 == 0){
                            p1call = false;
                            betting1 += 1;
                            e.getPlayer().getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                            p1.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "", 10, 10, 10);
                            p2.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "", 10, 10, 10);
                        }else if (betting1 == betting2){
                            p1call = false;
                            betting1 += 1;
                            e.getPlayer().getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                            p1.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "", 10, 10, 10);
                            p2.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "", 10, 10, 10);
                        }
                    }
                }else if (e.getPlayer() == p2){
                    if (e.getPlayer().getInventory().contains(Material.DIAMOND) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)) {
                        if (betting1 > betting2) {
                            p2call = false;
                            betting2 += 2;
                            e.getPlayer().getInventory().removeItem(new ItemStack(Material.DIAMOND, 2));
                            p1.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting2, "", 10, 10, 10);
                            p2.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting2, "", 10, 10, 10);
                        }else if (betting1 == betting2){
                            p2call = false;
                            betting2 += 1;
                            e.getPlayer().getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                            p1.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting2, "", 10, 10, 10);
                            p2.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting2, "", 10, 10, 10);
                        }
                    }
                }
            }
        }else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            if (isGaming == true) {
                if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK)) {

                    if (e.getPlayer() == p1) {
                        p1giveup = true;
                    } else if (e.getPlayer() == p2) {
                        p2giveup = true;
                    }

                } else {
                    if (e.getPlayer().getInventory().contains(Material.DIAMOND) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)) {
                        if (e.getPlayer() == p1) {
                            if (betting1 < betting2) {
                                betting1++;
                                e.getPlayer().getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                                p1.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "called", 10, 10, 10);
                                p2.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "called", 10, 10, 10);
                                p1call = true;
                            }else if (betting1 == betting2 && p1call == false){
                                p1.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "called", 10, 10, 10);
                                p2.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting1, "called", 10, 10, 10);
                                p1call = true;
                            }
                            if (p2call == true && p1call == true){
                                isGaming = false;
                            }
                        } else if (e.getPlayer() == p2) {
                            if (betting1 > betting2) {
                                betting2++;
                                e.getPlayer().getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                                p1.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting2, "called", 10, 10, 10);
                                p2.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting2, "called", 10, 10, 10);
                                p2call = true;
                            }else if (betting1 == betting2 && p2call == false){
                                p1.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting2, "called", 10, 10, 10);
                                p2.sendTitle(ChatColor.GREEN + e.getPlayer().getName() + ":" + betting2, "called", 10, 10, 10);
                                p2call = true;
                            }
                            if (p2call == true && p1call == true){
                                isGaming = false;
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onDisable(){
        PluginDescriptionFile file = this.getDescription();
        System.out.println(file.getName() + "version:" + file.getVersion() + " unloaded");
    }
}
