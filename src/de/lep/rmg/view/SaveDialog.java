package de.lep.rmg.view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.out.midi.SequenceGenerator;
import de.lep.rmg.out.xml.XMLException;
import de.lep.rmg.out.xml.XMLGenerator;

/**
 * {@link JDialog} zum Speichern von {@link Song}.
 * Der Nutzer kann einen Titel einen Dateinamen und die Ausgabedateitypen(MIDI und XML) wählen
 * und anschließend mit einem {@link JFileChooser} den Speicherordner auswäklen.
 * 
 * @author Lukas
 */
public class SaveDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	//anzuzeigende Komponenten
	JPanel northPanel;
	JLabel textLabel;
	JTextField titleTextField;//hier wird der Titel eingegeben
	JPanel centerPanel;
	JLabel fileTypLabel;
	JCheckBox xmlBox;
	JCheckBox midi0Box;
	JCheckBox midi1Box;
	JButton cancelButton;
	JButton fileButton;
	
	//SequenceGenerator für MIDI-Ausgabe
	SequenceGenerator seqGen = new SequenceGenerator();
	//XMLGenerator für XML-Ausgabe
	XMLGenerator xmlGen = new XMLGenerator();
	
	//Song der gespeichert wird
	Song song;
	
	public SaveDialog(Song song){
		this.song = song;
		
		//Einstellungen am Dialogfenster
		JDialog.setDefaultLookAndFeelDecorated(true);
		setTitle("RMG-Songtitel wählen");
		setLayout(new GridLayout(3,1));
		setAlwaysOnTop(true);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//oberste Zeile der Komponenten
		northPanel = new JPanel(new GridLayout(1,2));
		textLabel = new JLabel("Wähle einen Titel für das Stück:");
		titleTextField = new JTextField();
		northPanel.add(textLabel);
		northPanel.add(titleTextField);
		
		//mittlere Zeile der Komponenten
		centerPanel = new JPanel(new FlowLayout());
		fileTypLabel = new JLabel("Wähle die Dateitypen, in denen der Song gespeichert wird");
		xmlBox = new JCheckBox("MusicXML");
		xmlBox.setSelected(true);
		midi0Box = new JCheckBox("MIDI Typ 0");
		midi0Box.setSelected(false);
		midi1Box = new JCheckBox("MIDI Typ 1");
		midi1Box.setSelected(true);
		centerPanel.add(fileTypLabel);
		centerPanel.add(midi0Box);
		centerPanel.add(midi1Box);
		centerPanel.add(xmlBox);
		
		//unterste Zeile der Komponenten
		fileButton = new JButton("Fertig"); 
		fileButton.addActionListener(new ActionHandler());
		
		//Einfügen der oben erstellten Komponenten
		add(northPanel);
		add(centerPanel);
		add(fileButton);
		pack();
	}
	
	private class ActionHandler implements ActionListener {
		/**
		 * speichert den {@link Song}
		 */
		@Override
		public void actionPerformed(ActionEvent aE) {
			if(aE.getSource() == fileButton){
				if(!xmlBox.isSelected() && !midi0Box.isSelected() && ! midi1Box.isSelected()){
					//do nothing
				}else{
					SongConfig config = song.getConfig();
					config.setTitle(titleTextField.getText());
					song.setConfig(config);
					//lets the user choose a savefile
					JFileChooser filechoose = new JFileChooser(new File( "res/saves" ));
					filechoose.setSelectedFile(new File(titleTextField.getText()));
					File saveFile;
					if(filechoose.showSaveDialog(SaveDialog.this) == JFileChooser.APPROVE_OPTION){
						saveFile = filechoose.getSelectedFile();
						String fileName = saveFile.getAbsolutePath();
						//XML-Output
						if(xmlBox.isSelected()){
							if(fileName.endsWith(".xml")){
								try {
									xmlGen.save(saveFile, song);
								} catch (XMLException e) {
									e.printStackTrace();
								}
							}else{
								if(fileName.endsWith(".midi")){
									try {
										//erstzt ".midi" mit ".xml"
										xmlGen.save(new File(fileName.replace(".midi", ".xml")), song);
									} catch (XMLException e) {
										e.printStackTrace();
									}
								}else{//falls filename weder mit ".xml" noch mit ".midi" endet
									try {
										//fügt Dateiendung hinzu
										xmlGen.save(new File(fileName.concat(".xml")), song);
									} catch (XMLException e) {
										e.printStackTrace();
									}
								}
							}
						}
						//MIDI0-Output gleicher Aufbau, wie für XML-Ausgabe
						if(midi0Box.isSelected()){
							if(fileName.endsWith(".midi")){
								seqGen.saveSequence(seqGen.createSequence(song, true), saveFile);
							}else{
								if(fileName.endsWith(".xml")){
									seqGen.saveSequence(seqGen.createSequence(song, true), new File(fileName.replace(".xml", ".midi")));
								}else{
									seqGen.saveSequence(seqGen.createSequence(song, true), new File(fileName.concat(".midi")));
								}
							}
						}
						//MIDI1-Output
						if(midi1Box.isSelected()){
							if(fileName.endsWith(".midi")){
								seqGen.saveSequence(seqGen.createSequence(song, false), saveFile);
							}else{
								if(fileName.endsWith(".xml")){
									seqGen.saveSequence(seqGen.createSequence(song, false), new File(fileName.replace(".xml", ".midi")));
								}else{
									seqGen.saveSequence(seqGen.createSequence(song, false), new File(fileName.concat(".midi")));
								}
							}
						}
					}
					//schließt den Dialog
					SaveDialog.this.dispose();
				}
			}
		}
		
	}
	
}
