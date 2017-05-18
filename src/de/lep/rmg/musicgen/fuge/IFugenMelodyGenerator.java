package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.notes.INote;

public interface IFugenMelodyGenerator {
	/**
	 * Generiert das Hauptmotiv einer Fuge
	 * 
	 * @param config - ein SongConfig
	 * @param rhythm - der Rhythmus das Thema folgen soll als INotes in einer ArrayList
	 * @return Melodie als ArrayList
	 */
	public ArrayList<INote> generateSubject( SongConfig config, ArrayList<INote> rhythm );
	/**
	 * Generiert passened zu einem Thema ein Gegenthema, mit der gleichen Taktanzahl.
	 * 
	 * @param config - ein SongConfig
	 * @param dux - das Thema dessen Gegenpart geschaffen werden soll als INotes in einer ArrayList
	 * @return Gegenthema als ArrayList
	 */
	public ArrayList<INote> generateContreSubject( SongConfig config, ArrayList<INote> motif );
	/**
	 * Generiert eine freie Stimme die mit Thema und Gegenthema harmoniert und<br>
	 * gut an das Gegenthema anschließt.<br>
	 * Setzt voraus, dass Thema und Gegenthema gleich lang sind.
	 * 
	 * @param config - ein SongConfig
	 * @param motif - Hauptmotiv der Fuge
	 * @param contreMotif - Nebenmotiv der Fuge
	 * @param length - Dauer der freien Stimme in Vielfachen der Länge des Themas
	 * @return freie Stimme als ArrayList
	 */
	public ArrayList<INote> generateSubVoice( SongConfig config, ArrayList<INote> motif, ArrayList<INote> contreMotif, int length );
}
