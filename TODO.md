Bugs
------------------
- Can select ability even if it is "not allowed regardless of target".
- ArrayIndexOutOfBoundsException when charging toward a unit at the edge of the map. Can't reproduce...
- What is a unit allowed to do when his opponent dies after combat is resolved?


Phase 2 - graphics, animation and UI
------------------
- [x] Add universal tweening engine
- [x] Advanced event logging
- [x] Death animation (fade)
- [x] Movement animation (slide)
  - [ ] Follow actual path
- [ ] Movement path indicators
- [ ] Movement range indicators
- [ ] Toggleable grid
- [ ] Projectile animation
- [ ] Mouse cursor messages (dashing, charging, fire)
- [x] Result messages on target position (damage, miss)
- [ ] HUD
- [ ] Evaluate the need for a message log
- [ ] Prohibit user input during certain animations, e.g. AI has to complete turn, animation-wise, before player can act again
- [ ] Reset animation delay after certain user inputs, e.g. execute a move immediately, even if another unit is still moving

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