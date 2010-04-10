package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.java.InstanceContour;
import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.contour.java.StaticContour;
import edu.bsu.cs.jive.util.ContourID;

public class ContourGraph {
	
	public enum Section { STATIC, REACHABLE, UNREACHABLE }
	
	public static final int STATIC_SECTION = -1;
	
	private DirectedGraph graph = new DirectedGraph();;
	
	private Map<Contour, Node> contourToNodeMapping = new HashMap<Contour, Node>();
	
	private Map<Integer, Section> nodeSections = new HashMap<Integer, Section>();
	
	private Map<Integer, Integer> nodeColumns = new HashMap<Integer, Integer>();
	
	private Map<Integer, Integer> nodeLayers = new HashMap<Integer, Integer>();
	
	private Map<Integer, Integer> nodeCells = new HashMap<Integer, Integer>();
	
	private Map<Integer, Color> nodeColors = new HashMap<Integer, Color>();
	
	public ContourGraph(ContourModel model) {
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			initializeGraph(model);
			assignLayers();
		}
		finally {
			modelLock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeGraph(final ContourModel model) {
		initializeGraphNodes(model);
		initializeGraphEdges(model);
	}
	
	@SuppressWarnings("unchecked")
	private void initializeGraphNodes(ContourModel model) {
		for (Contour c : model.getRoots()) {
			// Create node and add it to the graph
			Integer data = determineNodeData(c);
			Node v = new Node(data);
			graph.nodes.add(v);
			
			// Color the node white for graph traversal algorithm
			nodeColors.put(data, Color.WHITE);
			
			// Add a contour-node mapping for the contour and its children
			contourToNodeMapping.put(c, v);
			addNestedContourMappings(model, c, v);
		}
	}
	
	private int determineNodeData(Contour c) {
		if (c instanceof StaticContour) {
			return 0;
		}
		else {
			String id = c.id().toString();
			int index = id.lastIndexOf(':');
			return Integer.parseInt(id.substring(index + 1));
		}
	}

	private void addNestedContourMappings(ContourModel model, Contour parent, Node v) {
		for (Contour c : model.getChildren(parent)) {
			contourToNodeMapping.put(c, v);
			addNestedContourMappings(model, c, v);
		}
	}
	
	private void initializeGraphEdges(final ContourModel model) {
		model.visitDepthFirst(new ContourModel.Visitor() {
			public void visit(Contour contour) {
				addGraphEdges(model, contour);
			}
		});
	}
	
	private void addGraphEdges(final ContourModel model, Contour c) {
		final Node u = contourToNodeMapping.get(c);
		c.export(new Contour.Exporter() {

			public void addID(ContourID id) {
				// do nothing
			}

			@SuppressWarnings("unchecked")
			public void addMember(ContourMember member) {
				Value value = member.value();
				if (value instanceof Value.ContourReference) {
					Value.ContourReference reference = (Value.ContourReference) value;
					Contour d = model.getContour(reference.getContourID());
					Node v = contourToNodeMapping.get(d);
					
					if (member.type().toString().equals("rpdl")) {
						Edge e = new Edge(v, u);
						graph.edges.add(e);
						v.outgoing.add(e);
						u.incoming.add(e);
					}
					else {
						Edge e = new Edge(u, v);
						graph.edges.add(e);
						u.outgoing.add(e);
						v.incoming.add(e);
					}
				}
				
			}

			public void exportFinished() {
				// do nothing
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void sortOutgoingEdges(Node u) {
		Collections.sort(u.outgoing, new Comparator() {
			public int compare(Object o1, Object o2) {
				Edge e1 = (Edge) o1;
				Edge e2 = (Edge) o2;
				Node v1 = e1.target;
				Node v2 = e2.target;
				int id1 = (Integer) v1.data;
				int id2 = (Integer) v2.data;
				if (id1 < id2) {
					return -1;
				}
				else if (id1 > id2) {
					return 1;
				}
				else {
					return 0;
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void sortRootOutgoingEdges(Node u) {
		Collections.sort(u.outgoing, new Comparator() {
			public int compare(Object o1, Object o2) {
				Edge e1 = (Edge) o1;
				Edge e2 = (Edge) o2;
				Node v1 = e1.target;
				Node v2 = e2.target;
				int incomingCount1 = v1.incoming.size();
				int incomingCount2 = v2.incoming.size();
				
				if (incomingCount1 < incomingCount2) {
					return -1;
				}
				else if (incomingCount1 > incomingCount2) {
					return 1;
				}
				else {
					int id1 = (Integer) v1.data;
					int id2 = (Integer) v2.data;
					
					if (id1 < id2) {
						return -1;
					}
					else if (id1 > id2) {
						return 1;
					}
					else {
						return 0;
					}
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void sortNodes(NodeList nodes) {
		Collections.sort(nodes, new Comparator() {
			public int compare(Object o1, Object o2) {
				Node v1 = (Node) o1;
				Node v2 = (Node) o2;
				int incomingCount1 = v1.incoming.size();
				int incomingCount2 = v2.incoming.size();
				
				if (incomingCount1 < incomingCount2) {
					return -1;
				}
				else if (incomingCount1 > incomingCount2) {
					return 1;
				}
				else {
					int id1 = (Integer) v1.data;
					int id2 = (Integer) v2.data;
					
					if (id1 < id2) {
						return -1;
					}
					else if (id1 > id2) {
						return 1;
					}
					else {
						return 0;
					}
				}
			}
		});
	}
	
	private enum Color { WHITE, GRAY, BLACK }
	
	private Color getColor(Node u) {
		Integer id = (Integer) u.data;
		return nodeColors.get(id);
	}
	
	private void setColor(Node u, Color c) {
		Integer id = (Integer) u.data;
		nodeColors.put(id, c);
	}
		
	private void assignLayers() {
		NodeList nodes = new NodeList(graph.nodes);
		if (nodes.isEmpty()) {
			return;
		}

		Node staticNode = (Node) nodes.remove(0);
		setPositionData(staticNode, Section.STATIC, 0, 0, 0);
		setColor(staticNode, Color.GRAY);
		
		sortRootOutgoingEdges(staticNode);
		Section section = Section.REACHABLE;
		int column = 0;
		int layer = 0;
		for (Object o : staticNode.outgoing) {
			Edge e = (Edge) o;
			Node u = e.target;
			if (getColor(u) == Color.WHITE) {
//				sortRootOutgoingEdges(u);
				Map<Integer, Integer> layerToNextCellMapping = new HashMap<Integer, Integer>();
				depthFirstSearchVisit(u, section, column, layer, layerToNextCellMapping);
				column++;
			}
		}
		
		setColor(staticNode, Color.BLACK);
		
		section = Section.UNREACHABLE;
		column = 0;
		layer = 0;
		
		sortNodes(nodes);
		for (Object o : nodes) {
			Node u = (Node) o;
			if (getColor(u) == Color.WHITE) {
//				sortOutgoingEdges(u);
				Map<Integer, Integer> layerToNextCellMapping = new HashMap<Integer, Integer>();
				depthFirstSearchVisit(u, section, column, layer, layerToNextCellMapping);
				column++;
			}
		}
	}
	
	private void depthFirstSearchVisit(Node u, Section section, int column, int layer, Map<Integer, Integer> layerToNextCellMapping) {
		if (layerToNextCellMapping.containsKey(layer)) {
			int cell = layerToNextCellMapping.get(layer);
			setPositionData(u, section, column, layer, cell);
			layerToNextCellMapping.put(layer, cell + 1);
		}
		else {
			setPositionData(u, section, column, layer, 0);
			layerToNextCellMapping.put(layer, 1);
		}
		
		setColor(u, Color.GRAY);
		layer++;
		
		for (Object o : u.outgoing) {
			Edge e = (Edge) o;
			Node v = e.target;
			
			if (getColor(v) == Color.WHITE) {
//				sortOutgoingEdges(v);
				depthFirstSearchVisit(v, section, column, layer, layerToNextCellMapping);
			}
		}
		
		setColor(u, Color.BLACK);
	}
	
//	@SuppressWarnings("unchecked")
//	private void assignLayers(int width) {
//		topologicallyLabelNodes();
//		int k = 0;
//		List<NodeList> layers = new ArrayList<NodeList>();
//		NodeList currentLayer = new NodeList();
//		NodeList remainingNodes = new NodeList(graph.nodes);
//		NodeList examinedNodes = new NodeList();
//		
//		while (examinedNodes.size() != graph.nodes.size()) {
//			Node u = chooseCandidateNode(remainingNodes, examinedNodes);
//			if (currentLayer.size() < width && containsEveryTargetNode(u, layers)) {
//				currentLayer.add(u);
//				nodeToInternalLayerMapping.put(u, k);
//			}
//			else {
//				layers.add(k, currentLayer);
//				k++;
//				currentLayer = new NodeList();
//				currentLayer.add(u);
//				nodeToInternalLayerMapping.put(u, k);
//			}
//			
//			examinedNodes.add(u);
//			remainingNodes.remove(u);
//		}
//		
//		lastLayer = k;
//	}
//	
//	private Node chooseCandidateNode(NodeList remainingNodes, NodeList examinedNodes) {
//		int smallestSeenLabel = Integer.MAX_VALUE;
//		Node result = null;
//		for (Object o : remainingNodes) {
//			Node temp = (Node) o;
//			if (containsEveryTargetNode(temp, examinedNodes)) {
//				int label = (Integer) temp.data;
//				if (label < smallestSeenLabel) {
//					result = temp;
//					smallestSeenLabel = label;
//				}
//			}
//		}
//		
//		assert result != null;
//		return result;
//	}
//	
//	private boolean containsEveryTargetNode(Node u, NodeList list) {
//		for (Object o : u.outgoing) {
//			Edge e = (Edge) o;
//			Node v = e.target;
//			if (!list.contains(v)) {
//				return false;
//			}
//		}
//		
//		return true;
//	}
//	
//	@SuppressWarnings("unchecked")
//	private boolean containsEveryTargetNode(Node u, List<NodeList> layers) {
//		NodeList temp = new NodeList();
//		for (NodeList list : layers) {
//			temp.addAll(list);
//		}
//		
//		return containsEveryTargetNode(u, temp);
//	}
//	
//	private enum Color { WHITE, GRAY, BLACK }
//	
//	private void topologicallyLabelNodes() {
//		Map<Node, Color> nodeToColorMapping = new HashMap<Node, Color>();
//		label = graph.nodes.size() - 1;
//		
//		for (Object o : graph.nodes) {
//			Node u = (Node) o;
//			nodeToColorMapping.put(u, Color.WHITE);
//		}
//		
//		for (Object o : graph.nodes) {
//			Node u = (Node) o;
//			if (nodeToColorMapping.get(u).equals(Color.WHITE)) {
//				depthFirstSearchVisit(u, nodeToColorMapping);
//			}
//		}
//	}
//	
//	private void depthFirstSearchVisit(Node u, Map<Node, Color> nodeToColorMapping) {
//		nodeToColorMapping.put(u, Color.GRAY);
//		
//		for (Object o : u.outgoing) {
//			Edge e = (Edge) o;
//			Node v = e.target;
//			if (nodeToColorMapping.get(v).equals(Color.WHITE)) {
//				depthFirstSearchVisit(v, nodeToColorMapping);
//			}
//		}
//		
//		nodeToColorMapping.put(u, Color.BLACK);
//		u.data = new Integer(label);
//		label--;
//	}
//	
//	public int getLayer(Contour c) {
//		if (contourToNodeMapping.containsKey(c)) {
//			Node u = contourToNodeMapping.get(c);
//			return lastLayer - nodeToInternalLayerMapping.get(u);
//		}
//		else {
//			System.out.println("No layer for contour " + c);
//			return -1;
//		}
//	}
	
	private void setPositionData(Node u, Section section, int column, int layer, int cell) {
		int id = (Integer) u.data;
		nodeSections.put(id, section);
		nodeColumns.put(id, column);
		nodeLayers.put(id, layer);
		nodeCells.put(id, cell);
	}
	
	public ContourGraphPosition getPosition(Contour c) {
		if (contourToNodeMapping.containsKey(c)) {
			Node u = contourToNodeMapping.get(c);
			Integer id = (Integer) u.data;
			return new ContourGraphPosition(nodeSections.get(id), nodeColumns.get(id), nodeLayers.get(id), nodeCells.get(id));
		}
		else {
			throw new IllegalArgumentException("Position undefined for contour " + c.id());
		}
	}
}
