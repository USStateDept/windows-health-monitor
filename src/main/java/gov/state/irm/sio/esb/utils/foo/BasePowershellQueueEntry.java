package gov.state.irm.sio.esb.utils.foo;

import java.util.Collections;
import java.util.List;

public class BasePowershellQueueEntry implements PowershellQueuable 
{
	@Override
	public long getTimeoutMS()
	{
		return -1;
	}
	
	@Override
	public List<String> getSetupCommands()
	{
		return Collections.emptyList();
	}
	
	@Override
	public String getCommand()
	{
		return "echo ''";
	}

	@Override
	public boolean process(CharSequence input)
	{
		return false;
	}
}
