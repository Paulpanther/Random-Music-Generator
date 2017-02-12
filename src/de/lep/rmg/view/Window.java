package de.lep.rmg.view;

import java.awt.LayoutManager;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.lep.rmg.out.midi.MidiPlayer;
/**
 * ein {@link JFrame} bei dem manche Variablen schon gesetzt sind
 * bevor das JFrame geschlossen wird (was das Programm beendet) wird ein Bestätigungsdialog gezeigt
 */
public class Window extends JFrame{
	private static final long serialVersionUID = 1L;
	//der midiPlayer wird gestopped und geschlossen bevor das Programm beendet wird
	MidiPlayer midiPlayer;
	
	/**
	 * macht ein neues Fenster mit dem übergebenen {@link LayoutManager}
	 * @param layout 
	 * @param midiPlayer wird geschlossen, sobald das Program beendet wird
	 */
	public Window(LayoutManager layout, MidiPlayer midiPlayer){
		this.midiPlayer = midiPlayer;
		initialize(layout, new ArrayList<JPanel>());
	}
	
	/**
	 * macht ein neues Fenster mit dem übergebenen Layout und fügt die {@link JPanel} hinzu
	 * @param layout
	 * @param midiPlayer
	 * @param panels
	 */
	public Window(LayoutManager layout, MidiPlayer midiPlayer, ArrayList<JPanel> panels){
		this.midiPlayer = midiPlayer;
		initialize(layout, panels);
	}
	
	/**
	 * Initialisationsmethode, die in jedem Konstruktor benutzt wird
	 * @param layout
	 * @param panels
	 */
	void initialize(LayoutManager layout, ArrayList<JPanel> panels){
		this.setLayout(layout);
		for(JPanel panel: panels){
			this.add(panel);
		}
		this.setTitle("Random Music Generator Canon");
		this.setSize(1200, 700);
		//places Frame in the middle of the screen
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new defaultWindowListener());
		//adjusting look to local system 
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		//shows the Frame on the screen
		this.setVisible(true);
	}
	
	/**
	 * öffnet einen Bestätigungsdialog bevor das Fenster geschlossen werden kann
	 */
	private class defaultWindowListener implements WindowListener{

		@Override
		public void windowActivated(WindowEvent arg0) {
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			//shows a confirmationDialog before closing the programm
			int antwort = JOptionPane.showConfirmDialog(Window.this , "Willst du das Programm beenden?", "Beenden?", JOptionPane.OK_CANCEL_OPTION);
			if (antwort == JOptionPane.OK_OPTION){
				midiPlayer.stop();
				midiPlayer.close();
				System.exit(NORMAL);
			}
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			
		}
		
	}
	
}
