package common.base.graph;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static common.base.graph.DirectedGraphNode.Presence.EDGE_EXISTS;

/**
 * @author zhaoju
 * @date 2018/9/3 22:39
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectedAcyclicGraph<N> implements Graph<N> {
    @Getter
    private int edgeCount;
    private Map<N, DirectedGraphNode<N>> nodeMap;

    public static <N1> DirectedAcyclicGraph<N1> of() {
        return new DirectedAcyclicGraph<>(0, Maps.newLinkedHashMap());
    }

    public boolean addNode(N node) {
        Objects.requireNonNull(node, "node");
        if (containsNode(node)) {
            return false;
        }
        addNodeInternal(node);
        return true;
    }

    public void putEdgeValue(N nodeU, N nodeV) {
        Objects.requireNonNull(nodeU, "nodeU");
        Objects.requireNonNull(nodeV, "nodeV");
        if (nodeU.equals(nodeV)) {
            throw new IllegalStateException(String.format("不能生成自环图 node ==> %s", nodeU));
        }
        // TODO: 2018/9/4 使用dfs或bfs校验nodeV和nodeB之间是否为强连通的
        DirectedGraphNode.Presence value = EDGE_EXISTS;
        DirectedGraphNode<N> directedGraphNodeU = nodeMap.get(nodeU);
        if (directedGraphNodeU == null) {
            directedGraphNodeU = addNodeInternal(nodeU);
        }
        DirectedGraphNode.Presence previousValue = directedGraphNodeU.addSuccessor(nodeV, value);

        DirectedGraphNode<N> directedGraphNodeV = nodeMap.get(nodeV);
        if (directedGraphNodeV == null) {
            directedGraphNodeV = addNodeInternal(nodeV);
        }
        directedGraphNodeV.addPredecessor(nodeU, value);

        if (previousValue == null) {
            edgeCount++;
        }
    }

    public boolean removeNode(N node) {
        Objects.requireNonNull(node, "node");

        DirectedGraphNode<N> directedGraphNode = nodeMap.get(node);
        if (directedGraphNode == null) {
            return false;
        }

        for (N successor : directedGraphNode.successors()) {
            nodeMap.get(successor).removePredecessor(node);
        }

        for (N predecessor : directedGraphNode.predecessors()) {
            nodeMap.get(predecessor).removeSuccessor(node);
            --edgeCount;
        }
        nodeMap.remove(node);
        return true;
    }

    public DirectedGraphNode.Presence removeEdge(N nodeU, N nodeV) {
        Objects.requireNonNull(nodeU, "nodeU");
        Objects.requireNonNull(nodeV, "nodeV");

        DirectedGraphNode<N> directedGraphNodeU = nodeMap.get(nodeU);
        DirectedGraphNode<N> directedGraphNodeV = nodeMap.get(nodeV);
        if (directedGraphNodeU == null || directedGraphNodeV == null) {
            return null;
        }
        DirectedGraphNode.Presence previousValue = directedGraphNodeU.removeSuccessor(nodeV);
        if (previousValue != null) {
            directedGraphNodeV.removePredecessor(nodeU);
            --edgeCount;
        }
        return previousValue;
    }

    public Set<N> node() {
        return Collections.unmodifiableSet(nodeMap.keySet());
    }

    private DirectedGraphNode<N> addNodeInternal(N node) {
        DirectedGraphNode<N> directedGraphNode = DirectedGraphNode.of();
        checkState(nodeMap.put(node, directedGraphNode) == null);
        return directedGraphNode;
    }

    private boolean containsNode(N node) {
        return nodeMap.containsKey(node);
    }

    private void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
}
