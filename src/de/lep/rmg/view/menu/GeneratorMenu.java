package de.lep.rmg.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import de.lep.rmg.musicgen.IMusicGenerator;
import de.lep.rmg.view.IGeneratorPanel;

public class GeneratorMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	ArrayList<JMenuItem> components;//anzuzeigende Komponenten
	ArrayList<IMusicGenerator> musicGenList;//verschiedene MusicGeneratoren
	IGeneratorPanel genPanel;//Kontrolliertes GeneratorPanel
	
	public GeneratorMenu(IGeneratorPanel genPanel, ArrayList<IMusicGenerator> musicGens){
		super("Generator");
		components = new ArrayList<JMenuItem>();
		this.genPanel = genPanel;
		this.musicGenList = musicGens;
		ActionListener actHand = new ActionHandler();
		for(IMusicGenerator musicGen: musicGenList){
			JMenuItem menuItem = new JMenuItem(musicGen.getGeneratorName());
			menuItem.addActionListener(actHand);
			components.add(menuItem);
			add(menuItem);
		}
	}
	
	private class ActionHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent aE) {
			//setzt den MusicGenerator des genPanels auf den vom Nutzer gewählten
			genPanel.setGenerator(musicGenList.get(components.indexOf(aE.getSource())));
		}
		
	}
}
