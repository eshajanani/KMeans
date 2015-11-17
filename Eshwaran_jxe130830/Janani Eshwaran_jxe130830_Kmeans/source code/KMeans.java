import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;


public class KMeans 
{
	static String  org_img_path =null;
  static  String comp_img_path=null;
    static double[] comp_ratio_vals = new double[6];
	public static void main(String [] args){
	if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	}
	for(int i=0;i<6;i++)
	{
	try{
		
		org_img_path= args[0];
		comp_img_path =args[2];
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
	    File originalFile =new File(org_img_path);
	     float originalBytes = (originalFile.length())/1024;
	     System.out.println("Original :" + originalBytes);

	     
	     File compressedFile=new File(comp_img_path);
	     float compressedBytes = (compressedFile.length())/1024;
	     System.out.println("Compressed : " +compressedBytes);
	     float comp_ratio =originalBytes/compressedBytes;
	     System.out.println("Compression Ratio : " + comp_ratio);
	     comp_ratio_vals[i]=comp_ratio;
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}	
	}
	System.out.println("COMP VALUES ARE: " );
	for(int j=0;j<6;j++)
	{
		System.out.println("Itreration no:" +j + " ratio :" +comp_ratio_vals[j]);
	}
	
	float mean=findMean(comp_ratio_vals);
	System.out.println("Mean is : "+mean);
	float variance=findVariance(comp_ratio_vals,mean);
	System.out.println("variance is : "+variance);
	
    }// end of main method
	static public float findMean(double[] a)
	{
		float mean=0,sum=0;
		for(int i=0;i<a.length;i++)
		{
			sum +=a[i];
		}
		mean=sum/a.length;
		return mean;
	}
	
	static public float findVariance(double[] a, double mean)
	{
		float variance=0,sum=0;
		for(int i=0;i<a.length;i++)
		{
			sum +=a[i]*a[i];
		}
		variance=(float)Math.pow (Math.sqrt((sum/a.length - (mean*mean))),2);
		return variance;
	}
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) throws IOException{
    	
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	Graphics2D g = kmeansImage.createGraphics();
	g.drawImage(originalImage, 0, 0, w,h , null);
	// Read rgb values from the image
	int[] rgb=new int[w*h];
	int count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		rgb[count++]=kmeansImage.getRGB(i,j);
	    }
	}
	// Call kmeans algorithm: update the rgb values
	rgb=kmeans(rgb,k);

 	System.out.println("Writing output");
	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	 
	return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static int[] kmeans(int[] rgb, int k){
    	System.out.println("Performing kmean");
    	int cluster[] = new int[rgb.length];
	int kmeans[] = new int[k];
	Random rn = new Random();
	int []newRGB= new int[rgb.length];
	// Find k random temporary mean values from rgb array
	for (int i=0;i<k;i++)
	{
		int temp = rn.nextInt((rgb.length)+1)+0;	
		kmeans[i]= rgb[temp];
		
	}
	// perform Kmeans
	for(int limit=0;limit <100;limit++)
	{
	 //	System.out.println("Limit:"+limit);
		// for every rgb values
		
			//Assign the cluster value to each rgb value
			// for each rgb value find the distance between current rgb value and each kmeans rgb value
			 for(int i=0;i<rgb.length;i++){
			    	float dist1=distance_cal((int)rgb[i],kmeans[0]);
			    	cluster[i]=0;
			 
			for(int j=1;j<k;j++)
			{
			float dist2= distance_cal(rgb[i],kmeans[j]);
				
				if(dist2<dist1)
				{
					cluster[i]=j;
					dist1=dist2;
				}// end of if
				
			
			}// end of for j - ever cluster
			
		}// end of for i - every rgb value
		
		
		// Update the new cluster value 
		
		// for every cluster fond the new mean 
		
		for(int i=0;i<k;i++)
		{
			int count=0;
			float red=0,green=0,blue=0;
			// for every rgb value 
			for(int j=0;j<rgb.length;j++)
			{
				// compare the rgb's cluster and check if they are equal
				if(cluster[j]==i)
				{
					Color c = new Color(rgb[j]);
					red= red+c.getRed();
					green=green+c.getGreen();
					blue=blue+c.getBlue();
					count++;
					
				}// end of if
				
			}// end of j- for ever rgb value
			
			if(count >0)
			{
				int new_red=(int) (red/count);
				int new_green=(int) (green/count);
				int new_blue=(int) (blue/count);
			Color new_color= new Color(new_red, new_green,new_blue );
			kmeans[i]= new_color.getRGB(); // update the new rgb value as new kmean for jth cluster.
				
			}
		}// end of for i-  every cluster
	 //	System.out.println("end of hardlimit");
		
	}// end of for limit
	
	
	// update new rgb value for every rgb
	
	for(int x=0;x<rgb.length;x++)
	{
		int index=cluster[x];
		newRGB[x]=kmeans[index]; // snehal- check here
	}
 	System.out.println("end of  kmean");
	return newRGB;
    }// end of k means function
    
    
    public static float distance_cal(int rgb, int kmean){
    	float distance=0;
    	Color a= new Color(rgb);
    	Color b= new Color(kmean);
    	int a_red=a.getRed();
		int a_blue=a.getBlue();
		int a_green=a.getGreen();
		
		int b_red=b.getRed();
		int b_blue=b.getBlue();
		int b_green=b.getGreen();
		
		int r=a_red-b_red;
		
		int g=a_green-b_green;
		
		int bl=a_blue-b_blue;
			
		distance= (float) Math.sqrt((Math.pow(r,2)+Math.pow(g,2)+Math.pow(bl, 2)));
    	return distance;
    }

} // end of class KMeans
