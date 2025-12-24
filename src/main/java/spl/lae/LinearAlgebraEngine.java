package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        ComputationNode resolvable = computationRoot.findResolvable();
        while (resolvable != null) {
            this.loadAndCompute(resolvable);
            resolvable = computationRoot.findResolvable();
        }
        try {
            executor.shutdown();
        } catch (InterruptedException e) {
            return null;
        }
        return computationRoot;

    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        if(!(node.getNodeType() == ComputationNodeType.TRANSPOSE))
            leftMatrix.loadRowMajor(node.getChildren().getFirst().getMatrix());

        if (node.getChildren().size() > 1) {
            rightMatrix.loadRowMajor(node.getChildren().get(1).getMatrix());
        }
        List<Runnable> tasks;
        switch (node.getNodeType()) {
            case ADD:
                tasks = createAddTasks();
                break;
            case MULTIPLY:
                tasks = createMultiplyTasks();
                break;
            case NEGATE:
                tasks = createNegateTasks();
                break;
            case TRANSPOSE:
                leftMatrix.loadColumnMajor(node.getChildren().getFirst().getMatrix());
                tasks = createTransposeTasks();
                break;
            default:
                throw new IllegalArgumentException("Unknown operation");
        }
        executor.submitAll(tasks);
        node.resolve(leftMatrix.readRowMajor());
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int row = i;
            Runnable addTask = () -> {
                SharedVector leftRow = leftMatrix.get(row);
                SharedVector rightRow = rightMatrix.get(row);
                leftRow.add(rightRow);
            };
            tasks.add(addTask);
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int row = i;
            Runnable addTask = () -> {
                SharedVector leftRow = leftMatrix.get(row);
                leftRow.vecMatMul(rightMatrix);
            };
            tasks.add(addTask);
        }
        return tasks;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int row = i;
            Runnable addTask = () -> {
                SharedVector leftRow = leftMatrix.get(row);
                leftRow.negate();
            };
            tasks.add(addTask);
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int row = i;
            Runnable addTask = () -> {
                SharedVector leftRow = leftMatrix.get(row);
                leftRow.transpose();
            };
            tasks.add(addTask);
        }
        return tasks;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
