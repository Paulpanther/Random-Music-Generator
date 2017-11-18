package de.lep.rmg.model.helper;

import java.util.ArrayList;

public class IntHelper {
	
	/**
	 * Stellt fest, mit welchen Kombinationen aus bestimmten Zahlen sich eine vorgegebene Summe erreichen lässt.
	 * Dabei werden auch einzelne Zahlen als Kombination angesehen. Es kommt nicht zu Aufführung von Kombinationen
	 * bei denen nur die Reihenfolge der Summanden verändert ist.
	 * <br>
	 * Achtung: summmands[0] muss den niedrigsten Wert der Summanden enthalten, damit, alle Kombinationen gefunden werden!
	 * @param summands - Summanden aus denen die Summe, gebildet werden kann
	 * @param sum - Summe die erreicht werden soll
	 * @return alle möglichen Kombinationen vom Summanden, die die Summe ergeben
	 */
	public static ArrayList<ArrayList<Integer>> possibleSums( int[] summands, int sum) {
		ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
		int maxIterations = sum / summands[0];//setzt voraus, dass summands[0] den niedrigsten Wert enthält
		//mit diesem Array wird gespeichert, welche Kombination von Summanden zuletzt getestet wurde
		//der gespeicherte Wert steht dabei für die Position im Summanden-Array
		int[] counter = new int[maxIterations];
		counter[0] = 0;
		for(int i = 1; i < counter.length; i++)
			counter[i] = -1;
		int add = 0;
		while(counter[counter.length - 1] < counter.length - 1){
			ArrayList<Integer> division = new ArrayList<Integer>();
			for(int i = 0; i < counter.length; i++){
				if(counter[i] > -1){
					add += summands[counter[i]];
					division.add(summands[counter[i]]);
				}
			}
			if(add == sum){
				ret.add(division);
			}
			add = 0;
			for(int i = 0; i < counter.length; i++){//ZählerArray erhöhen
				counter[i]++;
				if(counter[i] == summands.length)
					counter[i] = 0;
				else
					break;
			}
			//Vermeidung unnötiger Doppelungen von Kombinationen, die sich nur in der Reihenfolge unterscheiden
			for(int i = counter.length - 2; i >=0; i--){
				if(counter[i] < counter[i + 1])
					counter[i] = counter[i + 1];
			}
		}
		return ret;
	}
	
}
