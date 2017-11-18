package de.lep.rmg.musicgen;

import java.util.ArrayList;

import de.lep.rmg.model.Measure;
import de.lep.rmg.model.Part;
import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.notes.Rest;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.out.midi.MidiPlayer;
import de.lep.rmg.view.panels.CanonControllPanel;
import de.lep.rmg.view.panels.ControllPanel;

/**
 * Unsere Implementation des {@link IMusicGenerator}.<br>
 * Controller für {@link ChordGenerator}, {@link RhythmGenerator} und {@link MelodyGenerator}.
 *
 */
public class CanonGenerator implements IMusicGenerator {
	
	ICanonMelodyGenerator melGen;
	MidiPlayer midiPlayer;

	public CanonGenerator(MidiPlayer midiPlayer, ICanonMelodyGenerator melGen) {
		this.melGen = melGen;
		this.midiPlayer = midiPlayer;
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
		//Prüft die Konfiguration auf Fehler
		if( config.getChordNr() * config.getChordDuration() % config.getBeats() != 0){
			String errorMessage = String.format("SongConfig.getChordNr() * SongConfig.getChordDuration() "
					+ "does not match SongConfig.getBeats!\nChordNr: %d\nChordDuration: %d\nBeats: %d\n", 
					config.getChordNr(), config.getChordDuration(), config.getBeats());
			throw new IllegalStateException(errorMessage);
		}

		Song song = new Song( config );
		
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
		int width = config.getChordDuration() * config.getChordNr() / config.getBeats();//Die Anzahl an Takten, die eine Melodie lang ist
		
		//Anordnung der Melodien
		for( int p = 0; p < config.getMelodyNr(); p++ ) {
			Part part = new Part( config.getInstruments()[ p ] );//Ein Song besteht aus Instrument-Parts
			
			for( int m = 0; m < width * p; m++ ) {//Melodie-Einstieg wird verschoben
				Measure m1 = new Measure( config );
				m1.add( new Rest( SongConfig.measureDivision * config.getBeats() ) );//duration of one Measure
				part.add( m1 );
			}
			
			//Noten werden hinzugefügt
			for( int r = 0; r < config.getRepeats(); r++ ) {
				for( int m = 0; m < melody.length; m++ ) {

					int duration = SongConfig.measureDivision*config.getBeats();
					Measure m1 = new Measure( config );
					for(ArrayList<SNote> chordList: melody[m]){
						for(SNote sno: chordList){
							m1.add(sno);
							duration -= sno.getDuration();
							if(duration <= 0){
								part.add( m1.clone());
								m1 = new Measure(config);
								duration += SongConfig.measureDivision*config.getBeats();
							}
						}

					}
				}
			}
			
			//Am Ende wieder Pausen entsprechend dem Einschub
			for( int m = 0; m < width * ( melody.length - p-1 ); m++ ) {
				Measure m1 = new Measure( config );
				m1.add( new Rest( SongConfig.measureDivision * config.getBeats() ) );
				part.add( m1 );
			}
			
			song.add( part );
		}
		
		//System.out.println( song.toString() );
		return song;
	}

	@Override
	public String getSongType() {
		return "Kanon";
	}
	
	@Override
	public String getGeneratorName() {
		return "Kanon";
	}

	@Override
	public ControllPanel getGeneratorPanel() {
		return new CanonControllPanel(this, midiPlayer, 3);
	}
}
