package ninja.egg82.velocity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Handler;

import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.analytics.exceptions.NullExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.utils.VersionUtil;
import ninja.egg82.utils.FileUtil;
import ninja.egg82.utils.ThreadUtil;
import ninja.egg82.velocity.core.ShutdownEventHandler;
import ninja.egg82.velocity.messaging.EnhancedVelocityMessageHandler;
import ninja.egg82.velocity.processors.CommandProcessor;
import ninja.egg82.velocity.processors.EventProcessor;

public abstract class BasePlugin {
    // vars
    private ProxyServer proxy = null;
    private Logger logger = null;

    private volatile CommandSource consoleSource = null;

    private static volatile PluginContainer container = null;
    private static volatile String pluginName = null;
    private static volatile String pluginVersion = null;
    private volatile String externalIp = null;
    private volatile String serverId = null;

    private java.util.logging.Logger utilLogger = java.util.logging.Logger.getLogger(getClass().getName());

    // constructor
    @SuppressWarnings("resource")
    public BasePlugin(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;

        ServiceLocator.provideService(this);
        ServiceLocator.provideService(NullExceptionHandler.class);

        ThreadUtil.submit(new Runnable() {
            public void run() {
                int tries = 0;
                PluginContainer container = null;
                do {
                    container = proxy.getPluginManager().fromInstance(ServiceLocator.getService(BasePlugin.class)).orElse(null);
                    if (container == null || !container.getDescription().getSource().isPresent() || !container.getDescription().getName().isPresent()
                        || !container.getDescription().getVersion().isPresent()) {
                        tries++;
                        try {
                            Thread.sleep(50L);
                        } catch (Exception ex) {
                            IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                            if (handler != null) {
                                handler.sendException(ex);
                            }
                            ex.printStackTrace();
                        }
                    }
                } while (container == null && tries < 100);
                if (container == null) {
                    throw new RuntimeException("This plugin's container does not exist!");
                }
                BasePlugin.container = container;

                // BungeeCord onLoad
                serverId = getId();
                if (serverId == null || serverId.isEmpty() || serverId.equalsIgnoreCase("unnamed") || serverId.equalsIgnoreCase("unknown") || serverId.equalsIgnoreCase("default")) {
                    serverId = UUID.randomUUID().toString();
                    writeProperties();
                }

                String name = container.getDescription().getName().get();
                if (name == null) {
                    throw new RuntimeException("Plugin name cannot null.");
                }
                BasePlugin.pluginName = name;

                String version = container.getDescription().getVersion().get();
                if (version == null) {
                    throw new RuntimeException("Plugin version cannot null.");
                }
                BasePlugin.pluginVersion = version;

                ThreadUtil.rename(name);

                ServiceLocator.provideService(logger);
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null && handler instanceof Handler) {
                    utilLogger.addHandler((Handler) handler);
                }

                if (!SLF4JBridgeHandler.isInstalled()) {
                    SLF4JBridgeHandler.removeHandlersForRootLogger();
                    SLF4JBridgeHandler.install();
                }

                ServiceLocator.provideService(getClass().getClassLoader());

                consoleSource = proxy.getConsoleCommandSource();

                ServiceLocator.provideService(CommandProcessor.class, false);

                // BungeCord onEnable
                ServiceLocator.provideService(new EnhancedVelocityMessageHandler(pluginName, serverId));

                ServiceLocator.provideService(EventProcessor.class, false);

                // Shutdown event
                proxy.getEventManager().register(ServiceLocator.getService(BasePlugin.class), ProxyShutdownEvent.class, PostOrder.NORMAL, new ShutdownEventHandler());

                onStartupComplete();
            }
        });
    }

    // public
    public final ProxyServer getProxy() {
        return proxy;
    }
    public final Logger getLogger() {
        return logger;
    }

    public final java.util.logging.Logger getJULLogger() {
        return utilLogger;
    }

    public String getPluginName() {
        return pluginName;
    }
    public String getPluginVersion() {
        return pluginVersion;
    }
    public PluginDescription getDescription() {
        return container.getDescription();
    }

    public final void printInfo(String message) {
        printInfo(message, TextColor.WHITE);
    }
    public final void printInfo(String message, TextColor color) {
        printInfo(TextComponent.of(message, color));
    }
    public final void printInfo(Component component) {
        if (consoleSource == null) {
            consoleSource = proxy.getConsoleCommandSource();
        }

        if (consoleSource != null) {
            consoleSource.sendMessage(TextComponent.of("[INFO] ", TextColor.GRAY).append(TextComponent.of("[" + pluginName + "] ", TextColor.WHITE)).append(component));
        } else {
            logger.info(component.toString());
        }
    }
    public final void printWarning(String message) {
        printWarning(message, TextColor.WHITE);
    }
    public final void printWarning(String message, TextColor color) {
        printWarning(TextComponent.of(message, color));
    }
    public final void printWarning(Component component) {
        if (consoleSource == null) {
            consoleSource = proxy.getConsoleCommandSource();
        }

        if (consoleSource != null) {
            consoleSource.sendMessage(TextComponent.of("[WARN] ", TextColor.YELLOW).append(TextComponent.of("[" + pluginName + "] ", TextColor.WHITE)).append(component));
        } else {
            logger.warn(component.toString());
        }
    }
    public final void printError(String message) {
        printError(message, TextColor.WHITE);
    }
    public final void printError(String message, TextColor color) {
        printError(TextComponent.of(message, color));
    }
    public final void printError(Component component) {
        if (consoleSource == null) {
            consoleSource = proxy.getConsoleCommandSource();
        }

        if (consoleSource != null) {
            consoleSource.sendMessage(TextComponent.of("[ERROR] ", TextColor.RED).append(TextComponent.of("[" + pluginName + "] ", TextColor.WHITE)).append(component));
        } else {
            logger.error(component.toString());
        }
    }

    public String getServerIp() {
        if (externalIp == null) {
            externalIp = getExternalIp();
        }
        return externalIp;
    }
    public final String getServerId() {
        return serverId;
    }

    // private
    protected abstract void onStartupComplete();

    protected void reflect(String version, String pkg) {
        reflect(version, pkg, true);
    }
    protected void reflect(String version, String pkg, boolean lazyInitialize) {
        Class<Object> bestMatch = VersionUtil.getBestMatch(Object.class, version, pkg, false);

        if (bestMatch != null) {
            ServiceLocator.provideService(bestMatch, lazyInitialize);
        }
    }

    private String getId() {
        File propertiesFile = new File(container.getDescription().getSource().get().toAbsolutePath().getParent().getParent().toFile(), "velocity-extra.toml");
        String path = propertiesFile.getAbsolutePath();

        if (!FileUtil.pathExists(path) || !FileUtil.pathIsFile(path)) {
            return null;
        }

        try {
            FileUtil.open(path);

            String[] lines = toString(FileUtil.read(path, 0L), Charset.forName("UTF-8")).replaceAll("\r", "").split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].trim().startsWith("stats=")) {
                    return lines[i].substring(6).trim();
                } else if (lines[i].trim().startsWith("stats =")) {
                    return lines[i].substring(7).trim();
                }
            }
        } catch (Exception ex) {

        }

        return null;
    }
    private void writeProperties() {
        File propertiesFile = new File(container.getDescription().getSource().get().toAbsolutePath().getParent().getParent().toFile(), "velocity-extra.toml");
        String path = propertiesFile.getAbsolutePath();

        if (FileUtil.pathExists(path) && !FileUtil.pathIsFile(path)) {
            FileUtil.deleteDirectory(path);
        }
        if (!FileUtil.pathExists(path)) {
            try {
                FileUtil.createFile(path);
            } catch (Exception ex) {
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
                throw new RuntimeException("Could not create velocity-extra.toml", ex);
            }
        }

        try {
            FileUtil.open(path);

            String[] lines = toString(FileUtil.read(path, 0L), Charset.forName("UTF-8")).replaceAll("\r", "").split("\n");
            boolean found = false;
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].trim().startsWith("stats=")) {
                    found = true;
                    lines[i] = "stats= " + serverId;
                } else if (lines[i].trim().startsWith("stats =")) {
                    found = true;
                    lines[i] = "stats = " + serverId;
                }
            }
            if (!found) {
                ArrayList<String> temp = new ArrayList<String>(Arrays.asList(lines));
                temp.add("stats = " + serverId);
                lines = temp.toArray(new String[0]);
            }

            FileUtil.erase(path);
            FileUtil.write(path, toBytes(String.join(FileUtil.LINE_SEPARATOR, lines), Charset.forName("UTF-8")), 0L);
            FileUtil.close(path);
        } catch (Exception ex) {
            IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
            if (handler != null) {
                handler.sendException(ex);
            }
            throw new RuntimeException("Could not write to velocity-extra.toml", ex);
        }
    }

    private byte[] toBytes(String input, Charset enc) {
        return input.getBytes(enc);
    }
    private String toString(byte[] input, Charset enc) {
        return new String(input, enc);
    }
    private String getExternalIp() {
        URL url = null;
        BufferedReader in = null;

        String[] sites = new String[] { "http://checkip.amazonaws.com", "https://icanhazip.com/", "http://www.trackip.net/ip", "http://myexternalip.com/raw", "http://ipecho.net/plain",
            "https://bot.whatismyipaddress.com/" };

        for (String addr : sites) {
            try {
                url = new URL(addr);
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                String ip = in.readLine();
                InetAddress.getByName(ip);
                return ip;
            } catch (Exception ex) {
                continue;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                        in = null;
                    } catch (Exception ex) {

                    }
                }
            }
        }

        return null;
    }
}
