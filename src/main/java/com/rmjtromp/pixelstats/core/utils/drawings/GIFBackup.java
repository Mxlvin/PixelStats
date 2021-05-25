package com.rmjtromp.pixelstats.core.utils.drawings;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.jetbrains.annotations.NotNull;

import com.rmjtromp.pixelstats.core.utils.NumberUtils;

public class GIFBackup implements Drawable<Integer>, Sizeable<Integer> {
	
	private int frame = 0;
	private boolean loop = true;
	private final List<Frame> frames = new ArrayList<>();
	private final InputStream input;
	
	public GIFBackup(InputStream input) throws IOException {
		this.input = input;
		ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
	    ImageInputStream stream = ImageIO.createImageInputStream(input);
	    reader.setInput(stream);

	    boolean f = false;
	    for(int i = 0;; i++) {
	    	try {
	    		BufferedImage image = reader.read(i);
		    	IIOMetadata metadata = reader.getImageMetadata(i);

		    	IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree("javax_imageio_gif_image_1.0");
		        IIOMetadataNode graphicalControlExtension = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
		        IIOMetadataNode imageDescriptor = (IIOMetadataNode) root.getElementsByTagName("ImageDescriptor").item(0);
		        int x = imageDescriptor != null && imageDescriptor.hasAttribute("imageLeftPosition") ? Integer.parseInt(imageDescriptor.getAttribute("imageLeftPosition")) : 0;
		        int y = imageDescriptor != null && imageDescriptor.hasAttribute("imageTopPosition") ? Integer.parseInt(imageDescriptor.getAttribute("imageTopPosition")) : 0;
//		        int width = imageDescriptor != null && imageDescriptor.hasAttribute("imageWidth") ? Integer.parseInt(imageDescriptor.getAttribute("imageWidth")) : 0;
//		        int height = imageDescriptor != null && imageDescriptor.hasAttribute("imageHeight") ? Integer.parseInt(imageDescriptor.getAttribute("imageHeight")) : 0;
		        int delay = graphicalControlExtension != null && graphicalControlExtension.hasAttribute("delayTime") ? Integer.parseInt(graphicalControlExtension.getAttribute("delayTime")) : 10;

		        Frame frame = new Frame(image, delay);
		        frame.setOffset(new Point<>(x, y));
		        frames.add(frame);
	    	} catch(IndexOutOfBoundsException e) {
	    		break;
	    	}
	    	if(f) break;
	    }
	}

	@Override
	public GIFBackup setSize(@NotNull Size<Integer> size) {
		frames.forEach(frame -> frame.setSize(size));
		return this;
	}

	@Override
	public Size<Integer> getSize() {
		return frames.stream().map(Frame::getSize).findFirst().orElse(new Size<>(0, 0));
	}
	
	public int getFrame() {
		return frame;
	}
	
	public GIFBackup setFrame(int frame) {
		this.frame = NumberUtils.constraintToRange(frame, 0, frames.size() - 1);
		return this;
	}
	
	public Frame getFrame(int frame) {
		return frames.get(NumberUtils.constraintToRange(frame, 0, frames.size() - 1));
	}
	
	public Frame getCurrentFrame() {
		return getFrame(getFrame());
	}
	
	public Frame next() {
		frame++;
		if(frame >= frames.size()) {
			if(loop) frame = 0;
			else frame = frames.size() - 1;
		}
		return getFrame(frame);
	}

	@Override
	public void draw(@NotNull Point<Integer> point) {
		getFrame(0).draw(point);
		if(getFrame() != 0) getCurrentFrame().draw(point);
		next();
	}
	
	private Thread drawingThread = null;
	public GIFBackup startDrawing() {
		drawingThread = new Thread(() -> {
			long lastTime = System.currentTimeMillis();
			while(true) {
				long now = System.currentTimeMillis();
				System.out.println((now - lastTime) + "ms");
				lastTime = now;
				try {
					Thread.sleep(10L);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		drawingThread.start();
		return this;
	}
	
	public GIFBackup stopDrawing() {
		return this;
	}
	
	public boolean loop() {
		return loop;
	}
	
	public GIFBackup setLoop(boolean loop) {
		this.loop = loop;
		return this;
	}
	
	public static class Frame extends Image {

		private int duration;
		
		public Frame(@NotNull BufferedImage image, int duration) {
			super(image);
			this.duration = duration;
		}
		
		public int getFrameDuration() {
			return duration;
		}
		
	}

}
