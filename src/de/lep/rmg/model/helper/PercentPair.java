package de.lep.rmg.model.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Eine Hilfsklasse und Modell in einem, mit der aus Werten, welche einzelne Wahrscheinlichkeiten haben, ein Wert zufällig gezogen wird.
 *
 */
public class PercentPair {

	/**
	 * Der Wert
	 */
	private int value;
	
	/**
	 * Die Wahrscheinlichkeit für das ziehen
	 */
	private float percent;
	
	
	public PercentPair(int value, float percent) {
		this.value = value;
		this.percent = percent;
	}

	public int getValue() {
		return value;
	}
	
	public float getPercent() {
		return percent;
	}
	
	private void setPercent( float percent ) {
		this.percent = percent;
	}
	
	@Override
	protected Object clone() {
		return new PercentPair( value, percent );
	}
	
	/**
	 * Zieht einen zufälligen Wert aus den pairs-Array unter Beachtung der jeweiligen Wahrscheinlichkeiten.<br>
	 * <h1>Funktionsweise:</h1>
	 * <ul>
	 * <li>Die Wahrscheinlichkeiten werden aufsummiert, sodass sich die Werte auf einer Skala von 0 bis 1 befinden.<li>
	 * <li>Ein Zufallswert zwischen 0 und 1 wird berechnet</li>
	 * <li>Der nächst-größere Wert zur Zufallszahl wird gezogen</li>
	 * </ul>
	 * 
	 * @param pairs Die Wert-Wahrscheinlichkeit-Paare
	 * @param r Ein Random-Objekt
	 * @return Einen zufällig gezogenen Wert
	 */
	public static int getRandomValue( PercentPair[] pairs, Random r ) {
		//Setzt Summe zu 1
		float ges = 0;
		for( PercentPair entity : pairs )
			ges += entity.getPercent();
		
		float diff = 1 / ges;
		for( PercentPair entity : pairs )
			entity.setPercent( entity.getPercent() * diff );
		
		//In rangePairs werden die Werte als Skala von 0 bis 1 gespeichert
		PercentPair[] rangePairs = new PercentPair[ pairs.length ];
		ges = 0;
		for( int i = 0; i < pairs.length; i++ ) {
			rangePairs[ i ] = new PercentPair( pairs[ i ].getValue(), ges );
			ges += pairs[ i ].getPercent();
		}
		
		//Eine Zufallszahl wird generiert
		float random_nr = r.nextFloat();
		
		int value = -1;
		for( int i = 1; i < rangePairs.length; i++ ) {
			//Der nächst-größere Wert zur Zufallszahl wird gezogen
			if( rangePairs[ i ].getPercent() > random_nr ) {
				value = rangePairs[ i-1 ].getValue();
				break;
			}
		}
		if( value == -1 )
			value = rangePairs[ rangePairs.length -1 ].getValue();
		
		return value;
	}
	
	public static int getRandomValue( ArrayList<PercentPair> pairs, Random r ) {
		float randomNr = r.nextFloat();
		float sum = 0;
		for(PercentPair pair : pairs){
			sum += pair.getPercent();
		}
		randomNr *= sum;
		float counter = 0;
		for(PercentPair pair : pairs){
			counter += pair.getPercent();
			if(counter >= randomNr){
				return pair.getValue();
			}
		}
		throw new IllegalArgumentException("This is not supposed to happen");
	}
	
	/**
	 * Entfernt Werte, welche größer gleich dem gegebenen maximalen Wert sind.
	 * 
	 * @param max Der maximale Wert
	 * @param pairs Die Paare. Dieses Array wird nicht verändert.
	 * @return Ein Array aus den übrig gebliebenen Paaren
	 */
	public static PercentPair[] removeValuesGreaterThan( int max, PercentPair[] pairs ) {
		List<PercentPair> newPairs = new ArrayList<PercentPair>();
		for( PercentPair pair : pairs ) {
			if( pair.getValue() <= max )
				newPairs.add( pair );
		}
		return newPairs.toArray( new PercentPair[ newPairs.size() ] );
	}
	
	public static ArrayList<PercentPair> removeValuesGreaterThan( int max, ArrayList<PercentPair> pairs ) {
		ArrayList<PercentPair> newPairs = new ArrayList<PercentPair>();
		for( PercentPair pair : pairs ) {
			if( pair.getValue() <= max )
				newPairs.add( pair );
		}
		return newPairs;
	}
//	/**
//	 * Prüft, ob ein bestimmter Wert in einem Array von PercentPairs vorkommt
//	 * @param pairs - zu durchsuchender Array
//	 * @param value - Wert auf den geprüft wird
//	 * @return true, falls value in pairs vorhanden ist
//	 */
//	public static boolean valueInArray( PercentPair[] pairs, int value){
//		for( PercentPair pair : pairs) {
//			if( pair.getValue() == value)
//				return true;
//		}
//		return false;
//	}
	
	/**
	 * Klont ein PercentPair-Array
	 * 
	 * @param old Das alte Array
	 * @return Ein neues Array mit den gleichen Werten
	 */
	public static PercentPair[] clone( PercentPair[] old ) {
		PercentPair[] newP = new PercentPair[ old.length ];
		for( int i = 0; i < old.length; i++ )
			newP[ i ] = (PercentPair) old[ i ].clone();
		return newP;
	}
}
