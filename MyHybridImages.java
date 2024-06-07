import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.resize.ResizeProcessor;
import MyConvolution;

import java.util.ArrayList;

public class MyHybridImages {

    /**
     * Creates a hybrid image by combining a low-pass image and a high-pass image.
     *
     * @param lowImage  The low-resolution input image.
     * @param lowSigma  The standard deviation for the low-pass filter.
     * @param highImage The high-resolution input image.
     * @param highSigma The standard deviation for the high-pass filter.
     * @return The resulting hybrid image.
     */
    public static MBFImage makeHybrid(MBFImage lowImage, float lowSigma, MBFImage highImage, float highSigma) {
        // Generate a low-pass image from the input lowImage
        MBFImage lowPassImage = makeLowPassImage(lowImage, lowSigma);

        // Generate a high-pass image from the input highImage
        MBFImage highPassImage = makeHighPassImage(highImage, highSigma);

        // Combine the low-pass and high-pass images to create the hybrid image
        // Return the resulting hybrid image
        return lowPassImage.add(highPassImage);
    }

    /**
     * Creates a low-pass image by applying a Gaussian filter to the input image.
     *
     * @param image The input image.
     * @param sigma The standard deviation for the Gaussian filter.
     * @return The resulting low-pass image.
     */
    public static MBFImage makeLowPassImage(MBFImage image, float sigma) {
        // Clone the input image to avoid modifying the original
        MBFImage lowPassImage = image.clone();

        // Generate a Gaussian kernel based on the specified sigma
        float[][] kernel = makeGaussianKernel(sigma);

        // Create a convolution processor with the Gaussian kernel
        MyConvolution myConvolution = new MyConvolution(kernel);

        // Apply the convolution to the low-pass image
        lowPassImage.processInplace(myConvolution);

        // Return the resulting low-pass image
        return lowPassImage;
    }

    /**
     * Creates a high-pass image by subtracting a low-pass image from the original image.
     *
     * @param image The input image.
     * @param sigma The standard deviation for the low-pass filter.
     * @return The resulting high-pass image.
     */
    public static MBFImage makeHighPassImage(MBFImage image, float sigma) {
        // Generate a low-pass image using the specified sigma
        MBFImage lowPassImage = makeLowPassImage(image, sigma);

        // Subtract the low-pass image from the original to obtain the high-pass image
        return image.subtract(lowPassImage);
    }

    /**
     * Displays the high-pass image by adjusting its intensity.
     *
     * @param highPassImage The high-pass image.
     * @return The adjusted high-pass image.
     */
    public static MBFImage displayHighPassImage(MBFImage highPassImage) {
        // Adjust the high-pass image to avoid inversion
        return highPassImage.addInplace(0.5f);
    }

    /**
     * Generates scaled images by repeatedly halving the input image's size.
     *
     * @param inputImage The input image.
     * @return The canvas containing the drawn scaled images.
     */
    public static MBFImage generateScaledImages(MBFImage inputImage) {
        // Initialize a list to store the scaled images
        ArrayList<MBFImage> scaledImages = new ArrayList<>();

        // Clone the input image to avoid modifying the original
        MBFImage temporaryImage = inputImage.clone();
        scaledImages.add(temporaryImage);

        // Generate three additional scaled images and add them to the list
        for (int iteration = 0; iteration < 3; iteration++) {
            temporaryImage = ResizeProcessor.halfSize(temporaryImage);
            scaledImages.add(temporaryImage);
        }

        // Calculate the total dimensions of the canvas to contain all images
        int totalCanvasHeight = 0;
        int totalCanvasWidth = 0;

        for (MBFImage image : scaledImages) {
            totalCanvasWidth += image.getWidth();
            totalCanvasHeight = Math.max(totalCanvasHeight, image.getHeight());
        }

        // Create a canvas image with the calculated dimensions
        MBFImage canvas = new MBFImage(totalCanvasWidth, totalCanvasHeight);

        // Initialize the x-coordinate for drawing on the canvas
        int currentX = 0;

        // Draw each scaled image onto the canvas
        for (int i = 0; i < scaledImages.size(); i++) {
            MBFImage currentImage = scaledImages.get(i);

            // Calculate the starting y-coordinate for drawing from the bottom
            int startY = totalCanvasHeight - currentImage.getHeight();

            // Draw the image on the canvas
            canvas.drawImage(currentImage, currentX, startY);

            // Increment x by the width of the current image
            currentX += currentImage.getWidth();
        }

        // Return the canvas containing the drawn images
        return canvas;
    }


    /**
     * Generates a Gaussian kernel for convolution with the given sigma.
     *
     * @param sigma The standard deviation of the Gaussian distribution.
     * @return The generated Gaussian kernel.
     */
    public static float[][] makeGaussianKernel(float sigma) {
        // Calculate the size of the kernel based on the provided sigma
        int size = (int) Math.floor(8 * sigma + 1);

        // Ensure that the size is an odd number
        if (size % 2 == 0) {
            size++;
        }

        // Initialize the kernel matrix
        float[][] kernel = new float[size][size];

        // Calculate the sum for normalization
        float sum = 0.0f;

        // Populate the kernel matrix with Gaussian values
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = i - size / 2;
                int y = j - size / 2;

                // Compute the Gaussian value for the current position
                kernel[i][j] = (float) Math.exp(-(x * x + y * y) / (2 * sigma * sigma));

                // Accumulate the value for normalization
                sum += kernel[i][j];
            }
        }

        // Normalize the kernel values
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] /= sum;
            }
        }

        // Return the generated Gaussian kernel
        return kernel;
    }
}
