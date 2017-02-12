package de.lep.rmg.model.notes;

/**
 * Eine Pause.<br>
 * Gehört zum {@link Song}-Modell
 *
 */
public class Rest implements INote {
	
	/**
	 * Die Dauer der Pause
	 */
	private int duration;
	
	
	public Rest( int duration ) {
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration( int duration ) {
		this.duration = duration;
	}

	/**
	 * Für Debug-Zwecke
	 */
	@Override
	public String toString() {
		return "Rest [duration=" + duration + "]";
	}
}
