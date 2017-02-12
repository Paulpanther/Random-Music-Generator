package de.lep.rmg.view;

import de.lep.rmg.model.Song;

/**
 * Objects of this type can observe {@link ISongChanger} and will be notifyed if registerd in their observerlist.
 * Used by the JComponents  that are responsible for saving Songs {@link}<Song> (makes export to XML possible).<br>
 * <br>
 * Objekte dieses Typs können {@link ISongChanger} beobachten und werden von ihnen benachrichtigt,
 * falls sie in ihrer Beobachterliste stehen. Wird von den für das Speichern der Songs {@link}<Song> 
 * zuständigen Komponenten benutzt (macht den Export zu XML möglich).
 */
public interface ISongChangeObserver {
	public void songChange(Song song);
}
