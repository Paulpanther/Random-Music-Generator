package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.lep.rmg.model.Measure;
import de.lep.rmg.model.Part;
import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.model.notes.Rest;
import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.musicgen.RhythmGenerator;
import de.lep.rmg.musicgen.helper.MelodyHelper;
import de.lep.rmg.out.midi.MidiPlayer;
import de.lep.rmg.view.panels.ControllPanel;
import de.lep.rmg.view.panels.FugenControllPanel;

public class FugenGenerator implements IMusicGenerator {
	
	IFugenMelodyGenerator melGen;
	MidiPlayer midiPlayer;
	
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
		FugenInfo fugenInfo = new FugenInfo(themeList, antiThemeList, themePart, antiThemePart, voices);
		
		//lege die Stimmen der Fuge als verschiedene Parts an
		ArrayList<Part> partList = new ArrayList<Part>();
		for(int i = 0; i < voices; i++){
			try{
				partList.add(new Part(config.getInstruments()[i]));
			}catch (ArrayIndexOutOfBoundsException aE){
				partList.add(new Part(instrument));
			}
		}
		
		//Exposition, erste Durchführung
		addSection(partList, fugenInfo, config);
		if(config.getRepeats() > 1){
			//Modulation
			addModulation(partList, fugenInfo, config);
			//zweite Durchführung
			addSection(partList, fugenInfo, config);
		}
		if(config.getRepeats() > 2){
			//Modulation
			addModulation(partList, fugenInfo, config);
			//Engführung
			addSection(partList, fugenInfo, config);
		}
		//Modulation
		addModulation(partList, fugenInfo, config);
		//Engführung
		addFinalSection(partList, fugenInfo, config);
		//TODO Schlusskadenz
		
		
		Song song = new Song(config);song.addAll(partList);
		return song;
	}
	
	/**
	 * Fügt den Parts eine Durchführung an. Der erste Part spielt die tiefste Stimme, der letzte Part die höchste.
	 * Der erste Part spielt in der Originaltonhöhe aus dem FugenInfo, falls er einen Dux spielt und eine Quarte
	 * tiefer, falls er einen Comes spielt. Alle anderen spielen höher.
	 * 
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenInfo - {@link FugenInfo} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	private void addSection(ArrayList<Part> parts, FugenInfo fugenInfo, SongConfig config){
		//speichert in welcher Reihenfolge die Stimmen zu spielen beginnen
		@SuppressWarnings("unchecked")//clone benötigt, da ursprüngliche Reihenfolge in parts erhalten bleiben muss
		ArrayList<Part> order = (ArrayList<Part>) parts.clone();//Objekte in order und parts sind die selben
		Collections.shuffle(order);
		//Intervalle der Stimmen zur Tonhöhe im FugenInfo
		ArrayList<Integer> intervals = new ArrayList<Integer>();
		for( int partnr = 0; partnr < order.size(); partnr++ ){
			int pitch = parts.indexOf(order.get(partnr));
			int interval = 7 * pitch/2;//bei 1/2; 3/2; ... wird abgerundet auf 0; 1, ...
			if(partnr % 2 == 0){//Tonikeeinsatz; Dux
				intervals.add(interval);
			}else{//Dominanteinsatz; Comes
				intervals.add(interval - 3);
			}
		}
		
		//falls dies der Anfang des Stücks ist, werden die untergeordneten Stimmen mit Pausen aufgefüllt
		if(parts.get(0).isEmpty()){
			Measure restMeasure = new Measure(config);
			restMeasure.add(new Rest(config.getMeasureDivision() * config.getBeats()));
			for(int partnr = 1; partnr < parts.size(); partnr++){//erster Part beginnt direkt mit Thema, daher partnr = 1
				Part part = order.get(partnr);
				for(int j = 0; j < (fugenInfo.getSubjectPart().size()*partnr); j++){
					part.add(restMeasure);
				}
			}
		}else{//ansonsten werden freie Stimmen gespielt
			for(int partnr = 1; partnr < parts.size(); partnr++){//erster Part beginnt direkt mit Thema, daher partnr = 1
				Part part = order.get(partnr);
				ArrayList<INote> notes = melGen.generateSubVoice(config, fugenInfo, partnr);
				MelodyHelper.transpone(notes, intervals.get(partnr), config.getKey());
				part.addAll(MelodyHelper.noteListToPart(config, notes, part.getInstrument()));
			}
		}
		//Thema und Gegenthema hinzufügen
		for( int partnr = 0; partnr < order.size(); partnr++ ) {
			Part part = order.get(partnr);
			ArrayList<INote> subject = fugenInfo.getSubjectList();
			MelodyHelper.transpone(subject, intervals.get(partnr), config.getKey());
			part.addAll(MelodyHelper.noteListToPart(config, subject, part.getInstrument()));
			//falls es nicht der letzte Part ist, wird auch das Gegenthema hinzugefügt
			if(part != order.get(fugenInfo.getVoices())){
				ArrayList<INote> anitSubject = fugenInfo.getAntiSubjectList();
				MelodyHelper.transpone(anitSubject, intervals.get(partnr), config.getKey());
				part.addAll(MelodyHelper.noteListToPart(config, anitSubject, part.getInstrument()));
			}
		}
		// Rest mit freien Stimmen auffüllen
		for(int partnr = 0; partnr < parts.size() - 2; partnr++) {//letzte zwei Parts sind bereits fertig, daher partnr < parts.size() - 2
			Part part = order.get(partnr);
			int length = parts.size() - partnr - 2;//Länge der freien Stimme; -2 : gleicher Grund
			ArrayList<INote> notes = melGen.generateSubVoice(config, fugenInfo, length);
			MelodyHelper.transpone(notes, intervals.get(partnr), config.getKey());
			part.addAll(MelodyHelper.noteListToPart(config, notes, part.getInstrument()));
		}
		
	}
	
	/**
	 * Fügt den Parts eine Modulation an.
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenInfo - {@link FugenInfo} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	private void addModulation(List<Part> parts, FugenInfo fugenInfo, SongConfig config){
		
		//TODO
		
	}
	
	/**
	 * Fügt den Parts eine Engführung an. Der erste, dritte und fünfte Part, spielt den Dux, die restlichen den Comes.
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenInfo - {@link FugenInfo} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	private void addFinalSection(ArrayList<Part> parts, FugenInfo fugenInfo, SongConfig config){
		int measureDuration = config.getBeats() * config.getMeasureDivision();
		boolean halfMeasure = false;
		if(fugenInfo.getSubjectPart().size() % 2 == 1){//falls das Thema eine ungerade Taktanzahl hat
			halfMeasure = true;
		}
		//speichert in welcher Reihenfolge die Stimmen zu spielen beginnen
		@SuppressWarnings("unchecked")//clone benötigt, da ursprüngliche Reihenfolge in parts erhalten bleiben muss
		ArrayList<Part> order = (ArrayList<Part>) parts.clone();//Objekte in order und parts sind die selben
		Collections.shuffle(order);
		//Intervalle der Stimmen zur Tonhöhe im FugenInfo
		ArrayList<Integer> intervals = new ArrayList<Integer>();
		for( int partnr = 0; partnr < order.size(); partnr++ ){
			int pitch = parts.indexOf(order.get(partnr));
			int interval = 7 * pitch/2;//bei 1/2; 3/2; ... wird abgerundet auf 0; 1, ...
			if(partnr % 2 == 0){//Tonikeeinsatz; Dux
				intervals.add(interval);
			}else{//Dominanteinsatz; Comes
				intervals.add(interval - 3);
			}
		}
		for(int partnr = 0; partnr < parts.size(); partnr++){
			Part part = order.get(partnr);
			ArrayList<INote> notes = new ArrayList<INote>();
			if(halfMeasure && partnr % 2 == 1){
				if(partnr != 0){
					//erste freie Stimmen hinzufügen
					notes = melGen.generateSubVoice(config, fugenInfo, (partnr + 1) / 2);//überlagernder Themeneinsatz
					notes = MelodyHelper.subtNoteList(notes, notes.size() * measureDuration - measureDuration / 2, false);
				}
				//Thema hinzufügen
				notes.addAll(fugenInfo.getSubjectList());
				if(partnr != parts.size() - 1){
					//zweite freie Stimme hinzufügen
					ArrayList<INote> subVoice = melGen.generateSubVoice(config, fugenInfo, (parts.size() - partnr - 1) / 2);
					subVoice = MelodyHelper.subtNoteList(subVoice, subVoice.size() * measureDuration 
							- measureDuration / 2, true);
					notes.addAll(subVoice);
				}
			}else{
				if(partnr != 0){
					//erste freie Stimmen hinzufügen
					notes = melGen.generateSubVoice(config, fugenInfo, (partnr + 1) / 2);//überlagernder Themeneinsatz
				}
				//Thema hinzufügen
				notes.addAll(fugenInfo.getSubjectList());
				if(partnr != parts.size() - 1){
					//zweite freie Stimme hinzufügen
					notes.addAll(melGen.generateSubVoice(config, fugenInfo, (parts.size() - partnr - 1) / 2));
				}
			}
			//transponieren und Part hinzufügen
			notes = MelodyHelper.transpone(notes, intervals.get(partnr), config.getKey());
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
