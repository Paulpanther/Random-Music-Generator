package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;
import java.util.Random;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.helper.PercentPair;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.model.notes.IRealNote;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.model.notes.helper.NoteHelper;
import de.lep.rmg.musicgen.RhythmGenerator;

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
		INote lastNote = new SNote(config.getKey().getKeynote(), standardOctave, config.getMeasureDivision());
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
		ArrayList<INote> antiSubject = RhythmGenerator.generateAntiMotif(config, subject);
		
		int noteCounter = 0;//zählt die bereits untersuchten Noten des subjects
		int subjectDurCounter = 0;//zählt die Gesamtlänge der bereits untersuchten Noten des subjects
		int durCounter = 0;//zählt die Gesamtlänge der Noten im antiSubject
		ArrayList<INote> playingNotes = new ArrayList<INote>();
		INote lastNote = subject.get( subject.size() - 1 );
		
		for( INote inote : antiSubject ) {
			durCounter += inote.getDuration();
			while(subjectDurCounter <= durCounter){
				playingNotes.add(subject.get(noteCounter));
				subjectDurCounter += subject.get(noteCounter).getDuration();
				noteCounter++;
			}
			
			if ( inote instanceof IRealNote ) {
				IRealNote thisNote = (IRealNote) inote;
				if(lastNote instanceof IRealNote){
					setNextTone(config, thisNote, (IRealNote)lastNote);
				}else{
					thisNote.setTone(config.getKey().getKeynote());
					thisNote.setOctave(standardOctave);
					NoteHelper.addInterval(thisNote, rand.nextInt(3)*2, config.getKey());
				}
				for( INote subjectNote : playingNotes ){
					if( subjectNote instanceof IRealNote ){
						//TODO subject mit einbeziehen
					}
				}
			}
			lastNote = inote;
			for( int index = playingNotes.size() - 1; index >= 0; index-- ) {
				if(index != 0)
					playingNotes.remove(index);
				else
					if( subjectDurCounter <= durCounter ) {
						playingNotes.remove(index);
					}
			}
		}
		return antiSubject;
	}

	@Override
	public ArrayList<INote> generateSubVoice(SongConfig config, FugenInfo fugenInfo, int length) {
		INote lastNote = fugenInfo.getAntiSubjectList().get( fugenInfo.getAntiSubjectList().size() - 1 );
		ArrayList<INote> rhythm = RhythmGenerator.generateMotif(config, length);
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
		//TODO subject und antiSubject mit einbeziehen
		return rhythm;
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
		thisNote.setTone(lastNote.getTone());
		thisNote.setOctave(lastNote.getOctave());
		NoteHelper.addInterval(thisNote, interval, config.getKey());
		return thisNote;
	}
	
	/**
	 * Gibt alle Intervalle die vom {@link SongConfig} und gleichzeitig vom Tonbereich her erlaubt sind zurück.<br>
	 * Rückgabetyp ist dabei eine ArrayList von {@link PercentPair}s, wodurch auch die entsprechenden Wahscheinlichkeiten
	 * transportiert werden.
	 * @param config - das {@link SongConfig}
	 * @param lastNote - die Note, in Bezug auf die das Interval angegeben wird
	 * @return Liste von möglichen Intervallen
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
