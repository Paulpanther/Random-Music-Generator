package de.lep.rmg.model.notes;

import de.lep.rmg.model.notes.helper.NoteHelper;

/**
 * Repräsentiert einen Akkord.<br>
 * Besteht aus dem Grundton und der Tonart des Akkords.<br>
 * Wird vor allem vom {@link ChordGenerator} verwendet.
 *
 */
public class SChord {

	/**
	 * Der Grundton
	 */
	private int keytone;
	
	/**
	 * Die Tonart
	 */
	private CType type;
	
	
	public SChord( int keynote, CType type ) {
		this.keytone = keynote;
		this.type = type;
	}
	
	/*#############################################################################
	 * 						GETTER
	 *###########################################################################*/

	public int getKeynote() {
		return keytone;
	}

	public CType getType() {
		return type;
	}
	
	/*#############################################################################
	 * 						SETTER
	 *###########################################################################*/
	
	public void setKeynote( int keynote ) {
		this.keytone = keynote;
	}

	public void setType( CType type ) {
		this.type = type;
	}
	
	/**
	 * Vergleicht zwei Akkorde miteinander und gibt zurück, ob diese den gleichen Wert haben
	 * @param chord Der Akkord mit dem verglichen werden soll
	 * @return true, falls die Akkorde den gleichen Wert haben, sonst false
	 */
	public boolean equals( SChord chord ) {
		return chord.getKeynote() == keytone && chord.getType() == type;
	}
	
	@Override
	public boolean equals( Object obj ) {
		if( obj instanceof SChord )
			return equals( (SChord) obj );
		return super.equals( obj );
	}

	/**
	 * Für Debug-Zwecke
	 */
	@Override
	public String toString() {
		return "SChord [keynote=" + keytone + "(" + NoteHelper.getToneString( new SNote( keytone, 0, 0 ) ) + ( NoteHelper.getAlter( new SNote( keytone, 0, 0 ) ) == 1 ? "is" : "" ) + "), type=" + type + "]";
	}
}
