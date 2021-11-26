# george/assets/ Folder

This folder contains the source files for the game's assets:

- In `tilesets/`
  - PyxelEdit .pyxel files for the standard tile sets
- In `regions/<name>`
  - A Tiled .tmx map file for the region
  - A PyxelEdit .pyxel file for the map's tile set.


There is a parallel structure under the application's resources folder:

    src/main/resources/com/wjduquette/george/assets/...

On edit:

- .pyxel files are to be exported as .png images
- .tmx files are to be exported as .json files
- The exported files are to be copied to the matching resources folder.

