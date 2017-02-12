package de.lep.rmg.view;

/**
 * Interface indicating that the implementing class
 * can change the currently played Song and builds
 * a standard protocol for observer registeration and removal.<br>
 * <br>
 * Dieses Interface zeigt an, dass eine Klasse die es implementiert,
 * den aktuell vom {@link MidiPlayer} gespielten Song ändern kann.
 * Es gibt gleichzeitig ein Protokol für die Registrirung und das
 * Entfernen von Observern (ObserverPattern) vor.
 */
public interface ISongChanger {
	public void addSongChangeObserver(ISongChangeObserver sco);
	public void removeSongChangeObserver(ISongChangeObserver sco);
}
