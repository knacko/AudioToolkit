package com.AudioToolkit.SoundGenerator;

import android.util.Log;

public class SineWave extends BaseWave {

	public SineWave(AdvAudioTrack audioTrack, double hz1,
			double amplitude,	double balance, double rightPhase, double leftPhase) {
		super(audioTrack, hz1, 0, 0, 0, amplitude, balance,
				rightPhase, leftPhase, false);
	}

	@Override
	protected void generateWave() {	
		
		int numSamples = BUFFERSIZE; 
		short sample[] = new short[numSamples];

		double increment = (2*Math.PI) * hz1 / SAMPLERATE;
		double angle = 0;

		Log.d("playSine()","Calling Hz1: " + hz1); 

		Log.d("playSine()","Running");

		int i = 0;
		
		while (killFlag != true) {
	
			for (i = 0; i < numSamples && killFlag != true; i++) {

				//Tone generation
				sample[i++] = getLeftSin(angle);
				sample[i] = getRightSin(angle);

				angle += increment;
				
				//Log.d("playSine","Sine(angle) = " + angle);
			}

			audioTrack.write(sample, 0, i+1);			
		}

		Log.d("playSine()","killflag = " + killFlag);

	}
}
