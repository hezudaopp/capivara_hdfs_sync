#!/usr/bin/perl

#
# capivara - Java File Synchronization
#
# Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
# $Id: bundle_checker.pl,v 1.5 2004/11/21 09:05:52 hunold Exp $
#

# A tool that compares all property files in one directory
# to a default property file.
# If a key from the default file was not found the program
# reports a WARNING.

#
# example: perl ../bundle_checker.pl . MessageBundle
#

use File::Find;
use File::Basename;

die "usage bundle_checker.pl <basedir> <basebundle>" unless $#ARGV == 1;

my $basedir  = $ARGV[0];
my $basebundle = $ARGV[1];
my $bundle_ext = "properties";
my $cur_bundle_lang = "";
my %key_hash = ();


open(FILE, "<$basedir/$basebundle.$bundle_ext")
  or die "Could not open $basedir/$basebundle.$bundle_ext";
while( $line = <FILE> ) {
  next unless not iscomment($line);

  if( $line =~ /(.*)\=(.*)/ ) {
    my $key = $1;
    $key = trim_key($key);
#    printf("key : |%s|\n", $key);
    $key_hash{$key} = 1;
  }
}
close(FILE);

my @files;
find( sub{ push @files, $File::Find::name }, "$basedir");

foreach my $file (@files) {
  if( $file =~ /${basedir}\/${basebundle}.*${bundle_ext}$/ ) {
    $bname = basename($file);
    next unless "$bname" ne "$basebundle.$bundle_ext";
    printf("Checking file : %s\n", basename($file));
    if( $bname =~ /$basebundle(.*)\.$bundle_ext/ ) {
      $cur_bundle_lang = $1;
    }
    check_prop_file($file);
  }
}

sub check_prop_file {
  my $file = $_[0];
  my %ckeys = ();

  open(FILE, "<$file") or die $!;
  while( my $line = <FILE> ) {
    next unless not iscomment($line);
    if( $line =~ /(.*)\=(.*)/ ) {
      my $key = $1;
      $key = trim_key($key);
      $ckeys{$key} = 1;
    }
  }
  close(FILE);

  my @orig_keys = sort(keys(%key_hash));
  foreach my $okey (@orig_keys) {
    if( ! $ckeys{$okey} ) {
      printf("(%s) MISSING key: %s\n", $cur_bundle_lang, $okey);
    }
  }
}

sub trim_key {
 my $tkey = $_[0];
 $tkey =~ s/^\s+//;
 $tkey =~ s/\s+$//;
 return $tkey;
}

sub iscomment {
  my $line = $_[0];
  if( $line =~ /^\#/ ) {
    return 1;
  }
  return 0;
}
