Phase 1 - tRPG gameplay
------------------
- [x] Melee combat
  - [x] Charging
  - [x] Locked in combat
  - [x] Resolve combat
  - [x] Resolve all combat each turn
  - [x] No ranged weapons in melee
- [x] Load model information of map from file
- [x] Pathfinding
  - [x] Correct determination of reachable tiles (BFS)
  - [x] Obtaining a shortest path to the destination (backtracking)
- [x] Camera control
- [x] Abilities
  - [x] Action selection
  - [x] Action execution
  - [x] Non-immediate action effects
- [x] Better checking if actions are allowed
  - [x] Add history list
  - [x] Clear expired items from list at end/start of turn
  - [x] Base permission checks on history list
    - [x] Movement
    - [x] Dashing
    - [ ] Charging
    - [ ] Firing
    - [ ] Abilities (delegate to ability)
- [x] Basic AI
- [x] Code cleanup

Phase 2 - graphics, animation and UI
------------------
- [ ] Add universal tweening engine
- [ ] Movement (range) indicators
- [ ] Toggleable grid
- [ ] Death animation (fade)
- [ ] Projectile animation
- [ ] Mouse cursor messages (dashing, charging, fire)
- [ ] Result messages on target position (damage, miss)
- [ ] HUD
- [ ] Evaluate the need for a message log

Phase 3 - combat model
------------------
- [ ] Add full set of equipment and stats
- [ ] Add more skills
  - Where needed improve checking if actions are allowed
- [ ] Proper combat model based on stats and equipment
  - [ ] Hit chance
  - [ ] Damage calculation
  - [ ] Consolidation after killing enemy
- [ ] Modifiers
- [ ] Unique melee model
  - [ ] Choosing attack
  - [ ] Option to disengage
  - [ ] Defensive modes to stop charging enemy

Phase 4 - arena mode
------------------
*The arena mode is a game mode where a player encounters random battles of increasing difficulty while improving his party in the meanwhile.*
*This mode is designed such that many features can be developed before creating a real overworld.*
- [ ] Aftermath screen
- [ ] Battle generation
- [ ] Loot
- [ ] Party management
  - [ ] Party & inventory window
  - [ ] Skill trees

Phase 5 - overworld
------------------
- [ ] central overworld class - member of model class
- [ ] Design world/create world generator
- [ ] Traveling between areas
- [ ] Generate encounters
- [ ] Interaction with NPCs at cities
- [ ] Quests

Phase 6 - content and polish
------------------
- [ ] Original art
- [ ] Many items
- [ ] Many events
- [ ] Achievements?
