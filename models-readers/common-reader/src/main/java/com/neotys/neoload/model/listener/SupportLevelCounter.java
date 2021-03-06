package com.neotys.neoload.model.listener;

import java.util.Map;

public class SupportLevelCounter {

	private final CurrentAndTotalCounter supported = new CurrentAndTotalCounter();
	private final CurrentAndTotalCounter supportedWithWarn = new CurrentAndTotalCounter();
	private final CurrentAndTotalCounter unsupported = new CurrentAndTotalCounter();

	public void nextScript() {
		supported.nextScript();
		supportedWithWarn.nextScript();
		unsupported.nextScript();
	}

	public String getCurrentSummary() {
		return "supported: " + supported.getCurrent() + " (without warning) + " + supportedWithWarn.getCurrent() + " (with warning). Unsupported: "
				+ unsupported.getCurrent();
	}

	public String getTotalSummary() {
		final StringBuilder summary = new StringBuilder();
		summary.append("\tSupported: ").append(supported.getTotal()).append(" (without warning) + ").append(supportedWithWarn.getTotal()).append(" (with warning). \n");
		summary.append("\tUnsupported: ").append(unsupported.getTotal()).append("\n").append(unsupported.getListSummary());
		return summary.toString();
	}

	public void readSupported(final String name) {
		supported.increment(name);
	}

	public void readSupportedWithWarn(final String name) {
		supportedWithWarn.increment(name);
	}

	public void readUnsupported(final String name) {
		unsupported.increment(name);
	}

	public int getTotalCoveragePercent(){
		final int totalSupported = supported.getTotal() + supportedWithWarn.getTotal();
		final int totalUnsupported = unsupported.getTotal();
		return (int)(((float)totalSupported / (totalSupported + totalUnsupported))*100);
	}

	public String getTotalCoveragePercentAsString(){
		return getTotalCoveragePercent() + "%";
	}

	public int getSupportedFunctionsNoWarnCount(){
		return supported.getTotal();
	}

	public int getSupportedFunctionsWarnCount(){
		return supportedWithWarn.getTotal();
	}

	public int getUnsupportedFunctionsCount(){
		return unsupported.getTotal();
	}

	public Map<String, Integer> getTotalOccurencePerName(){
		return unsupported.getTotalOccurencePerName();
	}
}
