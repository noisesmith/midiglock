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
Pdefn( \durs,     Pseq( ( 18.rand + 1 ).collect{ | x | (x/8)**2+0.1 }, inf ) );
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

