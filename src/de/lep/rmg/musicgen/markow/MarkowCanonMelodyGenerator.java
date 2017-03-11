package de.lep.rmg.musicgen.markow;

import java.util.ArrayList;
import java.util.Random;

import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.helper.RandomHelper;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.model.notes.helper.NoteHelper;
import de.lep.rmg.musicgen.ICanonMelodyGenerator;
import de.lep.rmg.musicgen.helper.MelodyHelper;


public class MarkowCanonMelodyGenerator implements ICanonMelodyGenerator {
	
	private final int TONBEREICH = 11;//Töne von 0 bis 11 Intervalschritten möglich
	private final int PRIME = 0, TERZ = 2, QUINTE = 4, OKTAVE = 7;//Intervalle
	@Override
	public ArrayList<SNote>[][] generateMelodies(SChord key, SChord[] schords,
			ArrayList<Integer>[][] rhythm, SongConfig config) {
		//Vorbereitung
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[][] intervals = new ArrayList[ config.getMelodyNr() ][ schords.length ];//Diese Liste hat die gleiche Struktur wie in der Methoden-Dokumentation beschrieben, nur mit Intervallen statt Noten
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[][] tones = new ArrayList[ config.getMelodyNr() ][ schords.length ];//Diese Liste hat die gleiche Struktur wie in der Methoden-Dokumentation beschrieben, nur mit Tönen statt Noten
		Random rand = new Random();
		
		ArrayList<MarkowMelodyMatrix> matrixList = new ArrayList<MarkowMelodyMatrix>();
		for(SChord schord: schords){
			int chordinterval = NoteHelper.getInterval(key.getKeynote(), schord.getKeynote());//das Interval zwischen aktuellem und Grundakkord
			while(chordinterval < 0)
				chordinterval += OKTAVE;
			while(chordinterval >= 7)
				chordinterval -= OKTAVE;
			matrixList.add(new MarkowMelodyMatrix(TONBEREICH+1, chordinterval, config.getIntervals()));//TONBEREICH+1 wegen Nullbasierung von Arrays
		}
		//Melodie-/Intervalgenerierung
		int firstTone = NoteHelper.getInterval(key.getKeynote(), (schords[0].getKeynote()));//erster gespielter Ton (als Interval zum Grundton)
		while (firstTone < 0)
			firstTone += OKTAVE;
		while(firstTone >= OKTAVE)
			firstTone -= OKTAVE;
		firstTone += rand.nextInt(3)*2;
		for(int i = 0; i < intervals.length; i++){
			for(int k = 0; k < intervals[i].length; k++){
				if(k != 0){
					int lastTone = intervals[i][k-1].get(intervals[i][k-1].size()-1);//letzter generierter Ton
					ArrayList<Integer> allowedTones = getChordTones(key, schords[k]);//Liste aller im TONBEREICH dem aktuellen Akkord angehörigen Intervalle
					for(int index = 0; index < allowedTones.size(); index++){
						int tone = allowedTones.get(index);//Ton der unter dem Index gespeichert ist
						if(allowedTones.size() > 1){
							if(Math.abs(tone - lastTone) > 5){
								allowedTones.remove(index);
								index--;//ArrayList Indizes verschieben sich
								continue;//wenn das Element entfernt wird, müssen die restlichen Bedingungen nicht geprüft werden
							}
							if(i > 0){
								for(int l = i-1; l >= 0; l--){
									if(tone == intervals[l][k].get(0)){
										allowedTones.remove(index);
										index--;//ArrayList Indizes verschieben sich
									}
								}
							}
						}else{
							break;//falls nur ein Ton übrig ist
						}
					}
					Integer[] possibleTones = new Integer[allowedTones.size()];
					firstTone = RandomHelper.randFrom(allowedTones.toArray(possibleTones), rand);
				}
				intervals[i][k] = matrixList.get(k).generateMelody(rhythm[i][k].size(), firstTone, rand);
				tones[i][k] = new ArrayList<Integer>();
				for(int interval: intervals[i][k]){
					tones[i][k].add(NoteHelper.addInterval(key.getKeynote(), interval, key));
				}
			}
			
		}
		
		return MelodyHelper.intsToNotes(tones, rhythm);
	}
	
	/**
	 * Gibt alle möglichen Töne(als Intervalle zum Grundakkord) des Akkords im TONBEREICH zurück
	 * @param key - Grundtonart
	 * @param sch - Akkord als SChord
	 * @return alle erlaubten Töne in einer ArrayList als Intervalle zum Grundakkord
	 */
	ArrayList<Integer> getChordTones(SChord key, SChord sch){
		ArrayList<Integer> allowedIntervals = new ArrayList<Integer>();
		int interval = NoteHelper.getInterval(key.getKeynote(), sch.getKeynote());
		for(int i = 0; i <= TONBEREICH; i++){
			if((i - interval)%OKTAVE == PRIME || (i - interval -TERZ)%OKTAVE == PRIME ||
					(i - interval - QUINTE)%OKTAVE == PRIME){
				allowedIntervals.add(i);
			}
		}
		return allowedIntervals;
	}

	@Override
	public String getGeneratorName() {
		return "Markow";
	}
	
}
