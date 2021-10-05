import java.util.*;

public class HW4 {
	/**
	 * Vertex objects group a data field with an adjacency list of weighted directed
	 * edges that lead away from them.
	 */
	public static class Node {
		public Integer data; // vertex label or application specific data
		public LinkedList<Edge> edgesLeaving;

		public Node(Integer data) {
			this.data = data;
			this.edgesLeaving = new LinkedList<>();
		}
	}

	/**
	 * Edge objects are stored within their source vertex, and group together their
	 * target destination vertex, along with an integer weight.
	 */
	public static class Edge {
		public Integer weight;
		public ArrayList<Node> nodesOfEdge;

		public Edge(Node to, Node fro, int weight) {
			this.nodesOfEdge = new ArrayList<Node>();
			this.nodesOfEdge.add(to);
			this.nodesOfEdge.add(fro);
			this.weight = weight;
		}
	}

	static Hashtable<Integer, Node> nodes;
	static int largestEdgeWeight = 0;
	static ArrayList<ArrayList<Integer>> paths;

	static boolean edgeExists(Node to, Node fro) {
		for (int i = 0; i < to.edgesLeaving.size(); i++) {
			Edge tempEdge = to.edgesLeaving.get(i);
			if (tempEdge.nodesOfEdge.contains(to) && tempEdge.nodesOfEdge.contains(fro)) return true;
//			if ((tempEdge.to.data == to.data && tempEdge.fro.data == fro.data)
//					|| (tempEdge.to.data == fro.data && tempEdge.fro.data == to.data)) {
//				return true;
//			}
		}
//		for (int i = 0; i < fro.edgesLeaving.size(); i++) {
//			Edge tempEdge = fro.edgesLeaving.get(i);
//			if ((tempEdge.to.data == to.data && tempEdge.fro.data == fro.data)
//					|| (tempEdge.to.data == fro.data && tempEdge.fro.data == to.data)) {
//				return true;
//			}
//		}
		return false;
	}

	public static void removeEdge(Integer edgeWeight) {
		Iterator<Integer> nodesIterator = nodes.keySet().iterator();
		Node tempNode;
		while (nodesIterator.hasNext()) {
			tempNode = new Node(nodesIterator.next());
			for (int i = 0; i < nodes.get(tempNode.data).edgesLeaving.size(); i++) {
				if (nodes.get(tempNode.data).edgesLeaving.get(i).weight == edgeWeight) {
					nodes.get(tempNode.data).edgesLeaving.remove(i);
				}
			}
		}
	}
	
	public static int findNodeInPaths(Integer nodeData) {
		for (int i = 0; i < paths.size(); i++) {
			if (paths.get(i).contains(nodeData)) return i;
		}
		return -1;
	}

	public static Integer partition(Integer desiredParts, Integer maxEdgeWeight) {
		Integer maxWeight = maxEdgeWeight;
		Integer currentParts = 0;
		int inPathsIndex;
		Integer tempIndex;
		Node tempNode;
		Node tempLeave = null;
		Boolean found = false;
		if (currentParts == desiredParts) return maxWeight;
		while (currentParts != desiredParts + 1) {
			paths = new ArrayList<ArrayList<Integer>>();
			removeEdge(maxWeight);
			maxWeight--;
			Iterator<Integer> nodesIterator = nodes.keySet().iterator();
			while (nodesIterator.hasNext()) {
				tempNode = nodes.get(nodesIterator.next());
				inPathsIndex = findNodeInPaths(tempNode.data);
				if (inPathsIndex == -1) {
					inPathsIndex = paths.size();
					paths.add(new ArrayList<Integer>(Arrays.asList(tempNode.data)));
					
				}
				for (int i = 0; i < nodes.get(tempNode.data).edgesLeaving.size(); i++) {
					tempLeave = nodes.get(tempNode.data).edgesLeaving.get(i).nodesOfEdge.get(0);
					if (tempLeave.data == tempNode.data) {
						tempLeave = nodes.get(tempNode.data).edgesLeaving.get(i).nodesOfEdge.get(1);
					}
					if (findNodeInPaths(tempLeave.data) == -1) {
						paths.get(inPathsIndex).add(tempLeave.data);
					}
				}
			}

			currentParts = paths.size();
		}
		return maxWeight;
	}

	public static void main(String[] args) {
		Scanner scnr = new Scanner(System.in);
		nodes = new Hashtable<Integer, Node>();
		Integer numNodes = Integer.parseInt(scnr.next());
		Integer numCenters = Integer.parseInt(scnr.next());
		Integer numEdges = Integer.parseInt(scnr.next());
		scnr.nextLine();
		Node to;
		Node fro;
		Integer tempTo;
		Integer tempFro;
		Integer tempWeight;
		Edge tempEdge;
		for (int i = 0; i < numEdges; i++) {
			tempTo = Integer.parseInt(scnr.next());
			tempFro = Integer.parseInt(scnr.next());
			tempWeight = Integer.parseInt(scnr.next());
			scnr.nextLine();
			if (!nodes.containsKey(tempTo)) {
				to = new Node(tempTo);
				nodes.put(tempTo, to);
			} else {
				to = nodes.get(tempTo);
			}
			if (!nodes.containsKey(tempFro)) {
				fro = new Node(tempFro);
				nodes.put(tempFro, fro);
			} else {
				fro = nodes.get(tempFro);
			}
			
			if (!edgeExists(to,fro)) {
				//System.out.println("Adding Edge from node: " + to.data + " to " + fro.data + " of size " + tempWeight);
				tempEdge = new Edge(to, fro, tempWeight);
				to.edgesLeaving.add(tempEdge);
				fro.edgesLeaving.add(tempEdge);
			}
			
			if (tempWeight > largestEdgeWeight) {
				largestEdgeWeight = tempWeight;
			}
			
		}
		
		int weights = partition(numCenters, largestEdgeWeight);
		
//		for (int j = 0; j < paths.size(); j++) {
//		System.out.print("Path " + (j + 1) + ", contains nodes: ");
//		for (Integer k: paths.get(j)) {
//			System.out.print(k + ", ");
//		}
//		System.out.println();
//	}
		
		for (int i = 1; i < nodes.size() + 1; i++) {
			System.out.print(" Node: " + nodes.get(i).data + " Edges: ");
			for (int j = 0; j < nodes.get(i).edgesLeaving.size(); j++) {
				System.out.print(nodes.get(i).edgesLeaving.get(j).weight + ", ");				
			}
			System.out.println();
		}
		for (int j = 0; j < paths.size(); j++) {
			System.out.print("Path " + (j + 1) + ", contains nodes: ");
			for (Integer k: paths.get(j)) {
				System.out.print(k + ", ");
			}
			System.out.println();
		}
		
		
		System.out.println(weights);
	}
}
