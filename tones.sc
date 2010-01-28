// Thread Arrow Patch ESTRY
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
		strips )
};

// ~notevals.size = 35
~noteVals = ( 26 .. 60 ).asSet;

// midi connection
MIDIClient.init( 1, 1 );
// the next line will need customizing
~uid = 1048576;
~midiChan = MIDIOut( 0, ~uid );
// the next two lines are Linux specific, I think
~midiChan.latency_( 0 );
~midiChan.connect( ~uid );

~info = { | x | // print info about current definition of Pdef \x
	Pdefn( x ).source.post;
	", args: ".post;
	Pdefn(x).source.storeArgs.postln
};

Pdefn( \amp,      Pseq( [ 1 ], inf ) );
Pdefn( \degrees,   Pseq( [ 0, [ 2, 4 ], 3, 7, 8, [ 2, 7 ] ], inf ) );
Pdefn( \degrees,   Pseq( [ 0, [ 2, 4 ], 7, [ 2, 7 ] ], inf ) );
Pdefn( \degrees,   Pseq(
	( 10.rand + 1).collect{
		( 4.rand + 2).collect{ ( 0..7 ).choose } }, inf ) );
Pdefn( \degrees,   Pseq( [ 0, [ 2, 4 ], 7, [ 2, 7 ] ], inf ) );
Pdefn( \degrees,   Pseq( [ 1, [ 1, 3, 5 ], 7, [ 3, 5, 7 ] ], inf ) );
Pdefn( \mTransp,  Pseq( ( 0!40 )++( 2!60 ), inf ) );
Pdefn( \cTransp,  Pseq( ( 0!20 )++( 3!10 ), inf ) );

Pdefn( \octaves,  Pseq( ( 1!8 )++( 3!3 )++( 2!9 )++( 4!5 ), inf ) );

Pdefn( \roots,    Pseq( ( 0!100 )++( 3!19 ), inf ) );
Pdefn( \scales,
	Pseq( ( [ [ 0, 2, 4, 5, 7, 9, 11 ]!30,
		[ 3, 4, 5, 6, 7, 8, 9 ]!3 ] ), inf ) );
Pdefn( \scales,
	Pseq( ( [ [ 0, 2, 4, 5, 7, 9, 11 ]!10,
		[ 1, 3, 6, 8, 10 ]!3 ] ), inf ) );
Pdefn( \durs,     Pseq( [ 1, 0.4, 0.1, 0.2, 0.3 ], inf ) );
Pdefn( \durs,
	Pseq( [ 1, 0.4, 0.1, 1.2, 0.3 ]++8.collect{ | x | (x/8)**2+0.4 }, inf ) );
Pdefn( \durs,     Pseq( 18.rand.collect{ | x | (x/8)**2+0.1 }, inf ) );
Pdefn( \tempos,   Pseq( ((1..100)/100+1)/2, inf ) );
// Pdefn( \midinotes, Pseq( 12.collect{ ~noteVals.choose }, inf ) );

Pdefn(\main,
	Pbind( \type, \midi,
		\midiout,    ~midiChan,
		\amp,        Pdefn( \amps    ),
		\degree,     Pdefn( \degrees ),
		\mtranspose, Pdefn( \mTransp ),
		\ctranspose, Pdefn( \cTransp ),
		\octave,     Pdefn( \octaves ),
		\root,       Pdefn( \roots   ),
		\scale,      Pdefn( \scales  ),
		\dur,        Pdefn( \durs    ),
		\tempo,      Pdefn( \tempos  )
	)
).play;

)
~info.(\degrees)
~info.(\durs)

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
}

~shrinkScale = { | scale |
	scale.scramble.[ 1.. ].sort;
}

~normalizeScale = { | scale |
	scale = scale.asSet.asArray.sort;
	scale - scale[0];
}

~shrinkScale.([ 0, 2, 4, 6, 7, 8, 9, 10, 11 ])
~shrinkScale.([ 0 ])

~expandScale.([ 0, 2, 4, 6, 7, 8, 9, 10, 11 ])

~lyseScale.(~deriveScale.( [ 2, 2, 2, 1, 1, 1, 1, 1, 1 ] ));
~deriveScale.(~lyseScale.( [ 0, 2, 4, 6, 7, 8, 9, 10, 11, 0 ] ));

(0..11)++(8..20).asSet.asArray.sort
~test = [];
~test = ~expandScale.(~test);
~test = ~normalizeScale.(~shrinkScale.(~test));

~hmm = [0,1,2,3] ++ []
