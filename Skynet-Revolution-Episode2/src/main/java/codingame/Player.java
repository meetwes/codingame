package codingame;

import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    static Map<Integer, Node> nodesMap = new HashMap<>();
    static Map<Integer, Node> gwNodesMap = new HashMap<>();

    static class Link {
        Node prevNode;
        Integer distance;

        Link(Node prevNode, Integer distance) {
            this.prevNode = prevNode;
            this.distance = distance;
        }
    }
    static class Node {
        Integer id;
        Map<Integer, Node> siblings;
        Link link;
        Boolean isGW;

        Node(Integer id) {
            this.id = id;
            siblings = new HashMap<>();
            this.link = null;
            this.isGW = false;
        }

        void setIsGW (Boolean isGW) {
            this.isGW = isGW;
        }
        void initializeLink(Node prevNode, Integer distance) {
            this.link = new Link(prevNode, distance);
        }
    }

    static void handleNodeCut(Node firstNode, Node secondNode) {
        System.err.println("Removing link " + firstNode.id + ", " + secondNode.id);
        firstNode.siblings.remove(secondNode.id);
        secondNode.siblings.remove(firstNode.id);
        System.out.println(firstNode.id + " " + secondNode.id);
    }

    static Node getGWFromSibling(Node agentNode) {
        for (Integer temp: agentNode.siblings.keySet()) {
            if (gwNodesMap.get(temp) != null) {
                return gwNodesMap.get(temp);
            }
        }
        return null;
    }

    static void getGwNode(Node agentNode) {

        Node res = getGWFromSibling(agentNode);

        // If one of the siblings of the agent Node is a gateway, cut immediately.
        if (res != null) {
            handleNodeCut(agentNode, res);
            return;
        }

        Queue<Node> queue = new LinkedList<>();
        Map<Integer, Node> visitedNodes = new HashMap<>();

        Node currNode = agentNode; //new NodeDist(agentNode, null, 0, null);
        currNode.initializeLink(null, 0);
        while(currNode != null) {

            ArrayList<Node> listOfGWs = new ArrayList<>();
            //           if (!visitedNodes.containsKey(currNode.id)) {

            visitedNodes.put(currNode.id, currNode);
            Integer distance = currNode.link.distance + 1;
            int gwCountPerNode = 0;

            for (Node node: currNode.siblings.values()) {
                if (gwNodesMap.get(node.id) != null ) {
                    gwCountPerNode ++;
                    break;
                }
            }

            // Do not add distance to a node if it has one of the siblings as a gateway node.
            if (gwCountPerNode > 0) {
                distance --;
            }

            for (Node nextNode: currNode.siblings.values()) {
                // Don't calculate distance for same node more than once.
                if (visitedNodes.get(nextNode.id) != null) continue;
                nextNode.initializeLink(currNode, distance);

                if (nextNode.isGW) {
                    listOfGWs.add(nextNode);

                    if (currNode.link.distance < listOfGWs.size()) {
                        handleNodeCut(currNode, nextNode);
                        return;
                    }
                } else {
                    visitedNodes.put(nextNode.id, nextNode);
                    queue.add(nextNode);

                }
            };
            currNode = queue.poll();
        }

        for (Node gw: gwNodesMap.values()) {
            if (!gw.siblings.isEmpty()) {
                // Cut a random link.
                Node linkNode = gw.siblings.values().stream().findFirst().get();
                handleNodeCut(gw, linkNode);
                return;
            }
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the total number of nodes in the level, including the gateways
        int L = in.nextInt(); // the number of links
        int E = in.nextInt(); // the number of exit gateways
        for (int i = 0; i < L; i++) {
            int N1 = in.nextInt();
            int N2 = in.nextInt();

            Node N1Node = nodesMap.get(N1);
            if (N1Node == null) {
                N1Node = new Node(N1);
                nodesMap.put(N1, N1Node);
            }
            Node N2Node = nodesMap.get(N2);
            if (N2Node == null) {
                N2Node = new Node(N2);
                nodesMap.put(N2, N2Node);
            }
            // N1 and N2 defines a link between these nodes
            N1Node.siblings.put(N2, N2Node);
            N2Node.siblings.put(N1, N1Node);
        }
        for (int i = 0; i < E; i++) {
            int EI = in.nextInt(); // the index of a gateway node
            Node gwNode = nodesMap.get(EI);
            gwNode.setIsGW(true);
            gwNodesMap.put(EI, gwNode);
        }

        // game loop
        while (true) {
            int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn
            getGwNode(nodesMap.get(SI));
        }
    }
}

