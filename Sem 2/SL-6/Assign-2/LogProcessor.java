package logp;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class LogProcessor {

	public static void main(String args[]) throws Exception {
		
		Configuration c = new Configuration();
		
		String[] files=new GenericOptionsParser(c,args).getRemainingArgs();
		
		Path input=new Path(files[0]);
		
		Path output=new Path(files[1]);
		
		Job j=new Job(c,"logparser");
		
		j.setJarByClass(LogProcessor.class);
		j.setMapperClass(MapLog.class);
		j.setReducerClass(ReduceLog.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(j, input);
		FileOutputFormat.setOutputPath(j, output);
		System.exit(j.waitForCompletion(true)?0:1);
	}

	public static class MapLog extends Mapper<LongWritable, Text, Text, IntWritable>{
		public void map(LongWritable key, Text value, Context con) throws IOException, InterruptedException
		{
			int cnt = 0;
			Text  outputkey = null;
			IntWritable outputValue = null;
			String content = value.toString();
			
			String lines[]  = content.split("\n"); 
			
			for(String w : lines) {
			
				String[] words=w.split(",");
				cnt = 0;
				for(String word: words )
				{
					cnt++;
					if(cnt == 1) {
						outputkey = new Text(word.toUpperCase().trim());
					}else {
						outputValue = new IntWritable( Integer.parseInt(word.trim()));
					}
				}
				con.write(outputkey, outputValue);
				
			}
		}
	}
	
	public static class ReduceLog extends Reducer<Text, IntWritable, Text, IntWritable>
	{
		int max=0,min=37899;
		Text Tmax = new Text();
		Text Tmin = new Text();
		public void reduce(Text word, Iterable<IntWritable> values, Context con) throws IOException, InterruptedException
		{
			int sum=0;
			for (IntWritable value : values) {
				sum += value.get();
			}
			con.write(word, new IntWritable(sum));
						
		}
		//a func to write final result
		/*protected void cleanup(Context context) throws IOException, InterruptedException {
			
			context.write(Tmax, new IntWritable(max));
			context.write(Tmin, new IntWritable(min));
	    }*/
	}
}
