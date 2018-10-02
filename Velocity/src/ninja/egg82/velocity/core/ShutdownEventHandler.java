package ninja.egg82.velocity.core;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.velocity.processors.EventProcessor;

public class ShutdownEventHandler implements EventHandler<ProxyShutdownEvent> {
    // vars

    // constructor
    public ShutdownEventHandler() {

    }

    // public
    public void execute(ProxyShutdownEvent event) {
        ServiceLocator.getService(EventProcessor.class).close();

        if (SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.uninstall();
        }
    }

    // private

}
