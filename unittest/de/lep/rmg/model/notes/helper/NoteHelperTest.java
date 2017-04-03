package de.lep.rmg.model.notes.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.lep.rmg.model.notes.CType;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;

/**
 * Unit-Test für Methoden der Klasse {@link NoteHelper}
 *
 */
public class NoteHelperTest {

	/**
	 * Unit-Test für die Methode {@link NoteHelper#getInterval(int, int)}.
	 */
	@Test
	public void testGetInterval() {
		assertEquals( 2, NoteHelper.getInterval( SNote.C, SNote.E ) );
		assertEquals( 1, NoteHelper.getInterval( SNote.D, SNote.E ) );
		assertEquals( 1, NoteHelper.getInterval( SNote.E, SNote.F ) );
		assertEquals( 0, NoteHelper.getInterval( SNote.C, SNote.C ) );
		assertEquals( 4, NoteHelper.getInterval( SNote.E, SNote.B ) );
		assertEquals( -2, NoteHelper.getInterval( SNote.E, SNote.C ) );
		assertEquals( 7, NoteHelper.getInterval( SNote.C, SNote.C + 12 ) );
		assertEquals( -4, NoteHelper.getInterval(SNote.AIS, SNote.DIS));
	}
	
	/**
	 * Unit-Test für die Methode {@link NoteHelper#addInterval(int, int, SChord)}.
	 */
	@Test
	public void testAddInterval() {
		assertEquals( SNote.A, NoteHelper.addInterval( SNote.A, 0, new SChord(SNote.G, CType.MAJOR) ));
		assertEquals( SNote.E + 12 , NoteHelper.addInterval( SNote.AIS, 3, new SChord(SNote.D, CType.MINOR)));
		assertEquals( SNote.DIS, NoteHelper.addInterval( SNote.G, -2, new SChord(SNote.C, CType.MINOR)));
		assertEquals( SNote.AIS, NoteHelper.addInterval( SNote.G, 2, new SChord(SNote.A, CType.DIM)));
		assertEquals( SNote.C - 12, NoteHelper.addInterval( SNote.D, -8, new SChord(SNote.A, CType.MINOR)));
		assertEquals( SNote.GIS + 24, NoteHelper.addInterval( SNote.D, 17, new SChord(SNote.C, CType.AUG)));
		assertEquals( SNote.AIS, NoteHelper.addInterval( SNote.E, 4, new SChord(SNote.F, CType.MAJOR)));
		assertEquals( SNote.D, NoteHelper.addInterval(SNote.B, -5, new SChord(SNote.D, CType.MINOR)));
	}
	
}
