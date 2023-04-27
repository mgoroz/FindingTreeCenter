import java.util.*;

/** Container class to different classes, that makes the whole
 * set of classes one class formally.
 */
public class GraphTask {

   /** Main method. */
   public static void main (String[] args) {
      GraphTask a = new GraphTask();
      a.run();
   }

   /** Actual main method to run examples and everything. */
   public void run() {
      Graph g = new Graph("G");
      g.createRandomSimpleGraph(6, 9);
      System.out.println("Initial Graph:");
      System.out.println(g.toString());

      // Test case with a tree that has two centers

      Graph g2 = new Graph("G2");
      Vertex v1 = g2.createVertex("v1");
      Vertex v2 = g2.createVertex("v2");
      Vertex v3 = g2.createVertex("v3");
      Vertex v4 = g2.createVertex("v4");

      g2.createArc("a1", v1, v2);
      g2.createArc("a2", v2, v1);
      g2.createArc("a3", v2, v3);
      g2.createArc("a4", v3, v2);
      g2.createArc("a5", v3, v4);
      g2.createArc("a6", v4, v3);

      System.out.println("Example 2:");
      System.out.println(g2.toString());

      List<Vertex> centers2 = g2.findCenter();
      System.out.println("Centers: " + centers2);

      int[][] testCases = {
              {6, 5},
              {10, 9},
              {50, 49},
              {100, 99},
              {2000, 1999},
      };

      for (int[] testCase : testCases) {
         int vertices = testCase[0];
         int edges = testCase[1];
         System.out.println("Testing a graph with " + vertices + " vertices and " + edges + " edges.");

         Graph testGraph = new Graph("TestGraph");
         testGraph.createRandomTree(vertices);

         if (vertices != 2000) {
            System.out.println("Graph representation:");
            System.out.println(testGraph.toString());
         }

         long startTime = System.currentTimeMillis();
         List<Vertex> centers = testGraph.findCenter();
         long endTime = System.currentTimeMillis();

         System.out.println("Centers: " + centers);
         System.out.println("Execution time: " + (endTime - startTime) + " ms");
         System.out.println();
      }
   }



   // TODO!!! add javadoc relevant to your problem
   class Vertex {

      private String id;
      private Vertex next;
      private Arc first;
      private int info = 0;
      // You can add more fields, if needed

      Vertex (String s, Vertex v, Arc e) {
         id = s;
         next = v;
         first = e;
      }

      Vertex (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }

      // TODO!!! Your Vertex methods here!
   }


   /** Arc represents one arrow in the graph. Two-directional edges are
    * represented by two Arc objects (for both directions).
    */
   class Arc {

      private String id;
      private Vertex target;
      private Arc next;
      private int info = 0;
      // You can add more fields, if needed

      Arc (String s, Vertex v, Arc a) {
         id = s;
         target = v;
         next = a;
      }

      Arc (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }

      // TODO!!! Your Arc methods here!
   } 


   class Graph {

      private String id;
      private Vertex first;
      private int info = 0;
      // You can add more fields, if needed

      Graph (String s, Vertex v) {
         id = s;
         first = v;
      }

      Graph (String s) {
         this (s, null);
      }

      @Override
      public String toString() {
         String nl = System.getProperty ("line.separator");
         StringBuffer sb = new StringBuffer (nl);
         sb.append (id);
         sb.append (nl);
         Vertex v = first;
         while (v != null) {
            sb.append (v.toString());
            sb.append (" -->");
            Arc a = v.first;
            while (a != null) {
               sb.append (" ");
               sb.append (a.toString());
               sb.append (" (");
               sb.append (v.toString());
               sb.append ("->");
               sb.append (a.target.toString());
               sb.append (")");
               a = a.next;
            }
            sb.append (nl);
            v = v.next;
         }
         return sb.toString();
      }

      public Vertex createVertex (String vid) {
         Vertex res = new Vertex (vid);
         res.next = first;
         first = res;
         return res;
      }

      public Arc createArc (String aid, Vertex from, Vertex to) {
         Arc res = new Arc (aid);
         res.next = from.first;
         from.first = res;
         res.target = to;
         return res;
      }

      /**
       * Create a connected undirected random tree with n vertices.
       * Each new vertex is connected to some random existing vertex.
       * @param n number of vertices added to this graph
       */
      public void createRandomTree (int n) {
         if (n <= 0)
            return;
         Vertex[] varray = new Vertex [n];
         for (int i = 0; i < n; i++) {
            varray [i] = createVertex ("v" + String.valueOf(n-i));
            if (i > 0) {
               int vnr = (int)(Math.random()*i);
               createArc ("a" + varray [vnr].toString() + "_"
                  + varray [i].toString(), varray [vnr], varray [i]);
               createArc ("a" + varray [i].toString() + "_"
                  + varray [vnr].toString(), varray [i], varray [vnr]);
            } else {}
         }
      }

      /**
       * Create an adjacency matrix of this graph.
       * Side effect: corrupts info fields in the graph
       * @return adjacency matrix
       */
      public int[][] createAdjMatrix() {
         info = 0;
         Vertex v = first;
         while (v != null) {
            v.info = info++;
            v = v.next;
         }
         int[][] res = new int [info][info];
         v = first;
         while (v != null) {
            int i = v.info;
            Arc a = v.first;
            while (a != null) {
               int j = a.target.info;
               res [i][j]++;
               a = a.next;
            }
            v = v.next;
         }
         return res;
      }

      /**
       * Create a connected simple (undirected, no loops, no multiple
       * arcs) random graph with n vertices and m edges.
       * @param n number of vertices
       * @param m number of edges
       */
      public void createRandomSimpleGraph (int n, int m) {
         if (n <= 0)
            return;
         if (n > 2500)
            throw new IllegalArgumentException ("Too many vertices: " + n);
         if (m < n-1 || m > n*(n-1)/2)
            throw new IllegalArgumentException 
               ("Impossible number of edges: " + m);
         first = null;
         createRandomTree (n);       // n-1 edges created here
         Vertex[] vert = new Vertex [n];
         Vertex v = first;
         int c = 0;
         while (v != null) {
            vert[c++] = v;
            v = v.next;
         }
         int[][] connected = createAdjMatrix();
         int edgeCount = m - n + 1;  // remaining edges
         while (edgeCount > 0) {
            int i = (int)(Math.random()*n);  // random source
            int j = (int)(Math.random()*n);  // random target
            if (i==j) 
               continue;  // no loops
            if (connected [i][j] != 0 || connected [j][i] != 0) 
               continue;  // no multiple edges
            Vertex vi = vert [i];
            Vertex vj = vert [j];
            createArc ("a" + vi.toString() + "_" + vj.toString(), vi, vj);
            connected [i][j] = 1;
            createArc ("a" + vj.toString() + "_" + vi.toString(), vj, vi);
            connected [j][i] = 1;
            edgeCount--;  // a new edge happily created
         }
      }

      /**
       * Finds the center(s) of a tree represented by this graph.
       * A center is defined as a vertex with minimum eccentricity, which is the maximum distance to any other vertex.
       * This method assumes that the graph represents a tree: it is connected and has no cycles.
       *
       * @return A list of center vertices.
       */
      public List<Vertex> findCenter() {
         List<Vertex> leafNodes = new ArrayList<>();
         List<Vertex> nonLeafNodes = new ArrayList<>();

         //  Separate leaf nodes and non-leaf nodes
         Vertex v = first;
         while (v != null) {
            int neighborCount = 0;
            Arc a = v.first;
            while (a != null) {
               neighborCount++;
               a = a.next;
            }
            if (neighborCount == 1) {
               leafNodes.add(v);
            } else {
               nonLeafNodes.add(v);
            }
            v = v.next;
         }

         // Remove leaf nodes iteratively until 1 or 2 nodes remain
         while (nonLeafNodes.size() > 2) {
            List<Vertex> newLeafNodes = new ArrayList<>();
            for (Vertex leaf : leafNodes) {
               Arc arc = leaf.first;
               Vertex neighbor = arc.target;
               Arc prevArc = null;
               Arc currentArc = neighbor.first;
               int neighborCount = 0;
               while (currentArc != null) {
                  if (currentArc.target == leaf) {
                     if (prevArc == null) {
                        neighbor.first = currentArc.next;
                     } else {
                        prevArc.next = currentArc.next;
                     }
                  } else {
                     neighborCount++;
                     prevArc = currentArc;
                  }
                  currentArc = currentArc.next;
               }
               if (neighborCount == 1) {
                  newLeafNodes.add(neighbor);
               }
            }
            leafNodes = newLeafNodes;
            nonLeafNodes.removeAll(leafNodes);
         }

         // Return the center(s) of the tree
         return nonLeafNodes;
      }
   }

} 

