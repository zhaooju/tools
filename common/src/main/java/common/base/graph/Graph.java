package common.base.graph;

import java.util.Set;

/**
 * @author zhaoju
 * @date 2018/9/4 22:44
 */
public interface Graph<N> {
    /**
     * 添加一个新的节点
     *
     * @param node
     * @return
     */
    boolean addNode(N node);

    /**
     * 添加一条从nodeU指向nodeV的边
     *
     * @param nodeU
     * @param nodeV
     */
    void putEdge(N nodeU, N nodeV);

    /**
     * 删除节点node
     *
     * @param node
     * @return
     */
    boolean removeNode(N node);

    /**
     * 删除一条从nodeU指向nodeV的边
     *
     * @param nodeU
     * @param nodeV
     * @return
     */
    Object removeEdge(N nodeU, N nodeV);

    /**
     * 获取所有节点
     *
     * @return
     */
    Set<N> nodes();

    /**
     * 获取所有边
     *
     * @return
     */
    Set<DirectedGraphEdge<N>> edges();

    /**
     * 获取 node 的所有后置节点
     *
     * @param node
     * @return
     */
    Set<N> predecessors(N node);

    /**
     * 获取 node 的所有前置节点
     *
     * @param node
     * @return
     */
    Set<N> successors(N node);
}
