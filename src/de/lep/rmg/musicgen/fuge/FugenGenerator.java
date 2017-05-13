package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;

import de.lep.rmg.model.Measure;
import de.lep.rmg.model.Part;
import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.model.notes.Rest;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.musicgen.RhythmGenerator;
import de.lep.rmg.musicgen.helper.MelodyHelper;

public class FugenGenerator implements IMusicGenerator {
	
	IFugenMelodyGenerator melGen;
	
	public FugenGenerator( IFugenMelodyGenerator FMG ) {
		melGen = FMG;
	}
	
	@Override
	public Song generateSong(SongConfig config) {
		SChord key = config.getKey();
		Instrument instrument = config.getInstruments()[0];
		//generiere Rhythmus für Hauptmotiv
		ArrayList<INote> rhythm = RhythmGenerator.generateMotif(config);
		//generiere Thema und Gegenthema
		ArrayList<INote> themeList = melGen.generateMotif(config, rhythm);//Das Hauptthema
		ArrayList <INote> anitThemeList = melGen.generateContreMotif(config, themeList);//Das Gegenthema
		Part themePart = MelodyHelper.noteListToPart(config, themeList, instrument);
		Part anitThemePart = MelodyHelper.noteListToPart(config, anitThemeList, instrument);
		
		//bilde Part Dux, Hauptstimme in der Tonika
		Part dux = new Part(instrument);
		dux.addAll(themePart);
		dux.addAll(anitThemePart);
		
		//bilde Part Comes, Begleitstimme in der Dominante
		Part comes = new Part(instrument);
		for(int i = 0; i < themePart.size(); i++){
			Measure mea = new Measure( config );
			mea.add(new Rest(config.getMeasureDivision()*config.getBeats()));
			comes.add(mea);
		}
		comes.addAll(MelodyHelper.noteListToPart(config, MelodyHelper.transpone(themeList, 4, key), instrument));//fügt um eine Quinte transponiertes Thema hinzu
		
		Song song = new Song(config);
		song.add(dux);
		song.add(comes);
		return song;
	}

	@Override
	public String getSongType() {
		return "Fuge";
	}

	@Override
	public String getGeneratorName() {
		return "Fugengenrator";
	}

}
