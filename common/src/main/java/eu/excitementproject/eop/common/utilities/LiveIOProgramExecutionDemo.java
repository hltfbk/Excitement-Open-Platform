package eu.excitementproject.eop.common.utilities;

import java.util.LinkedList;

public class LiveIOProgramExecutionDemo
{
	
// the lines.c program is as follows:
//	#include <stdio.h>
//	#include <unistd.h>
//
//	int main()
//	{
//		setbuf(stdout,0);
//		char line[128];
//		char* ret;
//		ret = fgets(line,128,stdin);
//		while(ret != 0)
//		{
//			sleep(3);
//			if(ret != 0)
//			{
//				printf("%s",ret);
//				/*fflush(stdout);*/
//			}
//			ret = fgets(line,128,stdin);
//		}
//	}


	public static void main(String[] args)
	{
		LiveIOProgramExecution exec = null;
		try
		{
			exec = new LiveIOProgramExecution(Utils.arrayToCollection(new String[]{"lines"}, new LinkedList<String>()));
			exec.start();
			exec.putLine("hello");
			System.out.println("*");
			exec.putLine("world");
			System.out.println(exec.getLine(4000));
			System.out.println(exec.getLine(1000));
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (exec!=null)
				try{exec.endIO();}catch(Exception e){e.printStackTrace();}
		}
	}
}
