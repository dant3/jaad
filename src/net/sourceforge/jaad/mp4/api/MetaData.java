package net.sourceforge.jaad.mp4.api;

import java.util.ArrayList;
import java.util.EnumMap;
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
		RELEASE_DATE(Integer.class),
		GENRE(String.class),
		ENCODER_NAME(String.class),
		ENCODER_TOOL(String.class),
		COPYRIGHT(String.class),
		COMPILATION(Boolean.class),
		COVER_ARTWORK(List.class),
		GROUPING(String.class),
		LYRICS(String.class),
		TV_SHOW(String.class),
		RATING(Integer.class),
		PODCAST(Integer.class),
		PODCAST_URL(Integer.class),
		CATEGORY(String.class),
		KEYWORDS(String.class),
		EPISODE_GLOBAL_UNIQUE_ID(Integer.class),
		DESCRIPTION(String.class),
		TV_NETWORK(String.class),
		TV_EPISODE(String.class),
		TV_EPISODE_NUMBER(Integer.class),
		TV_SEASON(Integer.class),
		PURCHASE_DATE(String.class),
		GAPLESS_PLAYBACK(String.class),
		HD_VIDEO(Boolean.class);
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
		if(meta.hasChild(BoxTypes.ITUNES_META_LIST_BOX)) parseITunesMetaData(meta.getChild(BoxTypes.ITUNES_META_LIST_BOX));
	}

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
			else if(l==BoxTypes.DISK_NUMBER_BOX) contents.put(Field.DISK_NUMBER, data.getNumber());
			else if(l==BoxTypes.COMPOSER_NAME_BOX) contents.put(Field.COMPOSER, data.getText());
			else if(l==BoxTypes.COMMENTS_BOX) contents.put(Field.COMMENTS, data.getText());
			else if(l==BoxTypes.TEMPO_BOX) contents.put(Field.TEMPO, data.getNumber());
			else if(l==BoxTypes.RELEASE_DATE_BOX) contents.put(Field.RELEASE_DATE, Integer.parseInt(data.getText()));
			else if(l==BoxTypes.GENRE_BOX||l==BoxTypes.CUSTOM_GENRE_BOX) {
				final String s;
				if(data.getDataType()==ITunesMetadataBox.DataType.UTF8) s = data.getText();
				else s = STANDARD_GENRES[(int) data.getNumber()];
				contents.put(Field.GENRE, s);
			}
			else if(l==BoxTypes.ENCODER_NAME_BOX) contents.put(Field.ENCODER_NAME, data.getText());
			else if(l==BoxTypes.ENCODER_TOOL_BOX) contents.put(Field.ENCODER_TOOL, data.getText());
			else if(l==BoxTypes.COPYRIGHT_BOX) contents.put(Field.COPYRIGHT, data.getText());
			else if(l==BoxTypes.COMPILATION_PART_BOX) contents.put(Field.COMPILATION, data.getBoolean());
			else if(l==BoxTypes.COVER_BOX) {
				if(contents.containsKey(Field.COVER_ARTWORK)) ((List<Artwork>) get(Field.COVER_ARTWORK)).add(new Artwork(Artwork.Type.forDataType(data.getDataType()), data.getData()));
				else contents.put(Field.COVER_ARTWORK, new ArrayList<Artwork>());
			}
			else if(l==BoxTypes.GROUPING_BOX) contents.put(Field.GROUPING, data.getText());
			else if(l==BoxTypes.LYRICS_BOX) contents.put(Field.LYRICS, data.getText());
			else if(l==BoxTypes.RATING_BOX) contents.put(Field.RATING, data.getNumber());
			else if(l==BoxTypes.PODCAST_BOX) contents.put(Field.PODCAST, data.getNumber());
			else if(l==BoxTypes.PODCAST_URL_BOX) contents.put(Field.PODCAST_URL, data.getNumber());
			else if(l==BoxTypes.CATEGORY_BOX) contents.put(Field.CATEGORY, data.getText());
			else if(l==BoxTypes.KEYWORD_BOX) contents.put(Field.KEYWORDS, data.getText());
			else if(l==BoxTypes.DESCRIPTION_BOX) contents.put(Field.DESCRIPTION, data.getText());
			else if(l==BoxTypes.LONG_DESCRIPTION_BOX) contents.put(Field.DESCRIPTION, data.getText());
			else if(l==BoxTypes.TV_SHOW_BOX) contents.put(Field.TV_SHOW, data.getText());
			else if(l==BoxTypes.TV_NETWORK_NAME_BOX) contents.put(Field.TV_NETWORK, data.getText());
			else if(l==BoxTypes.TV_EPISODE_BOX) contents.put(Field.TV_EPISODE, data.getText());
			else if(l==BoxTypes.TV_EPISODE_NUMBER_BOX) contents.put(Field.TV_EPISODE_NUMBER, data.getNumber());
			else if(l==BoxTypes.TV_SEASON_BOX) contents.put(Field.TV_SEASON, data.getNumber());
			else if(l==BoxTypes.PURCHASE_DATE_BOX) contents.put(Field.PURCHASE_DATE, data.getText());
			else if(l==BoxTypes.GAPLESS_PLAYBACK_BOX) contents.put(Field.GAPLESS_PLAYBACK, data.getText());
			else if(l==BoxTypes.HD_VIDEO_BOX) contents.put(Field.HD_VIDEO, data.getBoolean());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Field field) {
		final Object o = get(field);
		if(o!=null) return (T) o;
		return null;
	}
}
