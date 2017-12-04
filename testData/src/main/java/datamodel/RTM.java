package datamodel;

import java.util.ArrayList;

public class RTM {

	private ArrayList<RTMEdge> edgesWithNodes;

	public RTM() {
		this.edgesWithNodes = new ArrayList<RTMEdge>();
	}

	public ArrayList<RTMEdge> getEdgesWithNodes() {
		return edgesWithNodes;
	}

	public boolean addEdge(RTMEdge edge) {
		return this.edgesWithNodes.add(edge);
	}

	public void setEdgesWithNodes(ArrayList<RTMEdge> edgesWithNodes) {
		this.edgesWithNodes = edgesWithNodes;
	}

}
