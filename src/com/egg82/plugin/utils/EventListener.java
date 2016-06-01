package com.egg82.plugin.utils;

import java.util.HashMap;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;

import com.egg82.plugin.commands.EventCommand;
import com.egg82.plugin.utils.interfaces.IEventListener;

public class EventListener implements IEventListener, Listener {
	//vars
	private HashMap<Class<? extends Event>, Class<? extends EventCommand>> events = new HashMap<Class<? extends Event>, Class<? extends EventCommand>>();
	
	//constructor
	public EventListener() {
		
	}
	
	//public
	public void initialize() {
		
	}
	public void destroy() {
		
	}
	
	public void addEvent(Class<? extends Event> event, Class<? extends EventCommand> command) {
		if (event == null || command == null) {
			return;
		}
		
		events.put(event, command);
	}
	public void removeEvent(Class<? extends Event> event) {
		events.remove(event);
	}
	
	public void clearEvents() {
		events.clear();
	}
	public boolean hasEvent(Class<? extends Event> event) {
		return events.containsKey(event);
	}
	
	//block events
	/*@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockCanBuild(BlockCanBuildEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockDamage(BlockDamageEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockDispense(BlockDispenseEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onBlockEvent(BlockEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onBlockExp(BlockExpEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockFadeBurn(BlockFadeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockFormBurn(BlockFormEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockGrow(BlockGrowEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onBlockMultiPlace(BlockMultiPlaceEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onBlockPiston(BlockPistonEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onEntityBlockForm(EntityBlockFormEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onNotePlay(NotePlayEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		onAnyEvent(e);
	}
	
	//enchantment events
	@EventHandler
	public void onEnchantItem(EnchantItemEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPrepareItemEnchant(PrepareItemEnchantEvent e) {
		onAnyEvent(e);
	}
	
	//entity events
	/*@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onCreeperPower(CreeperPowerEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onEntityBreakDoor(EntityBreakDoorEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onEntityCombustByBlock(EntityCombustByBlockEvent e) {
		onAnyEvent(e);
	}*/
	/*@EventHandler
	public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onEntityCombust(EntityCombustEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityCreatePortal(EntityCreatePortalEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onEntityDamageByBlock(EntityDamageByBlockEvent e) {
		onAnyEvent(e);
	}*/
	/*@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onEntityEvent(EntityEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityInteract(EntityInteractEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityPortalEnter(EntityPortalEnterEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityPortal(EntityPortalEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityPortalExit(EntityPortalExitEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityTame(EntityTameEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onEntityUnleash(EntityUnleashEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onExpBottle(ExpBottleEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onHorseJump(HorseJumpEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onPigZap(PigZapEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onPlayerLeashEntity(PlayerLeashEntityEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onSheepDyeWool(SheepDyeWoolEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onSheepRegrowWool(SheepRegrowWoolEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onSlimeSplit(SlimeSplitEvent e) {
		onAnyEvent(e);
	}
	
	//hanging events
	/*@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onHangingBreak(HangingBreakEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onHangingEvent(HangingEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onHangingPlace(HangingPlaceEvent e) {
		onAnyEvent(e);
	}
	
	//inventory events
	@EventHandler
	public void onBrew(BrewEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onFurnaceBurn(FurnaceBurnEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onFurnaceExtract(FurnaceExtractEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onFurnaceSmelt(FurnaceSmeltEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onInventoryCreative(InventoryCreativeEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onInventoryEvent(InventoryEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onInventoryInteract(InventoryInteractEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onInventoryPickupItem(InventoryPickupItemEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent e) {
		onAnyEvent(e);
	}
	
	//player events
	
	
	//private
	private void onAnyEvent(Event e) {
		Class<? extends Event> c = e.getClass();
		Class<? extends EventCommand> get = events.get(c);
		EventCommand run = null;
		
		if (get == null) {
			get = events.get(c.getSuperclass());
			if (get == null) {
				get = events.get(c.getSuperclass().getSuperclass());
				if (get == null) {
					return;
				}
			}
		}
		
		try {
			run = get.getDeclaredConstructor(Event.class).newInstance(e);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return;
		}
		
		run.start();
	}
}