package ninja.egg82.plugin.utils;

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

import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.plugin.utils.interfaces.IEventListener;

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
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerAnimation(PlayerAnimationEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onPlayerBucket(PlayerBucketEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerChannel(PlayerChannelEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onPlayerChat(PlayerChatEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerEditBook(PlayerEditBookEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerEggThrow(PlayerEggThrowEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onPlayerEvent(PlayerEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerFish(PlayerFishEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onPlayerInventory(PlayerInventoryEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerLevelChange(PlayerLevelChangeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onPlayerPreLogin(PlayerPreLoginEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onPlayerRegisterChannel(PlayerRegisterChannelEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerShearEntity(PlayerShearEntityEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPlayerToggleSprint(PlayerToggleSprintEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {
		onAnyEvent(e);
	}*/
	/*@EventHandler
	public void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onPlayerVelocity(PlayerVelocityEvent e) {
		onAnyEvent(e);
	}
	
	//server events
	@EventHandler
	public void onMapInitialize(MapInitializeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPluginEnable(PluginEnableEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onPluginEvent(PluginEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onRemoteServerCommand(RemoteServerCommandEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onServerCommand(ServerCommandEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onServerEvent(ServerEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onServerListPing(ServerListPingEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onServiceEvent(ServiceEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onServiceRegister(ServiceRegisterEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onServiceUnregister(ServiceUnregisterEvent e) {
		onAnyEvent(e);
	}
	
	//vehicle events
	@EventHandler
	public void onVehicleBlockCollision(VehicleBlockCollisionEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onVehicleCollision(VehicleCollisionEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onVehicleCreate(VehicleCreateEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onVehicleEvent(VehicleEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onVehicleExit(VehicleExitEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onVehicleMove(VehicleMoveEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onVehicleUpdate(VehicleUpdateEvent e) {
		onAnyEvent(e);
	}
	
	//weather events
	@EventHandler
	public void onLightningStrike(LightningStrikeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onThunderChange(ThunderChangeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onWeatherEvent(WeatherEvent e) {
		onAnyEvent(e);
	}*/
	
	//world events
	/*@EventHandler
	public void onChunkEvent(ChunkEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onChunkPopulate(ChunkPopulateEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onPortalCreate(PortalCreateEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onSpawnChange(SpawnChangeEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onStructureGrow(StructureGrowEvent e) {
		onAnyEvent(e);
	}
	/*@EventHandler
	public void onWorldEvent(WorldEvent e) {
		onAnyEvent(e);
	}*/
	@EventHandler
	public void onWorldInit(WorldInitEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onWorldLoad(WorldLoadEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onWorldSave(WorldSaveEvent e) {
		onAnyEvent(e);
	}
	@EventHandler
	public void onWorldUnload(WorldUnloadEvent e) {
		onAnyEvent(e);
	}
	
	//private
	private void onAnyEvent(Event e) {
		Class<? extends Event> c = e.getClass();
		Class<? extends EventCommand> get = events.get(c);
		EventCommand run = null;
		
		if (get == null) {
			Class<?> p = c.getSuperclass();
			get = events.get(p);
			if (get == null) {
				get = events.get(p.getSuperclass());
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