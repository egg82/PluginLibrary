package ninja.egg82.plugin.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.ReflectUtil;

public class PluginReflectUtil {
    // vars

    // constructor
    public PluginReflectUtil() {

    }

    // public
    public static int addServicesFromPackage(String packageName, boolean lazyInitialize) {
        return addServicesFromPackage(packageName, lazyInitialize, true);
    }

    public static int addServicesFromPackage(String packageName, boolean lazyInitialize, boolean recursive) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName cannot be null.");
        }

        List<Class<Object>> services = ReflectUtil.getClasses(Object.class, packageName, recursive, false, false);
        for (Class<Object> service : services) {
            ServiceLocator.provideService(service, lazyInitialize);
        }
        return services.size();
    }

    public static Map<String, String> getCommandMapFromPackage(String packageName, String classNamePrefix, String classNameSuffix) {
        return getCommandMapFromPackage(packageName, true, classNamePrefix, classNameSuffix);
    }

    public static Map<String, String> getCommandMapFromPackage(String packageName, boolean recursive, String classNamePrefix, String classNameSuffix) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName cannot be null.");
        }

        if (classNamePrefix == null) {
            classNamePrefix = "";
        } else {
            classNamePrefix = classNamePrefix.toLowerCase();
        }
        if (classNameSuffix == null) {
            classNameSuffix = "";
        } else {
            classNameSuffix = classNameSuffix.toLowerCase();
        }

        HashMap<String, String> retVal = new HashMap<String, String>();
        int minLength = classNamePrefix.length() + classNameSuffix.length();

        List<Class<Object>> commands = ReflectUtil.getClasses(Object.class, packageName, recursive, false, false);
        for (Class<Object> c : commands) {
            String n = c.getSimpleName().toLowerCase();
            String p = c.getName();
            p = p.substring(0, p.lastIndexOf('.'));

            if (n.length() <= minLength) {
                continue;
            }

            String cn = n;
            if (!classNamePrefix.isEmpty()) {
                if (!cn.startsWith(classNamePrefix)) {
                    continue;
                }
                cn = cn.substring(classNamePrefix.length());
            }
            if (!classNameSuffix.isEmpty()) {
                if (!cn.endsWith(classNameSuffix)) {
                    continue;
                }
                cn = cn.substring(0, cn.length() - classNameSuffix.length());
            }

            retVal.put(p + "." + n, cn);
        }

        return retVal;
    }

    // private

}
