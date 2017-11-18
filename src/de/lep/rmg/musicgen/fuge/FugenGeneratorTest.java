package de.lep.rmg.musicgen.fuge;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import de.lep.rmg.model.Part;
import de.lep.rmg.model.SongConfig;
import de.lep.rmg.model.instruments.*;

public class FugenGeneratorTest {
	
	FugenGenerator fugen;
	SongConfig config;
	Part[] parts;
	ArrayList<Part> pitchorder;
	ArrayList<Part> playorder;
	
	public FugenGeneratorTest(){
		fugen = new FugenGenerator(null, new FugenMelodyGenerator());
		config = new SongConfig();
		fugen.fugenSubjects = fugen.generateSubjects(config);
		pitchorder = new ArrayList<Part>();
		playorder = new ArrayList<Part>();
	}
	
	@Test
	public void testIntervals(){
		givenParts1();
		Integer[] intervals = fugen.intervals(pitchorder, playorder).toArray(new Integer[0]);
		assertArrayEquals( new Integer[]{0, 4, 7}, intervals);
		givenParts2();
		intervals = fugen.intervals(pitchorder, playorder).toArray(new Integer[0]);
		assertArrayEquals( new Integer[]{0, 4, 7, -3}, intervals);
		givenParts3();
		intervals = fugen.intervals(pitchorder, playorder).toArray(new Integer[0]);
		assertArrayEquals( new Integer[]{7, 4, 0}, intervals);
		givenParts4();
		intervals = fugen.intervals(pitchorder, playorder).toArray(new Integer[0]);
		assertArrayEquals( new Integer[]{7, 11, 0, 4}, intervals);
	}
	
	/**
	 * Testet nur, ob alle Parts gleich lang bleiben
	 */
	@Test
	public void testAddSection(){
		givenParts1();
		fugen.addSection(pitchorder, config);
		sameSize("erster Durchlauf, 3 Parts");
		fugen.addSection(pitchorder, config);
		sameSize("zweiter Durchlauf, 3 Parts");
		givenParts2();
		fugen.addSection(pitchorder, config);
		sameSize("erster Durchlauf, 4 Parts");
		fugen.addSection(pitchorder, config);
		sameSize("zweiter Durchlauf, 4 Parts");
	}
	
	/**
	 * Testet nur, ob alle Parts gleich lang bleiben
	 */
	@Test
	public void testAddFianlSection(){
		givenParts1();
		fugen.addFinalSection(pitchorder, config);
		sameSize("erster Durchlauf, 3 Parts");
		fugen.addFinalSection(pitchorder, config);
		sameSize("zweiter Durchlauf, 3 Parts");
		givenParts2();
		fugen.addFinalSection(pitchorder, config);
		sameSize("erster Durchlauf, 4 Parts");
		fugen.addFinalSection(pitchorder, config);
		sameSize("zweiter Durchlauf, 4 Parts");
	}
	
	/**
	 * Testet nur, ob alle Parts gleich lang bleiben
	 */
	@Test
	public void testModulation(){
		givenParts1();
		fugen.addModulation(pitchorder, config);
		sameSize("erster Durchlauf, 3 Parts");
		fugen.addModulation(pitchorder, config);
		sameSize("zweiter Durchlauf, 3 Parts");
		givenParts2();
		fugen.addModulation(pitchorder, config);
		sameSize("erster Durchlauf, 4 Parts");
		fugen.addModulation(pitchorder, config);
		sameSize("zweiter Durchlauf, 4 Parts");
	}
	
	private void sameSize(String message){
		for(int partnr = 0; partnr < parts.length - 1; partnr++){
			System.out.println(parts[partnr].size() + " " + parts[partnr + 1].size());
			if(parts[partnr].size() != parts[partnr + 1].size()){
				fail(message);
			}
		}
	}
	
	private void givenParts1(){
		emptyAll();
		partsInPitchOrder(3);
		playorder = pitchorder;//0,1,2
	}
	
	private void givenParts2(){
		emptyAll();
		partsInPitchOrder(4);
		for(Part part: parts){
			playorder.add(part);
		}
		Part part = playorder.remove(0);
		playorder.add(part);//1,2,3,0
	}
	
	private void givenParts3(){
		emptyAll();
		partsInPitchOrder(3);
		for(Part part: parts){
			playorder.add(part);
		}
		Part part = playorder.remove(0);
		playorder.add(part);//1,2,0
	}
	
	private void givenParts4(){
		emptyAll();
		partsInPitchOrder(4);
		for(Part part: parts){
			playorder.add(part);
		}
		Part part = playorder.remove(0);
		playorder.add(part);//1,2,3,0
		part = playorder.remove(1);
		playorder.add(part);//1,3,0,2
	}
	
	private void partsInPitchOrder(int nr){
		switch(nr){
		case 4:
			parts = new Part[]{new Part(new Piano()), new Part(new AcousticBass()), new Part(new Flute()), new Part(new Xylophone())};
			break;
		default://3 Parts
			parts = new Part[]{new Part(new Piano()), new Part(new AcousticBass()), new Part(new Flute())};
		}
		for(Part part: parts){
			pitchorder.add(part);
		}
	}
	
	private void emptyAll(){
		parts = null;
		pitchorder = new ArrayList<Part>();
		playorder = new ArrayList<Part>();
	}
	
}
