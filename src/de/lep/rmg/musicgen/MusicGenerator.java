package de.lep.rmg.musicgen;

import java.util.ArrayList;

import de.lep.rmg.model.Measure;
import de.lep.rmg.model.Part;
import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.notes.Rest;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;

/**
 * Unsere Implementation des {@link IMusicGenerator}.<br>
 * Controller für {@link ChordGenerator}, {@link RhythmGenerator} und {@link MelodyGenerator}.
 *
 */
public class MusicGenerator implements IMusicGenerator {
	
	ICanonMelodyGenerator melGen;

	public MusicGenerator(ICanonMelodyGenerator melGen) {
		this.melGen = melGen;
	}
	
	/**
	 * Generiert einen {@link Song} mithilfe des {@link ChordGenerator}s, {@link RhythmGenerator}s und {@link MelodyGenerator}s
	 * und fügt ihn zusammen.
	 * 
	 * @param config Die {@link SongConfig} mit Optionen für den Song
	 * @return Ein komplett komponierter {@link Song}
	 */
	@Override
	public Song generateSong( SongConfig config ) {
		Song song = new Song( config );
		
		//Prüft die Konfiguration auf Fehler
		if( config.getChordNr() * config.getChordDuration() % config.getBeats() != 0 ) {
			System.err.println( "Error: Songconfig value mismatch\nChordNr: " + config.getChordNr() + "\nChordDuration: " + 
					config.getChordDuration() + "\nBeats: " + config.getBeats());
			return null;
		}
		
		//Legt den Grundton fest (falls noch nicht festgelegt)
		SChord key = config.getKey();
		if( key == null )
			key = ChordGenerator.generateKey();
		
		//Generiere eine Akkordfolge
		SChord[] chords = ChordGenerator.generateChords( key, config.getChordNr() );
		//Generiert Rhythmen
		//Array-Struktur: Alle Rhythmen / Nur ein Rhythmus aus mehreren Akkordrhythmen / Akkordrhythmus aus mehreren Dauern / Eine einzelne Dauer
		ArrayList<Integer>[][] rhythm = RhythmGenerator.generateRhythm( config );
		//Generiert Melodien
		//Array-Struktur: Alle Melodien / Nur eine Melodie aus mehreren Akkordmelodien / Akkordmelodie aus mehreren Noten (mit Rhythmus) / SNote
		ArrayList<SNote>[][] melody = melGen.generateMelodies( key, chords, rhythm, config );
		
		//Vorbereitung für Anordnung
		int width = config.getChordDuration() * config.getChordNr() / 4;//Die Anzahl an Takten, die eine Melodie lang ist
		
		//Anordnung der Melodien
		for( int p = 0; p < config.getMelodyNr(); p++ ) {
			Part part = new Part( config.getInstruments()[ p ] );//Ein Song besteht aus Instrument-Parts
			
			for( int m = 0; m < width * p; m++ ) {//Melodie-Einstieg wird verschoben
				Measure m1 = new Measure( config );
				m1.add( new Rest( 32 ) );
				part.add( m1 );
			}
			
			//Noten werden hinzugefügt
			for( int r = 0; r < config.getRepeats(); r++ ) {
				for( int m = 0; m < melody.length; m++ ) {
					
					if(config.getChordDuration() == 3){
						int duration = 0;
						Measure m1 = new Measure(config);
						for(ArrayList<SNote> chordList: melody[m]){
							for(SNote sno: chordList){
								m1.add(sno);
								duration += sno.getDuration();
								if(duration >= 32){
									part.add( m1.clone());
									m1 = new Measure(config);
								}
							}
						}
					}else{
						for( int mm = 0; mm < width; mm++ ) {
							Measure me = new Measure( config );
							for( int c = 0; c < 4 / config.getChordDuration(); c++ ) {
								int chordNr = 4 / config.getChordDuration() * mm + c;
								me.addAll( melody[ m ][ chordNr ] );
							}
							part.add( me );
						}
					}
				}
			}
			
			//Am Ende wieder Pausen entsprechend dem Einschub
			for( int m = 0; m < width * ( melody.length - p-1 ); m++ ) {
				Measure m1 = new Measure( config );
				m1.add( new Rest( 32 ) );
				part.add( m1 );
			}
			
			song.add( part );
		}
		
		System.out.println( song.toString() );
		return song;
	}

	@Override
	public String getSongType() {
		return "Kanon";
	}
	
	@Override
	public String getGeneratorName() {
		String name = "Kanon ";
		name += melGen.getGeneratorName();
		return name;
	}
}
