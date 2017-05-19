package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;
import java.util.Random;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.helper.PercentPair;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.model.notes.IRealNote;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.model.notes.helper.NoteHelper;

/**
 * 
 * Diese Klasse enthält Methoden zum Generieren von Themen, dazu passenden Antithemen und zu beiden passenden freien Stimmen.
 *
 */
public class FugenMelodyGenerator implements IFugenMelodyGenerator {
	
	/**
	 * Geben die maximal mögliche Distanz der Noten in Intervallen nach oben und unten vom Grundton an.
	 * In keinem von dieser Klasse generierten Thema, können großere Intervalle zu m Grundton auftreten;
	 */
	private int upperMaxIntervalToKey = 6, lowerMaxIntervalToKey = -2;
	/**
	 * the octave the KeyNote is played
	 */
	private int standardOctave = 3;
	
	private Random rand = new Random();
	
	@Override
	public ArrayList<INote> generateSubject(SongConfig config, ArrayList<INote> rhythm) {
		INote lastNote = new SNote(config.getKey().getKeynote(), 3, config.getMeasureDivision());
		for(INote note : rhythm){
			if(note instanceof IRealNote){//else assume it is a Rest
				IRealNote thisNote = (IRealNote) note;
				if(lastNote instanceof IRealNote){
					setNextTone(config, thisNote, (IRealNote)lastNote);
				}else{
					thisNote.setTone(config.getKey().getKeynote());
					thisNote.setOctave(standardOctave);
					NoteHelper.addInterval(thisNote, rand.nextInt(3)*2, config.getKey());
				}
			}
			lastNote = note;
		}
		
		return rhythm;
	}
	
	@Override
	public ArrayList<INote> generateAntiSubject(SongConfig config, ArrayList<INote> subject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<INote> generateSubVoice(SongConfig config, ArrayList<INote> motif, ArrayList<INote> contreMotif, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Verändert eine übergebene {@link IRealNote} in der Tonhöhe. Dabei wird die neue Tonhöhe mit den
	 * im config übergebenen Wahrscheinlichkeiten zum entsprechenden Interval zur VorgängerNote.
	 * 
	 * @param config - {@link SongConfig}
	 * @param thisNote - die zu verändernde Note
	 * @param previous - die vorhergehende Note
	 * @return thisNote mit geänderter Tonhöhe
	 */
	private IRealNote setNextTone(SongConfig config, IRealNote thisNote, IRealNote lastNote){
		ArrayList<PercentPair> allowedIntervals = getAllowedIntervals(config, lastNote);
		int interval = PercentPair.getRandomValue( allowedIntervals.toArray(null), rand);
		thisNote.setTone(config.getKey().getKeynote());
		thisNote.setOctave(standardOctave);
		NoteHelper.addInterval(thisNote, interval, config.getKey());
		return thisNote;
	}
	
	/**
	 * Gibt alle Intervalle die vom {@link SongConfig} und gleichzeitig vom Tonbereich her erlaubt sind zurück.<br>
	 * Rückgabetyp ist dabei eine ArrayList von {@link PercentPair}s, wodurch auch die entsprechenden Wahscheinlichkeiten
	 * transportiert werden.
	 * @param config
	 * @param lastNote
	 * @return
	 */
	private ArrayList<PercentPair> getAllowedIntervals(SongConfig config, IRealNote lastNote){
		int previous = NoteHelper.getInterval( config.getKey().getKeynote() + standardOctave * 12, lastNote.getTone() + lastNote.getOctave() *12);
		ArrayList<Integer> intervals = new ArrayList<Integer>();
		ArrayList<PercentPair> allowed = new ArrayList<PercentPair>();
		for(PercentPair pair: config.getIntervals()){
			intervals.add(pair.getValue());
		}
		for(int value: intervals){
			if(!(previous + value <= upperMaxIntervalToKey)){//Interval nach oben
				allowed.add(new PercentPair(value, config.getInterval(value).getPercent()));
			}
			if(previous - value >= lowerMaxIntervalToKey && value != 0){//Interval nach unten
				allowed.add(new PercentPair(-value, config.getInterval(value).getPercent()));
			}
		}
		return allowed;
	}
	
	//-------------- Setter ----------------
	public void setLowerMaxIntervalToKey(int lowerMaxIntervalToKey) {
		if(lowerMaxIntervalToKey <= 0)
			this.lowerMaxIntervalToKey = lowerMaxIntervalToKey;
	}
	
	public void setUpperMaxIntervalToKey(int upperMaxIntervalToKey) {
		if(upperMaxIntervalToKey >= 0)
			this.upperMaxIntervalToKey = upperMaxIntervalToKey;
	}
	
	public void setStandardOctave(int octave) {
		if(octave >= 0 && octave < 9){
			standardOctave = octave;
		}
	}
	
	//-------------- Getter -----------------
	public int getLowerMaxIntervalToKey() {
		return lowerMaxIntervalToKey;
	}
	
	public int getUpperMaxIntervalToKey() {
		return upperMaxIntervalToKey;
	}
	
	public int getStandardOctave(){
		return standardOctave;
	}

}
