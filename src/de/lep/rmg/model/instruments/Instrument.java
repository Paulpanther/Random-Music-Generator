package de.lep.rmg.model.instruments;

/**
 * repräsentiert ein Instrument das einen bestimmten {@link Part} spielt
 */
public class Instrument {
	
	protected String name, shortName;
	protected int midiProgram;
	protected float volume = 80f;
	
	/**
	 * @param name
	 * @param shortName
	 * @param midiProgram
	 */
	public Instrument( String name, String shortName, int midiProgram ) {
		this.name = name;
		this.shortName = shortName;
		this.midiProgram = midiProgram;
	}

	/**
	 * @param name
	 * @param shortName
	 * @param midiProgram
	 * @param volume
	 */
	public Instrument( String name, String shortName, int midiProgram, float volume ) {
		this.name = name;
		this.shortName = shortName;
		this.midiProgram = midiProgram;
		this.volume = volume;
	}

	/**
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @return The midiProgram
	 */
	public int getMidiProgram() {
		return midiProgram;
	}

	public void setVolume(float volume){
		this.volume = volume;
		if(volume < 0)
			this.volume = 0;
		else
			if(volume > 127)
				this.volume = 127;
	}
	
	/**
	 * @return The volume
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * @see java.lang.Object#toString()
	 * @return Instruments name
	 */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * @return complete Information about instrument
	 */
	public String tolongString(){
		return "Instrument [name=" + name + ", shortName=" + shortName + ", midiProgram=" + midiProgram + ", volume="
				+ volume + "]";
	}
}
