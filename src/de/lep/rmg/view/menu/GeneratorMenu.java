package de.lep.rmg.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.view.ISongChangeObserver;
import de.lep.rmg.view.Window;
import de.lep.rmg.view.panels.ControllPanel;

public class GeneratorMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	ArrayList<JMenuItem> components;//anzuzeigende Komponenten
	ArrayList<IMusicGenerator> musicGenList;//verschiedene MusicGeneratoren
	ArrayList<ISongChangeObserver> songChangeObserverList;
	ControllPanel genPanel;//Kontrolliertes GeneratorPanel
	Window window;
	
	public GeneratorMenu(Window window, ControllPanel genPanel, ArrayList<IMusicGenerator> musicGens,
			ArrayList<ISongChangeObserver> songChangeObservers){
		super("Generator");
		components = new ArrayList<JMenuItem>();
		this.window = window;
		this.genPanel = genPanel;
		this.musicGenList = musicGens;
		songChangeObserverList = songChangeObservers;
		for(IMusicGenerator musicGen: musicGenList){
			JMenuItem menuItem = new JMenuItem(musicGen.getGeneratorName());
			menuItem.addActionListener(new ActionHandler(musicGen));
			components.add(menuItem);
			add(menuItem);
		}
	}
	
	private class ActionHandler implements ActionListener{
		IMusicGenerator musicGen;
		
		ActionHandler(IMusicGenerator musicGen){
			this.musicGen = musicGen;
		}
		@Override
		public void actionPerformed(ActionEvent aE) {
			//setzt das GeneratorPanel und den Musikgenerator auf die ausgewählte Option
			window.remove(genPanel);
			genPanel = musicGen.getGeneratorPanel();
			for(ISongChangeObserver sco : songChangeObserverList)
				genPanel.addSongChangeObserver(sco);
			window.add(genPanel, 0);
			
			window.repaint();
		}
		
	}
}
