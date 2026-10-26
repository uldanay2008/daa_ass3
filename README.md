# Assignment 3. Maze Escape

Student ID: 254317
Name: Uldanay Bazarbay
Group: SE-2512
Maze size (rows x columns): TODO

## Program output

The 13 lines your program prints on your data (`java -cp out Main`):

    Broken: 71
    Kept: 56
    Last broken wall: 88
    MST cost: 20495
    Heaviest broken wall: 727
    Path length: 17
    DFS visited: 50
    Dead ends: 22
    Zones: 8
    Largest zone: 15
    Is DAG: 0
    Flow stages: 6
    Fastest route cost: 3

My maze:

![maze](output/maze.png)

## Questions

Answer in your own words, two or three sentences each.

Mission 0. What do find and union do, and what makes them fast in your code?

The find operation returns the root of the group containing an element. The union operation joins two different groups and returns true, or returns false if they are already connected. Union by size keeps the trees short, and path compression makes future searches faster.

Mission 1. Why is Broken always R*C - 1, and Kept always (R-1)*(C-1)?

The algorithm connects all R*C cells without creating cycles, so the maze is a tree with R*C - 1 passages. The original grid has R*(C-1) + C*(R-1) inner walls; subtracting the broken walls gives (R-1)*(C-1) remaining walls. My maze has 72 cells, so 71 walls are broken and 56 walls are kept.

Mission 2. My MST cost from Kruskal and from Prim, and why they are equal:

Kruskal gives an MST cost of 20495 on my personal data. Both Kruskal and Prim find a minimum spanning tree of the same weighted graph, so a correct implementation of each must return the same total cost. All wall costs are different, so the minimum spanning tree is unique.

Mission 3. Why is there exactly one path between two cells? How did you avoid a stack overflow on a huge maze?

The maze is connected and has no cycles, so there is exactly one simple path between any two cells. I use iterative DFS with an explicit array-based stack instead of recursion to avoid call-stack overflow. Neighbours are checked in the required order: up, right, down, left.

Mission 4. Why does the second pass of Kosaraju go on the reversed graph? Why do the slides have no cycle exactly when Zones equals the number of cells?

Reversing the graph preserves its strongly connected components. Processing cells in decreasing finishing order on the reversed graph allows each DFS to identify one component without entering another unassigned component. Since the input has no self-loops, the graph has no directed cycles exactly when every component contains one cell.

Mission 5. Why is it enough to go through the zones in the order of their numbers? What does this pass cost?

Kosaraju assigns zone numbers in topological order, so every edge between different zones goes from a smaller number to a larger number. This allows dynamic programming to calculate the longest chain and cheapest route in one forward pass without a priority queue. Building and processing the zone graph takes O(n + m) time; parallel slides are retained, and checking all of them selects the cheapest option.

## AI use

I used ChatGPT to explain the assignment and the algorithms, provide code suggestions and explain time and space complexity.
ChatGPT also helped troubleshoot Java commands.

## Timing (Mission 7)

Average running time in milliseconds over 5 runs, measured using Bench.
| maze | Kruskal | Prim |
|---|---------|------|
| 100 x 100 | 17      | 12   |
| 300 x 300 | 116     | 103  |
| 1000 x 1000 | 1466    | 2868 |

Both algorithms take more time as the maze grows because they process more cells and walls. Prim was faster on the two smaller mazes, while Kruskal was faster on the largest maze. These measurements depend on the implementations, JVM behaviour, and the computer used.

## Complexity

n is the number of cells, and m is the number of walls or slides. Space includes additional graph representations and stored results.

| algorithm | time | space | assumptions |
|---|---|---|---|
| DSU find and union | O(alpha(n)) amortized per operation | O(n) for the DSU; O(1) per call | Union by size and iterative path compression; initialization takes O(n) |
| Build maze | O(n + m*alpha(n)) | O(n) | Walls processed in file order using DSU |
| Kruskal | O(n + m log m) | O(n + m) | Sort wall indexes by cost and use DSU |
| Prim | O((n + m) log n) | O(n + m) | Connected simple grid graph, adjacency lists, and a priority queue |
| DFS | O(n + m) | O(n + m) | Explicit stack; at most four neighbours per cell |
| Kosaraju | O(n + m) | O(n + m) | Two iterative DFS passes with forward and reversed graphs |
| Route in the zone graph | O(n + m) | O(n + m) | Includes graph construction; parallel edges retained and zones processed in topological order |

For a grid maze, m = O(n), so the DFS time and space are O(n).

## Before you submit

- data/walls.txt, data/slides.txt and output/maze.png are in the repository.
- `java tools/Check.java` shows all examples right.
- This README is filled in, the program output and the section AI use included.
- The work is pushed to main (at least one commit per mission) and the repository is public.
- The link is attached in Moodle.
