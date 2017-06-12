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
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.helper.RandomHelper;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.instruments.helper.InstrumentHelper;
import de.lep.rmg.model.notes.CType;
import de.lep.rmg.model.notes.SChord;
import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.out.midi.MidiPlayer;
import de.lep.rmg.out.xml.XMLException;
import de.lep.rmg.view.ISongChangeObserver;

public class FugenControllPanel extends ControllPanel{
	private static final long serialVersionUID = 1L;
	
	//auzuzeigende Komponenten, die nicht von ControllPanel geerbt werden
	JLabel chordNrLabel;
	JSlider chordNrSlider;
	JLabel repeatsLabel;
	JSlider repeatsSlider;
	JLabel instrumentNrLabel;
	JSlider instrumentNrSlider;
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

	/**
	 * erstellt ein neues JPanel
	 * @param musicGen - {@link CanonGenerator} der den {@link Song} generiert
	 * @param midiPlayer - midiPlayer zum Abspielen des generierten Songs
	 * @param instruments - Anzahl an Instrumenten die ausgewählt werden sollen
	 */
	public FugenControllPanel(IMusicGenerator musicGen, MidiPlayer midiPlayer){
		super();
		
		//die Anzahl der Reihen hängt von der Anzahl an Instrumenten ab
		setLayout(new GridLayout( 10, 2 ));//wird in setInstrumentNr() noch verändert
		observers = new ArrayList<ISongChangeObserver>();
		this.musicGen = musicGen;
		this.midiPlayer = midiPlayer;
		startButton = new JButton("Generiere " + musicGen.getSongType());
		ActionHandler aH = new ActionHandler();
		startButton.addActionListener(aH);
		randomButton = new JButton("Zufällige Werte");
		randomButton.setToolTipText("Setzt zufällige Werte ohne einen Song zu generieren");
		randomButton.addActionListener(aH);
		chordNrLabel = new JLabel("Themenlänge in Takten:");
		chordNrSlider = new JSlider(1, 4, 2);
		chordNrSlider.setMajorTickSpacing(2);
		chordNrSlider.setPaintTicks(true);
		chordNrSlider.setPaintLabels(true);
		repeatsLabel = new JLabel("Durchführungen:");
		repeatsSlider = new JSlider(1, 3, 2);
		repeatsSlider.setMajorTickSpacing(2);
		repeatsSlider.setPaintLabels(true);
		instrumentNrLabel = new JLabel("Stimmenanzahl:");
		instrumentNrSlider = new JSlider( 2, 6, 3);
		instrumentNrSlider.setMajorTickSpacing(2);
		instrumentNrSlider.setPaintTicks(true);
		instrumentNrSlider.setPaintLabels(true);
		instrumentNrSlider.addChangeListener(new ChangeHandler());
		instrumentsLabel = new JLabel("Instrumente");
		volumeLabel = new JLabel("Lautstärke");
		volumeLabel.setToolTipText("Anschlagstärke (velocity) in MIDI");
		keyLabel = new JLabel("Grundton:");
		keyComboBox = new JComboBox<String>(new String[] {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "H", "Zufall"});
		keyTypeLabel = new JLabel("Tonart:");
		keyTypeComboBox = new JComboBox<String>(new String[] {"Dur", "Moll", "Zufall"});

		add(chordNrLabel);
		add(chordNrSlider);
		add(repeatsLabel);
		add(repeatsSlider);
		add(instrumentNrLabel);
		add(instrumentNrSlider);
		add(instrumentsLabel);
		add(volumeLabel);

		//initializing all Instrument-ComboBoxes
		instrument6ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
		instrument6ComboBox.setSelectedIndex(/*Random*/ InstrumentHelper.getAllInstrNumber());
		volume6Slider = new JSlider(0, 120, 100);
		volume6Slider.setMajorTickSpacing(24);
		volume6Slider.setMinorTickSpacing(8);
		volume6Slider.setPaintTicks(true);
		volume6Slider.setPaintLabels(true);
		instrument5ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
		instrument5ComboBox.setSelectedIndex(InstrumentHelper.getAllInstrNumber());
		volume5Slider = new JSlider(0, 120, 100);
		volume5Slider.setMajorTickSpacing(24);
		volume5Slider.setMinorTickSpacing(8);
		volume5Slider.setPaintTicks(true);
		volume5Slider.setPaintLabels(true);
		instrument4ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
		instrument4ComboBox.setSelectedIndex(InstrumentHelper.getAllInstrNumber());
		volume4Slider = new JSlider(0, 120, 100);
		volume4Slider.setMajorTickSpacing(24);
		volume4Slider.setMinorTickSpacing(8);
		volume4Slider.setPaintTicks(true);
		volume4Slider.setPaintLabels(true);
		instrument3ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
		instrument3ComboBox.setSelectedIndex(InstrumentHelper.getAllInstrNumber());
		volume3Slider = new JSlider(0, 120, 100);
		volume3Slider.setMajorTickSpacing(24);
		volume3Slider.setMinorTickSpacing(8);
		volume3Slider.setPaintTicks(true);
		volume3Slider.setPaintLabels(true);
		instrument2ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
		instrument2ComboBox.setSelectedIndex(InstrumentHelper.getAllInstrNumber());
		volume2Slider = new JSlider(0, 120, 100);
		volume2Slider.setMajorTickSpacing(24);
		volume2Slider.setMinorTickSpacing(8);
		volume2Slider.setPaintTicks(true);
		volume2Slider.setPaintLabels(true);
		instrument1ComboBox = new JComboBox<Instrument>(InstrumentHelper.getAllInstrIncNull());
		instrument1ComboBox.setSelectedIndex(/*Piano*/ 4);
		volume1Slider = new JSlider(0, 120, 100);
		volume1Slider.setMajorTickSpacing(24);
		volume1Slider.setMinorTickSpacing(8);
		volume1Slider.setPaintTicks(true);
		volume1Slider.setPaintLabels(true);
		
		//adding instrument specific components in order
		add(instrument1ComboBox);
		add(volume1Slider);
		add(instrument2ComboBox);
		add(volume2Slider);
		setInstrumentNr(instrumentNrSlider.getValue());

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
				startMusicGenerator();
			}else{
				if(aE.getSource() == randomButton){
					setRandomValues();
				}
			}

		}
	}
	
	/**
	 * setzt komplett zufällige Werte außer bei den Lautstärken
	 */
	private void setRandomValues(){
		Random rand = RandomHelper.getRandom();
		chordNrSlider.setValue( rand.nextInt( chordNrSlider.getMaximum() - chordNrSlider.getMinimum()) + chordNrSlider.getMinimum());
		repeatsSlider.setValue( rand.nextInt( repeatsSlider.getMaximum() - repeatsSlider.getMinimum()) + repeatsSlider.getMinimum());
		//bei den ComboBoxen wird das Element Zufall nicht angewählt, daher die -1
		keyComboBox.setSelectedIndex(rand.nextInt(keyComboBox.getItemCount() - 1));//12 mögliche Grundtöne
		keyTypeComboBox.setSelectedIndex(rand.nextInt(keyTypeComboBox.getItemCount() - 1));//Dur oder Moll
		instrumentNrSlider.setValue( rand.nextInt( instrumentNrSlider.getMaximum() - instrumentNrSlider.getMinimum()) + instrumentNrSlider.getMinimum());//ChangeListener wird bei verändertem Wert automatisch informiertt
		switch( instrumentNrSlider.getValue() ){
		case 6:	
			instrument6ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
		case 5:
			instrument5ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
		case 4:
			instrument4ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
		case 3:
			instrument3ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
		case 2:
			instrument2ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
		case 1:
			instrument1ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
		}
	}

	private void startMusicGenerator(){
		Random rand = new Random();

		//falls Element "Zufall" in keyComboBox vom Nutzer gewählt wird
		if(keyComboBox.getSelectedIndex() == keyComboBox.getItemCount() - 1){
			//set random keytone
			keyComboBox.setSelectedIndex(rand.nextInt(keyComboBox.getItemCount() - 1));
		}

		//falfs Element "Zufall" in keyTypeComboBox vom Nutzer gewählt wird
		if(keyTypeComboBox.getSelectedIndex() == keyTypeComboBox.getItemCount() - 1){
			//set random keyType (Tonart)
			keyTypeComboBox.setSelectedIndex(rand.nextInt(keyTypeComboBox.getItemCount() - 1));
		}

		//speichert keyType in Variable cType
		CType ctype = CType.MAJOR;
		if(keyTypeComboBox.getSelectedIndex() == 1){
			ctype = CType.MINOR;
		}

		//wählt zufällige Instrumente aus falls der Nutzer "Zufall" wählt
		switch( instrumentNrSlider.getValue() ){
		case 6:	if(instrument6ComboBox.getSelectedIndex() == InstrumentHelper.getAllInstrNumber()){
			instrument6ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
			}
		case 5:	if(instrument5ComboBox.getSelectedIndex() == InstrumentHelper.getAllInstrNumber()){
			instrument5ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
			}
		case 4:	if(instrument4ComboBox.getSelectedIndex() == InstrumentHelper.getAllInstrNumber()){
			instrument4ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
			}
		case 3:	if(instrument3ComboBox.getSelectedIndex() == InstrumentHelper.getAllInstrNumber()){
			instrument3ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
			}
		case 2:	if(instrument2ComboBox.getSelectedIndex() == InstrumentHelper.getAllInstrNumber()){
			instrument2ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
			}
		case 1:	if(instrument1ComboBox.getSelectedIndex() == InstrumentHelper.getAllInstrNumber()){
			instrument1ComboBox.setSelectedIndex(rand.nextInt(InstrumentHelper.getAllInstrNumber()));
			}
		}
		//speichert die Instrumente in ein Array
		Instrument[] instruments = new Instrument[instrumentNrSlider.getValue()];
		instruments[0] = instrument1ComboBox.getItemAt(instrument1ComboBox.getSelectedIndex());
		instruments[0].setVolume(volume1Slider.getValue());
		if(instrumentNrSlider.getValue() > 1){
			instruments[1] = instrument2ComboBox.getItemAt(instrument2ComboBox.getSelectedIndex());
			instruments[1].setVolume(volume2Slider.getValue());
			if(instrumentNrSlider.getValue() > 2){
				instruments[2] = instrument3ComboBox.getItemAt(instrument3ComboBox.getSelectedIndex());
				instruments[2].setVolume(volume3Slider.getValue());
				if(instrumentNrSlider.getValue() > 3){
					instruments[3] = instrument4ComboBox.getItemAt(instrument4ComboBox.getSelectedIndex());
					instruments[3].setVolume(volume4Slider.getValue());
					if(instrumentNrSlider.getValue() > 4){
						instruments[4] = instrument5ComboBox.getItemAt(instrument5ComboBox.getSelectedIndex());
						instruments[4].setVolume(volume5Slider.getValue());
						if(instrumentNrSlider.getValue() > 5){
							instruments[5] = instrument6ComboBox.getItemAt(instrument6ComboBox.getSelectedIndex());
							instruments[5].setVolume(volume6Slider.getValue());
						}
					}
				}
			}
		}
		//macht das SongConfig und generiert einen Song mit dem MusicGenerator
		SongConfig config = new SongConfig(chordNrSlider.getValue(), repeatsSlider.getValue(), instrumentNrSlider.getValue(), 4,
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
	
	private class ChangeHandler implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent cE) {
			if(cE.getSource() == instrumentNrSlider)
				setInstrumentNr(instrumentNrSlider.getValue());
		}
		
	}
	
	private void setInstrumentNr(int instrNr) {
		//entferntComponenten, die eventuell zu viel sind
		remove(instrument3ComboBox);
		remove(volume3Slider);
		remove(instrument4ComboBox);
		remove(volume4Slider);
		remove(instrument5ComboBox);
		remove(volume5Slider);
		remove(instrument6ComboBox);
		remove(volume6Slider);
		//Layout an InstrumentenAnzahl anpassen
		setLayout(new GridLayout( 7 + instrNr, 2));
		//instrumentenspezifische Componenten hinzufügen
		if(instrNr > 2){
			add(instrument3ComboBox, 12);//TODO remove hard coded indices, replace by dynamic approach
			add(volume3Slider, 13);
			if(instrNr > 3){
				add(instrument4ComboBox, 14);
				add(volume4Slider, 15);
				if(instrNr > 4){
					add(instrument5ComboBox, 16);
					add(volume5Slider, 17);
					if(instrNr > 5){
						add(instrument6ComboBox, 18);
						add(volume6Slider, 19);
					}
				}
			}
		}
		doLayout();
		repaint();
	}

}
