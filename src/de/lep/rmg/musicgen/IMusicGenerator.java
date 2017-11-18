package de.lep.rmg.musicgen;

import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.view.panels.ControllPanel;

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
	 * Gibt den Typ des {@link Song}s zurück.<br>
	 * Z.B: Kanon
	 * @return Der Typ des Songs
	 */
	public String getSongType();
	
	/**
	 * Gibt den Namen des Generators zurück. Wird im GUI verwendet.
	 * @return Name des {@link IMusicGenerator}s
	 */
	public String getGeneratorName();
	
	/**
	 * Gibt ein {@link ControllPanel} zurück, mit dem der Nutzer das SongConfig einstellen
	 * und die Generierung des {@link Song}s starten kann
	 * @return ein zu Generator gehörendes {@link ControllPanel}
	 */
	public ControllPanel getGeneratorPanel();
}
