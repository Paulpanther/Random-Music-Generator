package de.lep.rmg.musicgen.fuge;

import de.lep.rmg.model.Part;

/**
 * 
 * Speichert grundsätzliche eigenschafften einer Fuge, ergänzend zum {@link SongConfig}.<br>
 * Dazu gehören Stimmenanzahl, Thema und Gegenthema.<br>
 * Wird benötigt, um diese Informationen gebündelt von {@link FugenGenerator#generateSong(de.lep.rmg.model.SongConfig)}<br>
 * an untergeordnete Methoden weiterzugeben.
 * 
 */
public class FugenInfo {
	private Part Subject, antiSubject;
	private int voices;
	
	protected FugenInfo(Part Subject, Part antiSubject, int voices){
		this.Subject = Subject;
		this.antiSubject = antiSubject;
		this.voices = voices;
 	}	
	
	Part getSubject(){
		return Subject;
	}
	Part getAntiSubject(){
		return antiSubject;
	}
	int getVoices(){
		return voices;
	}
}
