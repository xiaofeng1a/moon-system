# Design Rationale — Eclipse Nebula Inc.

---

## REQ1

### Summary

REQ1 sets up the basic game: five workers, each with their own weight-limited inventory and a flask, plus three shared items (AccessCard, FirstAidKit, SterilisationBox) on the armoured ship. My main goals were to keep actor and item logic separate, correctly protect item state, and reuse what the engine already provides.

### Inventory Design

The original code gave all five workers the same `BasicInventory` object, meaning they all shared one item list — every worker could access each other's items. I fixed this by giving each worker their own `WeightLimitedInventory` with a 50-unit cap so ownership is independent.

I considered keeping one shared inventory and using logic to separate ownership, but I rejected this because the requirement clearly states weight limits apply per worker, and sharing one list makes ownership confusing and hard to maintain.

`WeightLimitedInventory` checks the total weight before adding an item. If adding the item would exceed the limit, the pick-up fails and tells the player why. If an item has no weight statistic at all, I throw `IllegalArgumentException` to make the error obvious rather than silently letting overweight items through.

One limitation I acknowledge is that every item must define a weight for `WeightLimitedInventory` to work. If a future engine item has no weight statistic, it would break. A safer option would be to treat missing weight as zero, but I avoided this because it risks silently allowing overweight inventories.

### Item-Action Decoupling via `allowableActions()`

The original `ContractedWorker.playTurn()` manually checked `instanceof AccessCard` and `instanceof Flask` to decide which actions to offer. I identified this as a violation of SRP — `ContractedWorker` was doing two jobs: playing its turn and knowing the internal details of every item type. Adding any new item required editing `ContractedWorker`, which also violates OCP.

I fixed this by moving action generation into each item's `allowableActions(Actor, GameMap)` method. The engine already calls this automatically for every carried item in `World.prepareAllowableActions()`, so I did not need to change the engine at all. `ContractedWorker.playTurn()` now only handles three things: check if unconscious, handle multi-turn actions, and show the menu. New items add their own actions with no changes to `ContractedWorker`.

The trade-off is that item logic is now spread across many classes. However, I believe this is better because each class has one clear job (SRP) — if something is wrong with how `Flask` works, I look in `Flask`, not in `ContractedWorker`.

### Item Actions — Initial Approach and Its Limitation

In my initial REQ1 design, I gave each item its own action class: `Flask` used `ConsumeFlaskAction`, `FirstAidKit` used `UseFirstAidKitAction`, and `AccessCard` used `UnlockDoorAction`. This correctly moved logic out of `ContractedWorker`, but I noticed a new problem — every item needed its own action class even though the structure was always the same: get the item, call a method, return a result string. This is a DRY violation.

When I started planning REQ2 and adding `Apple` and `Cookies`, I would have needed two more action classes (`EatAppleAction`, `EatCookieAction`) following the exact same pattern. This made me realise the action itself never changed — only the item's effect changed. The action was just repeated boilerplate. This led me to introduce the `Consumable` interface in REQ2 to solve the problem (see REQ2).

Similarly, I replaced `UnlockDoorAction` with a generic `UnlockAction` backed by the `Unlockable` interface. Now `AccessCard` checks for `Unlockable` on adjacent tiles instead of checking specifically for `Door`. Any future lockable object just needs to implement `Unlockable` and `AccessCard` handles it automatically with no code changes (DIP).

### Flask Drop Prevention

The flask must stay in the worker's inventory even after it runs out. I fixed this by overriding `getDropAction()` in `Flask` to return `null`, which stops the engine from adding a drop option to the menu. I enforced this constraint directly in `Flask` rather than adding checks elsewhere to keep the change minimal and targeted.

### Limitations

`WeightLimitedInventory` throws an exception if an item has no weight statistic. This couples all items to needing a weight, which could be a problem if future engine items without weight are introduced.

---

## REQ2

### Summary

REQ2 adds five new items (Apple, Cookies, Lantern, FloppyDisk, CRTMonitor) and one ground interaction (drinking from Puddles). My main goals were to fix the DRY problem I found in REQ1 using a `Consumable` interface, model damage-over-time using the engine's `Status` interface, and keep item logic inside each item class.

### Consumable Interface — Introduced in REQ2

During REQ1, I noticed that `ConsumeFlaskAction` and `UseFirstAidKitAction` had exactly the same structure — both called a method on the item and returned a string. When REQ2 required `Apple` and `Cookies`, creating `EatAppleAction` and `EatCookieAction` would have repeated the same structure again. I identified this as a DRY violation — the action was just boilerplate and the only thing that varied was the item's effect.

I introduced the `Consumable` interface to fix this. It defines one method: `consume(Actor, GameMap)`. Each item puts its own effect there. One shared `ConsumeAction` calls `consumable.consume()` and that is it — the action pattern is written once no matter how many consumable items I add (DRY). I also refactored `Flask` and `FirstAidKit` to implement `Consumable`, removing their old dedicated action classes.

I considered using an abstract `ConsumableItem` class instead. However, I realised that `Puddle` — a `Ground` subclass — also needs to be consumable because workers can drink from it when standing on it. Since Java does not allow a class to extend two classes at once, an abstract class would not work here. An interface has no such restriction, so I can have both `Item` and `Ground` subclasses implement `Consumable` without any conflict (ISP). I did not foresee this in REQ1, but it became clear in REQ2 and confirmed that an interface was the right choice.

The same `ConsumeAction` now covers `Flask`, `FirstAidKit`, `Apple`, `Cookies`, and `Puddle` with no changes — I just implement the interface for new consumables (OCP).

### Status Effects — PoisonStatus and BurnStatus

Instead of calling `actor.hurt(1)` multiple times inside `consume()`, I modelled poison and burn as `Status` implementations using the engine's `Status` interface and `tickStatuses()` method. This separates when damage happens (each turn, handled by the engine) from how it is applied (`hurt()` inside the status tick). I believe each concern should be handled in the right place.

I made `PoisonStatus` take a turn count as a parameter so I can reuse it: apple poison lasts 5 turns and puddle poison lasts 3 turns. Having separate `ApplePoisonStatus` and `PuddlePoisonStatus` classes would just repeat the same code with a different number, which violates DRY.

I kept `BurnStatus` as a separate class from `PoisonStatus` even though they look similar. I did this because burn and poison are different things — a future update might add burn resistance or poison immunity, and keeping them as separate types makes that easy to add without confusion.

### Fire Ground and Lantern

I used `tick(Location, Actor)` in `Lantern`, which only runs while the item is being carried. This means the 5% leak chance only applies when someone is holding the lantern — putting it on the ground stops the risk, which matches the intended mechanic.

When a leak happens, I replace the ground tile with `Fire` using `location.setGround(new Fire())`. `Fire` counts down its own 5-turn timer and replaces itself with `Dirt` when it expires. I chose to model fire as a ground type rather than a status effect on the location because fire is a property of the tile itself. Any actor standing on the fire tile during `Fire.tick()` gets a `BurnStatus`.

One limitation I identified is that replacing the ground with `Fire` removes whatever was there before. If the lantern ignites a `Puddle`, the puddle is permanently gone. A stacking ground effect system would fix this, but I decided not to implement it since the requirement does not cover this scenario (YAGNI).

### Cookies — Depleted Item Removal

I made `Cookies` remove itself from the inventory after all five are eaten. This is the opposite of `Flask`, which stays in the inventory when empty. I put the depletion logic inside `consume()` for both items so the decision is made where the data lives (high cohesion).

---

## REQ3

### Summary

REQ3 introduces two creatures (`Undead`, `Slime`) and a spawn tile (`Hole`). My main goals were to use shared behaviour classes where it makes sense, use the `Behaviour<T,R>` interface correctly, and reuse the `Consumable` interface for Slime's eating mechanic.

### Behaviour Design — Named Classes vs. Inlined Logic

The engine's `Behaviour<T,R>` interface has one method (`operate()`), so I can implement it with a lambda. My rule for deciding whether to make a named class or inline the logic is based on reuse:

- I made **`WanderBehaviour`** a named class because both `Undead` and `Slime` wander the same way. Copying the same lambda into two classes would be a DRY violation. One named class means I write the logic once and both actors use it.
- I made **`AttackBehaviour`** a named class with a `targetType` parameter. `Undead` uses `new AttackBehaviour(ContractedWorker.class)` to only target workers. A future enemy that attacks any actor would use `new AttackBehaviour(Actor.class)`. The parameter means I do not need a separate class for each enemy's target type.
- I inlined **Slime's eat-from-ground logic** as an anonymous `Action` inside `playTurn` because only `Slime` needs this. Making a separate class for logic used by one actor adds unnecessary code (YAGNI).

I considered using a priority queue of behaviours instead of an if/else chain. I rejected this because the priority is simple — attack first, then move — and the if/else chain in `playTurn` already makes this clear without adding extra structures.

### Slime and Consumable Reuse

I made `Slime` eat `Consumable` items directly from the ground by reusing the existing `Consumable` interface from REQ2. The same `consume(Actor, GameMap)` method that workers call is also called by `Slime`. This means a rotten `Apple` poisons `Slime` the same way it would a worker, with no duplicate logic. I designed the interface to be actor-agnostic so any `Actor` can call it.

I inlined the eat logic as an anonymous `Action` inside `playTurn` that calls `consumable.consume()` and removes the item from the ground. I chose this because only `Slime` needs this behaviour, so a named class would add overhead with no benefit (YAGNI). The trade-off is slightly less readability, but the code is short enough to follow with comments.

### Slime Auto-Decision

I made `Slime` extend `Actor` as required by the engine for map placement and turn processing. The key difference from a player-controlled actor is that my `playTurn()` never shows a menu — it picks an action in code and returns it directly. This makes `Slime` fully autonomous without needing any extra interface or class.

### Hole Spawning

I used `Ground.tick(Location)` in `Hole`, which the engine calls every turn, to count turns and spawn a creature every 20 turns. I chose this hook because ground ticks run before actors take their turns, so newly spawned creatures can act in the same turn they appear.

If the tile is already occupied, I skip the spawn — the engine does not allow two actors on the same tile. I catch the `GameEngineException` from `location.addActor()` silently because a missed spawn due to a busy tile is not an error worth stopping the game for.

I used `Random.nextBoolean()` for a 50/50 random choice between `Undead` and `Slime`.

### Undead Intrinsic Weapon

`IntrinsicWeapon` is abstract and needs a subclass. Since only `Undead` uses a fist, I used an anonymous subclass inline: `new IntrinsicWeapon(1, "punches", 10, "Fist") {}`. The engine's own comments say making a natural weapon an `Item` is wrong — `IntrinsicWeapon` is the right abstraction. If a second creature needed the same fist, I would create a named class (DRY). For one user, the anonymous subclass keeps things clean without adding an extra file (YAGNI).

---

## REQ4 — The Facility Alarm System

### Summary

REQ4 adds an alarm system with one trigger (a hidden floor tile) and two consequences: `Undead` switch from wandering to chasing workers, and all doors lock for 10 turns. My main goal was that adding a new alarm consequence should not require changing any existing class. I achieved this using the Observer pattern.

### Observer Pattern — AlarmSystem and AlarmListener

I designed `AlarmSystem` as the subject — the class that manages the alarm state. `AlarmListener` is the observer interface with three methods: `onAlarmTriggered()`, `onAlarmTick()`, and `onAlarmDeactivated()`. I made `Undead` and `Door` implement this interface and register themselves when they are created.

The key benefit of this design is that adding a new alarm consequence only requires making a new class that implements `AlarmListener` — I never need to change `AlarmSystem` or `AlarmTile` (OCP). If I had made `AlarmSystem` directly call methods on `Undead` and `Door`, adding a third consequence would mean editing `AlarmSystem`, which violates both OCP and DIP.

I implemented `AlarmSystem` as a singleton so any class anywhere in the game can trigger or react to the alarm without needing a reference passed through constructors. The alternative — passing `AlarmSystem` as a parameter to `Door`, `Undead`, and `AlarmTile` — would add it to every constructor in the game and increase coupling unnecessarily.

The trade-off I accept is that singletons are harder to test since I cannot easily replace them with a mock. For a game at this scale I think this is acceptable, but in a larger system I would use dependency injection instead.

### AlarmListener Interface Design
 
I kept `AlarmListener` minimal — three methods with two default empty implementations. Classes only override what they need: I made `Undead` override `onAlarmTriggered()` and `onAlarmDeactivated()` to switch movement mode, and `Door` override all three to manage the lockdown countdown. The defaults mean classes are not forced to implement methods they do not need (ISP).

I considered using three separate interfaces: `TriggerListener`, `TickListener`, and `DeactivateListener`. This would be more strictly ISP-compliant, but it would mean `AlarmSystem` needs three separate listener lists and classes responding to multiple events would have to implement multiple interfaces. Since two of the three methods have sensible empty defaults anyway, I chose one interface with defaults as the simpler and more practical option.

### Alarm Trigger — AlarmTile

I considered three trigger options: a new creature like a security camera, a tripwire item, and a hidden floor tile. I chose `AlarmTile` because it fits naturally into the existing `Ground` class — the engine already calls `allowableActions()` on every ground tile every turn and I did not need to add any extra hooks.

I used `allowableActions()` rather than `tick()` for detection because `allowableActions()` is called during the actor's own turn, so the alarm fires immediately when a worker steps on the tile with no one-turn delay. I added a `triggered` flag to stop the alarm firing more than once. I made the tile look identical to a normal floor tile intentionally — workers cannot tell the difference until it is too late, which creates gameplay tension.

### Undead Chase Behaviour — ChaseBehaviour

When the alarm fires, I swap `Undead` movement from `WanderBehaviour` to `ChaseBehaviour`. This is the Strategy pattern — the movement algorithm sits behind the `Behaviour<Actor, Action>` interface and is swapped at runtime without me having to touch `playTurn`. Adding a third movement mode in the future just means swapping the field again.

I used Breadth-First Search (BFS) in `ChaseBehaviour` to find the shortest path to the nearest worker. BFS always finds the shortest path on a grid, so `Undead` always takes the most direct route. I considered a simpler greedy approach — always move toward the lowest distance — but rejected it because it gets stuck around walls. BFS is more reliable, and the extra computation per turn is acceptable given the map size.

### Door Lockdown

When the alarm fires, I set `isUnlocked = false` and start a 10-turn countdown in `Door.onAlarmTriggered()`. Each turn `onAlarmTick()` decrements the counter. I made the `unlock()` method block unlock attempts while the counter is above zero and tell the player how many turns remain. After 10 turns, doors can be reopened normally.

I kept all the door lockdown logic inside `Door` (SRP). `AlarmSystem` never knows how `Door` enforces the lockdown — it only calls `onAlarmTick()`. I considered using a global `doorsLocked` flag inside `AlarmSystem`, but this would give `AlarmSystem` knowledge of door state, which increases coupling and reduces cohesion.

### Limitations

The alarm turn off every 20 turn of the game, it should be a problem to the worker to survive in the facility.Also, worker can't turn it off manually, like the future can add a item if the worker picked it up then can close the alarm.