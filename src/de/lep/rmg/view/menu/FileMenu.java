package de.lep.rmg.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import de.lep.rmg.model.Song;
import de.lep.rmg.out.midi.MidiPlayer;
import de.lep.rmg.out.midi.SequenceGenerator;
import de.lep.rmg.view.ISongChangeObserver;
import de.lep.rmg.view.ISongChanger;
import de.lep.rmg.view.SaveDialog;

/**
 * Menu in the {@link JMenuBar} for save-/loadoperations and closing the programm.<br>
 * <br>
 * Titelzeilenmenü zum Speichern und Laden von Songs und zum beenden des Programms.
 */
public class FileMenu extends JMenu implements ISongChangeObserver{
	private static final long serialVersionUID = 1L;
	//anzuzeigende Menüpunkte
	JMenuItem openMenu;
	JMenuItem saveMenu;
	JSeparator separator;
	JMenuItem exitMenu;
	//zuspeichernder Song
	Song song;
	//MidiPlayer zum abspielen eingelesener Songs
	MidiPlayer player;
	//zum erzeugen einer Sequence aus einer ".midi"-Datei
	SequenceGenerator seqGen = new SequenceGenerator();
	
	public FileMenu(MidiPlayer midiPlayer, ISongChanger songChanger){
		super("Datei");
		initialize(null, midiPlayer, songChanger);
	}
	
	public FileMenu(Song song, MidiPlayer midiPlayer, ISongChanger songChanger){
		super("Datei");
		initialize(song, midiPlayer, songChanger);
	}
	
	/**
	 * in jedem Konstruktor verwendete Initialisationsmethode
	 * @param song
	 * @param midiPlayer
	 * @param songChanger
	 */
	void initialize(Song song, MidiPlayer midiPlayer, ISongChanger songChanger){
		player = midiPlayer;
		songChanger.addSongChangeObserver(this);
		openMenu = new JMenuItem("Öffnen");
		saveMenu = new JMenuItem("Speichern");
		separator = new JSeparator();
		exitMenu = new JMenuItem("Beenden");
		ActionHandler aH = new ActionHandler();
		openMenu.addActionListener(aH);
		saveMenu.addActionListener(aH);
		exitMenu.addActionListener(aH);
		add(openMenu);
		add(saveMenu);
		add(separator);
		add(exitMenu);
		songChange( song );
	}

	/**
	 * behandelt die vom FileMenu ausgelösten {@link actionEvents}
	 */
	private class ActionHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent aE) {
			if(aE.getSource() == saveMenu){
				//started die Speichersequenz des SaveDialog
				new SaveDialog(song);
			}else{
				if(aE.getSource() == openMenu){
					//lässt den Nutzer die zu öffnende Datei mit einem JFileChooser aussuchen
					JFileChooser fileCho = new JFileChooser(new File( "res/saves" ));
					if(fileCho.showOpenDialog(FileMenu.this) == JFileChooser.APPROVE_OPTION){
						File openFile = fileCho.getSelectedFile();
						if(openFile.getPath().endsWith(".xml")){
							//TODO not yet implemented
							System.out.println("Noch nicht implementiert. Nur '.midi' Dateien können gelesen werden!");
							JOptionPane.showMessageDialog(null, "Das Öffnen von Music-XML-Formaten wurde noch nicht implementiert!\n"
									+ "Nur MIDI-Formate können eingelesen werden.");
						}else{
							if(openFile.getPath().endsWith(".midi")){
								//liest die Datei und spielt den Song
								FileMenu.this.songChange(null);
								player.play(seqGen.loadSequence(openFile));
							}else{
								System.out.println("Inkompatibler Dateityp!");
								JOptionPane.showMessageDialog(null, "Inkompatiebler Dateityp!\nÖffnen nicht möglich!");
							}
						}
					}
				}else{
					if(aE.getSource() == exitMenu){
						//beendet das Programm
						player.stop();
						player.close();
						System.exit(0);
					}
				}
			}
		}
		
	}
	
	/**
	 * setzt den zu speichernden {@link Song}
	 */
	@Override
	public void songChange(Song song) {
		this.song = song;
		if(song == null){
			saveMenu.setEnabled(false);
		}else{
			saveMenu.setEnabled(true);
		}
	}
	
}
