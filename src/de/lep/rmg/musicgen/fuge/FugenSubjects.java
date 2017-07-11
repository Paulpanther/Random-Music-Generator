package de.lep.rmg.musicgen.fuge;

import java.util.ArrayList;

import de.lep.rmg.model.Part;
import de.lep.rmg.model.notes.INote;

/**
 * 
 * Speichert grundsätzliche eigenschafften einer Fuge, ergänzend zum {@link SongConfig}.<br>
 * Dazu gehören Stimmenanzahl, Thema und Gegenthema.<br>
 * Wird benötigt, um diese Informationen gebündelt von {@link FugenGenerator#generateSong(de.lep.rmg.model.SongConfig)}<br>
 * an untergeordnete Methoden weiterzugeben.
 * 
 */
public class FugenInfo {
	private ArrayList<INote> subjectList, antiSubjectList;
	private Part subjectPart, antiSubjectPart;
	private int voices;
	
	protected FugenInfo(ArrayList<INote> subjectList,ArrayList<INote> antiSubjectList,
			Part subjectPart, Part antiSubjectPart, int voices){
		this.subjectList = subjectList;
		this.antiSubjectList = antiSubjectList;
		this.subjectPart = subjectPart;
		this.antiSubjectPart = antiSubjectPart;
		this.voices = voices;
 	}	
	
	ArrayList<INote> getSubjectList(){
		return subjectList;
	}
	ArrayList<INote> getAntiSubjectList(){
		return antiSubjectList;
	}
	Part getSubjectPart(){
		return subjectPart;
	}
	Part getAntiSubjectPart(){
		return antiSubjectPart;
	}
	int getVoices(){
		return voices;
	}
}
