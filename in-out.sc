~buf = Buffer.alloc(s, 65536, 8);
~buf.write( "~/input_data_fabric.aiff".standardizePath, "aiff", "int16", 0, 0, true )
(
SynthDef( \grab_input,
	{ | buff |
		DiskOut.ar( buff, In.ar( 0, 8 ) );
		Out.ar( ( 0 .. 7 ), SinOsc.ar( 8.collect{ | x | 521.387*x+300 } ));
	}
).send(s);
)
~capture = Synth( \grab_input, [ ~buf ] );

~capture.free;

~buf.close;
