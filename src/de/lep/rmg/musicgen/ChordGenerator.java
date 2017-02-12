package de.lep.rmg.musicgen;

import java.util.Random;

import de.lep.rmg.model.helper.RandomHelper;
import de.lep.rmg.model.notes.CType;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.helper.ChordHelper;

/**
 * Enthält Methoden zum generieren von Akkorden
 * 
 * @see MusicGenerator Controller für diese Klasse
 * @see ChordHelper Hilfsklasse für Akkordumwandlungen	
 */
public class ChordGenerator {
	
	/**
	 * Generiert eine bestimmte Anzahl von Akkorden<br>
	 * Die Tonika muss mindestens einmal vorkommen. Die anderen Akkorde sind ihre Subdominante und Dominante bzw. deren parallele Akkorde.
	 * 
	 * @param key Die Tonika
	 * @param length Die Anzahl an Akkorden die generiert werden soll
	 * @return SChord[] Die generierten Akkorde als {@link SChord}
	 */
	static SChord[] generateChords( SChord key, int length ) {
		SChord[] chords = new SChord[ length ];
		
		//Array aus Tonleiterposition der Tonika, Subdominante, Dominante und parallele Akkorden
		int[] all = getAllChordsOf( key );
		
		Random r = new Random();
		
		boolean hasKey = false;
		for( int c = 0; c < chords.length; c++ ) {
			chords[ c ] = ChordHelper.getChordFromScaleAt( key, RandomHelper.randFrom( all, r ) );
			if( chords[ c ].equals( key ) ) 
				hasKey = true;
		}
		
		if( !hasKey ) {
			int whereKey = r.nextInt( chords.length );
			chords[ whereKey ] = key;
		}
		
		//Ausgabe
		System.out.println( "Chords: " );
		for( SChord ch : chords )
			System.out.println( "  " + ch.toString() );
		System.out.println();
		
		return chords;
	}
	
	/**
	 * Gibt die Tonika, Dominante, Subdominante und parallele Akkorde als Position auf der Tonleiter zurück<br>
	 * E.g 1: C = F (4), C (1), G (5), dm (2), am (6), em (3)<br>
	 * E.g 2: am = dm (4), am (1), em (5), F (6), C (3), G (7)<br>
	 * 
	 * @param key Die Tonika
	 * @return 6 Akkorde als <code>int</code>s (Positionen auf der Tonleiter des Grundakkords)
	 * 
	 * @see ChordHelper Hilfsklasse für Akkordumwandlungen
	 */
	private static int[] getAllChordsOf( SChord key ) {
		if( key.getType() == CType.MAJOR )
			return new int[]{ 0, 1, 2, 3, 4, 5 };
		else if( key.getType() == CType.MINOR )
			return new int[]{ 0, 2, 3, 4, 5, 6 };
		else
			throw new IllegalArgumentException( "Invalid type of chord" );
	}
	
	/**
	 * Generiert einen zufälligen Tonika-Akkord
	 * @return Zufälliger Akkord vom Typ {@link SChord}
	 */
	static SChord generateKey() {
		Random r = new Random();
		int note = r.nextInt( 12 ); //zufälliger Grundton
		CType type = r.nextInt( 2 ) == 0 ? CType.MAJOR : CType.MINOR;//zufällige Tonart
		return new SChord( note, type );
	}
}
