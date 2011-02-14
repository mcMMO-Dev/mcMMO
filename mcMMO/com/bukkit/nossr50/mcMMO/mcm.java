package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class mcm {
	/*
	 * I'm storing my functions/methods in here in an unorganized manner. Spheal with it.
	 */
	private static volatile mcm instance;
	public static mcm getInstance() {
    	if (instance == null) {
    	instance = new mcm();
    	}
    	return instance;
    	}
    public boolean inSameParty(Player playera, Player playerb){
        if(mcUsers.getProfile(playera).getParty().equals(mcUsers.getProfile(playerb).getParty())){
            return true;
        } else {
            return false;
        }
    }
    public boolean hasArrows(Player player){
    	for(ItemStack x : player.getInventory().getContents()){
    		if (x.getTypeId() == 262){
    			return true;
    		}
    	}
    	return false;
    }
    public void addArrows(Player player){
    	for(ItemStack x : player.getInventory().getContents()){
    		if (x.getTypeId() == 262){
    			x.setAmount(x.getAmount() + 1);
    			return;
    		}
    	}
    }
    public boolean isSkill(String skillname){
			if(skillname.equals("mining")){
				return true;
			}
			else if(skillname.equals("woodcutting")){
				return true;
			}
			else if(skillname.equals("repair")){
				return true;
			}
			else if(skillname.equals("herbalism")){
				return true;
			}
			else if(skillname.equals("acrobatics")){
				return true;
			}
			else if(skillname.equals("swords")){
				return true;
			}
			else if(skillname.equals("archery")){
				return true;
			}
			else if(skillname.equals("unarmed")){
				 return true;
			} else {
				return false;
			}
    }
    public boolean isInt(String string){
		try {
		    int x = Integer.parseInt(string);
		}
		catch(NumberFormatException nFE) {
		    return false;
		}
		return true;
	}
    public void simulateNaturalDrops(Entity entity){
    	Location loc = entity.getLocation();
    	if(entity instanceof Pig){
    		if(Math.random() * 3 > 2){
    			if(Math.random() * 2 > 1){
    				dropItem(loc, 319); //BACON
    			}
    			dropItem(loc, 319);
    		}
    	}
    	if(entity instanceof Spider){
    		if(Math.random() * 3 > 2){
    			if(Math.random() * 2 > 1){
    				dropItem(loc, 287); //SILK
    			}
    			dropItem(loc, 287);
    		}
    	}
    	if(entity instanceof Skeleton){
    		if(Math.random() * 3 > 2){
    			if(Math.random() * 2 > 1){
    				dropItem(loc, 262); //ARROWS
    			}
    			dropItem(loc, 262);
    		}
    		if(Math.random() * 3 > 2){
    			if(Math.random() * 2 > 1){
    				dropItem(loc, 352); //BONES
    			}
    			dropItem(loc, 352);
    		}
    	}
    	if(entity instanceof Zombie){
    		if(Math.random() * 3 > 2){
    			if(Math.random() * 2 > 1){
    				dropItem(loc, 288); //FEATHERS
    			}
    			dropItem(loc, 288);
    		}
    	}
    	if(entity instanceof Cow){
    		if(Math.random() * 3 > 2){
    			if(Math.random() * 2 > 1){
    				dropItem(loc, 334); //LEATHER
    			}
    			if(Math.random() * 2 > 1){
    				dropItem(loc, 334);
    			}
    			dropItem(loc, 334);
    		}
    	}
    	if(entity instanceof Squid){
    		if(Math.random() * 3 > 2){
    			if(Math.random() * 2 > 1){
    				dropItem(loc, 351); //INK SACS
    			}
    			if(Math.random() * 2 > 1){
    				dropItem(loc, 351);
    			}
    			dropItem(loc, 351);
    		}
    	}
    	
    	
    }
    public void dropItem(Location loc, int id){
    	if(loc != null){
    	Material mat = Material.getMaterial(id);
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		loc.getWorld().dropItemNaturally(loc, item);
    	}
    }
    public boolean checkPlayerProcRepair(Player player){
			if(mcUsers.getProfile(player).getRepairInt() >= 750){
				if(Math.random() * 10 > 2){
					player.sendMessage(ChatColor.GRAY + "That took no effort.");
					return true;
				}
			} else if (mcUsers.getProfile(player).getRepairInt() >= 450 && mcUsers.getProfile(player).getRepairInt() < 750){
				if(Math.random() * 10 > 4){
					player.sendMessage(ChatColor.GRAY + "That felt really easy.");
					return true;
				}
			} else if (mcUsers.getProfile(player).getRepairInt() >= 150 && mcUsers.getProfile(player).getRepairInt() < 450){
				if(Math.random() * 10 > 6){
					player.sendMessage(ChatColor.GRAY + "That felt pretty easy.");
					return true;
				}
			} else if (mcUsers.getProfile(player).getRepairInt() >= 50  && mcUsers.getProfile(player).getRepairInt() < 150){
				if(Math.random() * 10 > 8){
					player.sendMessage(ChatColor.GRAY + "That felt easy.");
					return true;
				}
			}
			return false;
    }
    
    //This determines how much we repair
    public short getArmorRepairAmount(ItemStack is, Player player){
    		short durability = is.getDurability();
    		switch(is.getTypeId())
    		{
    		case 306:
    		durability -= 27;
    		break;
    		case 310:
	    	durability -= 55;
	    	break;
    		case 307:
	    	durability -= 24;
	    	break;
    		case 311:
	    	durability -= 48;
	    	break;
    		case 308:
	    	durability -= 27;
	    	break;
    		case 312:
	    	durability -= 53;
	    	break;
    		case 309:
	    	durability -= 40;
	    	break;
    		case 313:
	    	durability -= 80;
	    	break;
    		}
			if(durability < 0)
			durability = 0;
			if(checkPlayerProcRepair(player))
	    	durability = 0;
			return durability;
    }
    public short getToolRepairAmount(ItemStack is, short durability, Player player){
    	//IRON SHOVEL
    	if(is.getTypeId() == 256){
    		return 0; //full repair
    	}
    	//DIAMOND SHOVEL
    	if(is.getTypeId() == 277){
    		return 0; //full repair
    	}
    	//IRON TOOLS
    	if(is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292){
    		if(durability < 84){
    			return 0;
    		}else {
    			if(checkPlayerProcRepair(player))
    				return 0; 
    			return (short) (durability-84);
    		}
    	//DIAMOND TOOLS
    	} else if(is.getTypeId() == 276 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293){
    		if(durability < 509){
    			return 0;
    		} else {
    			if(checkPlayerProcRepair(player))
    			return 0;
    			return (short) (durability-509);
    		}
    	} else { 
    		return durability;
    	}
    }
	public void blockProcSimulate(Block block){
    	Location loc = block.getLocation();
    	Material mat = Material.getMaterial(block.getTypeId());
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		if(block.getTypeId() != 73 && block.getTypeId() != 74 && block.getTypeId() != 56 && block.getTypeId() != 21 && block.getTypeId() != 1 && block.getTypeId() != 16)
		block.getWorld().dropItemNaturally(loc, item);
		if(block.getTypeId() == 73 || block.getTypeId() == 74){
			mat = Material.getMaterial(331);
			item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			block.getWorld().dropItemNaturally(loc, item);
			block.getWorld().dropItemNaturally(loc, item);
			block.getWorld().dropItemNaturally(loc, item);
			if(Math.random() * 10 > 5){
				block.getWorld().dropItemNaturally(loc, item);
			}
		}
			if(block.getTypeId() == 21){
				mat = Material.getMaterial(351);
				item = new ItemStack(mat, 1, (byte)0,(byte)0x4);
				block.getWorld().dropItemNaturally(loc, item);
				block.getWorld().dropItemNaturally(loc, item);
				block.getWorld().dropItemNaturally(loc, item);
				block.getWorld().dropItemNaturally(loc, item);
			}
			if(block.getTypeId() == 56){
				mat = Material.getMaterial(264);
				item = new ItemStack(mat, 1, (byte)0, damage);
				block.getWorld().dropItemNaturally(loc, item);
			}
			if(block.getTypeId() == 1){
				mat = Material.getMaterial(4);
				item = new ItemStack(mat, 1, (byte)0, damage);
				block.getWorld().dropItemNaturally(loc, item);
			}
			if(block.getTypeId() == 16){
				mat = Material.getMaterial(263);
				item = new ItemStack(mat, 1, (byte)0, damage);
				block.getWorld().dropItemNaturally(loc, item);
			}
    }
    public void blockProcCheck(Block block, Player player){
    	if(mcUsers.getProfile(player).getMiningInt() > 3000){
    		blockProcSimulate(block);
			return;
    	}
    	if(mcUsers.getProfile(player).getMiningInt() > 2000){
    		if((Math.random() * 10) > 2){
    		blockProcSimulate(block);
    		return;
    		}
    	}
    	if(mcUsers.getProfile(player).getMiningInt() > 750){
    		if((Math.random() * 10) > 4){
    		blockProcSimulate(block);
			return;
    		}
    	}
    	if(mcUsers.getProfile(player).getMiningInt() > 150){
    		if((Math.random() * 10) > 6){
    		blockProcSimulate(block);
			return;
    		}
    	}
    	if(mcUsers.getProfile(player).getMiningInt() > 25){
    		if((Math.random() * 10) > 8){
    		blockProcSimulate(block);
			return;
    		}
    	}
    			
	}
    public void miningBlockCheck(Player player, Block block){
    	if(block.getTypeId() == 1){
    		mcUsers.getProfile(player).addgather(1);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//COAL
    		if(block.getTypeId() == 16){
    		mcUsers.getProfile(player).addgather(3);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//GOLD
    		if(block.getTypeId() == 14){
    		mcUsers.getProfile(player).addgather(20);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//DIAMOND
    		if(block.getTypeId() == 56){
    		mcUsers.getProfile(player).addgather(50);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//IRON
    		if(block.getTypeId() == 15){
    		mcUsers.getProfile(player).addgather(10);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//REDSTONE
    		if(block.getTypeId() == 73 || block.getTypeId() == 74){
    		mcUsers.getProfile(player).addgather(15);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//LAPUS
    		if(block.getTypeId() == 21){
    		mcUsers.getProfile(player).addgather(50);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    }
    public void breadCheck(Player player, ItemStack is){
    	if(is.getTypeId() == 297){
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 50 && mcUsers.getProfile(player).getHerbalismInt() < 150){
    			player.setHealth(player.getHealth() + 1);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 150 && mcUsers.getProfile(player).getHerbalismInt() < 250){
    			player.setHealth(player.getHealth() + 2);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 250 && mcUsers.getProfile(player).getHerbalismInt() < 350){
    			player.setHealth(player.getHealth() + 3);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 350 && mcUsers.getProfile(player).getHerbalismInt() < 450){
    			player.setHealth(player.getHealth() + 4);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 450 && mcUsers.getProfile(player).getHerbalismInt() < 550){
    			player.setHealth(player.getHealth() + 5);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 550 && mcUsers.getProfile(player).getHerbalismInt() < 650){
    			player.setHealth(player.getHealth() + 6);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 650 && mcUsers.getProfile(player).getHerbalismInt() < 750){
    			player.setHealth(player.getHealth() + 7);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 750){
    			player.setHealth(player.getHealth() + 8);
    		}
    	}
    }
    public void stewCheck(Player player, ItemStack is){
    	if(is.getTypeId() == 282){
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 50 && mcUsers.getProfile(player).getHerbalismInt() < 150){
    			player.setHealth(player.getHealth() + 1);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 150 && mcUsers.getProfile(player).getHerbalismInt() < 250){
    			player.setHealth(player.getHealth() + 2);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 250 && mcUsers.getProfile(player).getHerbalismInt() < 350){
    			player.setHealth(player.getHealth() + 3);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 350 && mcUsers.getProfile(player).getHerbalismInt() < 450){
    			player.setHealth(player.getHealth() + 4);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 450 && mcUsers.getProfile(player).getHerbalismInt() < 550){
    			player.setHealth(player.getHealth() + 5);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 550 && mcUsers.getProfile(player).getHerbalismInt() < 650){
    			player.setHealth(player.getHealth() + 6);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 650 && mcUsers.getProfile(player).getHerbalismInt() < 750){
    			player.setHealth(player.getHealth() + 7);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 750){
    			player.setHealth(player.getHealth() + 8);
    		}
    	}
    }
    public void needMoreVespeneGas(ItemStack is, Player player){
    	if ((mcm.getInstance().isDiamondTools(is) || mcm.getInstance().isDiamondArmor(is) ) && mcUsers.getProfile(player).getRepairInt() < 50){
			player.sendMessage(ChatColor.DARK_RED +"You're not adept enough to repair Diamond");
		} else if (mcm.getInstance().isDiamondTools(is) && !mcm.getInstance().hasDiamond(player) || mcm.getInstance().isIronTools(is) && !mcm.getInstance().hasIron(player)){
			if(mcm.getInstance().isDiamondTools(is) && !mcm.getInstance().hasDiamond(player))
			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.BLUE+ "Diamonds");
			if(mcm.getInstance().isIronTools(is) && !mcm.getInstance().hasIron(player))
			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GRAY+ "Iron");
		} else if (mcm.getInstance().isDiamondArmor(is) && !mcm.getInstance().hasDiamond(player)){
			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.BLUE+ "Diamonds");
		} else if (mcm.getInstance().isIronArmor(is) && !mcm.getInstance().hasIron(player))
			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GRAY+ "Iron");
		}
    public boolean isSwords(ItemStack is){
    	if(is.getTypeId() == 268 || is.getTypeId() == 267 || is.getTypeId() == 271 || is.getTypeId() == 283 || is.getTypeId() == 276){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean simulateUnarmedProc(Player player){
    	if(mcUsers.getProfile(player).getUnarmedInt() >= 750){
    		if(Math.random() * 10 > 4){
    			return true;
    		}
    	}if(mcUsers.getProfile(player).getUnarmedInt() >= 350 && mcUsers.getProfile(player).getUnarmedInt() < 750){
    		if(Math.random() * 10 > 4){
    			return true;
    		}
    	}
    		return false;
    }
    public void playerVersusPlayerChecks(Entity x, Player attacker, EntityDamageByEntityEvent event, Plugin plugin){
    	if(x instanceof Player){
    		Player defender = (Player)x;
    		if(mcUsers.getProfile(attacker).inParty() && mcUsers.getProfile(defender).inParty()){
				if(inSameParty(defender, attacker)){
					event.setCancelled(true);
					return;
				}
    		}
			if(attacker.getItemInHand().getTypeId() == 0){
				//DMG MODIFIER
				if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
					defender.setHealth(calculateDamage(defender, 1));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
					defender.setHealth(calculateDamage(defender, 2));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
					defender.setHealth(calculateDamage(defender, 3));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
					defender.setHealth(calculateDamage(defender, 4));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
					defender.setHealth(calculateDamage(defender, 5));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
					defender.setHealth(calculateDamage(defender, 6));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
					defender.setHealth(calculateDamage(defender, 7));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
					defender.setHealth(calculateDamage(defender, 8));
				}
				if(mcUsers.getProfile(defender).isDead())
    				return;
				//XP
				if(attacker.getItemInHand().getTypeId() == 0 && Math.random() * 10 > 9){
					if(defender.getHealth() != 0){
						mcUsers.getProfile(attacker).skillUpUnarmed(1);
						attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
						}
				}
				//PROC
				if(simulateUnarmedProc(attacker)){
					Location loc = defender.getLocation();
					if(defender.getItemInHand() != null && defender.getItemInHand().getTypeId() != 0){
					attacker.sendMessage(ChatColor.DARK_RED+"You have hit with great force.");
					defender.sendMessage(ChatColor.DARK_RED+"You have been disarmed!");
					ItemStack item = defender.getItemInHand();
					if(item != null){
					loc.getWorld().dropItemNaturally(loc, item);
					Material mat;
					mat = Material.getMaterial(0);
					ItemStack itemx = null;
					defender.setItemInHand(itemx);
					}
					}
				}
				/*
				 * Make the defender drop items on death
				 */
				if(defender.getHealth()<= 0 && !mcUsers.getProfile(defender).isDead()){
					mcUsers.getProfile(defender).setDead(true);
					event.setCancelled(true); //SEE IF THIS HELPS
					for(ItemStack herp : defender.getInventory().getContents()){
						if(herp != null && herp.getTypeId() != 0)
						defender.getLocation().getWorld().dropItemNaturally(defender.getLocation(), herp);
					}
    				for(Player derp : plugin.getServer().getOnlinePlayers()){
    					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"slain "+ChatColor.GRAY+defender.getName());
    					mcUsers.getProfile(defender).setDead(true);
    				}
    			}
				return;
			}
			if(mcUsers.getProfile(defender).isDead())
				return;
			if((defender.getHealth() - event.getDamage()) <= 0 && defender.getHealth() != 0){
				for(Player derp : plugin.getServer().getOnlinePlayers()){
					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"slain "+ChatColor.GRAY+defender.getName());
					mcUsers.getProfile(defender).setDead(true);
				}
			}
			//Moving this below the death message for now, seems to have issues when the defender is not in a party
			if((mcUsers.getProfile(defender).inParty() && mcUsers.getProfile(attacker).inParty())&& mcUsers.getProfile(defender).getParty().equals(mcUsers.getProfile(attacker).getParty()))
				event.setCancelled(true);
		}
    }
    public void playerVersusSquidChecks(EntityDamageByEntityEvent event, Player attacker, Entity x, int type){
    	if(x instanceof Squid){
			Squid defender = (Squid)event.getEntity();
			if(isSwords(attacker.getItemInHand()) && defender.getHealth() > 0){
				if(Math.random() * 10 > 9){
					mcUsers.getProfile(attacker).skillUpSwords(1);
					attacker.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(attacker).getSwords()+")");
				}
			}
			/*
			 * UNARMED VS SQUID
			 */
			if(type == 0){
    			if(defender.getHealth() <= 0)
    				return;
    			if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
					defender.setHealth(calculateDamage(defender, 1));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
					defender.setHealth(calculateDamage(defender, 2));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
					defender.setHealth(calculateDamage(defender, 3));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
					defender.setHealth(calculateDamage(defender, 4));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
					defender.setHealth(calculateDamage(defender, 5));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
					defender.setHealth(calculateDamage(defender, 6));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
					defender.setHealth(calculateDamage(defender, 7));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
					defender.setHealth(calculateDamage(defender, 8));
				}
    			//XP
				if(attacker.getItemInHand().getTypeId() == 0 && Math.random() * 10 > 8){
					if(defender.getHealth() != 0){
					mcUsers.getProfile(attacker).skillUpUnarmed(1);
					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
					}
				}
				if(defender.getHealth() <= 0)
				mcm.getInstance().simulateNaturalDrops(defender);
    			}
		}
    }
    public void playerVersusAnimalsChecks(Entity x, Player attacker, EntityDamageByEntityEvent event, int type){
    	if(x instanceof Animals){
			if(type == 0){
			Animals defender = (Animals)event.getEntity();
			if(defender.getHealth() <= 0)
				return;
			if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
				defender.setHealth(calculateDamage(defender, 1));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
				defender.setHealth(calculateDamage(defender, 2));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
				defender.setHealth(calculateDamage(defender, 3));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
				defender.setHealth(calculateDamage(defender, 4));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
				defender.setHealth(calculateDamage(defender, 5));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
				defender.setHealth(calculateDamage(defender, 6));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
				defender.setHealth(calculateDamage(defender, 7));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
				defender.setHealth(calculateDamage(defender, 8));
			}
			if(defender.getHealth() <= 0){
				mcm.getInstance().simulateNaturalDrops(defender);
			}
			}
		}
    }
    public void playerDeathByMonsterMessageCheck(Entity y, Player defender, Plugin plugin){
    	if(y instanceof Monster){
			if(mcUsers.getProfile(defender).isDead())
				return;
			if(defender.getHealth() <= 0){
				mcm.getInstance().simulateNaturalDrops(defender);
				if(y instanceof Creeper){
					mcUsers.getProfile(defender).setDead(true);
					for(Player derp : plugin.getServer().getOnlinePlayers()){
    					derp.sendMessage(ChatColor.GRAY + "A "+ChatColor.DARK_GREEN+"Creeper"+ChatColor.GRAY+" has killed "+ChatColor.DARK_RED+defender.getName());
    				}
				}
				if(y instanceof Skeleton){
					mcUsers.getProfile(defender).setDead(true);
					for(Player derp : plugin.getServer().getOnlinePlayers()){
    					derp.sendMessage(ChatColor.GRAY + "A "+ChatColor.WHITE+"Skeleton"+ChatColor.GRAY+" has killed "+ChatColor.DARK_RED+defender.getName());
    				}
				}
				if(y instanceof Spider){
					mcUsers.getProfile(defender).setDead(true);
					for(Player derp : plugin.getServer().getOnlinePlayers()){
    					derp.sendMessage(ChatColor.GRAY + "A "+ChatColor.DARK_PURPLE+"Spider"+ChatColor.GRAY+" has killed "+ChatColor.DARK_RED+defender.getName());
    				}
				}
				if(y instanceof Zombie){
					mcUsers.getProfile(defender).setDead(true);
					for(Player derp : plugin.getServer().getOnlinePlayers()){
    					derp.sendMessage(ChatColor.GRAY + "A "+ChatColor.DARK_AQUA+"Zombie"+ChatColor.GRAY+" has killed "+ChatColor.DARK_RED+defender.getName());
    				}
				}
			}
		}
    }
    public void playerVersusMonsterChecks(EntityDamageByEntityEvent event, Player attacker, Entity x, int type){
    	if(x instanceof Monster){
			Monster defender = (Monster)event.getEntity();
			if(isSwords(attacker.getItemInHand()) && defender.getHealth() > 0){
				if(Math.random() * 10 > 9){
					mcUsers.getProfile(attacker).skillUpSwords(1);
					attacker.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(attacker).getSwords()+")");
				}
			}
			if(type == 0){
			if(defender.getHealth() <= 0)
				return;
			if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
				defender.setHealth(calculateDamage(defender, 1));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
				defender.setHealth(calculateDamage(defender, 2));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
				defender.setHealth(calculateDamage(defender, 3));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
				defender.setHealth(calculateDamage(defender, 4));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
				defender.setHealth(calculateDamage(defender, 5));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
				defender.setHealth(calculateDamage(defender, 6));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
				defender.setHealth(calculateDamage(defender, 7));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
				defender.setHealth(calculateDamage(defender, 8));
			}
			//XP
			if(x instanceof Skeleton && Math.random() * 100 > 95){
				if(defender.getHealth() != 0){
					mcUsers.getProfile(attacker).skillUpUnarmed(1);
					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
			}
			}
			if(x instanceof Spider&& Math.random() * 10 > 9){
				if(defender.getHealth() != 0){
					mcUsers.getProfile(attacker).skillUpUnarmed(1);
					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
			}
			}
			if(x instanceof Zombie && Math.random() * 100 > 95){
				if(defender.getHealth() != 0){
					mcUsers.getProfile(attacker).skillUpUnarmed(1);
					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
			}
			}
			if(x instanceof Creeper && Math.random() * 100 > 90){
    				if(defender.getHealth() != 0){
    					mcUsers.getProfile(attacker).skillUpUnarmed(2);
    					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 2. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
    		}
			}
			if(defender.getHealth() <= 0)
				mcm.getInstance().simulateNaturalDrops(defender);
			}
		}
    }
    public int calculateDamage(Player player, int dmg){
    	int health = player.getHealth();
    	if(health - dmg <0){
    		return 0;
    	} else {
    		health-= dmg;
    		return health;
    	}
    }
    public int calculateDamage(Squid squid, int dmg){
    	int health = squid.getHealth();
    	if(health - dmg <0){
    		return 0;
    	} else {
    		health-= dmg;
    		return health;
    	}
    }
    public int calculateDamage(Monster monster, int dmg){
    	int health = monster.getHealth();
    	if(health - dmg <0){
    		return 0;
    	} else {
    		health-= dmg;
    		return health;
    	}
    }
    public int calculateDamage(Animals animal, int dmg){
    	int health = animal.getHealth();
    	if(health - dmg <0){
    		return 0;
    	} else {
    		health-= dmg;
    		return health;
    	}
    }
    public void parryCheck(Player defender, EntityDamageByEntityEvent event, Entity y){
    	if(isSwords(defender.getItemInHand())){
			if(mcUsers.getProfile(defender).getSwordsInt() >= 50 && mcUsers.getProfile(defender).getSwordsInt() < 250){
				if(Math.random() * 10 > 8){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					mcUsers.getProfile(defender).skillUpSwords(1);
					defender.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(defender).getSwords()+")");
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
					return;
				}
			}
			if(mcUsers.getProfile(defender).getSwordsInt() >= 250 && mcUsers.getProfile(defender).getSwordsInt() < 450){
				if(Math.random() * 10 > 6){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					mcUsers.getProfile(defender).skillUpSwords(1);
					defender.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(defender).getSwords()+")");
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
					return;
				}
			}
			if(mcUsers.getProfile(defender).getSwordsInt() >= 450 && mcUsers.getProfile(defender).getSwordsInt() < 775){
				if(Math.random() * 10 > 5){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					mcUsers.getProfile(defender).skillUpSwords(1);
					defender.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(defender).getSwords()+")");
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
					return;
				}
			}
			if(mcUsers.getProfile(defender).getSwordsInt() >= 775){
				if(Math.random() * 10 > 4){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
					return;
				}
			}
		}
    }
    public void mcmmoHelpCheck(String[] split, Player player, PlayerChatEvent event){
    	if(split[0].equalsIgnoreCase("/woodcutting")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~WOODCUTTING INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Chop down trees.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Double Drops start to happen at 10 woodcutting skill");
			player.sendMessage(ChatColor.GRAY+"and it gets more frequent from there.");
    	}
    	if(split[0].equalsIgnoreCase("/archery")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~ARCHERY INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Shooting monsters.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Damage scales with Archery skill");
			player.sendMessage(ChatColor.GRAY+"Chance to daze player opponents with high skill lvl");
    	}
    	if(split[0].equalsIgnoreCase("/swords")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~SWORDS INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Slicing up monsters");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Parrying. It negates damage.");
			player.sendMessage(ChatColor.GRAY+"Chance to parry scales with skill.");
    	}
    	if(split[0].equalsIgnoreCase("/acrobatics")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~ACROBATICS INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Spraining ankles.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Rolling. Negates fall damage.");
			player.sendMessage(ChatColor.GRAY+"Chance to roll scales with skill.");
    	}
    	if(split[0].equalsIgnoreCase("/mining")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~MINING INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Mining ore and stone,");
			player.sendMessage(ChatColor.DARK_GRAY+"the xp rate depends entirely upon the rarity of what you're harvesting.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Double Drops start to happen at 25 Mining skill,");
			player.sendMessage(ChatColor.GRAY+"and the chance for it increases with skill.");
    	}
    	if(split[0].equalsIgnoreCase("/repair")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~REPAIR INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Repairing tools and armor.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"High skill levels make a proc to fully repair items happen more often.");
			player.sendMessage(ChatColor.GREEN+"~~USE~~");
			player.sendMessage(ChatColor.GRAY+"Approach an Anvil (Iron Block) with the item you wish ");
			player.sendMessage(ChatColor.GRAY+"to repair in hand, right click to consume resources of the");
			player.sendMessage(ChatColor.GRAY+"same type to repair it. This does not work for stone/wood/gold");
    	}
    	if(split[0].equalsIgnoreCase("/unarmed")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~UNARMED INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Punching monsters and players.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Damage scales with unarmed skill. The first damage increase");
			player.sendMessage(ChatColor.DARK_GRAY+"happens at 50 skill. At very high skill levels, you will");
			player.sendMessage(ChatColor.DARK_GRAY+"gain a proc to disarm player opponents on hit");
    	}
    	if(split[0].equalsIgnoreCase("/herbalism")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~HERBALISM INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Farming and picking herbs.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Increases healing effects of bread and stew.");
			player.sendMessage(ChatColor.GRAY+"Allows for chance to receive double drops based on skill");
    	}
    	if(split[0].equalsIgnoreCase("/excavation")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~EXCAVATION INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Digging.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"You will find treasures while digging based on your excavation,");
			player.sendMessage(ChatColor.GRAY+"and at high levels the rewards are quite nice. The items you get");
			player.sendMessage(ChatColor.GRAY+"depend on the block you're digging.");
			player.sendMessage(ChatColor.GRAY+"Different blocks give diffrent stuff.");
    	}
		if(split[0].equalsIgnoreCase("/mcmmo")){
			event.setCancelled(true);
    		player.sendMessage(ChatColor.GRAY+"mcMMO is an RPG inspired plugin");
    		player.sendMessage(ChatColor.GRAY+"You can gain skills in several professions by");
    		player.sendMessage(ChatColor.GRAY+"doing things related to that profession.");
    		player.sendMessage(ChatColor.GRAY+"Mining for example will increase your mining XP.");
    		player.sendMessage(ChatColor.GRAY+"Wood Cutting will increase Wood Cutting, etc...");
    		player.sendMessage(ChatColor.GRAY+"Repairing is simple in mcMMO");
    		player.sendMessage(ChatColor.GRAY+"Say you want to repair an iron shovel");
    		player.sendMessage(ChatColor.GRAY+"start by making an anvil by combining 9 iron ingots");
    		player.sendMessage(ChatColor.GRAY+"on a workbench. Place the anvil and while holding the shovel");
    		player.sendMessage(ChatColor.GRAY+"right click the anvil to interact with it, If you have spare");
    		player.sendMessage(ChatColor.GRAY+"iron ingots in your inventory the item will be repaired.");
    		player.sendMessage(ChatColor.GRAY+"You cannot hurt other party members");
    		player.sendMessage(ChatColor.BLUE+"Set your own spawn with "+ChatColor.RED+"/myspawn");
    		player.sendMessage(ChatColor.GREEN+"Based on your skills you will get "+ChatColor.DARK_RED+"random procs "+ChatColor.GREEN+ "when");
    		player.sendMessage(ChatColor.GREEN+"using your profession, like "+ChatColor.DARK_RED+"double drops "+ChatColor.GREEN+"or "+ChatColor.DARK_RED+"better repairs");
    		player.sendMessage(ChatColor.GREEN+"Find out mcMMO commands with /mcc");
    	}
    	if(split[0].equalsIgnoreCase("/mcc")){
    		event.setCancelled(true);
    		player.sendMessage(ChatColor.GRAY+"mcMMO has a party system included");
    		player.sendMessage(ChatColor.GREEN+"~~Commands~~");
    		if(mcPermissions.getInstance().party(player)){
    		player.sendMessage(ChatColor.GRAY+"/party <name> - to join a party");
    		player.sendMessage(ChatColor.GRAY+"/party q - to quit a party");
    		}
    		if(mcPermissions.getInstance().partyTeleport(player))
    		player.sendMessage(ChatColor.GRAY+"/ptp <name> - party teleport");
    		if(mcPermissions.getInstance().partyChat(player))
    		player.sendMessage(ChatColor.GRAY+"/p - toggles party chat");
    		player.sendMessage(ChatColor.GREEN+"/stats"+ChatColor.GRAY+" - Check current skill levels");
    		if(mcPermissions.getInstance().setMySpawn(player))
    		player.sendMessage(ChatColor.GRAY+"/setmyspawn - Skill info");
    		if(mcPermissions.getInstance().mySpawn(player))
    		player.sendMessage(ChatColor.GRAY+"/myspawn - travel to myspawn, clears inventory");
    		if(mcPermissions.getInstance().whois(player) || player.isOp())
    		player.sendMessage(ChatColor.GRAY+"/whois - view detailed info about a player (req op)");
    		player.sendMessage(ChatColor.GRAY+"/woodcutting - Skill info");
    		player.sendMessage(ChatColor.GRAY+"/mining - Skill info");
    		player.sendMessage(ChatColor.GRAY+"/repair - Skill info");
    		player.sendMessage(ChatColor.GRAY+"/unarmed - Skill info");
    		player.sendMessage(ChatColor.GRAY+"/herbalism - Skill info");
    		player.sendMessage(ChatColor.GRAY+"/excavation - Skill info");
    		player.sendMessage(ChatColor.GRAY+"/archery - Skill info");
    		player.sendMessage(ChatColor.GRAY+"/swords - Skill info");
    		player.sendMessage(ChatColor.GRAY+"/acrobatics - Skill info");
    		if(mcPermissions.getInstance().mmoedit(player))
    		player.sendMessage(ChatColor.GRAY+"/mmoedit - Modify mcMMO skills of players/yourself");
    	}
    }
    public void repairCheck(Player player, ItemStack is, Block block){
    	if(block != null && block.getTypeId() == 42){
        	short durability = is.getDurability();
        	if(player.getItemInHand().getDurability() > 0){
        		/*
        		 * ARMOR
        		 */
        		if(mcm.getInstance().isArmor(is) && block.getTypeId() == 42){
        			if(mcm.getInstance().isDiamondArmor(is) && mcm.getInstance().hasDiamond(player)){
        			mcm.getInstance().removeDiamond(player);
        			player.getItemInHand().setDurability(mcm.getInstance().getArmorRepairAmount(is, player));
        			mcUsers.getProfile(player).skillUpRepair(1);
        			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
        			} else if (mcm.getInstance().isIronArmor(is) && mcm.getInstance().hasIron(player)){
        			mcm.getInstance().removeIron(player);
            		player.getItemInHand().setDurability(mcm.getInstance().getArmorRepairAmount(is, player));
            		mcUsers.getProfile(player).skillUpRepair(1);
            		player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");	
        			} else {
        				needMoreVespeneGas(is, player);
        			}
        		}
        		/*
        		 * TOOLS
        		 */
        		if(mcm.getInstance().isTools(is) && block.getTypeId() == 42){
            		if(mcm.getInstance().isIronTools(is) && mcm.getInstance().hasIron(player)){
            			is.setDurability(mcm.getInstance().getToolRepairAmount(is, durability, player));
            			mcm.getInstance().removeIron(player);
            			mcUsers.getProfile(player).skillUpRepair(1);
            			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
            		} else if (mcm.getInstance().isDiamondTools(is) && mcm.getInstance().hasDiamond(player) && mcUsers.getProfile(player).getRepairInt() >= 50){ //Check if its diamond and the player has diamonds
            			is.setDurability(mcm.getInstance().getToolRepairAmount(is, durability, player));
            			mcm.getInstance().removeDiamond(player);
            			mcUsers.getProfile(player).skillUpRepair(1);
            			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
            		} else {
            			needMoreVespeneGas(is, player);
            		}
        		}
        		
        	} else {
        		player.sendMessage("That is at full durability.");
        	}
        	} //end if block is iron block bracket
    }
    public void herbalismProcCheck(Block block, Player player){
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	if(!mcConfig.getInstance().isBlockWatched(block)){
    	if(type == 39 || type == 40){
    			mcUsers.getProfile(player).skillUpHerbalism(3);
    			player.sendMessage(ChatColor.YELLOW+"Herbalism skill increased by 3. Total ("+mcUsers.getProfile(player).getHerbalismInt()+")");
    		}
    	if(type == 37 || type == 38){
    		if(Math.random() * 10 > 8){
    			mcUsers.getProfile(player).skillUpHerbalism(1);
    			player.sendMessage(ChatColor.YELLOW+"Herbalism skill increased by 1. Total ("+mcUsers.getProfile(player).getHerbalismInt()+")");
    		}
    	}
    	if(type == 59 && block.getData() == (byte) 0x7){
    		mat = Material.getMaterial(296);
			is = new ItemStack(mat, 1, (byte)0, (byte)0);
			if(Math.random() * 100 > 80){
    		mcUsers.getProfile(player).skillUpHerbalism(1);
    		player.sendMessage(ChatColor.YELLOW+"Herbalism skill increased by 1. Total ("+mcUsers.getProfile(player).getHerbalismInt()+")");
			}
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 50 && mcUsers.getProfile(player).getHerbalismInt() < 150){
    		if(Math.random() * 10 > 8)
			loc.getWorld().dropItemNaturally(loc, is);
    		}
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 150 && mcUsers.getProfile(player).getHerbalismInt() < 350 ){
    			if(Math.random() * 10 > 6)
    				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 350 && mcUsers.getProfile(player).getHerbalismInt() < 500 ){
    			if(Math.random() * 10 > 4)
    				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 500 && mcUsers.getProfile(player).getHerbalismInt() < 750 ){
    			if(Math.random() * 10 > 2)
    				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	}
    }
    public void excavationProcCheck(Block block, Player player){
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	if(type == 2 && mcUsers.getProfile(player).getExcavationInt() > 250){
    		//CHANCE TO GET APPLES
    		if(Math.random() * 100 > 99){
    			mat = Material.getMaterial(260);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//DIRT SAND OR GRAVEL
    	if(type == 3 || type == 13 || type == 2 || type == 12){
    		if(Math.random() * 100 > 95){
    			mcUsers.getProfile(player).skillUpExcavation(1);
    			player.sendMessage(ChatColor.YELLOW+"Excavation skill increased by 1. Total ("+mcUsers.getProfile(player).getExcavationInt()+")");
    			
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 750){
    			//CHANCE TO GET CAKE
    			if(Math.random() * 2000 > 1999){
    				mat = Material.getMaterial(354);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 150){
    			//CHANCE TO GET MUSIC
    			if(Math.random() * 1000 > 999){
    				mat = Material.getMaterial(2256);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    			
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 350){
    			//CHANCE TO GET DIAMOND
    			if(Math.random() * 500 > 499){
        				mat = Material.getMaterial(264);
        				is = new ItemStack(mat, 1, (byte)0, (byte)0);
        				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 250){
    			//CHANCE TO GET MUSIC
    			if(Math.random() * 1000 > 999){
    				mat = Material.getMaterial(2257);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    	}
    	//SAND
    	if(type == 12){
    		//CHANCE TO GET GLOWSTONE
    		if(mcUsers.getProfile(player).getExcavationInt() > 50 && Math.random() * 100 > 95){
				mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET DIAMOND
    		if(mcUsers.getProfile(player).getExcavationInt() > 500 && Math.random() * 500 > 499){
				mat = Material.getMaterial(264);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//GRASS OR DIRT
    	if((type == 2 || type == 3) && mcUsers.getProfile(player).getExcavationInt() > 25){
    		//CHANCE TO GET GLOWSTONE
    		if(Math.random() * 10 > 7){
    			mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//GRAVEL
    	if(type == 13){
    		//CHANCE TO GET SULPHUR
    		if(mcUsers.getProfile(player).getExcavationInt() > 75){
    		if(Math.random() * 10 > 7){
    			mat = Material.getMaterial(289);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 175){
        		if(Math.random() * 10 > 6){
        			mat = Material.getMaterial(352);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
        		}
        		}
    		//CHANCE TO GET COAL
    		if(mcUsers.getProfile(player).getExcavationInt() > 125){
    			if(Math.random() * 100 > 99){
    				mat = Material.getMaterial(263);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    	}
    }
    public void woodCuttingProcCheck(Player player, Block block, Location loc){
    	byte type = block.getData();
    	Material mat = Material.getMaterial(block.getTypeId());
    	byte damage = 0;
    	if(mcUsers.getProfile(player).getWoodCuttingint() > 1000){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 750){
		if((Math.random() * 10) > 2){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 300){
		if((Math.random() * 10) > 4){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 100){
		if((Math.random() * 10) > 6){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 10){
		if((Math.random() * 10) > 8){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
    }
    public void simulateSkillUp(Player player){
    	if(mcUsers.getProfile(player).getwgatheramt() > 10){
			while(mcUsers.getProfile(player).getwgatheramt() > 10){
			mcUsers.getProfile(player).removewgather(10);
			mcUsers.getProfile(player).skillUpWoodcutting(1);
			player.sendMessage(ChatColor.YELLOW+"Wood Cutting skill increased by 1. Total ("+mcUsers.getProfile(player).getWoodCutting()+")");
			}
		}
		if(mcUsers.getProfile(player).getgatheramt() > 50){
			while(mcUsers.getProfile(player).getgatheramt() > 50){
			mcUsers.getProfile(player).removegather(50);
			mcUsers.getProfile(player).skillUpMining(1);
			player.sendMessage(ChatColor.YELLOW+"Mining skill increased by 1. Total ("+mcUsers.getProfile(player).getMining()+")");
			}
		}
    }
    // IS TOOLS FUNCTION
    public boolean isArmor(ItemStack is){
    	if(is.getTypeId() == 306 || is.getTypeId() == 307 ||is.getTypeId() == 308 ||is.getTypeId() == 309 ||
    			is.getTypeId() == 310 ||is.getTypeId() == 311 ||is.getTypeId() == 312 ||is.getTypeId() == 313){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isIronArmor(ItemStack is){
    	if(is.getTypeId() == 306 || is.getTypeId() == 307 || is.getTypeId() == 308 || is.getTypeId() == 309)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isDiamondArmor(ItemStack is){
    	if(is.getTypeId() == 310 || is.getTypeId() == 311 || is.getTypeId() == 312 || is.getTypeId() == 313)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isTools(ItemStack is){
    	if(is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292 ||//IRON
    			is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293) //DIAMOND 
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean isIronTools(ItemStack is){
    	if(is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean isDiamondTools(ItemStack is){
    	if(is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public void removeIron(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 265){
    			if(x.getAmount() == 1){
    				x.setTypeId(0);
    				x.setAmount(0);
    				player.getInventory().setContents(inventory);
    			} else{
    			x.setAmount(x.getAmount() - 1);
    			player.getInventory().setContents(inventory);
    			}
    			return;
    		}
    	}
    }
    public void removeDiamond(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 264){
    			if(x.getAmount() == 1){
    				x.setTypeId(0);
    				x.setAmount(0);
    				player.getInventory().setContents(inventory);
    			} else{
    			x.setAmount(x.getAmount() - 1);
    			player.getInventory().setContents(inventory);
    			}
    			return;
    		}
    	}
    }
    public boolean hasDiamond(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 264){
    			return true;
    		}
    	}
    	return false;
    }
    public boolean hasIron(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 265){
    			return true;
    		}
    	}
    	return false;
    }
}
