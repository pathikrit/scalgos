// Matrix.java
// a simple java file for a standard class

package math.utils;

// Imports

/**
 * class Matrix
 */
public class Matrix {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    /** the number of rows. */
    private int        nRows;
    /** the number of columns. */
    private int        nCols;

    /** the element array of the matrix. */
    private double[][] el;

    // ===================================================================
    // constructors

    /** Main constructor */

    /**
     * Construct a new Matrix, with 1 row and 1 column, initialized to 1.
     */
    public Matrix() {
        this(1, 1);
    }

    /** init a new Matrix with nbRows rows, and nbCols columns. */
    public Matrix(int nbRows, int nbCols) {
        nRows = nbRows;
        nCols = nbCols;
        el = new double[nRows][nCols];
        setToIdentity();
    }

    /**
     * Construct a new Matrix, initialized with the given coefficients.
     */
    public Matrix(double[][] coef) {
        if (coef==null) {
            nRows = 1;
            nCols = 1;
            el = new double[nRows][nCols];
            setToIdentity();
            return;
        }

        nRows = coef.length;
        nCols = coef[0].length;
        el = new double[nRows][nCols];
        for (int r = 0; r<nRows; r++)
            for (int c = 0; c<nCols; c++)
                el[r][c] = coef[r][c];
    }

    // ===================================================================
    // accessors

    /**
     * return the coef. row and col are between 1 and the number of rows and
     * columns.
     */
    public double getCoef(int row, int col) {
        return el[row-1][col-1];
    }

    /** return the number of rows. */
    public int getRows() {
        return nRows;
    }

    /** return the number of columns. */
    public int getColumns() {
        return nCols;
    }

    /**
     * return true if the matrix is square, i.e. the number of rows equals the
     * number of columns.
     */
    public boolean isSquare() {
        return (nCols==nRows);
    }

    // ===================================================================
    // modifiers

    /**
     * set the coef to the given value. row and col are between 1 and the number
     * of rows and columns.
     */
    public void setCoef(int row, int col, double coef) {
        el[row-1][col-1] = coef;
    }

    // ===================================================================
    // computation methods

    /**
     * return the result of the multiplication of the matriux with another one.
     * The content of the matrix is not modified.
     */
    public Matrix multiplyWith(Matrix matrix) {

        // check sizes of the matrices
        if (nCols!=matrix.nRows) {
            System.out.println("Matrices size don't match !");
            return null;
        }
        double sum;

        Matrix m = new Matrix(nRows, matrix.nCols);

        for (int r = 0; r<m.nRows; r++)
            for (int c = 0; c<m.nCols; c++) {
                sum = 0;
                for (int i = 0; i<nCols; i++)
                    sum += el[r][i]*matrix.el[i][c];
                m.el[r][c] = sum;
            }

        return m;
    }

    /**
     * return the result of the multiplication of the matrix with the given
     * vector. The content of the matrix is not modified.
     */
    public double[] multiplyWith(double[] coefs) {

        if (coefs==null) {
            System.out.println("no data to compute");
            return null;
        }

        // check sizes of matrix and vector
        if (coefs.length!=nCols) {
            System.out.println("Matrices size don't match !");
            return null;
        }
        double sum;
        double[] res = new double[nRows];

        for (int r = 0; r<nRows; r++) {
            sum = 0;
            for (int c = 0; c<nCols; c++)
                sum += el[r][c]*coefs[c];
            res[r] = sum;
        }
        return res;
    }

    /**
     * return the result of the multiplication of the matrix with the given
     * vector. The content of the matrix is not modified.
     */
    public double[] multiplyWith(double[] src, double[] res) {

        if (src==null) {
            System.out.println("no data to compute");
            return null;
        }

        // check sizes of matrix and vector
        if (src.length!=nCols) {
            System.out.println("Matrices size don't match !");
            return null;
        }
        if (src.length!=res.length)
            res = new double[nRows];

        double sum;

        for (int r = 0; r<nRows; r++) {
            sum = 0;
            for (int c = 0; c<nCols; c++)
                sum += el[r][c]*src[c];
            res[r] = sum;
        }
        return res;
    }

    /** transpose the matrix, changing the inner coefficients. */
    public void transpose() {
        int tmp = nCols;
        nCols = nRows;
        nRows = tmp;
        double[][] oldData = el;
        el = new double[nRows][nCols];

        for (int r = 0; r<nRows; r++)
            for (int c = 0; c<nCols; c++)
                el[r][c] = oldData[c][r];
    }

    /**
     * get the transposed matrix, without changing the inner coefficients of the
     * original matrix.
     */
    public Matrix getTranspose() {
        Matrix mat = new Matrix(nCols, nRows);

        for (int r = 0; r<nRows; r++)
            for (int c = 0; c<nCols; c++)
                mat.el[c][r] = el[r][c];
        return mat;
    }

    /**
     * compute the solution of a linear system, using the Gauss-Jordan
     * algorithm. The inner coefficients of the matrix are not modified.
     */
    public double[] solve(double vector[]) {

        if (vector==null)
            throw new NullPointerException();
        if (vector.length!=nRows) {
            System.out.println("matrix and vector dimensions do not match!");
            return null;
        }
        if (nCols!=nRows) {
            System.out.println("Try to invert non square Matrix.");
            return null;
        }

        double[] res = new double[vector.length];
        for (int i = 0; i<nRows; i++)
            res[i] = vector[i];

        Matrix mat = new Matrix(el);

        int r, c; // row and column indices
        int p, r2; // pivot index, and secondary row index
        double pivot, tmp;

        // for each line of the matrix
        for (r = 0; r<nRows; r++) {

            p = r;
            // look for the first non-null pivot
            while ((Math.abs(mat.el[p][r])<1e-15)&&(p<=nRows))
                p++;

            if (p==nRows) {
                System.out.println("Degenerated linear system :");
                return null;
            }

            // swap the current line and the pivot
            for (c = 0; c<nRows; c++) {
                tmp = mat.el[r][c];
                mat.el[r][c] = mat.el[p][c];
                mat.el[p][c] = tmp;
            }

            // swap the corresponding values in the vector
            tmp = res[r];
            res[r] = res[p];
            res[p] = tmp;

            pivot = mat.el[r][r];

            // divide elements of the current line by the pivot
            for (c = r+1; c<nRows; c++)
                mat.el[r][c] /= pivot;
            res[r] /= pivot;
            mat.el[r][r] = 1;

            // update other lines, before current line...
            for (r2 = 0; r2<r; r2++) {
                pivot = mat.el[r2][r];
                for (c = r+1; c<nRows; c++)
                    mat.el[r2][c] -= pivot*mat.el[r][c];
                res[r2] -= pivot*res[r];
                mat.el[r2][r] = 0;
            }

            // and after current line
            for (r2 = r+1; r2<nRows; r2++) {
                pivot = mat.el[r2][r];
                for (c = r+1; c<nRows; c++)
                    mat.el[r2][c] -= pivot*mat.el[r][c];
                res[r2] -= pivot*res[r];
                mat.el[r2][r] = 0;
            }
        }

        return res;
    }

    // ===================================================================
    // general methods

    /**
     * Fill the matrix with zeros everywhere, except on the main diagonal,
     * filled with ones.
     */
    public void setToIdentity() {
        for (int r = 0; r<nRows; r++)
            for (int c = 0; c<nCols; c++)
                el[r][c] = 0;
        for (int i = Math.min(nRows, nCols)-1; i>=0; i--)
            el[i][i] = 1;
    }

    /**
     * return a String representation of the elements of the Matrix
     */
    @Override
    public String toString() {
        String res = new String("");
        res = res.concat("Matrix size : "+Integer.toString(nRows)+" rows and "
                +Integer.toString(nCols)+" columns.\n");
        for (int r = 0; r<nRows; r++) {
            for (int c = 0; c<nCols; c++)
                res = res.concat(Double.toString(el[r][c])).concat(" ");
            res = res.concat(new String("\n"));
        }
        return res;
    }
}