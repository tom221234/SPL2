package parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for ComputationNode class.
 * Tests node operations: findResolvable, associativeNesting, resolve
 */
public class ComputationNodeTest {

    // ==================== Constructor Tests ====================

    @Test
    void testMatrixConstructor_SetsTypeToMatrix() {
        double[][] data = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        ComputationNode node = new ComputationNode(data);
        assertEquals(ComputationNodeType.MATRIX, node.getNodeType());
    }

    @Test
    void testOperatorConstructor_SetsCorrectType() {
        double[][] m1 = { { 1.0 } };
        ComputationNode child = new ComputationNode(m1);

        ComputationNode addNode = new ComputationNode("+", List.of(child, child));
        assertEquals(ComputationNodeType.ADD, addNode.getNodeType());

        ComputationNode mulNode = new ComputationNode("*", List.of(child, child));
        assertEquals(ComputationNodeType.MULTIPLY, mulNode.getNodeType());

        ComputationNode negNode = new ComputationNode("-", List.of(child));
        assertEquals(ComputationNodeType.NEGATE, negNode.getNodeType());

        ComputationNode transNode = new ComputationNode("T", List.of(child));
        assertEquals(ComputationNodeType.TRANSPOSE, transNode.getNodeType());
    }

    @Test
    void testOperatorConstructor_UnknownOperator_Throws() {
        double[][] m1 = { { 1.0 } };
        ComputationNode child = new ComputationNode(m1);

        assertThrows(IllegalArgumentException.class, () -> new ComputationNode("X", List.of(child)));
    }

    // ==================== getMatrix Tests ====================

    @Test
    void testGetMatrix_ReturnsMatrix() {
        double[][] data = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        ComputationNode node = new ComputationNode(data);

        double[][] result = node.getMatrix();
        assertArrayEquals(data[0], result[0]);
        assertArrayEquals(data[1], result[1]);
    }

    @Test
    void testGetMatrix_NonMatrixNode_Throws() {
        double[][] m1 = { { 1.0 } };
        ComputationNode child = new ComputationNode(m1);
        ComputationNode addNode = new ComputationNode("+", List.of(child, child));

        assertThrows(IllegalStateException.class, addNode::getMatrix);
    }

    // ==================== findResolvable Tests ====================

    @Test
    void testFindResolvable_MatrixNode_ReturnsNull() {
        double[][] data = { { 1.0 } };
        ComputationNode node = new ComputationNode(data);
        assertNull(node.findResolvable());
    }

    @Test
    void testFindResolvable_SimpleOperation_ReturnsSelf() {
        double[][] m1 = { { 1.0 } };
        double[][] m2 = { { 2.0 } };
        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(m2);
        ComputationNode addNode = new ComputationNode("+", List.of(left, right));

        assertEquals(addNode, addNode.findResolvable());
    }

    @Test
    void testFindResolvable_NestedOperation_ReturnsDeepest() {
        double[][] m1 = { { 1.0 } };
        double[][] m2 = { { 2.0 } };
        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(m2);
        ComputationNode addNode = new ComputationNode("+", List.of(left, right));
        ComputationNode negNode = new ComputationNode("-", List.of(addNode));

        // The deepest resolvable is the add node
        assertEquals(addNode, negNode.findResolvable());
    }

    // ==================== associativeNesting Tests ====================

    @Test
    void testAssociativeNesting_TwoOperands_NoChange() {
        double[][] m1 = { { 1.0 } };
        double[][] m2 = { { 2.0 } };
        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(m2);
        ComputationNode addNode = new ComputationNode("+", List.of(left, right));

        addNode.associativeNesting();

        assertEquals(2, addNode.getChildren().size());
    }

    @Test
    void testAssociativeNesting_ThreeOperands_NestsToBinary() {
        double[][] m1 = { { 1.0 } };
        double[][] m2 = { { 2.0 } };
        double[][] m3 = { { 3.0 } };
        ComputationNode n1 = new ComputationNode(m1);
        ComputationNode n2 = new ComputationNode(m2);
        ComputationNode n3 = new ComputationNode(m3);
        ComputationNode addNode = new ComputationNode("+", new java.util.ArrayList<>(List.of(n1, n2, n3)));

        addNode.associativeNesting();

        // Should now be ((n1 + n2) + n3) - binary
        assertEquals(2, addNode.getChildren().size());
        assertEquals(ComputationNodeType.ADD, addNode.getChildren().get(0).getNodeType());
    }

    @Test
    void testAssociativeNesting_FourOperands_NestsCorrectly() {
        double[][] m = { { 1.0 } };
        ComputationNode n1 = new ComputationNode(m);
        ComputationNode n2 = new ComputationNode(m);
        ComputationNode n3 = new ComputationNode(m);
        ComputationNode n4 = new ComputationNode(m);
        ComputationNode mulNode = new ComputationNode("*", new java.util.ArrayList<>(List.of(n1, n2, n3, n4)));

        mulNode.associativeNesting();

        // Should be (((n1 * n2) * n3) * n4)
        assertEquals(2, mulNode.getChildren().size());
    }

    // ==================== resolve Tests ====================

    @Test
    void testResolve_ChangesTypeToMatrix() {
        double[][] m1 = { { 1.0 } };
        double[][] m2 = { { 2.0 } };
        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(m2);
        ComputationNode addNode = new ComputationNode("+", List.of(left, right));

        double[][] result = { { 3.0 } };
        addNode.resolve(result);

        assertEquals(ComputationNodeType.MATRIX, addNode.getNodeType());
        assertNull(addNode.getChildren());
        assertArrayEquals(result[0], addNode.getMatrix()[0]);
    }
}
