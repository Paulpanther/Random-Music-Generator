package de.lep.rmg.model;

import java.util.ArrayList;
import java.util.Random;

import de.lep.rmg.model.Measure.Clef;
import de.lep.rmg.model.helper.PercentPair;
import de.lep.rmg.model.helper.RandomHelper;
import de.lep.rmg.model.instruments.AcusticGuitar;
import de.lep.rmg.model.instruments.Flute;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.instruments.Piano;
import de.lep.rmg.model.notes.CType;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.model.notes.helper.ChordHelper;
import de.lep.rmg.out.midi.TrackFactory;
import de.lep.rmg.out.xml.XMLGenerator;

/**
 * Klasse speichert die Konfiguration des Songs.<br>
 * Manche Einstellungen werden durch die GUI oder andere Methoden noch verändert.<br>
 * <br>
 * Zu jeden Feld steht in '@category', in welchen Algorithmus sie verwendet wird (XML- oder MIDI-Felder ändern nichts am Musikgenerator):
 * XML: Wird in {@link XMLGenerator} verwendet.<br>
 * MIDI: Wird in {@link TrackFactory} verwendet.<br>
 * Musikgenerator: Wird beim Komponieren des Songs verwendet.
 */
public class SongConfig {

	/**
	 * Gibt einen relativen Wert an, mit welchem die Dauer berechnet wird.<br>
	 * Ein 4tel = 1*division, 8tel = 1/2*division, halb = 2*division, ...<br>
	 * Ist sozusagen die 'Schärfe' der Dauer, da keine float Werte für die Dauer erlaubt sind.
	 * @category XML, MIDI und Musikgenerator
	 */
	public static final int measureDivision = 8;
	
	/**
	 * Vorzeichen des Taktes.<br>
	 * Positiv: Kreuze, negativ: B
	 * @category XML und MIDI
	 */
	private int fifth = 0;
	
	/**
	 * Steht für die Taktart
	 * @category XML, MIDI und Musikgenerator
	 */
	private int beats = 4, beatType = 4;
	
	/**
	 * Gibt den Notenschlüssel an.
	 * @category XML
	 */
	private Clef clef = Clef.CLEF_G;//TODO In Instrument auslagern, da jedes Instrument anderen Notenschlüssel haben kann?
	
	/**
	 * Der Titel des Songs.<br>
	 * Kann in der GUI geändert werden.
	 * @category XML und MIDI
	 */
	private String title = "Untitled Song";
	
	/**
	 * Der Komponist des Songs.
	 * @category XML und MIDI
	 */
	private String composer = "RMG";
	
	/**
	 * Die Anzahl an Akkorden pro Melodie.<br>
	 * <code>chordNr * chordDuration</code> muss ein Vielfaches von <code>beats sein (Melodie darf nicht mitten im Takt aufhören)
	 * @category Musikgenerator
	 */
	private int chordNr = 4;
	
	/**
	 * Die Länge einer Akkordmelodie in Vierteln.<br>
	 * <code>chordNr * chordDuration</code> muss ein Vielfaches von 4 sein (Melodie muss n Takte lang sein)
	 * @category Musikgenerator
	 */
	private int chordDuration = 1;
	
	/**
	 * Wie oft die Melodiefolge wiederholt wird.
	 * @category Musikgenerator
	 */
	private int repeats = 1;
	
	/**
	 * Die Anzahl an Melodien und an Instrumenten.<br>
	 * Dieser Wert kann für den Musikgenerator verändert werden, falls allerdings die View verwendet wird nicht, da der Wert dort fest ist.
	 * @category Musikgenerator
	 */
	private int melodyNr = 2;
	
	/**
	 * Die Instrumente mit denen der Song abgespielt wird.<br>
	 * Werden zwar mit in {@link Song} gespeichert, werden aber nicht in der Musik-Generierung benutzt.<br>
	 * <code>melodyNr == instruments.length</code> muss True sein.
	 * @category (XML, MIDI) und Musikgenerator
	 */
	private Instrument[] instruments = { new Piano(), new Flute(), new AcusticGuitar() };
	
	/**
	 * Der Grundton des Songs.<br>
	 * Kann in der GUI verändert werden.
	 * @category Musikgenerator
	 */
	private SChord key = new SChord( SNote.C, CType.MAJOR );
	
	/**
	 * Speichert für jedes erlaubte Interval eine Wahrscheinlichkeit.<br>
	 * Die Summe der Wahrscheinlichkeiten muss 1 ergeben.<br>
	 * Es werden nur positive Intervalle oder 0 verwendet.
	 * @category Musikgenerator
	 */
	private ArrayList<PercentPair> intervals = new ArrayList<PercentPair>();
	
	/**
	 * Die Wahrscheinlichkeiten für die Länge von Dauern (Im XML-/MIDI-Format).<br>
	 * Der Array ist nach Notenlängen sortiert, so dass die kürzeste Notenlänge in noteDurations[0] steht.<br>
	 * Die Summe der Wahrscheinlichkeiten muss 1 sein
	 * @category Musikgenerator
	 */
	private PercentPair[] noteDurations = { 
			new PercentPair( SNote.EIGHTH, .3f ),
			new PercentPair( SNote.QUARTER, .3f ),
			new PercentPair( SNote.QUARTER_DOT, .15f ),
			new PercentPair( SNote.HALF, .25f )
	};
	
	/**
	 * Die Wahrscheinlichkeit im Rhythmus statt eines Tones eine Pause einzubauen.
	 * @category Musikgenerator
	 */
	private float restProbability = 0.1f;

	public SongConfig( int chordNr, int repeats, int melodyNr, int chordDuration, SChord key, Instrument[] instruments ) {
		this.chordNr = chordNr;
		this.repeats = repeats;
		this.melodyNr = melodyNr;
		this.chordDuration = chordDuration;
		if(chordDuration == 3){
			beats = 3;
			beatType = 3;
		}
		this.key = key;
		fifth = ChordHelper.getCircleOfFifthPos(key);
		if(instruments != null && instruments.length != 0){
			this.instruments = instruments;
		}
		alterProbabilities();
		printProbabilities();
	}
	
	public SongConfig(){
		alterProbabilities();
		printProbabilities();
	}
	
	/**
	 * Initializations-Methode, welche in jedem Konstruktor vorkommt.<br>
	 * Verändert zufällig folgende Werte:
	 * <ul>
	 * <li>Intervall-Wahrscheinlichkeiten.</li>
	 * <li>Dauer-Wahrscheinlichkeiten.</li>
	 * </ul>
	 */
	private void alterProbabilities(){
		Random rand = RandomHelper.getRandom();
		
		alterIntervalProbabilities(rand);
		alterDurationProbabilities(rand);
	}
	
	private void alterIntervalProbabilities(Random rand){
		Float[] intervalProbs = new Float[5];
		intervalProbs[0] = (float) ((rand.nextInt(25) + 2.5) / 100);//( 15 +- 12.5 )%
 		intervalProbs[1] = (float) ((rand.nextInt(40) + 5.0) / 100);//( 25 +- 20 )%
 		intervalProbs[2] = (float) ((rand.nextInt(35) + 5.0) / 100);//( 22,5 +- 17,5 )%
 		intervalProbs[3] = (float) ((rand.nextInt(25) + 2.5) / 100);//( 15 +- 12.5 )%
 		intervalProbs[4] = (float) ((rand.nextInt(35) + 5.0) / 100);//( 22,5 +- 17,5 )%
		float sum = 0;
		for(float percent : intervalProbs){
			sum += percent;
		}
		intervals = new ArrayList<PercentPair>();
		for(int i = 0; i < intervalProbs.length; i++){
			intervalProbs[i] /= sum;
			intervals.add(new PercentPair(i, intervalProbs[i]));
		}
	}
	
	private void alterDurationProbabilities(Random rand){
		//P(4)(8tel) ist 10 bis 50 Prozent
		noteDurations[0] = new PercentPair(noteDurations[0].getValue(), (rand.nextInt(30) + 15) / 100f);

		//P(8)(4tel ist 10 bis 50 Prozent
		noteDurations[1] = new PercentPair(noteDurations[1].getValue(), (rand.nextInt(30) + 15)/ 100f);

		//P(4) + P(8) muss größer als 40 Prozent und kleiner als 70 Prozent sein
		while( noteDurations[0].getPercent() + noteDurations[1].getPercent() < 0.4f ){
			noteDurations[0] = new PercentPair(noteDurations[0].getValue(), noteDurations[0].getPercent() + 0.05f);
			noteDurations[1] = new PercentPair(noteDurations[1].getValue(), noteDurations[1].getPercent() + 0.05f);
		}
		while( noteDurations[0].getPercent() + noteDurations[1].getPercent() > 0.7f ){
			noteDurations[0] = new PercentPair(noteDurations[0].getValue(), noteDurations[0].getPercent() - 0.05f);
			noteDurations[1] = new PercentPair(noteDurations[1].getValue(), noteDurations[1].getPercent() - 0.05f);
		}

		//P(12)(3/8tel) = 0.2 - (P(2) - P(6)); P(2) + P(6) + P(12) = 0.2
		noteDurations[2] = new PercentPair(noteDurations[2].getValue(), (rand.nextInt(20) + 5) / 100f);

		//P(16)(halbe) ist 20 bis 40 Prozent
		noteDurations[3] = new PercentPair(noteDurations[3].getValue(), 1f - noteDurations[0].getPercent() - noteDurations[1].getPercent() - noteDurations[2].getPercent());
	}
	
	void printProbabilities(){
		System.out.println("Intervalle: ");
		for(PercentPair percent : intervals) {
			System.out.println("Interval " + percent.getValue() + ": " + percent.getPercent());
		}
		System.out.println("\nNotenlängen: ");
		for(PercentPair percent : noteDurations) {
			System.out.println("Dauer " + percent.getValue() + ": " + percent.getPercent());
		}
		System.out.println("");
	}
	
	/*#############################################################################
	 * 						SETTER
	 *###########################################################################*/
	
	public void setTitle(String title){
		this.title = title;
	}
	
	/*#############################################################################
	 * 						GETTER
	 *###########################################################################*/
	
	public int getMeasureDivision() {
		return measureDivision;
	}

	public int getFifth() {
		return fifth;
	}

	public int getBeats() {
		return beats;
	}

	public int getBeatType() {
		return beatType;
	}

	public Clef getClef() {
		return clef;
	}

	public String getTitle() {
		return title;
	}

	public String getComposer() {
		return composer;
	}

	public int getChordNr() {
		return chordNr;
	}

	public int getRepeats() {
		return repeats;
	}

	public int getMelodyNr() {
		return melodyNr;
	}

	public SChord getKey() {
		return key;
	}
	
	public Instrument[] getInstruments() {
		return instruments;
	}
	
	public float getRestProbability(){
		return restProbability;
	}
	
//	public void readArgs() {
//		try {
//			File paramF = new File( "res/init/args" );
//			BufferedReader reader = new BufferedReader( new FileReader( paramF ) );
//			
//			chordNr = Integer.parseInt( reader.readLine() );
//			repeats = Integer.parseInt( reader.readLine() );
//			melodySameStart = Boolean.parseBoolean( reader.readLine() );
//			melodyNr = Integer.parseInt( reader.readLine() );
//			String keyS = reader.readLine();
//			
//			reader.close();
//			
//			if( keyS != null && keyS.length() == 3 ) {
//				char noteC = keyS.charAt( 0 );
//				char sharpC = keyS.charAt( 1 );
//				char typeC = keyS.charAt( 2 );
//				int note = 0;
//				CType type = null;
//				
//				if( noteC == 'C' && sharpC == '+' )
//					note = SNote.CIS;
//				else if( noteC == 'C' && sharpC == '-' )
//					note = SNote.C;
//				else if( noteC == 'D' && sharpC == '+' )
//					note = SNote.DIS;
//				else if( noteC == 'D' && sharpC == '-' )
//					note = SNote.D;
//				else if( noteC == 'E' )
//					note = SNote.E;
//				else if( noteC == 'F' && sharpC == '+' )
//					note = SNote.FIS;
//				else if( noteC == 'F' && sharpC == '-' )
//					note = SNote.F;
//				else if( noteC == 'G' && sharpC == '+' )
//					note = SNote.GIS;
//				else if( noteC == 'G' && sharpC == '-' )
//					note = SNote.G;
//				else if( noteC == 'A' && sharpC == '+' )
//					note = SNote.AIS;
//				else if( noteC == 'A' && sharpC == '-' )
//					note = SNote.A;
//				else if( noteC == 'H' || noteC == 'B' )
//					note = SNote.B;
//				else
//					return;
//				
//				if( typeC == 'D' )
//					type = CType.MAJOR;
//				else if( typeC == 'M' )
//					type = CType.MINOR;
//				else
//					return;
//				
//				key = new SChord( note, type );
//			}
//			
//			System.out.println( "Read init/args: " );
//			System.out.println( "  Number of Chords: " + chordNr );
//			System.out.println( "  Number of Repeats: " + repeats );
//			System.out.println( "  Melodys start at same time: " + melodySameStart );
//			System.out.println( "  Number of Melodys: " + melodyNr );
//			if( key == null )
//				System.out.println( "  Key: Undefinied" );
//			else
//				System.out.println( "  Key: " + key.toString() );
//			
//		} catch ( FileNotFoundException e ) {
//			System.out.println( "Could not parse init/args" );
//			e.printStackTrace();
//		} catch( NumberFormatException e ) {
//			System.out.println( "Could not parse init/args" );
//			e.printStackTrace();
//		} catch ( IOException e ) {
//			System.out.println( "Could not parse init/args" );
//			e.printStackTrace();
//		}
//	}

	public int getChordDuration() {
		return chordDuration;
	}

	public PercentPair[] getNoteDurations() {
		return PercentPair.clone( noteDurations );
	}
	
	public ArrayList<PercentPair> getIntervals() {
		return intervals;
	}
	
	/**
	 * Gibt das PercentPair zum angefragten Interval zurück, falls es existiert.<br>
	 * Negative Intervalle werden innerhalb der Methode wie positive behandelt und können ohne Einschränkungen übergeben werden.<br>
	 * Falls das gesuchte Interval nicht vorhanden ist, wird ein PercentPair mit Wahrscheinlichkeit 0 zurückgegeben.<br>
	 * @param interval - das gesuchte Interval
	 * @return Das PercentPair zum entsprechenden Interval, in dem Interval und Wahrscheinlichkeit gespeichert sind
	 */
	public PercentPair getInterval(int interval) {
		if(interval < 0){
			interval *= -1;
		}
		for(PercentPair pp: intervals){
			if(pp.getValue() == interval){
				return pp;
			}
		}
//		throw new NullPointerException("Interval " + interval + " not in this configs interval-list!");
		return new PercentPair(interval, 0f);
	}

	public static SongConfig getArgsInstance() {
		SongConfig config = new SongConfig();
//		config.readArgs();
		return config;
	}
}
