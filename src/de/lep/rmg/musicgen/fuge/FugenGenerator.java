package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;
import java.util.List;

import de.lep.rmg.model.Part;
import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.musicgen.RhythmGenerator;
import de.lep.rmg.musicgen.helper.MelodyHelper;

public class FugenGenerator implements IMusicGenerator {
	
	IFugenMelodyGenerator melGen;
	
	public FugenGenerator( IFugenMelodyGenerator FMG ) {
		melGen = FMG;
	}
	
	/**
	 * Generiert eine Fuge auf Basis des {@link SongConfig}s.<br>
	 * Die Anzahl der Stimmen der Fuge legt sich aus der Länge des Instrumenten-Arrays im SongConfig fest,
	 * wobei es immer mindestens zwei und höchstens sechs Stimmen gibt. Falls unterschiedliche Instrumente
	 * gewählt wurden, werden sie auch spielen, auch wenn dieses Verhalten untypisch für Fugen ist.<br>
	 * Thema und Antithema werden von  {@link FugenMelodyGenerator} nach den Angeben des SongConfigs erstellt.<br>
	 * Abhängig von der Einstellung der {@link SongConfig#repeats} werden bis zu drei, von Modulationen getrennte,
	 * Durchführungen erstellt, wobei die letzte eine Engführung ist. Es wird also nur dann eine echte Fuge generiert,
	 * wenn die repeats drei (oder mehr - das hat keine Auswirkungen) betragen.<br>
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
		ArrayList<INote> rhythm = RhythmGenerator.generateMotif(config);
		//generiere Thema und Gegenthema
		ArrayList<INote> themeList = melGen.generateSubject(config, rhythm);//Das Hauptthema
		ArrayList <INote> anitThemeList = melGen.generateContreSubject(config, themeList);//Das Gegenthema
		Part themePart = MelodyHelper.noteListToPart(config, themeList, instrument);
		Part antiThemePart = MelodyHelper.noteListToPart(config, anitThemeList, instrument);
		//erstelle FugenInfo-Objekt zum bündeln der Informationen
		FugenInfo fugenInfo = new FugenInfo(themePart, antiThemePart, voices);
		
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
			addFinalSection(partList, fugenInfo, config);
		}
		//TODO Schlusskadenz
		
		
		Song song = new Song(config);song.addAll(partList);
		return song;
	}
	
	/**
	 * Fügt den Parts eine Durchführung an.
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenInfo - {@link FugenInfo} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	private void addSection(List<Part> parts, FugenInfo fugenInfo, SongConfig config){
		
		//TODO
		
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
	 * Fügt den Parts eine Engführung an
	 * @param parts - Liste der Stimmen, welche die Fuge spielen sollen
	 * @param fugenInfo - {@link FugenInfo} der Fuge
	 * @param config - das {@link SongConfig} der Fuge
	 */
	private void addFinalSection(List<Part> parts, FugenInfo fugenInfo, SongConfig config){
		
		//TODO
		
	}
	
	@Override
	public String getSongType() {
		return "Fuge";
	}

	@Override
	public String getGeneratorName() {
		return "Fugengenerator";
	}

}
