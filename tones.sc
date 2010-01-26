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
Pdefn( \degree,   Pseq( [ 0, [ 2, 4 ], 3, 7, 8, [ 2, 7 ] ], inf ) );
Pdefn( \degree,   Pseq( [ 0, [ 2, 4 ], 7, [ 2, 7 ] ], inf ) );
Pdefn( \degree,   Pseq(
	10.rand.collect{
		8.rand.collect{ ( 1..8 ).choose } }, inf ) );
Pdefn( \degree,   Pseq( [ 0, [ 2, 4 ], 7, [ 2, 7 ] ], inf ) );
Pdefn( \degree,   Pseq( [ 0, [ 2, 4 ], 7, [ 2, 7 ] ], inf ) );
Pdefn( \mTransp,  Pseq( ( 0!40 )++( 2!60 ), inf ) );
Pdefn( \cTransp,  Pseq( ( 0!20 )++( 3!10 ), inf ) );

Pdefn( \octaves,  Pseq( ( 1!8 )++( 3!3 )++( 2!9 )++( 4!5 ), inf ) );

Pdefn( \roots,    Pseq( ( 0!100 )++( 3!19 ), inf ) );
Pdefn( \scales,
	Pseq( ( [ [ 0, 2, 4, 5, 7, 9, 11 ]!30,
		[ 3, 4, 5, 6, 7, 8, 9 ]!3 ] ), inf ) );
Pdefn( \durs,     Pseq( [ 1, 0.4, 0.1, 0.2, 0.3 ], inf ) );
Pdefn( \durs,
	Pseq( [ 1, 0.4, 0.1, 1.2, 0.3 ]++8.collect{ | x | (x/8)**2+0.4 }, inf ) );
Pdefn( \durs,     Pseq( [ 1, 0.4, 0.1, 0.2, 0.3 ], inf ) );
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

Pdefn(\main).debug

Tdef( \newnotes,
	{
		var notes = [ 26 ];
		var durs;
		loop( {
			if( 5.rand > 3,
				{ notes = ( 6.rand + 1 ).collect{ ~noteVals.choose } } );
			durs   = (notes.size * ( 5.rand + 1 ) ).collect( { 1.0.rand**3 } );
			Pdefn( \notes,
				Pseq( notes, inf )
			);
			Pdefn( \durs,
				Pseq( durs, inf )
			);
			"new notes: ".postln;
			("notes: " ++ notes).postln;
			("durs: " ++ durs).postln;
			( 3.0.rand * durs.size ).wait;
		} )
	}
).play;

)
~info.(\degree)
~info.(\durs)