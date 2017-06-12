package de.lep.rmg;

import java.awt.FlowLayout;
import java.util.ArrayList;
//import java.io.File;

//import javax.sound.midi.Sequence;
import javax.swing.JMenuBar;

import de.lep.rmg.model.Song;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.helper.ArrayHelper;
import de.lep.rmg.model.helper.PercentPair;
import de.lep.rmg.model.helper.RandomHelper;
import de.lep.rmg.model.notes.helper.ChordHelper;
import de.lep.rmg.model.notes.helper.NoteHelper;
import de.lep.rmg.musicgen.ChordGenerator;
import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.musicgen.MelodyGenerator;
//import de.lep.rmg.model.Song;
//import de.lep.rmg.model.SongConfig;
import de.lep.rmg.musicgen.CanonGenerator;
import de.lep.rmg.musicgen.RhythmGenerator;
import de.lep.rmg.musicgen.fuge.FugenGenerator;
import de.lep.rmg.musicgen.fuge.FugenMelodyGenerator;
import de.lep.rmg.out.midi.MidiPlayer;
import de.lep.rmg.out.midi.TrackFactory;
import de.lep.rmg.out.xml.XMLGenerator;
//import de.lep.rmg.out.midi.SequenceGenerator;
//import de.lep.rmg.out.xml.XMLException;
//import de.lep.rmg.out.xml.XMLGenerator;
import de.lep.rmg.view.Window;
import de.lep.rmg.view.menu.FileMenu;
import de.lep.rmg.view.menu.GeneratorMenu;
import de.lep.rmg.view.panels.CanonControllPanel;
import de.lep.rmg.view.panels.PlayerControllPanel;

/**
 * Der Controller für den Musikgenerator.<br><br>
 * 
 * <h1>Struktur:</h1><br>
 * Das Programm ist nach der Model-View-Controller-Architektur aufgebaut.<br>
 * Die View befindet sich in {@link Window}, welche auch die Ausgabefunktionen für XML ({@link XMLGenerator} und MIDI {@link TrackFactory} ansteuert.<br>
 * Der {@link CanonGenerator} ist der Controller für die Musikgenerierung mit seinen Unterklassen {@link ChordGenerator}, {@link RhythmGenerator} und {@link MelodyGenerator}.<br>
 * Der Song wird in der Klasse {@link Song} gespeichert.<br>
 * Für die Modell-Klassen, welche im {@link ChordGenerator} und {@link MelodyGenerator}, 
 * aber auch beim Exportieren in eine Datei verwendet werden gibt es die Hilfsklassen {@link ChordHelper} und {@link NoteHelper}.<br>
 * Für diese Hilfsklassen gibt es die Unit-Tests {@link ChordHelperTest} und {@link NoteHelperTest}.<br>
 * Zusätzliche Hilfsklassen sind {@link ArrayHelper}, {@link RandomHelper} und {@link PercentPair}.<br>
 * Einige festen Einstellungen können noch in {@link SongConfig} geändert werden.<br><br>
 * 
 * Für genauere Erläuterungen zur Funktionsweise des Programms, siehe die Dokumentation.
 */
public class Controller {
	
	public static void main( String[] args ) {
		
		// makes a MidiPlayer and MusicGenerators for later use
		MidiPlayer player = new MidiPlayer();
		ArrayList<IMusicGenerator> musicGenList = new ArrayList<IMusicGenerator>();
		IMusicGenerator canonGen = new CanonGenerator(player, new MelodyGenerator());//CanonGenerator mit standard MelodyGenerator
		IMusicGenerator fuge = new FugenGenerator(player, new FugenMelodyGenerator());
		musicGenList.add(canonGen);
		musicGenList.add(fuge);
		
		//creates a new window and adds the standard components for this programm
		Window window = new Window(new FlowLayout(), player);
		
		//Panels
		CanonControllPanel ccP = new CanonControllPanel(canonGen, player, 3);//GeneratorControllPanel with 3 Instruments to choose
		window.add(ccP);
		window.add(new PlayerControllPanel(player));
//		window.add(new SaveLoadPanel(song, player, gcP));
		
		//MenuLeiste
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(new FileMenu(/*song*/ null, player, ccP));
		menuBar.add(new GeneratorMenu(window, ccP, musicGenList));
		window.setJMenuBar(menuBar);
		
//		SongConfig config = new SongConfig();
		//stellt Fenster fertig
		window.pack();
	}
}
