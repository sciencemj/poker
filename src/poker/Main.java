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
    public int card1, card2 = 0;
    @Override
    public void onEnable() {
        this.getCommand("poker").setExecutor(this);
        PluginDescriptionFile file = this.getDescription();
        System.out.println(file.getName() + "version:" + file.getVersion() + " loaded");
        this.getServer().getPluginManager().registerEvents(this,this);
    }
    public void giveDia(Player p, int betting){
        p.getInventory().addItem(new ItemStack(Material.DIAMOND, betting));
    }

    public void reset(){
        isGaming = false;
        p2call = false;
        p1call = false;
        p2giveup = false;
        p1giveup = false;
        betting1 = 0;
        betting2 = 0;
        count = 4;
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
                            p1.sendTitle(ChatColor.RED + "p2:" + card2, "p1:" + card1, 20, 1, 20);
                            p2.sendTitle(ChatColor.RED + "p2:" + card2, "p1:" + card1, 20, 1, 20);
                            giveDia(p2, betting1 + betting2);
                            reset();
                            Bukkit.getScheduler().cancelTask(sch);
                        }else  if (p2giveup){
                            p1.sendTitle(ChatColor.GREEN + "p1:" + card1, "p2:" + card2, 20, 1, 20);
                            p2.sendTitle(ChatColor.GREEN + "p1:" + card1, "p2:" + card2, 20, 1, 20);
                            giveDia(p1, betting1 + betting2);
                            reset();
                            Bukkit.getScheduler().cancelTask(sch);
                        }else if(isGaming == false){
                            if (card1 > card2){
                                p1.sendTitle(ChatColor.GREEN + p1.getName() + " WIN!!!", card1 + " > " + card2, 20, 1, 20);
                                p2.sendTitle(ChatColor.GREEN + p1.getName() + " WIN!!!", card1 + " > " + card2, 20, 1, 20);
                                giveDia(p1, betting1 + betting2);
                                reset();
                                Bukkit.getScheduler().cancelTask(sch);
                            }else if (card1 < card2){
                                p1.sendTitle(ChatColor.RED+ p2.getName() + " WIN!!!", card1 + " < " + card2, 20, 1, 20);
                                p2.sendTitle(ChatColor.RED + p2.getName() + " WIN!!!", card1 + " < " + card2, 20, 1, 20);
                                giveDia(p2, betting1 + betting2);
                                reset();
                                Bukkit.getScheduler().cancelTask(sch);
                            }else {
                                p1.sendTitle(ChatColor.GRAY + "BET", "", 20, 1, 20);
                                p2.sendTitle(ChatColor.GRAY + "BET", "", 20, 1, 20);
                                giveDia(p1, betting1);
                                giveDia(p2, betting2);
                                reset();
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
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
            if (isGaming == true){
                int diamond = 0;
                for (ItemStack is : p.getInventory().getContents()) {
                    if (is.getType() == Material.DIAMOND) {
                        diamond = diamond + is.getAmount();
                    }
                }
                if (p == p1){
                    if (p.getInventory().contains(Material.DIAMOND) && p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)) { //p1 betting
                        if (betting1 >= betting2) {
                            p1call = false;
                            betting1++;
                            p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                            p1.sendTitle(ChatColor.GREEN + p.getName() + ":" + betting1, "UP!", 10, 20, 10);
                            p2.sendTitle(ChatColor.GREEN + p.getName() + ":" + betting1, "UP!", 10, 20, 10);
                        }else if (diamond > (betting2 - betting1 + 1)){
                            p1call = false;
                            betting1 = betting2 + 1;
                            p.getInventory().removeItem(new ItemStack(Material.DIAMOND, (betting2 - betting1) + 1));
                            p1.sendTitle(ChatColor.GREEN + p.getName() + ":" + betting1, "UP!", 10, 20, 10);
                            p2.sendTitle(ChatColor.GREEN + p.getName() + ":" + betting1, "UP!", 10, 20, 10);
                        }
                    }
                }else if (p == p2){
                    if (p.getInventory().contains(Material.DIAMOND) && p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)) {//p2 bettting
                        if (betting1 <= betting2) {
                            p2call = false;
                            betting2++;
                            p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                            p1.sendTitle(ChatColor.RED + p.getName() + ":" + betting2, "UP!", 10, 20, 10);
                            p2.sendTitle(ChatColor.RED + p.getName() + ":" + betting2, "UP!", 10, 20, 10);
                        }else if (diamond > (betting1 - betting2 + 1)){
                            p1call = false;
                            betting2 = betting1 + 1;
                            p.getInventory().removeItem(new ItemStack(Material.DIAMOND, (betting1 - betting2) + 1));
                            p1.sendTitle(ChatColor.RED + p.getName() + ":" + betting2, "UP!", 10, 20, 10);
                            p2.sendTitle(ChatColor.RED + p.getName() + ":" + betting2, "UP!", 10, 20, 10);
                        }
                    }
                }
            }
        }else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)){//give up -----------------------------------------------------------------
            if (isGaming == true) {
                int diamond = 0;
                for (ItemStack is : p.getInventory().getContents()) {
                    if (is.getType() == Material.DIAMOND) {
                        diamond = diamond + is.getAmount();
                    }
                }
                if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {

                    if (p == p1) {
                        p1giveup = true;
                    } else if (p== p2) {
                        p2giveup = true;
                    }

                } else if (p.getInventory().contains(Material.DIAMOND) && p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)) {//called ----------------------------------------

                    if (p == p1) {
                        if (betting1 < betting2) {
                            if (diamond > (betting2 - betting1)) {
                                betting1 = betting2;
                                p.getInventory().removeItem(new ItemStack(Material.DIAMOND, betting2 - betting1));
                                p1.sendTitle(ChatColor.GREEN + p.getName() + ":" + betting1, "called", 10, 20, 10);
                                p2.sendTitle(ChatColor.GREEN + p.getName() + ":" + betting1, "called", 10, 20, 10);
                                p1call = true;
                            }else {
                                betting1 += diamond;
                                p.getInventory().removeItem(new ItemStack(Material.DIAMOND, diamond));
                                p1.sendTitle(ChatColor.GREEN + p.getName() + ":" + betting1, "ALL IN", 10, 20, 10);
                                p2.sendTitle(ChatColor.GREEN + p.getName() + ":" + betting1, "ALL IN", 10, 20, 10);
                                isGaming = false;
                            }
                        }else if (betting1 == betting2 && p1call == false){
                            p1.sendTitle(ChatColor.GREEN + p.getName(), "called", 10, 20, 10);
                            p2.sendTitle(ChatColor.GREEN + p.getName(), "called", 10, 20, 10);
                            p1call = true;
                        }
                        if (p2call == true && p1call == true){
                            isGaming = false;
                        }
                    } else if (p == p2) {
                        if (betting1 > betting2) {
                            if (diamond > (betting1 - betting2)) {
                                betting2 = betting1;
                                p.getInventory().removeItem(new ItemStack(Material.DIAMOND, betting1 - betting2));
                                p1.sendTitle(ChatColor.RED + p.getName() + ":" + betting2, "called", 10, 20, 10);
                                p2.sendTitle(ChatColor.RED + p.getName() + ":" + betting2, "called", 10, 20, 10);
                                p2call = true;
                            }else {
                                betting2 += diamond;
                                p.getInventory().removeItem(new ItemStack(Material.DIAMOND, diamond));
                                p1.sendTitle(ChatColor.RED + p.getName() + ":" + betting2, "ALL IN", 10, 20, 10);
                                p2.sendTitle(ChatColor.RED + p.getName() + ":" + betting2, "ALL IN", 10, 20, 10);
                                isGaming = false;
                            }
                        }else if (betting1 == betting2 && p2call == false){
                            p1.sendTitle(ChatColor.RED + p.getName(), "called", 10, 20, 10);
                            p2.sendTitle(ChatColor.RED + p.getName(), "called", 10, 20, 10);
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


    @Override
    public void onDisable(){
        PluginDescriptionFile file = this.getDescription();
        System.out.println(file.getName() + "version:" + file.getVersion() + " unloaded");
    }
}
