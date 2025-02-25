package it.crystalnest.soul_fire_d.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.crystalnest.soul_fire_d.Constants;
import it.crystalnest.soul_fire_d.api.Fire;
import it.crystalnest.soul_fire_d.api.FireManager;
import it.crystalnest.soul_fire_d.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

/**
 * Resource reload listener for syncing ddfires.
 */
public abstract class FireResourceReloadListener extends SimpleJsonResourceReloadListener {
  /**
   * Current ddfires to unregister (previous registered ddfires).
   */
  protected static final ArrayList<ResourceLocation> ddfiresUnregister = new ArrayList<>();

  /**
   * Current registered ddfires.
   */
  protected static final ArrayList<ResourceLocation> ddfiresRegister = new ArrayList<>();

  /**
   * JSON field name for a Fire's source block.
   */
  private static final String SOURCE_FIELD_NAME = "source";

  /**
   * JSON field name for a Fire's campfire block.
   */
  private static final String CAMPFIRE_FIELD_NAME = "campfire";

  protected FireResourceReloadListener() {
    super(new Gson(), "fires");
  }

  /**
   * Handles datapack sync event.
   *
   * @param player {@link ServerPlayer} to which the data is being sent.
   */
  protected static void handle(@Nullable ServerPlayer player) {
    for (ResourceLocation fireType : ddfiresUnregister) {
      Services.NETWORK.sendToClient(player, fireType);
    }
    for (ResourceLocation fireType : ddfiresRegister) {
      Services.NETWORK.sendToClient(player, FireManager.getFire(fireType));
    }
  }

  /**
   * Returns the given {@link JsonElement} as a {@link JsonObject}.
   *
   * @param identifier identifier of the JSON file.
   * @param element {@link JsonElement}.
   * @return the given {@link JsonElement} as a {@link JsonObject}.
   * @throws IllegalStateException if the element is not a {@link JsonObject}.
   */
  private static JsonObject getJsonObject(String identifier, JsonElement element) throws IllegalStateException {
    try {
      return element.getAsJsonObject();
    } catch (IllegalStateException e) {
      Constants.LOGGER.error(Constants.MOD_ID + " encountered a non-blocking DDFire error!\nError parsing ddfire [{}]: not a JSON object.", identifier);
      throw e;
    }
  }

  /**
   * Parses the given {@link JsonObject data} to retrieve the specified {@code field} using the provided {@code parser}.
   *
   * @param <T> element type.
   * @param identifier identifier of the JSON file.
   * @param field field to parse.
   * @param data {@link JsonObject} with data to parse.
   * @param parser function to use to retrieve parse a JSON field.
   * @return value of the field.
   * @throws NullPointerException if there's no such field.
   * @throws UnsupportedOperationException if this element is not a {@link JsonPrimitive} or {@link JsonArray}.
   * @throws IllegalStateException if this element is of the type {@link JsonArray} but contains more than a single element.
   * @throws NumberFormatException if the value contained is not a valid number and the expected type ({@code T}) was a number.
   */
  private static <T> T parse(String identifier, String field, JsonObject data, Function<JsonElement, T> parser) throws NullPointerException, UnsupportedOperationException, IllegalStateException, NumberFormatException {
    try {
      return parser.apply(data.get(field));
    } catch (NullPointerException | UnsupportedOperationException | IllegalStateException | NumberFormatException e) {
      Constants.LOGGER.error(Constants.MOD_ID + " encountered a non-blocking DDFire error!\nError parsing required field \"{}\" for ddfire [{}]: missing or malformed field.", field, identifier);
      throw e;
    }
  }

  /**
   * Parses the given {@link JsonObject data} to retrieve the specified {@code field} using the provided {@code parser}.
   *
   * @param <T> element type.
   * @param identifier identifier of the JSON file.
   * @param field field to parse.
   * @param data {@link JsonObject} with data to parse.
   * @param parser function to use to retrieve parse a JSON field.
   * @param fallback default value if no field named {@code field} exists.
   * @return value of the field or default.
   * @throws UnsupportedOperationException if this element is not a {@link JsonPrimitive} or {@link JsonArray}.
   * @throws IllegalStateException if this element is of the type {@link JsonArray} but contains more than a single element.
   * @throws NumberFormatException if the value contained is not a valid number and the expected type ({@code T}) was a number.
   */
  private static <T> T parse(String identifier, String field, JsonObject data, Function<JsonElement, T> parser, T fallback) throws UnsupportedOperationException, IllegalStateException, NumberFormatException {
    try {
      return parser.apply(data.get(field));
    } catch (NullPointerException e) {
      return fallback;
    } catch (UnsupportedOperationException | IllegalStateException | NumberFormatException e) {
      Constants.LOGGER.error(Constants.MOD_ID + " encountered a non-blocking DDFire error!\nError parsing optional field \"{}\" for ddfire [{}]: malformed field.", field, identifier);
      throw e;
    }
  }

  /**
   * Unregisters all DDFires.
   */
  private static void unregisterFires() {
    for (ResourceLocation fireType : ddfiresRegister) {
      if (FireManager.unregisterFire(fireType) != null) {
        ddfiresUnregister.add(fireType);
      }
    }
    ddfiresRegister.clear();
  }

  /**
   * Registers a DDFire.
   *
   * @param fireType fire type.
   * @param fire fire.
   */
  private static void registerFire(ResourceLocation fireType, Fire fire) {
    if (FireManager.registerFire(fire) != null) {
      ddfiresRegister.add(fireType);
    } else {
      Constants.LOGGER.error("Unable to register ddfire [{}].", fireType);
    }
  }

  /**
   * Builds and registers a DDFire.
   *
   * @param jsonFire JSON fire data.
   * @param mod related mod.
   * @param jsonIdentifier JSON ID.
   */
  private static void registerFire(JsonObject jsonFire, String mod, String jsonIdentifier) {
    ResourceLocation fireType = ResourceLocation.fromNamespaceAndPath(mod, parse(jsonIdentifier, "fire", jsonFire, JsonElement::getAsString));
    Fire.Builder builder = FireManager.fireBuilder(fireType)
      .setDamage(parse(fireType.toString(), "damage", jsonFire, JsonElement::getAsFloat, Fire.Builder.DEFAULT_DAMAGE))
      .setInvertHealAndHarm(parse(fireType.toString(), "invertHealAndHarm", jsonFire, JsonElement::getAsBoolean, Fire.Builder.DEFAULT_INVERT_HEAL_AND_HARM))
      .removeComponent(Fire.Component.CAMPFIRE_ITEM)
      .removeComponent(Fire.Component.LANTERN_BLOCK)
      .removeComponent(Fire.Component.LANTERN_ITEM)
      .removeComponent(Fire.Component.TORCH_BLOCK)
      .removeComponent(Fire.Component.TORCH_ITEM)
      .removeComponent(Fire.Component.WALL_TORCH_BLOCK)
      .removeComponent(Fire.Component.FLAME_PARTICLE);
    if (jsonFire.get(SOURCE_FIELD_NAME) != null && jsonFire.get(SOURCE_FIELD_NAME).isJsonNull()) {
      builder.removeComponent(Fire.Component.SOURCE_BLOCK);
    } else {
      String source = parse(fireType.toString(), SOURCE_FIELD_NAME, jsonFire, JsonElement::getAsString, null);
      if (source != null && ResourceLocation.tryParse(source) != null) {
        builder.setComponent(Fire.Component.SOURCE_BLOCK, ResourceLocation.parse(source));
      }
    }
    if (jsonFire.get(CAMPFIRE_FIELD_NAME) != null && jsonFire.get(CAMPFIRE_FIELD_NAME).isJsonNull()) {
      builder.removeComponent(Fire.Component.CAMPFIRE_BLOCK);
    } else {
      String campfire = parse(fireType.toString(), CAMPFIRE_FIELD_NAME, jsonFire, JsonElement::getAsString, null);
      if (campfire != null && ResourceLocation.tryParse(campfire) != null) {
        builder.setComponent(Fire.Component.CAMPFIRE_BLOCK, ResourceLocation.parse(campfire));
      }
    }
    registerFire(fireType, builder.build());
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> fires, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
    unregisterFires();
    for (Map.Entry<ResourceLocation, JsonElement> fire : fires.entrySet()) {
      String jsonIdentifier = fire.getKey().getPath();
      try {
        JsonObject jsonData = getJsonObject(jsonIdentifier, fire.getValue());
        String mod = parse(jsonIdentifier, "mod", jsonData, JsonElement::getAsString);
        if (Services.PLATFORM.isModLoaded(mod)) {
          parse(jsonIdentifier, "fires", jsonData, JsonElement::getAsJsonArray).forEach(element -> registerFire(getJsonObject(jsonIdentifier, element), mod, jsonIdentifier));
        } else {
          Constants.LOGGER.warn("Registering of ddfires for [{}] is canceled: {} is not loaded.", mod, mod);
        }
      } catch (NullPointerException | UnsupportedOperationException | IllegalStateException | NumberFormatException e) {
        Constants.LOGGER.error("Registering of ddfires for [{}] is canceled.", jsonIdentifier);
      }
    }
  }
}
