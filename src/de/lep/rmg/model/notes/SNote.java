package de.lep.rmg.model.notes;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.notes.helper.NoteHelper;

/**
 * Speichert eine einzelne Note.<br>
 * Gehört zum {@link Song}-Modell.
 *
 */
public class SNote implements INote {

	/**
	 * Verschiedene Töne in Halbtonschritten.<br>
	 * Jeder Ton kann auch durch ton+n*12 dargestellt werden
	 */
	public static final int C = 0, CIS = 1, D = 2, DIS = 3, E = 4, F = 5, FIS = 6, G = 7, GIS = 8, A = 9, AIS = 10, B = 11;
	
	/**
	 * Verschiedene Dauern.<br>
	 * Diese gelten nur wenn {@link SongConfig#getMeasureDivision()} == 8 ist.
	 */
	public static final int A32TH = 1, A16TH = 2, EIGHTH = 4, EIGHTH_DOT = 6, QUARTER = 8, QUARTER_DOT = 12, 
			HALF = 16, WHOLE = 32, BREVE = 64, LONG = 128;
	
	/**
	 * Der Ton der Note.<br>
	 * Wird in Halbtonschritten angegeben.
	 */
	private int tone;
	
	/**
	 * Die Oktave der Note.<br>
	 * Die Oktave muss nicht hier gesetzt, sondern kann auch mit tone verrechnet sein, aus Gründen der Lesbarkeit sind beide Werte allerdings getrennt.
	 */
	private int octave;
	
	/**
	 * Die Dauer der Note.
	 */
	private int duration;
	
	
	public SNote( int tone, int octave, int duration ) {
		this.tone = tone;
		this.octave = octave;
		this.duration = duration;
	}
	
	/*#############################################################################
	 * 						GETTER
	 *###########################################################################*/

	public int getTone() {
		return tone;
	}
	
	public int getOctave() {
		return octave;
	}
	
	public int getDuration() {
		return duration;
	}
	
	/*#############################################################################
	 * 						SETTER
	 *###########################################################################*/

	public void setTone( int tone ) {
		this.tone = tone;
	}

	public void setOctave( int octave ) {
		this.octave = octave;
	}

	public void setDuration( int duration ) {
		this.duration = duration;
	}

	/**
	 * Für Debug-Zwecke
	 */
	@Override
	public String toString() {
		String stepStr = NoteHelper.getToneString( this );
		if( NoteHelper.getAlter( this ) == -1 )
			stepStr = "b";
		else if( NoteHelper.getAlter( this ) == 1 )
			stepStr = "#";
		return "SNote [tone=" + tone + " (" + stepStr + "), octave=" + octave + ", duration=" + duration + "]";
	}
}
