package com.woutwoot.noflyzone;

import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author woutwoot
 *         Created by on 5/01/2015 - 11:04.
 */
public class Main extends JavaPlugin implements Listener {

    private List<String> regionsEnabled = new ArrayList<String>();
    private File saveFile;

    @Override
    public void onEnable(){
        this.getDataFolder().mkdir();
        saveFile = new File(this.getDataFolder() + File.separator + "regions.txt");
        load();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable(){
        save();
    }

    @EventHandler
    public void onRegionEntered(RegionEnteredEvent event){
        if(regionsEnabled.contains(event.getRegion().getId())){
            if(!event.getPlayer().hasPermission("noflyzone.ignore")){
                if(event.getPlayer().isFlying() || event.getPlayer().getAllowFlight()) {
                    event.getPlayer().setFlying(false);
                    event.getPlayer().setAllowFlight(false);
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "FLYING IS NOT ALLOWED IN THIS AREA");
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("noflyzone")){
            if(args.length == 2){
                if(args[0].equalsIgnoreCase("add")){
                    this.regionsEnabled.add(args[1]);
                    save();
                    sender.sendMessage("Added. (remember, you still have to block /fly in the region!)");
                }
                if(args[0].equalsIgnoreCase("remove")){
                    this.regionsEnabled.remove(args[1]);
                    save();
                    sender.sendMessage("Removed.");
                }
            }else{
                sender.sendMessage("Wrong args.");
            }
            return true;
        }
        return false;
    }

    public void load(){
        try {
            regionsEnabled.addAll(readLines());
        } catch (IOException e) {}
    }

    public void save(){
        FileWriter writer = null;
        try {
            writer = new FileWriter(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String str: regionsEnabled) {
            try {
                writer.write(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readLines() throws IOException {
        FileReader fileReader = new FileReader(saveFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines;
    }

}
