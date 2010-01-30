SwingOSC.program = "/usr/local/bin/SwingOSC.jar";
SwingOSC.default.boot;

8.do{ | n | FreqScope( busNum: n+8 ) };
~fqs = [ 860, 1100, 1700, 1975, 2793, 3729, 4698, 6271 ];

// register to receive the analysis messages

OSCresponder( s.addr, '/tr', { arg time,responder,msg;
  [ time, msg[ 2 ] >> 3, msg[ 2 ] & 8, msg[ 3 ] ].postln;
} ).add;

(
SynthDef( \io,
  { var result;
	var anal;
	Out.ar( ( 0 .. 7 ), SinOsc.ar( ~fqs ) );
	result = BRF.ar( SoundIn.ar( ( 0 .. 7 ) ), ~fqs );
	8.do{ | x |
	  ~fqs.select{ | y, z | z != x }.do {
		|  fq, idx |
		anal = BPF.ar( result[ idx ], fq ).abs;
		Out.ar( x*8 + 8 + idx, anal );
		SendTrig.kr(
		  Amplitude.kr(anal)
		  + Impulse.kr( 5, 0, 1 )
		  - 2.12, idx, x );
	  } } } ).send( s );
)
~io = Synth( \io );
