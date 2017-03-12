package de.lep.rmg.musicgen.helper;

import java.util.ArrayList;

import de.lep.rmg.model.notes.SNote;

public class MelodyHelper {
	
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
	
}
