package processor;


import java.util.Scanner;

class ExitProgramException extends RuntimeException {

}

public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            printMenu();
            int choice = getUserInput();
            try {
                processUser(choice);
            } catch (ExitProgramException e) {
                break;
            } catch (RuntimeException e) {
                System.out.println("ERROR");
            }

        }
    }

    private static void printResult(String s) {
        System.out.printf("The result is:%n%s%n%n", s);
    }

    private static void processUser(int choice) {
        switch (choice) {
            case 0:
                throw new ExitProgramException();
            case 1:
                Matrix matrix1 = executeAddMatrix();
                printResult(matrix1.toString());
                break;
            case 2:
                Matrix matrix2 = executeScalarMatMul();
                printResult(matrix2.toString());
                break;
            case 3:
                Matrix matrix3 = executeMatMul();
                printResult(matrix3.toString());
                break;
            case 4:
                int transposeType = getTransposeType();
                Matrix matrix4 = executeTranspose(transposeType);
                printResult(matrix4.toString());
                break;
            case 5:
                double det = executeDeterminant();
                printResult(det + "");
                break;
            case 6:
                Matrix matrix6 = executeInverse();
                if (!matrix6.isEmpty()) {
                    printResult(matrix6.toString());
                }
                break;
            default:
        }
    }

    private static Matrix executeInverse() {
        Matrix A = Matrix.fromInputStream();
        try {
            Matrix A_inv = A.inverse();
            return A_inv;
        } catch (RuntimeException e) {
            System.out.println("This matrix doesn't have an inverse.");
        }
        return Matrix.empty();
    }

    private static double executeDeterminant() {
        Matrix A = Matrix.fromInputStream();
        double det = A.determinant();
        return det;
    }

    private static int getTransposeType() {
        System.out.println("1. Main diagonal\n" +
                "2. Side diagonal\n" +
                "3. Vertical line\n" +
                "4. Horizontal line\n" +
                "Your choice: ");
        return scanner.nextInt();
    }

    private static Matrix executeTranspose(int transposeType) {
        Matrix A = Matrix.fromInputStream();
        Matrix B = A.transpose(transposeType);
        return B;
    }

    private static Matrix executeMatMul() {
        Matrix A = Matrix.fromInputStream();
        Matrix B = Matrix.fromInputStream();
        Matrix C = A.matmul(B);
        return C;
    }

    private static Matrix executeScalarMatMul() {
        Matrix A = Matrix.fromInputStream();
        double b = readNum();
        Matrix C = A.scalarMultiply(b);
        return C;
    }

    private static Matrix executeAddMatrix() {
        Matrix A = Matrix.fromInputStream();
        Matrix B = Matrix.fromInputStream();
        Matrix C = A.sum(B);
        return C;
    }

    private static int getUserInput() {
        System.out.println("Your choice: ");
        return scanner.nextInt();
    }

    private static void printMenu() {
        System.out.println("1. Add matrices\n" +
                "2. Multiply matrix by a constant\n" +
                "3. Multiply matrices\n" +
                "4. Transpose matrix\n" +
                "5. Calculate a determinant\n" +
                "6. Inverse matrix\n" +
                "0. Exit");
    }

    private static double readNum() {
        System.out.println("Enter constant: ");
        return scanner.nextDouble();
    }
}
