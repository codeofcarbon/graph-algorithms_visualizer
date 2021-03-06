# Graph-Algorithms Visualizer

Application for creating and visualizing graph algorithms such as traversals, spanning trees, and the shortest path finding.

## Table of contents
* [Overview](#overview)
* [Screenshots](#screenshots)
* [Technologies](#technologies)
* [How to start](#how-to-start)

## Overview
:jigsaw: application in development :jigsaw:

As for today, the application allows the execution of various types of algorithms, such as traversing a graph or tree in the depth direction (using the ***Depth-first search algorithm***) or the breadth-through movement (using the ***Breadth-first search algorithm***). You can also find the minimum spanning tree using ***Prim's algorithm***, or find the shortest possible path from one node to any other using ***Dijkstra's algorithm***.

All activities are performed easily. Adding nodes is simply done by clicking the mouse in the application window and giving the node an id. 
To add an edge connecting two nodes also it is enough to just click on the source node followed by the target node and enter the edge weight. 
After selecting the appropriate mode, the deletion is also done by clicking on the object of interest (however, remember that deleting a node also means deleting all the edges connected to it!). 
Additional features of the application are, for example:
 - the ability to move nodes (simply drag & drop to improve readability when needed),
 - after executing Dijkstra's algorithm, you can check the path from a root to another node (along with path's weight) - again, just click on the object you are interested in,
 - undo or redo any action you've taken - from moving a node to another location to removing or adding a new node or edge,
 - visual effects - nodes fading out when removed or fading in when those are added. Also, there are some effects of hovering over components
 - tip manager which makes using the application highly intuitive,
 - application also allows you to save graphs you've created and load already saved ones.

#### Future upgrades:
- [ ] *resizing view of the graph*
- [ ] *more algorithms to choose from*
- [ ] *option to choose the method of tree traverse order*
- [ ] *watching the execution of algorithms step by step*
- [ ] *further ease of use - such as automatic assigning of IDs to nodes*

## Screenshots
###### adding nodes & edges
<img alt="adding_nodes" src="src/main/resources/screenshots/adding%20nodes.png" width="400"/> <img alt="adding_edges" src="src/main/resources/screenshots/adding%20edges.png" width="400"/>

###### bfs & dfs algorithm
<img alt="bfs_algorithm" src="src/main/resources/screenshots/bfs%20algorithm.png" width="400"/> <img alt="dfs_algorithm" src="src/main/resources/screenshots/dfs%20algorithm.png" width="400"/>

###### prim's algorithm
<img alt="prim's_algorithm" src="src/main/resources/screenshots/prim%20algorithm.png" width="700"/>

###### dijkstra's algorithm
<img alt="dijkstra's_algorithm_root_C" src="src/main/resources/screenshots/dijkstra%20algorithm%20-%20root%20node%20C.png" width="400"/>
<img alt="dijkstra's_algorithm_root_L" src="src/main/resources/screenshots/dijkstra%20algorithm%20-%20root%20node%20L.png" width="400"/>

###### dijkstra's algorithm - shortest path
<img alt="dijkstra's_shortest_path_C" src="src/main/resources/screenshots/dijkstra%20root%20C%20-%20shortest%20path%20example.png" width="400"/>
<img alt="dijkstra's_shortest_path_L" src="src/main/resources/screenshots/dijkstra%20root%20L%20-%20shortest%20path%20example.png" width="400"/>

###### save & open
<img alt="save_&_open_graph" src="src/main/resources/screenshots/save%20&%20open%20graph.png" width="700"/>

###### removing edges
<img alt="removing_edges" src="src/main/resources/screenshots/removing%20edges.png" width="700"/>

###### removing nodes
<img alt="removing_nodes_and_connected_edges" src="src/main/resources/screenshots/removing%20nodes%20with%20connected%20edges.png" width="700"/>

###### drag & drop
<img alt="drag & drop" src="src/main/resources/screenshots/drag%20&%20drop%20nodes.png" width="700"/>


## Technologies
- Java 17
- Gradle 7.2
- Lombok 1.18
- Java Swing
- Java AWT

## How to start
You can simply download an archive, unzip it inside the directory you want to, and open it in your IDE.

If you want clone the repo:

- run command line in the directory you want to store the app and type the following command:

``git clone https://github.com/codeofcarbon/graph-algorithms_visualizer``

- or start with Project from Version Control in your IDE by providing the url of this repository.
