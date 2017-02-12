package de.lep.rmg.out.xml;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import de.lep.rmg.model.Measure;
import de.lep.rmg.model.Part;
import de.lep.rmg.model.Song;
import de.lep.rmg.model.instruments.Instrument;
import de.lep.rmg.model.notes.Chord;
import de.lep.rmg.model.notes.INote;
import de.lep.rmg.model.notes.Rest;
import de.lep.rmg.model.notes.SNote;
import de.lep.rmg.model.notes.helper.NoteHelper;

/**
 * Klasse zur Erzeugung eines XML-Dokuments
 *
 */
public class XMLGenerator {
	
	/**
	 * Das XML-Dokument
	 */
	private Document doc;
	
	/**
	 * Der Dateipfad der zu speichernden Datei
	 */
	private File file;
	
	/**
	 * Der zu speichernde Song
	 */
	private Song song;
	
	public XMLGenerator() {}
	
	/**
	 * Speichert einen Song in einer XML-Datei an den angegebenen Pfad.<br>
	 * Datei hat ein zulässiges Music-XML Format, welches offen ist und in allen gängigen Notensatzprogrammen unterstützt wird.<br>
	 * Mehr Informationen über Music-XML sind auf der <a href="http://www.musicxml.com/">offiziellen Webseite des Formats</a><br>
	 * Wir verwenden für das Öffnen der Dateien das Open-Source-Programm <a href="https://musescore.org/">MuseScore</a> 
	 * 
	 * @param file Der Pfad an dem die Datei gespeichert werden soll
	 * @param song Der zu speichernde Song
	 * @throws XMLException Falls ein Fehler beim Speichern auftritt
	 */
	public void save( File file, Song song ) throws XMLException {
		this.file = file;
		this.song = song;
		
		Document doc = createDocument();
		this.doc = doc;
		
		Element root = doc.createElement( "score-partwise" );
		doc.appendChild( root );
		
		createHead( root );//Erstellt den Kopfteil der Datei
		createPartList( root );//Erstellt die Part-Liste
		createParts( root );//erstellt die eigentlichen Parts
		
		saveDocumentTo();//speichert das Dokument
	}
	
	/**
	 * Erstellt den Kopfteil des Dokuments
	 * @param root Das root-Element
	 */
	private void createHead( Element root ) {
		String title = song.getConfig().getTitle();
		String composer = song.getConfig().getComposer();
		
		Element workE = create( "work" );
		root.appendChild( workE );
		workE.appendChild( create( "work-title", title ) );
		
		Element identE = create( "identification" );
		root.appendChild( identE );
		
		identE.appendChild( attr( create( "creator", composer ), "type", "composer" ) );
		
		Element encodingE = create( "encoding" );
		identE.appendChild( encodingE );
		encodingE.appendChild( create( "software", "Random Music Generator" ) );
		String time = new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date() );
		encodingE.appendChild( create( "encoding-date", time ) );
		encodingE.appendChild( attr( attr( create( "supports" ), "element", "accidental" ), "type", "yes" ) );
		encodingE.appendChild( attr( attr( create( "supports" ), "element", "beam" ), "type", "no" ) );
		encodingE.appendChild( attr( attr( attr( attr( create( "supports" ), "element", "print" ), "attribute", "new-page" ), "type", "yes" ), "value", "yes" ) );
		encodingE.appendChild( attr( attr( attr( attr( create( "supports" ), "element", "print" ), "attribute", "new-system" ), "type", "yes" ), "value", "yes" ) );
		encodingE.appendChild( attr( attr( create( "supports" ), "element", "stem" ), "type", "no" ) );
		
		Element defE = create( "defaults" );
		root.appendChild( defE );
		
		Element scaleE = create( "scaling" );
		defE.appendChild( scaleE );
		scaleE.appendChild( create( "millimeters", "7.05556" ) );
		scaleE.appendChild( create( "tenths", "40" ) );
		
		Element layoutE = create( "page-layout" );
		defE.appendChild( layoutE );
		layoutE.appendChild( create( "page-height", "1683.36" ) );
		layoutE.appendChild( create( "page-width", "1190.88" ) );
		
		Element marginEvenE = attr( create( "page-margins" ), "type", "even" );
		layoutE.appendChild( marginEvenE );
		marginEvenE.appendChild( create( "left-margin", "56.6929" ) );
		marginEvenE.appendChild( create( "right-margin", "56.6929" ) );
		marginEvenE.appendChild( create( "top-margin", "56.6929" ) );
		marginEvenE.appendChild( create( "bottom-margin", "113.386" ) );
		
		Element marginOddE = attr( create( "page-margins" ), "type", "odd" );
		layoutE.appendChild( marginOddE );
		marginOddE.appendChild( create( "left-margin", "56.6929" ) );
		marginOddE.appendChild( create( "right-margin", "56.6929" ) );
		marginOddE.appendChild( create( "top-margin", "56.6929" ) );
		marginOddE.appendChild( create( "bottom-margin", "113.386" ) );
		
		defE.appendChild( attr( attr( create( "word-font" ), "font-family", "FreeSerif" ), "font-size", "10" ) );
		defE.appendChild( attr( attr( create( "lyric-font" ), "font-family", "FreeSerif" ), "font-size", "11" ) );
		
		Element cred1E = attr( create( "credit" ), "page", "1" );
		root.appendChild( cred1E );
		cred1E.appendChild( attr( attr( attr( attr( attr( create( "credit-words", title ), "default-x", "595.44" ), "default-y", "1626.67" ), 
				"justify", "center" ), "valign", "top" ), "font-size", "24" ) );
		Element cred2E = attr( create( "credit" ), "page", "1" );
		root.appendChild( cred2E );
		cred2E.appendChild( attr( attr( attr( attr( attr( create( "credit-words", composer ), "default-x", "1134.19" ), "default-y", "1526.67" ), 
				"justify", "right" ), "valign", "bottom" ), "font-size", "12" ) );
	}
	
	/**
	 * Erstellt die Part-Liste des Dokuments
	 * @param root Das root-Element
	 */
	private void createPartList( Element root ) {
		Instrument[] instruments = song.getInstruments();
		
		Element partlistE = create( "part-list" );
		root.appendChild( partlistE );
		
		for( int i = 0; i < instruments.length; i++ ) {
			Instrument instrument = instruments[ i ];
			
			Element partE = attr( create( "score-part" ), "id", "P" + ( i +1 ) );
			partlistE.appendChild( partE );
			
			partE.appendChild( create( "part-name", instrument.getName() ) );
			partE.appendChild( create( "part-abbreviation", instrument.getShortName() ) );
			
			Element scoreInE = attr( create( "score-instrument" ), "id", "P" + ( i +1 ) + "-I1" );
			partE.appendChild( scoreInE );
			scoreInE.appendChild( create( "instrument-name", instrument.getName() ) );
			
			partE.appendChild( attr( attr( create( "midi-device" ), "id", "P" + ( i +1 ) + "-I1" ), "port", "1" ) );
			
			Element midiInstrE = attr( create( "midi-instrument" ), "id", "P" + ( i +1 ) + "-I1" );
			partE.appendChild( midiInstrE );
			midiInstrE.appendChild( create( "midi-channel", Integer.toString( i +1 ) ) );
			midiInstrE.appendChild( create( "midi-program", Integer.toString( instrument.getMidiProgram() ) ) );
			midiInstrE.appendChild( create( "volume", Float.toString( instrument.getVolume() ) ) );
			midiInstrE.appendChild( create( "pan", "0" ) );
		}
	}
	
	/**
	 * Erstellt die eigentlichen Parts des Dokuments
	 * 
	 * @param root Das root-Element
	 */
	private void createParts( Element root ) {
		for( int p = 0; p < song.size(); p++ ) {
			Part part = song.get( p );
			
			Element partE = attr( create( "part" ), "id", "P" + ( p +1 ) );
			root.appendChild( partE );
			
			for( int m = 0; m < part.size(); m++ ) {
				Measure measure = part.get( m );
				
				Element measureE = attr( create( "measure" ), "number", Integer.toString( m + 1 ) );
				partE.appendChild( measureE );
				
				if( m == 0 ) {
					Element attribsE = create( "attributes" );
					measureE.appendChild( attribsE );
					attribsE.appendChild( create( "divisions", Integer.toString( measure.getDivision() ) ) );
					
					Element keyE = create( "key" );
					attribsE.appendChild( keyE );
					keyE.appendChild( create( "fifths", Integer.toString( measure.getFifths() ) ) );
					
					Element timeE = create( "time" );
					attribsE.appendChild( timeE );
					timeE.appendChild( create( "beats", Integer.toString( measure.getBeats() ) ) );
					timeE.appendChild( create( "beat-type", Integer.toString( measure.getBeattype() ) ) );
					
					Element clefE = create( "clef" );
					attribsE.appendChild( clefE );
					clefE.appendChild( create( "sign", measure.getClef().signToString() ) );
					clefE.appendChild( create( "line", Integer.toString( measure.getClef().getLine() ) ) );
				} else {
					Measure oldMeasure = part.get( m-1 );
					Element attribsE = create( "attributes" );
					if( oldMeasure.getDivision() != measure.getDivision() ) {
						attribsE.appendChild( create( "divisions", Integer.toString( measure.getDivision() ) ) );
						measureE.appendChild( attribsE );
					} if( oldMeasure.getFifths() != measure.getFifths() ) {
						attribsE.appendChild( create( "key" ).appendChild( create( "fifths", Integer.toString( measure.getFifths() ) ) ) );
						measureE.appendChild( attribsE );
					} if( oldMeasure.getBeats() != measure.getBeats() || oldMeasure.getBeattype() != measure.getBeattype() ) {
						Element timeE = create( "time" );
						attribsE.appendChild( timeE );
						timeE.appendChild( create( "beats", Integer.toString( measure.getBeats() ) ) );
						timeE.appendChild( create( "beat-type", Integer.toString( measure.getBeattype() ) ) );
						measureE.appendChild( attribsE );
					} if( !oldMeasure.getClef().equals( measure.getClef() ) ) {
						Element clefE = create( "clef" );
						attribsE.appendChild( clefE );
						clefE.appendChild( create( "sign", measure.getClef().signToString() ) );
						clefE.appendChild( create( "line", Integer.toString( measure.getClef().getLine() ) ) );
						measureE.appendChild( attribsE );
					}
				}
				
				for( int n = 0; n < measure.size(); n++ ) {
					INote iNote = measure.get( n );
					
					Element noteE = create( "note" );
					measureE.appendChild( noteE );
					
					if( iNote instanceof SNote ) {
						SNote snote = (SNote) iNote;
						
						createSNote( noteE, snote );
					} else if( iNote instanceof Rest ) {
						Rest rest = (Rest) iNote;
						
						noteE.appendChild( create( "rest" ) );
						
						noteE.appendChild( create( "duration", Integer.toString( rest.getDuration() ) ) );
						noteE.appendChild( create( "voice", Integer.toString( 1 ) ) );
						noteE.appendChild( create( "type", NoteHelper.getDurationString( rest ) ) );
						if( NoteHelper.hasDot( rest ) ) noteE.appendChild( create( "dot" ) );
					} else if( iNote instanceof Chord ) {
						Chord chord = (Chord) iNote;
						for( int c = 0; c < chord.size(); c++ ) {
							if( c == 0 ) {
								createSNote( noteE, chord.get( c ) );
							} else {
								noteE = create( "note" );
								measureE.appendChild( noteE );
								
								noteE.appendChild( create( "chord" ) );
								
								createSNote( noteE, chord.get( c ) );
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Speichert eine {@link SNote} in einen Element ab
	 * 
	 * @param ele Das Element in dem die Note abgespeichert werden soll
	 * @param snote Die zu speichernde Note
	 */
	private void createSNote( Element ele, SNote snote ) {
		Element pitchE = create( "pitch" );
		ele.appendChild( pitchE );
		pitchE.appendChild( create( "step", NoteHelper.getToneString( snote ) ) );
		if( NoteHelper.getAlter( snote ) != 0 )
			pitchE.appendChild( create( "alter", Integer.toString( NoteHelper.getAlter( snote ) ) ) );
		pitchE.appendChild( create( "octave", Integer.toString( NoteHelper.getOctave( snote ) ) ) );
		
		ele.appendChild( create( "duration", Integer.toString( snote.getDuration() ) ) );
		ele.appendChild( create( "voice", Integer.toString( 1 ) ) );
		ele.appendChild( create( "type", NoteHelper.getDurationString( snote ) ) );
		if( NoteHelper.hasDot( snote ) ) ele.appendChild( create( "dot" ) );
	}
	
	private Element create( String tagName ) {
		return doc.createElement( tagName );
	}
	
	private Element create( String tagName, String content ) {
		Element child = doc.createElement( tagName );
		child.setTextContent( content );
		return child;
	}
	
	private Element attr( Element em, String attr, String value ) {
		em.setAttribute( attr, value );
		return em;
	}
	
	private Document createDocument() throws XMLException {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dbuilder = null;
		try {
			dbuilder = dbfactory.newDocumentBuilder();
		} catch ( ParserConfigurationException e ) {
			throw new XMLException( "Could not generate a Document" );
		}
		
		Document doc = dbuilder.newDocument();
		return doc;
	}
	
	private void saveDocumentTo() throws XMLException {
		try {
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transf = tfactory.newTransformer();
			
			transf.setOutputProperty( OutputKeys.INDENT, "yes" );
			transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transf.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
			transf.setOutputProperty( OutputKeys.METHOD, "xml" );
			
			DOMImplementation impl = doc.getImplementation();
			DocumentType doctype = impl.createDocumentType( "doctype", 
					"-//Recordare//DTD MusicXML 3.0 Partwise//EN", "http://www.musicxml.org/dtds/partwise.dtd" );
			
			transf.setOutputProperty( OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId() );
			transf.setOutputProperty( OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId() );
			
			DOMSource src = new DOMSource( doc );
			StreamResult resultS = new StreamResult( file );
			transf.transform( src, resultS );
			
		} catch ( TransformerException e ) {
			throw new XMLException( "Could not save the File" );
		}
	}
}
