package de.polocloud.bootstrap.module;

import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.module.CloudModule;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleCache extends ConcurrentHashMap<CloudModule, ModuleLocalCache> {

    public void unloadModules() {
        for (CloudModule module : this.keySet()) {
            URLClassLoader classLoader = this.get(module).getLoader();

            if (module.canReload()) {

                module.onShutdown();
                EventRegistry.unregisterModuleListener(module);

                String moduleName = get(module).getModuleData().getName();

                remove(module);
                try {
                    classLoader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Logger.log(Logger.PREFIX + "The module " + ConsoleColors.LIGHT_BLUE + moduleName + ConsoleColors.GRAY + " unloaded...");
            }
        }
    }

    public void unloadModule(CloudModule module) {
        try {
            if (module == null || !containsKey(module)) {
                return;
            }

            URLClassLoader urlClassLoader = get(module).getLoader();
            EventRegistry.unregisterModuleListener(module);
            String moduleName = get(module).getModuleData().getName();

            remove(module);

            urlClassLoader.close();
            Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "unloaded module » " + ConsoleColors.LIGHT_BLUE + moduleName + ConsoleColors.GRAY + "!");
        } catch (IOException exception) {
            exception.printStackTrace();
            Logger.log(LoggerType.ERROR, Logger.PREFIX + ConsoleColors.RED + "Failed to unload module » " + ConsoleColors.LIGHT_BLUE + get(module).getModuleData().getName() + ConsoleColors.RED + "!" + ConsoleColors.GRAY);
            return;
        }
    }

    public CloudModule getModuleByName(String name) {
        return this.keySet().stream().filter(module -> get(module).getModuleData().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    protected Class<?> findModuleClass(String name) throws ClassNotFoundException {
        for (ModuleLocalCache value : values()) {
            Class<?> clazz = value.getLoader().findClass_(name, false);
            if (clazz != null) {
                return clazz;
            }
        }
        throw new ClassNotFoundException(name);
    }

}
