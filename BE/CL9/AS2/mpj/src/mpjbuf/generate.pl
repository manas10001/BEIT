#!/usr/bin/perl

# File         : generate.pl
# Author       : Bryan Carpenter
# Created      : Thu Jul 15 10:44:32 BST 2004
# Revision     : $Revision: 1.4 $
# Updated      : $Date: 2005/07/29 14:03:10 $

@targets = ('Buffer.java', 'RawBuffer.java', 'NIOBuffer.java') ;

for($itarg = 0 ; $itarg < @targets ; $itarg++) {

    $target   = $targets [$itarg] ;
    $template = $targets [$itarg] . '.in' ;

    open(TEMPLATE, $template) or die "Can't open $template: $!\n" ;

    $script = "\n" . '$target = "' . $target . '"' . " ;\n" ;

    $script .= <<'EOF' ;

open(TARGET, ">$target")  or die "Can't create $target: $!\n" ;

print TARGET "/* This file generated automatically ",
             "from template $template. */\n" ;

EOF


    while($line = <TEMPLATE>) {
      if($line =~ m/^<<<<<<</) {
        $script .= "print TARGET <<EOF ;\n" ;
      }
      elsif($line =~ m/^>>>>>>>/) {
        $script .= "EOF\n" ;
      }
      else {
        $script .= $line ;
      }
    }


    $script .= <<'EOF' ;

close TARGET ;
close TEMPLATE ;

EOF


    #print $script ;  # debug
    eval $script ;

    # Failure to create target file probably indicates the generated script
    # compiled with errors, due to errors in perl parts of the template.

    # To trace, uncomment the "debug" line above, redirect standard output
    # to a file, and manually feed that file to the `perl' command.

}

