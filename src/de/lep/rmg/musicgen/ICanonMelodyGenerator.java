package de.lep.rmg.musicgen;

import java.util.ArrayList;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;

public interface ICanonMelodyGenerator {
	/**
	 * Generiert die Melodie für den Song.<br>
	 * Gibt eine 3-dimensionale Liste von SNoten, die bereits den übergebenen Rhythmus haben aus.<br>
	 * @param key Der Grundakkord des Songs
	 * @param schords Die Akkorde des Songs (s. {@link ChordGenerator})
	 * @param rhythm Der Rhythmus des Songs (s. {@link RhythmGenerator})
	 * @param config Die Konfiguration des Songs (s. {@link SongConfig})
	 * @return Eine 3-dimensionale Liste von SNoten, die bereits den übergebenen Rhythmus haben. Siehe oben zur Struktur.
	 */
	ArrayList<SNote>[][] generateMelodies( 
			SChord key,
			SChord[] schords,
			ArrayList<Integer>[][] rhythm,
			SongConfig config
			);
	
	String getGeneratorName();
}
