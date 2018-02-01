package gov.state.irm.sio.esb.utils.foo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PingCommand implements PowershellQueuable
{
	static String[] test={"<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" , 
			"<Objects>\r\n" ,
			"  <Object Type=\"System.Net.NetworkInformation.PingReply\">\r\n" , 
			"    <Property Name=\"Status\" Type=\"System.Net.NetworkInformation.IPStatus\">\r\n" , 
			"      <Property Name=\"value__\" Type=\"System.Int32\">0</Property>\r\n" ,
			"    </Property>\r\n" ,
			"    <Property Name=\"Address\" Type=\"System.Net.IPAddress\">\r\n" , 
			"      <Property Name=\"IPAddressToString\" Type=\"System.String\">1.2.3.4</Property>\r\n" , 
			"      <Property Name=\"Address\" Type=\"System.Int64\">4264625930</Property>\r\n" ,
			"      <Property Name=\"AddressFamily\" Type=\"System.Net.Sockets.AddressFamily\">InterNetwork</Property>\r\n" , 
			"      <Property Name=\"ScopeId\" Type=\"System.Int64\" />\r\n" ,
			"      <Property Name=\"IsIPv6Multicast\" Type=\"System.Boolean\">False</Property>\r\n" , 
			"      <Property Name=\"IsIPv6LinkLocal\" Type=\"System.Boolean\">False</Property>\r\n" , 
			"      <Property Name=\"IsIPv6SiteLocal\" Type=\"System.Boolean\">False</Property>\r\n" , 
			"      <Property Name=\"IsIPv6Teredo\" Type=\"System.Boolean\">False</Property>\r\n" ,
			"      <Property Name=\"IsIPv4MappedToIPv6\" Type=\"System.Boolean\">False</Property>\r\n" , 
			"    </Property>\r\n" ,
			"    <Property Name=\"RoundtripTime\" Type=\"System.Int64\">2</Property>\r\n" , 
			"    <Property Name=\"Options\" Type=\"System.Net.NetworkInformation.PingOptions\">\r\n" , 
			"      <Property Name=\"Ttl\" Type=\"System.Int32\">255</Property>\r\n" ,
			"      <Property Name=\"DontFragment\" Type=\"System.Boolean\">False</Property>\r\n" , 
			"    </Property>\r\n" ,
			"    <Property Name=\"Buffer\" Type=\"System.Byte[]\">System.Byte[]</Property>\r\n" , 
			"  </Object>\r\n" ,
			"</Objects>\r\n"};
//	/bin.println();
//	/bin.println("$ping.Send(\"1.2.3.4\") | ConvertTo-Xml -As String; echo ''");
	
	String host;
	
	
	@Override
	public long getTimeoutMS()
	{
		return -1;
	}

	private static List<String> cmds=Collections.unmodifiableList(Arrays.asList(new String[]{"$ping = New-Object System.Net.Networkinformation.ping;"}));

	
	@Override
	public List<String> getSetupCommands()
	{
		return null;
	}

	@Override
	public String getCommand()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	List<String> buffered=new ArrayList<String>();
	private SAXException lastError;
	
	Reader getBuffer()
	{
		return new Reader()
		{
			int pos=0;
			int ptr=0;
			@Override
			synchronized public int read(char[] cbuf, int off, int len) throws IOException
			{
//				System.err.println("read...");
//				System.err.println("buffered.length="+buffered.size());
//				System.err.println("pos="+pos);
//				System.err.println("ptr="+ptr);
//				System.err.println("off="+off);
//				System.err.println("len="+len);
				
				if (cbuf==null) throw new NullPointerException();
				if (off+len>cbuf.length) throw new IndexOutOfBoundsException();
				
				//are we out of strings?
				if (pos>=buffered.size()) 
				{
//					System.err.println("\nEOF");
					return -1;
				}

//				System.err.println("buffered.get("+pos+").length()="+buffered.get(pos).length());
				
				// is ptr past the end of this string?
				if (ptr>=buffered.get(pos).length())
				{
					++pos;
					ptr=0;
					len=0;
//					System.err.println("len="+len);
					return len;
				}
				//is len too big?
				if (len>buffered.get(pos).length()-ptr)
				{
//					System.err.println(len+">"+(buffered.get(pos).length()-ptr));
				    len=buffered.get(pos).length()-ptr;
//					System.err.println("len="+len);
				}
				
				if (len<=0) return 0;

				try
				{
					buffered.get(pos).getChars(ptr, len, cbuf, off);
				}
				catch (RuntimeException e)
				{
//					System.err.println("buffered.get("+pos+").getChars("+ptr+", "+len+", "+cbuf+", "+off+");");
					throw e;
				}
				ptr+=len;
//				System.err.println("ptr="+ptr);

				return len;
			}
			
			@Override
			public void close() throws IOException
			{
			}
		};
	}
	
	@Override
	public boolean process(CharSequence input)
	{
		buffered.add(input.toString());
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
		try
		{
			saxParser = factory.newSAXParser();
	        PingResultHandler userhandler = new PingResultHandler();
			saxParser.parse(new InputSource(new BufferedReader(getBuffer())), userhandler);     
		}
		catch (ParserConfigurationException | IOException e)
		{
			throw new RuntimeException(e);
		}
		catch (SAXException e)
		{
			lastError=e;
			return false;
		}
		
		return true;
	}
	
	public class PingResultHandler extends DefaultHandler 
	{
		boolean done=false;
		@Override
		public void endDocument() throws SAXException
		{
			System.out.println("end document");
			super.endDocument();
			done=true;
		}
		
	}

	public static void main(String[] args) throws IOException
	{
		PingCommand pc = new PingCommand();
		for (int i = 0; i<test.length; ++i)
		{
			boolean res = pc.process(test[i]);
//			boolean res = pc.process(test[i].replaceAll("\r", ""));
			System.out.println(res);
			if (i==test.length-1 && !res)
			{
				pc.lastError.printStackTrace();
			}
			
		}
		
	}

	private void dumpbuffer()
	{
		BufferedReader x = new BufferedReader(getBuffer());
		String l;
		int line=0;
		try
		{
			while ((l=x.readLine()) != null)
			{
				System.out.println("line "+(++line)+":"+l);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
	}
	
}
