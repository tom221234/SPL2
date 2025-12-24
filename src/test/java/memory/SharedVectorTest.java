package memory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SharedVector class.
 * Tests vector operations: add, negate, dot, transpose, vecMatMul
 */
public class SharedVectorTest {

    private SharedVector rowVector;
    private SharedVector columnVector;

    @BeforeEach
    void setUp() {
        rowVector = new SharedVector(new double[] { 1.0, 2.0, 3.0 }, VectorOrientation.ROW_MAJOR);
        columnVector = new SharedVector(new double[] { 4.0, 5.0, 6.0 }, VectorOrientation.COLUMN_MAJOR);
    }

    // ==================== Basic Operations ====================

    @Test
    void testGet_ReturnsCorrectElement() {
        assertEquals(1.0, rowVector.get(0));
        assertEquals(2.0, rowVector.get(1));
        assertEquals(3.0, rowVector.get(2));
    }

    @Test
    void testLength_ReturnsCorrectLength() {
        assertEquals(3, rowVector.length());
    }

    @Test
    void testGetOrientation_ReturnsCorrectOrientation() {
        assertEquals(VectorOrientation.ROW_MAJOR, rowVector.getOrientation());
        assertEquals(VectorOrientation.COLUMN_MAJOR, columnVector.getOrientation());
    }

    // ==================== Transpose ====================

    @Test
    void testTranspose_ChangesRowToColumn() {
        assertEquals(VectorOrientation.ROW_MAJOR, rowVector.getOrientation());
        rowVector.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, rowVector.getOrientation());
    }

    @Test
    void testTranspose_ChangesColumnToRow() {
        assertEquals(VectorOrientation.COLUMN_MAJOR, columnVector.getOrientation());
        columnVector.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, columnVector.getOrientation());
    }

    @Test
    void testTranspose_TwiceReturnsOriginal() {
        VectorOrientation original = rowVector.getOrientation();
        rowVector.transpose();
        rowVector.transpose();
        assertEquals(original, rowVector.getOrientation());
    }

    // ==================== Negate ====================

    @Test
    void testNegate_NegatesAllElements() {
        rowVector.negate();
        assertEquals(-1.0, rowVector.get(0));
        assertEquals(-2.0, rowVector.get(1));
        assertEquals(-3.0, rowVector.get(2));
    }

    @Test
    void testNegate_Twice_ReturnsOriginal() {
        SharedVector original = new SharedVector(new double[] { 1.0, 2.0, 3.0 }, VectorOrientation.ROW_MAJOR);
        rowVector.negate();
        rowVector.negate();
        assertEquals(original.get(0), rowVector.get(0));
        assertEquals(original.get(1), rowVector.get(1));
        assertEquals(original.get(2), rowVector.get(2));
    }



    // ==================== Add ====================

    @Test
    void testAdd_AddsVectorsCorrectly() {
        SharedVector v1 = new SharedVector(new double[] { 1.0, 2.0, 3.0 }, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[] { 4.0, 5.0, 6.0 }, VectorOrientation.ROW_MAJOR);
        v1.add(v2);
        assertEquals(5.0, v1.get(0));
        assertEquals(7.0, v1.get(1));
        assertEquals(9.0, v1.get(2));
    }

    @Test
    void testAdd_WithNegativeNumbers() {
        SharedVector v1 = new SharedVector(new double[] { 1.0, -2.0, 3.0 }, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[] { -1.0, 2.0, -3.0 }, VectorOrientation.ROW_MAJOR);
        v1.add(v2);
        assertEquals(0.0, v1.get(0));
        assertEquals(0.0, v1.get(1));
        assertEquals(0.0, v1.get(2));
    }

    @Test
    void testAdd_DimensionMismatch_ThrowsException() {
        SharedVector v1 = new SharedVector(new double[] { 1.0, 2.0 }, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[] { 1.0, 2.0, 3.0 }, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> v1.add(v2));
    }

    // ==================== Dot Product ====================

    @Test
    void testDot_CalculatesCorrectly() {
        SharedVector v1 = new SharedVector(new double[] { 1.0, 2.0, 3.0 }, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[] { 4.0, 5.0, 6.0 }, VectorOrientation.COLUMN_MAJOR);
        // 1*4 + 2*5 + 3*6 = 4 + 10 + 18 = 32
        assertEquals(32.0, v1.dot(v2));
    }

    @Test
    void testDot_WithZeroVector() {
        SharedVector v1 = new SharedVector(new double[] { 1.0, 2.0, 3.0 }, VectorOrientation.ROW_MAJOR);
        SharedVector zero = new SharedVector(new double[] { 0.0, 0.0, 0.0 }, VectorOrientation.COLUMN_MAJOR);
        assertEquals(0.0, v1.dot(zero));
    }

    @Test
    void testDot_DimensionMismatch_ThrowsException() {
        SharedVector v1 = new SharedVector(new double[] { 1.0, 2.0 }, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[] { 1.0, 2.0, 3.0 }, VectorOrientation.COLUMN_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> v1.dot(v2));
    }

    // ==================== vecMatMul ====================

    @Test
    void testVecMatMul_CalculatesCorrectly() {
        // Row vector [1, 2] × Matrix [[1,2,3],[4,5,6]] = [1*1+2*4, 1*2+2*5, 1*3+2*6] =
        // [9, 12, 15]
        SharedVector row = new SharedVector(new double[] { 1.0, 2.0 }, VectorOrientation.ROW_MAJOR);
        SharedMatrix matrix = new SharedMatrix(new double[][] {
                { 1.0, 2.0, 3.0 },
                { 4.0, 5.0, 6.0 }
        });
        row.vecMatMul(matrix);
        assertEquals(9.0, row.get(0));
        assertEquals(12.0, row.get(1));
        assertEquals(15.0, row.get(2));
    }

    @Test
    void testVecMatMul_IdentityMatrix() {
        SharedVector row = new SharedVector(new double[] { 1.0, 2.0, 3.0 }, VectorOrientation.ROW_MAJOR);
        SharedMatrix identity = new SharedMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 1.0, 0.0 },
                { 0.0, 0.0, 1.0 }
        });
        row.vecMatMul(identity);
        assertEquals(1.0, row.get(0));
        assertEquals(2.0, row.get(1));
        assertEquals(3.0, row.get(2));
    }

    @Test
    void testVecMatMul_DimensionMismatch_ThrowsException() {
        SharedVector row = new SharedVector(new double[] { 1.0, 2.0 }, VectorOrientation.ROW_MAJOR);
        SharedMatrix matrix = new SharedMatrix(new double[][] {
                { 1.0, 2.0 },
                { 3.0, 4.0 },
                { 5.0, 6.0 }
        });
        assertThrows(IllegalArgumentException.class, () -> row.vecMatMul(matrix));
    }

    // ==================== Locking Tests ====================

    @Test
    void testReadLock_AllowsMultipleReaders() throws InterruptedException {
        SharedVector vec = new SharedVector(new double[] { 1.0, 2.0, 3.0 }, VectorOrientation.ROW_MAJOR);

        Thread t1 = new Thread(() -> {
            vec.readLock();
            try {
                Thread.sleep(50);
                assertEquals(1.0, vec.get(0));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                vec.readUnlock();
            }
        });

        Thread t2 = new Thread(() -> {
            vec.readLock();
            try {
                assertEquals(2.0, vec.get(1));
            } finally {
                vec.readUnlock();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
