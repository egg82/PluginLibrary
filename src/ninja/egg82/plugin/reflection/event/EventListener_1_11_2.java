package ninja.egg82.plugin.reflection.event;

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
import org.bukkit.event.server.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.event.weather.*;
import org.bukkit.event.world.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.startup.InitRegistry;

public final class EventListener_1_11_2 implements IEventListener, Listener {
	//vars
	private HashMap<String, Class<? extends EventCommand<? extends Event>>> events = new HashMap<String, Class<? extends EventCommand<? extends Event>>>();
	private HashMap<String, EventCommand<? extends Event>> initializedEvents = new HashMap<String, EventCommand<? extends Event>>();
	
	//constructor
	public EventListener_1_11_2() {
		PluginManager pluginManager = ServiceLocator.getService(InitRegistry.class).getRegister("plugin.manager", PluginManager.class);
		pluginManager.registerEvents(this, ServiceLocator.getService(InitRegistry.class).getRegister("plugin", JavaPlugin.class));
	}
	
	//public
	public synchronized void setEvent(Class<? extends Event> event, Class<? extends EventCommand<? extends Event>> clazz) {
		if (event == null) {
			throw new IllegalArgumentException("event cannot be null.");
		}
		
		String key = event.getName();
		
		if (clazz == null) {
			// Remove event
			initializedEvents.remove(key);
			events.remove(key);
		} else {
			// Add/Replace event
			initializedEvents.remove(key);
			events.put(key, clazz);
		}
	}
	public synchronized boolean hasEvent(Class<? extends Event> event) {
		return events.containsKey(event.getName());
	}
	public synchronized void clear() {
		initializedEvents.clear();
		events.clear();
	}
	
	//block events
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockCanBuild(BlockCanBuildEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockDamage(BlockDamageEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockDispense(BlockDispenseEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onBlockEvent(BlockEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onBlockExp(BlockExpEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockFadeBurn(BlockFadeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockFormBurn(BlockFormEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockGrow(BlockGrowEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockMultiPlace(BlockMultiPlaceEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onBlockPiston(BlockPistonEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityBlockForm(EntityBlockFormEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onNotePlay(NotePlayEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//enchantment events
	@EventHandler
	public void onEnchantItem(EnchantItemEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPrepareItemEnchant(PrepareItemEnchantEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//entity events
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onCreeperPower(CreeperPowerEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityBreakDoor(EntityBreakDoorEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityCombustByBlock(EntityCombustByBlockEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityCombust(EntityCombustEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityCreatePortal(EntityCreatePortalEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityDamageByBlock(EntityDamageByBlockEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onEntityEvent(EntityEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityInteract(EntityInteractEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityPortalEnter(EntityPortalEnterEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityPortal(EntityPortalEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityPortalExit(EntityPortalExitEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityTame(EntityTameEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onEntityUnleash(EntityUnleashEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onExpBottle(ExpBottleEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onHorseJump(HorseJumpEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPigZap(PigZapEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerLeashEntity(PlayerLeashEntityEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onSheepDyeWool(SheepDyeWoolEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onSheepRegrowWool(SheepRegrowWoolEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onSlimeSplit(SlimeSplitEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//hanging events
	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onHangingBreak(HangingBreakEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onHangingEvent(HangingEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onHangingPlace(HangingPlaceEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//inventory events
	@EventHandler
	public void onBrew(BrewEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onFurnaceBurn(FurnaceBurnEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onFurnaceExtract(FurnaceExtractEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onFurnaceSmelt(FurnaceSmeltEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onInventoryCreative(InventoryCreativeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onInventoryEvent(InventoryEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onInventoryPickupItem(InventoryPickupItemEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//player events
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerAnimation(PlayerAnimationEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onPlayerBucket(PlayerBucketEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerChannel(PlayerChannelEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onPlayerChat(PlayerChatEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerEditBook(PlayerEditBookEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerEggThrow(PlayerEggThrowEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onPlayerEvent(PlayerEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerFish(PlayerFishEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onPlayerInventory(PlayerInventoryEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerLevelChange(PlayerLevelChangeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onPlayerPreLogin(PlayerPreLoginEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerRegisterChannel(PlayerRegisterChannelEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerShearEntity(PlayerShearEntityEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerToggleSprint(PlayerToggleSprintEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerVelocity(PlayerVelocityEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//server events
	@EventHandler
	public void onMapInitialize(MapInitializeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPluginEnable(PluginEnableEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onPluginEvent(PluginEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onRemoteServerCommand(RemoteServerCommandEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onServerCommand(ServerCommandEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onServerEvent(ServerEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onServerListPing(ServerListPingEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onServiceEvent(ServiceEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onServiceRegister(ServiceRegisterEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onServiceUnregister(ServiceUnregisterEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//vehicle events
	@EventHandler
	public void onVehicleBlockCollision(VehicleBlockCollisionEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onVehicleCollision(VehicleCollisionEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onVehicleCreate(VehicleCreateEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onVehicleEvent(VehicleEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onVehicleExit(VehicleExitEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onVehicleMove(VehicleMoveEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onVehicleUpdate(VehicleUpdateEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//weather events
	@EventHandler
	public void onLightningStrike(LightningStrikeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onThunderChange(ThunderChangeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onWeatherEvent(WeatherEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	
	//world events
	/*@EventHandler
	public void onChunkEvent(ChunkEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onChunkPopulate(ChunkPopulateEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPortalCreate(PortalCreateEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onSpawnChange(SpawnChangeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onStructureGrow(StructureGrowEvent e) {
		onAnyEvent(e, e.getClass());
	}
	/*@EventHandler
	public void onWorldEvent(WorldEvent e) {
		onAnyEvent(e, e.getClass());
	}*/
	@EventHandler
	public void onWorldInit(WorldInitEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onWorldLoad(WorldLoadEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onWorldSave(WorldSaveEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onWorldUnload(WorldUnloadEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//private
	@SuppressWarnings("unchecked")
	private synchronized <T extends Event> void onAnyEvent(T event, Class<? extends Event> clazz) {
		String key = clazz.getName();
		
		EventCommand<T> run = (EventCommand<T>) initializedEvents.get(key);
		Class<? extends EventCommand<T>> c = (Class<? extends EventCommand<T>>) events.get(key);
		
		// run might be null, but c will never be as long as the event actually exists
		if (c == null) {
			return;
		}
		
		// Lazy initialize. No need to create an event that's never been used
		if (run == null) {
			// Create a new event and store it
			try {
				run = c.getDeclaredConstructor(clazz).newInstance(event);
			} catch (Exception ex) {
				return;
			}
			initializedEvents.put(key, run);
		} else {
			// We already have the event initialized, no need to create a new one
			run.setEvent(event);
		}
		
		run.start();
	}
}