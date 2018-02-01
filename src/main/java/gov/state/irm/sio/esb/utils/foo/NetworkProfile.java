package gov.state.irm.sio.esb.utils.foo;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class NetworkProfile
{
	public static void main(String[] args) throws SocketException
	{
		Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
		
		
		while (nics.hasMoreElements())
		{
			NetworkInterface nic = nics.nextElement();
			byte[] mac = nic.getHardwareAddress();
			System.out.println(nic.getDisplayName());
			System.out.println("\t"+Mac.format(mac));
			List<InterfaceAddress> addrs = nic.getInterfaceAddresses();
			for (InterfaceAddress addr:addrs)
			{
				System.out.println("\t"+addr.getAddress().getHostAddress());
			}
		}
	}
	public static class Mac
	{
		public static CharSequence format(byte[] mac)
		{
			if (mac==null) return null;
			StringBuilder sb=new StringBuilder(mac.length*3);
			
			for (int i=0; i<mac.length; ++i)
			{
				sb.append(String.format(i==0?"%02x":":%02x", mac[i]));
			}
			return sb;
		}
		
	}
}


