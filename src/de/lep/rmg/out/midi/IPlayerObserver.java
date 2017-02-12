package de.lep.rmg.out.midi;

/**
 * Interface needed to be implemented by any observer of {@link MidiPlayer}s. Observers will be informed
 * when a {@link Sequence} is loaded or removed and when the playing state changes.<br>
 * <br>
 * Interface für MidiPlayerObserver. Das implementierende Objekt wird informiert, falls der {@link MidiPlayer} zu spielen
 * beginnt oder stoppt und falls eine {@link Sequence} geladen oder entfernt wird
 */
public interface IPlayerObserver {
	void playingStateChanged(boolean playing);
	void sequenceStateChanged(boolean loaded);
}
