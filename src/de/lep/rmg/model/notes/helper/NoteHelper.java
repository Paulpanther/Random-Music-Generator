package de.lep.rmg.model.notes.helper;

import de.lep.rmg.model.notes.Chord;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.model.notes.Rest;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.out.xml.XMLGenerator;

/**
 * Hilfsklasse für Noten und Töne.
 *
 * @see NoteHelperTest Unit-Test für Methoden dieser Klasse
 */
public class NoteHelper {
	
	/**
	 * Gibt den Ton der Note in dem vom {@link XMLGenerator} benötigten Format zurück
	 * 
	 * @param note Die Note
	 * @return Der Ton in einen vom {@link XMLGenerator} benötigten Format
	 */
	public static String getToneString( SNote note ) {
		int modTone = note.getTone() % 12;
		while( modTone < 0 )
			modTone += 12;
		switch( modTone ) {
		case SNote.C:
			return "C";
		case SNote.CIS:
			return "C";
		case SNote.D:
			return "D";
		case SNote.DIS:
			return "D";
		case SNote.E:
			return "E";
		case SNote.F:
			return "F";
		case SNote.FIS:
			return "F";
		case SNote.G:
			return "G";
		case SNote.GIS:
			return "G";
		case SNote.A:
			return "A";
		case SNote.AIS:
			return "A";
		case SNote.B:
			return "B";
		default:
			throw new IllegalArgumentException( "Invalid tone" );
		}
	}
	
	/**
	 * Gibt die tatsächliche Oktave des Tons zurück
	 * @param note Die Note
	 * @return Die tatsächliche Oktave des Tons
	 */
	public static int getOctave( SNote note ) {
		int tone = note.getTone();
		int addOctaves = tone / 12;
		while( tone < 0 ) {
			addOctaves--;
			tone += 12;
		}
		return note.getOctave() + addOctaves;
	}
	
	/**
	 * Gibt die Dauer der Note in einen vom {@link XMLGenerator} akzeptierten Format zurück
	 * @param iNote Die Note
	 * @return Die Dauer im XML kompatiblen Format
	 */
	public static String getDurationString( INote iNote ) {
		int duration = 0;
		
		if( iNote instanceof SNote )
			duration = ((SNote) iNote).getDuration();
		else if( iNote instanceof Rest )
			duration = ((Rest) iNote).getDuration();
		else if( iNote instanceof Chord )
			duration = ((Chord) iNote).size() != 0 ? ((Chord) iNote).get( 0 ).getDuration() : 0;
		
		switch( duration ) {
		case SNote.A32TH:
			return "32th";
		case SNote.A16TH:
			return "16th";
		case SNote.EIGHTH:
		case SNote.EIGHTH_DOT:
			return "eighth";
		case SNote.QUARTER:
		case SNote.QUARTER_DOT:
			return "quarter";
		case SNote.HALF:
			return "half";
		case SNote.WHOLE:
			return "whole";
		case SNote.BREVE:
			return "breve";
		case SNote.LONG:
			return "long";
		default:
			throw new IllegalArgumentException( "Invalid Duration" );
		}
	}
	
	/**
	 * Gibt zurück, ob die Note eine punktierte Dauer hat.<br>
	 * Wird vom {@link XMLGenerator} benötigt.
	 * 
	 * @param iNote Die Note
	 * @return Boolean, welcher besagt, ob die dauer punktiert ist oder nicht
	 */
	public static boolean hasDot( INote iNote ) {
		int duration = 0;
		
		if( iNote instanceof SNote )
			duration = ((SNote) iNote).getDuration();
		else if( iNote instanceof Rest )
			duration = ((Rest) iNote).getDuration();
		else if( iNote instanceof Chord )
			duration = ((Chord) iNote).size() != 0 ? ((Chord) iNote).get( 0 ).getDuration() : 0;
			
		switch( duration ) {
		case SNote.EIGHTH_DOT:
		case SNote.QUARTER_DOT:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Gibt eine 0 zurück, wenn die Note kein Vorzeichen hat, 1 bei einen Kreuz (#) und theoretisch -1 bei einen B.<br>
	 * Da der Musikgenerator ohne Bs arbeitet fällt dies allerdings weg.<br>
	 * Diese Methode wird vom {@link XMLGenerator} benötigt.
	 * 
	 * @param note Die Note
	 * @return Einen Integer, welcher das Vorzeichen der Note repräsentiert
	 */
	public static int getAlter( SNote note ) {
		int modStep = note.getTone() % 12;
		switch( modStep ) {
		case SNote.C:
			return 0;
		case SNote.CIS:
			return 1;
		case SNote.D:
			return 0;
		case SNote.DIS:
			return 1;
		case SNote.E:
			return 0;
		case SNote.F:
			return 0;
		case SNote.FIS:
			return 1;
		case SNote.G:
			return 0;
		case SNote.GIS:
			return 1;
		case SNote.A:
			return 0;
		case SNote.AIS:
			return 1;
		case SNote.B:
			return 0;
		default:
			return 0;
		}
	}
	
	/**
	 * Gibt den Intervall zwischen zwei Tönen zurück.<br>
	 * Die Töne stehen hierbei in Halbtonschritten, das Intervall allerdings in Ganztonschritten.<br>
	 * Prime: 0, Sekunde: 1, Terz: 2, ...<br>
	 * Es können auch negative Intervalle entstehen, wenn tone2 kleiner als tone1 ist.<br>
	 * Es wird nicht moduliert, dass heißt ein Oktavenintervall ergibt 7 und nicht 0.
	 * 
	 * @param tone1 Der erste Ton
	 * @param tone2 Der zweite Ton
	 * @return Das Intervall zwischen den Tönen
	 * 
	 * @see NoteHelperTest#testGetInterval() Unit-Test für diese Methode
	 */
	public static int getInterval( int tone1, int tone2 ){
		int interval = 0;
		int steps = tone2 - tone1;
		int correct = steps / 12;
		steps %= 12;
		if(steps != 0){
			if(steps > 0){
				if(steps < 3)
					interval = 1;
				else if(steps < 5)
					interval = 2;
				else if(steps == 5)
					interval = 3;
				else if(steps == 6){
					interval = 4;
				}else if(steps == 7)
					interval = 4;
				else if(steps < 10)
					interval = 5;
				else interval = 6;
			}else{
				if(steps > -3)
					interval = -1;
				else if(steps > -5)
					interval = -2;
				else if(steps == -5)
					interval = -3;
				else if(steps == -6){
					interval = -4;
				}else if(steps == -7)
					interval = -4;
				else if(steps > -10)
					interval = -5;
				else interval = -6;
			}
		}
		interval += correct*7;
		
		return interval;				
	}
}
