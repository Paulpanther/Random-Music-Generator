package de.lep.rmg.musicgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.helper.ArrayHelper;
import de.lep.rmg.model.helper.IntHelper;
import de.lep.rmg.model.helper.PercentPair;
import de.lep.rmg.model.helper.RandomHelper;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.model.notes.Rest;
import de.lep.rmg.model.notes.SNote;

/**
 * Klasse zum generieren des Rhythmus.<br>
 * Enthält die Methode {@link RhythmGenerator#generateRhythm(SongConfig)}, welche den Rhythmus generiert.
 *
 * @see CanonGenerator Controller für diese Klasse
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
	public static ArrayList<Integer>[][] generateRhythm( SongConfig config ) {
		
		Random r = new Random();
		int duration = config.getChordDuration() * config.getMeasureDivision();
		int[] miniRhythm1 = generateMiniRhythm( config, duration, r );//1. Mini-Rhythmus
		int[] miniRhythm2 = generateMiniRhythm( config, duration, r );//2. Mini-Rhythmus
		
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
	 * 
	 * @param config - zum {@link Song} gehörendes {@link SongConfig}
	 * @param length - Länge des rhythmischen Motivs in vielfachen der Länge des Themas (Beats * ChordNr * MeasureDivision)
	 * @return rhytmisches Motiv als ArrayList<INote>
	 */
	public static ArrayList<INote> generateMotif( SongConfig config, int length ) {
		ArrayList<INote> motif = new ArrayList<INote>();
		int measure = config.getBeats() * config.getMeasureDivision();
		for(int i = 0; i < config.getChordNr() * length; i++){
			for(int dur : generateMiniRhythm( config, measure, RandomHelper.getRandom() ) ){
				motif.add( makeNote( config, dur) );
			}
		}
		return motif;
	}
	
	/**
	 * Generiert ein rhythmische Gegenmotiv zum übergebenen Hauptmotiv. Wenn im Hauptmotiv
	 * kurze Noten gespielt werden, werden diese zu längeren zusammengefasst, sofern sich 
	 * eine zulässige Notenänge ergibt. Lange Notendauern werden meistens zu kürzeren aufgespalten.
	 * @param config - zum {@link Song} gehörendes {@link SongConfig}
	 * @param motif - rhythmisches Motiv, zu dem ein Gegenmotiv generiert werden soll. Bleibt unverändert.
	 * @return rhythmisches Gegenmotiv mit gleicher Länge, wie das Hauptmotiv
	 */
	public static ArrayList<INote> generateAntiMotif( SongConfig config, ArrayList<INote> motif ) {
		ArrayList<INote> antiMotif = new ArrayList<INote>();//Rhythmus der zurückgegeben wird
		int measure = config.getBeats() * config.getMeasureDivision();//im Measure verbleibende Dauer
		int duration = 0;//Dauer einer noch nicht hinzugefügten Note
		int[] possibleDurs = new int[config.getNoteDurations().size()];//mögliche Notenlängen, Wahrscheinlichkeiten aus SongConfig werden nicht gebraucht
		for(int i = 0; i < possibleDurs.length; i++){
			possibleDurs[i] = config.getNoteDurations().get(i).getValue();
		}
		int min = possibleDurs[0];//Maximallänge für Fall kurze Note und Grenze für verbleibende Taktlänge
		if( possibleDurs[1] < min*2 ){
			min = possibleDurs[1];
			if( possibleDurs[2] < min*2)
				min = possibleDurs[2];
		}
		int max = possibleDurs[possibleDurs.length - 1];//Mindestlänge für Fall lange Note
		if( possibleDurs[possibleDurs.length - 2] > max/2 ){
			max = possibleDurs[possibleDurs.length - 2];
			if( possibleDurs[possibleDurs.length - 3] > max/2 )
				max = possibleDurs[possibleDurs.length - 3];
		}
		for ( INote inote : motif) {//Schleife, in welcher der Rhythmus erstellt wird
			duration += inote.getDuration();
			//Spezialfälle:
			if( measure <= min ){//wenig Platz im Takt verbleibend
				if( duration > inote.getDuration()){//es ist eine Note nicht hinzugefügt worden
					if( Arrays.binarySearch(possibleDurs, duration) > 0) {
						antiMotif.add( makeNote( config, duration) );
						measure -= duration;
						while( measure <= 0)
							measure += config.getBeats() * config.getMeasureDivision();
						duration = 0;
						continue;
					}else{
						duration -= inote.getDuration();
						antiMotif.add( makeNote( config, duration) );
						measure -= inote.getDuration();
					}
				}
				antiMotif.add( makeNote(config, duration));
			}else{
				if( duration > inote.getDuration()){//beim letzten Durchlauf wurde eine Note nicht hinzugefügt
					if( Arrays.binarySearch(possibleDurs, duration) > 0) {//falls die Gesamtdauer beider Noten eine gültige Note ergibt
						antiMotif.add( makeNote( config, duration) );//wird eine neue Note mit der Gesamtdauer hinzugefügt
					}else{//andernfalls werden zwei einzelne Noten mit den ursprünglichen Dauern hinzugefügt
						antiMotif.add( makeNote( config, duration - inote.getDuration()));
						antiMotif.add( makeNote( config, inote.getDuration()));
					}
				}else{//Normalfall
					if( duration <= min){//kurze Note
						continue;//es wird zunächst keine Note hinzugefügt, um eine längere Note (diese + nächste) hinzufügen
					}else{
						if( duration >= max){//lange Note
							divisionCase( config, antiMotif, possibleDurs, duration);//es werden mehrere kürzere Noten gespielt
						}else{//falls duration zwischen min und max liegt, wird zufällig eine der zugehörigen Aktionen ausgewählt
							if( RandomHelper.getRandom().nextBoolean()){
								continue;
							}else{
								divisionCase( config, antiMotif, possibleDurs, duration);
							}
						}
					}
				}
			}
			measure -= duration;
			while( measure <= 0)
				measure += config.getBeats() * config.getMeasureDivision();
			duration = 0;
		}
		if( duration > 0){
			antiMotif.add( makeNote( config, duration));
		}
		return antiMotif;
	}

	/**
	 * Erstellt eine neue {@link INote} mit der übergebenen Dauer.
	 * Mit der in {@link SongConfig#getRestProbability()} angegebenen Wahrscheinlichkeit wird
	 * es eine Pause ({@link Rest}, ansonsten eine {@link SNote} mit Tonhöhe und Oktave gleich 0
	 * @param config - zum {@link Song} gehörendes {@link SongConfig}
	 * @param dur - Dauer der Note
	 * @return eine INote
	 */
	private static INote makeNote( SongConfig config, int dur ) {
		INote inote = null;
		if(RandomHelper.getRandom().nextFloat() < config.getRestProbability())
			inote = new Rest(dur);
		else
			inote = new SNote(0,0,dur);
		return inote;
	}

	private static void divisionCase( SongConfig config, ArrayList<INote> antiMotif, int[] possibleDurs, int duration) {
//		if(RandomHelper.getRandom().nextInt(10) <= 2){//30% Wahrscheinlichkeit eine Note der vollen Länge hinzuzufügen
//			antiMotif.add( makeNote( config, duration));//soll übermäßiger Unregelmäßigkeit und der Tendenz zu kürzeren Notenwerten entgegenwirken
//		}else{//zu testzwecken auskommentiert
		ArrayList<ArrayList<Integer>> list = IntHelper.possibleSums(possibleDurs, duration);
		ArrayList<Integer> durations = RandomHelper.randFrom(list);
		Collections.shuffle(durations);
		for(int dur : durations){
			antiMotif.add(makeNote( config, dur));
//		}
		}
	}

	/**
	 * Generiert einen Mini-Rhythmus (Rhythmus-Motiv).<br>
	 * 
	 * @param config Die Konfiguration des Songs (s. {@link SongConfig}).
	 * @param duration - Gesamtnotendauer des Mini-Rhythmus
	 * @param rand - Ein {@link Random}-Objekt.
	 * @return Ein Integer-Array welches einen Mini-Rhythmus repräsentiert.
	 */
	private static int[] generateMiniRhythm( SongConfig config, int duration, Random rand ) {

		//Vorbereitung
		List<Integer> rhythm = new ArrayList<Integer>();

		//Die möglichen Dauern sind in SongConfig#getNoteDurations() abgelegt
		ArrayList<PercentPair> allNotes = new ArrayList<PercentPair>();
		for(PercentPair pair : config.getNoteDurations()){
			allNotes.add(pair);
		}

		//Speichert die 'unbenutzte' Länge
		int left = duration;

		//Solange noch 'unbenutzte' Länge vorhanden ist
		while( left > 0 ) {

			//Mögliche Dauern, die zu lang für die verfügbare Länge werden entfernt
			allNotes = PercentPair.removeValuesGreaterThan( left, allNotes );

			//Eine zufällige Dauer wird ausgewählt
			rhythm.add( PercentPair.getRandomValue( allNotes, rand ) );

			//Die unbenutzte Länge wird neu berechnet
			left = duration - ArrayHelper.getSum( rhythm );
		}

		//Der Mini-Rhythmus wird durchgemischt, da sonst wegen des obigen Auswahlverfahrens die längeren Dauern eher vorne und die kürzeren eher hinten im Rhythmus liegen würden
		Collections.shuffle( rhythm, rand );
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
