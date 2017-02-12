package de.lep.rmg.musicgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.helper.ArrayHelper;
import de.lep.rmg.model.helper.PercentPair;
import de.lep.rmg.model.helper.RandomHelper;
import de.lep.rmg.model.notes.MChord;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.model.notes.helper.ChordHelper;
import de.lep.rmg.model.notes.helper.NoteHelper;

/**
 * Was der Name sagt: Generiert Melodien<br>
 * Die Klasse beinhaltet die nur im Package sichtbare Methode {@link MelodyGenerator#generateMelodies(SChord[], ArrayList[][], SongConfig)}, welche die Melodien generiert.
 *
 * @see MusicGenerator Controller für diese Klasse
 */
public class MelodyGenerator {
	
	/**
	 * Minimum- und Maximum-Stufe.
	 * Entspricht nicht dem tatsächlichen, späteren Ton, da im MusicGenerator noch Oktaven drauf addiert werden.
	 */
	private static final int MIN = -5, MAX = 20;
	
	/**
	 * Generiert die Melodie für den Song.<br>
	 * Gibt eine 3-dimensionale Liste von SNoten, die bereits den übergebenen Rhythmus haben aus.<br>
	 * <br>
	 * Folgende Struktur liegt vor:<br>
	 * Die ganze Liste repräsentiert die Menge an Melodien (Nicht Instrument-Parts!!).<br>
	 * <b>Erste Ebene</b>: Einzelne Melodie; Enthält Menge an Akkordmelodien (s. unten).<br>
	 * <b>Zweite Ebene</b>: Einzelne Akkordmelodien. Eine Melodie besteht aus mehreren Akkorden. Während dieser Akkordmelodien werden mehrere Noten gespielt, die zur der Tonleiter dieses Akkordes gehören. 
	 * Diese Noten bilden eben diese Akkordmelodie. Am Anfang einer Akkordmelodie steht immer eine Note des Akkord-Dreiklangs. 
	 * Da eine Akkordmelodie unterschiedlich viele Noten beinhalten kann muss sie in einer ArrayList gespeichert werden.<br>
	 * <b>Dritte Ebene</b>: Die tatsächlichen Noten (Als SNote).<br>
	 * <br>
	 * Ablauf der Methode:<ol>
	 * <li>Für jede Akkordmelodie wird ein Anfangston generiert. Diese ist ein Ton des Akkord-Dreiklangs.<br>
	 * {@link MChord} und {@link MelodyGenerator#getNewTone(int, MChord, Random, SongConfig, int)} sollen erreichen, dass die Anfangstöne aus gleichen Akkorden, 
	 * aber unterschiedlichen Melodien möglichst einen Dreiklang bilden.</li>
	 * <li>Die jeweiligen Akkordmelodien werden generiert.<br>
	 * Als erstes wird der bereits generierte Anfangston gesetzt.<br>
	 * Dann die restlichen Töne. Diese müssen die Intervall-Regeln einhalten.</li>
	 * </ol>
	 * 
	 * @param key Der Grundakkord des Songs
	 * @param schords Die Akkorde des Songs (s. {@link ChordGenerator})
	 * @param rhythm Der Rhythmus des Songs (s. {@link RhythmGenerator})
	 * @param config Die Konfiguration des Songs (s. {@link SongConfig})
	 * @return Eine 3-dimensionale Liste von SNoten, die bereits den übergebenen Rhythmus haben. Siehe oben zur Struktur.
	 */
	static ArrayList<SNote>[][] generateMelodies( SChord key, SChord[] schords, ArrayList<Integer>[][] rhythm, SongConfig config ) {
		MChord[] chords = MChord.toMChords( schords );//SChord zu MChord
		
		@SuppressWarnings("unchecked")//Vorbereitung
		ArrayList<Integer>[][] melody = new ArrayList[ config.getMelodyNr() ][ schords.length ];//Diese Liste at die gleiche Struktur wie in der Methoden-Dokumentation beschrieben, nur mit Tönen statt Noten
		int[][] firstTones = new int[ config.getMelodyNr() ][ schords.length ];
		Random rand = new Random();
		
		int previous = RandomHelper.randFrom( chords[ 0 ].getAvailableTones(), rand );//Zufälliger vorheriger Ton (Wird nicht gespielt)
		
		//Generiert Anfangstöne aus Dreiklang des jeweiligen Akkords
		for( int m = 0; m < firstTones.length; m++ ) {
			for( int n = 0; n < firstTones[ m ].length; n++ ) {
				MChord chord = chords[ n ];
				int next = getNewTone( previous, chord, rand, config );//Neuer Anfangston
				firstTones[ m ][ n ] = next;
				previous = next;
			}
		}
		
		for( int m = 0; m < melody.length; m++ ) {
			for( int c = 0; c < melody[ m ].length; c++ ) {
//			###			Generiert Akkordmelodie			###
				ArrayList<Integer> chordMelody = new ArrayList<Integer>();//Liste zur Aufbewahrung der Akkordmelodie
				int next = c+1 >= chords.length ? ( m+1 >= melody.length ? firstTones[ 0 ][ 0 ] : firstTones[ m+1 ][ 0 ] ) : firstTones[ m ][ c+1 ];//Der Anfangston, welcher nach dieser Akkordmelodie gespielt wird
				
				//Setzt ersten Ton
				int firstNote = firstTones[ m ][ c ];//Anfangston
//				chords[ c ].used( firstNote );
				chordMelody.add( firstNote );
				
				int count = rhythm[ m ][ c ].size() -1;//count ist die Anzahl an folgenden Tönen die gesetzt werden sollen
				
				for( int n = 0; n < count; n++ ) {//Setzt folgende Töne
					int allowD = ( count - n ) * 5;//Die erlaubte Intervall-Distanz von dem nächsten Anfangston (Garantiert, dass alle Töne zwischen Anfangston und nächstem Anfangston die Intervall-Schritte einhalten)
					previous = chordMelody.get( chordMelody.size() -1 );//Der vorherige Ton
					int[] allowed = getAllowedTones( previous, key );//Die von den Intervallen her erlaubten Töne
					
					List<Integer> realAllow = new ArrayList<Integer>();
					for( int allow : allowed ) {//Berechnet mit allowD die tatsächlich erlaubten Töne
						if( Math.abs( allow - next ) <= allowD )
							realAllow.add( allow );
					}
					
					int posInScale = ChordHelper.getPositionOnScale( previous, key );//Position des vorherigen Tons auf der Tonleiter
					int[] three = new int[]{ 0, 2, 4 };//Diese Tonleiterpositionen gehören zu Dreiklängen
					int note = 0;//Der Ton
					
					if( !ArrayHelper.isIn( posInScale, three ) ) {//Wenn der vorherige Ton nicht zum Dreiklang gehört
						List<Integer> allowedInThree = new ArrayList<Integer>();
						for( int i = 0; i < realAllow.size(); i++ ) {//Werden nur Töne als nächstes erlaubt, die zum Dreiklang gehören
							if( ArrayHelper.isIn( realAllow.get( i ), three ) )
								allowedInThree.add( realAllow.get( i ) );
						}
						
						if( allowedInThree.size() == 0 )//Falls es keine Töne gibt, welche zum Dreiklang gehören, werden wieder normale ausgewählt
							note = realAllow.get( rand.nextInt( realAllow.size() ) );//Zufälliger Ton wird ausgewählt
						else//Nur Töne des Dreiklangs werden ausgewählt
							note = allowedInThree.get( rand.nextInt( allowedInThree.size() ) );//Zufälliger des Dreiklangs Ton wird ausgewählt
						
					} else//Ohne Dreiklang ist erlaubt
						note = realAllow.get( rand.nextInt( realAllow.size() ) );//Zufälliger Ton wird ausgewählt
					
					chordMelody.add( note );//wird zur Akkordmelodie hinzugefügt
				}
				
				previous = chordMelody.get( chordMelody.size() -1 );//Vorheriger Ton wird gesetzt
				melody[ m ][ c ] = chordMelody;//Akkordmelodie wird zur Melodie hinzugefügt
				
//				System.out.println( m + " " + c + " " + chordMelody );
				
			}//Ende Akkordmelodie-Generierung
		}
		
		@SuppressWarnings("unchecked")//Fügt zur Melodie den Rhythmus hinzu (Ton + Dauer = Note)
		ArrayList<SNote>[][] realMelody = new ArrayList[ melody.length ][ chords.length ];
		for( int m = 0; m < melody.length; m++ ) {
			for( int c = 0; c < chords.length; c++ ) {
				realMelody[ m ][ c ] = new ArrayList<SNote>();
				for( int n = 0; n < melody[ m ][ c ].size(); n++ )
					realMelody[ m ][ c ].add( new SNote( melody[ m ][ c ].get( n ), 4, rhythm[ m ][ c ].get( n ) ) );
			}
		}
		
		return realMelody;
	}
	
	/**
	 * Überladen mit {@link MelodyGenerator#getNewTone(int, MChord, Random, SongConfig, int)}.<br>
	 * Ruft <code>getNewTone(previous, chord, r, config, 0 )</code> auf (counter = 0).
	 * 
	 * @param previous Der vorherige Anfangston in Halbtonschritten
	 * @param chord Der Akkord der Akkordmelodie als {@link MChord}
	 * @param r {@link Random}
	 * @param config Die Song-Konfiguration für die Interval-Wahrscheinlichkeiten ({@link SongConfig})
	 * @return Ein Integer-Wert welcher den Ton repräsentiert (Halbtonschritte)
	 */
	private static int getNewTone( int previous, MChord chord, Random r, SongConfig config ) {
		return getNewTone(previous, chord, r, config, 0 );
	}
	
	/**
	 * Generiert eine neuen Anfangston.<br>
	 * Anfangstöne sind Töne aus dem Dreiklangs des jeweiligen Akkordes.<br>
	 * Anfangstöne eines Akkords in verschiedenen Melodien sollen wenn möglich unterschiedlich sein, damit sie eher einen Dreiklang bilden.<br>
	 * Deshalb wird hier {@link MChord} verwendet.<br>
	 * In dieser Klasse wird zu jedem Ton zusätzlich gespeichert, ob er bereits benutzt wurde ({@link MChord#used(int)}).<br>
	 * Benutzte Töne werden bei dem Aufruf {@link MChord#getAvailableTones()} nicht mit ausgegeben.<br>
	 * Durch {@link MChord#reset()} werden alle Töne auf unbenutzt gesetzt.
	 * 
	 * @param previous Der vorheriger Anfangston in Halbtonschritten
	 * @param chord Der Akkord der Akkordmelodie als {@link MChord}
	 * @param r {@link Random}
	 * @param config Die Song-Konfiguration für die Intervall-Wahrscheinlichkeiten ({@link SongConfig})
	 * @param counter Zähler der StackOverflow verhindert. Für Debug-Zwecke
	 * @return Ein Integer-Wert welcher den Ton repräsentiert (Halbtonschritte)
	 */
	private static int getNewTone( int previous, MChord chord, Random r, SongConfig config, int counter ) {
		int[] available = chord.getAvailableTones();//Alle verfügbaren Töne
		
		int[] tones = new int[ available.length * 3 ];//Nimmt zusätzlich noch die Töne eine Oktave höher/tiefer
		for( int i = 0; i < available.length; i++ ) {
			tones[ i ] = available[ i ] - 12;//Oktave tiefer
			tones[ i + available.length ] = available[ i ];//Gleich
			tones[ i + available.length * 2 ] = available[ i ] + 12;//Oktave höher
		}
		
		List<Integer> allowed_tones = new ArrayList<Integer>();//Töne die zwischen den MIN- und MAX-Werten liegen und ein erlaubtes Intervall zu dem vorherigen Ton haben
		for( int i = 0; i < tones.length; i++ ) {
			int interval = NoteHelper.getInterval( previous, tones[ i ] );//Gibt das Intervall zwischen den beiden Tönen zurück
			if( tones[ i ] >= MIN && tones[ i ] <= MAX && ArrayHelper.isIn( interval, new int[]{ -4, -2, -1, 0, 1, 2, 4 } ) )//Prüft
				allowed_tones.add( tones[ i ] );//Erlaubte Töne werden hinzugefügt
		}
		
		if( allowed_tones.size() == 0 && counter == 0 ) {//Wenn es keine erlaubten Töne für diesen Akkord mehr gibt wird MChord#reset() aufgerufen
			chord.reset();
			return getNewTone( previous, chord, r, config, 1 );
		} else if( allowed_tones.size() == 0 && counter > 0 )//Falls dies trotz Reset nochmals auftritt, wird eine NullPointerException geworfen, da kein Ton generiert werden kann.
			throw new NullPointerException( "Error: Could not generate Note (in MelodyGenerator.getNewNote)" );//Dies wird nur für Debug-Zwecke beibehalten, im lauffähigen Programm tritt dieser Fehler nie auf.
		
		List<PercentPair> percent_tones = new ArrayList<PercentPair>();//Vorbereitung für zufällige Auswahl.
		for( Integer tone : allowed_tones ) {
			float percent = 0;
			if( Math.abs( NoteHelper.getInterval( previous, tone ) ) == 0 )//Die verschiedenen Intervall-Wahrscheinlichkeiten werden gespeichert.
				percent = config.getFrstPercent();
			else if( Math.abs( NoteHelper.getInterval( previous, tone ) ) == 1 )
				percent = config.getScndPercent();
			else if( Math.abs( NoteHelper.getInterval( previous, tone ) ) == 2 )
				percent = config.getThrdPercent();
			else if( Math.abs( NoteHelper.getInterval( previous, tone ) ) == 4 )
				percent = config.getFfthPercent();
			percent_tones.add( new PercentPair( tone, percent ) );
		}
		
		PercentPair[] pn_array = percent_tones.toArray( new PercentPair[ percent_tones.size() ] );//List zu Array
		
		int tone = PercentPair.getRandomValue( pn_array, r );//Zufällige Auswahl eines Tons
		chord.used( tone % 12 );//Ton wird als 'benutzt' gesetzt, damit er nicht erneut verwendet wird
		
		return tone;
	}
	
	/**
	 * Berechnet die erlaubten Töne für diesen Akkord.<br>
	 * Einbezogen wird das Intervall zum vorherigen Ton, die MIN- und MAX-Werte und die Tonleiter des Akkords.<br>
	 * 
	 * @param previous Der vorher gespielte Ton als Halbtonschritt. Muss auf der Tonleiter des Akkords sein!
	 * @param chord Der momentane Akkord als {@link SChord}
	 * @return Ein int-Array mit den erlaubten Tönen als Halbtonschritte
	 */
	private static int[] getAllowedTones( int previous, SChord chord ) {
		int[] scale = ChordHelper.getScale( chord );//Die Tonleiter des Akkords
		
		int preS = ChordHelper.getPositionOnScale( previous, chord );//Speichert die Position des vorherigen Tones auf der Tonleiter des Akkords
		int octave = previous - scale[ preS ];//Der Oktave-Unterschied zwischen dem echten vorherigen Ton und dem gespeicherten
		
		int[] allowed_scale_tones = new int[]{//Die erlaubten Töne als Positionen auf der Tonleiter (Ganze Töne)
				preS -4,//Erlaubte Intervalle
				preS -2,
				preS -1,
				preS,
				preS +1,//Mehr Sekunden; Experimentell
				preS -1,
				preS +1,
				preS +2,
				preS +4
		};
		
		int[] steps = new int[ allowed_scale_tones.length ];//Rechnet die Töne wieder zu Halbtonschritten um
		for( int i = 0; i < allowed_scale_tones.length; i++ ) {
			if( allowed_scale_tones[ i ] < 0 )
				steps[ i ] = scale[ allowed_scale_tones[ i ] + scale.length ] - 12;
			else if( allowed_scale_tones[ i ] >= scale.length )
				steps[ i ] = scale[ allowed_scale_tones[ i ] - scale.length ] + 12;
			else
				steps[ i ] = scale[ allowed_scale_tones[ i ] ];
			
			steps[ i ] = steps[ i ] + octave;//Oktaven-Unterschied wird addiert
		}
		
		List<Integer> allowed = new ArrayList<Integer>();//Nur Töne zwischen MIN und MAX werden erlaubt
		for( int tone : steps ) {
			if( tone >= MIN && tone <= MAX )
				allowed.add( tone );
		}
		
		int[] allowedArr = ArrayHelper.toArray( allowed );//Konvertierung zum Array
		return allowedArr;
	}
}
