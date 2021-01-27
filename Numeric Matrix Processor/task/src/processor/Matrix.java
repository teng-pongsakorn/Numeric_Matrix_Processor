package processor;

import java.util.Scanner;

public class Matrix {

    private double[][] data;

    private Matrix(int numRow, int numColumn) {
        data = new double[numRow][numColumn];
    }

    private Matrix(Matrix A) {
        data = new double[A.getNumRow()][A.getNumCol()];

        for (int i = 0; i < A.getNumRow(); i++) {
            for (int j = 0; j < A.getNumCol(); j++) {
                this.setValue(i, j, A.getValue(i, j));
            }
        }
    }

    public static Matrix fromInputStream() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter size of matrix: ");
        int numRow = scanner.nextInt();
        int numColumn = scanner.nextInt();
        Matrix matrix = new Matrix(numRow, numColumn);

        System.out.println("Enter second matrix: ");
        // fill values
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numColumn; j++) {
                matrix.setValue(i, j, scanner.nextDouble());
            }
        }
        scanner.close();
        return matrix;
    }

    public static Matrix empty() {
        return new Matrix(0, 0);
    }

    public void setValue(int row, int col, double value) {
        try {
            data[row][col] = value;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("ERROR: invalid (row, col)");
        }

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < data.length; row++) {
            for (Number num: data[row]) {
                builder.append(String.format("%.2f ", num));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public Matrix sum(Matrix other) {

        if (dimensionMismatch(this, other)) {
            throw new RuntimeException();
        }

        int numRow = getNumRow();
        int numCol = getNumCol();
        Matrix result = new Matrix(numRow, numCol);

        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                result.setValue(i, j, this.getValue(i, j) + other.getValue(i, j));
            }
        }
        return result;
    }

    private double getValue(int i, int j) {
        return data[i][j];
    }

    public static boolean dimensionMismatch(Matrix A, Matrix B) {
        return A.getNumRow() != B.getNumRow() || A.getNumCol() != B.getNumCol();
    }

    public int getNumCol() {
        return data[0].length;
    }

    public int getNumRow() {
        return data.length;
    }

    public Matrix scalarMultiply(double c) {
        Matrix result = new Matrix(this);
        for (int i = 0; i < result.getNumRow(); i++) {
            for (int j = 0; j < result.getNumCol(); j++) {
                result.setValue(i, j, c * result.getValue(i, j));
            }
        }
        return result;
    }

    public Matrix matmul(Matrix B) {
        if (!canMultiply(this, B)) {
            throw new RuntimeException();
        }
        int newRow = this.getNumRow();
        int newCol = B.getNumCol();
        Matrix C = new Matrix(newRow, newCol);

        for (int i = 0; i < newRow; i++) {
            for (int j = 0; j < newCol; j++) {
                double val = dotProduct(this, i, B, j);
                C.setValue(i, j, val);
            }
        }
        return C;
    }

    private double dotProduct(Matrix A, int rowA, Matrix B, int colB) {
        double result = 0;
        int length = B.getNumRow();
        for (int i = 0; i < length; i++) {
            result += A.getValue(rowA, i) * B.getValue(i, colB);
        }
        return result;
    }

    private boolean canMultiply(Matrix A, Matrix B) {
        return A.getNumCol() == B.getNumRow();
    }

    public Matrix transpose(int transposeType) {
        switch (transposeType) {
            case 1:
                return mainDiagonalTranspose();
            case 2:
                return sideDiagonalTranspose();
            case 3:
                return verticalLineTranspose();
            case 4:
                return horizontalLineTranspose();
            default:
                return null;
        }
    }

    private Matrix mainDiagonalTranspose() {
        int numRow = getNumRow();
        int numCol = getNumCol();
        Matrix transposeMatrix = new Matrix(numCol, numRow);

        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                transposeMatrix.setValue(j, i, getValue(i, j));
            }
        }
        return transposeMatrix;
    }

    private Matrix sideDiagonalTranspose() {
        int numRow = getNumRow();
        int numCol = getNumCol();
        Matrix transposeMatrix = new Matrix(numCol, numRow);
        for (int i = numCol - 1; i >= 0; i--) {
            int newRow = numCol - i - 1;
            for (int j = 0; j < numRow; j++) {
                transposeMatrix.setValue(newRow, numCol - j - 1, getValue(j, i));
            }
        }
        return transposeMatrix;
    }

    private Matrix verticalLineTranspose() {
        int numRow = getNumRow();
        int numCol = getNumCol();
        Matrix transposeMatrix = new Matrix(numRow, numCol);

        for (int i = 0; i < numCol / 2; i++) {
            // swap i=0 <-> i=numCol-1-i
            int col1 = i;
            int col2 = numCol - 1 - col1;
            for (int j = 0; j < numRow; j++) {
                transposeMatrix.setValue(j, col1, getValue(j, col2));
                transposeMatrix.setValue(j, col2, getValue(j, col1));
            }
        }
        return transposeMatrix;
    }

    private Matrix horizontalLineTranspose() {
        int numRow = getNumRow();
        int numCol = getNumCol();
        Matrix transposeMatrix = new Matrix(numRow, numCol);

        for (int i = 0; i < numRow / 2; i++) {
            // swap i=0 <-> i=numRow-1-i
            int row1 = i;
            int row2 = numRow - 1 - row1;
            for (int j = 0; j < numCol; j++) {
                transposeMatrix.setValue(row1, j, getValue(row2, j));
                transposeMatrix.setValue(row2, j, getValue(row1, j));
            }
        }
        return transposeMatrix;
    }

    public double determinant() {
        if (is2x2Matrix()) {
            double result = determinant2x2();
            return result;
        }
        if (!isSquareMatrix()) {
            throw new RuntimeException("not square matrix: no determinant");
        }

        double det = 0;
        int row = 0;
        for (int col = 0; col < getNumCol(); col++) {
            double minorResult = getMinor(row, col);
            double cofactorResult = getCofactor(row, col);
            det += minorResult * cofactorResult;
        }
        return det;
    }

    private double getCofactor(int row, int col) {
        return (row + col) % 2 == 0 ? this.getValue(row, col) : -this.getValue(row, col);
    }

    private double getMinor(int row, int col) {
        Matrix subMatrix = getSubmatrix(row, col);
        return subMatrix.determinant();
    }

    private Matrix getSubmatrix(int row, int col) {
        Matrix subMatrix = new Matrix(getNumRow()-1, getNumCol()-1);
        int curRow = 0;
        for (int r = 0; r < getNumRow(); r++) {
            if (r != row) {
                int curCol = 0;
                for (int c = 0; c < getNumCol(); c++) {
                    if (c != col) {
                        subMatrix.setValue(curRow, curCol, getValue(r, c));
                        curCol++;
                    }
                }
                curRow++;
            }
        }
        return subMatrix;
    }

    private boolean isSquareMatrix() {
        return getNumCol() == getNumRow();
    }

    private double determinant2x2() {
        return getValue(0, 0)*getValue(1, 1) - getValue(0, 1)*getValue(1,0);
    }

    private boolean is2x2Matrix() {
        return getNumCol()==2 && getNumRow()==2;
    }

    public Matrix inverse() {
        double det = determinant();
        if (!isSquareMatrix() || det==0) {
            throw new RuntimeException("no inverse matrix: not square or 0 determinant");
        }
        Matrix Adj = getAdjMatrix();
        Matrix A_inv = Adj.scalarMultiply(1./det);
        return A_inv;
    }

    private Matrix getAdjMatrix() {
        Matrix Adj = new Matrix(getNumRow(), getNumRow());
        for (int r = 0; r < getNumRow(); r++) {
            for (int c = 0; c < getNumCol(); c++) {
                Adj.setValue(r, c, getAdjValue(r, c));
            }
        }
        Matrix Adj_transpose = Adj.transpose(1);
        return Adj_transpose;
    }

    private double getAdjValue(int r, int c) {
        double cofactor = (r + c) % 2 == 0 ? 1 : -1;
        double det = getMinor(r, c);
        return cofactor * det;
    }

    public boolean isEmpty() {
        return getNumRow() == 0 && getNumCol() == 0;
    }
}
