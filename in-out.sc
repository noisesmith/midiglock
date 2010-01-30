Platform.case (
  { \osx }, {
  },
  { \linux }, {
	  SwingOSC.program = "/usr/local/bin/SwingOSC.jar";
	  //	  SwingOSC.java = "/usr/lib/jvm/java-1.5.0-sun-1.5.0.17/bin/java";
	  SwingOSC.default.boot;
  }
);

FreqScope( busNum: 0 );
~fqs = [ 860, 1100, 1700, 1975, 2793, 3729, 4698, 6271 ];

// register to receive the analysis messages

~buffs = 8.collect{ Buffer.alloc( s, 1024, 1 ) };

(
SynthDef( \io, {
	var result;
	Out.ar( ( 0..7 ), SinOsc.ar( ~fqs ) );
	result = SoundIn.ar( ( 0..7 ) );
	8.do{ | n | FFT( ~buffs[ n ], result[ n ] ) } } ).send( s );

)

~io = Synth( \io );

1024.do{ | n |
	~buffs[ 6 ].get( n,
		{ | x | if( x.abs > 100, { [ x, n ].postln } ) } ) };
