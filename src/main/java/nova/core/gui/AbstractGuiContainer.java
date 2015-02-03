package nova.core.gui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import nova.core.gui.GuiEvent.ResizeEvent;
import nova.core.gui.layout.BorderLayout;
import nova.core.gui.layout.GuiLayout;
import nova.core.gui.nativeimpl.NativeContainer;

/**
 * This class provides container for {@link GuiComponent}
 */
public abstract class AbstractGuiContainer<T extends NativeContainer> extends GuiComponent<T> {

	private GuiLayout layout = new BorderLayout();
	private HashMap<String, GuiComponent<?>> children = new HashMap<String, GuiComponent<?>>();

	public AbstractGuiContainer(String uniqueID) {
		super(uniqueID);
		this.registerListener(this::onResized, ResizeEvent.class);
	}

	/**
	 * @return Immutable collection of components inside this container
	 */
	public Collection<GuiComponent<?>> getChildComponents() {
		return Collections.unmodifiableCollection(children.values());
	}

	/**
	 * Returns a child {@link GuiComponent} based on its qualified name.
	 * 
	 * @param qualifiedName qualified name of the sub component
	 * @return The requested {@link GuiComponent} or {@code null} if not
	 *         present.
	 * 
	 * @see GuiComponent#getQualifiedName()
	 * @see AbstractGuiContainer#getChildElement(String, Class)
	 */
	public GuiComponent<?> getChildElement(String qualifiedName) {
		// TODO untested.
		if (qualifiedName.startsWith(getQualifiedName())) {
			qualifiedName = qualifiedName.substring(getQualifiedName().length());
		}
		int dot = qualifiedName.indexOf(".");
		if (dot == -1) {
			return children.get(qualifiedName);
		}
		GuiComponent<?> subContainer = children.get(qualifiedName.substring(0, dot - 1));
		if (subContainer instanceof AbstractGuiContainer) {
			return ((AbstractGuiContainer<?>) subContainer).getChildElement(qualifiedName.substring(dot + 1));
		}
		return null;
	}

	/**
	 * Will return a child component that matches the provided subclass of
	 * {@link GuiComponent}.
	 * 
	 * @param qualifiedName qualified name of the sub component
	 * @param clazz class of the requested {@link GuiComponent}
	 * 
	 * @return The requested {@link GuiComponent} or {@code null} if not present
	 *         / the type doesn't match.
	 */
	@SuppressWarnings("unchecked")
	public <E extends GuiComponent<?>> E getChildElement(String qualifiedName, Class<T> clazz) {
		GuiComponent<?> component = getChildElement(qualifiedName);
		if (clazz.isInstance(component))
			return (E) component;
		return null;
	}

	/**
	 * Sets layout of this container
	 * 
	 * @param layout {@link GuiLayout} to set
	 * @return This GuiContainer
	 * @throws NullPointerException if the provided layout is {@code null}.
	 */
	public AbstractGuiContainer<T> setLayout(GuiLayout layout) {
		if (layout == null)
			throw new NullPointerException();
		this.layout = layout;
		layout.revalidate(this);
		return this;
	}

	/**
	 * Processes an event, i.e. sends it to each children
	 * 
	 * @param event {@link GuiEvent} to process
	 */
	public void onEvent(GuiEvent event) {
		super.onEvent(event);
		getChildComponents().stream().forEach((e) -> {
			e.onEvent(event);
		});
	}

	/**
	 * Adds {@link GuiComponent} to this container.
	 * 
	 * @param component {@link GuiCanvas} to add
	 * @param properties Properties for the Layout
	 * @return This GuiContainer
	 * 
	 * @see GuiLayout#add(GuiComponent, AbstractGuiContainer, Object[])
	 */
	public AbstractGuiContainer<T> addElement(GuiComponent<?> component, Object... properties) {
		if (component == null)
			throw new NullPointerException();
		component.parentContainer = Optional.of(this);
		component.updateQualifiedName();
		children.put(component.getID(), component);
		layout.add(component, this, properties);
		return this;
	}

	/**
	 * Removes {@link GuiComponent}. Shouldn't be used unless really needed as
	 * it requires the sub component to update its qualified name using
	 * {@link #updateQualifiedName()}.
	 * 
	 * @param component {@link GuiComponent} to remove
	 * @return This GuiContainer
	 */
	public AbstractGuiContainer<T> removeElement(GuiComponent<?> component) {
		if (component == null)
			throw new NullPointerException();
		children.remove(component);
		layout.remove(component);
		component.updateQualifiedName();
		return this;
	}

	public void onResized(ResizeEvent event) {
		layout.revalidate(this);
	}

	@Override
	protected void updateQualifiedName() {
		super.updateQualifiedName();
		children.forEach((k, v) -> {
			v.updateQualifiedName();
		});
	}
}
