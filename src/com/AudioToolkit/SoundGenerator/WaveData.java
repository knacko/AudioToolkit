package com.AudioToolkit.SoundGenerator;

public class WaveData {

	double hz1, hz2;
	double dur, bands, amplitude = 1, leftBalance, rightBalance;
	double rightPhase; //in rads
	double leftPhase; //in rads
	boolean isLooping;
	
	boolean killFlag = false;
	
	public WaveData(double hz, double hz2, double dur, double bands, double amplitude,
			double balance, double rightPhase, double leftPhase, boolean isLooping) {
				
		this.hz1 = hz;
		this.hz2 = hz2;
		this.dur = dur;
		this.bands = bands;
		this.amplitude = amplitude;
		leftBalance = Math.min((1-balance)*2,1);
		rightBalance = Math.min(balance*2,1);
		this.rightPhase = rightPhase;
		this.leftPhase = leftPhase;
		this.isLooping = isLooping;
		
	}
}
