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
	
	IFugenMelodyGenerator melGen;
	MidiPlayer midiPlayer;
	FugenSubjects fugenSubjects;
	
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
		addSection(parts, fugenSubjects, config);
		if(config.getRepeats() > 1){
			//Modulation
			addModulation(parts, fugenSubjects, config);
			//zweite Durchführung
			addSection(parts, fugenSubjects, config);
		}
		if(config.getRepeats() > 2){
			//Modulation
			addModulation(parts, fugenSubjects, config);
			//Engführung
			addSection(parts, fugenSubjects, config);
		}
		//Modulation
		addModulation(parts, fugenSubjects, config);
		//Engführung
		addFinalSection(parts, fugenSubjects, config);
		//Schlusskadenz
		addFinalCadence(parts, config);
		
		Song song = new Song(config);song.addAll(parts);
		return song;
	}
	
	private FugenSubjects generateSubjects(SongConfig config){
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
	private void addSection(ArrayList<Part> parts, FugenSubjects fugenSubjects, SongConfig config){
		//speichert in welcher Reihenfolge die Stimmen zu spielen beginnen
		@SuppressWarnings("unchecked")//clone benötigt, da ursprüngliche Reihenfolge in parts erhalten bleiben muss
		ArrayList<Part> order = (ArrayList<Part>) parts.clone();//Objekte in order und parts sind die selben
		Collections.shuffle(order);
		//Intervalle der Stimmen zur Tonhöhe im FugenInfo
		ArrayList<Integer> intervals = intervals(parts, order);
		
		//falls dies der Anfang des Stücks ist, werden die untergeordneten Stimmen mit Pausen aufgefüllt
		if(parts.get(0).isEmpty()){
			Measure restMeasure = new Measure(config);
			restMeasure.add(new Rest(config.getMeasureDivision() * config.getBeats()));
			for(int partnr = 1; partnr < parts.size(); partnr++){//erster Part beginnt direkt mit Thema, daher partnr = 1
				Part part = order.get(partnr);
				for(int j = 0; j < (fugenSubjects.getSubjectPart().size()*partnr); j++){
					part.add(restMeasure);
				}
			}
		}else{//ansonsten werden freie Stimmen gespielt
			for(int partnr = 1; partnr < parts.size(); partnr++){//erster Part beginnt direkt mit Thema, daher partnr = 1
				Part part = order.get(partnr);
				ArrayList<INote> notes = melGen.generateSubVoice(config, fugenSubjects, partnr);
				MelodyHelper.transpone(notes, intervals.get(partnr), config.getKey());
				part.addAll(MelodyHelper.noteListToPart(config, notes, part.getInstrument()));
			}
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
		for(int partnr = 0; partnr < order.size() - 2; partnr++) {//letzte zwei Parts sind bereits fertig, daher partnr < parts.size() - 2 2
			Part part = order.get(partnr);
			int length = parts.size() - partnr - 2;//Länge der freien Stimme; -2 : gleicher Grund
			ArrayList<INote> notes = melGen.generateSubVoice(config, fugenSubjects, length);
			MelodyHelper.transpone(notes, intervals.get(partnr), config.getKey());
			part.addAll(MelodyHelper.noteListToPart(config, notes, part.getInstrument()));
		}
		
	}
	
	/**
	 * Fügt den Parts eine Modulation an.
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenSubjects - {@link FugenSubjects} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	private void addModulation(List<Part> parts, FugenSubjects fugenSubjects, SongConfig config){
		
		//TODO
		
	}
	
	/**
	 * Fügt den Parts eine Engführung an. Der erste, dritte und fünfte Part, spielen den Dux, die restlichen den Comes.
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenSubjects - {@link FugenSubjects} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	private void addFinalSection(ArrayList<Part> parts, FugenSubjects fugenSubjects, SongConfig config){
		int measureDuration = config.getBeats() * config.getMeasureDivision();
		boolean halfMeasure = false;
		if(fugenSubjects.getSubjectPart().size() % 2 == 1){//falls das Thema eine ungerade Taktanzahl hat
			halfMeasure = true;
		}
		//speichert in welcher Reihenfolge die Stimmen zu spielen beginnen
		@SuppressWarnings("unchecked")//clone benötigt, da ursprüngliche Reihenfolge in parts erhalten bleiben muss
		ArrayList<Part> order = (ArrayList<Part>) parts.clone();//Objekte in order und parts sind die selben
		Collections.shuffle(order);
		//Intervalle der Stimmen zur Tonhöhe im FugenInfo
		ArrayList<Integer> intervals = intervals(parts, order);
		
		for(int partnr = 0; partnr < parts.size(); partnr++){
			Part part = order.get(partnr);
			ArrayList<INote> notes = new ArrayList<INote>();
			if(halfMeasure && partnr % 2 == 1){
				if(partnr != 0){
					//erste freie Stimmen hinzufügen
					notes = melGen.generateSubVoice(config, fugenSubjects, (partnr + 1) / 2);//überlagernder Themeneinsatz
					//TODO funktioniert so nicht, Fehler evtl. in MelodyHelper#subNoteList
					notes = MelodyHelper.subNoteList(notes, notes.size() * measureDuration - measureDuration / 2, false);
				}
				//Thema hinzufügen
				notes.addAll(fugenSubjects.getSubjectList());
				if(partnr != parts.size() - 1){
					//zweite freie Stimme hinzufügen
					ArrayList<INote> subVoice = melGen.generateSubVoice(config, fugenSubjects, (parts.size() - partnr - 1) / 2);
					subVoice = MelodyHelper.subNoteList(subVoice, subVoice.size() * measureDuration 
							- measureDuration / 2, true);
					notes.addAll(subVoice);
				}
				//TODO mit Pausen auffüllen
			}else{
				if(partnr != 0){
					//erste freie Stimmen hinzufügen
					notes = melGen.generateSubVoice(config, fugenSubjects, (partnr + 1) / 2);//überlagernder Themeneinsatz
				}
				//Thema hinzufügen
				notes.addAll(fugenSubjects.getSubjectList());
				if(partnr != parts.size() - 1){
					//zweite freie Stimme hinzufügen
					notes.addAll(melGen.generateSubVoice(config, fugenSubjects, (parts.size() - partnr - 1) / 2));
				}
				//TODO mit Pausen auffüllen
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
	
	private ArrayList<Integer> intervals(List<Part> parts, List<Part> order){
		ArrayList<Integer> intervals = new ArrayList<Integer>();
		for( int partnr = 0; partnr < order.size(); partnr++ ){
			Part part1 = order.get(partnr);
			int pitch = 0;
			for(int index = 0; index < parts.size(); index++){
				Part part2 = parts.get(index);
				if(part1 == part2){
					pitch = index;
				}
			}
			int interval = 7 * ((int)(pitch + 1)/2);//bei 1/2; 3/2; ... wird abgerundet auf 0; 1, ...
			if(partnr % 2 == 0){//Tonikaeinsatz; Dux
				intervals.add(interval);
			}else{//Dominanteinsatz; Comes
				intervals.add(interval - 3);
			}
		}
		return intervals;
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
