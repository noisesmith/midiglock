Platform.case (
  { \osx }, {
  },
  { \linux }, {
	  SwingOSC.program = "/usr/local/bin/SwingOSC.jar";
	  //SwingOSC.java = "/usr/lib/jvm/java-1.5.0-sun-1.5.0.17/bin/java";
	  SwingOSC.default.boot;
  } );

// FreqScope( busNum: 0 );
~fqs = [ 860, 1100, 1700, 1975, 2793, 3729, 4698, 6271 ];

~buffs = 8.collect{ Buffer.alloc( s, 1024, 1 ) };

SynthDef( \io, {
  var result = SoundIn.ar( ( 0..7 ) );
  8.do{
	| n | 
	Out.ar( n, SinOsc.ar( ~fqs[ n ] ) );
	FFT( ~buffs[ n ], result[ n ] );
  } } ).send( s );

~io = Synth( \io );
// ~io.free;

// where to find each of the inputs in the FFT
~ranges = [
  ( 38 .. 43 ), ( 50 .. 53 ), ( 78 .. 81 ), ( 90 .. 95 ),
  ( 128 .. 133 ), ( 172 .. 175 ), ( 216 .. 221 ), ( 290 .. 293 )
];

~debugWin = Window( "signal input debug", Rect( -1, -1, 8*64, 8*64 ) ).front;

~displays = 8.collect{
  | x |
  8.collect{
	| y |
	JSCStaticText( ~debugWin, Rect( x*64, y*64, 64, 64 )).string_( 0 ) } };

~sens = 0.001;

~report = {
  | x |
  x.do{ | y, i |
	y.do{ | z, j |
	  ~displays[ i ][ j ].string = z } } };

~monitor = Routine{
  var results  = 0!8!8;		
  var allCollected = false!8;
  loop {
	8.do{ | chan |
	  ~ranges.do{
		| range, rnum |
		var collected = false!range.size;
		var total = 0;
		range.do{ | i, n |
		  ~buffs[ chan ].get( i,
			{ | result |
			  total = total + result;
			  collected[ n ] = true;
			  if( collected.select{ | x | not( x ) }.size == 0,
				{
				  results[ chan ][ rnum ] = total;
				  allCollected[ chan ] = true;
				  if( allCollected.select{ | x | not( x ) }.size == 0,
					{
					  ~report.( results );
					  results = 0!8!8;		
					  allCollected = false!8;
					} ) } ) } ) } } };
	0.5.wait } };

~monitor.play;
// ~monitor.stop;
// ~monitor.isPlaying;

// Thread Arrow Patch ESTRY
~strips = 8.collect( Set[ ] );

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
		strips )
};

// ~notevals.size = 35
~noteArray = ( 26 .. 60 ); 
~noteVals = ~noteArray.asSet;

// midi connection
Platform.case (
  { \osx }, {
	MIDIClient.init;
	~uid = MIDIClient.destinations[ 0 ].uid;
	~midiChan = MIDIOut( 0, ~uid );
	~midiChan.connect( ~uid );
  },
  { \linux }, {
	MIDIClient.init( 1, 1 );
	~uid = 1048576;
	~midiChan = MIDIOut( 0, ~uid );
	// the next two lines are Linux specific, I think
	~midiChan.latency_( 0 );
	~midiChan.connect( ~uid );
  }
);

~deriveScale = { | steps |
	var total = 0;
	steps.collect{ | step |
		total = total + step;
		total % 12
	}.asSet.asArray.sort;
};

~lyseScale = { | scale |
	var last = scale[ 0 ];
	var result;
	scale.[1..].collect{ | degree |
		result = (degree - last) % 12;
		last = degree;
		result
	}
};

~expandScale = { | scale |
	var missing = ( 0..11 ).select{ | x | scale.find ( [ x ] ).isNil };
	( scale ++ missing.choose ).sort;
};

~shrinkScale = { | scale |
	scale.scramble.[ 1.. ].sort;
};

~normalizeScale = { | scale |
	scale = scale.asSet.asArray.sort;
	scale - scale[0];
};

// ~shrinkScale.([ 0, 2, 4, 6, 7, 8, 9, 10, 11 ])
// ~shrinkScale.([ 0 ])

// ~expandScale.([ 0, 2, 4, 6, 7, 8, 9, 10, 11 ])

// ~lyseScale.(~deriveScale.( [ 2, 2, 2, 1, 1, 1, 1, 1, 1 ] ));
// ~deriveScale.(~lyseScale.( [ 0, 2, 4, 6, 7, 8, 9, 10, 11, 0 ] ));

~white2used = Set();

~melodArray = [ ~noteVals.choose ];
~melodIdx = 0;

~scale = [ 0, 2, 4, 5, 7, 9, 11 ]; // major scale

~melodyExpand = {
	var newTone = ~scale.choose + 20 + (4.rand * 12);
	if ( newTone < 26,
		{ newTone = newTone + 12 } );
	if ( newTone > 60,
		{ newTone = newTone - 12 } );
	~melodArray = ~melodArray ++ [ newTone ];
};

~melodyPrune = {
	~melodArray = ~melodArray[ 1.. ]
};

~getNote = { | name |
	var diff;
	var newnote;
	switch ( name,
		\white, { ~noteVals.choose },
		\white2, {
			diff = ~white2used.symmetricDifference( ~noteVals.asSet );
			if( diff.size > 0, {
				newnote = diff.choose;
				~white2used.add( newnote );
			}, {
				newnote = ~noteVals.choose;
				~white2used = Set[ newnote ];
			}
			);
			newnote;
		},
		\melody, {
			if( ~melodArray.size > 0, {
				~melodIdx = ~melodIdx + 1 % ~melodArray.size;
				~melodArray[ ~melodIdx ];
			}, {
				~noteVals.asArray[ 0 ];
			} ) }
	)
};

~getVelocity = { | name |
	27.rand + 100
};

~getDur = { | name |
	switch ( name,
		\white,	{ 0.02 + 0.2.rand },
		\white2, { 0.2 + 0.02.rand },
		\melody, { [ 0.4, 0.2, 0.3, 0.1 ] @@ ~melodIdx }
	)
};
	

~makeMelody = { | name |
	Routine{
		loop {
			~midiChan.noteOn( 0,
				~getNote.( name ),
				~getVelocity.( name ) );
			~getDur.( name ).wait;
		} } };

~white = ~makeMelody.( \white );
~white2 = ~makeMelody.( \white2 );
~melodic = ~makeMelody.( \melody );

~white.play;
~white2.play;
~melodic.play;

  ~white.isPlaying;
 ~white2.isPlaying;
~melodic.isPlaying;

~white.reset;
~white2.reset;
~melodic.reset;
~white.isPlaying;


~white.stop;
~white2.stop;
~melodic.stop;

~melodyPrune.();
~melodyExpand.();

~scale = ~shrinkScale.( ~scale );
~scale = ~expandScale.( ~scale );