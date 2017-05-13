package de.lep.rmg.model.notes;

/**
 * Interface für spielbare Noten({@link INote}), also keine Pausen.<br>
 * enthält Methoden zum abfragen und ändern der Tonhöhe
 */
public interface IRealNote extends INote{
	/**
	 * Gibt den Notenwert in Halbtonschritten zurück ( tone + 12*octave )
	 * @return Notenwert in Halbtonschritten
	 */
	public int getStep();
	/**
	 * Erhöht den Notenwert um 'steps' Halbtonschritte
	 * @param step - Anzahl der Halbtonschritte, um die die Note erhöht wird. Darf auch negativ sein.
	 */
	public void addStep( int steps );
}
