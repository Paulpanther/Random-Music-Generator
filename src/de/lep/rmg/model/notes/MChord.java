package de.lep.rmg.model.notes;

import de.lep.rmg.model.notes.helper.ChordHelper;

/**
 * Speichert zu Dreiklangstönen eines Akkords auch, ob diese verwendet wurden oder nicht.<br>
 * Wird in {@link MelodyGenerator} verwendet.<br>
 * Soll bewirken, dass die Anfangstöne verschiedener Melodien aber gleicher Akkorde wenn möglich einen Dreiklang bilden.<br>
 *
 */
public class MChord {

	/**
	 * Die Dreiklangstöne
	 */
	private int[] tones;
	
	/**
	 * Benutzt oder nicht
	 */
	private boolean[] used;
	
	/**
	 * Entnimmt aus dem {@link SChord} mithilfe von {@link ChordHelper#createArrayChord(SChord)} die Dreiklangstöne.<br>
	 * Alle Töne werden auf unbenutzt gesetzt.
	 * 
	 * @param chord Der {@link SChord}
	 */
	public MChord( SChord chord ) {
		tones = ChordHelper.createArrayChord( chord );
		used = new boolean[ tones.length ];
		for( int i = 0; i < used.length; i++ )
			used[ i ] = false;
	}
	
	/**
	 * Getter für alle Töne. (Egal ob benutzt)
	 * @return Alle Töne des Dreiklangs
	 */
	public int[] getTones() {
		return tones;
	}
	
	/**
	 * Getter für alle Töne die noch nicht benutzt wurden.<br>
	 * Falls alle Töne verwendet wurden setzt die Methode alle Töne wieder auf unbenutzt.
	 * @return Alle Töne die noch nicht benutzt wurden
	 */
	public int[] getAvailableTones() {
		int length = getAvailableTonesLength();//Gibt aus wie viele Töne unbenutzt sind
		if( length == 0 ) {//Falls alle Töne benutzt:
			reset();//Alle Töne werden auf unbenutzt gesetzt
			length = getAvailableTonesLength();
		}
		
		int[] avTones = new int[ length ];
		for( int i = 0, j = 0; i < tones.length; i++, j++ ) {
			if( !used[ i ] )
				avTones[ j ] = tones[ i ];//Unbenutzte Töne werden hinzugefügt
			else
				j--;
		}
		return avTones;
	}
	
	/**
	 * Gibt zurück wie viele Töne unbenutzt sind
	 * @return Wie viele Töne unbenutzt sind
	 */
	public int getAvailableTonesLength() {
		int available = 0;
		for( boolean use : used ) {
			if( !use )
				available++;
		}
		return available;
	}
	
	/**
	 * Der gegebene Ton des Dreiklangs wird auf benutzt gesetzt.<br>
	 * Sollte gesetzt werden, wenn ein Ton des Dreiklangs tatsächlich verwendet wird
	 * @param tone Der Ton des Dreiklangs, der benutzt wurde
	 */
	public void used( int tone ) {
		for( int i = 0; i < tones.length; i++ ) {
			if( tones[ i ] == tone ) {
				used[ i ] = true;
				break;
			}
		}
	}
	
	/**
	 * Setzt alle Töne zurück auf unbenutzt
	 */
	public void reset() {
		for( int i = 0; i < used.length; i++ )
			used[ i ] = false;
	}
	
	/**
	 * Für Debug-Zwecke
	 */
	@Override
	public String toString() {
		String value = "MChord= {\n";
		for( int i = 0; i < tones.length; i++ )
			value += "    Ton = {Step: " + tones[ i ] + "; Used: " + used[ i ] + "}\n";
		return value + "}\n";
	}
	
	/**
	 * Konvertiert ein {@link SChord}-Array zu einen {@link MChord}-Array
	 * @param schords Das {@link SChord}-Array, welches konvertiert werden soll
	 * @return Die konvertierten {@link MChord}s
	 */
	public static MChord[] toMChords( SChord[] schords ) {
		MChord[] mchords = new MChord[ schords.length ];
		for( int i = 0; i < mchords.length; i++ )
			mchords[ i ] = new MChord( schords[ i ] );
		return mchords;
	}
}
