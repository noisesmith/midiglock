// Thread Arrow Patch ESTRY

Platform.case (
  { \osx }, {
	FreqScope( busNum: 0 );
	~testing = false },
  { \linux }, {
	~debugWin = Window( "signal debug", Rect( -1, -1, 8*64, 8*64 ) ).front;
	~displays = 8.collect{
	  | x |
	  8.collect{
		| y |
		StaticText( ~debugWin, Rect( x*64, y*64, 64, 64 )).string_( 0 ) } };
	~testing = true } );

~fqs = [ 860, 1100, 1700, 1975, 2793, 3729, 4698, 6271 ];

~buffs = 8.collect{ Buffer.alloc( s, 1024, 1 ) };

SynthDef( \io, {
	var result = SoundIn.ar( ( 0..7 ) );
	8.do{
		| n | 
		Out.ar( n, SinOsc.ar( ~fqs[ n ] ) );
		FFT( ~buffs[ n ], result[ n ] ) } } ).send( s );

// where to find each of the inputs in the FFT
~ranges = [
  ( 38 .. 43 ), ( 50 .. 53 ), ( 78 .. 81 ), ( 90 .. 95 ),
  ( 128 .. 133 ), ( 172 .. 175 ), ( 216 .. 221 ), ( 290 .. 293 )
];

~sens = 0.001!8!8;
~strips = Set[ ]!8;

// tare for ~sens value
~tareBuffs = { | conn |
	conn.do{ | row, i |
		row.do{ | col, j |
			~sens[ i ][ j ] = max( ~sens[ i ][ j ], col.abs ) } } };

~reportHooks = [];

~reportHooks = ~reportHooks ++ {
	var strips_raw = 0!8;
	{ | action, x, i, j |
		if( action == \elt, {
			if( x > ~sens[ i ][ j ], {
				strips_raw[ j ] = strips_raw[ j ] + 1 } ) } );
		if( x == \done, {
			strips_raw.do{ | v, i |
				if( v > 4, {
					~strips[ i ].add( i )
				}, {
					~strips[ i ].remove( i ) } ) };
			strips_raw = 0!8 } ) } }.();

~reportHooks = ~reportHooks ++ {
	var max = [0, -1];
	var count = 0;
	{ | action, x, i, j |
		if( action == \raw, {
			if( count == 0, { [ \maximum, max].postln } );
			count = ( count + 1 ) % 60 } );
		if( and(action == \column, count == 0), { x.postln } );
		if( action == \elt, {
			if( and(i != j, x.abs > max[ 0 ]),
				{ max = [ x.abs, i, j ] } ) } ) } }.();

Platform.case (
	{ \osx }, { },
	{ \linux }, {
		~reportHooks = ~reportHooks ++ = {
			| action, x, i, j |
			var color;
			if( action == \elt, {
				color = min( x/100, 1 );
				~displays[ i ][ j ].background = Color( color, color, color);
				~displays[ i ][ j ].stringColor =
				if(color > 0.5, Color.black, Color.white );
				~displays[ i ][ j ].string =
				if( x > ~sens[ i ][ j ], {
					z
				}, {
					0 } ) } ) } } );

~reportHooks[ 0 ].( 5, 0, 1 );
~reportHooks[ 0 ].( \done, nil, nil );

~monitor.isPlaying = Routine{
	var result;
	var getbuffs = { | count, allbuffs |
		if( count < 8,
			{
				~buffs[ count ].getn( 0, 1024, {
					| buff |
					getbuffs.( count + 1, allbuffs ++ [ buff ] ) } ) },
			{
				result = 0!8!8;
				allbuffs.do {
					| buff, i |
					~ranges.do{
						| range, j |
						range.do{ | k |
							result[ i ][ j ] = buff[ k ]+result[ i ][ j ] } } };
				result.do{ | y, i |
					~reportHooks.do{ | f | f.( \column, y ) };
					y.do{ | z, j |
						~reportHooks.do{ | f | f.( \elt, z, i, j ) } } };
				~reportHooks.do{ | f | f.( \raw, result ) } } ) };
	loop { getbuffs.( 0, [] ); 0.1.wait } }.play( AppClock );

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
				~white2used = Set[ newnote ] } );
			newnote;
		},
		\melody, {
			if( ~melodArray.size > 0, {
				~melodIdx = ~melodIdx + 1 % ~melodArray.size;
				~melodArray[ ~melodIdx ];
			}, {
				~noteVals.asArray[ 0 ] } ) } ) };

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
			~getDur.( name ).wait } } };

~white = ~makeMelody.( \white );
~white2 = ~makeMelody.( \white2 );
~melodic = ~makeMelody.( \melody );

if( not( ~testing ), {
	~io = Synth( \io );
	// ~io.free;
}, { // use with preset 3 on bcf2000
	
	~touched = 0!8;
	
	CCResponder( {
		| src, ch, num, val |
		//var strip = ch; // for bcf2000 preset 3
		var strip = ( num - 1 ) % 8; // for my custom nanocontrol config
		var adjusted = val**4 * 0.00000005;
		var getbuffs = { | count, allbuffs |
			if( count < 8,
				{
					~buffs[ count ].getn( 0, 1024, { | buff |
						getbuffs.( count + 1, allbuffs ++ [ buff ] ) } )
				},
				{
					~touched.do{ | tn, i |
						~ranges[ strip ].do{ | elt |
							allbuffs[ i ][ elt ] = tn.rand + adjusted.rand};
						~ranges[ i ].do{ | elt |
							allbuffs[ strip ][ elt ] = tn +
							( ( adjusted.rand + adjusted.rand ) / 2 ) } };
					allbuffs.do{ | buff, i |
						~buffs[ i ].setn( 0, buff ) } } ) };
		~touched[ strip ] = adjusted;
		getbuffs.( 0, [] ) } ) } );

// ~buffs[ 1 ].getn( 0, 1024, { | buff | buff.postln } )
// MIDIResponder.removeAll
