# Refactoring plan

Goals (from the user):
1. State machines get proper state enums and consistent management (single owner, explicit
   transitions, one reset path, no scattered flag juggling).
2. Event handling: one handler owner per module instead of everything bunched in `BetterPit.java`.
3. Complex/unclear code extracted into shared utilities (fused across modules where it is shared).
4. Minimal comments: only where intent is not self-evident.

Game behavior (packet sequences, slot ids, click modes, delays, triggers, messages) is preserved
except where a state-machine bug is fixed (listed in "Behavior changes" below).

## Target structure

```
BetterPit.java            entry point only: commands, managers, module registration,
                          sendMessage/isInPit/getMapName helpers
modules/Enemies.java      tick handler + enemy detection (static set read by EnemiesHud)
modules/DarkPants.java    tick handler + dark detection (hasDarks shared with swap module)
modules/Bounties.java     tick handler + bounty detection
modules/Denicker.java     tick handler + nick detection; resolution uses PitApi + CacheManager
modules/Automation/AutoPod.java          tick-driven state machine + death/respawn/disconnect handlers
modules/Automation/DiamondPantSwap.java  tick-driven state machine + trigger
modules/Automation/RightClickSwap.java   mouse trigger + tick-driven state machine
modules/Automation/AutoGhead.java        tick-driven state machine + trigger
modules/Automation/AutoBulletTime.java   mouse press/release driven
modules/Automation/AutoQuickMath.java    chat event driven
util/Lobby.java           null-safe access to lobby players (infos + entities), tick gating
util/InventoryUtil.java   player inventory GUI open/close/click + slot scanning (shared by
                          AutoPod, DiamondPantSwap, RightClickSwap, AutoGhead-style scans)
util/InputBlocker.java    static `blocked` flag + the four input-cancellation handlers
util/MathSolver.java      expression evaluator extracted from AutoQuickMath
util/PitApi.java          HTTP/API layer extracted from Denicker (pitmart, pitpanda, mojang)
```

## Key decisions

- **Registration**: Forge 1.8.9 `EventBus.register` only supports instance registration
  (verified against the 1.8.9 EventBus source: it calls `target.getClass()` and has no
  Class/static branch). Every module registers an instance whose instance methods carry
  `@SubscribeEvent`.
- **Shared state stays static** in detection modules (`lastEnemySet`, `lastDarkSet`,
  `lastNickedSet`, `lastBountiedSet`) because the OneConfig HUDs read it statically.
- **Tick gating**: the old central tick applied `thePlayer/theWorld != null` and the
  `whileInPit` gate to everything. `Lobby.isReady()` / `Lobby.shouldRun()` carry that gate
  into each module's own tick handler, so per-module behavior is unchanged.
- **State machines**: each gets a private state enum with names describing the intent, a
  `transition(next)` helper (sets state + resets tickDelay), one `reset()` that clears every
  field, and explicit `start()`/`rearm()` entry points. Each machine runs once per game tick
  (`TickEvent.Phase.START`).
- **`/spawn` rearm, config button, and `/autopod`** all go through `AutoPod.rearm()` instead
  of touching `alreadyDidPod` from the outside; `alreadyDidPod` and the state enum become
  private to the module.

## Behavior changes (intentional fixes)

1. `DiamondPantSwap`: when `swapBack` is enabled, the old `CLOSE_INV` state never left the
   state machine (it only reset when `!swapBack`), so the module re-sent
   `C0DPacketCloseWindow` + closed the screen every tick forever. Closing now always returns
   to `IDLE`.
2. `AutoPod`: the old central tick sent a chat message *every tick* while `thePlayer.isDead`
   (≈20 msgs/s spam). Death/respawn events now own the reset, the per-tick `isDead` poll is
   removed.
3. Tick phase: `AutoPod` and `AutoGhead` previously advanced on both tick phases (START and
   END, because the central tick did not filter). All machines now run on `Phase.START` only.
   Per-state delays are therefore up to ~2x longer in real ticks (all delays are 1-2 ticks,
   i.e. 50-200 ms); click sequences and their ordering are unchanged.
4. `Denicker`/`Enemies`/`DarkPants`/`Bounties` detection now runs from their own tick
   handlers instead of the central one; iteration source is the same data
   (`getPlayerInfoMap()` / `world.playerEntities`).
5. `PlayerLocation.getPlayerDistance` no longer NPEs when the named player entity is not
   (currently) in the world (HUDs iterate name sets that can briefly be stale).
6. `Denick` command: fixed copy-pasted usage string (`/getdisplayname player` → `/denick <player>`).
7. Removed template leftover mixin `MixinGuiMainMenu` (only printed "Hello from Main Menu!").
8. Removed dead code: empty `NetworkMixin.onChannelRead` + unused `@Shadow`, unused
   `BetterPit.connected`/`onConnect`, redundant double `currentScreen` checks.

## Explicit non-goals

- No changes to gameplay slot ids / windowClick parameters (kept byte-for-byte).
- No renaming of mod id / names / versions (drift between `fentpit` and `betterpit` is
  out of scope for this pass).
- No new tests, no CI changes.

## Verification

- `./gradlew :1.8.9-forge:compileJava` after each step (baseline compile confirmed before
  starting).
- Full `./gradlew build` at the end.
