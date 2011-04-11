package net.sourceforge.jaad.mp4.api;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox;

public class MetaData<T> {

	private static final String[] STANDARD_GENRES = {
		"undefined",
		//IDv1 standard
		"blues",
		"classic rock",
		"country",
		"dance",
		"disco",
		"funk",
		"grunge",
		"hip hop",
		"jazz",
		"metal",
		"new age",
		"oldies",
		"other",
		"pop",
		"r and b",
		"rap",
		"reggae",
		"rock",
		"techno",
		"industrial",
		"alternative",
		"ska",
		"death metal",
		"pranks",
		"soundtrack",
		"euro techno",
		"ambient",
		"trip hop",
		"vocal",
		"jazz funk",
		"fusion",
		"trance",
		"classical",
		"instrumental",
		"acid",
		"house",
		"game",
		"sound clip",
		"gospel",
		"noise",
		"alternrock",
		"bass",
		"soul",
		"punk",
		"space",
		"meditative",
		"instrumental pop",
		"instrumental rock",
		"ethnic",
		"gothic",
		"darkwave",
		"techno industrial",
		"electronic",
		"pop folk",
		"eurodance",
		"dream",
		"southern rock",
		"comedy",
		"cult",
		"gangsta",
		"top ",
		"christian rap",
		"pop funk",
		"jungle",
		"native american",
		"cabaret",
		"new wave",
		"psychedelic",
		"rave",
		"showtunes",
		"trailer",
		"lo fi",
		"tribal",
		"acid punk",
		"acid jazz",
		"polka",
		"retro",
		"musical",
		"rock and roll",
		//winamp extension
		"hard rock",
		"folk",
		"folk rock",
		"national folk",
		"swing",
		"fast fusion",
		"bebob",
		"latin",
		"revival",
		"celtic",
		"bluegrass",
		"avantgarde",
		"gothic rock",
		"progressive rock",
		"psychedelic rock",
		"symphonic rock",
		"slow rock",
		"big band",
		"chorus",
		"easy listening",
		"acoustic",
		"humour",
		"speech",
		"chanson",
		"opera",
		"chamber music",
		"sonata",
		"symphony",
		"booty bass",
		"primus",
		"porn groove",
		"satire",
		"slow jam",
		"club",
		"tango",
		"samba",
		"folklore",
		"ballad",
		"power ballad",
		"rhythmic soul",
		"freestyle",
		"duet",
		"punk rock",
		"drum solo",
		"a capella",
		"euro house",
		"dance hall"
	};

	public enum Field {

		ARTIST(String.class),
		TITLE(String.class),
		ALBUM_ARTIST(String.class),
		ALBUM(String.class),
		TRACK_NUMBER(Integer.class),
		TOTAL_TRACKS(Integer.class),
		DISK_NUMBER(Integer.class),
		COMPOSER(String.class),
		COMMENTS(String.class),
		TEMPO(Integer.class),
		YEAR(Integer.class),
		GENRE(String.class),
		ENCODER_TOOL(String.class),
		COPYRIGHT(String.class),
		COMPILATION(Boolean.class),
		COVER_ARTWORK(List.class),
		GROUPING(String.class),
		LYRICS(String.class),
		TELEVISION_SHOW(String.class),
		RATING(Integer.class),
		PODCAST(Integer.class),
		PODCAST_URL(Integer.class),
		CATEGORY(String.class),
		KEYWORDS(String.class),
		EPISODE_GLOBAL_UNIQUE_ID(Integer.class),
		DESCRIPTION(String.class),
		TV_NETWORK_NAME(String.class),
		TV_EPISODE_NAME(String.class),
		TV_EPISODE_NUMBER(Integer.class),
		TV_SEASON(Integer.class),
		PURCHASE_DATE(String.class),
		GAPLESS_PLAYBACK(String.class);
		private Class<?> type;

		private Field(Class<?> type) {
			this.type = type;
		}

		private Class<?> getType() {
			return type;
		}
	}
	private Map<Field, Object> contents;

	MetaData() {
		contents = new EnumMap<Field, Object>(Field.class);
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

	//TODO: rating (rtng), podcast (pcst), category (catg), keyword (keyw), episode id (egid),
	//description (desc), tv network name (tvnn), tv episode number (tven), tv season (tvsn),
	//tv episode (tves), purchase date (purd), gapless playback (pgap)
	@SuppressWarnings({"unchecked", "unchecked"})
	private void parseITunesMetaData(Box ilst) {
		final List<Box> boxes = ilst.getChildren();
		long l;
		ITunesMetadataBox data;
		for(Box box : boxes) {
			l = box.getType();
			data = (ITunesMetadataBox) box.getChild(BoxTypes.ITUNES_METADATA_BOX);
			if(l==BoxTypes.ARTIST_NAME_BOX) contents.put(Field.ARTIST, data.getText());
			else if(l==BoxTypes.TRACK_NAME_BOX) contents.put(Field.TITLE, data.getText());
			else if(l==BoxTypes.ALBUM_ARTIST_NAME_BOX) contents.put(Field.ALBUM_ARTIST, data.getText());
			else if(l==BoxTypes.ALBUM_NAME_BOX) contents.put(Field.ALBUM, data.getText());
			else if(l==BoxTypes.TRACK_NUMBER_BOX) {
				byte[] b = data.getData();
				contents.put(Field.TRACK_NUMBER, b[7]);
				contents.put(Field.TOTAL_TRACKS, b[9]);
			}
			else if(l==BoxTypes.DISK_NUMBER_BOX) contents.put(Field.DISK_NUMBER, data.getInteger());
			else if(l==BoxTypes.COMPOSER_NAME_BOX) contents.put(Field.COMPOSER, data.getText());
			else if(l==BoxTypes.COMMENTS_BOX) contents.put(Field.COMMENTS, data.getText());
			else if(l==BoxTypes.TEMPO_BOX) contents.put(Field.TEMPO, data.getData()[5]);
			else if(l==BoxTypes.PUBLICATION_YEAR_BOX) contents.put(Field.YEAR, Integer.parseInt(data.getText()));
			else if(l==BoxTypes.GENRE_BOX||l==BoxTypes.CUSTOM_GENRE_BOX) {
				final String s;
				if(data.getFlags()==1) s = data.getText();
				else s = STANDARD_GENRES[data.getInteger()];
				contents.put(Field.GENRE, s);
			}
			else if(l==BoxTypes.ENCODER_TOOL_BOX) contents.put(Field.ENCODER_TOOL, data.getText());
			else if(l==BoxTypes.COPYRIGHT_BOX) contents.put(Field.COPYRIGHT, data.getText());
			else if(l==BoxTypes.COMPILATION_PART_BOX) contents.put(Field.COMPILATION, data.getBoolean());
			else if(l==BoxTypes.COVER_BOX) {
				if(contents.containsKey(Field.COVER_ARTWORK)) ((List<Artwork>) get(Field.COVER_ARTWORK)).add(new Artwork(Artwork.Type.forInt(data.getFlags()), data.getData()));
				else contents.put(Field.COVER_ARTWORK, new ArrayList<Artwork>());
			}
			else if(l==BoxTypes.GROUPING_BOX) contents.put(Field.GROUPING, data.getText());
			else if(l==BoxTypes.LYRICS_BOX) contents.put(Field.LYRICS, data.getText());
			else if(l==BoxTypes.TELEVISION_SHOW_BOX) contents.put(Field.TELEVISION_SHOW, data.getText());
			else if(l==BoxTypes.RATING_BOX) contents.put(Field.RATING, data.getInteger());
			else if(l==BoxTypes.PODCAST_BOX) contents.put(Field.PODCAST, data.getInteger());
			else if(l==BoxTypes.PODCAST_URL_BOX) contents.put(Field.PODCAST_URL, data.getInteger());
			else if(l==BoxTypes.CATEGORY_BOX) contents.put(Field.CATEGORY, data.getText());
			else if(l==BoxTypes.KEYWORD_BOX) contents.put(Field.KEYWORDS, data.getText());
			else if(l==BoxTypes.DESCRIPTION_BOX) contents.put(Field.DESCRIPTION, data.getText());
			else if(l==BoxTypes.TV_NETWORK_NAME_BOX) contents.put(Field.TV_NETWORK_NAME, data.getText());
			else if(l==BoxTypes.TV_EPISODE_BOX) contents.put(Field.TV_EPISODE_NAME, data.getText());
			else if(l==BoxTypes.TV_EPISODE_NUMBER_BOX) contents.put(Field.TV_EPISODE_NUMBER, data.getInteger());
			else if(l==BoxTypes.TV_SEASON_BOX) contents.put(Field.TV_SEASON, data.getInteger());
			else if(l==BoxTypes.PURCHASE_DATE_BOX) contents.put(Field.PURCHASE_DATE, data.getText());
			else if(l==BoxTypes.GAPLESS_PLAYBACK_BOX) contents.put(Field.GAPLESS_PLAYBACK, data.getText());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Field field) {
		final Object o = get(field);
		if(o!=null) return (T) o;
		return null;
	}
}
