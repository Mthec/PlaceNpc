package com.wurmonline.server.behaviours;

import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import mod.wurmunlimited.WurmObjectsFactory;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mod.wurmunlimited.Assert.didNotReceiveMessageContaining;
import static mod.wurmunlimited.Assert.receivedMessageContaining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlaceNpcMenuTests {
    private static final String npc1 = "Npc 1";
    private static final String npc2 = "Npc 2";
    private WurmObjectsFactory factory;
    private static PlaceNpcMenu menu;
    private static ActionEntry npc1Action;
    private Player gm;
    private Item wand;

    @BeforeAll
    public static void initialSetUp() throws NoSuchFieldException, IllegalAccessException {
        menu = PlaceNpcMenu.register();
        new MenuEntryTestImplementation(npc1);
        npc1Action = ReflectionUtil.<List<ActionEntry>>getPrivateField(null, PlaceNpcMenu.class.getDeclaredField("actionEntries")).get(1);
        new MenuEntryTestImplementation(npc2);
    }

    @BeforeEach
    public void setUp() throws Throwable {
        factory = new WurmObjectsFactory();
        gm = factory.createNewPlayer();
        gm.setPower((byte)2);
        wand = factory.createNewItem(ItemList.wandDeity);
    }

    // GetBehavioursFor

    @Test
    public void testCorrectBehaviourReceived() {
        List<ActionEntry> entries = menu.getBehavioursFor(gm, wand, 0, 0, true, 0);
        assertEquals(3, entries.size());
        assertEquals("Place Npc", entries.get(0).getActionString());
        assertEquals(npc1, entries.get(1).getActionString());
        assertEquals(npc2, entries.get(2).getActionString());
    }

    @Test
    public void testPlayersDoNotGetOption() {
        Player player = factory.createNewPlayer();
        assert player.getPower() < 2;
        List<ActionEntry> entries = menu.getBehavioursFor(player, wand, 0, 0, true, 0);
        assertNull(entries);
    }

    @Test
    public void testWandRequired() {
        Item item = factory.createNewItem();
        assert !item.isWand();
        List<ActionEntry> entries = menu.getBehavioursFor(gm, item, 0, 0, true, 0);
        assertNull(entries);
    }

    @Test
    public void testCorrectBehaviourReceivedDir() {
        List<ActionEntry> entries = menu.getBehavioursFor(gm, wand, 0, 0, true, 0, 1);
        assertEquals(3, entries.size());
        assertEquals("Place Npc", entries.get(0).getActionString());
        assertEquals(npc1, entries.get(1).getActionString());
        assertEquals(npc2, entries.get(2).getActionString());
    }

    @Test
    public void testCorrectBehaviourReceivedFloor() {
        List<ActionEntry> entries = menu.getBehavioursFor(gm, wand, true, mock(Floor.class));
        assertEquals(3, entries.size());
        assertEquals("Place Npc", entries.get(0).getActionString());
        assertEquals(npc1, entries.get(1).getActionString());
        assertEquals(npc2, entries.get(2).getActionString());
    }

    @Test
    public void testCorrectBehaviourReceivedBridge() {
        List<ActionEntry> entries = menu.getBehavioursFor(gm, wand, true, mock(BridgePart.class));
        assertEquals(3, entries.size());
        assertEquals("Place Npc", entries.get(0).getActionString());
        assertEquals(npc1, entries.get(1).getActionString());
        assertEquals(npc2, entries.get(2).getActionString());
    }

    // action

    @Test
    public void testAction() {
        Action action = mock(Action.class);
        when(action.getActionString()).thenReturn(npc1);
        when(action.getNumber()).thenReturn(npc1Action.getNumber());

        assertTrue(menu.action(action, gm, wand, 0, 0, true, 0, Server.surfaceMesh.getTile(0, 0), npc1Action.getNumber(), 0));
        assertThat(gm, receivedMessageContaining("You place the Npc 1."));
    }

    @Test
    public void testActionFloor() {
        Action action = mock(Action.class);
        when(action.getActionString()).thenReturn(npc1);
        when(action.getNumber()).thenReturn(npc1Action.getNumber());
        Floor floor = mock(Floor.class);
        VolaTile tile = Zones.getOrCreateTile(0, 0, true);
        when(floor.getTile()).thenReturn(tile);
        when(floor.getFloorLevel()).thenReturn(1);

        assertTrue(menu.action(action, gm, wand, true, floor, Server.surfaceMesh.getTile(0, 0), npc1Action.getNumber(), 0));
        assertThat(gm, receivedMessageContaining("You place the Npc 1."));
    }

    @Test
    public void testActionBridge() {
        Action action = mock(Action.class);
        when(action.getActionString()).thenReturn(npc1);
        when(action.getNumber()).thenReturn(npc1Action.getNumber());
        BridgePart bridgePart = mock(BridgePart.class);
        VolaTile tile = Zones.getOrCreateTile(0, 0, true);
        when(bridgePart.getTile()).thenReturn(tile);
        when(bridgePart.getFloorLevel()).thenReturn(1);

        assertTrue(menu.action(action, gm, wand, true, bridgePart, Server.surfaceMesh.getTile(0, 0), npc1Action.getNumber(), 0));
        assertThat(gm, receivedMessageContaining("You place the Npc 1."));
    }

    @Test
    public void testActionNotPlayer() {
        Action action = mock(Action.class);
        when(action.getActionString()).thenReturn(npc1);
        when(action.getNumber()).thenReturn(npc1Action.getNumber());
        Player player = factory.createNewPlayer();

        assertTrue(menu.action(action, player, wand, 0, 0, true, 0, Server.surfaceMesh.getTile(0, 0), npc1Action.getNumber(), 0));
        assertThat(player, didNotReceiveMessageContaining("You place the Npc 1."));
    }

    @Test
    public void testActionNotWand() {
        Action action = mock(Action.class);
        when(action.getActionString()).thenReturn(npc1);
        when(action.getNumber()).thenReturn(npc1Action.getNumber());

        assertTrue(menu.action(action, gm, factory.createNewItem(), 0, 0, true, 0, Server.surfaceMesh.getTile(0, 0), npc1Action.getNumber(), 0));
        assertThat(gm, didNotReceiveMessageContaining("You place the Npc 1."));
    }

    @Test
    public void testActionCouldNotBeLocated() {
        Action action = mock(Action.class);
        when(action.getActionString()).thenReturn(npc1);
        when(action.getNumber()).thenReturn(npc1Action.getNumber());

        assertTrue(menu.action(action, gm, wand, -1000, -1000, true, 0, Server.surfaceMesh.getTile(0, 0), npc1Action.getNumber(), 0));
        assertThat(gm, receivedMessageContaining("not be located."));
    }

    @Test
    public void testActionPerformers() {
        Action action = mock(Action.class);
        when(action.getActionString()).thenReturn(npc1);
        when(action.getActionEntry()).thenReturn(npc1Action);
        when(action.getNumber()).thenReturn(npc1Action.getNumber());

        assertTrue(ModActions.getActionPerformer(action).action(action, gm, wand, 0, 0, true, 0, Server.surfaceMesh.getTile(0, 0), npc1Action.getNumber(), 0));
        assertEquals(1, factory.getCommunicator(gm).getMessages().length);
        assertThat(gm, receivedMessageContaining("You place the Npc 1."));
    }
}
