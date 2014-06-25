#!/usr/bin/sh -- # This comment tells perl not to loop!
eval 'exec /usr/bin/perl -S $0 ${1+"$@"}'
    if 0;

use English;
$OUTPUT_AUTOFLUSH = 1;          # Don't buffer stdout

## Habash - Fri Nov 15 19:51:41 EST 2002
 
#Hardcoded parameters:
$catvar[0] = "catvar21.signed";
#$catvar[1] = "catvar2.prep.signed";

#Resource Name
$sign[0] = "WordNet 1.6";  #+1
$sign[1] = "Brown Corpus"; #+2 
$sign[2] = "Englex Dictionary"; #+4
$sign[3] = "UMD-NOMLEX pair"; #+8, Gremio
$sign[4] = "LCS Lexicon"; #+16
$sign[5] = "UMD-LDOCE pair"; #+32, R.Green
$sign[6] = "UMD-Englex pair"; #+64 N.Habash
$sign[7] = "UMD-Habash pair"; #+128 

#Resource Short Name
$cosign[0] = "WN";  #+1
$cosign[1] = "BC"; #+2 
$cosign[2] = "ED"; #+4
$cosign[3] = "NX"; #+8, Gremio
$cosign[4] = "LL"; #+16
$cosign[5] = "LD"; #+32, R.Green
$cosign[6] = "EX"; #+64 N.Habash
$cosign[7] = "UH"; #+128 

# argument handling
$argc  = @ARGV;
die "Usage: $0 { <word>{_<pos>}? <length>?(+||-)? || <signature> } \n%% Allowed regexp include ^ (start), _ (end) . (any) * (zero+)\n%% <length>+ means clusters of size <length> or more\n%% <length>- means clusters of size <length> or less\n"
    if (($argc > 2)||($argc < 1));
$query = $ARGV[0];
$length = $ARGV[1];

$ormore = ($length =~ /\+/);
$orless = ($length =~ /\-/);
$length =~ s/\D//g;


#initialize porter stemmer code
initialise();


if ($query =~ /^[0-9]+$/) {
    #signature
    for ($i=0; $i<@sign; $i++){
	$s = 2 ** $i;
       	$present = $query & $s;
	if ($present){print "$s $sign[$i] ($cosign[$i])\n";}
    }
}
else {
    $total=0;
    for ($i=0; $i<@catvar; $i++){
	print "-------------------------------------\n";
	print "CATVAR File: $catvar[$i] ... "; 
	$count=0;

	if (open(IN, $catvar[$i])) {
	    print "\n\n";
	    
	    while ($line = <IN>) {
		chomp($line);
		$line =~ s/\#/\n/g;
		if (($line ne "")&&($line !~ /^\#/)&&($line =~ /$query/m)){
		#if (($line ne "")&&($line !~ /^\#/)&&($line =~ /$query/)){
		    @l = split('\n',$line);
		    if ((not $length)||($ormore && ($length <= @l))||($orless && ($length >= @l))||($length == @l)){ 
			for ($j=0; $j<@l; $j++){
			    $l[$j] =~ s/\%/\_/g;
			    @ll=split('\_',$l[$j]);
			    $cosign = &coded_signature($ll[2]);
			    $porter = &stem($ll[0]);
			    print "$ll[0]\t$ll[1]\t<$ll[2]>\t($cosign $porter)\n";
			    
			    #$line = "$line\#";
			    #$line =~ s/\%(\d+)\#/\t<$1> &coded\_signature\n/g;
			    #$line =~ s/\_/\t/g;
			    #print "$line";
			}
			print "-------------------------------------\n";
			$count++; $total++;
		    }
		}
	    }
	    print "Subtotal = $count clusters found\n";
	    close(IN);
	}
	else {
	    print "UNAVAILABLE!\n\n";
	    
	}
    }
    print "Total = $total clusters found\n";
}

sub coded_signature {
    my($query)=@_;
    my($i,$s,$cosign);
    
    for ($i=0; $i<@sign; $i++){
	$s = 2 ** $i;
	if ($query & $s){$cosign="$cosign $cosign[$i]";}
    }
    
    $cosign =~ s/^\s//;
    return ($cosign);
}



##------------------------------------------------------------
## code from porter stemmer!
## http://www.tartarus.org/~martin/PorterStemmer/

local %step2list;
local %step3list;
local ($c, $v, $C, $V, $mgr0, $meq1, $mgr1, $_v);


sub stem
{  my ($stem, $suffix, $firstch);
   my $w = shift;
   if (length($w) < 3) { return $w; } # length at least 3
   # now map initial y to Y so that the patterns never treat it as vowel:
   $w =~ /^./; $firstch = $&;
   if ($firstch =~ /^y/) { $w = ucfirst $w; }

   # Step 1a
   if ($w =~ /(ss|i)es$/) { $w=$`.$1; }
   elsif ($w =~ /([^s])s$/) { $w=$`.$1; }
   # Step 1b
   if ($w =~ /eed$/) { if ($` =~ /$mgr0/o) { chop($w); } }
   elsif ($w =~ /(ed|ing)$/)
   {  $stem = $`;
      if ($stem =~ /$_v/o)
      {  $w = $stem;
         if ($w =~ /(at|bl|iz)$/) { $w .= "e"; }
         elsif ($w =~ /([^aeiouylsz])\1$/) { chop($w); }
         elsif ($w =~ /^${C}${v}[^aeiouwxy]$/o) { $w .= "e"; }
      }
   }
   # Step 1c
   if ($w =~ /y$/) { $stem = $`; if ($stem =~ /$_v/o) { $w = $stem."i"; } }

   # Step 2
   if ($w =~ /(ational|tional|enci|anci|izer|bli|alli|entli|eli|ousli|ization|ation|ator|alism|iveness|fulness|ousness|aliti|iviti|biliti|logi)$/)
   { $stem = $`; $suffix = $1;
     if ($stem =~ /$mgr0/o) { $w = $stem . $step2list{$suffix}; }
   }

   # Step 3

   if ($w =~ /(icate|ative|alize|iciti|ical|ful|ness)$/)
   { $stem = $`; $suffix = $1;
     if ($stem =~ /$mgr0/o) { $w = $stem . $step3list{$suffix}; }
   }

   # Step 4

   if ($w =~ /(al|ance|ence|er|ic|able|ible|ant|ement|ment|ent|ou|ism|ate|iti|ous|ive|ize)$/)
   { $stem = $`; if ($stem =~ /$mgr1/o) { $w = $stem; } }
   elsif ($w =~ /(s|t)(ion)$/)
   { $stem = $` . $1; if ($stem =~ /$mgr1/o) { $w = $stem; } }


   #  Step 5

   if ($w =~ /e$/)
   { $stem = $`;
     if ($stem =~ /$mgr1/o or
         ($stem =~ /$meq1/o and not $stem =~ /^${C}${v}[^aeiouwxy]$/o))
        { $w = $stem; }
   }
   if ($w =~ /ll$/ and $w =~ /$mgr1/o) { chop($w); }

   # and turn initial Y back to y
   if ($firstch =~ /^y/) { $w = lcfirst $w; }
   return $w;
}

sub initialise {

   %step2list =
   ( 'ational'=>'ate', 'tional'=>'tion', 'enci'=>'ence', 'anci'=>'ance', 'izer'=>'ize', 'bli'=>'ble',
     'alli'=>'al', 'entli'=>'ent', 'eli'=>'e', 'ousli'=>'ous', 'ization'=>'ize', 'ation'=>'ate',
     'ator'=>'ate', 'alism'=>'al', 'iveness'=>'ive', 'fulness'=>'ful', 'ousness'=>'ous', 'aliti'=>'al',
     'iviti'=>'ive', 'biliti'=>'ble', 'logi'=>'log');

   %step3list =
   ('icate'=>'ic', 'ative'=>'', 'alize'=>'al', 'iciti'=>'ic', 'ical'=>'ic', 'ful'=>'', 'ness'=>'');


   $c =    "[^aeiou]";          # consonant
   $v =    "[aeiouy]";          # vowel
   $C =    "${c}[^aeiouy]*";    # consonant sequence
   $V =    "${v}[aeiou]*";      # vowel sequence

   $mgr0 = "^(${C})?${V}${C}";               # [C]VC... is m>0
   $meq1 = "^(${C})?${V}${C}(${V})?" . '$';  # [C]VC[V] is m=1
   $mgr1 = "^(${C})?${V}${C}${V}${C}";       # [C]VCVC... is m>1
   $_v   = "^(${C})?${v}";                   # vowel in stem

}

# that's the definition. Run initialise() to set things up, then stem($word) to stem $word, as here:

