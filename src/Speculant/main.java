package Speculant;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import cmds.specul;
import obj.Game;

public class main extends JavaPlugin{
	public static main instance;
	public static HashMap<Integer,Game> games=new HashMap<>();
	public static int maxGame=0;
	
	public void onEnable(){
		instance = this;
		Bukkit.getPluginCommand("specul").setExecutor(new specul());
		Bukkit.getPluginManager().registerEvents(new Events(), this);
	}
	
	public Game getPlayersGame(Player p){
		for(Game g:games.values()){
			for(String st:g.players.keySet()){
				if(st.equals(p.getName())){
					return g;
				}
			}
		}
		return null;
	}
}
