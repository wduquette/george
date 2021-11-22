proc splitTiles {infile} {
    set dest ../src/main/resources/com/wjduquette/george/tiles
    puts "Splitting: $infile"
    set basename [file rootname $infile]
    exec magick convert $infile -crop 40x40 +adjoin \
        [file join $dest ${basename}_%03d.png]
}


foreach filename $argv {
    splitTiles $filename
}


