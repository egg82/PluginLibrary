package ninja.egg82.plugin.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public abstract class AbstractConfiguration implements ConfigurationNode {
    // vars
    private ConfigurationNode root = null;

    // constructor
    public AbstractConfiguration(ConfigurationNode root) {
        if (root == null) {
            throw new IllegalArgumentException("root cannot be null.");
        }

        this.root = root;
    }

    // public
    /**
     * {@inheritDoc}
     */
    public Object getKey() {
        return root.getKey();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getPath() {
        return root.getPath();
    }

    /**
     * {@inheritDoc}
     */
    public ConfigurationNode getParent() {
        return root.getParent();
    }

    /**
     * {@inheritDoc}
     */
    public ConfigurationOptions getOptions() {
        return root.getOptions();
    }

    /**
     * {@inheritDoc}
     */
    public Object getValue(Object def) {
        return root.getValue(def);
    }

    /**
     * {@inheritDoc}
     */
    public Object getValue(Supplier<Object> defSupplier) {
        return root.getValue(defSupplier);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getValue(Function<Object, T> transformer, T def) {
        return root.getValue(transformer, def);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getValue(Function<Object, T> transformer, Supplier<T> defSupplier) {
        return root.getValue(transformer, defSupplier);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getList(Function<Object, T> transformer) {
        return root.getList(transformer);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getList(Function<Object, T> transformer, List<T> def) {
        return root.getList(transformer, def);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getList(Function<Object, T> transformer, Supplier<List<T>> defSupplier) {
        return root.getList(transformer, defSupplier);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getList(TypeToken<T> type, List<T> def) throws ObjectMappingException {
        return root.getList(type, def);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getList(TypeToken<T> type, Supplier<List<T>> defSupplier) throws ObjectMappingException {
        return root.getList(type, defSupplier);
    }

    /**
     * {@inheritDoc}
     */
    public ConfigurationNode setValue(Object value) {
        return root.setValue(value);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getValue(TypeToken<T> type, T def) throws ObjectMappingException {
        return root.getValue(type, def);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getValue(TypeToken<T> type, Supplier<T> defSupplier) throws ObjectMappingException {
        return root.getValue(type, defSupplier);
    }

    /**
     * {@inheritDoc}
     */
    public ConfigurationNode mergeValuesFrom(ConfigurationNode other) {
        return root.mergeValuesFrom(other);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasListChildren() {
        return root.hasListChildren();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasMapChildren() {
        return root.hasMapChildren();
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends ConfigurationNode> getChildrenList() {
        return root.getChildrenList();
    }

    /**
     * {@inheritDoc}
     */
    public Map<Object, ? extends ConfigurationNode> getChildrenMap() {
        return root.getChildrenMap();
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeChild(Object key) {
        return root.removeChild(key);
    }

    /**
     * {@inheritDoc}
     */
    public ConfigurationNode getAppendedNode() {
        return root.getAppendedNode();
    }

    /**
     * {@inheritDoc}
     */
    public ConfigurationNode getNode(Object... path) {
        return root.getNode(path);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVirtual() {
        return root.isVirtual();
    }

    public ValueType getValueType() {
        return root.getValueType();
    }

    public ConfigurationNode copy() {
        return root.copy();
    }

    // private

}
