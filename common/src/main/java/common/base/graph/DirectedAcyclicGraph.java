package common.base.graph;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static common.base.graph.GraphUtil.GRAPH_HAS_CYCLE;

/**
 * @author zhaoju
 * @date 2018/9/3 22:39
 */
public class DirectedAcyclicGraph<N> extends DirectedGraph<N> implements Graph<N> {

    private DirectedAcyclicGraph(int edgeCount, Map<N, DirectedGraphNode<N>> nodeMap) {
        super(edgeCount, nodeMap);
    }

    public static <N1> DirectedAcyclicGraph<N1> of() {
        return new DirectedAcyclicGraph<>(0, Maps.newLinkedHashMap());
    }

    public static <N> DirectedAcyclicGraph<N> copyOf(Graph<N> graph) {
        DirectedAcyclicGraph<N> copyGraph = DirectedAcyclicGraph.of();
        for (N node : graph.nodes()) {
            copyGraph.addNode(node);
        }
        for (DirectedGraphEdge<N> edge : graph.edges()) {
            copyGraph.addEdge(edge.source(), edge.target());
        }
        checkArgument(!GraphUtil.hasCycle(graph), GRAPH_HAS_CYCLE, graph);
        return copyGraph;
    }

    @Override
    public void putEdge(N nodeU, N nodeV) {
        Objects.requireNonNull(nodeU, "nodeU");
        Objects.requireNonNull(nodeV, "nodeV");

        if (super.containsNode(nodeU) && super.containsNode(nodeV) && GraphUtil.hasRoute(this, nodeV, nodeU)) {
            throw new IllegalStateException(String.format("DAG 图不能成环 nodeU ==> %s, nodeV ==> %s", nodeU, nodeV));
        }
        addEdge(nodeU, nodeV);
    }

    void addEdge(N nodeU, N nodeV) {
        super.putEdge(nodeU, nodeV);
    }
}
