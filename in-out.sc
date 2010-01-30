~fqs = [ 860, 1100, 1700, 1975, 2793, 3729, 4698, 6271 ];

// register to receive the analysis messages

OSCresponder(s.addr,'/tr',{ arg time,responder,msg;
  [time,responder,msg].postln;
}
).add;

SynthDef( \io,
  {
	var result;
	Out.ar( ( 0 .. 7 ), SinOsc.ar( ~fqs ));
	result = BRF.ar( SoundIn.ar( ( 0 .. 7 )), ~fqs );
	8.do{ | x |
	  ~fqs.select{ | y, z | z != x }.do {
		|  fq, idx |
		SendTrig.kr(
		  Amplitude.kr(
			BPF.ar( result[ idx ], fq ) ) - 0.5, ( x<<3 ) + idx );
	  } } } ).send(s);

~io = Synth( \io );

