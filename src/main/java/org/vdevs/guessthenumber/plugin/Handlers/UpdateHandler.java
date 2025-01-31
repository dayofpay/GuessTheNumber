package org.vdevs.guessthenumber.plugin.Handlers;

import org.vdevs.guessthenumber.plugin.Main;
import com.tchristofferson.configupdater.ConfigUpdater;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class UpdateHandler {
    private final Main plugin;
    public UpdateHandler(final Main plugin) {
        this.plugin = plugin;
    }
    public void checkConfigUpdates() {
        plugin.saveDefaultConfig();
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(plugin, "config.yml", configFile, Arrays.asList());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}