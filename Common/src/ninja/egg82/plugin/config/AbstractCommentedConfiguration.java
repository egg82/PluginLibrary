package ninja.egg82.plugin.config;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class AbstractCommentedConfiguration implements CommentedConfigurationNode {
	//vars
	private CommentedConfigurationNode root = null;
	
	//constructor
	public AbstractCommentedConfiguration(CommentedConfigurationNode root) {
		if (root == null) {
			throw new IllegalArgumentException("root cannot be null.");
		}
		
		this.root = root;
	}
	
	//public
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
	public CommentedConfigurationNode getParent() {
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
	public CommentedConfigurationNode setValue(Object value) {
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
	public CommentedConfigurationNode mergeValuesFrom(ConfigurationNode other) {
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
	public List<? extends CommentedConfigurationNode> getChildrenList() {
		return root.getChildrenList();
	}
	/**
	 * {@inheritDoc}
	 */
	public Map<Object, ? extends CommentedConfigurationNode> getChildrenMap() {
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
	public CommentedConfigurationNode getAppendedNode() {
		return root.getAppendedNode();
	}
	/**
	 * {@inheritDoc}
	 */
	public CommentedConfigurationNode getNode(Object... path) {
		return root.getNode(path);
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean isVirtual() {
		return root.isVirtual();
	}
	/**
	 * {@inheritDoc}
	 */
	public Optional<String> getComment() {
		return root.getComment();
	}
	/**
	 * {@inheritDoc}
	 */
	public CommentedConfigurationNode setComment(String comment) {
		return root.setComment(comment);
	}
	
	//private
	
}
