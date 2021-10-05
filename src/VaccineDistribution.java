import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class VaccineDistribution {

  public static class Node {
    LinkedList<Edge> edges;
    int name;
    int isVisited;

    public Node(int name) {
      this.edges = new LinkedList<Edge>();
      this.name = name;
      isVisited = 0;
    }

    public int isVisited() {
      return isVisited;
    }

    public void addEdge(Edge newEdge) {
      edges.add(newEdge);
    }
  }

  public static class Edge {
    int weight;
    Node node1;
    Node node2;

    public Edge(int weight, Node node1, Node node2) {
      this.weight = weight;
      this.node1 = node1;
      this.node2 = node2;
    }

    public Node getNode1() {
      return this.node1;
    }

    public Node getNode2() {
      return this.node2;
    }
  }

  public static class Path {
    ArrayList<Node> path;

    public Path() {
      this.path = new ArrayList<Node>();
    }

    public boolean doesContain(Node node) {
      if (path.contains(node)) {
        return true;
      } else {
        return false;
      }
    }
  }

  public static void main(String[] args) {
    Scanner scnr = new Scanner(System.in);

    String input = scnr.nextLine();
    String[] tempIn = input.split(" ");

    int numNodes = Integer.parseInt(tempIn[0]);
    int numPartitions = Integer.parseInt(tempIn[1]);
    int numEdges = Integer.parseInt(tempIn[2]);

    ArrayList<Node> nodes = new ArrayList<Node>();

    for (int i = 0; i < numEdges; i++) {
      String newData = scnr.nextLine();
      String[] data = newData.split(" ");

      int weight = Integer.parseInt(data[2]);
      int node1 = Integer.parseInt(data[1]);
      int node2 = Integer.parseInt(data[0]);

      Node newNode1 = new Node(node1);
      Node newNode2 = new Node(node2);

      int contNode1 = containsNode(nodes, newNode1);
      int contNode2 = containsNode(nodes, newNode2);

      if (contNode1 >= 0) {
        if (contNode2 >= 0) {
          // contains both
          Edge newEdge = new Edge(weight, nodes.get(contNode1), nodes.get(contNode2));
          nodes.get(contNode1).addEdge(newEdge);
          nodes.get(contNode2).addEdge(newEdge);
        } else {
          // contains node1 but not node2
          nodes.add(newNode2);

          Edge newEdge = new Edge(weight, nodes.get(contNode1), newNode2);
          nodes.get(contNode1).addEdge(newEdge);
          newNode2.addEdge(newEdge);
        }
      }

      if (contNode2 >= 0) {
        if (contNode1 == -1) {
          // contains node2 but not node1
          nodes.add(newNode1);

          Edge newEdge = new Edge(weight, newNode1, nodes.get(contNode2));
          newNode1.addEdge(newEdge);
          nodes.get(contNode2).addEdge(newEdge);
        }
      }

      // both are new nodes
      if (contNode1 == -1 && contNode2 == -1) {
        nodes.add(newNode1);
        nodes.add(newNode2);

        Edge newEdge = new Edge(weight, newNode1, newNode2);
        newNode1.addEdge(newEdge);
        newNode2.addEdge(newEdge);
      }
    }

    scnr.close();
    divide(nodes, numPartitions);
  }

  private static int containsNode(ArrayList<Node> nodes, Node searchNode) {
    for (int i = 0; i < nodes.size(); i++) {
      if (nodes.get(i).name == searchNode.name) {
        return i;
      }
    }
    return -1;
  }

  // divides the graph of nodes into partitions
  public static void divide(ArrayList<Node> nodes, int partitions) {
    int connectedParts = -1;
    while (connectedParts < partitions) {
      connectedParts = isConnected(nodes);

      if (connectedParts == partitions)
        break;

      Edge biggestEdge = findBiggestEdge(nodes);
      removeEdge(nodes, biggestEdge);
    }
    System.out.print(findBiggestEdge(nodes).weight);
  }

  // removes the edge from the graph
  private static void removeEdge(ArrayList<Node> nodes, Edge edge) {
    for (int i = 0; i < nodes.size(); i++) {
      if (nodes.get(i).edges.contains(edge)) {
        nodes.get(i).edges.remove(edge);
      }
    }
  }

  // finds the edge with the highest weight
  private static Edge findBiggestEdge(ArrayList<Node> nodes) {
    Edge bigEdge = new Edge(0, null, null);
    for (int i = 0; i < nodes.size(); i++) {
      Node currNode = nodes.get(i);
      LinkedList<Edge> currEdges = currNode.edges;

      for (int j = 0; j < currEdges.size(); j++) {
        if (bigEdge.weight < currEdges.get(j).weight) {
          bigEdge = currEdges.get(j);
        }
      }
    }

    return bigEdge;
  }

  // checks if the given graph is connected
  // returns the number of partitions
  private static int isConnected(ArrayList<Node> nodes) {
    ArrayList<Path> listOfPaths = new ArrayList<Path>();

    for (int i = 0; i < nodes.size(); i++) {
      Node currNode = nodes.get(i);

      LinkedList<Edge> currEdges = currNode.edges;
      boolean foundPath = false;
      boolean breakLoop = false;

      for (int j = 0; j < currEdges.size(); j++) {
        for (int k = 0; k < listOfPaths.size(); k++) {
          if (listOfPaths.get(k).doesContain(currEdges.get(j).getNode1())
              || listOfPaths.get(k).doesContain(currEdges.get(j).getNode2())) {
            listOfPaths.get(k).path.add(currNode);
            foundPath = true;
            breakLoop = true;
            break;
          }
        }
        if (breakLoop == true) {
          break;
        }
      }

      if (foundPath == false) {
        Path newPath = new Path();
        newPath.path.add(currNode);
        listOfPaths.add(newPath);
      }
    }

    for (int i = 0; i < listOfPaths.size(); i++) {
      for (int j = 0; j < listOfPaths.size(); j++) {
        if (i != j) {
          Path path1 = listOfPaths.get(i);
          Path path2 = listOfPaths.get(j);

          if (hasNeighbor(path1, path2)) {
            Path oldPath = listOfPaths.get(i);
            for (int q = 0; q < listOfPaths.get(j).path.size(); q++) {
              oldPath.path.add(listOfPaths.get(j).path.get(q));
            }
            listOfPaths.remove(j);
            i--;
          }
        }
      }
    }

    return listOfPaths.size();
  }

  private static boolean hasNeighbor(Path p1, Path p2) {
    for (int k = 0; k < p2.path.size(); k++) {
      Node node = p2.path.get(k);
      for (int i = 0; i < p1.path.size(); i++) {
        Node currNode = p1.path.get(i);
        LinkedList<Edge> edges = currNode.edges;

        for (int j = 0; j < edges.size(); j++) {
          if (i != j) {
            if (edges.get(j).node1.equals(node) || edges.get(j).node2.equals(node)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private static ArrayList<Node> getNeighbors(Node findNode) {
    ArrayList<Node> neighbors = new ArrayList<Node>();
    LinkedList<Edge> edges = findNode.edges;

    for (int i = 0; i < edges.size(); i++) {
      if (!neighbors.contains(edges.get(i).getNode1()) && edges.get(i).getNode1() != findNode) {
        neighbors.add(edges.get(i).getNode1());
      }
      if (!neighbors.contains(edges.get(i).getNode2()) && edges.get(i).getNode2() != findNode) {
        neighbors.add(edges.get(i).getNode2());
      }
    }
    return neighbors;
  }

}
