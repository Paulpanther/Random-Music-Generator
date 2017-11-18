package de.lep.rmg.model.helper;

import java.util.List;
import java.util.Random;

/**
 * Hilfsklasse für Operationen mit zufälligen Ausgabewerten
 *
 * @see PercentPair PercentPair
 */
public class RandomHelper {
	
	static Random rand = new Random();
	
	/**
	 * Gibt mit der angegebenen Wahrscheinlichkeit true zurück
	 * @param probability Die Wahrscheinlichkeit für true
	 * @return true oder false
	 */
	public static boolean randBoolean( float probability ) {
		return randBoolean( rand, probability );
	}
	
	/**
	 * Gibt mit der angegebenen Wahrscheinlichkeit true zurück
	 * @param rand Ein Random-Objekt
	 * @param probability Die Wahrscheinlichkeit für true
	 * @return true oder false
	 */
	public static boolean randBoolean( Random rand, float probability ) {
		return rand.nextFloat() <= probability;
	}
	
	/**
	 * Wählt ein zufälliges Element aus dem Array aus
	 * @param array Das Array
	 * @return Ein zufälliges Element aus dem Array
	 */
	public static int randFrom( int[] array ) {
		return randFrom( array, rand );
	}
	
	/**
	 * Wählt ein zufälliges Element aus dem Array aus
	 * @param array Das Array
	 * @param rand Ein Random-Objekt
	 * @return Ein zufälliges Element aus dem Array
	 */
	public static int randFrom( int[] array, Random rand ) {
		return array[ rand.nextInt( array.length ) ];
	}
	
	/**
	 * Wählt ein zufälliges Element aus dem Array aus
	 * @param array Das Array
	 * @param <T> Ein unbestimmtes Objekt
	 * @return Ein zufälliges Element aus dem Array
	 */
	public static <T> T randFrom( T[] array ) {
		return randFrom( array, rand );
	}
	
	/**
	 * Wählt ein zufälliges Element aus dem Array aus
	 * @param array Das Array
	 * @param rand Ein Random-Objekt
	 * @param <T> Ein unbestimmtes Objekt
	 * @return Ein zufälliges Element aus dem Array
	 */
	public static <T> T randFrom( T[] array, Random rand ) {
		return array[ rand.nextInt( array.length ) ];
	}
	
	/**
	 * Wählt ein zufälliges Element aus einer Liste aus
	 * @param list - die Liste
	 * @param rand - ein Random-Objekt
	 * @return Ein zufälliges Objekt aus der Liste
	 */
	public static <T> T randFrom( List<T> list ) {
		return list.get(rand.nextInt(list.size()));
	}
	
	/**
	 * Wählt ein zufälliges Element aus einer Liste aus
	 * @param list - die Liste
	 * @param rand - ein Random-Objekt
	 * @return Ein zufälliges Objekt aus der Liste
	 */
	public static <T> T randFrom( List<T> list, Random rand ) {
		return list.get(rand.nextInt(list.size()));
	}
	
	/**
	 * 
	 * @return ein standard Random-Objekt, dass mit 'new Random()' erschaffen wurde
	 */
	public static Random getRandom(){
		return rand;
	}
}
