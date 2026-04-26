package game.alarm;

/**
 * Observer interface for the facility alarm system.
 * Any object that needs to react when the alarm is triggered implements this interface.
 *
 * <p>SOLID — Interface Segregation: this interface is intentionally minimal,
 * containing only what is needed to respond to alarm events. Objects are not
 * forced to implement methods they don't need.
 *
 * <p>SOLID — Open/Closed: new alarm consequences (e.g. spawning drones,
 * sealing vents) are added by creating new {@link AlarmListener} implementations,
 * without modifying AlarmSystem or any existing class.
 *
 * <p>Current implementors: Undead, Door
 */
public interface AlarmListener {

    /**
     * Called by AlarmSystem when the alarm is triggered.
     * Implementors define their own response (e.g. change behavior, lock doors).
     */
    void onAlarmTriggered();

    /**
     * Called by AlarmSystem each turn while the alarm is active.
     * Implementors can use this to count down timers or maintain alarm state.
     * Default implementation does nothing.
     */
    default void onAlarmTick() {}

    /**
     * Called by AlarmSystem when the alarm is deactivated.
     * Implementors should revert any changes made when the alarm was triggered.
     * Default implementation does nothing.
     */
    default void onAlarmDeactivated() {}
}