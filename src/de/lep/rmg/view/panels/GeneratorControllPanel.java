package de.lep.rmg.view.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.midi.Sequence;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.instruments.helper.InstrumentHelper;
import de.lep.rmg.model.notes.CType;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.out.midi.MidiPlayer;
import de.lep.rmg.out.midi.SequenceGenerator;
import de.lep.rmg.out.xml.XMLException;
import de.lep.rmg.out.xml.XMLGenerator;
import de.lep.rmg.view.IGeneratorPanel;
import de.lep.rmg.view.ISongChangeObserver;
import de.lep.rmg.view.ISongChanger;

/**
 * With this {@link JPanel} the user can set the configuration values (of the {@link SongConfig})
 * and start the {@link MusicGenerator}.<br>
 * <br>
 * Mit diesem {@link JPanel} kann der Nutzer die Startwerte des {@link SongConfig}
 * festlegen (z.B. Grundton, Musiḱinstrumente, ...) und den {@link MusicGenerator} starten.
 */
public class GeneratorControllPanel extends JPanel implements ISongChanger, IGeneratorPanel{
	private static final long serialVersionUID = 1L;
	//Beobachterliste
	private ArrayList<ISongChangeObserver> observers;
	//zu kontrollierender MusicGenerator
	IMusicGenerator musicGen;
	//MidiPlayer dem die fertige Sequence zum abspielen übergeben wird
	MidiPlayer midiPlayer;
	//SequenceGenerator für Midi-Ausgabe
	SequenceGenerator seqGen = new SequenceGenerator();
	//XMLGenerator für XML-Ausgabe
	XMLGenerator xmlGen = new XMLGenerator();
	//auzuzeigende Komponenten
	JButton startButton;
	JButton randomButton;
	JLabel chordNrLabel;
	JSlider chordNrSlider;
	JLabel chordDurationLabel;
	JSlider chordDurationSlider;
	JLabel repeatsLabel;
	JSlider repeatsSlider;
	JLabel keyLabel;
	JComboBox<String> keyComboBox;
	JLabel keyTypeLabel;
	JComboBox<String> keyTypeComboBox;
	JLabel instrumentsLabel;
	JLabel volumeLabel;
	JComboBox<Instrument> instrument1ComboBox;
	JSlider volume1Slider;
	JComboBox<Instrument> instrument2ComboBox;
	JSlider volume2Slider;
	JComboBox<Instrument> instrument3ComboBox;
	JSlider volume3Slider;
	JComboBox<Instrument> instrument4ComboBox;
	JSlider volume4Slider;
	JComboBox<Instrument> instrument5ComboBox;
	JSlider volume5Slider;
	JComboBox<Instrument> instrument6ComboBox;
	JSlider volume6Slider;
	//Anzahl der activierten instrumentComboBoxen
	int instr;
	//Anzahl an wählbaren Instrumenten
	int instrNumber = InstrumentHelper.getAllInstrNumber();
	
	/**
	 * erstellt ein neues JPanel
	 * @param musicGen - {@link MusicGenerator} der den {@link Song} generiert
	 * @param midiPlayer - midiPlayer zum Abspielen des generierten Songs
	 * @param instruments - Anzahl an Instrumenten die ausgewählt werden sollen
	 */
	public GeneratorControllPanel(IMusicGenerator musicGen, MidiPlayer midiPlayer, int instruments){
		super();
		instr = instruments;
		if( instruments < 1 )
			instr = 1;
		else
			if( instruments > 6 )
				instr = 6;
		//die Anzahl der Reihen hängt von der Anzahl an Instrumenten ab
		setLayout(new GridLayout( 7 + instr, 2 ));
		observers = new ArrayList<ISongChangeObserver>();
		this.musicGen = musicGen;
		this.midiPlayer = midiPlayer;
		startButton = new JButton("Generiere " + musicGen.getSongType());
		ActionHandler aH = new ActionHandler();
		startButton.addActionListener(aH);
		randomButton = new JButton("Zufällige Werte");
		randomButton.setToolTipText("Setzt zufällige Werte ohne einen Song zu generieren");
		randomButton.addActionListener(aH);
		chordNrLabel = new JLabel("Akkorde pro Melodie:");
		chordNrSlider = new JSlider(1, 16, 4);
		chordNrSlider.setMajorTickSpacing(4);
		chordNrSlider.setPaintTicks(true);
		chordNrSlider.setPaintLabels(true);
		chordDurationLabel = new JLabel("Akkorddauer:");
		chordDurationLabel.setToolTipText("Dauer eines Akkords in Vierteln eines Takts");
		chordDurationSlider = new JSlider(1, 4, 2);
		chordDurationSlider.setMajorTickSpacing(3);
		chordDurationSlider.setPaintLabels(true);
		repeatsLabel = new JLabel("Wiederholungen:");
		repeatsSlider = new JSlider(1, 3, 2);
		repeatsSlider.setMajorTickSpacing(2);
		repeatsSlider.setPaintLabels(true);
		keyLabel = new JLabel("Grundton:");
		keyComboBox = new JComboBox<String>(new String[] {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "H", "Zufall"});
		keyTypeLabel = new JLabel("Tonart:");
		keyTypeComboBox = new JComboBox<String>(new String[] {"Dur", "Moll", "Zufall"});
		instrumentsLabel = new JLabel("Instrumente");
		volumeLabel = new JLabel("Lautstärke");
		volumeLabel.setToolTipText("Anschlagstärke (velocity) in MIDI");

		add(chordNrLabel);
		add(chordNrSlider);
		add(chordDurationLabel);
		add(chordDurationSlider);
		add(repeatsLabel);
		add(repeatsSlider);
		add(instrumentsLabel);
		add(volumeLabel);
		
		//initializes a number of components depending on the number of instruments
		switch( instr ){
		case 6:	instrument6ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
			instrument6ComboBox.setSelectedIndex(/*Random*/ InstrumentHelper.getAllInstrNumber());
			volume6Slider = new JSlider(0, 120, 100);
			volume6Slider.setMajorTickSpacing(24);
			volume6Slider.setMinorTickSpacing(8);
			volume6Slider.setPaintTicks(true);
			volume6Slider.setPaintLabels(true);
		case 5:	instrument5ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
			instrument5ComboBox.setSelectedIndex(InstrumentHelper.getAllInstrNumber());
			volume5Slider = new JSlider(0, 120, 100);
			volume5Slider.setMajorTickSpacing(24);
			volume5Slider.setMinorTickSpacing(8);
			volume5Slider.setPaintTicks(true);
			volume5Slider.setPaintLabels(true);
		case 4:	instrument4ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
			instrument4ComboBox.setSelectedIndex(InstrumentHelper.getAllInstrNumber());
			volume4Slider = new JSlider(0, 120, 100);
			volume4Slider.setMajorTickSpacing(24);
			volume4Slider.setMinorTickSpacing(8);
			volume4Slider.setPaintTicks(true);
			volume4Slider.setPaintLabels(true);
		case 3:	instrument3ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
			instrument3ComboBox.setSelectedIndex(InstrumentHelper.getAllInstrNumber());
			volume3Slider = new JSlider(0, 120, 100);
			volume3Slider.setMajorTickSpacing(24);
			volume3Slider.setMinorTickSpacing(8);
			volume3Slider.setPaintTicks(true);
			volume3Slider.setPaintLabels(true);
		case 2:	instrument2ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
			instrument2ComboBox.setSelectedIndex(InstrumentHelper.getAllInstrNumber());
			volume2Slider = new JSlider(0, 120, 100);
			volume2Slider.setMajorTickSpacing(24);
			volume2Slider.setMinorTickSpacing(8);
			volume2Slider.setPaintTicks(true);
			volume2Slider.setPaintLabels(true);
		case 1:	instrument1ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
			instrument1ComboBox.setSelectedIndex(/*Piano*/ 4);
			volume1Slider = new JSlider(0, 120, 100);
			volume1Slider.setMajorTickSpacing(24);
			volume1Slider.setMinorTickSpacing(8);
			volume1Slider.setPaintTicks(true);
			volume1Slider.setPaintLabels(true);
		}
		//adding instrument specific components in order
		add(instrument1ComboBox);
		add(volume1Slider);
		
		if(instr > 1){
			add(instrument2ComboBox);
			add(volume2Slider);
			if(instr > 2){
				add(instrument3ComboBox);
				add(volume3Slider);
				if(instr > 3){
					add(instrument4ComboBox);
					add(volume4Slider);
					if(instr > 4){
						add(instrument5ComboBox);
						add(volume5Slider);
						if(instr > 5){
							add(instrument6ComboBox);
							add(volume6Slider);
						}
					}
				}
			}
		}
		
		add(keyLabel);
		add(keyComboBox);
		add(keyTypeLabel);
		add(keyTypeComboBox);
		add(startButton);
		add(randomButton);
	}
	
	/**
	 * starts the MusicGenerator with the user input, saves the resulting Song in standardFiles and starts the MidiPlayer<br>
	 * 
	 * this type of ActionHandler is only given to the start-button
	 */
	private class ActionHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent aE) {
			if(aE.getSource() == startButton){
				//Prüft Werte auf Stimmigkeit:
				//Die Anzahl der Akkorde mal die Länge eines Akkords muss ein Vielfaches eines ganzen Takts ergeben
				if((chordNrSlider.getValue() * chordDurationSlider.getValue()) % 4 == 0 ){
					startMusicGenerator();
				}else{
					int answer = JOptionPane.showConfirmDialog(null, ("Die Anzahl der Akkorde mal die Länge eines Akkords muss" +
							" ein Vielfaches eines ganzen Takts ergeben! Mit automatisch korrigierten Werten fortfahren?"),
							"", JOptionPane.OK_CANCEL_OPTION);
					if(answer == JOptionPane.OK_OPTION){
						//Passt Werte an Vorgaben für SongConfig an
						while((chordNrSlider.getValue() * chordDurationSlider.getValue()) % 4 != 0 ){
							int currentValue = chordNrSlider.getValue();
							if( currentValue < 12){
								currentValue++;
								chordNrSlider.setValue(currentValue);
							}else{
								currentValue--;
								chordNrSlider.setValue(currentValue);
							}
						}
						startMusicGenerator();
					}
				}
				
			}else{
				if(aE.getSource() == randomButton){
					Random rand = new Random();
					//setzt komplett zufällige Werte außer bei den Lautstärken
					chordNrSlider.setValue(rand.nextInt(16) + 1);
					chordDurationSlider.setValue(rand.nextInt(4) + 1);
					repeatsSlider.setValue(rand.nextInt(3) + 1);
					//bei den ComboBoxen wird das Element Zufall nicht angewählt
					keyComboBox.setSelectedIndex(rand.nextInt(12));
					keyTypeComboBox.setSelectedIndex(rand.nextInt(2));
					switch( instr ){
					case 6:	if(instrument6ComboBox.getSelectedIndex() == instrNumber){
						instrument6ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
					}
					case 5:	if(instrument5ComboBox.getSelectedIndex() == instrNumber){
						instrument5ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
					}
					case 4:	if(instrument4ComboBox.getSelectedIndex() == instrNumber){
						instrument4ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
					}
					case 3:	if(instrument3ComboBox.getSelectedIndex() == instrNumber){
						instrument3ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
					}
					case 2:	if(instrument2ComboBox.getSelectedIndex() == instrNumber){
						instrument2ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
					}
					case 1:	if(instrument1ComboBox.getSelectedIndex() == instrNumber){
						instrument1ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
					}
					}
					//Passt Werte an Vorgaben für SongConfig an
					//Die Anzahl an Akkorden mal die Länge eines Akkords muss ein Vielfaches eines ganzen Takts ergeben
					while((chordNrSlider.getValue() * chordDurationSlider.getValue()) % 4 != 0 ){
						int currentValue = chordNrSlider.getValue();
						if( currentValue < 12){
							currentValue++;
							chordNrSlider.setValue(currentValue);
						}else{
							currentValue--;
							chordNrSlider.setValue(currentValue);
						}
					}
				}
			}
		}
		
	}
	
	private void startMusicGenerator(){
		Random rand = new Random();
		
		//falls Element "Zufall" in keyComboBox vom Nutzer gewählt wird
		if(keyComboBox.getSelectedIndex() == 12){
			//set random keytone
			keyComboBox.setSelectedIndex(rand.nextInt(12));
		}

		//falfs Element "Zufall" in keyTypeComboBox vom Nutzer gewählt wird
		if(keyTypeComboBox.getSelectedIndex() == 2){
			//set random keyType (Tonart)
			keyTypeComboBox.setSelectedIndex(rand.nextInt(2));
		}

		//speichert keyType in Variable cType
		CType ctype = CType.MAJOR;
		if(keyTypeComboBox.getSelectedIndex() == 1){
			ctype = CType.MINOR;
		}

		//wählt zufällige Instrumente aus falls der Nutzer "Zufall" wählt
		switch( instr ){
		case 6:	if(instrument6ComboBox.getSelectedIndex() == instrNumber){
			instrument6ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
		}
		case 5:	if(instrument5ComboBox.getSelectedIndex() == instrNumber){
			instrument5ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
		}
		case 4:	if(instrument4ComboBox.getSelectedIndex() == instrNumber){
			instrument4ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
		}
		case 3:	if(instrument3ComboBox.getSelectedIndex() == instrNumber){
			instrument3ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
		}
		case 2:	if(instrument2ComboBox.getSelectedIndex() == instrNumber){
			instrument2ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
		}
		case 1:	if(instrument1ComboBox.getSelectedIndex() == instrNumber){
			instrument1ComboBox.setSelectedIndex(rand.nextInt(instrNumber));
		}
		}
		//speichert die Instrumente in ein Array
		Instrument[] instruments = new Instrument[instr];
		instruments[0] = instrument1ComboBox.getItemAt(instrument1ComboBox.getSelectedIndex());
		instruments[0].setVolume(volume1Slider.getValue());
		if(instr > 1){
			instruments[1] = instrument2ComboBox.getItemAt(instrument2ComboBox.getSelectedIndex());
			instruments[1].setVolume(volume2Slider.getValue());
			if(instr > 2){
				instruments[2] = instrument3ComboBox.getItemAt(instrument3ComboBox.getSelectedIndex());
				instruments[2].setVolume(volume3Slider.getValue());
				if(instr > 3){
					instruments[3] = instrument4ComboBox.getItemAt(instrument4ComboBox.getSelectedIndex());
					instruments[3].setVolume(volume4Slider.getValue());
					if(instr > 4){
						instruments[4] = instrument5ComboBox.getItemAt(instrument5ComboBox.getSelectedIndex());
						instruments[4].setVolume(volume5Slider.getValue());
						if(instr > 5){
							instruments[5] = instrument6ComboBox.getItemAt(instrument6ComboBox.getSelectedIndex());
							instruments[5].setVolume(volume6Slider.getValue());
						}
					}
				}
			}
		}
		//macht das SongConfig und generiert einen Song mit dem MusicGenerator
		SongConfig config = new SongConfig(chordNrSlider.getValue(), repeatsSlider.getValue(), instr, chordDurationSlider.getValue(),
				new SChord(keyComboBox.getSelectedIndex(), ctype), instruments);
		Song song = musicGen.generateSong(config);
		//standard Speicherung
		try {
			xmlGen.save( new File( "res/saves/standardFile.xml" ), song );
		} catch ( XMLException e ) {
			e.printStackTrace();
		}
		Sequence seq = seqGen.createSequence(song, false);
		seqGen.saveSequence( seq, new File( "res/saves/standardFile.midi" ) );
		//informiert Beobachter
		for(ISongChangeObserver sco : observers){
			sco.songChange(song);
		}
		//spielt den Song ab
		midiPlayer.play(seq);
	}
	
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

	@Override
	public void setGenerator(IMusicGenerator musicGen) {
		this.musicGen = musicGen;
	}
	
}
