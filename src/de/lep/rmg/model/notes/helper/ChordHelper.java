package de.lep.rmg.model.notes.helper;

import de.lep.rmg.model.Song;
import de.lep.rmg.model.notes.CType;
import de.lep.rmg.model.notes.Chord;
import de.lep.rmg.model.notes.MChord;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;

/**
 * Hilfsklasse zur Umwandlung verschiedener Akkord-Darstellungen.<br>
 * Es gibt folgende Darstellungen:<br>
 * 
 * <h1>Darstellungen ohne Dauer:</h1>
 * <ul>
 * <li><b>Array-Akkord (int[])</b>: Besteht aus einem Array aus den im Dreiklang des Akkords enthaltenen Tönen.</li>
 * <li><b>{@link SChord}</b>: Besteht aus dem Grundton und der Tonart des Akkords.<br>Wird vor allem vom {@link ChordGenerator} verwendet.</li>
 * <li><b>Tonleiterposition (int und {@link SChord})</b>: Beschreibt den Akkord als Position (int) auf einer Tonleiter.<br>
 * Diese Darstellung ist relativ zu einen Grundakkord der meistens als {@link SChord} gespeichert wird.<br>
 * Sie wird in {@link ChordGenerator} verwendet</li>
 * <li><b>{@link MChord}</b>: Eigentlich wie Array-Akkord, speichert aber auch ob der jeweilige Dreiklangston bereits in der Melodie verwendet wurde oder nicht.<br>
 * Seine Verwendung wird in der Klasse selbst und in {@link MelodyGenerator} näher erläutert</li>
 * </ul>
 * <h1>Darstellungen mit Dauer:</h1>
 * <ul>
 * <li>{@link Chord}: Eine ArrayListe aus {@link SNote}s. Speichert die im Dreiklang des Akkords enthaltenen Noten.<br>
 * Gehört zur Architektur vom {@link Song}-Modell</li>
 * </ul>
 * 
 * @see ChordHelperTest Unit-Test für einige Methoden dieser Klasse
 */
public class ChordHelper {
	
	/**
	 * Wandelt einen {@link SChord} in einen Array-Akkord um.
	 * 
	 * @param sChord Ein {@link SChord}
	 * @return Der Akkord als Array-Akkord
	 * 
	 * @see ChordHelper#createSChord(int[]) Umwandlung Array-Akkord zu SChord
	 * @see ChordHelperTest#testCreateArrayChord() Unit-Test für diese Methode
	 */
	public static int[] createArrayChord( SChord sChord ) {
		int keynote = sChord.getKeynote();
		CType type = sChord.getType();
		
		int[] chord = new int[ 3 ];
		chord[ 0 ] = keynote;
		
		if( type == CType.MAJOR ) {
			chord[ 1 ] = (keynote + 4) % 12; 
			chord[ 2 ] = (chord[ 1 ] + 3) % 12; 
		} else if( type == CType.MINOR ) {
			chord[ 1 ] = (keynote + 3) % 12; 
			chord[ 2 ] = (chord[ 1 ] + 4) % 12; 
		}
		
		return chord;
	}
	
	/**
	 * Wandelt einen Array-Akkord zu einen {@link SChord} um.
	 * 
	 * @param chord Ein Array-Akkord
	 * @return Der Akkord als {@link SChord}
	 * 
	 * @see ChordHelper#createArrayChord(SChord) Umwandlung SChord zu Array-Akkord
	 * @see ChordHelperTest#testCreateSChord() Unit-Test für diese Methode
	 */
	public static SChord createSChord( int[] chord ) {
		int keynote = chord[ 0 ];
		CType type;
		
		if( chord[ 0 ] > chord[ 1 ] )
			chord[ 1 ] += 12;
		if( chord[ 1 ] > chord[ 2 ] )
			chord[ 2 ] += 12;
		
		if( ( chord[ 1 ] - chord[ 0 ] ) % 12 == 4 && ( chord[ 2 ] - chord[ 1 ] ) % 12 == 3 )
			type = CType.MAJOR;
		else if( ( chord[ 1 ] - chord[ 0 ] ) % 12 == 3 && ( chord[ 2 ] - chord[ 1 ] ) % 12 == 4 )
			type = CType.MINOR;
		else {
			for( int c : chord )
				System.out.println( c );
			throw new IllegalArgumentException( "Invalid chord" );
		}
		return new SChord( keynote, type );
	}
	
	/**
	 * Wandelt eine Tonleiterposition mit ihren entsprechenden Grundakkord in einen {@link SChord} um.
	 * 
	 * @param sChord Der Grundakkord (Tonika) der Tonleiter
	 * @param step Die Position des Akkordes auf der Tonleiter
	 * @return Der Akkord als {@link SChord}
	 * 
	 * @see ChordHelper#getScale(SChord) Methode, welche die Tonleiter eines SChords zurückgibt
	 * @see ChordHelperTest#testGetChordFromScaleAt() Unit-Test für diese Methode
	 */
	public static SChord getChordFromScaleAt( SChord sChord, int step ) {
		//Wandelt die Tonleiterposition erst in Array-Akkord um
		int[] scale = getScale( sChord );
		int[] chord = new int[ 3 ];
		chord[ 0 ] = scale[ step ];
		chord[ 1 ] = scale[ ( step +2 ) % scale.length ];
		chord[ 2 ] = scale[ ( step +4 ) % scale.length ];
		
		for( int i = 0; i < chord.length; i++ )
			chord[ i ] %= 12;
		
		//Array-Akkord wird zu SChord umgewandelt
		return createSChord( chord );
	}
	
	/**
	 * Gibt die Töne der Tonleiter des gegebenen Akkords zurück.<br>
	 * Töne können über 12 liegen, da kein Modulo verwendet wird.<br>
	 * Der erste Ton ist der Grundton und die nächsten Töne sind aufsteigend.
	 * 
	 * @param sChord Der Grundakkord als {@link SChord}
	 * @return Ein Array aus 7 Tönen, welche die Tonleiter repräsentieren
	 */
	public static int[] getScale( SChord sChord ) {
		int keynote = sChord.getKeynote();
		CType type = sChord.getType();
		
		int[] scale = new int[ 7 ];
		
		if( type == CType.MAJOR ) {
			scale[ 0 ] = keynote;
			scale[ 1 ] = keynote += 2;
			scale[ 2 ] = keynote += 2;
			scale[ 3 ] = keynote += 1;
			scale[ 4 ] = keynote += 2;
			scale[ 5 ] = keynote += 2;
			scale[ 6 ] = keynote += 2;
		} else if( type == CType.MINOR ) {
			scale[ 0 ] = keynote;
			scale[ 1 ] = keynote += 2;
			scale[ 2 ] = keynote += 1;
			scale[ 3 ] = keynote += 2;
			scale[ 4 ] = keynote += 2;
			scale[ 5 ] = keynote += 1;
			scale[ 6 ] = keynote += 2;
		} else
			throw new IllegalArgumentException( "Invalid Chord-Type" );
		return scale;
	}
	
	/**
	 * Gibt die Position eines Tons auf der Tonleiter des gegebenen {@link SChord}s zurück.<br>
	 * Gibt -1 zurück, falls der Ton nicht auf der Tonleiter liegt.
	 * 
	 * @param note Der Ton
	 * @param key Der Grundakkord
	 * @return Die Position des Tons auf der Tonleiter oder -1, falls der Ton nicht auf der Tonleiter liegt
	 *
	 * @see ChordHelperTest#testGetPositionOnScale() Unit-Test für diese Methode
	 */
	public static int getPositionOnScale( int note, SChord key ) {
		int[] scale = getScale( key );
		
		while( note < 0 )
			note += 12;
		
		note = note % 12;
		
		for( int i = 0; i < scale.length; i++ ) {
			if( scale[ i ] % 12 == note )
				return i;
		}
		return -1;
	}
	
	/**
	 * Gibt die Position des Akkordes auf dem Quintenzirkel aus.<br>
	 * C-Dur ist 0. Alle Werte im Uhrzeigersinn bis 6 sind positiv, alle Wert entgegen dem Uhrzeigersinn bis -6 sind negativ. Bei Moll wird 3 abgezogen, und der Wert angepasst, damit er -6 nie überschreitet.<br><br>
	 * Dieser Wert kann beispielsweise für die einfache Berechnung der Vorzeichen verwendet werden.<br><br>
	 * Die Position wird in dieser Methode erst durch ein Switch-Case bestimmt (, obwohl man sie auch berechnen könnte), als hätte der Akkord die Tonart Dur.<br>
	 * Falls die Tonart Moll ist wird die Position umgerechnet.
	 * 
	 * @param sChord Der Akkord als {@link SChord}, dessen Position berechnet werden soll.
	 * @return Die Position auf dem Quintenzirkel
	 * 
	 * @see ChordHelperTest#testGetCircleOfFifthPos() Unit-Test für diese Methode
	 */
	public static int getCircleOfFifthPos( SChord sChord ) {
		int keynote = sChord.getKeynote();
		CType type = sChord.getType();
		
		keynote = keynote % 12;
		int pos = 0;
		switch( keynote ) {
		case SNote.C:
			pos = 0; 
			break;
		case SNote.CIS:
			pos = -5; 
			break;
		case SNote.D:
			pos = 2; 
			break;
		case SNote.DIS:
			pos = -3; 
			break;
		case SNote.E:
			pos = 4; 
			break;
		case SNote.F:
			pos = -1; 
			break;
		case SNote.FIS:
			pos = 6; 
			break;
		case SNote.G:
			pos = 1; 
			break;
		case SNote.GIS:
			pos = -4; 
			break;
		case SNote.A:
			pos = 3; 
			break;
		case SNote.AIS:
			pos = -2; 
			break;
		case SNote.B:
			pos = 5; 
			break;
		default:
			throw new IllegalArgumentException( "Keynote " + keynote + " out of range" );
		}
		
		if( type == CType.MINOR ) {
			pos -= 3;
			if( pos < -6 )
				pos = 6 - ( Math.abs( pos ) - 6 );
		}
		return pos;
	}
	
	/**
	 * Erstellt einen {@link Chord} aus einen Array-Akkord.<br>
	 * Da der Array-Akkord keine Dauer hat muss diese zusätzlich angegeben werden.<br>
	 * Außerdem müssen für jeden Ton noch eine Oktave angegeben werden.<br>
	 * Falls alle Oktaven gleich sind kann alternativ {@link ChordHelper#createChordFromInts(int[], int, int)} verwendet werden.
	 * 
	 * @param chordInts Der Array-Akkord
	 * @param octaves Die Oktaven für jeden Ton
	 * @param duration Die Dauer des Akkords
	 * @return Der entstandene {@link Chord}
	 */
	public static Chord createChordFromInts( int[] chordInts, int[] octaves, int duration ) {
		SNote[] notes = new SNote[ chordInts.length ];
		for( int i = 0; i < notes.length; i++ )
			notes[ i ] = new SNote( chordInts[ i ], octaves[ i ], duration );
		return new Chord( notes );
	}
	
	/**
	 * Erstellt einen {@link Chord} aus einen Array-Akkord.<br>
	 * Da der Array-Akkord keine Dauer hat muss diese zusätzlich angegeben werden.<br>
	 * Außerdem muss eine Oktave angegeben werden, in der der Akkord gespielt werden soll.<br>
	 * Falls es unterschiedliche Oktaven für die jeweiligen Töne gibt kann alternativ {@link ChordHelper#createChordFromInts(int[], int, int)} verwendet werden.
	 * 
	 * @param chordInts Der Array-Akkord
	 * @param octave Die Oktave für den Akkord
	 * @param duration Die Dauer des Akkords
	 * @return Der entstandene {@link Chord}
	 */
	public static Chord createChordFromInts( int[] chordInts, int octave, int duration ) {
		SNote[] notes = new SNote[ chordInts.length ];
		for( int i = 0; i < notes.length; i++ )
			notes[ i ] = new SNote( chordInts[ i ], octave, duration );
		return new Chord( notes );
	}
}
