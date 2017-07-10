package me.unei.configuration.api;

import java.util.List;

public interface IFlatCSVConfiguration extends IFlatConfiguration {
	
	public List<String> getHeaderLine();
	
	public void resetHeaderLine();
}