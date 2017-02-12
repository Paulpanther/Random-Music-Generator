package de.lep.rmg.model;

import java.util.ArrayList;

import de.lep.rmg.model.notes.INote;

/**
 * Ein einzelner Takt.<br>
 * Teil des {@link Song}-Modells
 * 
 */
public class Measure extends ArrayList<INote> implements Cloneable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Gibt einen relativen Wert an, mit welchem die Dauer berechnet wird.<br>
	 * Ein 4tel = 1*division, 8tel = 1/2*division, halb = 2*division, ...<br>
	 * Ist sozusagen die 'Schärfe' der Dauer, da keine float Werte für die Dauer erlaubt sind.<br>
	 * Dieser Wert wird für das XML- und MIDI-Format benötigt.
	 */
	private int division;
	
	/**
	 * Vorzeichen des Taktes.<br>
	 * Positiv: Kreuze, negativ: B
	 */
	private int fifths;
	
	/**
	 * Steht für die Taktart.<br>
	 * Hat momentan noch keine Auswirkung im Musikgenerator, was noch geändert werden sollte.
	 */
	private int beats, beattype;
	
	/**
	 * Notenschlüssel (für XML-Format)
	 */
	private Clef clef;
	
	
	public Measure( SongConfig config ) {
		this.division = config.getMeasureDivision();
		this.fifths = config.getFifth();
		this.beats = config.getBeats();
		this.beattype = config.getBeatType();
		this.clef = config.getClef();
	}
	
	Measure( int division, int fifths, int beats, int beattype, Clef clef ) {
		this.division = division;
		this.fifths = fifths;
		this.beats = beats;
		this.beattype = beattype;
		this.clef = clef;
	}
	
	/*#############################################################################
	 * 						GETTER
	 *###########################################################################*/
	
	public int getDivision() {
		return division;
	}

	public int getFifths() {
		return fifths;
	}

	public int getBeats() {
		return beats;
	}

	public int getBeattype() {
		return beattype;
	}

	public Clef getClef() {
		return clef;
	}
	
	/*#############################################################################
	 * 						SETTER
	 *###########################################################################*/
	
	public void setDivision(int division) {
		this.division = division;
	}
	
	public void setFifths(int fifths) {
		this.fifths = fifths;
	}
	
	public void setBeats(int beats) {
		this.beats = beats;
	}
	
	public void setBeattype(int beattype) {
		this.beattype = beattype;
	}

	public void setClef( Clef clef ) {
		this.clef = clef;
	}

	
	/**
	 * Für Debug-Zwecke
	 */
	@Override
	public String toString() {
		String value = "Measure [division=" + division + ", fifths=" + fifths + ", beats=" + beats + ", beattype=" + beattype
				+ ", clef=" + clef + ", notes=\n";
		for( INote iNote : this )
			value += "  " + iNote.toString() + "\n";
		value += "]\n";
		return value;
	}
	
	public Measure clone(){
		Measure mea = new Measure( division, fifths, beats, beattype, clef );
		mea.addAll(this);
		return mea;
	}

	/**
	 * Der Notenschlüssel in einer dem XML-Format angepassten Form
	 *
	 */
	public static class Clef {
		
		/**
		 * Mögliche Wert für Symbol.<br>
		 * G: Violinschlüssel, F: Bassschlüssel, TAB: Tabulatur-Schlüssel
		 */
		public static final int G = 1, F = 2, TAB = 3;
		
		/**
		 * Vorlagen der typischen Schlüssel
		 */
		public static final Clef CLEF_G = new Clef( Clef.G, 2 ), CLEF_F = new Clef( Clef.F, 4 ), CLEF_TAB = new Clef( Clef.TAB, 5 );
		
		/**
		 * Mögliche Werte:<br>
		 * 1: Violinschlüssel, 2: Bassschlüssel, 3: Tabulatur-Schlüssel
		 */
		private int sign;
		
		/**
		 * Die Notenlinie, auf die der Notenschlüssel zeigt
		 */
		private int line;

		
		private Clef( int sign, int line ) {
			this.sign = sign;
			this.line = line;
		}

		public int getSign() {
			return sign;
		}

		public int getLine() {
			return line;
		}
		
		/**
		 * 1 = "G", 2 = "F", 3 = "TAB"<br>
		 * @return Das Symbol des Notenschlüssels in einer dem XML-Format angepassten Form
		 */
		public String signToString() {
			switch( sign ) {
			case G:
				return "G";
			case F:
				return "F";
			case TAB:
				return "TAB";
			default:
				return "";
			}
		}

		/**
		 * Für Debug-Zwecke
		 */
		@Override
		public String toString() {
			return "Clef [sign=" + sign + ", line=" + line + "]";
		}
	}
}
