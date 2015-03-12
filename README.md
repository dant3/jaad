[JAAD](http://jaadec.sourceforge.net/)
==========

JAAD is an open-source AAC decoder and MP4 demultiplexer library written completely in Java. It is platform-independent and portable.


Usage
----------

**AAC decoder:**

The AAC decoder needs to be instantiated with a DecoderSpecificInfo, which contains information about the following data. This info can be retrieved from the container, hence an AAC stream is never present without a container.
After that, the decodeFrame method can be called repeatedly to decode the frames. The resulting audio data (PCM) is stored in the SampleBuffer.

```java
Decoder dec = new Decoder(decoderSpecificinfo);
SampleBuffer buf = new SampleBuffer();
dec.decodeFrame(aacFrame, buf);
//the aacFrame array contains the AAC frame to decode
byte[] audio = buf.getData(); //this array contains the raw PCM audio data
```

**ADTS demultiplexer:**

If the AAC data is stored in an ADTS container, the ADTSDemultiplexer can be used to parse an InputStream.

```java
ADTSDemultiplexer adts = new ADTSDemultiplexer(inputStream);
byte[] decoderSpecificInfo = adts.getDecoderSpecificInfo();
byte[] frame;
while((frame = adts.readNextFrame())!=null) {
    //do something with the frame, e.g. pass it to the AAC decoder
}
```


**MP4 demultiplexing API**

To parse an MP4 container, you can use the net.sourceforge.jaad.mp4 package. Since the MP4 container format has many capabilities, the API supports more features than the ADTS demultiplexer. The central class is the MP4Container which can parse an InputStream.
For more features of the MP4 API, see the [net.sourceforge.jaad.MP4Info](http://jaadec.sourceforge.net/javadoc/net/sourceforge/jaad/MP4Info.html) class and the [Javadoc](http://jaadec.sourceforge.net/javadoc/index.html).

```java
MP4Container container = new MP4Container(inputStream);
Movie movie = container.getMovie();
List<Track> tracks = movie.getTracks(AudioTrack.AudioCodec.AAC);
if(tracks.size()>0) {
    Track track = tracks.get(0);
    byte[] decoderSpecificInfo = track.getDecoderSpecificInfo();
    byte[] frame = track.readNextFrame();
    //do something with the frame, e.g. pass it to the AAC decoder
}
```


**Java Sound API**

You can also use JAAD indirectly with the Java Sound API.
When the jar-file is in your classpath, JAAD will be automatically registered as a service provider for Java Sound.
You just need to create a SourceDataLine and JAAD will be called if AAC audio data is detected.
For more information, read: http://www.oracle.com/technetwork/java/index-jsp-140234.html

Read more here: http://jaadec.sourceforge.net/usage.php


Get It
----------

You can download the binaries of the current version here: [Download jaad-0.8.4.jar](http://sourceforge.net/projects/jaadec/files/latest)


License
----------

Public domain.
