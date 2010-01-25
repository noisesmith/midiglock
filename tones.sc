MIDIClient.init( 1, 1 );
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


~destination =
{ | destinations, uid |
	var i = 0;
	var result = nil;
	while { and( i < destinations.size, result.isNil ) }
	{
		if( destinations[ i ].uid == uid,
			{ result = i } );
		i = i+1;
	};
	destinations[ result ].uid }.( MIDIClient.destinations, 1048576 );

~midiChan = MIDIOut( 0, ~destination );
~midiChan.latency_( 0 );
~midiChan.connect( ~destination );

~rout = 40.collect {
	Routine {
		inf.do{
			~midiChan.noteOn( 0, ~noteVals.choose, 127.rand );
			1.0.rand.wait;
		}
	}
};

~total = 4;

~routControl = Routine {
inf.do {
	if( ~total < 0,
		{ ~rout.choose.stop },
		{ ~rout.choose.play } );
	~total = ~total + [ -1, 0, 1 ].choose;
	1.wait;
}
}

~routControl.play;
~routControl.isPlaying;

~countplaying = { var count = 0; ~rout.do{ | ro | if( ro.isPlaying, { count = count + 1 } ) }; count }

~rout.collect(_.isPlaying);
~countplaying.()
~total;

~route.collect(_.stop)

~total = -20;
~total = 0;
~total = 10;

~rout[ 0 ].play;
~rout[ 0 ].stop;
~rout[ 0 ].isPlaying;