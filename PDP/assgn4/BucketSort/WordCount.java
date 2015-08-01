import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobHistory.Values;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCount {
	
	/**
	 * @author hduser
	 * seperate the files into lines and lines into token
	 * input: <key, value>, key: line number, value: line
	 * output: <key, value>, key: each word, value: number of occurence 
	 * e.g. input <line1, hello a bye a>
	 * 		output<hello, 1>,<a, 1>, <bye, 1>,<a, 1>
	 */
	public static class Map extends Mapper<LongWritable, Text, IntWritable, FloatWritable>{
		private IntWritable key_map = new IntWritable();
		private FloatWritable value_map = new FloatWritable();
		
		public void map(LongWritable key, Text value, Context context){
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line); // based on space
			while(tokenizer.hasMoreElements()){
				String s = tokenizer.nextToken();
				value_map.set(Float.parseFloat(s));
				key_map.set((int) Float.parseFloat(s));
				try {
					//context.write(key_map,value_map);
					context.write(new IntWritable(1),value_map);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
	}
	/**
	 * @author hduser
	 * Count the number of times the given keys occur
	 * input: <key, value>, key = Text, value: number of occurrence
	 * output: <key, value>, key = Text, value = number of occurrence
	 * 
	 * */
	public static class Reduce extends Reducer<IntWritable, FloatWritable, Text, Text>{
		public void reduce(IntWritable key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException{
			ArrayList<Float> sort_array = new ArrayList<Float>();
			Text wrd = new Text();
			
			for (FloatWritable val: values){
				sort_array.add(val.get());
			}
			Collections.sort(sort_array);
			for(int i=0;i<sort_array.size();i++){
				wrd.set(new Text (sort_array.get(i).toString()));
				context.write(new Text(""), wrd);
			}
		}
	}
	
	
	public static void main(String[] args) {
		
		int n = 100000;
		GuassianRandomNumber g = new GuassianRandomNumber(n);		
	
		try {
			Configuration conf = new Configuration();
			Job job = new Job(conf, "wordcount");
			
			
			job.setMapperClass(Map.class);
			job.setReducerClass(Reduce.class);
			
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			
			job.setMapOutputKeyClass(IntWritable.class);
			job.setMapOutputValueClass(FloatWritable.class);
			
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			
			FileInputFormat.addInputPath(job, new Path("README.txt"));
			FileOutputFormat.setOutputPath(job, new Path("output_wordcount"));
			
			job.setJarByClass(WordCount.class);
			long startTime = System.currentTimeMillis();
			job.waitForCompletion(true);
			long endTime = System.currentTimeMillis();
			System.out.println("The time is: "+(endTime - startTime)/1000);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
