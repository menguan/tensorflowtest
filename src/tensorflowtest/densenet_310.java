package tensorflowtest;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.types.UInt8;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import javax.imageio.ImageIO;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;  
public class densenet_310 {
	private static final int IMG_WIDTH = 224;
    private static final int IMG_HEIGHT = 224;
    private static final String MODELPATH="model";
    private static final String MODELTAG="test";
    private static String FILE_PATH;
    
    //private static final int lanczosSize=1;
    private Session session;
    private Tensor p1;
    private Tensor p2;
	public densenet_310() 
	{
		session = SavedModelBundle.load(MODELPATH,MODELTAG).session();
		p1=Tensor.create((float)1);
	    p2=Tensor.create(false);
	}
	public void setimage(String imgpath)
	{
		FILE_PATH=imgpath;
	}
	public float[] getresult() throws Exception {
		    
		     Tensor inputimage = null;
		     inputimage = Tensor.create(getpixel(FILE_PATH));
		     
		     
		     List<Tensor<?>> outputs = 
		    		 session.runner()
		    		 .feed("inputs:0",inputimage)
		    		 .feed("keep_prob:0",p1)
		    		 .feed("is_training:0",p2)
		    		 .fetch("prediction:0")
		    		 .run();
		     float[][] result = new float[1][2];
		     float[] res=new float[2];
	         for (Tensor s : outputs) 
	             s.copyTo(result);
	         res[0]=result[0][0];res[1]=result[0][1];
	         return res;
		     
	 }
	
	public float[] getpixel(String filename) throws Exception {
	    
		
		
	    BufferedImage img=zoomImage(filename);
	   
	    byte[] bytedata = ((DataBufferByte) img.getData().getDataBuffer()).getData();
	    
	    float[] data = new float[150528];
	    
	    for (int i = 0; i < data.length; i ++) //byte转float
	    	{
	    	data[i]=bytedata[i]&0xff;
	    	data[i]=data[i]/255;}
	    bgr2rgb(data);//BGR转RGB
	    
	    
	    img.flush();
	    return data;
	  }
	public void bgr2rgb(float[] data) {
	    for (int i = 0; i < data.length; i += 3) {
	      float tmp = data[i];
	      data[i] = data[i + 2];
	      data[i + 2] = tmp;
	    }
	}
	
	
	/*
	private static BufferedImage resizeImage(BufferedImage originalImage, int type) throws IOException{
		    BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		    Graphics2D g = resizedImage.createGraphics();
		    g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		    g.dispose();
		    return resizedImage;
	}
	
	*/
	 public BufferedImage zoomImage(String src) {  
	        //System.out.println(src);
	        BufferedImage result = null;  
	  
	        try {  
	            File srcfile = new File(src);  
	            if (!srcfile.exists()) {  
	                System.out.println("文件不存在");  
	                  
	            }  
	            BufferedImage im = ImageIO.read(srcfile);  
	  
	            
	            int width = im.getWidth();  
	            int height = im.getHeight();  
	              
	           
	            int toWidth = IMG_WIDTH;  
	            int toHeight = IMG_HEIGHT;  
	  
	            result = new BufferedImage(toWidth, toHeight,  
	                    BufferedImage.TYPE_3BYTE_BGR);  
	           
	            result.getGraphics().drawImage(  
	                    im.getScaledInstance(toWidth, toHeight,  
	                            java.awt.Image.SCALE_SMOOTH), 0, 0,null);  
	              
	  
	        } catch (Exception e) {  
	            System.out.println("创建缩略图发生异常" + e.getMessage());  
	        }     
	        return result;  
	    } 
	    
	 /*
	 public float[] filter(BufferedImage src) {
		 	byte[] bytedata = ((DataBufferByte) src.getData().getDataBuffer()).getData();
			int width = src.getWidth();
			int height = src.getHeight();
			float ratio = width / 224;
			float rcp_ratio = 2.0f / ratio;
			float range2 = (float) Math.ceil(ratio * lanczosSize / 2);
			
			// destination image
			int dh = IMG_WIDTH;
			int dw = IMG_HEIGHT;
	 
			
			int[] inPixels = new int[width * height];
			float[] outPixels = new float[dw * dh *3];
			for(int i=0;i<width * height;i++)
			{
				inPixels[i]=bytedata[i*3] & 0xff |   
			            (bytedata[i*3+1] & 0xff) << 8 |   
			            (bytedata[i*3+2] & 0xff) << 16 ;   
			             
			}
			
			int index = 0;
			float fcy = 0, icy = 0, fcx = 0, icx = 0;
			for (int row = 0; row < dh; row++) {
				int ta = 0, tr = 0, tg = 0, tb = 0;
				fcy = (row + 0.5f) * ratio;
				icy = (float) Math.floor(fcy);
				for (int col = 0; col < dw; col++) {
					fcx = (col + 0.5f) * ratio;
					icx = (float) Math.floor(fcx);
	 
					float sumred = 0, sumgreen = 0, sumblue = 0;
					float totalWeight = 0;
					for (int subcol = (int) (icx - range2); subcol <= icx + range2; subcol++) {
						if (subcol < 0 || subcol >= width)
							continue;
						int ncol = (int) Math.floor(1000 * Math.abs(subcol - fcx));
	 
						for (int subrow = (int) (icy - range2); subrow <= icy + range2; subrow++) {
							if (subrow < 0 || subrow >= height)
								continue;
							int nrow = (int) Math.floor(1000 * Math.abs(subrow - fcy));
							float weight = (float) getLanczosFactor(Math.sqrt(Math.pow(ncol * rcp_ratio, 2)
									+ Math.pow(nrow * rcp_ratio, 2)) / 1000);
							if (weight > 0) {
								index = (subrow * width + subcol);
								tr = (inPixels[index] >> 16) & 0xff;
								tg = (inPixels[index] >> 8) & 0xff;
								tb = inPixels[index] & 0xff;
								totalWeight += weight;
								sumred += weight * tr;
								sumgreen += weight * tg;
								sumblue += weight * tb;
							}
						}
					}
					index = row * dw + col;
					tr = (int) (sumred / totalWeight);
					tg = (int) (sumgreen / totalWeight);
					tb = (int) (sumblue / totalWeight);
					outPixels[index*3] = (clamp(tr) << 16) & 0xff;
					outPixels[index*3+1]=(clamp(tg) << 8)& 0xff;
					outPixels[index*3+2]=(clamp(tb))& 0xff;
					// clear for next pixel
					sumred = 0;
					sumgreen = 0;
					sumblue = 0;
					totalWeight = 0;
	 
				}
			}
			
			return outPixels;
		}
	 public static int clamp(int v)
		{
			return v > 255 ? 255 : (v < 0 ? 0 : v);
		}

	 private double getLanczosFactor(double distance) {
			if (distance > lanczosSize)
				return 0;
			distance *= Math.PI;
			if (Math.abs(distance) < 1e-16)
				return 1;
			double xx = distance / lanczosSize;
			return Math.sin(distance) * Math.sin(xx) / distance / xx;
		}
	 */
}
