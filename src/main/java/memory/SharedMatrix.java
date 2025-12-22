package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        vectors = new SharedVector[0];

    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            double[] temp = new double[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                temp[j] = matrix[i][j];
            }
            vectors[i] = new SharedVector(temp, VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            double[] temp = new double[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                temp[j] = matrix[i][j];
            }
            vectors[i] = new SharedVector(temp, VectorOrientation.ROW_MAJOR);
        }

    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        vectors = new SharedVector[matrix[0].length];
        for (int j = 0; j < matrix[0].length; j++) {
            double[] temp = new double[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                temp[i] = matrix[i][j];
            }
            vectors[j] = new SharedVector(temp, VectorOrientation.COLUMN_MAJOR);
        }
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        if (vectors.length == 0) {
            return new double[0][0];
        }
        double[][] result = new double[vectors.length][vectors[0].length()];
        for (int i = 0; i < vectors.length; i++) {
            for (int j = 0; j < vectors[i].length(); j++) {
                result[i][j] = vectors[i].get(j);
            }
        }
        return result;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        return vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        if (vectors.length == 0) {
            return null;
        }
        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for (SharedVector v : vecs) {
            v.readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for (SharedVector v : vecs) {
            v.readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for (SharedVector v : vecs) {
            v.writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for (SharedVector v : vecs) {
            v.writeUnlock();
        }
    }
}
