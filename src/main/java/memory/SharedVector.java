package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        lock.readLock().lock();
        try {
            return vector[index];
        } finally {
            lock.readLock().unlock();
        }
    }

    public int length() {
        // TODO: return vector length
        lock.readLock().lock();
        try {
            return vector.length;
        } finally {
            lock.readLock().unlock();
        }
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        lock.readLock().lock();
        try {
            return orientation;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();

    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();

    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        lock.writeLock().lock();
        try {
            if (this.orientation == VectorOrientation.ROW_MAJOR)
                this.orientation = VectorOrientation.COLUMN_MAJOR;
            else
                this.orientation = VectorOrientation.ROW_MAJOR;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        if (this.length() != other.length()) // checks if the vectors in the same size
            throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
        try {
            this.writeLock();
            other.readLock();
            for (int i = 0; i < vector.length; i++) {
                this.vector[i] += other.vector[i];
            }
        } finally {
            other.readUnlock();
            this.writeUnlock();
        }
    }

    public void negate() {
        // TODO: negate vector
        lock.writeLock().lock();
        try {
            for (int i = 0; i < vector.length; i++) {
                vector[i] = -vector[i];
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        if (this.length() != other.length()) // checks if the vectors in the same size
            throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
        double result = 0;
        try {
            this.readLock();
            other.readLock();

            for (int i = 0; i < vector.length; i++) {
                result += this.vector[i] * other.vector[i];
            }
        } finally {
            this.readUnlock();
            other.readUnlock();
        }
        return result;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        this.writeLock();
        try {
            if (vector.length != matrix.length())
                throw new IllegalArgumentException("Illegal operation: dimensions mismatch");

            double[] temp = new double[matrix.get(0).length()];
            for (int i = 0; i < matrix.get(0).length(); i++) {
                double sum = 0;
                for (int j = 0; j < matrix.length(); j++) {
                    sum += this.vector[j] * matrix.get(j).get(i);
                }
                temp[i] = sum;
            }
            this.vector = temp;
        } finally {
            this.writeUnlock();
        }

    }
}
