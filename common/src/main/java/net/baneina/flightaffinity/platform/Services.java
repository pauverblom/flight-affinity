package net.baneina.flightaffinity.platform;

import net.baneina.flightaffinity.Constants;
import net.baneina.flightaffinity.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

/**
 * Service locator for cross-platform modding implementations.
 */
public class Services {

    /** Private constructor to hide the implicit public one. */
    private Services() {}

    /** The platform helper service. */
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    /**
     * Loads a service using Java's ServiceLoader.
     * @param clazz The interface class to load an implementation for.
     * @param <T> The type of the service.
     * @return The loaded service instance.
     */
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}