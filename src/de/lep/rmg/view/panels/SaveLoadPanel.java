package de.lep.rmg.view.panels;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.lep.rmg.model.Song;
import de.lep.rmg.out.midi.MidiPlayer;
import de.lep.rmg.out.midi.SequenceGenerator;
import de.lep.rmg.view.ISongChangeObserver;
import de.lep.rmg.view.ISongChanger;
import de.lep.rmg.view.SaveDialog;

/**
 * {@link JPanel} for save-/loadoperations and closing the programm.<br>
 * <br>
 * {@link JPanel} zum Speichern und Laden von Songs und zum beenden des Programms.
 */
public class SaveLoadPanel extends JPanel implements ISongChangeObserver{
	private static final long serialVersionUID = 1L;
	
	//MidiPlayer dem der geladene Song übergeben wird
	MidiPlayer midiPlayer;
	//wird zum Laden von Sequencen aus Dateien benötigt
	SequenceGenerator seqGen = new SequenceGenerator();
	
	//anzuzeigende Komponenten
	JButton saveButton;
	JButton loadButton;
	JLabel notifications;
	
	//Song der gespeichert werden kann
	Song song;
	
	public SaveLoadPanel(MidiPlayer midiPlayer, ISongChanger songChanger){
		initialize(null, midiPlayer, songChanger);
	}
	
	public SaveLoadPanel(Song song, MidiPlayer midiPlayer, ISongChanger songChanger){
		initialize(song, midiPlayer, songChanger);
	}
	/**
	 * Initialisationsmethode, die in jedem Konstruktor genutzt wird
	 * @param song
	 * @param midiPlayer
	 * @param songChanger
	 */
	private void initialize(Song song, MidiPlayer midiPlayer, ISongChanger songChanger){
		songChanger.addSongChangeObserver(this);
		this.song = song;
		this.midiPlayer = midiPlayer;
		setLayout(new FlowLayout());
		saveButton = new JButton("Speichern");
		loadButton = new JButton("Laden");
		ActionHandler aH = new ActionHandler();
		saveButton.addActionListener(aH);
		loadButton.addActionListener(aH);
		notifications = new JLabel();
		add(saveButton);
		add(loadButton);
		add(notifications);
	}
	
	/**
	 * handelt die von den Knöpfen erzeugten {@link actionEvents}
	 */
	private class ActionHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent aE) {
			if(aE.getSource() == saveButton){
				//startet die Speichersequenz im SaveDialog
				new SaveDialog(song);
			}else{
				if(aE.getSource() == loadButton){
					//lässt den Nutzer mit einem JFileChosser eine Datei zum Öffnen aussuchen
					JFileChooser fileCho = new JFileChooser(new File( "res/saves" ));
					if(fileCho.showOpenDialog(SaveLoadPanel.this) == JFileChooser.APPROVE_OPTION){
						File openFile = fileCho.getSelectedFile();
						if(openFile.getPath().endsWith(".xml")){
							//TODO not yet implemented
							notifications.setText("Noch nicht implementiert. Nur '.midi' Dateien können gelesen werden!");
							new Thread(new RemoveLabelTextJob());
						}else{
							if(openFile.getPath().endsWith(".midi")){
								//liest die Datei und spielt den Song
								SaveLoadPanel.this.setSong(null);
								midiPlayer.play(seqGen.loadSequence(openFile));
							}else{
								notifications.setText("Inkompatibler Dateityp!");
								//zeigt die Fehler für acht Sekunden
								new Thread(new RemoveLabelTextJob());
							}
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * sets {@link Song} to save
	 * @param song
	 */
	public void setSong(Song song){
		this.song = song;
		if(song == null){
			saveButton.setEnabled(false);
		}else{
			saveButton.setEnabled(true);
		}
	}
	
	/**
	 * removes the notificationLabels text
	 */
	private class RemoveLabelTextJob implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			notifications.setText("");
		}
		
	}

	@Override
	public void songChange(Song song) {
		setSong(song);
	}
	
}
