package memory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SharedMatrix class.
 * Tests matrix operations: loadRowMajor, loadColumnMajor, readRowMajor
 */
public class SharedMatrixTest {

    private SharedMatrix matrix;

    @BeforeEach
    void setUp() {
        matrix = new SharedMatrix();
    }

    // ==================== Constructor Tests ====================

    @Test
    void testDefaultConstructor_CreatesEmptyMatrix() {
        SharedMatrix empty = new SharedMatrix();
        assertEquals(0, empty.length());
    }

    @Test
    void testConstructor_CreatesMatrixFromArray() {
        double[][] data = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        SharedMatrix m = new SharedMatrix(data);
        assertEquals(2, m.length());
        assertEquals(VectorOrientation.ROW_MAJOR, m.getOrientation());
    }

    // ==================== loadRowMajor Tests ====================

    @Test
    void testLoadRowMajor_LoadsCorrectly() {
        double[][] data = { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 } };
        matrix.loadRowMajor(data);

        assertEquals(2, matrix.length());
        assertEquals(1.0, matrix.get(0).get(0));
        assertEquals(2.0, matrix.get(0).get(1));
        assertEquals(3.0, matrix.get(0).get(2));
        assertEquals(4.0, matrix.get(1).get(0));
    }

    @Test
    void testLoadRowMajor_SetsCorrectOrientation() {
        double[][] data = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        matrix.loadRowMajor(data);
        assertEquals(VectorOrientation.ROW_MAJOR, matrix.getOrientation());
    }

    @Test
    void testLoadRowMajor_SingleRow() {
        double[][] data = { { 1.0, 2.0, 3.0, 4.0 } };
        matrix.loadRowMajor(data);
        assertEquals(1, matrix.length());
        assertEquals(4, matrix.get(0).length());
    }

    @Test
    void testLoadRowMajor_SingleColumn() {
        double[][] data = { { 1.0 }, { 2.0 }, { 3.0 } };
        matrix.loadRowMajor(data);
        assertEquals(3, matrix.length());
        assertEquals(1, matrix.get(0).length());
    }

    // ==================== loadColumnMajor Tests ====================

    @Test
    void testLoadColumnMajor_LoadsCorrectly() {
        double[][] data = { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 } };
        matrix.loadColumnMajor(data);

        // Column major: vectors are columns of the original matrix
        assertEquals(3, matrix.length()); // 3 columns
        assertEquals(VectorOrientation.COLUMN_MAJOR, matrix.getOrientation());
    }

    @Test
    void testLoadColumnMajor_SetsCorrectOrientation() {
        double[][] data = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        matrix.loadColumnMajor(data);
        assertEquals(VectorOrientation.COLUMN_MAJOR, matrix.getOrientation());
    }

    // ==================== readRowMajor Tests ====================

    @Test
    void testReadRowMajor_ReturnsCorrectData() {
        double[][] original = { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 } };
        matrix.loadRowMajor(original);
        double[][] result = matrix.readRowMajor();

        assertArrayEquals(original[0], result[0]);
        assertArrayEquals(original[1], result[1]);
    }

    @Test
    void testReadRowMajor_EmptyMatrix() {
        double[][] result = matrix.readRowMajor();
        assertEquals(0, result.length);
    }

    @Test
    void testReadRowMajor_PreservesData() {
        double[][] data = { { 1.0, 2.0 }, { 3.0, 4.0 }, { 5.0, 6.0 } };
        matrix.loadRowMajor(data);
        double[][] result = matrix.readRowMajor();

        assertEquals(3, result.length);
        assertEquals(2, result[0].length);
        assertEquals(1.0, result[0][0]);
        assertEquals(6.0, result[2][1]);
    }

    // ==================== get Tests ====================

    @Test
    void testGet_ReturnsCorrectVector() {
        double[][] data = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        matrix.loadRowMajor(data);

        SharedVector row0 = matrix.get(0);
        assertEquals(1.0, row0.get(0));
        assertEquals(2.0, row0.get(1));

        SharedVector row1 = matrix.get(1);
        assertEquals(3.0, row1.get(0));
        assertEquals(4.0, row1.get(1));
    }

    // ==================== length Tests ====================

    @Test
    void testLength_ReturnsCorrectValue() {
        double[][] data = { { 1.0, 2.0 }, { 3.0, 4.0 }, { 5.0, 6.0 } };
        matrix.loadRowMajor(data);
        assertEquals(3, matrix.length());
    }

    // ==================== Edge Cases ====================

    @Test
    void testLargeMatrix() {
        int size = 100;
        double[][] data = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                data[i][j] = i * size + j;
            }
        }
        matrix.loadRowMajor(data);

        assertEquals(size, matrix.length());
        assertEquals(0.0, matrix.get(0).get(0));
        assertEquals(size * size - 1, matrix.get(size - 1).get(size - 1));
    }

    @Test
    void testReloadMatrix_ReplacesOldData() {
        double[][] data1 = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        matrix.loadRowMajor(data1);

        double[][] data2 = { { 5.0, 6.0, 7.0 } };
        matrix.loadRowMajor(data2);

        assertEquals(1, matrix.length());
        assertEquals(5.0, matrix.get(0).get(0));
    }
}
