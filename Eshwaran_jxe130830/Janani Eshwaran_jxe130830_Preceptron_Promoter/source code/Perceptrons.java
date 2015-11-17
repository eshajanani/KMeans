import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class Perceptrons {
	static String TrainPath=null; // holds training data ham path folder name
	static String TestPath=null;  // holds test data ham path folder name
	static int[][] word_list =null;
	static int[][] test_data=null;
	static double[] weight_list=null;	
	static int[] train_tot_lines_col = new int[2];

	
	static double N=0.743; 
        static int limit= 100;
	

	 

	
	static double w0 =0.1;
	static LinkedHashMap<String, Double> weight_Map=new LinkedHashMap<String, Double>();

	public Perceptrons(String path1,String path2)
	{
		this.TrainPath=path1;
		this.TestPath=path2;
	    
	}
	
	// Method to do Perceptrons
	public static void do_Perceptrons() throws IOException
	{
		int row =0;
		int col=0;
		
		
		train_tot_lines_col = total_lines_col_file(TrainPath);
		row=train_tot_lines_col[0];
		col=train_tot_lines_col[1];
	//	 System.out.println(col);
		word_list= new int [row][col];
		weight_list= new double[col];
		create_word_list(TrainPath);
//		for(int i=0;i<word_list.length;i++)
//		{
//			for(int j=0;j<word_list[i].length;j++)
//			{
//				System.out.print(word_list[i][j]+"\t");
//			}
//			System.out.print("\n");
//		}
		
		 create_weight_list(col);
		 cal_weight_list(row, col);
		   
		} // end of do_perceptrons
	
	
	
	// method to calculate the weight list with new values 
		public static void cal_weight_list( int row , int col ) throws NumberFormatException, IOException
		{
			
			
		
				System.out.println("N= " +N + " and Limit is : "+limit);
				
				for(int l=0;l<limit;l++)
				{
				
				//	 System.out.println("---------Iteration--------"+ (limit+1));
					 
					 int cor_count=0;
					 
					 for(int line=0;line<word_list.length;line++){

						 int prior= cal_prior(line,word_list,col);
						 
						 if(prior == 1 && (word_list[line][0])==1){
							 cor_count++;
						   }
						   else if(prior == 1 && (word_list[line][0])==0){
							   //recalculate weights
							   update_weight_list(line,prior,word_list,col);
						   }
						   else if(prior == -1 && (word_list[line][0])==0){
							   cor_count++;
						   }
						   else if(prior == -1 && (word_list[line][0]==1)){
							   update_weight_list(line,prior,word_list,col);
						   }
						 
					 } // end of for doc
					 
					
				
					
				} // end of for loop limit
				
				 
				 System.out.println("\nChecking accuracy for Perceptrons");
				cal_accuracy(TestPath);
			//	cal_accuracy(TestPath, 2);
				
			
		} // end of method cal_weight_list
		
		
	// method to calculate the accuracy of the test data
		
		public static void cal_accuracy(String file) throws NumberFormatException, IOException
		{
			
			int[] total_row_col=total_lines_col_file(file);
			int t=0;
			int lines=0;
			String line = "";  
			int col=0,row=0;
			int row_data=total_row_col[0], col_data=total_row_col[1];
			String[] traingData=null;
			int[][] test_data = new int[row_data][col_data];
				BufferedReader br = new BufferedReader(new FileReader(file)); 	
				   while ((line = br.readLine()) != null) {  
					   lines++;
					   traingData    = line.split(" ");  
				//	   System.out.println("Test data line :"+ traingData[0]);
					  col=traingData.length;
					//  System.out.println("Test data line :"+ col);
					  for(int j=0;j<col;j++){		
						  
						   if(traingData[j].contains(":"))
							{
						String val[]= traingData[j].split(":");
						 test_data[row][j]= Integer.parseInt(val[1]);
				//	System.out.println("First :"+ val[0]+ "second :"+val[1]);
							}
							else
							{
						//		System.out.println("First :"+traingData[j]);
								test_data[row][j]= Integer.parseInt(traingData[j]);
							
							}
						}
					   
					  row++;
				   }
				   int count=0;
			  for(int i=0;i<test_data.length;i++)
				  
			  {
				//  System.out.println("test_data line:" + (i+1));
				  double tot_weight=0.0;
				   for(int j=1;j<test_data[i].length;j++){		
					 //  System.out.println("test_data :"+test_data[i][j]);
					   tot_weight=tot_weight+(weight_list[j]*test_data[i][j]);
				   }
				   if(tot_weight>0 && test_data[i][0]==1){
					   count++;
				   }
				   else if(tot_weight<0 && test_data[i][0]==0){
					   count++;
				   }
			   }//end of for i
			  System.out.println("Preceptron accuracy is :"+ (count*100)/test_data.length +"%");
			
		}// end of method cal_accuracy
		
		
		
		// method to count the words in a single doc
		public static LinkedHashMap<String,Integer> count_doc_words(String path,LinkedHashMap<String,Integer> map,String stop) throws FileNotFoundException
		{
			
				File file =new File(path);
				Scanner scan = new Scanner(file);
				while(scan.hasNext())
				{
					String word =scan.next();
				
					if(map.containsKey(word))
					 {
					 Integer count = (int)map.get(word);
					 count= count+1;
					 map.remove(word);
					 map.put(word, count);
						 
					 }//end of else if
					
					 else
						 map.put(word, 1);
				}// end of while
			return map;
		}// end of method count_words
		
		
	
		// method to update weight list
		public static void update_weight_list(int line_no, int O, int[][] word_list, int col)
		{
			
			int file_type= word_list[line_no][0];
			int t=0;
			if(file_type==1)
			{
				t=1;
			}
			else
			{
				t=-1;
			}
			for(int i=1;i<col;i++){
				
				double wI= weight_list[i];
				wI=wI+((N)*(t-O)*word_list[line_no][i]);
				weight_list[i]=wI;
				
			}//end of for i
		} // end of method update_weight_list
		
		// method to calculate document prior 
		
		public static int cal_prior(int line_no, int[][] word_list,int col)
		{
			
			double O=0;
			int prior_val=0;
			
			for(int i=1;i< col;i++)
			{
				
				int xI=word_list[line_no][i];
				double wI=weight_list[i];
				O=O+(xI*wI);
			}// end of for i
			O=O+w0;
			
			if(O >0)
			{
				prior_val=1;
			}
			else
			{
				prior_val=-1;
			}
			
			return prior_val;
		}// end of method cal_prior
		
		
		
	
	//method to create weight list
	
	public static void create_weight_list(int col)
	{
		
	//	System.out.print("col :" +col);
		for(int j=1;j<col;j++)
		{
		//	System.out.print("col :" +j);
			double wI=random_number_generator(0,3);
		//	System.out.print("wi :" +wI);
			weight_list[j]=wI;
		//	System.out.print("weight :" +weight_list[j]);
		}
		

		
	}// end of method create_weight_list
	
	
	// method to generate random number
	public static double random_number_generator(float min, float max)
	{
		
		Random r= new Random();
		double r_no = (r.nextFloat()*(max-min))+min;
	//	System.out.print("\n the random is: "+r_no);
		
		return r_no;
	}
	
	// method to create word list
			public static void  create_word_list(String path) throws IOException
			{
				int lines=0;
				String line = "";  
				String[] traingData=null;
				int col=0, row=0;
					BufferedReader br = new BufferedReader(new FileReader(path)); 	
					   while ((line = br.readLine()) != null) {  
						   lines++;
						   traingData    = line.split(" ");  
						  col=traingData.length;
						  for(int j=0;j<col;j++){		
							  
							   if(traingData[j].contains(":"))
								{
							String val[]= traingData[j].split(":");
							 word_list[row][j]= Integer.parseInt(val[1]);
						//	System.out.println("First :"+ val[0]+ "second :"+val[1]);
								}
								else
								{	 word_list[row][j]= Integer.parseInt(traingData[j]);
							//	System.out.println("First :"+traingData[j]);
								}
							}
						   
						  row++;
					   }
			
			}//end of method create_word_list
			
	
	
	// method to calculate total no of words in a one folder 
	public static int count_tot_words(LinkedHashMap map)
	{
		int count =0;
		Set set = map.entrySet();
		Iterator it = set.iterator();
		while(it.hasNext())
		{
			 Map.Entry me = (Map.Entry)it.next();
	   
	         count = count + (int)me.getValue();
		}
		
		return count;
		
	}
	
	
	
	// method to count the words in the folder 
	
	public static void count_words(String path,LinkedHashMap map) throws FileNotFoundException
	{
		File folder = new File(path);
		File[] files = folder.listFiles();
		
		for(int i=0;i<files.length;i++)
		{
			File file = files[i];
			Scanner scan = new Scanner(file);
			while(scan.hasNext())
			{
				String word =scan.next();
				
//				 if(spl_char1.contains(word)){
//					 continue;
//				 }//end of if
//				 else if(map.containsKey(word))
//				 {
//				 Integer count = (int)map.get(word);
//				 count= count+1;
//				 map.remove(word);
//				 map.put(word, count);
//					 
//				 }//end of else if
//				 else
//					 map.put(word, 1);
			}// end of while
		}// end of for
	}// end of method count_words
	
	
	
	
	
	
  
	//method to calculate total no of documents in a folder
		public static int[] total_lines_col_file(String path) throws IOException
		{
			int lines_col[]= new int[2];
			int lines=0;
			String line = "";  
			String[] traingData=null;
				BufferedReader br = new BufferedReader(new FileReader(path)); 	
				   while ((line = br.readLine()) != null) {  
					   lines++;
					   traingData    = line.split(" ");  
					//   columnCount=traingData.length;
				   }
				   lines_col[0]=lines;
				   lines_col[1]=traingData.length;
			return lines_col;
		}// end of total_lines_file method
		
		
		
		
		
	
}
