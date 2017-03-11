package de.lep.rmg.model.notes;

/**
 * Beschreibt die Tonart.<br>
 * MAJOR = Dur, MINOR = Moll.<br>
 * AUG und DIM sind Platzhalter für erhöhte und verminderte Akkorde, welche allerdings momentan noch nicht implementiert sind.
 *
 */
public enum CType {
	
	/**
	 * Dur
	 */
	MAJOR,
	
	/**
	 * Moll
	 */
	MINOR,
	
	/**
	 * Übermäßig
	 */
	AUG,
	
	/**
	 * Vermindert
	 */
	DIM
}
