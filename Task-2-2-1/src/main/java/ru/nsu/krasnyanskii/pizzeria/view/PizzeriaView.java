package ru.nsu.krasnyanskii.pizzeria.view;

/**
 * Composite view that aggregates all role-specific view interfaces.
 *
 * <p>Implementations of this interface satisfy every output channel of the pizzeria.
 * Clients should depend on the narrowest role interface they need
 * ({@link BakerView}, {@link CourierView}, {@link GeneratorView}, {@link LifecycleView})
 * rather than on this composite.</p>
 */
public interface PizzeriaView
        extends BakerView, CourierView, GeneratorView, LifecycleView {
}
