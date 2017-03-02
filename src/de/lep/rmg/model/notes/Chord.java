package de.lep.rmg.model.notes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.lep.rmg.model.notes.helper.ChordHelper;
import de.lep.rmg.out.midi.TrackFactory;
import de.lep.rmg.out.xml.XMLGenerator;

/**
 * Speichert einen Akkord als Liste aus {@link SNote}s.<br>
 * Wird in {@link Song} verwendet.<br>
 * Diese Klasse wird nicht während der Produktion des Stückes, sondern nur zur Zwischenspeicherung und Datenweitergabe an den {@link XMLGenerator} und die Midi-{@link TrackFactory} verwendet.<br>
 * Die Getter- und Setter-Methoden sind in {@link ArrayList} implementiert. In dieser Klasse sind nur Zusatz-Methoden.
 *  
 * @see Song Song: Überklasse für Song-Daten 
 * @see INote Implementiert INote
 * @see ArrayList Erbt von ArrayList&lt;SNote&gt;
 * @see SChord SChord: Akkordklasse für Musikalgorithmus
 * @see ChordHelper ChordHelper: Hilfsklasse für alle Akkordklassen
 */
public class Chord extends ArrayList<SNote> implements INote {
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor
	 * @param notes Als {@link List}e aus {@link SNote}s
	 */
	public Chord( List<SNote> notes ) {
		super( notes );
	}
	
	/**
	 * Konstruktor
	 * @param notes Als {@link SNote}-Array
	 */
	public Chord( SNote[] notes ) {
		super( new ArrayList<SNote>( Arrays.asList( notes ) ) );
	}
	
	/**
	 * Getter für die Dauer des Akkords.<br>
	 * Gibt die Dauer der längsten Note zurück.<br>
	 * Implementiert von {@link INote}.
	 * @return Die Dauer des Akkords
	 */
	@Override
	public int getDuration() {
		int ret = 0;
		for(SNote sno : this){
			if(sno.getDuration() > ret)
				ret = sno.getDuration();
		}
		return ret;
	}
	
	/**
	 * Konvertiert ein {@link SChord}-Array zu einen {@link Chord}-Array.<br>
	 * <code>duration</code> wird benötigt, da {@link SChord} keine Dauer hat.
	 * @param chords
	 * @param duration
	 * @return
	 */
	public static Chord[] convertFrom( SChord[] chords, int duration ) {
		Chord[] newChords = new Chord[ chords.length ];
		for( int c = 0; c < chords.length; c++ )
			newChords[ c ] = ChordHelper.createChordFromInts( ChordHelper.createArrayChord( chords[ c ]), 3, duration );
		return newChords;
	}
	
	/**
	 * Neue {@link Object#toString()}-Methode, welche die Noten innerhalb des Akkordes auflistet
	 */
	@Override
	public String toString() {
		String value = "Chord [\n";
		for( SNote note : this )
			value += "  " + note.toString() + "\n";
		value += "]\n";
		return value;
	}
	
	
}
