package common.base.graph;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * create by zhaoju on 2018/09/05
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class DirectedGraphEdge<N> {

    private N nodeU;
    private N nodeV;

    static <N1> DirectedGraphEdge<N1> of(N1 nodeU, N1 nodeV) {
        return new DirectedGraphEdge<>(nodeU, nodeV);
    }

    N source() {
        return nodeU;
    }

    N target() {
        return nodeV;
    }

    @Override
    public String toString() {
        return "(" + source() + " -> " + target() + ")";
    }
}
