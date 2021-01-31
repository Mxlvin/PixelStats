package com.rmjtromp.pixelstats.core.utils.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang3.Validate;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredListener {
    private final Listener listener;
    private final EventPriority priority;
    private final EventExecutor executor;
    private final boolean ignoreCancelled;

    public RegisteredListener(final Listener listener, final EventExecutor executor, final EventPriority priority, final boolean ignoreCancelled) {
        this.listener = listener;
        this.priority = priority;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
    }

    /**
     * Gets the listener for this registration
     *
     * @return Registered Listener
     */
    public Listener getListener() {
        return listener;
    }

    /**
     * Gets the priority for this registration
     *
     * @return Registered Priority
     */
    public EventPriority getPriority() {
        return priority;
    }

    /**
     * Calls the event executor
     *
     * @param event The event
     * @throws EventException If an event handler throws an exception.
     */
    public void callEvent(final Event event) throws EventException {
        if (event instanceof Cancellable){
            if (((Cancellable) event).isCancelled() && isIgnoringCancelled()){
                return;
            }
        }
        executor.execute(listener, event);
    }

     /**
     * Whether this listener accepts cancelled events
     *
     * @return True when ignoring cancelled events
     */
    public boolean isIgnoringCancelled() {
        return ignoreCancelled;
    }
    
    public static Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener) {
        Validate.notNull(listener, "Listener can not be null");

        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<Class<? extends Event>, Set<RegisteredListener>>();
        Set<Method> methods;
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            methods = new HashSet<Method>(publicMethods.length, Float.MAX_VALUE);
            for (Method method : publicMethods) {
                methods.add(method);
            }
            for (Method method : listener.getClass().getDeclaredMethods()) {
                methods.add(method);
            }
        } catch (NoClassDefFoundError e) {
            System.out.print("Failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
            return ret;
        }

        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                System.out.print("Attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }
            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<RegisteredListener> eventSet = ret.get(eventClass);
            if (eventSet == null) {
                eventSet = new HashSet<RegisteredListener>();
                ret.put(eventClass, eventSet);
            }

//            for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
//                // This loop checks for extending deprecated events
//                if (clazz.getAnnotation(Deprecated.class) != null) {
//                    Warning warning = clazz.getAnnotation(Warning.class);
//                    WarningState warningState = server.getWarningState();
//                    if (!warningState.printFor(warning)) {
//                        break;
//                    }
//                    plugin.getLogger().log(
//                            Level.WARNING,
//                            String.format(
//                                    "\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated." +
//                                    " \"%s\"; please notify the authors %s.",
//                                    plugin.getDescription().getFullName(),
//                                    clazz.getName(),
//                                    method.toGenericString(),
//                                    (warning != null && warning.reason().length() != 0) ? warning.reason() : "Server performance will be affected",
//                                    Arrays.toString(plugin.getDescription().getAuthors().toArray())),
//                            warningState == WarningState.ON ? new AuthorNagException(null) : null);
//                    break;
//                }
//            }

            EventExecutor executor = new EventExecutor() {
                public void execute(Listener listener, Event event) throws EventException {
                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        method.invoke(listener, event);
                    } catch (InvocationTargetException ex) {
                        throw new EventException(ex.getCause());
                    } catch (Throwable t) {
                        throw new EventException(t);
                    }
                }
            };
            eventSet.add(new RegisteredListener(listener, executor, eh.priority(), eh.ignoreCancelled()));
        }
        return ret;
    }
}
