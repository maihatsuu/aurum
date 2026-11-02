package maihatsuu.aurum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Plain JSON config at config/aurum.json. Read once at startup. */
public final class AurumConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(Aurum.MOD_ID + ".json");

	private static Data data = new Data();

	/** Serialized shape of the config file. */
	private static final class Data {
		boolean enabled = true;
	}

	private AurumConfig() {
	}

	public static boolean enabled() {
		return data.enabled;
	}

	static void load() {
		if (Files.exists(PATH)) {
			try (Reader reader = Files.newBufferedReader(PATH)) {
				Data loaded = GSON.fromJson(reader, Data.class);
				data = loaded != null ? loaded : new Data();
			} catch (IOException | JsonParseException e) {
				Aurum.LOGGER.warn("Failed to read {}, using defaults", PATH, e);
				data = new Data();
				return; // don't overwrite the user's broken file
			}
		}
		save();
	}

	private static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException e) {
			Aurum.LOGGER.warn("Failed to write {}", PATH, e);
		}
	}
}
