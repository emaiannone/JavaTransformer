import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        /* arg[0]: root path for input folder, ie, ~/data/methods/
         * arg[1]: root path for output folder, ie, ~/data/transforms/
         *
         * extracted single method of project should be in 'methods' folder
         * separate folder for each refactoring will be created in 'transforms' folder
         */

        if (args.length >= 2) {
            String inputPath = args[0];
            if (!inputPath.endsWith("/")) {
                inputPath += "/";
            }
            Common.mRootInputPath = inputPath;

            String outputPath = args[1];
            if (!outputPath.endsWith("/")) {
                outputPath += "/";
            }
            Common.mRootOutputPath = outputPath;

            if (args.length >= 3) {
                System.out.println("Found the intention to set a fixed seed for generating random numbers. Everytime a random number is generated, the generator will start from the given seed rather than using a random one.");
                Common.wantFixedSeed = true;
                try {
                    Common.seedForRandom = Long.parseLong(args[2]);
                } catch (NumberFormatException e) {
                    System.out.println("The supplied seed is not a valid long integer. Using the default one: " + Common.seedForRandom);
                }
            }

            inspectDataset();
        } else {
            String msg = "Error (args):" +
                    "\n\targ[0]: root path for input folder" +
                    "\n\targ[1]: root path for output folder";
            System.out.println(msg);
        }
    }

    private static void inspectDataset() {
        String input_dir = Common.mRootInputPath;
        ArrayList<File> javaFiles = new ArrayList<>(
                FileUtils.listFiles(
                        new File(input_dir),
                        new String[]{"java"},
                        true)
        );

        javaFiles.forEach((javaFile) -> {
            try {
                new VariableRenaming().inspectSourceCode(javaFile);
                new BooleanExchange().inspectSourceCode(javaFile);
                new LoopExchange().inspectSourceCode(javaFile);
                new SwitchToIf().inspectSourceCode(javaFile);
                new ReorderCondition().inspectSourceCode(javaFile);
                new PermuteStatement().inspectSourceCode(javaFile);
                new UnusedStatement().inspectSourceCode(javaFile);
                new LogStatement().inspectSourceCode(javaFile);
                new TryCatch().inspectSourceCode(javaFile);
            } catch (Exception ex) {
                System.out.println("Exception: " + javaFile);
                ex.printStackTrace();
            }
        });
    }
}
