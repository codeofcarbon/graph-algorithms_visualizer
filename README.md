# Graph-Algorithms Visualizer
 
Based on the Swing library, Java GUI application for creating and visualizing graph algorithms such 
as traversals, spanning trees, and finding the shortest path.

## Table of contents
* [Overview](#overview)
* [Screenshots](#screenshots)
* [Technologies](#technologies)
* [How to start](#how-to-start)

## Overview
:jigsaw: application in development :jigsaw:

The application now allows the execution of various types of algorithms, such as traversing a graph or tree in the depth direction (using the ***Depth-first search algorithm***) or the breadth-through movement (using the ***Breadth-first search algorithm***). You can also find the minimum spanning tree using ***Prim's algorithm***, or find the shortest possible path from one node to any other using ***Dijkstra's algorithm***.
The application also allows you to save and load already saved graphs and trees. 

All activities are performed easily. Adding nodes is simply done by clicking the mouse in the application window and giving the node an ID. To add an edge connecting two nodes also it is enough to just click on the source node followed by the target node and enter the edge weight. 
After selecting the appropriate mode, the deletion is also done by clicking on the object of interest (however, remember that deleting a node also means deleting all the edges connected to it!). Additional features of the application are, for example, the ability to move nodes (simply drag & drop to improve readability when needed) or, after executing Dijkstra's algorithm, you can also check the path from a root to another node (along with path's weight) - again, just click on the object you are interested in.

#### Future upgrades:
- [ ] *more algorithms to choose from*
- [ ] *option to choose the method of tree traverse order*
- [ ] *resizing view of the graph*
- [ ] *undo and redo options*
- [ ] *watching the execution of algorithms step by step*
- [ ] *further ease of use - such as automatic assigning of IDs to nodes*

## Screenshots
###### screenshot1
<!-- ![screenshot1](screenshots/screenshot1.png)  -->

###### screenshot2
<!-- ![screenshot2](screenshots/screenshot2.png) -->

###### screenshot3
<!-- ![screenshot3](screenshots/screenshot3.png) -->

## Technologies
- Java 11
- Java Swing
- Java AWT
- Gradle 7.1

## How to start
You can simply download an archive, unzip it inside the directory you want to, and open it in your IDE.

If you want clone the repo:

- run command line in the directory you want to store the app and type the following command:

``git clone https://github.com/codeofcarbon/graph-algorithms_visualizer``

- or start with Project from Version Control in your IDE by providing the url of this repository.
