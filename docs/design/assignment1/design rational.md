# Design Rationale

---

## REQ1

### Summary

This requirement establishes the foundational game setup: five contracted workers, each with a weight-limited inventory, a flask, and access to three shared items (AccessCard, FirstAidKit, SterilisationBox) placed aboard the armoured ship.
The key design goals are low coupling between actor and item logic, correct encapsulation of item state, and reuse of engine-provided abstractions.

### Inventory Design

Each worker is given their own WeightLimitedInventory instance with a 50-unit cap, rather than sharing a single BasicInventory.
The original base code passed the same BasicInventory object to all five workers, meaning all workers shared one item list — a clear design fault where object identity was confused with logical sharing. Giving each worker their own WeightLimitedInventory correctly models independent ownership. 
An alternative would have been a single shared inventory with reference semantics, but this contradicts the requirement that weight limits apply per worker.
WeightLimitedInventory enforces weight checking in add() and correctly updates the tracked weight in remove(). 
Items without a WEIGHT statistic throw IllegalArgumentException, ensuring the contract is explicit rather than silently ignored.

### Item-Action Decoupling via allowableActions()
The original ContractedWorker.playTurn() manually scanned the inventory for instanceof AccessCard, instanceof Flask, and hand-added their actions.
This is a violation of the Single Responsibility Principle (SRP) — ContractedWorker was responsible both for actor behaviour and for knowing the internal logic of every item type.
Any new item would require modifying ContractedWorker, violating the Open-Closed Principle (OCP).
The refactored design moves action generation into each item's allowableActions(Actor, GameMap) method, which the engine's World.prepareAllowableActions() already calls automatically for every carried item.
ContractedWorker.playTurn() is now reduced to three responsibilities: handle unconsciousness, handle multi-turn actions, and show the menu. Adding a new item with new actions requires zero changes to ContractedWorker.
A trade-off is that item logic is now distributed across many classes rather than centralized in one place. 
However, this is preferable because each class has a single, clearly defined responsibility (SRP), and the system is more maintainable — finding the logic for Flask means looking in Flask, not in ContractedWorker.

### Item Actions — Initial Approach and Its Limitation
In the initial REQ1 design, each consumable item had its own dedicated action class: Flask used ConsumeFlaskAction, FirstAidKit used UseFirstAidKitAction, and AccessCard used UnlockDoorAction. While this correctly moved action logic out of ContractedWorker, it introduced a new problem — every new consumable item required a new action class, even when the action structure was identical (find item, call a method on it, return a result string). This is a DRY violation at the action level: the pattern was repeated for every item rather than written once.
The problem became apparent when REQ2 was planned and Apple and Cookies were introduced. Under the one-action-per-item approach, two more action classes (EatAppleAction, EatCookieAction) would have been needed, each following the exact same structural pattern. This signalled that the action itself was not the varying part — the item's effect was. The action was boilerplate. This realisation led to the Consumable interface being introduced in REQ2 as a better solution (see REQ2 — Consumable Interface).
Similarly, UnlockDoorAction was replaced with a generic UnlockAction backed by the Unlockable interface, so AccessCard depends on Unlockable rather than on Door directly (Dependency Inversion Principle, DIP). Any future lockable object simply implements Unlockable — AccessCard picks it up automatically with no modification.

### Flask Drop Prevention

The requirement states the flask must remain in the worker's inventory once depleted. The `getDropAction()` method is overridden in `Flask` to return `null`, preventing the engine from offering a drop option. This is the minimal, targeted fix — rather than adding conditional logic elsewhere, overriding one method on `Flask` cleanly enforces the constraint at its source.

### Limitations

WeightLimitedInventory throws IllegalArgumentException if an item has no weight statistic. This means all items in the game must define a weight — a coupling that could become restrictive if engine-level items (e.g., quest items without weight) are introduced later. A safer alternative would be to treat missing weight as zero rather than throwing, but this risks silently allowing overweight inventories.

---

## REQ2

### Summary

This requirement introduces five new items (Apple, Cookies, Lantern, FloppyDisk, CRTMonitor) and one ground interaction (Puddle drinking). The key design goals are reuse of the Consumable interface,
correct modelling of status effects via the engine's Status interface, and keeping item-specific logic inside each item class rather than in actor classes.

### Consumable Interface
During REQ1, it became clear that the one-action-per-item approach was producing boilerplate action classes with no structural variation.
ConsumeFlaskAction and UseFirstAidKitAction were structurally identical — both delegated to a method on the item, both returned a result string. 
When REQ2 required Apple and Cookies, creating EatAppleAction and EatCookieAction would have continued this pattern unnecessarily.
The Consumable interface was introduced as a direct response to this design smell. By defining a single consume(Actor, GameMap) method on the interface,
the varying part (the effect) moves into each item, while the fixed part (the action structure) becomes one reusable ConsumeAction.
This follows DRY — the action pattern is written once, regardless of how many consumable items exist. Flask and FirstAidKit were also refactored to implement Consumable, replacing their dedicated action classes.
An alternative would have been an abstract ConsumableItem class. However, Puddle (a Ground subclass) also needs to be consumable — it offers a drink action when a worker stands on it. 
Since Java does not support multiple inheritance, an abstract class would exclude Ground subclasses from implementing the consumable behaviour. 
Using an interface allows both Item and Ground subclasses to implement Consumable without structural conflict (ISP). 
This was not anticipated during REQ1 but became clear once REQ2 introduced the puddle interaction, validating the choice of an interface over an abstract class.
The same ConsumeAction now handles Flask, FirstAidKit, Apple, Cookies, and Puddle without modification, satisfying OCP — new consumables are added by implementing the interface , not by changing the action.

### Status Effects — PoisonStatus and BurnStatus

Rather than applying damage directly in `consume()` methods (e.g., `actor.hurt(1)` five times), poison and burn are modelled as `Status` implementations using the engine's existing `Status` interface and `tickStatuses()` mechanism. This correctly separates **when** damage happens (each turn) from **how** it is applied (via `hurt()`).

`PoisonStatus` is parameterized with a turn count, making it reusable for apple poison (5 turns), puddle poison (3 turns), and any future source. An alternative would have been separate `ApplePoisonStatus` and `PuddlePoisonStatus` classes, but this violates DRY — the only difference is the duration, which is cleanly handled by a constructor parameter.

`BurnStatus` is a separate class from `PoisonStatus` despite having the same structure, because burn and poison are semantically distinct status types. Future requirements might add burn resistance or poison immunity — keeping them separate avoids conflating two different effects.

### Fire Ground and Lantern

The `Lantern` item uses `tick(Location, Actor)`, which is called only while the item is carried. This correctly ensures the 5% leak chance only applies during active carrying — leaving the lantern on the ground stops the risk, which matches the intended game mechanic.

When a leak occurs, the current ground tile is replaced with a `Fire` instance via `location.setGround(new Fire())`. `Fire` ticks down its own 5-turn lifetime and restores the tile to `Dirt` when expired. This models fire as a ground state rather than a status on the location, which is semantically correct — fire is a property of the tile, not of any entity standing on it. Actors receive `BurnStatus` when they stand on fire during `Fire.tick()`.

A limitation is that replacing the ground with `Fire` destroys any existing ground type information. If the lantern ignites a `Puddle`, the puddle is lost. A more sophisticated design might stack ground effects, but this adds significant complexity for a feature not required by the specification (YAGNI).

### Cookies — Depleted Item Removal

`Cookies` tracks remaining uses and removes itself from the actor's inventory once all five are consumed. This mirrors `Flask`'s non-removal behaviour (flask stays in inventory when depleted) but applies the opposite rule. Both items own their depletion logic inside `consume()`, keeping the decision close to the data it depends on (high cohesion).

---

## REQ3

### Summary

This requirement introduces two new creature types (`Undead`, `Slime`) and a spawn mechanism (`Hole`). The key design goals are **reusable behaviour classes**, **correct application of the `Behaviour<T,R>` functional interface**, and **reuse of the `Consumable` interface for Slime's eating mechanic**.

### Behaviour Design — Named Classes vs. Lambdas

The engine provides `Behaviour<T,R>` as a functional interface (single abstract method `operate()`), meaning it can be implemented via lambda expressions. The decision of whether to use a named class or a lambda depends on reuse:

- `WanderBehaviour` is a **named class** because both `Undead` and `Slime` use identical wander logic. Inlining the same lambda in both classes would duplicate code (DRY violation). A named class defines the logic once and both actors reference it.
- `AttackBehaviour` is a **named class** parameterised by `targetType` (`Class<? extends Actor>`). This makes it reusable for any future hostile actor regardless of what actor type they target. `Undead` uses `new AttackBehaviour(ContractedWorker.class)`. A future enemy targeting any actor uses `new AttackBehaviour(Actor.class)`.
- Slime's eat-from-ground logic is **inlined as an anonymous `Action`** inside `playTurn`, because only `Slime` needs this behaviour. Creating a separate `EatFromGroundBehaviour` class for logic used by exactly one actor violates YAGNI — it adds a class with no reuse benefit.

An alternative would have been to use a single `BehaviourList` prioritised queue inside each actor. This was rejected because the priority is simple (attack > move) and visible directly in `playTurn`'s if/else chain, making the code easier to read without the indirection of a separate structure.

### Slime and Consumable Reuse

`Slime` consumes `Consumable` items directly from the ground. Rather than creating a new item-eating interface, the existing `Consumable` interface is reused — the same `consume(Actor, GameMap)` method that workers call is called by `Slime`. This means poison from a rotten `Apple` affects `Slime` exactly as it affects a worker, with no duplication of effect logic. This demonstrates a key benefit of the `Consumable` design from REQ2: the interface is actor-agnostic, so any `Actor` can call `consume()`.

The eat action is inlined as an anonymous `Action` that calls `consumable.consume(actor, map)` and then removes the item from the ground. This avoids creating a `ConsumeFromGroundAction` class for logic that is unique to `Slime`. The trade-off is reduced readability compared to a named class, but the inline code is short and self-documenting via comments.

### Hole Spawning

`Hole` uses `Ground.tick(Location)`, called each turn by the engine, to count turns and spawn creatures every 20 turns. This is the correct hook because ground ticks before actors take their turns, ensuring spawned creatures participate in the same game turn they appear. A 50/50 random selection between `Undead` and `Slime` is implemented using `Random.nextBoolean()`.

Spawning is skipped if the tile is already occupied (`location.containsAnActor()`), preventing two actors from occupying the same tile — a game rule enforced by the engine. The `GameEngineException` thrown by `location.addActor()` is caught silently, as a failed spawn on a busy tile is a non-critical event.

### Undead Intrinsic Weapon

`IntrinsicWeapon` is abstract — it must be subclassed. Since only `Undead` uses a fist, an anonymous subclass `new IntrinsicWeapon(1, "punches", 10, "Fist") {}` is used rather than creating a named `Fist` class. The engine's own Javadoc notes that making a natural weapon an `Item` is semantically incorrect — `IntrinsicWeapon` is the appropriate abstraction. If a second creature needed the same fist stats, a named class would be warranted (DRY), but for a single user the anonymous subclass minimises unnecessary class proliferation (YAGNI).

---

## REQ4

### Summary

This requirement introduces an alarm system that can be triggered by environmental hazards and produces two consequences: Undead switch from wandering to actively chasing workers, and all doors are locked for 10 turns. The key design goal is **zero modification to existing classes when adding alarm consequences**, achieved through the Observer pattern.

### Observer Pattern — AlarmSystem and AlarmListener

`AlarmSystem` acts as the subject in the Observer pattern. `AlarmListener` is the observer interface with three methods: `onAlarmTriggered()`, `onAlarmTick()`, and `onAlarmDeactivated()`. `Undead` and `Door` implement `AlarmListener` and register themselves at construction.

The critical benefit is that adding a new alarm consequence (e.g., spawning drones, sealing vents) requires only implementing `AlarmListener` on a new or existing class — `AlarmSystem` and `AlarmTile` are never modified (OCP). If `AlarmSystem` called `undead.switchToChaseBehaviour()` and `door.lock()` directly, adding a third consequence would require modifying `AlarmSystem` — a violation of both OCP and DIP.

`AlarmSystem` is implemented as a singleton because it must be globally accessible to any trigger (`AlarmTile`) and any listener (`Undead`, `Door`) without a shared reference being passed through every constructor. The alternative — passing `AlarmSystem` as a constructor parameter to every class that needs it — would introduce pervasive coupling and make the constructor signatures of `Door` and `Undead` aware of the alarm system at the infrastructure level.

A limitation of the singleton approach is reduced testability — unit tests cannot easily substitute a mock `AlarmSystem`. However, for a game codebase at this scale, the pragmatic benefit of global accessibility outweighs the testing cost.

### AlarmListener Interface Design

`AlarmListener` is kept intentionally minimal — three methods with two default implementations. Classes only override what they need: `Undead` overrides `onAlarmTriggered()` and `onAlarmDeactivated()` (to switch movement behaviour); `Door` overrides all three (to enforce lockdown and count down the timer). Default implementations in the interface mean classes are not forced to implement methods irrelevant to them (ISP).

An alternative design was to use separate `TriggerListener`, `TickListener`, and `DeactivateListener` interfaces. This would be more ISP-pure but would require `AlarmSystem` to maintain three separate listener lists, and classes implementing multiple concerns would need to implement multiple interfaces. Given that `onAlarmTick()` and `onAlarmDeactivated()` have sensible defaults, the single interface with defaults is the pragmatic choice.

### Alarm Trigger — AlarmTile

Three trigger mechanisms were considered: a new creature (security camera), a tripwire item, and a pressure-sensitive tile. `AlarmTile` (a floor tile) was chosen because it integrates most naturally with the existing `Ground.tick(Location)` mechanism — the engine already calls this every turn. No new engine hooks or actor logic are needed.

`AlarmTile` uses `Ground.allowableActions()` rather than `tick()` for detection, because `allowableActions()` is called during the actor's own turn, giving immediate triggering (no one-turn delay). The `triggered` flag prevents the alarm from being re-triggered on subsequent turns. The tile appears identical to a normal floor tile (`_`), which is intentional — the gameplay tension comes from not knowing which tiles are alarmed.

### Undead Chase Behaviour — ChaseBehaviour

When the alarm triggers, `Undead` replaces its `movementBehaviour` field from `WanderBehaviour` to `ChaseBehaviour`. This is the Strategy pattern — the movement algorithm is encapsulated behind the `Behaviour<Actor, Action>` interface and swapped at runtime without changing `playTurn`'s structure.

`ChaseBehaviour` uses Breadth-First Search (BFS) to find the shortest path to the nearest target. BFS guarantees the shortest path in an unweighted graph (the tile grid), which ensures Undead always takes the most direct route. An alternative would have been a greedy approach (always move toward the lowest Manhattan distance), which is simpler but can get stuck around walls. BFS is more robust at the cost of higher computational overhead per turn. Given the map size and number of Undead, this is acceptable.

### Door Lockdown

When the alarm triggers, `Door.onAlarmTriggered()` sets `isUnlocked = false` and `alarmLockRemaining = 10`. Each turn `onAlarmTick()` decrements the counter. The `unlock()` method checks `alarmLockRemaining > 0` and rejects unlock attempts during lockdown, returning a message explaining the remaining turns. After 10 turns, doors can be reopened normally.

This design keeps all door-state logic inside `Door`, consistent with SRP. `AlarmSystem` knows nothing about how `Door` enforces the lockdown — it only calls `onAlarmTick()`. An alternative would have been a global `doorsLocked` flag in `AlarmSystem`, but this would mean `AlarmSystem` has knowledge of door state — a dependency that increases coupling and reduces cohesion.

### Limitations

The alarm currently never fully deactivates in the base implementation (the 10-turn door lockdown expires, but `Undead` keeps chasing indefinitely). A complete design would add a turn counter to `AlarmSystem.tick()` that calls `deactivate()` after N turns, reverting all listeners. The `onAlarmDeactivated()` method exists on `AlarmListener` in anticipation of this, meaning deactivation can be added by wiring one method call in `AlarmSystem` — no listener changes required.