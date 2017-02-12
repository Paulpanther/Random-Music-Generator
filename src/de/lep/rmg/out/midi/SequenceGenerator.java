package de.lep.rmg.out.midi;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.*;

import de.lep.rmg.model.Song;

/**
 * provides methods for creating, saving and loading {@link Sequence}s<br>
 * <br>
 * bietet Metoden zum erschaffen, speichern und laden von {@link Sequence}n
 * 
 * @author Lukas
 */
public class SequenceGenerator {
	/**
	 * 
	 * @return neue {@link Sequence} ohne Songdaten
	 */
	public Sequence createSequence(){
		Sequence seq = null;
		try {
			//Die 8 steht für eine Auflösung von einem Achtelbeat
			seq = new Sequence(Sequence.PPQ, 8);
		} catch (InvalidMidiDataException e) {
			System.out.println("Failed to create Sequence");
			e.printStackTrace();
		}
		return seq;
	}
	
	/**
	 * builds a {@link Sequence} on basis of the given {@link Song}<br>
	 * if miditype0 == false<br>
	 * 	every {@link Part} will be saved on a different {@link Track}<br>
	 * else<br>
	 * 	every {@link Part} will be added on an other channel of the same {@link Track}<br>
	 * <br>
	 * erschafft eine {@link Sequece} aus dem gegebenen {@link Song}<br>
	 * falls miditype == false<br>
	 * 	jeder {@link Part} wird als eigener {@link Track} gespeichert<br>
	 * falls miditype == true<br>
	 * 	jeder {@link Part} wird auf dem selben {@link Track} aber in einem anderen Channnel gespeichert.
	 * 	
	 * @param miditype0 decides about which type of MIDI-File the output will be
	 * @param song
	 * @return Sequence
	 */
	public Sequence createSequence(Song song, boolean miditype0){
		Sequence seq = null;
		TrackFactory trackFac = new TrackFactory();
		if(miditype0){
			try {
				//Die 8 steht für eine Auflösung von einem Achtelbeat
				seq = new Sequence(Sequence.PPQ, 8);
				trackFac.createTrack(seq, song, false);
			} catch (InvalidMidiDataException e) {
				System.out.println("Failed to create Sequence");
				e.printStackTrace();
			}
		}
		else{
			try {
				seq = new Sequence(Sequence.PPQ, 8);
				trackFac.createTracks(seq, song);
			} catch (InvalidMidiDataException e) {
				System.out.println("Failed to create Sequence");
				e.printStackTrace();
			}
		}
		return seq;
	}
	
	/**
	 * speichert eine {@link Sequence}, als midi0- oder midi1-Datei, je nach Anzahl an {@link Track}s auf der Sequence
	 * @param seq
	 * @param outputFile
	 */
	public void saveSequence(Sequence seq, File outputFile){
		if(seq.getTracks().length == 1){
			try {
				MidiSystem.write(seq, 0, outputFile);
			} catch (IOException e) {
				System.out.println("Failed to save Sequence " + seq + " in File "+ outputFile);
				e.printStackTrace();
			}
		}else{
			try {
				MidiSystem.write(seq, 1, outputFile);
			} catch (IOException e) {
				System.out.println("Failed to save Sequence " + seq + " in File "+ outputFile);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * lädt eine Sequence aus einer .midi-Datei
	 * @param file
	 * @return
	 */
	public Sequence loadSequence(File file){
		Sequence seq = null;
		try {
			seq = MidiSystem.getSequence(file);
		} catch (InvalidMidiDataException | IOException e) {
			System.out.println("Failed to load Sequence form file " + file);
			e.printStackTrace();
		}
		return seq;
	}
	
}
