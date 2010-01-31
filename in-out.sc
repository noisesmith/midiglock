Platform.case (
  { \osx }, {
  },
  { \linux }, {
	  SwingOSC.program = "/usr/local/bin/SwingOSC.jar";
	  //SwingOSC.java = "/usr/lib/jvm/java-1.5.0-sun-1.5.0.17/bin/java";
	  SwingOSC.default.boot;
  }
);

FreqScope( busNum: 0 );
~fqs = [ 860, 1100, 1700, 1975, 2793, 3729, 4698, 6271 ];

// register to receive the analysis messages

~buffs = 8.collect{ Buffer.alloc( s, 1024, 1 ) };

(
SynthDef( \io, {
  var result = SoundIn.ar( ( 0..7 ) );
  8.do{
	| n | 
	Out.ar( n, SinOsc.ar( ~fqs[ n ] ) );
	FFT( ~buffs[ n ], result[ n ] );
  } } ).send( s );

)

~io = Synth( \io );
~io.free;

1024.do{ | n |
	~buffs[ 6 ].get( n,
		{ | x | if( x.abs > 100, { [ x, n ].postln } ) } ) };

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
~monitor.stop;
~monitor.isPlaying;

