package utilsSpeculant;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class ItemUtil {
	public static ItemStack create(Material material, int amount, String DisplayName, String[] lore1, Enchantment[] ench, int[] lvl) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if(DisplayName != null){
			meta.setDisplayName(DisplayName);
		}
		ArrayList<String> lore = new ArrayList<String>();
		if(lore1 != null){
			for(int i=0;i<lore1.length;i++){
				lore.add(lore1[i]);
			}
		}
		meta.setLore(lore);
		if(ench != null){
			for(int i=0;i<ench.length;i++){
				if(ench[i]!=null){
					meta.addEnchant(ench[i], lvl[i], true);
				}
			}
		}
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack create(Material material, int amount, String DisplayName, String[] lore1, Enchantment ench, int lvl) {
		List<String> lore = new ArrayList<String>();
		if(lore1 != null){
			for(int i=0;i<lore1.length;i++){
				lore.add(lore1[i]);
			}
		}
		return createal(material,amount,DisplayName,lore,ench,lvl);
	}
	public static ItemStack createal(Material material, int amount, String DisplayName, List<String> lore, Enchantment ench, int lvl) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if(DisplayName != null){
			meta.setDisplayName(DisplayName);
		}
		meta.setLore(lore);
		if(ench != null&&lvl>0){
			meta.addEnchant(ench, lvl, true);
		}
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createTool(Material material, String DisplayName, String[] lore1, Enchantment ench, int lvl) {
		ItemStack item = new ItemStack(material, 1);
		ItemMeta meta = item.getItemMeta();
		if(DisplayName != null){
			meta.setDisplayName(DisplayName);
		}
		ArrayList<String> lore = new ArrayList<String>();
		if(lore1 != null){
			for(int i=0;i<lore1.length;i++){
				lore.add(lore1[i]);
			}
		}
		meta.setLore(lore);
		meta.setUnbreakable(true);
		if(ench != null){
			meta.addEnchant(ench, lvl, true);
		}
		item.setItemMeta(meta);
		return item;
	}
	/*public static ItemStack create(Material material, String DisplayName){
		return create(material, 1, (byte)0, DisplayName, null, null, null, false);
	}
	public static ItemStack create(Material material, int amount, byte data){
		return create(material, amount, data, null, null, null, null, false);
	}
	public static ItemStack create(Material material, int amount, String DisplayName, String[] lore1){
		return create(material, amount, (byte) 0, DisplayName, lore1, null, null, false);
	}
	public static ItemStack create(Material material, int amount, String DisplayName, String[] lore1, Enchantment[] ench, int[] lvl, boolean unbreak){
		return create(material, amount, (byte) 0, DisplayName, lore1, ench, lvl, unbreak);
	}*/
	public static ItemStack createPotion(Material material, int amount, String DisplayName, String[] lore1, int r, int g, int b, PotionEffect[] pe) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if(DisplayName != null){
			meta.setDisplayName(DisplayName);
		}
		ArrayList<String> lore = new ArrayList<String>();
		if(lore1 != null){
			for(int i=0;i<lore1.length;i++){
				lore.add(lore1[i]);
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		PotionMeta pmeta = (PotionMeta) item.getItemMeta();
		pmeta.setColor(Color.fromRGB(r, g, b));
		pmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		if(pe!=null){
			for(PotionEffect pef:pe){
				pmeta.addCustomEffect(pef, false);
			}
		}
		item.setItemMeta(pmeta);
		return item;
	}
	public static ItemStack createArmorColored(Material mat, String DisplayName, String[] lore, int r, int g, int b) {
		ItemStack item = create(mat,1,DisplayName,lore,null,null);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(r, g, b));
		meta.setUnbreakable(true);
		item.setItemMeta(meta);
		return item;
	}
	
}
