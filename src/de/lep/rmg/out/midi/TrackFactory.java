package de.lep.rmg.out.midi;

import java.util.Iterator;

import javax.sound.midi.*;

import de.lep.rmg.model.*;
import de.lep.rmg.model.notes.*;

/**
 * stellt Factorymethoden und Änderungsmethoden für {@link Track}s zur Verfügung.
 * 
 * @author Lukas
 * 
 */
public class TrackFactory {
	/**
	 * macht einen neuen {@link Track} auf der {@link Sequence}, in dem der gegebene {@link Part} repräsentiert wird
	 * @param seq
	 * @param part
	 * @return
	 */
	public Track createTrack(Sequence seq, Part part){
		long counter = 0;
		Track track = seq.createTrack();
		eventMaking(counter, track, part, (byte) 1);
		return track;
	}
	
	/**
	 * macht einen neuen {@link Track} auf der {@link Sequence} und fügt den {@link Part} auf dem angegebenen Channel hinzu
	 * @param seq
	 * @param part
	 * @param channel
	 * @return
	 */
	public Track createTrack(Sequence seq, Part part, byte channel){
		long counter = 0;
		Track track = seq.createTrack();
		eventMaking(counter, track, part, channel);
		return track;
	}
	
	/**
	 * macht einen neuen {@link Track} in dem die übergebenen {@link Part}s (in form of a Song) repräsentiert sind
	 * @param seq die Sequence auf der der Track erstellt wird
	 * @param song
	 * @param partsOnSameChannel entscheidet, ob die Parts nacheinander oder gleichzeitig gespielt werden
	 * @return einen neuen Track auf der Sequence
	 */
	public Track createTrack(Sequence seq, Song song, boolean partsOnSameChannel){
		Track track = seq.createTrack();
		long counter = 0;
		if(partsOnSameChannel)
			SongIteration(song, track, counter, (byte) 1);
		else
			SongIteration(song, track, counter);
		return track;
	}
	
	/**
	 * erstellt einen neuen Track auf der Sequence für jeden Part des Songs
	 * @param seq
	 * @param song
	 */
	public void createTracks(Sequence seq, Song song){
		byte channel = 0;
		for(Part part: song){
			createTrack(seq, part, channel);
			channel++;
		}
	}
	
	/**
	 * fügt den Part auf dem angegeben Channel zum Track hinzu
	 * @param track
	 * @param part
	 * @return track with added part
	 */
	public Track addToTrack(Track track, Part part, byte channel){
		long counter = track.ticks();
		eventMaking(counter, track, part, channel);
		return track;
	}
	
	/**
	 * adds the information contained in the Song to the Track (on channel 1) in form of MidiEvents
	 * the added Song will be played after the already existing music is finished
	 * @param track
	 * @param song
	 * @param partsOnSameChannel decides whether the parts will be played after each other on the same channel<br>
	 * or at the same time on different channels
	 * @return track with added Song
	 */
	public Track addToTrack(Track track, Song song, boolean partsOnSameChannel){
		long counter = track.ticks();
		if(partsOnSameChannel)
			SongIteration(song, track, counter, (byte) 1);
		else
			SongIteration(song, track, counter);
		return track;
	}
	
	/**
	 * add a Part to a track but on an other channel
	 * if afterwards something is added, it will start after the last event on the channel playing longest(if startAtZero is not toggled)
	 * always make sure you are not adding MidiEvents with ticks equal to already existing MidiEvents, as the result will sound horrible
	 * @param track
	 * @param channel
	 * @param part
	 * @param startAtZero
	 * @return track with added part
	 */
	public Track addOnOtherChannel(Track track, byte channel, Part part, boolean startAtZero){
		long counter = 0;
		if(!startAtZero)
			counter = track.ticks();
		eventMaking(counter, track, part, channel);
		return track;
	}
	
	/**
	 * add a Song to a track but on an other channel
	 * if afterwards something is added, it will start after the last event on the channel playing longest(if startAtZero is not toggled)
	 * always make sure you are not adding MidiEvents with ticks equal to already existing MidiEvents, as the result will sound horrible
	 * @param track
	 * @param channel
	 * @param song
	 * @param startAtZero
	 * @return track with added song
	 */
	public Track addOnOtherChannel(Song song, Track track, byte channel, boolean startAtZero){
		long counter = 0;
		if(!startAtZero)
				counter = track.ticks();
		SongIteration(song, track, counter, channel);
		return track;
	}
	
	/**
	 * Iterates through a Song and adds every Part on the same channel of the given track
	 * @param song
	 * @param track
	 * @param counter
	 * @param channel
	 */
	private void SongIteration(Song song, Track track, long counter, byte channel){
		Iterator<Part> partsIterator = song.iterator();
		while(partsIterator.hasNext()){
			Part part = partsIterator.next();
			counter += eventMaking(counter, track, part, channel);
		}
	}
	
	/**
	 * Iterates through a Song and adds every Part on a new channel of the given track
	 * @param song
	 * @param track
	 * @param counter
	 */
	private void SongIteration(Song song, Track track, long counter){
		Iterator<Part> partsIterator = song.iterator();
		byte channel = 1;
		while(partsIterator.hasNext()){
			Part part = partsIterator.next();
			eventMaking(counter, track, part, channel);
			channel++;
		}
	}
	
	/**
	 * adds a Part on a specific channel of the given track
	 * @param counter specifies on which tick to add he first Note
	 * @param track
	 * @param part
	 * @param channel
	 * @return value of the counter after every Note is added
	 */
	private long eventMaking(long counter, Track track, Part part, byte channel){
		int velocity = (int) part.getInstrument().getVolume();
		
		ShortMessage sm = new ShortMessage();
		try {
			sm.setMessage(192, channel, part.getInstrument().getMidiProgram(), 0);
			track.add(new MidiEvent(sm, counter));
		} catch (InvalidMidiDataException e) {
			System.out.println("Failed to build MIDI-Track");
			e.printStackTrace();
		}
		
		try {
			for( Measure measure : part ) {
				Iterator<INote> iterator = measure.iterator();
				while(iterator.hasNext()){
					INote mn = iterator.next();
					if(mn instanceof SNote){
						SNote snote = (SNote) mn;
						track.add(new MidiEvent(new ShortMessage(144, channel, snote.getTone() + snote.getOctave() * 12 + 12, velocity), counter));
						track.add(new MidiEvent(new ShortMessage(128, channel, snote.getTone() + snote.getOctave() * 12 + 12, velocity), counter+snote.getDuration()));
						counter += snote.getDuration();
					}else{
						if(mn instanceof Chord){
							int x = 0;
							Chord chord = (Chord) mn;
							Iterator<SNote> it = chord.iterator();
							while(it.hasNext()){
								SNote snote = it.next();
								track.add(new MidiEvent(new ShortMessage(144, channel, snote.getTone() + snote.getOctave() * 12 + 12, velocity), counter));
								track.add(new MidiEvent(new ShortMessage(128, channel, snote.getTone() + snote.getOctave() * 12 + 12, velocity), counter+snote.getDuration()));
								if(x < snote.getDuration())
									x = snote.getDuration();
							}
							counter += x;
						}else{
							if(mn instanceof Rest){
								counter += mn.getDuration();
							}
						}
					}
				}
			}
		} catch (InvalidMidiDataException e) {
			System.out.println("Failure while building MIDI-Track");
			e.printStackTrace();
		}
		return counter;
	}
	
}
