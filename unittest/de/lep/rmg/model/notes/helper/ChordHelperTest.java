package de.lep.rmg.model.notes.helper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.lep.rmg.model.notes.CType;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;

/**
 * Unit-Test für {@link ChordHelper}
 * 
 */
public class ChordHelperTest {

	/**
	 * Test für {@link ChordHelper#createArrayChord(SChord)}
	 */
	@Test
	public void testCreateArrayChord() {
		assertArrayEquals( new int[]{ 0, 4, 7 }, ChordHelper.createArrayChord( new SChord( SNote.C, CType.MAJOR ) ) );
		assertArrayEquals( new int[]{ 0, 3, 7 }, ChordHelper.createArrayChord( new SChord( SNote.C, CType.MINOR ) ) );
		assertArrayEquals( new int[]{ 3, 7, 10 }, ChordHelper.createArrayChord( new SChord( SNote.DIS, CType.MAJOR ) ) );
		assertArrayEquals( new int[]{ 4, 7, 11 }, ChordHelper.createArrayChord( new SChord( SNote.E, CType.MINOR ) ) );
	}

	/**
	 * Test für {@link ChordHelper#getChordFromScaleAt(SChord, int)}
	 */
	@Test
	public void testGetChordFromScaleAt() {
		assertArrayEquals( new int[]{ SNote.G, SNote.B, SNote.D }, 		ChordHelper.createArrayChord( ChordHelper.getChordFromScaleAt( new SChord( SNote.C, CType.MAJOR ), 4 ) ) );
		assertArrayEquals( new int[]{ SNote.B, SNote.D, SNote.FIS }, 	ChordHelper.createArrayChord( ChordHelper.getChordFromScaleAt( new SChord( SNote.D, CType.MAJOR ), 5 ) ) );
		assertArrayEquals( new int[]{ SNote.C, SNote.E, SNote.G }, 		ChordHelper.createArrayChord( ChordHelper.getChordFromScaleAt( new SChord( SNote.G, CType.MAJOR ), 3 ) ) );
		assertArrayEquals( new int[]{ SNote.D, SNote.FIS, SNote.A }, 	ChordHelper.createArrayChord( ChordHelper.getChordFromScaleAt( new SChord( SNote.G, CType.MAJOR ), 4 ) ) );
		assertArrayEquals( new int[]{ SNote.E, SNote.G, SNote.B }, 		ChordHelper.createArrayChord( ChordHelper.getChordFromScaleAt( new SChord( SNote.G, CType.MAJOR ), 5 ) ) );
		assertArrayEquals( new int[]{ SNote.G, SNote.B, SNote.D }, 		ChordHelper.createArrayChord( ChordHelper.getChordFromScaleAt( new SChord( SNote.G, CType.MAJOR ), 0 ) ) );
	}
	
	/**
	 * Test für {@link ChordHelper#getPositionOnScale(int, SChord)}
	 */
	@Test
	public void testGetPositionOnScale() {
		assertEquals( 0, ChordHelper.getPositionOnScale( 0, new SChord( SNote.C, CType.MAJOR ) ) );
		assertEquals( 0, ChordHelper.getPositionOnScale( 12, new SChord( SNote.C, CType.MAJOR ) ) );
		assertEquals( 0, ChordHelper.getPositionOnScale( -12, new SChord( SNote.C, CType.MAJOR ) ) );
		assertEquals( 4, ChordHelper.getPositionOnScale( SNote.G, new SChord( SNote.C, CType.MAJOR ) ) );
		assertEquals( 4, ChordHelper.getPositionOnScale( SNote.G -12, new SChord( SNote.C, CType.MAJOR ) ) );
		assertEquals( 4, ChordHelper.getPositionOnScale( SNote.G +12, new SChord( SNote.C, CType.MAJOR ) ) );
	}

	/**
	 * Test für {@link ChordHelper#getCircleOfFifthPos(SChord)}
	 */
	@Test
	public void testGetCircleOfFifthPos() {
		assertEquals( 0, ChordHelper.getCircleOfFifthPos( new SChord( SNote.C, CType.MAJOR ) ) );
		assertEquals( -3, ChordHelper.getCircleOfFifthPos( new SChord( SNote.C, CType.MINOR ) ) );
		assertEquals( 4, ChordHelper.getCircleOfFifthPos( new SChord( SNote.E, CType.MAJOR ) ) );
		assertEquals( 6, ChordHelper.getCircleOfFifthPos( new SChord( SNote.FIS, CType.MAJOR ) ) );
		assertEquals( 4, ChordHelper.getCircleOfFifthPos( new SChord( SNote.CIS, CType.MINOR ) ) );
	}

	/**
	 * Test für {@link ChordHelper#createSChord(int[])}
	 */
	@Test
	public void testCreateSChord() {
		assertEquals( new SChord( SNote.C, CType.MAJOR ), ChordHelper.createSChord( new int[]{ SNote.C, SNote.E, SNote.G } ) );
		assertEquals( new SChord( SNote.C, CType.MINOR ), ChordHelper.createSChord( new int[]{ SNote.C, SNote.DIS, SNote.G } ) );
		assertEquals( new SChord( SNote.D, CType.MAJOR ), ChordHelper.createSChord( new int[]{ SNote.D, SNote.FIS, SNote.A } ) );
	}
}
