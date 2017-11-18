package de.lep.rmg.model.notes.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.lep.rmg.model.notes.CType;
import de.lep.rmg.model.notes.IRealNote;
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
		assertEquals( -11, NoteHelper.getInterval( SNote.GIS + 12, SNote.CIS));
		assertEquals( 9, NoteHelper.getInterval( SNote.E, SNote.G + 12));
		assertEquals( 9, NoteHelper.getInterval( SNote.E, SNote.GIS + 12));
	}
	
	/**
	 * Unit-Test für die Methode {@link NoteHelper#addInterval(int, int, SChord)}.
	 */
	@Test
	public void testAddInterval() {
		assertEquals( SNote.A, NoteHelper.addInterval( SNote.A, /*Prime*/0, new SChord(SNote.G, CType.MAJOR) ));
		assertEquals( SNote.E + 12 , NoteHelper.addInterval( SNote.AIS, /*Quarte*/3, new SChord(SNote.D, CType.MINOR)));
		assertEquals( SNote.DIS, NoteHelper.addInterval( SNote.G, /*Terz abwärts*/-2, new SChord(SNote.C, CType.MINOR)));
		assertEquals( SNote.AIS, NoteHelper.addInterval( SNote.G, /*Terz aufwärts*/2, new SChord(SNote.A, CType.DIM)));
		assertEquals( SNote.C - 12, NoteHelper.addInterval( SNote.D, /*Sekunde + Oktave abwärts*/-8, new SChord(SNote.A, CType.MINOR)));
		assertEquals( SNote.GIS + 24, NoteHelper.addInterval( SNote.D, /*Quarte + 2 Oktaven*/17, new SChord(SNote.C, CType.AUG)));
		assertEquals( SNote.AIS, NoteHelper.addInterval( SNote.E, /*Qunite*/4, new SChord(SNote.F, CType.MAJOR)));
		assertEquals( SNote.D, NoteHelper.addInterval(SNote.AIS, /*Sexte abwärts*/-5, new SChord(SNote.D, CType.MINOR)));
	}
	
	/**
	 * Unit-Test für die Methode {@link NoteHelper#addInterval(IRealNote, int, SChord)}.
	 */
	@Test
	public void testAddIntervalRealNote() {
		//Tonarten
		SChord cMajorChord = new SChord( SNote.C, CType.MAJOR);
		SChord aMinorChord = new SChord( SNote.A, CType.MINOR);
		SChord eMajorChord = new SChord( SNote.E, CType.MAJOR);
		SChord cMinorChord = new SChord( SNote.C, CType.MINOR);
		SChord gAugChord = new SChord( SNote.G, CType.AUG);
		SChord bDimChord = new SChord( SNote.B, CType.DIM);
		
		IRealNote note = new SNote( SNote.C, /*Oktave*/3, /*Dauer*/16);
		
		//Test
		NoteHelper.addInterval(note, /*Prime*/0, cMajorChord);
		assertEquals( SNote.C, note.getTone());//Ton
		assertEquals( 3, note.getOctave());//Oktave
		
		note.setTone( SNote.C);
		note.setOctave( 3 );
		NoteHelper.addInterval(note, /*Sekunde*/1, cMajorChord);
		assertEquals( SNote.D,  note.getTone() );//Ton
		assertEquals( 3, note.getOctave());//Oktave
		
		note.setTone( SNote.A);
		note.setOctave( 3 );
		NoteHelper.addInterval(note, /*Quinte abwärts*/-4, aMinorChord);
		assertEquals( SNote.D, note.getTone());
		assertEquals( 3, note.getOctave());
		
		note.setTone( SNote.C);
		note.setOctave( 3 );
		NoteHelper.addInterval(note, /*Terz abwärts*/-2, aMinorChord);
		assertEquals( SNote.A, note.getTone());
		assertEquals( 2, note.getOctave());
		
		note.setTone( SNote.C);
		note.setOctave( 3 );
		NoteHelper.addInterval(note, /*Terz abwärts*/-2, cMajorChord);
		assertEquals( SNote.A, note.getTone());
		assertEquals( 2, note.getOctave());
		
		note.setTone( SNote.GIS);
		note.setOctave( 3 );
		NoteHelper.addInterval(note, /*Quarte*/3, eMajorChord);
		assertEquals( SNote.CIS, note.getTone());
		assertEquals( 4, note.getOctave());
		
		note.setTone( SNote.FIS);
		note.setOctave( 3 );
		NoteHelper.addInterval(note, /*Sexte abwärts*/-5, eMajorChord);
		assertEquals( SNote.A, note.getTone());
		assertEquals( 2, note.getOctave());
		
		note.setTone( SNote.DIS);
		note.setOctave( 1 );
		NoteHelper.addInterval(note, /*Oktave*/7, cMinorChord);
		assertEquals( SNote.DIS, note.getTone());
		assertEquals( 2, note.getOctave());
		
		note.setTone( SNote.AIS);
		note.setOctave( 7 );
		NoteHelper.addInterval(note, /*Sekunde + Oktave abwärts*/-8, cMinorChord);
		assertEquals( SNote.GIS, note.getTone());
		assertEquals( 6, note.getOctave());
		
		note.setTone( SNote.F);
		note.setOctave( 3 );
		NoteHelper.addInterval(note, /*Septime*/6, cMinorChord);
		assertEquals( SNote.DIS, note.getTone());
		assertEquals( 4, note.getOctave());
		
		note.setTone( SNote.G);
		note.setOctave( 0 );
		NoteHelper.addInterval(note, /*Quinte + Oktave*/11, gAugChord);
		assertEquals( SNote.DIS, note.getTone());
		assertEquals( 2, note.getOctave());
		
		note.setTone( SNote.F);
		note.setOctave( 5 );
		NoteHelper.addInterval(note, /*Quarte + Oktave abwärts*/-10, bDimChord);
		assertEquals( SNote.C, note.getTone());
		assertEquals( 4, note.getOctave());
	}
	
}
