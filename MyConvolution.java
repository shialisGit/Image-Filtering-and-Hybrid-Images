import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

/**
 * MyConvolution class represents a single-band image processor that applies convolution to an input image.
 */
public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {
    private final float[][] kernel; // The convolution kernel

    /**
     * Constructs a MyConvolution object with the specified convolution kernel.
     *
     * @param kernel The convolution kernel to be applied to the image.
     */
    public MyConvolution(float[][] kernel) {
        this.kernel = kernel;
    }

    /**
     * Processes the input image using convolution with the specified kernel.
     *
     * @param image The input image to be processed.
     */
    @Override
    public void processImage(FImage image) {
        // Clone the input image to preserve the original
        FImage sourceImage = image.clone();

// Invert the convolution kernel
        float[][] kerInverse = invertMatrix(this.kernel);

// Iterate over each pixel in the input image
        for (int x = 0; x < image.getHeight(); x++) {
            for (int y = 0; y < image.getWidth(); y++) {
                float sum = 0;

                int kHeight = kerInverse.length;
                int kWidth = kerInverse[0].length;

                // Calculate the half-width and half-height of the kernel
                int halfKernelWidth = Math.floorDiv(kWidth, 2);
                int halfKernelHeight = Math.floorDiv(kHeight, 2);

                // Calculate the starting row and column for the convolution
                int firstRow = x - halfKernelHeight;
                int firstColumn = y - halfKernelWidth;

                // Iterate over each element in the inverted kernel
                for (int yOffset = 0; yOffset < kHeight; yOffset++) {
                    int modifiedCurrentRow = firstRow + yOffset;

                    for (int xOffset = 0; xOffset < kWidth; xOffset++) {
                        int currentX = firstColumn + xOffset;

                        float point = 0; // Initialize point to 0

                        // Check if the modifiedCurrent position is within the image boundaries
                        if (modifiedCurrentRow >= 0 && modifiedCurrentRow < image.getHeight()
                                && currentX >= 0 && currentX < image.getWidth()) {
                            point = sourceImage.pixels[modifiedCurrentRow][currentX];
                        }

                        // Sum the product of the inverted kernel and the corresponding image pixel
                        sum += kerInverse[yOffset][xOffset] * point;
                    }
                }

                // Modify the pixel value in the input image with the convolution result
                image.pixels[x][y] = sum;
            }
        }
    }

    /**
     * Inverts the given matrix by reversing its rows and columns.
     *
     * @param matrix The matrix to be inverted.
     * @return The inverted matrix.
     */
    private static float[][] invertMatrix(float[][] matrix) {
        int height = matrix.length;
        int width = matrix[0].length;
        float[][] invertMatrix = new float[height][width];

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                invertMatrix[x][y] = matrix[height - 1 - x][width - 1 - y];
            }
        }
        return invertMatrix;
    }
}
