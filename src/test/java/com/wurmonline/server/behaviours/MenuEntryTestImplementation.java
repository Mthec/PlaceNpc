package com.wurmonline.server.behaviours;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.VolaTile;

public class MenuEntryTestImplementation implements NpcMenuEntry {
    private final String name;

    MenuEntryTestImplementation(String name) {
        this.name = name;
        PlaceNpcMenu.addNpcAction(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean doAction(Action action, short num, Creature performer, Item source, VolaTile tile, int floorLevel) {
        performer.getCommunicator().sendNormalServerMessage("You place the " + name + ".");
        return true;
    }
}
