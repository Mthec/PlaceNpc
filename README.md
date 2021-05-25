# Place Npc Menu

A mod to centralise a menu named `Place Npc` for GM players (`power >= 2`) under which you can place individual menu entries rather than filling up the wand menu.

### Usage

Include the class and interface in your mod.  Then create a class that implements `NpcMenuEntry` with a `name` for the in-game menu entry and 
`boolean doAction(Action action, short num, Creature performer, Item source, VolaTile tile, int floorLevel);` to handle the actual spawning/followup `Question`.

Call `PlaceNpcMenu.addNpcAction(NpcMenuEntry entry)` to add your entry.  Then when you register actions call `PlaceNpcMenu.register()` and it will handle its own registration.  e.g:
```java
@Override
public void onServerStarted() {
    ModActions.registerAction(new OtherCustomAction());
    new PlaceJesterMenuEntry();
    PlaceNpcMenu.register();
}
```
Then in your Place class:
```java
public class PlaceJesterMenuEntry implements NpcMenuEntry {
    public PlaceJesterMenuEntry() {
        PlaceNpcMenu.addNpcAction(this);
    }

    @Override
    public String getName() {
        return "Jester";
    }

    @Override
    public boolean doAction(Action action, short num, Creature performer, Item source, VolaTile tile, int floorLevel) {
        Creature.doNew(JesterTemplate.templateId, (float)(tile.getTileX() << 2) + 2.0F, (float)(tile.getTileY() << 2) + 2.0F, 
                180.0F, tile.getLayer(), JesterTemplate.createName(), (byte)new Random().nextInt(2), performer.getKingdomId());
        performer.getCommunicator().sendNormalServerMessage("You place the Jester.");
        return true;
    }
}
```