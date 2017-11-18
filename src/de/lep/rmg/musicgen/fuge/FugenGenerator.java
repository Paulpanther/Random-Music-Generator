package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.lep.rmg.model.Measure;
import de.lep.rmg.model.Part;
import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.helper.RandomHelper;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.model.notes.Rest;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.musicgen.RhythmGenerator;
import de.lep.rmg.musicgen.helper.MelodyHelper;
import de.lep.rmg.out.midi.MidiPlayer;
import de.lep.rmg.view.panels.ControllPanel;
import de.lep.rmg.view.panels.FugenControllPanel;

public class FugenGenerator implements IMusicGenerator {
	
	private IFugenMelodyGenerator melGen;
	private MidiPlayer midiPlayer;
	/*private*/ FugenSubjects fugenSubjects;//private disabled for testing
	
	public FugenGenerator( MidiPlayer midiPlayer, IFugenMelodyGenerator FMG ) {
		melGen = FMG;
		this.midiPlayer = midiPlayer;
	}
	
	/**
	 * Generiert eine Fuge auf Basis des {@link SongConfig}s.<br>
	 * Die Anzahl der Stimmen der Fuge legt sich aus der Länge des Instrumenten-Arrays im SongConfig fest,
	 * wobei es immer mindestens zwei und höchstens sechs Stimmen gibt. Falls unterschiedliche Instrumente
	 * gewählt wurden, werden sie auch spielen, auch wenn dieses Verhalten untypisch für Fugen ist.<br>
	 * Thema und Antithema werden vom {@link FugenMelodyGenerator} nach den Angaben des SongConfigs erstellt.<br>
	 * Abhängig von der Einstellung der SongConfig#repeats werden bis zu drei, von Modulationen getrennte,
	 * Durchführungen erstellt, die am Schluss durch eine Engführung und eine abschließende Kadenz vollendet.<br>
	 * 
	 * @param config - {@link SongConfig} für diese Fuge
	 * @return die fertig genrierte Fuge als {@link Song}
	 */
	@Override
	public Song generateSong(SongConfig config) {
		fugenSubjects = generateSubjects(config);
		Instrument instrument = config.getInstruments()[0];
		
		//lege die Stimmen der Fuge als verschiedene Parts an
		ArrayList<Part> parts = new ArrayList<Part>();
		for(int i = 0; i < fugenSubjects.getVoices(); i++){
			try{
				parts.add(new Part(config.getInstruments()[i]));
			}catch (ArrayIndexOutOfBoundsException aE){
				parts.add(new Part(instrument));
			}
		}
		
		//Exposition, erste Durchführung
		addSection(parts, config);
		if(config.getRepeats() > 1){
			//Modulation
			addModulation(parts, config);
			//zweite Durchführung
			addSection(parts, config);
		}
		if(config.getRepeats() > 2){
			//Modulation
			addModulation(parts, config);
			//Engführung
			addSection(parts, config);
		}
		//Modulation
		addModulation(parts, config);
		//Engführung
		addFinalSection(parts, config);
		//Schlusskadenz
		addFinalCadence(parts, config);
		
		Song song = new Song(config);song.addAll(parts);
		return song;
	}
	
	/**
	 * 
	 * @param config
	 * @return
	 */
	/*private*/ FugenSubjects generateSubjects(SongConfig config){ //private disabled for testing
		Instrument instrument = config.getInstruments()[0];
		int voices = config.getInstruments().length;//Anzahl der Stimmen, die die Fuge spielen
		if( voices < 2)//mindestens 2, maximal 6 Stimmen, üblich sind 3 oder 4
			voices = 2;
		else
			if( voices > 6)
			voices = 6;
		
		//generiere Rhythmus für Hauptmotiv
		ArrayList<INote> rhythm = RhythmGenerator.generateMotif(config, 1);//1: höhere Werte nur für freie Stimmen
		//generiere Thema und Gegenthema
		ArrayList<INote> themeList = melGen.generateSubject(config, rhythm);//Das Hauptthema
		ArrayList <INote> antiThemeList = melGen.generateAntiSubject(config, themeList);//Das Gegenthema
		Part themePart = MelodyHelper.noteListToPart(config, themeList, instrument);
		Part antiThemePart = MelodyHelper.noteListToPart(config, antiThemeList, instrument);
		//erstelle FugenInfo-Objekt zum bündeln der Informationen
		FugenSubjects fugenSubjects = new FugenSubjects(themeList, antiThemeList, themePart, antiThemePart, voices);
		return fugenSubjects;
	}
	
	/**
	 * Fügt den Parts eine Durchführung an. Der erste Part spielt die tiefste Stimme, der letzte Part die höchste.
	 * Der erste Part spielt in der Originaltonhöhe aus dem FugenInfo, falls er einen Dux spielt und eine Quarte
	 * tiefer, falls er einen Comes spielt. Alle anderen spielen höher.
	 * 
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenSubjects - {@link FugenSubjects} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	/*private*/ void addSection(ArrayList<Part> parts, SongConfig config){ //private disabled for testing
		//speichert in welcher Reihenfolge die Stimmen zu spielen beginnen
		@SuppressWarnings("unchecked")//clone benötigt, da ursprüngliche Reihenfolge in parts erhalten bleiben muss
		ArrayList<Part> order = (ArrayList<Part>) parts.clone();//Objekte in order und parts sind die selben
		Collections.shuffle(order);
		//Intervalle der Stimmen zur Tonhöhe im FugenInfo
		ArrayList<Integer> intervals = intervals(parts, order);
		
		//falls dies der Anfang des Stücks ist, werden die untergeordneten Stimmen mit Pausen aufgefüllt
		if(parts.get(0).isEmpty()){
			fillWithRests(order, fugenSubjects.getSubjectPart().size(), config);
		}else{//ansonsten werden freie Stimmen gespielt
			fillSubVoices(order, intervals, config);
		}
		//Thema und Gegenthema hinzufügen
		for( int partnr = 0; partnr < order.size(); partnr++ ) {
			Part part = order.get(partnr);
			ArrayList<INote> subject = fugenSubjects.getSubjectList();
			ArrayList<INote> subTransposition = MelodyHelper.transpone(subject, intervals.get(partnr), config.getKey());
			part.addAll(MelodyHelper.noteListToPart(config, subTransposition, part.getInstrument()));
			//falls es nicht der letzte Part ist, wird auch das Gegenthema hinzugefügt
			if(part != order.get(order.size() - 1)){
				ArrayList<INote> antiSubject = fugenSubjects.getAntiSubjectList();
				ArrayList<INote> antiTransposition = MelodyHelper.transpone(antiSubject, intervals.get(partnr), config.getKey());
				part.addAll(MelodyHelper.noteListToPart(config, antiTransposition, part.getInstrument()));
			}
		}
		// Rest mit freien Stimmen auffüllen
		fillSubVoicesReverseOrder(order, intervals, config);
	}
	
	/**
	 * Fügt den Parts eine Modulation an.
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenSubjects - {@link FugenSubjects} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	/*private*/ void addModulation(List<Part> parts, SongConfig config){ //private disabled for testing
		
		//TODO
		
	}
	
	/**
	 * Fügt den Parts eine Engführung an. Der erste, dritte und fünfte Part, spielen den Dux, die restlichen den Comes.
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenSubjects - {@link FugenSubjects} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	/*private*/ void addFinalSection(ArrayList<Part> parts, SongConfig config){//private disabled for testing
		int measureDuration = config.getBeats() * config.getMeasureDivision();
		//speichert in welcher Reihenfolge die Stimmen zu spielen beginnen
		@SuppressWarnings("unchecked")//clone benötigt, da ursprüngliche Reihenfolge in parts erhalten bleiben muss
		ArrayList<Part> order = (ArrayList<Part>) parts.clone();//Objekte in order und parts sind die selben
		Collections.shuffle(order);
		//Intervalle der Stimmen zur Tonhöhe im FugenInfo
		ArrayList<Integer> intervals = intervals(parts, order);
		
		for(int partnr = 0; partnr < parts.size(); partnr++){
			Part part = order.get(partnr);
			ArrayList<INote> notes = new ArrayList<INote>();
			//erste freie Stimmen hinzufügen
			notes = melGen.generateSubVoice(config, fugenSubjects, partnr);//überlagernder Themeneinsatz
			notes = MelodyHelper.subNoteList(notes, config.getChordNr() * measureDuration / 2, false);
			//Thema hinzufügen
			notes.addAll(fugenSubjects.getSubjectList());
			if(parts.size() - 1 > partnr){
				//zweite freie Stimme hinzufügen
				ArrayList<INote> subVoice = melGen.generateSubVoice(config, fugenSubjects, (parts.size() - 1 - partnr));
				subVoice = MelodyHelper.subNoteList(subVoice, config.getChordNr() * measureDuration / 2, true);
			}
			//transponieren und Part hinzufügen
			notes = MelodyHelper.transpone(notes, intervals.get(partnr), config.getKey());
			part.addAll(MelodyHelper.noteListToPart(config, notes, part.getInstrument()));
		}

	}
	
	private void addFinalCadence(List<Part> parts, SongConfig config){
		ArrayList<Integer> intervals = intervals(parts, parts);
		boolean[] parallelFunction = new boolean[4];
		for(int i = 0; i < parallelFunction.length; i++){
			parallelFunction[i] = RandomHelper.getRandom().nextBoolean();
		}
		for(int partnr = 0; partnr < parts.size(); partnr++){
			ArrayList<INote> notes = new ArrayList<INote>();
			for(int notenr = 0; notenr < parallelFunction.length; notenr++){
				int tone = config.getKey().getKeynote();
				switch(notenr){
				case 1: tone += 4;
				case 2: tone += 5;
				default: break;
				}
				if(parallelFunction[notenr]){
					notes.add(new SNote(tone - 2, melGen.getStandardOctave(), SNote.QUARTER));
				}else{
					notes.add(new SNote(tone, melGen.getStandardOctave(), SNote.QUARTER));
				}
			}
			notes = MelodyHelper.transpone(notes, intervals.get(partnr), config.getKey());
			parts.get(partnr).addAll(MelodyHelper.noteListToPart(config, notes, parts.get(partnr).getInstrument()));
		}
	}
	
	/**
	 * Berechnet für jeden Part, um welches Interval er transponiert werden muss.
	 * Jeder Part der in 'order' einen geraden Index hat spielt einen Tonikaeinsatz
	 * und erhält ein durch 7 teilbares Interval.
	 * Jeder Part, der in 'order' einen ungeraden Index hat spielt einen Dominanteinsatz
	 * und erhält ein durch 7 teilbares Interval + 4 (oder -3).
	 * Dabei kommt kein vergebenes Interval doppelt vor und alle Intervalle einer Einsatzgruppe
	 * sind nach der Position des Parts in 'parts' sortiert.
	 * Die Methode setzt voraus, dass 'parts' und 'order' die selben Element enthalten,
	 * wenn auch nicht unbedingt in der selben Reihenfolge.
	 * @param parts - Parts soritert nach Tonhöhe von tief nach hoch
	 * @param order - gleiche Parts sortiert nach Reihenfolge des Einsatzes
	 * @return Intervalle in Reihenfolge des Einsatzes,
	 * um die die Parts transponiert werden müssen um die passende Tonhöhe zu erhalten
	 */
	/*private*/ ArrayList<Integer> intervals(List<Part> parts, List<Part> order){ //private disabled for testing
		ArrayList<Integer> intervals = new ArrayList<Integer>();
		ArrayList<Integer> duxInt = new ArrayList<Integer>();
		ArrayList<Integer> comesInt = new ArrayList<Integer>();
		ArrayList<Part> dux = new ArrayList<Part>();
		ArrayList<Part> comes = new ArrayList<Part>();
		for( int partnr = 0; partnr < order.size(); partnr++ ){
			if(partnr % 2 == 0){//Tonikaeinsatz
				dux.add(order.get(partnr));
			}else{//Dominanteinsatz
				comes.add(order.get(partnr));
			}
		}
		int dim = 0;
		for( int partnr = 0; partnr < parts.size(); partnr++){
			Part part = parts.get(partnr);
			for( int pitch = 0; pitch < dux.size(); pitch++){
				if(part == dux.get(pitch)){
					duxInt.add(pitch*7);
				}
			}
			for( int pitch = 0; pitch < comes.size(); pitch++){
				if(part == comes.get(pitch)){
					if(partnr == 0 && dux.size() == comes.size()){
						dim = 7;
					}
					comesInt.add(pitch*7 + 4 - dim);
				}
			}
		}
		for(int index = 0; index < dux.size(); index++){
			intervals.add(duxInt.get(index));
			if(index < comesInt.size()){
				intervals.add(comesInt.get(index));
			}
		}
		return intervals;
	}
	
	/**
	 * Die {@link Part}s werden mit Takten, die nur Pausen enthalten, in der
	 * Anzahl entsprechend ihres Index' in der Liste * {@code measures} erweitert.
	 * Das heißt, dass der erste Part unverändert bleibt.
	 * Die Methode kann nur ganze Takte anfügen undgeht davon aus,
	 * dass der letzte Takt abgeschlossen
	 * ist. Sie wird immer mit einem neuen Takt beginnen.
	 * @param partorder - Die mit Pausen zu füllenden {@link Part}s in Reihenfolge
	 * @param measures - Anzahl der Pausentakte die angefügt werden sollen
	 * @param config - ein {@link SongConfig}
	 */
	private void fillWithRests(List<Part> partorder, int measures, SongConfig config){
		Measure restMeasure = new Measure(config);
		restMeasure.add(new Rest(config.getMeasureDivision() * config.getBeats()));
		for(int partnr = 1; partnr < partorder.size(); partnr++){//erster Part beginnt direkt mit Thema, daher partnr = 1
			Part part = partorder.get(partnr);
			for(int j = 0; j < measures*partnr; j++){
				part.add(restMeasure);
			}
		}
	}
	
	/**
	 * Die Parts werden mit untergeordneten Stimmen erweitert.
	 * Der erste Part bleibt dabei unverändert. Der zweite erhält
	 * so viele neue Takte, wie das Hauptthema hat. Der Dritte zweimal
	 * soviele, der Vierte dreimal soviele und so weiter.
	 * @param partorder - die mit untergeordenten Stimmen zu füllenden {@link Part}s in Reihenfolge
	 * @param transponationIntervals - Die Intervalle, um die die verschiedenen Stimmen transponiert werden sollen
	 * @param config - ein {@link SongConfig}
	 */
	private void fillSubVoices(List<Part> partorder, List<Integer> transponationIntervals, SongConfig config){
		for(int partnr = 1; partnr < partorder.size(); partnr++){//erster Part beginnt direkt mit Thema, daher partnr = 1
			Part part = partorder.get(partnr);
			ArrayList<INote> notes = melGen.generateSubVoice(config, fugenSubjects, partnr);
			MelodyHelper.transpone(notes, transponationIntervals.get(partnr), config.getKey());
			part.addAll(MelodyHelper.noteListToPart(config, notes, part.getInstrument()));
		}
	}
	
	/**
	 * Die Parts werden mit untergeordneten Stimmen erweitert.
	 * Die letzten beiden Part bleibt dabei unverändert. Der vorvorletzte Part erhält
	 * so viele neue Takte, wie das Hauptthema hat. Der davor zweimal
	 * soviele, der davor dreimal soviele und so weiter.
	 * @param partorder - die mit untergeordenten Stimmen zu füllenden {@link Part}s in Reihenfolge
	 * @param transponationIntervals - Die Intervalle, um die die verschiedenen Stimmen transponiert werden sollen
	 * @param config - ein {@link SongConfig}
	 */
	private void fillSubVoicesReverseOrder(List<Part> partorder, List<Integer> transponationIntervals, SongConfig config){
		for(int partnr = 0; partnr < partorder.size() - 2; partnr++) {//letzte zwei Parts sind bereits fertig, daher partnr < parts.size() - 2 2
			Part part = partorder.get(partnr);
			int length = partorder.size() - partnr - 2;//Länge der freien Stimme; -2 : gleicher Grund
			ArrayList<INote> notes = melGen.generateSubVoice(config, fugenSubjects, length);
			MelodyHelper.transpone(notes, transponationIntervals.get(partnr), config.getKey());
			part.addAll(MelodyHelper.noteListToPart(config, notes, part.getInstrument()));
		}
	}
	
	@Override
	public String getSongType() {
		return "Fuge";
	}

	@Override
	public String getGeneratorName() {
		return "Fuge";
	}

	@Override
	public ControllPanel getGeneratorPanel() {
		return new FugenControllPanel(this, midiPlayer);
	}

}
