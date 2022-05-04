# Heuristic-grid-map-search-meet-at-the-border-of-two-graphs
A program to implement and test-run heuristic search algorithms on a grid map or two maps that meet at a border.
The program can run on a map, or on dual maps, the dual map is for my academic research.
My acadmic research deals with finding optimal route for two separate agents each in his own domain to meet on the border.
The problem name is MATB: Meeting At The Border of two separate domains.

Code:  JAVA
Map files must be located in a sibling folder named "Maps" ("../Maps").
Maps were downloaded from known benchmark:  https://www.movingai.com/
Output files, CSV format, are saved in the same folder as the main program.

This program was developed during my research study at BGU university.
The program was developed over 1.5 years during the course of my study.
The primary purpose of the program is to visualize search algorithms (2D),
and help the student understand step-by-step, and develop new algorithms/heuristics.
The secondary purpose of this program is to allow student run these algorithms
in marathon mode for collecting statistical data of the performance (run-time/expansions).

This program consist of additional feature that is part of my research,
  the feature is to allow research on the new define problem called MATB.
The MATB is best described in my, and my mentors: Dr. Dor Azmon, Prof. Ariel Felner.
The paper was published on SOCS-2022 conference.
Paper Title: Meeting at the border of two separate domains.
The additional feature is connecting two maps/graphs with border nodes connecting them.
And my research main purpose was to develop new heuristic that make use of the border nodes
  to improve heuristic search algorithms on these MATB problems.

With this program I provide implementations for some of the known algorithms:
A*, MM, fMM, A*+BMPX, with the heuristic FE and added my own heuristics from my research:
  FBE,  E-FBE.
My research is still ongoing to develop a improvement to these new 2 heuristics.
Users may test them, and develop their own.
Plan to add additional search algorithms:  BS*, NBS.

The program was built with focus on object-oriented methodology.
This allows to run exactly the algorithm class file on 2D and 3D.
Also, able to provide the heuristic object to the algorithm and so,
making different combinations of algorithms & heuristics.

-----------------------------------------------------------
 
There are two options for maps, either selecting single map, or selecting dual maps.
For dual map mode, the selected maps are concatenated along the X-axis, for both 2D/3D.
In dual mode, there will be a wall between the two maps, and it is needed to solder the maps
  which means to connected the maps as one creating open passages through the wall.
There are two types of creating passages, either simple terrain, or border node.
The border node, is for experimenting with specific problem of two maps/graphs which
  have a border between them and the user could use those border nodes to research for new
  algorithms/heuristics.

There are two variables that defines if a map is multiplied and concatenated (for dual mode, it is applied on each side):
  MAP_COMPOSING_ROWS
  MAP_COMPOSING_COLS
  
  

