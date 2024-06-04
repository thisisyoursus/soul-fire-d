package it.crystalnest.soul_fire_d.config;

import it.crystalnest.cobweb.api.config.CommonConfig;
import it.crystalnest.soul_fire_d.Constants;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mod common configuration.
 */
@ApiStatus.Internal
public final class ModConfig extends CommonConfig {
  /**
   * Mod common configuration.
   */
  public static final ModConfig CONFIG = register(Constants.MOD_ID, ModConfig::new);

  /**
   * Whether to enable Soul Fire Aspect enchantment.
   */
  private ModConfigSpec.BooleanValue enableSoulFireAspect;

  /**
   * Whether Soul Fire Aspect can appear in the enchanting table and loots.
   */
  private ModConfigSpec.BooleanValue enableSoulFireAspectDiscovery;

  /**
   * Whether Soul Fire Aspect can be traded with villagers.
   */
  private ModConfigSpec.BooleanValue enableSoulFireAspectTrades;

  /**
   * Whether Soul Fire Aspect is a treasure enchantment (like Vanilla Mending).
   */
  private ModConfigSpec.BooleanValue enableSoulFireAspectTreasure;

  /**
   * Whether to enable Soul Flame enchantment.
   */
  private ModConfigSpec.BooleanValue enableSoulFlame;

  /**
   * Whether Soul Flame can appear in the enchanting table and loots.
   */
  private ModConfigSpec.BooleanValue enableSoulFlameDiscovery;

  /**
   * Whether Soul Flame can be traded with villagers.
   */
  private ModConfigSpec.BooleanValue enableSoulFlameTrades;

  /**
   * Whether Soul Flame is a treasure enchantment (like Vanilla Mending).
   */
  private ModConfigSpec.BooleanValue enableSoulFlameTreasure;

  /**
   * @param builder configuration builder.
   */
  private ModConfig(ModConfigSpec.Builder builder) {
    super(builder);
  }

  /**
   * Returns the value of {@link #enableSoulFireAspect} as read from the configuration file.
   *
   * @return the value of {@link #enableSoulFireAspect} as read from the configuration file.
   */
  public static Boolean getEnableSoulFireAspect() {
    return CONFIG.enableSoulFireAspect.get();
  }

  /**
   * Returns the value of {@link #enableSoulFireAspectDiscovery} as read from the configuration file.
   *
   * @return the value of {@link #enableSoulFireAspectDiscovery} as read from the configuration file.
   */
  public static Boolean getEnableSoulFireAspectDiscovery() {
    return CONFIG.enableSoulFireAspectDiscovery.get();
  }

  /**
   * Returns the value of {@link #enableSoulFireAspectTrades} as read from the configuration file.
   *
   * @return the value of {@link #enableSoulFireAspectTrades} as read from the configuration file.
   */
  public static Boolean getEnableSoulFireAspectTrades() {
    return CONFIG.enableSoulFireAspectTrades.get();
  }

  /**
   * Returns the value of {@link #enableSoulFireAspectTreasure} as read from the configuration file.
   *
   * @return the value of {@link #enableSoulFireAspectTreasure} as read from the configuration file.
   */
  public static Boolean getEnableSoulFireAspectTreasure() {
    return CONFIG.enableSoulFireAspectTreasure.get();
  }

  /**
   * Returns the value of {@link #enableSoulFlame} as read from the configuration file.
   *
   * @return the value of {@link #enableSoulFlame} as read from the configuration file.
   */
  public static Boolean getEnableSoulFlame() {
    return CONFIG.enableSoulFlame.get();
  }

  /**
   * Returns the value of {@link #enableSoulFlameDiscovery} as read from the configuration file.
   *
   * @return the value of {@link #enableSoulFlameDiscovery} as read from the configuration file.
   */
  public static Boolean getEnableSoulFlameDiscovery() {
    return CONFIG.enableSoulFlameDiscovery.get();
  }

  /**
   * Returns the value of {@link #enableSoulFlameTrades} as read from the configuration file.
   *
   * @return the value of {@link #enableSoulFlameTrades} as read from the configuration file.
   */
  public static Boolean getEnableSoulFlameTrades() {
    return CONFIG.enableSoulFlameTrades.get();
  }

  /**
   * Returns the value of {@link #enableSoulFlameTreasure} as read from the configuration file.
   *
   * @return the value of {@link #enableSoulFlameTreasure} as read from the configuration file.
   */
  public static Boolean getEnableSoulFlameTreasure() {
    return CONFIG.enableSoulFlameTreasure.get();
  }

  @Override
  protected void define(ModConfigSpec.Builder builder) {
    enableSoulFireAspect = defineEnableEnchantment(builder, "Soul Fire Aspect");
    enableSoulFireAspectTreasure = defineEnableTreasure(builder, "Soul Fire Aspect");
    enableSoulFireAspectDiscovery = defineEnableDiscovery(builder, "Soul Fire Aspect");
    enableSoulFireAspectTrades = defineEnableTrades(builder, "Soul Fire Aspect");
    enableSoulFlame = defineEnableEnchantment(builder, "Soul Flame");
    enableSoulFlameTreasure = defineEnableTreasure(builder, "Soul Flame");
    enableSoulFlameDiscovery = defineEnableDiscovery(builder, "Soul Flame");
    enableSoulFlameTrades = defineEnableTrades(builder, "Soul Flame");
  }

  private ModConfigSpec.BooleanValue defineEnableEnchantment(ModConfigSpec.Builder builder, String enchantment) {
    return builder.comment(" Whether to enable " + enchantment + " enchantment.", " Takes precedence over all other " + enchantment + " configs.").define("enable " + enchantment.toLowerCase(), true);
  }

  private ModConfigSpec.BooleanValue defineEnableTreasure(ModConfigSpec.Builder builder, String enchantment) {
    return builder.comment(
      " Whether " + enchantment + " cannot appear in the enchanting table.",
      " If enabled along with [enable " + enchantment.toLowerCase() + " discovery], the enchantment won't appear in the enchanting table, but can still be found in loots."
    ).define("enable " + enchantment.toLowerCase() + " treasure", false);
  }

  private ModConfigSpec.BooleanValue defineEnableDiscovery(ModConfigSpec.Builder builder, String enchantment) {
    return builder.comment(" Whether " + enchantment + " can appear in the enchanting table and loots.", " [enable " + enchantment + " treasure] takes precedence.").define("enable " + enchantment  + " discovery", true);
  }

  private ModConfigSpec.BooleanValue defineEnableTrades(ModConfigSpec.Builder builder, String enchantment) {
    return builder.comment(" Whether " + enchantment + " can be found in villager trades.").define("enable " + enchantment  + " trades", true);
  }
}
