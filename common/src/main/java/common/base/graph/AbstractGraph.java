package common.base.graph;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * create by zhaoju on 2018/09/06
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractGraph<N> implements Graph<N> {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Graph)) {
            return false;
        }
        Graph<?> other = (Graph<?>) o;
        return nodes().equals(other.nodes())
                && edges().equals(other.edges());
    }

    @Override
    public int hashCode() {
        return edges().hashCode();
    }

    @Override
    public String toString() {
        return "DirectedAcyclicGraph{" +
                "nodes=" + nodes() +
                ", edges=" + edges() +
                '}';
    }
}
