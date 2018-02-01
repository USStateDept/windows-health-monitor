package gov.state.irm.sio.esb.utils.foo;

import java.util.List;

public interface PowershellQueuable
{
	/**
	 * 
	 * @return milliseconds to wait before giving up
	 */
	public long getTimeoutMS();

	/**
	 * All commands run from there will have their output discarded. They must not consume STDIN.
	 * @return a sequence of PowerShell commands to run prior to capturing the output.
	 */
	public List<String> getSetupCommands();

	/**
	 * 
	 * @return
	 */
	public String getCommand();

	/**
	 * Processes iterative input.
	 * 
	 * @param input a sequence of characters (e.g. a single line of input)
	 * @return true if needs more input to complete the command output parsing
	 */
	public boolean process(CharSequence input);

}
