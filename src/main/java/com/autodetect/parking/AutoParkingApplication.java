package com.autodetect.parking;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.autodetect.parking.service.FFTService;
import com.autodetect.parking.service.FFTServiceImpl;
import com.autodetect.parking.service.PopulateData;
import com.autodetect.parking.service.PopulateDataImpl;

/**
 * Using FFT to try perform an object detection
 */
public class AutoParkingApplication extends JPanel {
	private static final long serialVersionUID = 1130163759606342268L;

	int frameWidth = 256*5;
	int frameHight = 256*ImagePath.values().length;
	
	private BufferedImage imgTmp = new BufferedImage(frameWidth, frameHight, BufferedImage.TYPE_INT_ARGB);

	private Map<Integer, BufferedImage> imagesMap = new HashMap<Integer, BufferedImage>();

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public void paint(Graphics g) {
		/**
		 * Draw - wait message
		 */
		Graphics gTmp = imgTmp.getGraphics();
		gTmp.setColor(new Color(100, 100, 100));
		gTmp.drawString("PLEASE WAIT...", imgTmp.getWidth() / 2 - 70,
				imgTmp.getHeight() / 2 - 5);
		g.drawImage(imgTmp, 0, 0, null);
		/**
		 * Draw pictures
		 */

		paintImages(g);

	}

	public static void main(String[] args) {

		FFTService fftService = new FFTServiceImpl();
		PopulateData populateService = new PopulateDataImpl();

		final AutoParkingApplication a = new AutoParkingApplication();

		JFrame f = new JFrame("FFT Processing");
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final JScrollPane pane = new JScrollPane();
		f.add(pane);
		pane.setViewportView(a);

		f.pack();
		f.setVisible(true);
		a.repaint();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				a.setPreferredSize(new Dimension(a.frameWidth, a.frameHight));
				pane.revalidate();
			}
		});

		/**
		 * Load images from file
		 */
		
		int i=1;
		
		for(ImagePath path : ImagePath.values()) {

			/**
			 * Grayscale
			 */
//			BufferedImage img = populateService.populateGrayscaleImageData(path.getPath(), i);	
//			BufferedImage fftimg = populateService.populateFFTImageData(img.getHeight(), img.getWidth(), i, ImageType.GRAYSCALE);

			/**
			 * RGB24
			 */
			BufferedImage img = populateService.populateRGB24ImageData(path.getPath(), i);	
			BufferedImage fftimg = populateService.populateFFTImageData(img.getHeight(), img.getWidth(), i, ImageType.RGB24);

			/**
			 * HSV
			 */
//			BufferedImage img = populateService.populateHSVImageData(path.getPath(), i);	
//			BufferedImage fftimg = populateService.populateFFTImageData(img.getHeight(), img.getWidth(), i, ImageType.HSV);

			BufferedImage imgg = new BufferedImage(img.getWidth() * 5, img.getHeight(), BufferedImage.TYPE_INT_BGR);
			a.paintGraph(imgg.getGraphics(), 0, fftService.prepareGraphRe(populateService.getFFTData(i),img.getHeight(),img.getWidth()));
			a.paintGraph(imgg.getGraphics(), 2, fftService.prepareGraphIm(populateService.getFFTData(i),img.getHeight(),img.getWidth()));
			a.paintGraph(imgg.getGraphics(), 3, fftService.prepareGraph(  populateService.getFFTData(i),img.getHeight(),img.getWidth()));
			
			BufferedImage rowImage = new BufferedImage(img.getWidth() * 7, img.getHeight(),  BufferedImage.TYPE_INT_BGR);
			a.paintRowImage(rowImage.getGraphics(), img, fftimg, imgg);
			a.addRowImage(i, rowImage);
			
			i++;
			pane.revalidate();
		}

		pane.revalidate();
	}

	private void paintImages(Graphics g) {

		int shiftY = 0;
		int shiftX = 0;

		Set<Integer> keys = getImageKeys();
		
		for(int key : keys) {
			BufferedImage img =getRowImage(key);
			g.drawImage(img, shiftX, shiftY, null);
			shiftY+=img.getHeight();
		}
		
	}

	private void paintRowImage(Graphics g, BufferedImage img, BufferedImage imgFFT, BufferedImage imgGraph) {
		if (img == null || imgGraph == null) {
			return;
		}
		
		g.drawImage(img, 0, 0, null);
		if(imgFFT == null) {
			g.drawImage(imgGraph, img.getWidth(), 0, null);
		} else {	
			g.drawImage(imgFFT, img.getWidth(), 0, null);
			g.drawImage(imgGraph, img.getWidth()+imgFFT.getWidth(), 0, null);
		}
	}
	
	private void paintGraph(Graphics g, int index, int[] data) {
		if (data == null || data.length ==0) {
			return;
		}
		
		int stepWidth =5;
		g.setColor(Colors.getColorValue(index));

		int x0 = 0;
		int y0 = 255;

		for (int i = 1; i < 255; i++) {
			int x1 = x0 + (i - 1) * stepWidth;
			int a1 = data[i - 1] > 255 ? 255 : data[i - 1];
			int a2 = data[i] > 255 ? 255 : data[i];
			int y1 = y0 - a1;
			int x2 = x0 + i * stepWidth;
			int y2 = y0 - a2;

			g.drawLine(x1, y1, x2, y2);
		}

	}
	
	private synchronized void addRowImage(int key, BufferedImage img) {
		this.imagesMap.put(key, img);
	}

	public synchronized BufferedImage getRowImage(int key) {
		return imagesMap.get(key);
	}

	public synchronized Set<Integer> getImageKeys() {
		return imagesMap.keySet();
	}

}