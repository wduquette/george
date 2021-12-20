# Copies the asset files into the resource package tree

set root ../../..
set dest [file join $root src main resources com wjduquette george assets regions overworld]
set dest [file normalize $dest]

puts "Dest: $dest"

proc copyFile {args} {
    global dest
    foreach name $args {
        puts "  Copy $name"
        file copy -force $name $dest
    }
}

copyFile overworld.png overworld.json 


