package de.lep.rmg.model.notes;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.notes.helper.NoteHelper;

/**
 * Speichert eine einzelne Note.<br>
 * Gehört zum {@link Song}-Modell.
 *
 */
public class SNote implements INote, IRealNote {

	/**
	 * Verschiedene Töne in Halbtonschritten.<br>
	 * Jeder Ton kann auch durch ton+n*12 dargestellt werden
	 */
	public static final int C = 0, CIS = 1, D = 2, DIS = 3, E = 4, F = 5, FIS = 6, G = 7, GIS = 8, A = 9, AIS = 10, B = 11;
	
	/**
	 * Verschiedene Notendauern, abhängig von {@link SongConfig#getMeasureDivision()}.
	 */
	public static final int A32TH = SongConfig.measureDivision/8, A16TH = SongConfig.measureDivision/4,
			EIGHTH = SongConfig.measureDivision/2, EIGHTH_DOT = 3*(SongConfig.measureDivision/4),
			QUARTER = SongConfig.measureDivision, QUARTER_DOT = 3*(SongConfig.measureDivision/2), 
			HALF = 2*SongConfig.measureDivision, HALF_DOT = 3*SongConfig.measureDivision,
			WHOLE = 4*SongConfig.measureDivision, BREVE = 8*SongConfig.measureDivision, LONG = 16*SongConfig.measureDivision;
	
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
	
	@Override
	public int getStep() {
		return this.tone + 12 * this.octave;
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
	 * Erhöht die Note um 'steps' Halbtonschritte und gleicht die interne Variable 'octave' entsprechend an.
	 * @param steps - Anzahl der Halbtonschritte, um die die Note erhöht wird. Darf auch negativ sein.
	 */
	public void addStep( int steps ) {
		this.tone += steps;
		while ( tone < 0 ) {
			this.octave -= 1;
			this.tone += 12;
		}
		while ( tone >= 12 ) {
			this.octave += 1;
			this.tone -= 12;
		}
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
