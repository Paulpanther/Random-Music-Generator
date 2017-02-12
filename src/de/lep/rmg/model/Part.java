package de.lep.rmg.model;

import java.util.ArrayList;

import de.lep.rmg.model.instruments.Instrument;

/**
 * Ein Teil des gesamten {@link Song}s, der nur von einem {@link Instrument} gespielt wird.<br>
 * Er besteht aus einer nicht vorher festgelegten Anzahl an Takten ({@link Measure}).<br>
 * Ein Part hat nichts mit der Einteilung in Melodien des {@link MelodyGenerator} zu tun.
 */
public class Part extends ArrayList<Measure> {
	private static final long serialVersionUID = 1L;

	/**
	 * Das in diesen Part verwendete Instrument
	 */
	private Instrument instrument;

	
	public Part( Instrument instrument ) {
		super();
		this.instrument = instrument;
	}

	
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Für Debug-Zwecke
	 */
	@Override
	public String toString() {
		String value = "Part [instrument=" + instrument + ", measures=\n";
		for( Measure measure : this )
			value += "  " + measure.toString() + "\n";
		value += "]\n";
		return value;
	}
	
	
}
