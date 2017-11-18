package de.lep.rmg.model.notes;

/**
 * Interface für spielbare Noten({@link INote}), also keine Pausen.<br>
 * enthält Methoden zum abfragen und ändern der Tonhöhe
 */
public interface IRealNote extends INote{
	/**
	 * Gibt den Notenwert in Halbtonschritten zurück
	 * @return Notenwert in Halbtonschritten
	 */
	public int getTone();
	/**
	 * Gibt die Oktave der Note zurück
	 * @return Oktave der Note
	 */
	public int getOctave();
	/**
	 * Erhöht den Notenwert um 'steps' Halbtonschritte. Oktave wird angepasst.
	 * @param step - Anzahl der Halbtonschritte, um die die Note erhöht wird. Darf auch negativ sein.
	 */
	public void addStep( int steps );
	/**
	 * Setzt den Notenwert der Note in Halbtonschritten
	 * @param tone - der Notenwert
	 */
	public void setTone( int tone );
	/**
	 * Setzt die Oktave der Note
	 * @param octave - die Oktave
	 */
	public void setOctave( int octave );
}
