package Speculant;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import obj.Game;
import obj.PlayerInfo;

public class Events implements Listener{
	@EventHandler
	public void click(InventoryClickEvent e){
		if(e.getClickedInventory() != null) {
			Player p = (Player) e.getWhoClicked();
			if(e.getCurrentItem()!=null){
				ItemStack item=e.getCurrentItem();
				Game game=main.instance.getPlayersGame(p);
				PlayerInfo pi=game.players.get(p.getName());
				int slot=e.getSlot();
				if(e.getView().getTitle().equals(ChatColor.AQUA+"Спекулянт")){
					e.setCancelled(true);
					if(game.cancel)return;
					if(item.getType().equals(Material.TOTEM_OF_UNDYING)){
						pi.next=!pi.next;
						if(!pi.next){
							for(Player pl:game.getPlayers()){
								pl.playSound(pl.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 0);
							}
						}else{
							for(Player pl:game.getPlayers()){
								pl.playSound(pl.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 2);
							}
							boolean next=true;
							for(PlayerInfo pli:game.players.values()){
								if(!pli.next){
									next=false;
									break;
								}
							}
							if(next){
								for(PlayerInfo pli:game.players.values()){
									pli.next=false;
								}
								game.setCards();
							}
						}
						game.updateInventory();
					}
					if(slot>=28&&slot<=34&&slot%2==0){
						int ssl=(slot-28)/2;
						if(e.isLeftClick()){
							if(game.futureMoney(pi)>=game.fprice(game.set[ssl], pi.changes[ssl])&&game.buyable(game.set[ssl]))
							pi.changes[ssl]++;
						}else{
							if(pi.haves[ssl]+pi.changes[ssl]>0)
							pi.changes[ssl]--;
						}
						game.updateInventory(p);
					}
				}
			}
		}
	}
}
