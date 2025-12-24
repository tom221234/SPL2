package spl.lae;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import parser.ComputationNode;
import parser.ComputationNodeType;
import java.util.List;

/**
 * Unit tests for LinearAlgebraEngine class.
 * Tests LAE operations: task creation, computation resolution
 */
public class LinearAlgebraEngineTest {

    private LinearAlgebraEngine engine;

    @BeforeEach
    void setUp() {
        engine = new LinearAlgebraEngine(4);
    }

    @AfterEach
    void tearDown() {
        // Engine shuts down after run() completes
    }

    // ==================== Addition Tests ====================

    @Test
    void testAddition_2x2Matrices() {
        double[][] m1 = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        double[][] m2 = { { 5.0, 6.0 }, { 7.0, 8.0 } };

        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(m2);
        ComputationNode addNode = new ComputationNode(ComputationNodeType.ADD, List.of(left, right));

        ComputationNode result = engine.run(addNode);
        double[][] matrix = result.getMatrix();

        assertEquals(6.0, matrix[0][0]);
        assertEquals(8.0, matrix[0][1]);
        assertEquals(10.0, matrix[1][0]);
        assertEquals(12.0, matrix[1][1]);
    }

    @Test
    void testAddition_LargerMatrix() {
        double[][] m1 = { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 }, { 7.0, 8.0, 9.0 } };
        double[][] m2 = { { 9.0, 8.0, 7.0 }, { 6.0, 5.0, 4.0 }, { 3.0, 2.0, 1.0 } };

        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(m2);
        ComputationNode addNode = new ComputationNode(ComputationNodeType.ADD, List.of(left, right));

        ComputationNode result = engine.run(addNode);
        double[][] matrix = result.getMatrix();

        // All elements should sum to 10
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(10.0, matrix[i][j]);
            }
        }
    }

    // ==================== Multiplication Tests ====================

    @Test
    void testMultiplication_2x2Matrices() {
        double[][] m1 = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        double[][] m2 = { { 5.0, 6.0 }, { 7.0, 8.0 } };

        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(m2);
        ComputationNode mulNode = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(left, right));

        ComputationNode result = engine.run(mulNode);
        double[][] matrix = result.getMatrix();

        // [1,2] * [[5,6],[7,8]] = [1*5+2*7, 1*6+2*8] = [19, 22]
        // [3,4] * [[5,6],[7,8]] = [3*5+4*7, 3*6+4*8] = [43, 50]
        assertEquals(19.0, matrix[0][0]);
        assertEquals(22.0, matrix[0][1]);
        assertEquals(43.0, matrix[1][0]);
        assertEquals(50.0, matrix[1][1]);
    }

    @Test
    void testMultiplication_IdentityMatrix() {
        double[][] m1 = { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 } };
        double[][] identity = { { 1.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 } };

        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(identity);
        ComputationNode mulNode = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(left, right));

        ComputationNode result = engine.run(mulNode);
        double[][] matrix = result.getMatrix();

        // Multiplying by identity should give same matrix
        assertEquals(1.0, matrix[0][0]);
        assertEquals(2.0, matrix[0][1]);
        assertEquals(3.0, matrix[0][2]);
        assertEquals(4.0, matrix[1][0]);
        assertEquals(5.0, matrix[1][1]);
        assertEquals(6.0, matrix[1][2]);
    }

    // ==================== Negate Tests ====================

    @Test
    void testNegate_2x2Matrix() {
        double[][] m1 = { { 1.0, 2.0 }, { 3.0, 4.0 } };

        ComputationNode operand = new ComputationNode(m1);
        ComputationNode negNode = new ComputationNode(ComputationNodeType.NEGATE, List.of(operand));

        ComputationNode result = engine.run(negNode);
        double[][] matrix = result.getMatrix();

        assertEquals(-1.0, matrix[0][0]);
        assertEquals(-2.0, matrix[0][1]);
        assertEquals(-3.0, matrix[1][0]);
        assertEquals(-4.0, matrix[1][1]);
    }

    @Test
    void testNegate_WithNegativeNumbers() {
        double[][] m1 = { { -1.0, -2.0 }, { 3.0, -4.0 } };

        ComputationNode operand = new ComputationNode(m1);
        ComputationNode negNode = new ComputationNode(ComputationNodeType.NEGATE, List.of(operand));

        ComputationNode result = engine.run(negNode);
        double[][] matrix = result.getMatrix();

        assertEquals(1.0, matrix[0][0]);
        assertEquals(2.0, matrix[0][1]);
        assertEquals(-3.0, matrix[1][0]);
        assertEquals(4.0, matrix[1][1]);
    }

    // ==================== Transpose Tests ====================

    @Test
    void testTranspose_2x3Matrix() {
        double[][] m1 = { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 } };

        ComputationNode operand = new ComputationNode(m1);
        ComputationNode transNode = new ComputationNode(ComputationNodeType.TRANSPOSE, List.of(operand));

        ComputationNode result = engine.run(transNode);
        double[][] matrix = result.getMatrix();

        // 2x3 becomes 3x2
        assertEquals(3, matrix.length);
        assertEquals(2, matrix[0].length);

        assertEquals(1.0, matrix[0][0]);
        assertEquals(4.0, matrix[0][1]);
        assertEquals(2.0, matrix[1][0]);
        assertEquals(5.0, matrix[1][1]);
        assertEquals(3.0, matrix[2][0]);
        assertEquals(6.0, matrix[2][1]);
    }

    @Test
    void testTranspose_SquareMatrix() {
        double[][] m1 = { { 1.0, 2.0 }, { 3.0, 4.0 } };

        ComputationNode operand = new ComputationNode(m1);
        ComputationNode transNode = new ComputationNode(ComputationNodeType.TRANSPOSE, List.of(operand));

        ComputationNode result = engine.run(transNode);
        double[][] matrix = result.getMatrix();

        assertEquals(1.0, matrix[0][0]);
        assertEquals(3.0, matrix[0][1]);
        assertEquals(2.0, matrix[1][0]);
        assertEquals(4.0, matrix[1][1]);
    }

    // ==================== Nested Operations Tests ====================

    @Test
    void testNested_AddThenNegate() {
        double[][] m1 = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        double[][] m2 = { { 5.0, 6.0 }, { 7.0, 8.0 } };

        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(m2);
        ComputationNode addNode = new ComputationNode(ComputationNodeType.ADD, List.of(left, right));
        ComputationNode negNode = new ComputationNode(ComputationNodeType.NEGATE, List.of(addNode));

        ComputationNode result = engine.run(negNode);
        double[][] matrix = result.getMatrix();

        assertEquals(-6.0, matrix[0][0]);
        assertEquals(-8.0, matrix[0][1]);
        assertEquals(-10.0, matrix[1][0]);
        assertEquals(-12.0, matrix[1][1]);
    }

    @Test
    void testNested_MultiplyThenTranspose() {
        double[][] m1 = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        double[][] m2 = { { 1.0, 0.0, 0.0 }, { 0.0, 1.0, 1.0 } };

        ComputationNode left = new ComputationNode(m1);
        ComputationNode right = new ComputationNode(m2);
        ComputationNode mulNode = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(left, right));
        ComputationNode transNode = new ComputationNode(ComputationNodeType.TRANSPOSE, List.of(mulNode));

        ComputationNode result = engine.run(transNode);
        double[][] matrix = result.getMatrix();

        // Result should be transposed
        assertEquals(3, matrix.length);
        assertEquals(2, matrix[0].length);
    }

    // ==================== getWorkerReport Tests ====================

    @Test
    void testGetWorkerReport_ReturnsReport() {
        double[][] m1 = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        ComputationNode operand = new ComputationNode(m1);
        ComputationNode negNode = new ComputationNode(ComputationNodeType.NEGATE, List.of(operand));

        engine.run(negNode);
        String report = engine.getWorkerReport();

        assertNotNull(report);
        assertFalse(report.isEmpty());
    }
}
