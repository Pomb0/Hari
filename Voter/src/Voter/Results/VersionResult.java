package Voter.Results;

import java.io.Serializable;

public class VersionResult implements Serializable {
	private boolean successful = false;
	private String serviceURI = null;
	private Integer result = null;
	private Long runTime = null;

	public boolean isSuccessful() {
		return successful;
	}

	public VersionResult setSuccessful(boolean successful) {
		this.successful = successful;
		return this;
	}

	public String getServiceURI() {
		return serviceURI;
	}

	public VersionResult setServiceURI(String serviceURI) {
		this.serviceURI = serviceURI;
		return this;
	}

	public Integer getResult() {
		return result;
	}

	public VersionResult setResult(Integer result) {
		this.result = result;
		return this;
	}

	public Long getRunTime() {
		return runTime;
	}

	public VersionResult setRunTime(Long runTime) {
		this.runTime = runTime;
		return this;
	}
}