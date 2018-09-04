package common.base.graph;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaoju
 * @date 2018/9/3 22:47
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class DirectedGraphNode<N> {
    private Map<N, Object> nodeValue;
    private int predecessorCount;
    private int successorCount;

    private static final Object PRED = new Object();

    public static <N1> DirectedGraphNode<N1> of() {
        return new DirectedGraphNode<>(Maps.newHashMap(), 0, 0);
    }

    /**
     * 返回后续节点
     *
     * @return
     */
    public Set<N> successors() {
        Set<N> resultSet = Sets.newHashSet();
        nodeValue.forEach((node, type) -> {
            if (isSuccessor(type)) {
                resultSet.add(node);
            }
        });
        return resultSet;
    }

    /**
     * 返回前驱节点
     *
     * @return
     */
    public Set<N> predecessors() {
        Set<N> resultSet = nodeValue.entrySet().stream()
                .filter(entry -> isPredecessor(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        return resultSet;
    }

    /**
     * 添加一个后继节点
     *
     * @param node
     * @param value
     * @return
     */
    public Presence addSuccessor(N node, Presence value) {
        Object previousValue = nodeValue.put(node, value);
        if (previousValue == null) {
            ++successorCount;
            return null;
        } else if (previousValue instanceof PredAndSucc) {
            nodeValue.put(node, new PredAndSucc(value));
            return (Presence) ((PredAndSucc) previousValue).getSuccessorValue();
        } else if (previousValue == PRED) {
            nodeValue.put(node, new PredAndSucc(value));
            ++successorCount;
            return null;
        }
        return (Presence) previousValue;
    }

    /**
     * 添加一个前驱节点
     *
     * @param node
     * @param unused
     */
    public void addPredecessor(N node, Presence unused) {
        Object previousValue = nodeValue.put(node, PRED);
        if (previousValue == null) {
            ++predecessorCount;
        } else if (previousValue instanceof PredAndSucc) {
            nodeValue.put(node, previousValue);
        } else if (previousValue != PRED) {
            nodeValue.put(node, new PredAndSucc(previousValue));
            ++predecessorCount;
        }
    }

    private boolean isSuccessor(Object value) {
        return value != PRED && value != null;
    }

    private boolean isPredecessor(Object value) {
        return value == PRED || value instanceof PredAndSucc;
    }

    enum Presence {
        EDGE_EXISTS
    }

    @Getter
    @AllArgsConstructor
    private static final class PredAndSucc {
        private final Object successorValue;

    }
}
