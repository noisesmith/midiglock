// Thread Arrow Patch ESTRY

/*
	SwingOSC.program="/usr/local/bin/SwingOSC.jar";
	SwingOSC.default.boot;
	Quarks.gui;
*/ 

(
~inSigs = Bus.audio(s, 8);

~strips = 8.collect(Set[]);

// test case
// ~normalizeStrips.( [ Set[ 0, 1, 2 ], Set[ 3 ], Set[ 0, 4 ], Set[ 3, 5 ], Set[ 5, 7 ]/*, [ 1, 5 ] */] )

~normalizeStrips =
{ | strips |
	var modified = false;
	strips.size.collect
	{ | i |
		strips.size.do
		{ | j |
			if ( and( i != j, sect( strips[ i ], strips[ j ] ).size > 0 ),
				{
					modified = true;
					strips[ i ] = union( strips[ i ], strips[ j ] );
					strips[ j ] = Set[] } ) } };
	strips = strips.select { | st | st.size > 0 };
	if( modified,
		{ ~normalizeStrips.( strips ) },
		strips ) };

// ~notevals.size = 35
~noteVals = ( 26 .. 60 ).asSet;
)
// midi connection
MIDIClient.init( 1, 1 );
~uid = 1048576;
~midiChan = MIDIOut( 0, ~uid );
~midiChan.latency_( 0 );
~midiChan.connect( ~uid );


Pdef(\x);
Pdef(\x, Pbind(\type, \midi, \midiout, m, \midinote, Pseq([31, 32, 31], inf), \dur, Pseq([0.5,0.3, 0.2], inf)));
Pdef(\x).play;
