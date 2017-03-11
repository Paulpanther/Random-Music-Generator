package de.lep.rmg.musicgen.markow;

import java.util.ArrayList;
import java.util.Random;

import de.lep.rmg.model.helper.PercentPair;

public class MarkowMelodyMatrix {
	private final int PRIME = 0, TERZ = 2, QUINTE = 4, OKTAVE = 7;
	private int size;
	private int keytone;
	/**
	 * Quadratische Matrix der Übergangswahrscheinlichkeiten.<br>
	 * Im ersten Paar eckiger Klammern [] wird der aktuelle Zustand angegeben
	 * und im zweiten Paar eckiger Klammern [] der zu erreichende Zustand.<br>
	 * Der durcj diese Abfrage erhaltene Wert gibt dann die Wahrscheinlichkeit an,
	 * von aktuellen Zustand in den zu erreichenden zu kommen.<br>
	 * matrix[0][3] gibt also an, wie wahrscheinlich es ist, von Zustand 0 aus
	 * Zustand 3 als nächsten Zustand zu erhalten.<br>
	 * Zustand 0 repräsentiert den Grundton, Zustand 1 eine Sekunde nach oben, von Grundton aus, ...
	 */
	private double[][] matrix;
	
	/**
	 * Erstellt ein MarkowMelodyMatrix-Objekt auf Basis eines Wahrscheinlichkeitsvektors.
	 * 
	 * @param size - Die Größe des Zustandsraums. Size 7 entspricht dem Tonumfang einer Oktave.
	 * @param keytone - Grundton (wird benötigt, da auf einen Ton der nicht Teil des Grundakkords ist ein Ton des Grundakkords folgen muss.
	 * @param probabilitys - Wahrscheilichkeitsvektor, der angibt, wie wahrscheinlich es ist,
	 *  von einem Zustand in einen um x entfernten Zustand zu kommen.<br>
	 */
	public MarkowMelodyMatrix(int size, int keytone, ArrayList<PercentPair> probabilitys){
		int highest = -1;
		for(PercentPair pair: probabilitys){
			if(pair.getValue() > highest)
				highest = pair.getValue();
			if(highest < 0){
				throw new IllegalArgumentException("Illegal probabilitylist! Negative value!");
			}
		}
		double[] probVector = new double[highest+1];//values start with zero
		for(int i = 0; i < probVector.length; i++)
			probVector[i] = 0;
		for(PercentPair pair: probabilitys){
			probVector[pair.getValue()] = pair.getPercent();
		}
		initialize(size, keytone, probVector);
	}
	
	/**
	 * Erstellt ein MarkowMelodyMatrix-Objekt auf Basis eines Wahrscheinlichkeitsvektors.
	 * 
	 * @param size - Die Größe des Zustandsraums. Size 7 entspricht dem Tonumfang einer Oktave.
	 * @param keytone - Grundton (wird benötigt, da auf einen Ton der nicht Teil des Grundakkords ist ein Ton des Grundakkords folgen muss.
	 * @param probabilitys - Wahrscheilichkeitsvektor, der angibt, wie wahrscheinlich es ist,
	 *  von einem Zustand in einen um x entfernten Zustand zu kommen.<br>
	 *  probablitiys[0] = Wahrscheinlichkeit einer Prime; probablitiys[1] = Wahrscheinlichkeit einer Sekunde ...
	 */
	public MarkowMelodyMatrix(int size, int keytone, double[] probabilitys){
		initialize(size, keytone, probabilitys);
	}
	
	private void initialize(int size, int keytone, double[] probabilitys){
		//Argumente auf Korrektheit prüfen
				for(double value: probabilitys){
					if(value < 0){
						throw new IllegalArgumentException("Negative probability " + value + " in probabilityvector");
					}
				}
				//Object initialisieren
				this.size = size;
				this.keytone = keytone;
				matrix = new double[size][size];
				for(int i = 0; i < size; i++){
					for(int j = 0; j < size; j++){
						if(Math.abs(i - j) < probabilitys.length){
							matrix[i][j] = probabilitys[Math.abs((i - j))];//die Wahrscheinlichkeit von i nach j zu gelangen,
						//ist im Wahrscheinlichkeitsvektor angegeben, an der Stelle |i-j|
						}else{
							matrix[i][j] = 0;//falls der Vektor zu kurz ist
						}
						if(!((i - keytone)%OKTAVE == PRIME || (i - keytone - TERZ)%OKTAVE == PRIME ||
								(i - keytone - QUINTE)%OKTAVE == PRIME)){//falls der aktuelle Ton nicht Teil des Grundtons ist
							if(!((j - keytone)%OKTAVE == PRIME || (j - keytone - TERZ)%OKTAVE == PRIME ||
									(j - keytone - QUINTE)%OKTAVE == PRIME)){//falls auch der folgende Ton nicht Teil des Grundtons ist
								matrix[i][j] = 0;//auf einen Ton der nicht Teil des Grundtons ist muss immer ein Ton des Grundtons folgen, da sonst ein Vorhalt gebildet wird
							}
						}						
					}
				}
				//Werte angleichen: Die Summe einer Reihe muss 1 ergeben
				for(int i = 0; i < size; i++){
					double sum = 0;
					for(double value : matrix[i])
						sum += value;
					if(sum != 0){
						for(int j = 0; j < size; j++){
							matrix[i][j] /= sum;
						}
					}
				}
	}
	
	/**
	 * Generiert die Tonhöhen einer length Töne langen Melodie mit der Markowmatrix
	 * @param length - Anzahl der Töne in der Melodie (Länge der Markow-Kette)
	 * @param zustand - Ausgangszustand für Generierung (Intervalabstand vom Grundton), wird als erster Ton in die Melodie aufgenommen
	 * @param rand - ein Random-Object
	 * @return Melodie als ArrayList; Töne werden als Intervalle angegeben
	 */
	public ArrayList<Integer> generateMelody(int length, int zustand, Random rand){
		if(zustand >= size){
			throw new IllegalArgumentException("Zustand " + zustand + " außerhalb des Wertebereichs " + size);
		}
		ArrayList<Integer> intervals = new ArrayList<Integer>();
		intervals.add(zustand);
		while(--length > 0){
			double x = rand.nextDouble();//zufällige Zahl zwischen 0 und 1
			for(int i = 0; i < size; i++){
				if(matrix[zustand][i] > x){//falls die Wahrscheinlichkeit für i größer ist als die Zufallszahl x
					intervals.add(i);//füge i der Melodie hinzu und brich die Schleife ab
					break;
				}else{
					x -= matrix[zustand][i];//sonst: ziehe Wahrscheinlichkeit von i von der Zufallszahl ab
				}
			}
		}
		return intervals;
	}
	
	public int size(){
		return size;
	}
	public int getKeyTone(){
		return keytone;
	}
}
