#!/usr/bin/sh -- # This comment tells perl not to loop!
eval 'exec /usr/bin/perl -S $0 ${1+"$@"}'
    if 0;

use English;
$OUTPUT_AUTOFLUSH = 1;          # Don't buffer stdout

## Habash - Fri Nov 15 16:57:29 EST 2002

# given a catvar cluster file, this script returns:
# 1. number of clusters
# 2. number of words and number of unique words (to debug for strays)
# 3. distribution of cluster size
# 4. distribution of POS over all words



$clusterCount=0;
$wordCount=0;
@clusterSize=();
%POS=();
%word=();
$uniqueWordCount=0;

print STDERR "READING CLUSTERS ... \n";
while ($line = <STDIN>) {
    $count++;
    &print_count;
    chomp($line);

    if ($line ne ""){
	$clusterCount++;
	@l= split('\#',$line);
	$clusterSize[@l]++;


	for ($i=0; $i<@l; $i++) {
	    @ll = split('\_',$l[$i]);

	    
	    $POS{$ll[1]}++;
	    #if ($POS{$ll[1]} == 1) {print "POS: $line >>>$ll[1]<<<\n";}
	    $wordCount++;
	    $word{$ll[0]}++;
	    $wordpos{$l[$i]}++;
	}
	 
    }
}

$uniqueWordCount = keys(%word);
$uniqueWordPOSCount = keys(%wordpos);


print "\n--------------------------------------\n";
print "Cluster Count = $clusterCount\n";
print "Word Count = $wordCount ($uniqueWordPOSCount unique)\n";
print "Cluster Size Distribution:\n";
for ($i=0; $i<@clusterSize; $i++) {if ($clusterSize[$i] > 0) {print "\t$i\t$clusterSize[$i]\n";}}
print "Part of Speech Distribution:\n";
foreach $p (sort (keys (%POS))) {print "\t$p\t$POS{$p} \n";}
print "--------------------------------------\n";





sub print_count {
    print STDERR "[$count]" if (($count % 1000) == 0);
    print STDERR "\n"       if (($count % 6000) == 0);
}
