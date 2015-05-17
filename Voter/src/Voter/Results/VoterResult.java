package Voter.Results;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Jaime on 16/05/2015.
 *
 */
public class VoterResult implements Serializable{
	private String method;
	private boolean successful = false;
	private Integer versionCount = null;
	private Integer result = null;
	private Long runTime = null;
	private List<VersionResult> versionResults = new LinkedList<>();

	public String getMethod() {
		return method;
	}

	public VoterResult setMethod(String method) {
		this.method = method;
		return this;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public VoterResult setSuccessful(boolean successful) {
		this.successful = successful;
		return this;
	}

	public Integer getVersionCount() {
		return versionCount;
	}

	public VoterResult setVersionCount(Integer versionCount) {
		this.versionCount = versionCount;
		return this;
	}

	public Integer getResult() {
		return result;
	}

	public VoterResult setResult(Integer result) {
		this.result = result;
		return this;
	}

	public Long getRunTime() {
		return runTime;
	}

	public VoterResult setRunTime(Long runTime) {
		this.runTime = runTime;
		return this;
	}

	public List<VersionResult> getVersionResults() {
		return versionResults;
	}

	public VoterResult setVersionResults(List<VersionResult> versionResults) {
		this.versionResults = versionResults;
		return this;
	}

	public JSONObject getJSON(){
		JSONObject result = new JSONObject();
		JSONArray versions = new JSONArray();

		result.put("successful", isSuccessful());
		result.put("versionCount", getVersionCount());
		result.put("result", getResult());
		result.put("runTime", getRunTime());
		result.put("method", getMethod());

		for(VersionResult version : versionResults){
			JSONObject versionJSON = new JSONObject();
			versionJSON.put("successful", version.isSuccessful());
			versionJSON.put("uri", version.getServiceURI());
			versionJSON.put("result", version.getResult());
			versionJSON.put("runTime", version.getRunTime());
			versions.add(versionJSON);
		}

		result.put("versionResults", versions);
		return result;
	}
}



