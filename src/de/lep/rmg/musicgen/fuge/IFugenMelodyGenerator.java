package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.notes.INote;

public interface IFugenMelodyGenerator {
	/**
	 * Generiert das Hauptmotiv einer Fuge den Dux.
	 * 
	 * @param config - ein SongConfig
	 * @param rhythm - der Rhythmus das Thema folgen soll als INotes in einer ArrayList
	 * @return Melodie als ArrayList
	 */
	public ArrayList<INote> generateMotif( SongConfig config, ArrayList<INote> rhythm );
	/**
	 * Generiert passened zu einem Thema ein Gegenthema, den Comes.
	 * 
	 * @param config - ein SongConfig
	 * @param dux - das Thema dessen Gegenpart geschaffen werden soll als INotes in einer ArrayList
	 * @return Gegenthema als ArrayList
	 */
	public ArrayList<INote> generateContreMotif( SongConfig config, ArrayList<INote> dux );
}
