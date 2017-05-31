package mahbub1.umbc.eclipse.sensordatashared.representation;


/**
 * Created by mahbub on 5/25/17.
 */

public class MatrixF3x3 {
    private boolean colMaj = true;

    /**
     * The matrix.
     */
    public float[] matrix;

    /**
     * Instantiates a new matrixf4x4. The Matrix is assumed to be Column major, however you can change this by using the
     * setColumnMajor function to false and it will operate like a row major matrix.
     */
    public MatrixF3x3() {
        // The matrix is defined as float[column][row]
        this.matrix = new float[9];
        Matrix.setIdentityM(this.matrix, 0);
    }

    public void getAnglesFromRotationMatrix(float[] R) {

    }
}
