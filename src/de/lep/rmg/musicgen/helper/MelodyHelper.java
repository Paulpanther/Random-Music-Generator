package de.lep.rmg.musicgen.helper;

import java.util.ArrayList;

import de.lep.rmg.model.Measure;
import de.lep.rmg.model.Part;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.model.notes.IRealNote;
import de.lep.rmg.model.notes.Rest;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.model.notes.helper.NoteHelper;

public class MelodyHelper {
	
	/**
	 * Transponiert eine Liste von Noten, um das angegebene Interval und gibt
	 * das Ergebnis in einer neuen Liste zurück. Das Original bleibt erhalten.
	 * Funktioniert nur für Noten, die {@link IRealNote} implementieren, andere
	 * Noten werden wie Pausen behandelt und bleiben unverändert.
	 * @param notes - Liste der zu Transponierenden Noten
	 * @param interval - Intervval, um das Transponiert wird
	 * @param key - Grundtonart des Originals
	 * @return die transponierte Melodie
	 */
	public static ArrayList<INote> transpone( ArrayList<INote> notes, int interval, SChord key ){
		ArrayList<INote> transposition = new ArrayList<INote>(notes.size());
		for(INote note: notes){
			if(note instanceof IRealNote){
				IRealNote realNote = (IRealNote) note;
				IRealNote transponedNote = (IRealNote) realNote.clone();
				NoteHelper.addInterval(transponedNote, interval, key);
			transposition.add(transponedNote);
			}else{
				transposition.add(note.clone());
			}
		}
		return transposition;
	}
	
	/**
	 * Wandelt eine Liste von Noten (ohne Takteinteilung) in einen {@link Part} (mit Einteilung in Takte) um
	 * 
	 * @param config - {@link SongConfig} mit Angaben zu Taktlänge und -art
	 * @param notes - Notenliste
	 * @param instru - {@link Instrument} das den Part spielen soll
	 * @return ein {@link Part}-Objekt
	 */
	public static Part noteListToPart( SongConfig config, ArrayList<INote> notes, Instrument instru){
		Part part = new Part(instru);
		int duration = SongConfig.measureDivision * config.getBeats();
		//int duration = 0;
		Measure mea = new Measure( config );
		for(INote inote: notes){
			mea.add(inote);
			duration -= inote.getDuration();
			//if(duration >= SongConfig.measureDivision*config.getBeats()){
			if(duration <= 0){
				part.add( mea.clone());
				mea = new Measure(config);
				duration += SongConfig.measureDivision * config.getBeats();
			}
		}
		if(duration > 0){
			mea.add(new Rest(duration));//fügt eine entsprechend lange Pause an, falls der letzte Takt nicht komplett  ist
		}
		return part;
	}

	/**
	  * Vereint die Listen der Tonhöhen und die der Notendauern zu einem Listenarray von SNoten
	  * @param tones Tonhöhen
	  * @param rhythm Notendauern
	  * @return die Melodie die sich aus beidem ergibt
	  */
	public static ArrayList<SNote>[][] intsToNotes(ArrayList<Integer>[][] tones, ArrayList<Integer>[][] rhythm){
		@SuppressWarnings("unchecked")//Fügt zur Melodie den Rhythmus hinzu (Ton + Dauer = Note)
		ArrayList<SNote>[][] melody = new ArrayList[ tones.length ][ tones[0].length ];
		for( int m = 0; m < tones.length; m++ ) {
			for( int c = 0; c < tones[m].length; c++ ) {
				melody[ m ][ c ] = new ArrayList<SNote>();
				for( int n = 0; n < tones[ m ][ c ].size(); n++ )
					melody[ m ][ c ].add( new SNote( tones[ m ][ c ].get( n ), 4, rhythm[ m ][ c ].get( n ) ) );
			}
		}
		return melody;
	}
	
	/**
	 * Spaltet von einer Liste von {@link INote}n eine kleinere Liste ab, deren Notenlängen die als Parameter
	 * angegebene Gesamtlänge (duration) erreichen. Die Originalliste bleibt unverändert
	 * @param notes - Liste der Noten, von der abgespalten werden soll
	 * @param duration - die Summe der Notendauern, die das angespaltene Stück haben soll
	 * @param beginFront - falls true werden die Noten vom Anfang der Liste aus gewählt, 
	 * 	ansonsten wird rückwärts über die Liste iteriet
	 * @return neue Liste, die
	 */
	public static ArrayList<INote> subtNoteList(ArrayList<INote> notes, int duration, boolean beginFront) {
		ArrayList<INote> ret = new ArrayList<INote>();
		INote nextNote;
		if(beginFront)
			nextNote = notes.get(0);
		else
			nextNote = notes.get(notes.size() - 1);
		duration -= nextNote.getDuration();
		int elemCount = 1;
		while( duration > 0 ) {
			ret.add(nextNote);
			if(beginFront)
				nextNote = notes.get(elemCount);
			else
				nextNote = notes.get(notes.size() - elemCount - 1);
			duration -= nextNote.getDuration();
			elemCount++;
		}
		if(duration < 0) {
			nextNote = nextNote.clone();
			nextNote.setDuration(Math.abs(duration));
			ret.add(nextNote);
		}
		return ret;
	}
}
