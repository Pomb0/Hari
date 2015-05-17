package Voter;

import Voter.Results.VoterResult;

/**
 * Created by Jaime on 17/05/2015.
 *
 *
 */
public interface VoterInterface {
	VoterResult mealtimeInsulinDose(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int personalSensitivity);

	VoterResult backgroundInsulinDose(int bodyWeight);

	VoterResult personalSensitivityToInsulin(int physicalActivityLevel, int[] physicalActivitySamples, int[] bloodSugarDropSamples);
}
