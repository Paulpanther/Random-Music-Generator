package de.lep.rmg.musicgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.helper.ArrayHelper;
import de.lep.rmg.model.helper.PercentPair;

/**
 * Klasse zum generieren des Rhythmus.<br>
 * Enthält die Methode {@link RhythmGenerator#generateRhythm(SongConfig)}, welche den Rhythmus generiert.
 *
 * @see MusicGenerator Controller für diese Klasse
 */
public class RhythmGenerator {
	
	/**
	 * Erzeugt einen zufälligen Rhythmus.<br>
	 * Ablauf:
	 * <ol>
	 * <li>Erzeugt 2 Mini-Rhythmen (sozusagen Rhythmus-Motive), die ein Akkord-Rhythmus lang sind</li>
	 * <li>Fügt diese in zufälliger Anordnung dem Rhythmus hinzu</li>
	 * </ol>
	 * Gibt eine 3-dimensionale Liste an Integern zurück mit folgender Struktur:<br>
	 * Die ganze Liste repräsentiert die Menge an Rhythmen (Nicht Instrument-Parts!!).<br>
	 * <b>Erste Ebene</b>: Einzelner Rhythmus; Enthält Menge an Akkord-Rhythmen (s. unten).<br>
	 * <b>Zweite Ebene</b>: Einzelner Akkord-Rhythmus (Näher erklärt in {@link MelodyGenerator#generateMelodies(de.lep.rmg.model.notes.SChord[], ArrayList[][], SongConfig)}).<br>
	 * Ein Akkord-Rhythmus besteht immer aus einen Mini-Rhythmus.<br>
	 * Da ein Akkord-Rhythmus unterschiedlich viele Dauern beinhalten kann muss er in einer ArrayList gespeichert werden.<br>
	 * <b>Dritte Ebene</b>: Die tatsächlichen Dauern (Als Integer).
	 * 
	 * @param config Die Konfiguration des Songs (s. {@link SongConfig}).
	 * @return Eine 3-dimensionale Liste aus Dauern. Siehe oben zur Struktur.
	 */
	static ArrayList<Integer>[][] generateRhythm( SongConfig config ) {
		
		Random r = new Random();
		int[] miniRhythm1 = generateMiniRhythm( config, r );//1. Mini-Rhythmus
		int[] miniRhythm2 = generateMiniRhythm( config, r );//2. Mini-Rhythmus
		
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[][] rhythm = new ArrayList[ config.getMelodyNr() ][ config.getChordNr() ];//Vorbereitung
		
		for( ArrayList<Integer>[] melRhythm : rhythm ) {
			for( int c = 0; c < melRhythm.length; c++ ) {
				
				if( r.nextBoolean() )//Zufällig wird für den Akkord-Rhythmus der 1. oder der 2. Mini-Rhythmus gesetzt.
					melRhythm[ c ] = new ArrayList<Integer>( ArrayHelper.toList( miniRhythm1 ) );
				else
					melRhythm[ c ] = new ArrayList<Integer>( ArrayHelper.toList( miniRhythm2 ) );
			}
		}
		return rhythm;
	}
	
	/**
	 * Generiert einen Mini-Rhythmus (Rhythmus-Motiv).<br>
	 * 
	 * @param config Die Konfiguration des Songs (s. {@link SongConfig}).
	 * @param r Ein {@link Random}-Objekt.
	 * @return Ein Integer-Array welches einen Mini-Rhythmus repräsentiert.
	 */
	private static int[] generateMiniRhythm( SongConfig config, Random r ) {
		
		//Vorbereitung
		List<Integer> rhythm = new ArrayList<Integer>();
		
		//Länge des Akkord-Rhythmus wird berechnet.
		//(SongConfig#getChordDuration() ist in Vierteln angegeben, die jeweils SongConfig#getMeasureDivision() lang sind)
		int duration = config.getChordDuration() * config.getMeasureDivision();
		
		//Die möglichen Dauern sind in SongConfig#getNoteDurations() abgelegt
		PercentPair[] allNotes = config.getNoteDurations();
		
		//Speichert die 'unbenutzte' Länge
		int left = duration;
		
		//Solange noch 'unbenutzte' Länge vorhanden ist
		while( left > 0 ) {
			
			//Mögliche Dauern, die zu lang für die verfügbare Länge werden entfernt
			allNotes = PercentPair.removeValuesGreaterThan( left, allNotes );
			
			//Eine zufällige Dauer wird ausgewählt
			rhythm.add( PercentPair.getRandomValue( allNotes, r ) );
			
			//Die unbenutzte Länge wird neu berechnet
			left = duration - ArrayHelper.getSum( rhythm );
		}
		
		//Der Mini-Rhythmus wird durchgemischt, da sonst wegen des obigen Auswahlverfahrens die längeren Dauern eher vorne und die kürzeren eher hinten im Rhythmus liegen würden
		Collections.shuffle( rhythm, r );
		return ArrayHelper.toArray( rhythm );
	}
	
//	/**
//	 * Creates random rhythms on basis of the config parameters.<br>
//	 * Every Array of ArrayList<<Integer>> represents one rhythm.<br>
//	 * Every ArrayList<<Integer>> represents one Measure.<br>
//	 * The Integers in these Lists stand for the duration of the tone.
//	 * @param config SongConfig that specifies the number of rhythms and a set of probabilitys
//	 * @return rhythms as ArrayList<<Integer>>[][]
//	 */
//	public static ArrayList<Integer>[][] generateRhythm( SongConfig config ) {
//		Random r = new Random();
//		ArrayList<Integer> miniRhythm1 = generateMiniRhythm( config, r );
//		ArrayList<Integer> miniRhythm2 = generateMiniRhythm( config, r );
//		ArrayList<Integer> miniRhythm3 = generateMiniRhythm( config, r );
//		ArrayList<Integer> endRhythm = generateMiniEndRhythm( config, r );
//		
//		@SuppressWarnings("unchecked")
//		ArrayList<Integer>[][] rhythm = new ArrayList[ config.getMelodyNr() ][ config.getChordNr() ];//ArrayList with 'config.getMelodyNr()' rhythms
//		for( ArrayList<Integer>[] melRhythm : rhythm ) {//for every single rhythm
//
//			int[] aorb = new int[ config.getChordNr() ];//list with random numbers from 0 to 2
//			for( int i = 0; i < aorb.length; i++ )
//				aorb[ i ] = r.nextInt( 3 );
//			
//			for( int c = 0; c < melRhythm.length; c++ ) {//for every Measure in this rhythm
//				if( c == melRhythm.length -1 ){
//					melRhythm[ c ] = endRhythm;
//				}
//				switch( aorb[ c ] ){
//				case 0: melRhythm[ c ] =  miniRhythm1 ;
//					break;
//				case 1: melRhythm[ c ] =  miniRhythm2 ;
//					break;
//				case 2: melRhythm[ c ] =  miniRhythm3 ;
//					break;
//				}
//				
//			}
//		}
//		
//		return rhythm;
//	}
//	
//	/**
//	 * 
//	 * @param config
//	 * @param r a Random object
//	 * @return a rhythm over one Measure as int[]
//	 */
//	private static ArrayList<Integer> generateMiniRhythm( SongConfig config, Random r ) {
//		ArrayList<Integer> rhythm = new ArrayList<Integer>();
//		int duration = config.getBeats() * config.getMeasureDivision();//duration of one Measure
//		PercentPair[] allNotes = config.getNoteDurations();//possible note durations
//		
//		int left = duration;
//		while( left > 0 ) {
//			allNotes = PercentPair.removeValuesGreaterThan( left, allNotes );
//			rhythm.add( PercentPair.getRandomValue( allNotes, r ) );
//			left = duration - ArrayHelper.getSum( rhythm );
//		}
//		Collections.shuffle( rhythm, r );
//		return rhythm;
//	}
//	
//	public static ArrayList<Integer> generateMiniEndRhythm( SongConfig config, Random rand ) {
//		ArrayList<Integer> rhythm = generateMiniRhythm( config, rand );
//		//makes sure the end tone is not shorter than the one before
//		if( rhythm.size() > 1 && rhythm.get(rhythm.size() - 1) < rhythm.get(rhythm.size() - 2)) {
//			int x = rhythm.get(rhythm.size() - 2);
//			rhythm.remove(rhythm.size() - 2);
//			rhythm.add(x);
//		}
//		return rhythm;
//	}
	
}
