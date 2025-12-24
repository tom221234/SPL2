package spl.lae;

import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // TODO: main
        long startTime = System.nanoTime();
        int numThreads = Integer.parseInt(args[0]);
        String inputPath = args[1];
        String outputPath = args[2];

        try {
            InputParser parser = new InputParser();
            LinearAlgebraEngine engine = new LinearAlgebraEngine(numThreads);

            ComputationNode root = parser.parse(inputPath);
            root.associativeNesting(); // Convert n-ary operations to binary (left-associative)

            ComputationNode resultNode = engine.run(root);

            OutputWriter.write(resultNode.getMatrix(), outputPath);
            System.out.println(engine.getWorkerReport());

            long endTime = System.nanoTime();
            double seconds = (endTime - startTime) / 1_000_000_000.0;
            System.out.println(" time in seconds: " + seconds);

        } catch (Exception e) {
            try {
                OutputWriter.write(e.getMessage(), outputPath);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}