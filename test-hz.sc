// the goal: none of these are less than a halfstep apart from one another,
// and none have harmonics that are less than a halfstep from any of the others

// I started with a semi-naive algorithmic approach, then
// ~fqs = 8.collect{ | x | 500*x+4300 };
// ~fqs = 8.collect{ | x | 120*x+860 }; // switched to five octaves lower
// and finally iteratively moved frequencies to maximize distance, and preserve
// distance from all harmonics



// testing, first should succeed, second fail
~seedFqs = [ 860, 1100, 1700, 1975, 2793, 3729, 4698, 6271 ];
~badSeedFqs = [ 800, 1100, 1600, 1975, 2793, 3729, 4698, 6271 ];

~testFqs.( *~analFqs.( ~seedFqs ) ) &&
not( ~testFqs.( *~analFqs.( ~badSeedFqs ) ) );


~analFqs = { | fqs |
	var diffs;
	var allMid = 0!200;
	var mids = fqs.collect{ | f |
		var result = [ f ];
		while ( { result[ 0 ] < 22050 },
			{
				result = [ result[ 0 ] + f ] ++ result;
			}
		);
		result.cpsmidi.round.asSet.asArray.sort;
	};
	mids.do{ | nums |
		nums.do{ | num |
			allMid[ num ] = allMid[ num ] + 1 }
	};
	fqs.do{ | num |
		allMid[ num.cpsmidi.round ] = allMid[ num.cpsmidi.round ] + 99
	};
	[ fqs, allMid ];
}

~testFqs = { | fqs, allMid |
	var diffs = fqs.collect{ | f |
		fqs.collect{ | f2 |
			f2.cpsmidi - f.cpsmidi }
	};
	( diffs.collect{ | diffs | diffs.select{ | diff | abs(diff) < 1 } }
		== [ [ 0 ], [ 0 ], [ 0 ], [ 0 ], [ 0 ], [ 0 ], [ 0 ], [ 0 ] ] ) &&
	allMid.select{ | x | x > 100 }.size == 0;
}

// ~showfqs.plot( discrete: true );
