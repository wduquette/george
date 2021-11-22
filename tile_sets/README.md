# README.md

This folder contains tile set definitions, in the form of PyxelEdit files and
PNG files exported from them.  Each file contains 40x40 images, tiled together.

To build the individual tile image files, use this command line:

$ tclsh split.tcl *.png

Because the PNG images have blank spots, some of the resulting PNG files will
be empty.
