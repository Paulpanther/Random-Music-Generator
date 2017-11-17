package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.notes.INote;

public interface IFugenMelodyGenerator {
	/**
	 * Generiert das Hauptmotiv einer Fuge. Es wird keine neue {@link ArrayList} erschaffen
	 * sondern, die Noten im rhythm, werden angepasst. Soll der rhythm unverändert erhalten bleiben,
	 * muss eine tiefe Kopie übergeben werden.
	 * 
	 * @param config - ein SongConfig
	 * @param rhythm - der Rhythmus dem das Thema folgen soll, als INotes in einer ArrayList
	 * @return Thema als ArrayList
	 */
	public ArrayList<INote> generateSubject( SongConfig config, ArrayList<INote> rhythm );
	/**
	 * Generiert passened zu einem Thema ein Gegenthema, mit der gleichen Taktanzahl.
	 * Es wird in der gleichen Tonhöhe und Tonart komponiert, wie das Hauptthema.
	 * 
	 * @param config - ein SongConfig
	 * @param subject - das Thema dessen Gegenpart geschaffen werden soll als INotes in einer ArrayList
	 * @return Gegenthema als ArrayList
	 */
	public ArrayList<INote> generateAntiSubject( SongConfig config, ArrayList<INote> sublect );
	/**
	 * Generiert eine freie Stimme die mit Thema und Gegenthema harmoniert und
	 * gut an das Gegenthema anschließt.<br>
	 * Setzt voraus, dass Thema und Gegenthema gleich lang sind.
	 * 
	 * @param config - ein SongConfig
	 * @param subject - Hauptmotiv der Fuge
	 * @param antiSubject - Nebenmotiv der Fuge
	 * @param length - Dauer der freien Stimme in Vielfachen der Länge des Themas
	 * @return freie Stimme als ArrayList
	 */
	public ArrayList<INote> generateSubVoice( SongConfig config, FugenSubjects fugenSubjects, int length );
}
