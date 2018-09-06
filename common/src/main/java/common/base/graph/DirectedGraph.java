package common.base.graph;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static common.base.graph.DirectedGraphNode.Presence.EDGE_EXISTS;

/**
 * create by zhaoju on 2018/09/06
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectedGraph<N> extends AbstractGraph<N> implements Graph<N> {

    @Getter
    @NonNull
    private int edgeCount;
    @NonNull
    protected Map<N, DirectedGraphNode<N>> nodeMap;

    private Set<DirectedGraphEdge<N>> edges;
    private Set<N> nodes;

    public static <N1> DirectedGraph<N1> of() {
        return new DirectedGraph<>(0, Maps.newLinkedHashMap());
    }

    @Override
    public boolean addNode(N node) {
        Objects.requireNonNull(node, "node");
        if (containsNode(node)) {
            return false;
        }
        addNodeInternal(node);
        return true;
    }

    @Override
    public void putEdge(N nodeU, N nodeV) {
        edges = null;
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

    @Override
    public boolean removeNode(N node) {
        Objects.requireNonNull(node, "node");
        nodes = null;

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

    @Override
    public Object removeEdge(N nodeU, N nodeV) {
        Objects.requireNonNull(nodeU, "nodeU");
        Objects.requireNonNull(nodeV, "nodeV");
        edges = null;

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

    @Override
    public Set<N> nodes() {
        if (CollectionUtils.isEmpty(nodes)) {
            nodes = Collections.unmodifiableSet(nodeMap.keySet());
        }
        return nodes;
    }

    @Override
    public Set<DirectedGraphEdge<N>> edges() {
        if (CollectionUtils.isEmpty(edges)) {
            Set<DirectedGraphEdge<N>> result = Sets.newHashSet();
            nodeMap.forEach((node, value) -> value.successors()
                    .forEach(successor -> result.add(DirectedGraphEdge.of(node, successor))));
            edges = Collections.unmodifiableSet(result);
        }

        return edges;
    }

    @Override
    public Set<N> predecessors(N node) {
        return checkedConnections(node).predecessors();
    }

    @Override
    public Set<N> successors(N node) {
        return checkedConnections(node).successors();
    }

    private DirectedGraphNode<N> checkedConnections(N node) {
        DirectedGraphNode<N> graphNode = nodeMap.get(node);
        if (graphNode == null) {
            Objects.requireNonNull(node, "node");
            throw new IllegalArgumentException("Node " + node + " is not an element of this graph.");
        }
        return graphNode;
    }

    private DirectedGraphNode<N> addNodeInternal(N node) {
        nodes = null;
        DirectedGraphNode<N> directedGraphNode = DirectedGraphNode.of();
        checkState(nodeMap.put(node, directedGraphNode) == null);
        return directedGraphNode;
    }

    protected boolean containsNode(N node) {
        return nodeMap.containsKey(node);
    }

    private void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
}
