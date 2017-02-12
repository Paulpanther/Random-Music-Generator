package de.lep.rmg.model.notes.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
	}

}
