package net.sourceforge.jaad.mp4.api;

import java.util.List;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox;

public class MetaData<T> {

	//TODO: use a map instead?!
	private String artist, title, albumArtist, album, composer;
	private String comments;

	MetaData() {
		artist = "";
		title = "";
		albumArtist = "";
		album = "";
		composer = "";
		comments = "";
	}

	MetaData(Box meta) {
		this();
		
		//hdlr = (HandlerBox) meta.getChild(BoxTypes.HANDLER_BOX);
		//standard boxes
		//if(meta.containsChild(BoxTypes.PRIMARY_ITEM_BOX)) pitm = (PrimaryItemBox) meta.getChild(BoxTypes.PRIMARY_ITEM_BOX);
		//if(meta.containsChild(BoxTypes.DATA_INFORMATION_BOX)) dinf = (ContainerBox) meta.getChild(BoxTypes.DATA_INFORMATION_BOX);
		//if(meta.containsChild(BoxTypes.ITEM_LOCATION_BOX)) iloc = (ItemLocationBox) meta.getChild(BoxTypes.ITEM_LOCATION_BOX);
		//if(meta.containsChild(BoxTypes.ITEM_PROTECTION_BOX)) ipro = (ItemProtectionBox) meta.getChild(BoxTypes.ITEM_PROTECTION_BOX);
		//if(meta.containsChild(BoxTypes.ITEM_INFORMATION_BOX)) iinf = (ItemInformationBox) meta.getChild(BoxTypes.ITEM_INFORMATION_ENTRY);
		//TODO: optional IPMPControlBox
		//id3
		//if(meta.containsChild(BoxTypes.ID3_TAG_BOX)) id3 = (ID3TagBox) meta.getChild(BoxTypes.ID3_TAG_BOX);
		//itunes
		if(meta.containsChild(BoxTypes.ITUNES_META_LIST_BOX)) parseITunesMetaData(meta.getChild(BoxTypes.ITUNES_META_LIST_BOX));
	}

	private void parseITunesMetaData(Box ilst) {
		final List<Box> boxes = ilst.getChildren();
		long l;
		ITunesMetadataBox data;
		for(Box box : boxes) {
			l = box.getType();
			data = (ITunesMetadataBox) box.getChild(BoxTypes.ITUNES_METADATA_BOX);
			if(l==BoxTypes.ARTIST_NAME_BOX) artist = data.getText();
			else if(l==BoxTypes.TRACK_NAME_BOX) title = data.getText();
			else if(l==BoxTypes.ALBUM_ARTIST_NAME_BOX) albumArtist = data.getText();
			else if(l==BoxTypes.ALBUM_NAME_BOX) album = data.getText();
			else if(l==BoxTypes.COMPOSER_NAME_BOX) composer = data.getText();
			else if(l==BoxTypes.COMMENTS_BOX) comments = data.getText();
		}
	}

	public String getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public String getAlbum() {
		return album;
	}

	public String getComposer() {
		return composer;
	}

	public String getComments() {
		return comments;
	}
}
