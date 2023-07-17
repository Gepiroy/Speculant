package obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import Speculant.main;
import utilsSpeculant.GepUtil;
import utilsSpeculant.ItemUtil;
import utilsSpeculant.TextUtil;

public class Game {
	public HashMap<String,PlayerInfo> players=new HashMap<>();
	public List<Integer> cards=new ArrayList<>();
	public int[] settings={0,6,1,0,30};
	//Джокер;мин. карта;комиссия(да/нет);траты на жизнь.
	public Integer[] set={0,0,0,0};
	public int gerPer=1;
	public int plam=0;
	public boolean cancel=false;
	public int anim=0;
	
	Random r=new Random();
	
	public Game(){
		recardo();
	}
	
	public void start(){
		for(PlayerInfo pi:players.values()){
			pi.Money=settings[4];
		}
		setCards();
		updateInventory();
	}
	
	public void recardo(){
		for(int i=settings[1];i<=15;i++){
			if(i>=15){
				cards.add(15);
				cards.add(15);
				break;
			}
			for(int ii=0;ii<4;ii++){
				cards.add(i);
			}
		}
	}
	
	public void setCards(){
		cancel=true;
		for(Player p:getPlayers()){
			PlayerInfo pi=players.get(p.getName());
			pi.Money=futureMoney(pi);
			for(int i=0;i<4;i++){
				pi.haves[i]+=pi.changes[i];
				pi.changes[i]=0;
			}
			p.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
		}
		int tuzes=0;
		for(int i:cards){
			if(i==14)tuzes++;
		}
		if(tuzes==0){
			recardo();
			for(int s=0;s<4;s++){
				set[s]=0;
			}
			for(Player p:getPlayers()){
				p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 0);
				updateInventory(p);
			}
		}
		new BukkitRunnable(){
			int t=20;
			int i=0;
			boolean j=false;
			@Override
			public void run() {
				t--;
				if(t<=0){
					if(i>=4){
						cancel=false;
						if(j){
							for(Player p:getPlayers()){
								PlayerInfo pi=players.get(p.getName());
								for(int s=0;s<4;s++){
									if(pi.haves[s]>0){
										int price=price(set[s]);
										pi.Money+=price*pi.haves[s];
										pi.haves[s]=0;
									}
								}
							}
						}
						for(Player p:getPlayers()){
							p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 0.5f+r.nextFloat()*1.5f);
						}
						anim=0;
						updateInventory();
						this.cancel();
						return;
					}
					anim=i+1;
					int cn=r.nextInt(cards.size());
					int card=cards.get(cn);
					set[i]=card;
					cards.remove(cn);
					t=15;
					if(card<11){
						for(Player p:getPlayers()){
							p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 2f-card*0.1f);
						}
					}else if(card==11){//Валет
						for(Player p:getPlayers()){
							p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.8f+r.nextFloat()/2);
						}
					}else if(card==12){//Дама
						for(Player p:getPlayers()){
							p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1.5f+r.nextFloat()/2);
						}
					}else if(card==13){//Король
						for(Player p:getPlayers()){
							PlayerInfo pi=players.get(p.getName());
							if(pi.haves[i]>0){
								pi.Money+=2*pi.haves[i];
								p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 2.2f-pi.haves[i]*0.2f);
							}
							p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 0.5f+r.nextFloat()/2);
						}
					}else if(card==14){//Туз
						for(Player p:getPlayers()){
							PlayerInfo pi=players.get(p.getName());
							if(pi.haves[i]>0){
								int tuzes=0;
								for(int i:cards){
									if(i==14)tuzes++;
								}
								if(tuzes>0)pi.Money+=(Math.ceil(settings[1]/2.0))*pi.haves[i];
								pi.haves[i]=0;
							}
							p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 0.5f+r.nextFloat()/2);
						}
					}else if(card==15){//Джокер
						j=true;
						for(Player p:getPlayers()){
							p.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 1, 0.5f+r.nextFloat()*1.5f);
						}
					}
					i++;
					updateInventory();
				}
			}
		}.runTaskTimer(main.instance, 1, 1);
	}
	
	public ItemStack itemCard(Player p, int n){
		ItemStack ret=ItemUtil.create(Material.PAPER, n, ""+n, new String[]{"Обычная карта."}, null, 0);
		if(n==11)ret=ItemUtil.create(Material.MAP, 1, ChatColor.YELLOW+"Валет", new String[]{"Обычная карта,","почти максимальная цена."}, null, 0);
		if(n==12)ret=ItemUtil.create(Material.BOOK, 1, ChatColor.AQUA+"Дама", new String[]{"Максимальная цена!"}, null, 0);
		if(n==13)ret=ItemUtil.create(Material.WRITABLE_BOOK, 1, ChatColor.GREEN+"Король", new String[]{"Выплата дивидендов."}, null, 0);
		if(n==14)ret=ItemUtil.create(Material.ENDER_EYE, 1, ChatColor.RED+"Туз", new String[]{ChatColor.RED+"Банкротство компании."}, null, 0);
		if(n==15){
			ret=ItemUtil.create(Material.NETHER_STAR, 1, ChatColor.LIGHT_PURPLE+"Джокер", new String[]{""}, Enchantment.LUCK, 10);
			ItemMeta meta=ret.getItemMeta();
			List<String> lore=new ArrayList<>();
			if(settings[0]==0){
				lore.add(ChatColor.LIGHT_PURPLE+"Из за эмоций, владельцы");
				lore.add(ChatColor.LIGHT_PURPLE+"этой карты продают все");
				lore.add(ChatColor.LIGHT_PURPLE+"свои карты по выпавшей цене.");
			}
			meta.setLore(lore);
			ret.setItemMeta(meta);
		}
		return ret;
	}
	
	public void updateInventory(){
		for(Player p:getPlayers()){
			updateInventory(p);
		}
	}
	
	public void updateInventory(Player p){
		PlayerInfo pi=players.get(p.getName());
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.AQUA+"Спекулянт");
		for(int i=0;i<54;i++){
			if(!cancel)inv.setItem(i, ItemUtil.create(Material.YELLOW_STAINED_GLASS_PANE, 1, " ", null, null, 0));
			else inv.setItem(i, ItemUtil.create(Material.ORANGE_STAINED_GLASS_PANE, 1, " ", null, null, 0));
		}
		int onum=0;
		for(Player pl:getPlayers()){
			if(pl.getName().equals(p.getName()))continue;
			PlayerInfo pli=players.get(pl.getName());
			Enchantment ench=null;
			if(pli.next)ench=Enchantment.SWEEPING_EDGE;
			inv.setItem(onum, ItemUtil.create(Material.GOLD_INGOT, (int)pli.Money/10, ChatColor.GOLD+"Деньги "+ChatColor.YELLOW+pl.getName(), new String[]{
					ChatColor.YELLOW+GepUtil.CylDouble(pli.Money,"#0.00"),
					ChatColor.GRAY+"("+ChatColor.WHITE+"Будет "+GepUtil.CylDouble(futureMoney(pli),"#0.00")+ChatColor.GRAY+")"
			}, ench, 10));
			inv.setItem(9+onum, ItemUtil.create(Material.GOLD_NUGGET, (int)pli.Money%10, ChatColor.GOLD+"Деньги "+ChatColor.YELLOW+pl.getName(), new String[]{
					ChatColor.YELLOW+GepUtil.CylDouble(pli.Money,"#0.00"),
					ChatColor.GRAY+"("+ChatColor.WHITE+"Будет "+GepUtil.CylDouble(futureMoney(pli),"#0.00")+ChatColor.GRAY+")"
			}, ench, 10));
			onum++;
		}
		for(int i=0;i<4;i++){
			List<String> lore = new ArrayList<>();
			lore.add(TextUtil.string("&bУ вас &a"+pi.haves[i]));
			for(String st:players.keySet()){
				if(st!=p.getName()){
					lore.add(TextUtil.string("|У &f"+st+" |"+players.get(st).haves[i]));
				}
			}
			ItemStack ami=ItemUtil.createal(Material.IRON_NUGGET, pi.haves[i], ChatColor.GRAY+"Имеется", lore, null, 0);
			if(pi.haves[i]<=0){
				ami.setType(Material.COBWEB);
				ami.setAmount(1);
			}
			inv.setItem(19+i*2, ami);
			ItemStack card=itemCard(p,set[i]);
			ItemMeta meta=card.getItemMeta();
			lore=meta.getLore();
			if(lore==null)lore=new ArrayList<>();
			String your="&2У вас &"+TextUtil.bool("b", "7", pi.haves[i]>0)+pi.haves[i];
			if(pi.changes[i]!=0)your+=" |-> &"+TextUtil.bool("a", "c", pi.changes[i]>0)+(pi.haves[i]+pi.changes[i]);
			lore.add(TextUtil.string(your));
			double fm=futureMoney(pi);
			//Цена: 8+0.5
			//$30.00 -> 22.00
			int price=price(set[i]);
			double fprice=fprice(set[i],pi.changes[i]);
			String buy="&bЦена: &";
			if(fm-price>0){
				if(fm-fprice>0){
					buy+="a";
				}else{
					buy+="e";
				}
			}else{
				buy+="c";
			}
			buy+=price;
			if(fprice>price)buy+="&6+"+GepUtil.CylDouble(pi.changes[i]*0.5, "#0.0");
			String m="&2$&f"+pi.Money;
			if(fm!=pi.Money)m+=" &7-> &"+TextUtil.bool("a", "c", fm>pi.Money)+GepUtil.CylDouble(fm, "#0.0");
			lore.add(TextUtil.string(buy));
			lore.add(TextUtil.string(m));
			meta.setLore(lore);
			card.setItemMeta(meta);
			inv.setItem(28+i*2, card);
		}//19+i*2
		if(anim>0){
			inv.setItem(35+anim*2, ItemUtil.create(Material.WHITE_STAINED_GLASS_PANE, 1, " ", null, null, 0));
		}
		int tuzes=0;
		for(int i:cards){
			if(i==14)tuzes++;
		}
		if(tuzes<4)
		inv.setItem(45, ItemUtil.create(Material.ENDER_EYE, 4-tuzes, ChatColor.RED+"Тузы", new String[]{
				ChatColor.RED+""+(4-tuzes)+ChatColor.GOLD+"/4"
		}, null, 0));
		Enchantment next=null;
		if(pi.next){
			next=Enchantment.SWEEPING_EDGE;
		}
		inv.setItem(53, ItemUtil.create(Material.TOTEM_OF_UNDYING, 1, ChatColor.AQUA+"Готов", new String[]{
				ChatColor.GOLD+"Согласие на следующий ход."
		}, next, 10));
		inv.setItem(52, ItemUtil.create(Material.GOLD_INGOT, (int)pi.Money/10, ChatColor.GOLD+"Деньги", new String[]{
				ChatColor.GOLD+"У вас "+ChatColor.YELLOW+GepUtil.CylDouble(pi.Money,"#0.00"),
				ChatColor.GRAY+"("+ChatColor.WHITE+"Будет "+GepUtil.CylDouble(futureMoney(pi),"#0.00")+ChatColor.GRAY+")"
		}, null, 0));
		inv.setItem(51, ItemUtil.create(Material.GOLD_NUGGET, (int)pi.Money%10, ChatColor.GOLD+"Деньги", new String[]{
				ChatColor.GOLD+"У вас "+ChatColor.YELLOW+GepUtil.CylDouble(pi.Money,"#0.00"),
				ChatColor.GRAY+"("+ChatColor.WHITE+"Будет "+GepUtil.CylDouble(futureMoney(pi),"#0.00")+ChatColor.GRAY+")"
		}, null, 0));
		p.openInventory(inv);
	}
	public List<Player> getPlayers(){
		List<Player> ret=new ArrayList<>();
		for(String st:new ArrayList<>(players.keySet())){
			Player p=Bukkit.getPlayer(st);
			if(p!=null){
				ret.add(p);
			}else{
				players.remove(st);
			}
		}
		return ret;
	}
	public int price(int card){
		int price=card;
		if(card==13){//Король
			price=10;
		}else if(card==14){//Туз
			price=(int) (Math.ceil(settings[1]/2.0));
		}else if(card==15){//Джокер
			price=settings[1];
		}
		return price;
	}
	public double fprice(int card,int changes){
		double price=price(card);
		price+=0.5*changes;
		return price;
	}
	public boolean buyable(int card){
		if(card>=14)return false;
		return true;
	}
	public double futureMoney(PlayerInfo pi){
		double ret=pi.Money;
		for(int i=0;i<4;i++){
			if(pi.changes[i]!=0){
				int price=price(set[i]);
				ret-=price*pi.changes[i];
				for(int m=0;m<pi.changes[i];m++){
					ret-=0.5*m;
				}
			}
		}
		ret-=settings[3];
		return ret;
	}
}
