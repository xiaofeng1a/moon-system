package game.alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * The facility's alarm system. Implemented as a singleton so that any object
 * anywhere in the game can trigger or query the alarm without needing a reference
 * passed through every constructor.
 *
 * <p>Uses the Observer pattern: objects that care about the alarm register
 * themselves as {@link AlarmListener}s. When the alarm is triggered, all
 * listeners are notified. Each turn the alarm is active, listeners receive
 * a tick. When the alarm deactivates, listeners are notified to revert.
 *
 * <p>SOLID — Single Responsibility: AlarmSystem only manages alarm state
 * and listener notification. It knows nothing about game logic.
 *
 * <p>SOLID — Dependency Inversion: AlarmSystem depends on the {@link AlarmListener}
 * abstraction, not on concrete classes like Undead or Door.
 */
public class AlarmSystem {

    /** The single instance. */
    private static AlarmSystem instance;

    /** All objects listening for alarm events. */
    private final List<AlarmListener> listeners = new ArrayList<>();

    /** Whether the alarm is currently active. */
    private boolean alarmActive = false;

    /** Private constructor — use {@link #getInstance()}. */
    private AlarmSystem() {}

    /**
     * Returns the single instance of AlarmSystem, creating it if necessary.
     *
     * @return the AlarmSystem singleton
     */
    public static AlarmSystem getInstance() {
        if (instance == null) {
            instance = new AlarmSystem();
        }
        return instance;
    }

    /**
     * Registers an object to receive alarm events.
     * Has no effect if the listener is already registered.
     *
     * @param listener the object to register
     */
    public void registerListener(AlarmListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener from the alarm system.
     * Called when an actor is removed from the map (e.g. Undead killed).
     *
     * @param listener the object to deregister
     */
    public void deregisterListener(AlarmListener listener) {
        listeners.remove(listener);
    }

    private static final int ALARM_DURATION = 20;
    private int alarmTurnsRemaining = 0;

    public void tick() {
        if (alarmActive) {
            for (AlarmListener listener : new ArrayList<>(listeners)) {
                listener.onAlarmTick();
            }
            alarmTurnsRemaining--;
            if (alarmTurnsRemaining <= 0) {
                deactivate();
            }
        }
    }

    public void trigger() {
        if (!alarmActive) {
            alarmActive = true;
            alarmTurnsRemaining = ALARM_DURATION;
            for (AlarmListener listener : listeners) {
                listener.onAlarmTriggered();
            }
        }
    }

    /**
     * Deactivates the alarm and notifies all listeners to revert their state.
     */
    public void deactivate() {
        if (alarmActive) {
            alarmActive = false;
            for (AlarmListener listener : listeners) {
                listener.onAlarmDeactivated();
            }
        }
    }
}