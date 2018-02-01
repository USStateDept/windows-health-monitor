package gov.state.irm.sio.esb.utils.foo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class NetworkProfilePS
{
	public static void main (String[] args) throws IOException, InterruptedException
	{
		ProcessBuilder pb=new ProcessBuilder("powershell.exe");
		Process powershell = pb.start();
		OutputStream pin = powershell.getOutputStream();
		PrintWriter bin=new PrintWriter(pin);
//		final BufferedReader bout=new BufferedReader(new InputStreamReader(pout));
//		final BufferedReader berr=new BufferedReader(new InputStreamReader(perr));
		
		bin.println("$ping = New-Object System.Net.Networkinformation.ping;");
		bin.println("$ping.Send(\"1.2.3.4\") | ConvertTo-Xml -As String; echo ''");

//		bin.println("exit");
	    bin.flush();
		final InputStream perr = powershell.getErrorStream();
		final InputStream pout = powershell.getInputStream();

//		System.err.println("sleep");
//		Thread.sleep(5000);
		
		new Thread()
		{
			public void run()
			{
				byte[] buf=new byte[1];
				int count=0;
				try
				{
					while ((count=pout.read(buf))!=-1)
					{
						if (count==0)
							Thread.yield();
						else
							System.out.write(buf[0]);
					}
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		}.run();

		new Thread()
		{
			public void run()
			{
				byte[] buf=new byte[1];
				int count=0;
				try
				{
					while ((count=perr.read(buf))!=-1)
					{
						if (count==0)
							Thread.yield();
						else
							System.err.write(buf[0]);
					}
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		}.run();
		
		System.err.println("sleep");
		Thread.sleep(10000);
		
		System.err.println("done");
		
	    bin.close();
		
	}
}
