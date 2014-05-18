package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JComponent;


/**
 * Holds an image, and a resize ratio. Returns the image resized to the given ratio. 
 * 
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and
 * should be improved. Need to re-design, make it more modular,
 * adding documentation and improve code.
 * 
 * @author Asher Stern
 * @since May 24, 2011
 *
 */
@SuppressWarnings("serial")
public class ImagePaintingComponent extends JComponent
{
	
	public void paint(Graphics g)
	{
		if (this.image!=null)
			g.drawImage(this.image,0,0,null);
	}
	
	
	
	public BufferedImage getImage()
	{
		if (image == null || needToResize)
		{
			if (fullSizeImage != null)
				image = resizeImage(fullSizeImage, zoomRatio);
			needToResize = false;
		}
		return image;
	}

	/**
	 * @return the zoomRation
	 */
	public double getZoomRatio() {
		return zoomRatio;
	}
	
	
	/**
	 * @param zoomRatio the zoomRation to set
	 * @throws IOException 
	 */
	public void setZoomRatio(double zoomRatio) throws VisualTracingToolException {
		if (zoomRatio > 1 || zoomRatio <= 0)
			throw new VisualTracingToolException("the zoom ration must be between 0 and 1, I got " + zoomRatio); 
		this.zoomRatio = zoomRatio;
		needToResize = true;
	}

	/**
	 * @param fullSizeImage the fullSizeImage to set
	 */
	public void setFullSizeImage(BufferedImage fullSizeImage) {
		this.fullSizeImage = fullSizeImage;
		needToResize = true;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param image
	 * @param ratio
	 * @return
	 * @throws Exception 
	 */
	private BufferedImage resizeImage(BufferedImage image, double ratio)  {

		// Create new (blank) image of required (scaled) size

		BufferedImage resizedImage = new BufferedImage((int) (image.getWidth()*ratio), (int) (image.getHeight()*ratio), BufferedImage.TYPE_INT_ARGB);

		// Paint scaled version of image to new image

		Graphics2D graphics2D = resizedImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,	RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, (int) (image.getWidth()*ratio), (int) (image.getHeight()*ratio), null);

		// clean up

		graphics2D.dispose();
		return resizedImage;
	}

	private BufferedImage image;
	private BufferedImage fullSizeImage = null;
	/**
	 * 0 < zoomRation <= 1
	 */
	private double zoomRatio = 1;
	private boolean needToResize = false;

}
