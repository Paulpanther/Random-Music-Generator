package de.lep.rmg.musicgen;

import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;

/**
 * Interface für Musikgeneratoren.<br>
 * Zukünftig soll es mehrere Musikgeneratoren mit unterschiedlichen Formen (Kanon, Fuge, ...) geben.
 *
 */
public interface IMusicGenerator {
	
	/**
	 * Methode zum generieren eines teilweise zufälligen {@link Song}s, basierend auf einem {@link SongConfig}.
	 * 
	 * @param config Die {@link SongConfig} für das Musikstück
	 * @return Ein zufällig generierter {@link Song}
	 */
	public Song generateSong( SongConfig config );
	
	/**
	 * Gibt den Typ des Songs zurück.<br>
	 * Z.B: Kanon
	 * @return Der Typ des Songs
	 */
	public String getSongType();
}
