package de.lep.rmg.model.instruments.helper;

import de.lep.rmg.model.instruments.AcousticBass;
import de.lep.rmg.model.instruments.AcusticGuitar;
import de.lep.rmg.model.instruments.Cello;
import de.lep.rmg.model.instruments.Flute;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.instruments.NullInstrument;
import de.lep.rmg.model.instruments.Piano;
import de.lep.rmg.model.instruments.TenorSax;
import de.lep.rmg.model.instruments.Xylophone;

public class InstrumentHelper {
	
	/**
	 * @return alle {@link} Instrument}e (außgenommen {@link NullInstrument})
	 */
	public static Instrument[] getAllInstr(){
		return new Instrument[]{new AcousticBass(), new AcusticGuitar(), new Cello(), new Flute(), new Piano(), new TenorSax(), new Xylophone()};
	}
	
	/**
	 * @return alle {@link Instrument}e ({@link NullInstrument} eingeschlossen)
	 */
	public static Instrument[] getAllInstrIncNull(){
		return new Instrument[]{new AcousticBass(), new AcusticGuitar(), new Cello(), new Flute(), new Piano(), new TenorSax(), new Xylophone(), new NullInstrument()};
	}
	
	/**
	 * @return Anzahl aller Instrumente (außgenommen Nullinstrument)
	 */
	public static int getAllInstrNumber(){
		return getAllInstr().length;
	}
	
}
