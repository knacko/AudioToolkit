package com.AudioToolkit.SoundGenerator;

public class BaseWave extends Thread {

	AdvAudioTrack audioTrack;
	int BUFFERSIZE, SAMPLERATE;

	double hz1, hz2;
	double dur, bands, amplitude = 1, leftBalance, rightBalance;
	double rightPhase; //in rads
	double leftPhase; //in rads
	boolean isLooping;

	boolean killFlag = false;

	public BaseWave() {}
	
	public BaseWave(AdvAudioTrack audioTrack, double hz,
			double hz2, double dur, double bands, double amplitude,
			double balance, double rightPhase, double leftPhase, boolean isLooping) {

		this.audioTrack = audioTrack.clone();
		BUFFERSIZE = audioTrack.getBufferSize();
		SAMPLERATE = audioTrack.getSampleRate();				
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
	
	@Override
	public void run() {

		android.os.Process
		.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		audioTrack.play();
		
		generateWave();
		
		audioTrack.stop();	
		audioTrack.flush();	
		audioTrack.release();

	}

	public void stopWave() {
		if (audioTrack != null) killFlag = true;	
	}
	
	protected void generateWave() {
 
		//INITIALIZE THE STUFF
			
		while (!killFlag) {

			//START SAMPLE CODE

			int numSamples = BUFFERSIZE;
			short sample[] = new short[numSamples];

			int i = 0;

			for (i = 0; i < numSamples && killFlag != true; i++) {
				sample[i] = 1;
			}

			//END SAMPLE CODE
			
			audioTrack.write(sample, 0, i+1);	

		}
		
	}

	//Input of -1 to 1, angle is in rads
	public Short getLeftSin(double angle) {
		return getLeftWave(Math.sin(angle+leftPhase));
	}

	//Input of -1 to 1, angle is in rads
	public Short getRightSin(double angle) {

		return getRightWave(Math.sin(angle+rightPhase));

	}	
	
	//Gets wave multipliers and converts to short for buffer input
	public Short getLeftWave(double v) {

		return limitToShort(v*leftBalance*amplitude);

	}

	//Gets wave multipliers and converts to short for buffer input
	public Short getRightWave(double v) {

		return limitToShort(v*rightBalance*amplitude);

	}

	//input of -1 to 1, converts to an equipvalent short
	private Short limitToShort(double v) {

		v = Math.min(Math.max(v, -1), 1);

		//divide by two because the output is prone to clipping, allows higher volume level
		return (short) (v*(Short.MAX_VALUE/2));

	}	

}
