package de.lep.rmg.model.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse für verschieden Operation mit Arrays
 *
 */
public class ArrayHelper {

	/**
	 * Wandelt eine Integer-Liste zu einen int-Array um.<br>
	 * (Die native Methode {@link List#toArray()} gibt ein Integer-Array zurück, was Probleme gibt)
	 * 
	 * @param list Die umzuwandelnde {@link List}e
	 * @return Ein entsprechendes int-Array
	 * 
	 * @see ArrayHelper#toList(int[]) int-Array zu Integer-Liste
	 */
	public static int[] toArray( List<Integer> list ) {
		int[] array = new int[ list.size() ];
		for( int i = 0; i < array.length; i++ )
			array[ i ] = list.get( i );
		return array;
	}
	
	/**
	 * Prüft ob der Wert in dem Array enthalten ist
	 * 
	 * @param needle Der zu suchende Wert
	 * @param haystack Das zu durchsuchende Array
	 * @return Boolean, welches angibt ob der Wert enthalten ist
	 */
	public static boolean isIn( int needle, int[] haystack ) {
		for( int in : haystack ) {
			if( in == needle )
				return true;
		}
		return false;
	}
	
	/**
	 * Wandelt ein int-Array in eine Integer-Liste um.
	 * 
	 * @param array Das umzuwandelnde Array
	 * @return Die Integer-Liste
	 * 
	 * @see ArrayHelper#toArray(List) Integer-Liste zu int-Array
	 */
	public static List<Integer> toList( int[] array ) {
		List<Integer> list = new ArrayList<Integer>();
		for( int value : array )
			list.add( value );
		return list;
	}
	
	/**
	 * Addiert die Wert des Arrays auf und gibt die Summe zurück.
	 * 
	 * @param array Das Array. Die Referenz wird nicht verändert
	 * @return Die Summe der Werte des Arrays
	 */
	public static int getSum( int[] array ) {
		int ges = 0;
		for( int value : array )
			ges += value;
		return ges;
	}
	
	/**
	 * Addiert die Wert der List auf und gibt die Summe zurück.
	 * 
	 * @param list Die Liste. Die Referenz wird nicht verändert
	 * @return Die Summe der Werte der Liste
	 */
	public static int getSum( List<Integer> list ) {
		int ges = 0;
		for( int value : list )
			ges += value;
		return ges;
	}
}
