// for testing
// ~buf = Buffer.alloc(s, 65536, 8);
// ~buf.write( "~/input_data_fabric.aiff".standardizePath,
//	"aiff", "int16", 0, 0, true );

~fqs = [ 860, 1100, 1700, 1975, 2793, 3729, 4698, 6271 ];

SynthDef( \io,
	{ | buff |
		// more testing
		// DiskOut.ar( buff, In.ar( 0, 8 ) );
		Out.ar( ( 0 .. 7 ), SinOsc.ar( ~fqs ));
		SoundIn( ( 0 .. 7 ))
	}
).send(s);


// yet more testing
// ~capture = Synth( \grab_input, [ ~buf ] );
// ~capture.free;
// ~buf.close;

// JFreqScope.new( 400, 200, 0 );
// { BRF.ar(WhiteNoise.ar(1), MouseX.kr(100, 20000, 1), 3) }.play;
// {
// 	var selection = MouseX.kr( 0, 8, 0 ).floor;
// 	BRF.ar(Mix( SinOsc.ar( ~fqs ) ),
// 	~fqs[  ], 3)}.play;

// TODO: figure out the frequency / rq args for each of the 8 filters