package de.lep.rmg.view.panels;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lep.rmg.out.midi.IPlayerObserver;
import de.lep.rmg.out.midi.MidiPlayer;

/**
 * With this {@link JPanel} the user can controll the {@link MidiPlayer},
 * e.g. start, pause and stop the music and alter the playing speed.<br>
 * <br>
 * Mit desem {@link JPanel} kann der Nutzer den {@link MidiPlayer} steuern,
 * z.B. die Musik starten, pausieren, stoppen und die Geschwindigkeit ändern.
 */
public class PlayerControllPanel extends JPanel implements IPlayerObserver{
	private static final long serialVersionUID = 1L;
	//zu steuernder MidiPlayer
	MidiPlayer midiPlayer;
	//anzuzeigende Komponenten
	JPanel panelNorth;
	JButton startButton;
	JButton pauseButton;
	JButton stopButton;
	JLabel playerTimeLabel;
	JPanel panelSouth;
	JLabel tempoLabel;
	JSlider tempoSlider;
	
	//speichert Tickposition, wenn der MidiPlayer pausiert wird
	long tickPosition;
	
	/**
	 * macht ein neues PlayerControllPanel
	 * @param player : zu steuernder {@link MidiPlayer}
	 */
	public PlayerControllPanel(MidiPlayer player){
		super(new GridLayout(2,1));
		midiPlayer = player;
		midiPlayer.addObserver(this);
		panelNorth = new JPanel(new FlowLayout());
		ActionHandler aH = new ActionHandler();
		startButton = new JButton("Spielen");
		startButton.addActionListener(aH);
		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(aH);
		stopButton = new JButton("Stop");
		stopButton.addActionListener(aH);
		playerTimeLabel = new JLabel();
		panelNorth.add(startButton);
		panelNorth.add(pauseButton);
		panelNorth.add(stopButton);
		panelNorth.add(playerTimeLabel);
		panelSouth = new JPanel(new FlowLayout());
		tempoSlider = new JSlider(60, 220, (int)midiPlayer.getTempoInBPM());
		tempoSlider.setMajorTickSpacing(40);
		tempoSlider.setMinorTickSpacing(10);
		tempoSlider.setPaintLabels(true);
		tempoSlider.setPaintTicks(true);
		tempoSlider.addChangeListener(new ChangeHandler());
		tempoLabel = new JLabel("Tempo in Beats pro Minute");
		panelSouth.add(tempoLabel);
		panelSouth.add(tempoSlider);
		this.add(panelNorth);
		this.add(panelSouth);
		//dis-/enable buttons depending on player state
		if(midiPlayer.isRunning()){
			startButton.setEnabled(false);
			pauseButton.setEnabled(true);
			stopButton.setEnabled(true);
		}else{
			stopButton.setEnabled(false);
			pauseButton.setEnabled(false);
			if(midiPlayer.sequenceLoaded()){
				startButton.setEnabled(true);
			}else{
				startButton.setEnabled(false);
			}
		}
	}
	
	/**
	 * behandelt die ausgelöste ActionEvents
	 */
	private class ActionHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent aE) {
			if(aE.getSource() == startButton){
				//startet den midiPlayer an dem Punkt wo er zuvor pausiert wurde oder am Anfang falls der Song ganz gespielt wurde
				midiPlayer.setTickPosition(tickPosition);
				midiPlayer.setTempoInBPM(tempoSlider.getValue());
				midiPlayer.start();
				tickPosition = 0;
			}else{
				if(aE.getSource() == pauseButton){
					//pausiert den midiPlayer und speichert die tickPosition
					tickPosition = midiPlayer.getTickPosition();
					midiPlayer.stop();
				}else{
					if(aE.getSource() == stopButton){
						//stoppt den midiPlayer und setzt die TickPosition auf den Anfang
						midiPlayer.stop();
						midiPlayer.setTickPosition(0);
						tickPosition = 0;
					}
				}
			}
		}
	}
	
	/**
	 * passt (auch währed der MidiPlayer spielt) das Tempo an
	 */
	private class ChangeHandler implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent cE) {
				if(!tempoSlider.getValueIsAdjusting()){
					midiPlayer.setTempoInBPM(tempoSlider.getValue());
			}
		}
	}
	
	/**
	 * gleicht Knöpfe an aktuellen Status der {@link MidiPlayers} an
	 */
	@Override
	public void playingStateChanged(boolean playing) {
		if(playing){
			startButton.setEnabled(false);
			pauseButton.setEnabled(true);
			stopButton.setEnabled(true);
		}else{
			stopButton.setEnabled(false);
			pauseButton.setEnabled(false);
			if(midiPlayer.sequenceLoaded()){
				startButton.setEnabled(true);
			}else{
				startButton.setEnabled(false);
			}
		}
	}
	
	/**
	 * gleicht Knöpfe an aktuellen Status der {@link MidiPlayers} an
	 */
	@Override
	public void sequenceStateChanged(boolean loaded) {
		if(midiPlayer.isRunning()){
			startButton.setEnabled(false);
			pauseButton.setEnabled(true);
			stopButton.setEnabled(true);
		}else{
			stopButton.setEnabled(false);
			pauseButton.setEnabled(false);
			if(loaded){
				startButton.setEnabled(true);
			}else{
				startButton.setEnabled(false);
			}
		}
	}
	
}
