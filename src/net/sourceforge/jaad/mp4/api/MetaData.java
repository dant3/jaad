package net.sourceforge.jaad.mp4.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox;

public class MetaData {

	/*public enum Genre {

	UNDEFINED("undefined"),
	BLUES("blues"),
	CLASSIC_ROCK("classic rock"),
	COUNTRY("country"),
	DANCE("dance"),
	DISCO("disco"),
	FUNK("funk"),
	GRUNGE("grunge"),
	HIP_HOP("hip hop"),
	JAZZ("jazz"),
	METAL("metal"),
	NEW_AGE("new age"),
	OLDIES("oldies"),
	OTHER("other"),
	POP("pop"),
	R_AND_B("r and b"),
	RAP("rap"),
	REGGAE("reggae"),
	ROCK("rock"),
	TECHNO("techno"),
	INDUSTRIAL("industrial"),
	ALTERNATIVE("alternative"),
	SKA("ska"),
	DEATH_METAL("death metal"),
	PRANKS("pranks"),
	SOUNDTRACK("soundtrack"),
	EURO_TECHNO("euro techno"),
	AMBIENT("ambient"),
	TRIP_HOP("trip hop"),
	VOCAL("vocal"),
	JAZZ_FUNK("jazz funk"),
	FUSION("fusion"),
	TRANCE("trance"),
	CLASSICAL("classical"),
	INSTRUMENTAL("instrumental"),
	ACID("acid"),
	HOUSE("house"),
	GAME("game"),
	SOUND_CLIP("sound clip"),
	GOSPEL("gospel"),
	NOISE("noise"),
	ALTERNROCK("alternrock"),
	BASS("bass"),
	SOUL("soul"),
	PUNK("punk"),
	SPACE("space"),
	MEDITATIVE("meditative"),
	INSTRUMENTAL_POP("instrumental pop"),
	INSTRUMENTAL_ROCK("instrumental rock"),
	ETHNIC("ethnic"),
	GOTHIC("gothic"),
	DARKWAVE("darkwave"),
	TECHNO_INDUSTRIAL("techno industrial"),
	ELECTRONIC("electronic"),
	POP_FOLK("pop folk"),
	EURODANCE("eurodance"),
	DREAM("dream"),
	SOUTHERN_ROCK("southern rock"),
	COMEDY("comedy"),
	CULT("cult"),
	GANGSTA("gangsta"),
	TOP_("top "),
	CHRISTIAN_RAP("christian rap"),
	POP_FUNK("pop funk"),
	JUNGLE("jungle"),
	NATIVE_AMERICAN("native american"),
	CABARET("cabaret"),
	NEW_WAVE("new wave"),
	PSYCHEDELIC("psychedelic"),
	RAVE("rave"),
	SHOWTUNES("showtunes"),
	TRAILER("trailer"),
	LO_FI("lo fi"),
	TRIBAL("tribal"),
	ACID_PUNK("acid punk"),
	ACID_JAZZ("acid jazz"),
	POLKA("polka"),
	RETRO("retro"),
	MUSICAL("musical"),
	ROCK_AND_ROLL("rock and roll"),
	HARD_ROCK("hard rock"),
	FOLK("folk"),
	FOLK_ROCK("folk rock"),
	NATIONAL_FOLK("national folk"),
	SWING("swing"),
	FAST_FUSION("fast fusion"),
	BEBOB("bebob"),
	LATIN("latin"),
	REVIVAL("revival"),
	CELTIC("celtic"),
	BLUEGRASS("bluegrass"),
	AVANTGARDE("avantgarde"),
	GOTHIC_ROCK("gothic rock"),
	PROGRESSIVE_ROCK("progressive rock"),
	PSYCHEDELIC_ROCK("psychedelic rock"),
	SYMPHONIC_ROCK("symphonic rock"),
	SLOW_ROCK("slow rock"),
	BIG_BAND("big band"),
	CHORUS("chorus"),
	EASY_LISTENING("easy listening"),
	ACOUSTIC("acoustic"),
	HUMOUR("humour"),
	SPEECH("speech"),
	CHANSON("chanson"),
	OPERA("opera"),
	CHAMBER_MUSIC("chamber music"),
	SONATA("sonata"),
	SYMPHONY("symphony"),
	BOOTY_BASS("booty bass"),
	PRIMUS("primus"),
	PORN_GROOVE("porn groove"),
	SATIRE("satire"),
	SLOW_JAM("slow jam"),
	CLUB("club"),
	TANGO("tango"),
	SAMBA("samba"),
	FOLKLORE("folklore"),
	BALLAD("ballad"),
	POWER_BALLAD("power ballad"),
	RHYTHMIC_SOUL("rhythmic soul"),
	FREESTYLE("freestyle"),
	DUET("duet"),
	PUNK_ROCK("punk rock"),
	DRUM_SOLO("drum solo"),
	A_CAPELLA("a capella"),
	EURO_HOUSE("euro house"),
	DANCE_HALL("dance hall");
	private String name;

	private Genre(String name) {
	this.name = name;
	}
	}*/
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

	public static class Field<T> {

		public static final Field<String> ARTIST = new Field<String>();
		public static final Field<String> TITLE = new Field<String>();
		public static final Field<String> ALBUM_ARTIST = new Field<String>();
		public static final Field<String> ALBUM = new Field<String>();
		public static final Field<Integer> TRACK_NUMBER = new Field<Integer>();
		public static final Field<Integer> TOTAL_TRACKS = new Field<Integer>();
		public static final Field<Integer> DISK_NUMBER = new Field<Integer>();
		public static final Field<String> COMPOSER = new Field<String>();
		public static final Field<String> COMMENTS = new Field<String>();
		public static final Field<Integer> TEMPO = new Field<Integer>();
		public static final Field<Integer> RELEASE_DATE = new Field<Integer>();
		public static final Field<String> GENRE = new Field<String>();
		public static final Field<String> ENCODER_NAME = new Field<String>();
		public static final Field<String> ENCODER_TOOL = new Field<String>();
		public static final Field<String> COPYRIGHT = new Field<String>();
		public static final Field<Boolean> COMPILATION = new Field<Boolean>();
		public static final Field<List<Artwork>> COVER_ARTWORK = new Field<List<Artwork>>();
		public static final Field<String> GROUPING = new Field<String>();
		public static final Field<String> LYRICS = new Field<String>();
		public static final Field<String> TV_SHOW = new Field<String>();
		public static final Field<Integer> RATING = new Field<Integer>();
		public static final Field<Integer> PODCAST = new Field<Integer>();
		public static final Field<String> PODCAST_URL = new Field<String>();
		public static final Field<String> CATEGORY = new Field<String>();
		public static final Field<String> KEYWORDS = new Field<String>();
		public static final Field<Integer> EPISODE_GLOBAL_UNIQUE_ID = new Field<Integer>();
		public static final Field<String> DESCRIPTION = new Field<String>();
		public static final Field<String> TV_NETWORK = new Field<String>();
		public static final Field<String> TV_EPISODE = new Field<String>();
		public static final Field<Integer> TV_EPISODE_NUMBER = new Field<Integer>();
		public static final Field<Integer> TV_SEASON = new Field<Integer>();
		public static final Field<String> PURCHASE_DATE = new Field<String>();
		public static final Field<String> GAPLESS_PLAYBACK = new Field<String>();
		public static final Field<Boolean> HD_VIDEO = new Field<Boolean>();

		private Field() {
		}
	}
	private Map<Field<?>, Object> contents;

	MetaData() {
		contents = new HashMap<Field<?>, Object>();
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
		if(meta.hasChild(BoxTypes.ITUNES_META_LIST_BOX)) parseITunesMetaData(meta.getChild(BoxTypes.ITUNES_META_LIST_BOX));
	}

	private void parseITunesMetaData(Box ilst) {
		final List<Box> boxes = ilst.getChildren();
		long l;
		ITunesMetadataBox data;
		for(Box box : boxes) {
			l = box.getType();
			data = (ITunesMetadataBox) box.getChild(BoxTypes.ITUNES_METADATA_BOX);
			if(l==BoxTypes.ARTIST_NAME_BOX) put(Field.ARTIST, data.getText());
			else if(l==BoxTypes.TRACK_NAME_BOX) put(Field.TITLE, data.getText());
			else if(l==BoxTypes.ALBUM_ARTIST_NAME_BOX) put(Field.ALBUM_ARTIST, data.getText());
			else if(l==BoxTypes.ALBUM_NAME_BOX) put(Field.ALBUM, data.getText());
			else if(l==BoxTypes.TRACK_NUMBER_BOX) {
				byte[] b = data.getData();
				put(Field.TRACK_NUMBER, new Integer(b[7]));
				put(Field.TOTAL_TRACKS, new Integer(b[9]));
			}
			else if(l==BoxTypes.DISK_NUMBER_BOX) put(Field.DISK_NUMBER, data.getInteger());
			else if(l==BoxTypes.COMPOSER_NAME_BOX) put(Field.COMPOSER, data.getText());
			else if(l==BoxTypes.COMMENTS_BOX) put(Field.COMMENTS, data.getText());
			else if(l==BoxTypes.TEMPO_BOX) put(Field.TEMPO, data.getInteger());
			else if(l==BoxTypes.RELEASE_DATE_BOX) put(Field.RELEASE_DATE, Integer.parseInt(data.getText()));
			else if(l==BoxTypes.GENRE_BOX||l==BoxTypes.CUSTOM_GENRE_BOX) {
				final String s;
				if(data.getDataType()==ITunesMetadataBox.DataType.UTF8) s = data.getText();
				else s = STANDARD_GENRES[data.getInteger()];
				put(Field.GENRE, s);
			}
			else if(l==BoxTypes.ENCODER_NAME_BOX) put(Field.ENCODER_NAME, data.getText());
			else if(l==BoxTypes.ENCODER_TOOL_BOX) put(Field.ENCODER_TOOL, data.getText());
			else if(l==BoxTypes.COPYRIGHT_BOX) put(Field.COPYRIGHT, data.getText());
			else if(l==BoxTypes.COMPILATION_PART_BOX) put(Field.COMPILATION, data.getBoolean());
			else if(l==BoxTypes.COVER_BOX) {
				if(contents.containsKey(Field.COVER_ARTWORK)) get(Field.COVER_ARTWORK).add(new Artwork(Artwork.Type.forDataType(data.getDataType()), data.getData()));
				else put(Field.COVER_ARTWORK, new ArrayList<Artwork>());
			}
			else if(l==BoxTypes.GROUPING_BOX) put(Field.GROUPING, data.getText());
			else if(l==BoxTypes.LYRICS_BOX) put(Field.LYRICS, data.getText());
			else if(l==BoxTypes.RATING_BOX) put(Field.RATING, data.getInteger());
			else if(l==BoxTypes.PODCAST_BOX) put(Field.PODCAST, data.getInteger());
			else if(l==BoxTypes.PODCAST_URL_BOX) put(Field.PODCAST_URL, data.getText());
			else if(l==BoxTypes.CATEGORY_BOX) put(Field.CATEGORY, data.getText());
			else if(l==BoxTypes.KEYWORD_BOX) put(Field.KEYWORDS, data.getText());
			else if(l==BoxTypes.DESCRIPTION_BOX) put(Field.DESCRIPTION, data.getText());
			else if(l==BoxTypes.LONG_DESCRIPTION_BOX) put(Field.DESCRIPTION, data.getText());
			else if(l==BoxTypes.TV_SHOW_BOX) put(Field.TV_SHOW, data.getText());
			else if(l==BoxTypes.TV_NETWORK_NAME_BOX) put(Field.TV_NETWORK, data.getText());
			else if(l==BoxTypes.TV_EPISODE_BOX) put(Field.TV_EPISODE, data.getText());
			else if(l==BoxTypes.TV_EPISODE_NUMBER_BOX) put(Field.TV_EPISODE_NUMBER, data.getInteger());
			else if(l==BoxTypes.TV_SEASON_BOX) put(Field.TV_SEASON, data.getInteger());
			else if(l==BoxTypes.PURCHASE_DATE_BOX) put(Field.PURCHASE_DATE, data.getText());
			else if(l==BoxTypes.GAPLESS_PLAYBACK_BOX) put(Field.GAPLESS_PLAYBACK, data.getText());
			else if(l==BoxTypes.HD_VIDEO_BOX) put(Field.HD_VIDEO, data.getBoolean());
		}
	}

	private <T> void put(Field<T> field, T value) {
		put(field, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Field<T> field) {
		return (T) contents.get(field);
	}
}
