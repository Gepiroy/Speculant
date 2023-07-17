package cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Speculant.main;
import obj.Game;
import obj.PlayerInfo;

public class specul implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p=(Player) sender;
			if(args.length==0){
				p.sendMessage("/specul create|join|start");
				//create - создать комнату, тобi пишут номер комнаты.
				//join - присоединиться к номеру, тупо никаких ограничалок нет.
				//start - первый участник из arlist-а игры может юзать это (цэ и есть лидер).
			}else{
				Game pg=main.instance.getPlayersGame(p);
				if(args[0].equals("create")){
					if(pg!=null){
						p.sendMessage("Вы уже в игре!");
						return true;
					}
					Game game=new Game();
					game.players.put(p.getName(), new PlayerInfo());
					main.games.put(main.maxGame, game);
					main.maxGame++;
					p.sendMessage("Вы создали игру под номером "+(main.maxGame-1));
				}else if(args[0].equals("join")){
					if(main.games.containsKey(Integer.parseInt(args[1]))){
						Game g=main.games.get(Integer.parseInt(args[1]));
						g.players.put(p.getName(), new PlayerInfo());
						for(Player pl:g.getPlayers()){
							pl.sendMessage(ChatColor.YELLOW+"К вашей игре присоединился "+ChatColor.GOLD+p.getName());
						}
					}
				}else if(args[0].equals("start")){
					if(pg!=null){
						if(pg.players.keySet().toArray()[0].equals(p.getName())){
							pg.start();
						}else{
							p.sendMessage("Вы не создатель.");
							return true;
						}
					}else{
						p.sendMessage("У вас нет игры.");
						return true;
					}
				}else if(args[0].equals("set")){
					if(pg!=null){
						if(pg.players.keySet().toArray()[0].equals(p.getName())){
							if(args.length>3){
								for(int i=0;i<args.length-1;i++){
									pg.settings[i]=Integer.parseInt(args[i+1]);
								}
							}
							else pg.settings[Integer.parseInt(args[1])]=Integer.parseInt(args[2]);
						}else{
							p.sendMessage("Вы не создатель.");
							return true;
						}
					}else{
						p.sendMessage("У вас нет игры.");
						return true;
					}
				}else if(args[0].equals("restart")){
					if(pg!=null){
						if(pg.players.keySet().toArray()[0].equals(p.getName())){
							for(String st:pg.players.keySet()){
								PlayerInfo pi=pg.players.get(st);
								pi.Money=pg.settings[4];
							}
							pg.recardo();
							pg.start();
						}else{
							p.sendMessage("Вы не создатель.");
							return true;
						}
					}else{
						p.sendMessage("У вас нет игры.");
						return true;
					}
				}
			}
		}
		return true;
	}
	
}
