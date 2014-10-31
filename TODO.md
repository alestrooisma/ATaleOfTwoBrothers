Phase 1 - tRPG gameplay
------------------
- [ ] Melee combat
  - [x] Charging
  - [x] Locked in combat
  - [x] Resolve combat
  - [x] Resolve all combat each turn
  - [x] No ranged weapons in melee
  - [ ] Consolidation
- [x] Load model information of map from file
- [x] Pathfinding
  - [x] Correct determination of reachable tiles (BFS)
  - [x] Obtaining a shortest path to the destination (backtracking)
- [x] Camera control
- [x] Abilities
  - [x] Action selection
  - [x] Action execution
  - [x] Non-immediate action effects
- [ ] Better checking if actions are allowed
  - [x] Add history list
  - [ ] Clear expired items from list at end/start of turn (currently clearing everthing each turn)
  - [ ] Base permission checks on history list
    - [x] Movement
    - [x] Dashing
    - [ ] Charging
    - [ ] Abilities
- [x] Basic AI
- [x] Code cleanup

Phase 2 - graphics, animation and UI
------------------
- [ ] Add universal tweening engine
- [ ] Movement (range) indicators
- [ ] Grid
- [ ] Death animation
- [ ] Projectile animation
- [ ] Mouse cursor messages (dashing, charging, fire)
- [ ] Result messages on target position (damage, miss)
- [ ] Action choice menu
- [ ] Selection info
- [ ] Party window

Phase 3 - combat model
------------------
- [ ] Add full set of equipment and stats
- [ ] Proper combat model based on stats and equipment
  - [ ] Hit chance
  - [ ] Damage calculation
- [ ] Unique melee model
  - [ ] Choosing attack
  - [ ] Option to disengage
  - [ ] Bonus/malus

Phase 4 - arena mode
------------------
*The arena mode is a game mode where a player encounters random battles of increasing difficulty while improving his party in the meanwhile.*
*This mode is designed such that many features can be developed before creating a real overworld.*
- [ ] Rethink order of Phase 2&3 parts with respect to Phase 4

Phase 5 - overworld
------------------
- [ ] central overworld class - member of model class

Phase 6 - content and polish
------------------
- [ ] Original art
- [ ] Many items
- [ ] Many events
