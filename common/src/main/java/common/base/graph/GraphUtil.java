package common.base.graph;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static common.base.graph.GraphUtil.NodeVisitState.COMPLETE;
import static common.base.graph.GraphUtil.NodeVisitState.PENDING;

/**
 * create by zhaoju on 2018/09/05
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraphUtil {

    private static final String NODE_NOT_IN_GRAPH = "Node %s is not an element of this graph.";
    private static final String GRAPH_HAS_CYCLE = "Graph %s has cycle.";

    public static <N> Graph<N> copyOf(Graph<N> graph) {
        DirectedAcyclicGraph<N> copyGraph = DirectedAcyclicGraph.of();
        for (N node : graph.nodes()) {
            copyGraph.addNode(node);
        }
        for (DirectedGraphEdge<N> edge : graph.edges()) {
            copyGraph.addEdge(edge.source(), edge.target());
        }
        checkArgument(!hasCycle(graph), GRAPH_HAS_CYCLE, graph);
        return copyGraph;
    }

    public static boolean isEmpty(Graph graph) {
        return graph == null || graph.nodes().size() == 0;
    }

    public static boolean notEmpty(Graph graph) {
        return graph != null && graph.nodes().size() > 0;
    }

    /**
     * 使用 BFS 获取从node 出发的所有可达节点
     *
     * @param graph
     * @param node
     * @param <N>
     * @return
     */
    public static <N> Set<N> reachableNodes(Graph<N> graph, N node) {
        checkArgument(graph.nodes().contains(node), NODE_NOT_IN_GRAPH, node);
        Set<N> visitedNodes = Sets.newLinkedHashSet();
        Queue<N> queuedNodes = Lists.newLinkedList();
        visitedNodes.add(node);
        queuedNodes.offer(node);
        while (!queuedNodes.isEmpty()) {
            N currentNode = queuedNodes.poll();
            for (N successor : graph.successors(currentNode)) {
                if (visitedNodes.add(successor)) {
                    queuedNodes.offer(successor);
                }
            }
        }
        return Collections.unmodifiableSet(visitedNodes);
    }

    /**
     * 获取包含所有nodes节点的子图
     *
     * @param graph
     * @param nodes
     * @param <N>
     * @return
     */
    public static <N> Graph<N> subGraph(Graph<N> graph, Iterable<N> nodes) {
        DirectedAcyclicGraph<N> subGraph = DirectedAcyclicGraph.of();
        if (isEmpty(graph) || nodes == null || !nodes.iterator().hasNext()) {
            return subGraph;
        }
        for (N node : nodes) {
            subGraph.addNode(node);
        }
        for (N node : subGraph.nodes()) {
            for (N successorNode : graph.successors(node)) {
                if (subGraph.nodes().contains(successorNode)) {
                    subGraph.addEdge(node, successorNode);
                }
            }
        }
        checkArgument(!hasCycle(graph), GRAPH_HAS_CYCLE);
        return subGraph;
    }

    /**
     * 获得拓扑排序
     *
     * @param graph
     * @param <N>
     * @return
     */
    public static <N> List<N> topologicalSort(Graph<N> graph) {
        List<N> result = Lists.newArrayList();
        if (isEmpty(graph)) {
            return result;
        }
        Queue<N> queueNodes = Lists.newLinkedList();
        Map<N, Integer> integerMap = graph.nodes().stream()
                .collect(Collectors.toMap(node -> node, node -> graph.predecessors(node).size()));
        integerMap.forEach((node, num) -> {
            if (num == 0) {
                queueNodes.offer(node);
            }
        });

        while (!queueNodes.isEmpty()) {
            N currentNode = queueNodes.poll();
            result.add(currentNode);
            Set<N> successors = graph.successors(currentNode);
            for (N successor : successors) {
                integerMap.put(successor, integerMap.get(successor) - 1);
                if (integerMap.get(successor) == 0) {
                    queueNodes.offer(successor);
                }
            }
        }
        return result;
    }

    /**
     * nodeU 到 nodeV 之间有无通路
     *
     * @param graph
     * @param nodeU
     * @param nodeV
     * @param <N>
     * @return
     */
    public static <N> boolean hasRoute(Graph<N> graph, N nodeU, N nodeV) {
        Objects.requireNonNull(nodeU, "nodeU");
        Objects.requireNonNull(nodeV, "nodeV");
        Set<N> visitedSet = Sets.newHashSetWithExpectedSize(graph.nodes().size());

        return dfs(graph, visitedSet, nodeU, nodeV);
    }

    public static <N> boolean hasCycle(Graph<N> graph) {
        int numEdges = graph.edges().size();
        if (numEdges == 0) {
            return false;
        }

        Map<N, NodeVisitState> visitedNodes = Maps.newHashMapWithExpectedSize(graph.nodes().size());
        for (N node : graph.nodes()) {
            if (dfsHasCycle(graph, visitedNodes, node)) {
                return true;
            }
        }
        return false;
    }

    private static <N> boolean dfs(Graph<N> graph, Set<N> visitedSet, N nodeU, N nodeV) {
        if (nodeU.equals(nodeV)) {
            return true;
        }

        visitedSet.add(nodeU);
        Set<N> successors = graph.successors(nodeU);
        if (!CollectionUtils.isEmpty(successors)) {
            for (N successor : successors) {
                if (visitedSet.contains(successor)) {
                    continue;
                }
                if (dfs(graph, visitedSet, successor, nodeV)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static <N> boolean dfsHasCycle(Graph<N> graph, Map<N, NodeVisitState> visitedNodes, N node) {
        NodeVisitState nodeVisitState = visitedNodes.get(node);
        if (nodeVisitState == PENDING) {
            return true;
        }
        if (nodeVisitState == COMPLETE) {
            return false;
        }

        visitedNodes.put(node, PENDING);
        for (N successor : graph.successors(node)) {
            if (dfsHasCycle(graph, visitedNodes, successor)) {
                return true;
            }
        }
        visitedNodes.put(node, COMPLETE);
        return false;
    }

    enum NodeVisitState {
        /**
         * 正在遍历中的节点
         */
        PENDING,
        /**
         * 已经遍历结束的节点
         */
        COMPLETE
    }

    enum Presence {
        /**
         * 这条边是存在的
         */
        EDGE_EXISTS
    }
}
