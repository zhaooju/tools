package common.base.graph;

import java.util.Set;

/**
 * @author zhaoju
 * @date 2018/9/4 22:44
 */
public interface Graph<N> {
    boolean addNode(N node);

    void putEdgeValue(N nodeU, N nodeV);

    boolean removeNode(N node);

    Object removeEdge(N nodeU, N nodeV);

    Set<N> nodes();

    Set<DirectedGraphEdge<N>> edges();

    Set<N> predecessors(N node);

    Set<N> successors(N node);
}
