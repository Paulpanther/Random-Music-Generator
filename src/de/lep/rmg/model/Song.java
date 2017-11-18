package de.lep.rmg.model;

import java.util.ArrayList;

import de.lep.rmg.model.instruments.Instrument;
/**
 * Internes Modell für den Song.<br>
 * Kann später in MIDI und XML exportiert werden.<br>
 * Folgende Struktur liegt vor:<br>
 * {@link Song} -> {@link Part} -> {@link Measure} -> {@link INote} ({@link SNote}/{@link Chord}/{@link Rest})
 * 
 * @see CanonGenerator Controller dieser Klasse
 */
public class Song extends ArrayList<Part> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Das Konfigurationsobjekt dieses Songs
	 */
	private SongConfig config;

	
	public Song( SongConfig config ) {
		this.config = config;
	}

	public Song() {}
	
	
	public SongConfig getConfig() {
		return config;
	}
	
	public void setConfig( SongConfig config ) {
		this.config = config;
	}
	
	/**
	 * Gibt die in den {@link Part}s verwendeten {@link Instrument}e zurück
	 * @return Die verwendeten {@link Instrument}e
	 */
	public Instrument[] getInstruments() {
		Instrument[] instruments = new Instrument[ size() ];
		for( int i = 0; i < size(); i++ )
			instruments[ i ] = get( i ).getInstrument();
		return instruments;
	}

	/**
	 * Für Debug-Zwecke
	 */
	@Override
	public String toString() {
		String value = "Song [parts=\n";
		for( Part part : this )
			value += "  " + part.toString() + "\n";
		value += "]\n";
		return value;
	}
	
	
}
