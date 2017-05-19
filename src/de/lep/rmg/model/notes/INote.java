package de.lep.rmg.model.notes;

/**
 * Interface für die Noten welche in {@link Measure} gespeichert werden können.<br>
 * Da dazu auch die Pause ({@link Rest}) (, welche keinen Ton sondern nur eine Dauer hat) gehört, haben diese Klassen nur die Methode {@link INote#getDuration()} als Gemeinsamkeit.
 *
 */
public interface INote extends Cloneable {
	int getDuration();
}
