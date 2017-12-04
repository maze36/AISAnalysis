package datamodel;

public class RTMEdge {

	private RTMNode startNode;
	private RTMNode endNode;

	public RTMEdge(RTMNode startNode, RTMNode endNode) {
		this.startNode = startNode;
		this.endNode = endNode;
	}

	public RTMNode getStartNode() {
		return startNode;
	}

	public void setStartNode(RTMNode startNode) {
		this.startNode = startNode;
	}

	public RTMNode getEndNode() {
		return endNode;
	}

	public void setEndNode(RTMNode endNode) {
		this.endNode = endNode;
	}

}
