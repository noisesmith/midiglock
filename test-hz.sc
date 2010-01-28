// the goal: none of these are less than a halfstep apart from one another,
// and none have harmonics that are less than a halfstep from any of the others

// I started with a semi-naive algorithmic approach, then
// ~fqs = 8.collect{ | x | 500*x+4300 };
// ~fqs = 8.collect{ | x | 120*x+860 }; // switched to five octaves lower
// and finally iteratively moved frequencies to maximize distance, and preserve
// distance from all harmonics

~fqs = [ 860, 1100, 1700, 1975, 2793, 3729, 4698, 6271 ];

~diffs = ~fqs.collect{ | f | ~fqs.collect{ | f2 | f2.cpsmidi - f.cpsmidi } };

// none of these are less than a halfstep apart from one another,

~diffs.collect{ | diffs | diffs.select{ | diff | abs(diff) < 1 } }
== [ [ 0 ], [ 0 ], [ 0 ], [ 0 ], [ 0 ], [ 0 ], [ 0 ], [ 0 ] ];

// none have harmonics that are less than a halfstep from any of the others
~showfqs = 0!200;

~fqs.collect{ | f |
	var result = [ f ];
	while ( { result[ 0 ] < 22050 },
		{
			result = [ result[ 0 ] + f ] ++ result;
		}
	);
	result.cpsmidi.round.asSet.asArray.sort;
}.do{ | nums | nums.do{ | num | ~showfqs[ num ] = ~showfqs[ num ] + 1 } };

~fqs.do{ | num |
	~showfqs[ num.cpsmidi.round ] = ~showfqs[ num.cpsmidi.round ] + 99
};

~showfqs.select{ | x | x > 50 }.size == 8;

// ~showfqs.plot( discrete: true );
