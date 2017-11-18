package de.lep.rmg.view.panels;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.out.midi.MidiPlayer;
import de.lep.rmg.out.midi.SequenceGenerator;
import de.lep.rmg.out.xml.XMLGenerator;
import de.lep.rmg.view.ISongChangeObserver;
import de.lep.rmg.view.ISongChanger;

/**
 * abstakte ElternKlasse für alle {@link JPanel}, die von {@link IMusicGenerator}en als
 * ControllPanel an das GUI weitergegeben werden können, um den IMusicGenerator zu starten.
 * Enthält Variablen und Methoden, die jedes solche ControllPanel verpflichtend enthalten muss.
 */
public abstract class ControllPanel extends JPanel implements ISongChanger{

	private static final long serialVersionUID = 1L;
	
	//Beobachterliste
	protected ArrayList<ISongChangeObserver> observers;
	//zu kontrollierender MusicGenerator
	IMusicGenerator musicGen;
	//MidiPlayer dem die fertige Sequence zum abspielen übergeben wird
	MidiPlayer midiPlayer;
	//SequenceGenerator für Midi-Ausgabe
	SequenceGenerator seqGen = new SequenceGenerator();
	//XMLGenerator für XML-Ausgabe
	XMLGenerator xmlGen = new XMLGenerator();
	//auzuzeigende Komponenten, müssen von erbenden Klassen initialisiert werden
	JButton startButton;
	JButton randomButton;
	JLabel keyLabel;
	JComboBox<String> keyComboBox;
	JLabel keyTypeLabel;
	JComboBox<String> keyTypeComboBox;
	
	/**
	 * registriert einen Beobachter
	 * @param sco : Beobachter der registriert werden soll
	 */
	@Override
	public void addSongChangeObserver(ISongChangeObserver sco) {
		observers.add(sco);
	}
	
	/**
	 * entfernt einen Beobachter
	 * @param sco : Beobachter der entfernt werden soll
	 */
	@Override
	public void removeSongChangeObserver(ISongChangeObserver sco) {
		observers.remove(sco);
	}
}
