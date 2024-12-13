package org.vdevs.guessthenumber.plugin.Storage;

import org.vdevs.guessthenumber.plugin.Main;

public class GetStorageType {

    private Main plugin;

    // Constructor to pass plugin instance
    public GetStorageType(Main plugin) {
        this.plugin = plugin;
    }

    // Get the storage type from the config
    public String getStorageType() {
        String storageType = plugin.getConfig().getString("storage.storage_type");
        if (storageType == null) {
            plugin.getLogger().warning("Storage type is not set in the config, defaulting to 'yaml'.");
            return "none";  // Default to NONE if not defined
        }
        if(storageType.equals("mysql")){
            return "mysql";
        }
        if (storageType.equals("sqlite") || storageType.equals("yaml")) {
            return storageType;
        }
        if(storageType.equals("none")){
            return "none";
        }
        else {
            plugin.getLogger().warning("Invalid storage type in config. Defaulting to 'none'.");
            return "none";  // Fallback to NONE if the value is invalid
        }
    }
}
