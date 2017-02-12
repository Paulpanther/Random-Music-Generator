package de.lep.rmg.model;

import java.util.Random;

import de.lep.rmg.model.Measure.Clef;
import de.lep.rmg.model.helper.PercentPair;
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
 * Musikgenerator: Wird bei der Komposition des Songs verwendet.
 */
public class SongConfig {

	/**
	 * Gibt einen relativen Wert an, mit welchem die Dauer berechnet wird.<br>
	 * Ein 4tel = 1*division, 8tel = 1/2*division, halb = 2*division, ...<br>
	 * Ist sozusagen die 'Schärfe' der Dauer, da keine float Werte für die Dauer erlaubt sind.
	 * @category XML und MIDI
	 */
	private final int measureDivision = 8;
	
	/**
	 * Vorzeichen des Taktes.<br>
	 * Positiv: Kreuze, negativ: B
	 * @category XML und MIDI
	 */
	private int fifth = 0;
	
	/**
	 * Steht für die Taktart.<br>
	 * Hat momentan noch keine Auswirkung im Musikgenerator, was noch geändert werden sollte.
	 * @category XML und MIDI
	 */
	private int beats = 4, beatType = 4;//TODO Baue Taktart in Musikgenerator ein
	
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
	 * <code>chordNr * chordDuration</code> muss ein Vielfaches von 4 sein (Melodie muss n Takte lang sein)
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
	 * @category XML und MIDI
	 */
	private Instrument[] instruments = { new Piano(), new Flute(), new AcusticGuitar() };
	
	/**
	 * Der Grundton des Songs.<br>
	 * Kann in der GUI verändert werden.
	 * @category Musikgenerator
	 */
	private SChord key = new SChord( SNote.C, CType.MAJOR );
	
	/**
	 * Die Wahrscheinlichkeiten für diese Intervall zwischen zwei aufeinander folgenden Tönen.<br>
	 * Die Summe muss 1 sein
	 * @category Musikgenerator
	 */
	private float frstPercent = .2f, scndPercent = .35f, thrdPercent = .3f, ffthPercent = .15f;
	
	/**
	 * Die Wahrscheinlichkeiten für die Länge von Dauern (Im XML-/MIDI-Format).<br>
	 * Die Summe muss 1 sein
	 * @category Musikgenerator
	 */
	private PercentPair[] noteDurations = { 
			new PercentPair( 4, .3f ),//8tel
			new PercentPair( 8, .3f ),//4tel
			new PercentPair( 12, .15f ),//4tel punktiert
			new PercentPair( 16, .25f )//halbe
	};
	

	public SongConfig( int chordNr, int repeats, int melodyNr, int chordDuration, SChord key, Instrument[] instruments ) {
		this.chordNr = chordNr;
		this.repeats = repeats;
		this.melodyNr = melodyNr;
		this.chordDuration = chordDuration;
		this.key = key;
		fifth = ChordHelper.getCircleOfFifthPos(key);
		if(instruments != null && instruments.length != 0){
			this.instruments = instruments;
		}
		alterProbabilities();
	}
	
	public SongConfig(){
		alterProbabilities();
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
		Random rand = new Random();
		
//		###		setzt zufällige Intervall-Wahrscheinlichkeiten		###
		
		frstPercent = (rand.nextInt(30) + 5) / 100f;
		scndPercent = (rand.nextInt(30) + 20) / 100f;
		
		//P(0)(Prime) + P(1)(Sekunde) muss mindestens 0.44 betragen und kann höchstens 0.85 sein
		if(frstPercent + scndPercent < 44){
			scndPercent = 0.44f - frstPercent;
		}
		thrdPercent = (rand.nextInt(40) + 10) / 100f;
		
		//P(0) + P(1) + P(2)(Terz) darf maximal 0.98 betragen
		if(thrdPercent + scndPercent + frstPercent > 0.98){
			thrdPercent = 0.98f - scndPercent - frstPercent;
		}
		//P(5)(Quinte) ergänzt die anderen Wahrscheinlichkeiten zu 1
		ffthPercent = 1 - frstPercent - scndPercent - thrdPercent;
		
		//schreibt Ergebnisse in Konsole
		System.out.println("Intervalle:");
		System.out.println("Prime : " + frstPercent);
		System.out.println("Sekunde : " + scndPercent);
		System.out.println("Terz : " + thrdPercent);
		System.out.println("Quinte : " + ffthPercent + "\n");
		
//		###			zufällige Dauer-Wahrscheinlichkeiten			###
		
		//P(4)(8tel) ist 10 bis 50 Prozent
		noteDurations[0] = new PercentPair(4, (rand.nextInt(30) + 15) / 100f);
		
		//P(8)(4tel ist 10 bis 50 Prozent
		noteDurations[1] = new PercentPair(8, (rand.nextInt(30) + 15)/ 100f);
		
		//P(4) + P(8) muss größer als 40 Prozent und kleiner als 70 Prozent sein
		while( noteDurations[0].getPercent() + noteDurations[1].getPercent() < 0.4f ){
			noteDurations[0] = new PercentPair(4, noteDurations[0].getPercent() + 0.05f);
			noteDurations[1] = new PercentPair(8, noteDurations[1].getPercent() + 0.05f);
		}
		while( noteDurations[0].getPercent() + noteDurations[1].getPercent() > 0.7f ){
			noteDurations[0] = new PercentPair(4, noteDurations[0].getPercent() - 0.05f);
			noteDurations[1] = new PercentPair(8, noteDurations[1].getPercent() - 0.05f);
		}
		
		//P(12)(3/8tel) = 0.2 - (P(2) - P(6)); P(2) + P(6) + P(12) = 0.2
		noteDurations[2] = new PercentPair(12, (rand.nextInt(20) + 5) / 100f);
		
		//P(16)(halbe) ist 20 bis 40 Prozent
		noteDurations[3] = new PercentPair(16, 1f - noteDurations[0].getPercent() - noteDurations[1].getPercent() - noteDurations[2].getPercent());
		
		//schreibt Ergebnisse in Konsole
		System.out.println("Notenlängen:");
		for(int i = 0; i < noteDurations.length; i++){
			System.out.println(noteDurations[i].getValue() + " : " + noteDurations[i].getPercent());
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
	
	public float getFrstPercent() {
		return frstPercent;
	}

	public float getScndPercent() {
		return scndPercent;
	}

	public float getThrdPercent() {
		return thrdPercent;
	}

	public float getFfthPercent() {
		return ffthPercent;
	}

	public int getChordDuration() {
		return chordDuration;
	}

	public PercentPair[] getNoteDurations() {
		return PercentPair.clone( noteDurations );
	}

	public static SongConfig getArgsInstance() {
		SongConfig config = new SongConfig();
//		config.readArgs();
		return config;
	}
}
