package com.AudioToolkit.Objects;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SoundGeneratorBackup extends Thread {

	/*
	 * TODO: SoundGenerator - auto-send system specs if the audiotrack fails to initialize
	 */

	private static final int STREAM = AudioManager.STREAM_MUSIC;
	private static final int SAMPLERATE = 44100;
	private static final int CHANNEL = AudioFormat.CHANNEL_OUT_STEREO;
	private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int MODE = AudioTrack.MODE_STREAM;
	private int BUFFERSIZE = AudioTrack.getMinBufferSize(SAMPLERATE,CHANNEL,ENCODING);

	AudioTrack audioTrack;

	Handler handle;
	int updateFreq = 10000;

	int type = -1; //the tone that is actually playing
	double hz1;
	double hz2;
	double dur, bands, balance = .5, leftphase = 0, rightphase = 0, amplitude = 1, leftBalance, rightBalance;
	boolean isLooping;

	final int W_SINE = 0;
	final int W_CLIPPED_SINE = 4;
	final int W_SQUARE = 1;
	final int W_TRIANGLE = 2;
	final int W_SAWTOOTH = 3;
	final int S_LINEAR = 10;
	final int S_CUBIC = 11;
	final int S_EXP = 12;
	final int N_WHITE = 20;
	final int N_PINK = 21;
	final int N_BLUE = 22;
	final int B_ADD = 30;
	final int B_MULT = 31;

	final int SUCCESS = 100;
	final int ERROR = 101;
	final int DONE = 102;

	boolean changed = true;
	boolean killthread = false;
	boolean killflag = false;

	
		
	
	
	public SoundGeneratorBackup() {
		//BUFFERSIZE = Math.max(BUFFERSIZE, SAMPLERATE * 2);
		audioTrack = new AudioTrack(STREAM,	SAMPLERATE,	CHANNEL, ENCODING, BUFFERSIZE, MODE);
	}

	public SoundGeneratorBackup(Handler h) {
		this.handle = h;
		//BUFFERSIZE = Math.max(BUFFERSIZE, SAMPLERATE * 2);
		audioTrack = new AudioTrack(STREAM,	SAMPLERATE,	CHANNEL, ENCODING, BUFFERSIZE, MODE);
		int state = audioTrack.getState();
		if (state != 1) {
			Log.d("SoundGenerator()","Error creating SoundGenerator, state " + state);
		} else {
			Log.d("SoundGenerator()","SoundGenerator created, state " + state);
		}
	}

	/*
	 * @returns - true is initialized correctly, false if not
	 */

	public boolean checkState() {		
		if (audioTrack.getState() == 1) return true;
		return false;
	}

	public SoundGeneratorBackup(Handler h, int type) {
		this.handle = h;
		this.type = type;
		audioTrack = new AudioTrack(STREAM,	SAMPLERATE,	CHANNEL, ENCODING, BUFFERSIZE, MODE);
		Log.d("SoundGenerator()","SoundGenerator created with type: " + type);
	}

	public void run() {

		android.os.Process
		.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		//h.sendMessage(h.obtainMessage(GEN));

		while (!killthread) { //Loop until the thread is stopped

			while (type == -1) {
				try {
					if (killthread) {
						Log.d("SoundGenerator.run()","killthread = "  + killthread);
						return;
					}
					sleep(250);					//Sleep thread while stopped or waiting until a type is specified
					//Log.d("SoundGenerator.run()","Waiting for type to be specified");
				} catch (InterruptedException e) {
					sendError(e);
				}
			}

			Log.d("SoundGenerator.run()","Type: " + type);

			killflag = false;

			//try {

				switch (type) {

				case W_SINE:
					playSine();
					break;
				case W_CLIPPED_SINE:
					playClippedSine();
					break;
				case W_SQUARE:
					playSquare();
					break;
				case W_TRIANGLE:
					playTriangle();
					break;
				case W_SAWTOOTH:
					playSawtooth();
					break;
				case S_LINEAR:
					playSweep();
					break;
				case S_CUBIC:
					playCubic();
					break;
				case S_EXP:
					playExp();
					break;
				case N_WHITE:
					playWhite();
					break;
				case N_PINK:
					playPink();
					break;
				case N_BLUE:
					playBlue();
					break;	
				case B_ADD:
					playAdd();
					break;
				case B_MULT:
					playMult();
					break;
				}	

				/*
			} catch (Exception e) {

				killThread();
				sendError(e);
				
			}*/
			//h.sendMessage(h.obtainMessage(DONE));
		}
	}

	/*
	 * @param - hz1 - the tone in which to play
	 * @return - true if the tone type has changed, false if the tone is stopped
	 */

	private double sin(double val) {
		
		return Math.sin(val);
		
	}
	
	private void playSine(){
		
		int numSamples = BUFFERSIZE/2;
		short sample[] = new short[numSamples];
		double hz1 = getHz1();

		double increment = (2*Math.PI) * hz1 / SAMPLERATE;
		double angle = 0;
		double temp;

		Log.d("playSine()","Calling Hz1: " + hz1);

		startAudioTrackStream();

		Log.d("playSine()","Running");

		while (killflag != true) {

			for (int i = 0; i < numSamples; i++) {

				//Runtime variables
				if (changed) {
					hz1 = getHz1();
					increment = (2*Math.PI) * hz1 / SAMPLERATE;
					angle = 0;
					changed = false;
				}

				//Tone generation
				sample[i++] = limitToShort(sin(angle+getLeftPhaseRads())*getLeftBalance()*getAmplitude());
				sample[i] = limitToShort(sin(angle+getRightPhaseRads())*getRightBalance()*getAmplitude());

				//System.out.println("L: " + sample[i-1] + " R: " + sample[i] + " B: " + balance);

				angle += increment;
				//Log.d("playSine()","hz1: " + hz1 + ", i: " + i +", sample[i]: " + sample[i] + ", angle: " + angle);
			}

			audioTrack.write(sample, 0, sample.length);			
		}

		Log.d("playSine()","killflag = " + killflag);

	}

	private void playClippedSine(){


		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];
		double freqOfTone = getHz1();


		double increment = (2*Math.PI) * freqOfTone / SAMPLERATE;
		double angle = 0;
		double temp;

		Log.d("playCLippedSine()","Calling Hz1: " + hz1);

		startAudioTrackStream();

		while (killflag != true) {

			for (int i = 0; i < numSamples; ++i) {
				//sample[i] = (short) (sin( angle )*Short.MAX_VALUE);

				//Runtime variables   getPhase()
				freqOfTone = getHz1();
				increment = (2*Math.PI) * freqOfTone / SAMPLERATE;

				//Tone generation
				temp = (Math.max(Math.min(sin( angle ),0.7),-0.7)*Short.MAX_VALUE);

				sample[i++] = (short) (temp*getLeftBalance());
				sample[i] = (short) (temp*getRightBalance());

				angle += increment;
			}

			audioTrack.write(sample, 0, sample.length);			
		}
	}

	private void playSquare(){

		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];
		double hz1 = getHz1();

		double increment = (2*Math.PI) * hz1 / SAMPLERATE;
		double angle = 0;

		Log.d("playSquare()","Calling Hz1: " + hz1);

		startAudioTrackStream();

		Log.d("playSquare()","Running");

		while (killflag != true) {

			for (int i = 0; i < numSamples; i++) {

				//Runtime variables
				if (changed) {
					hz1 = getHz1();
					increment = (2*Math.PI) * hz1 / SAMPLERATE;
					angle = 0;
					changed = false;
				}

				//Tone generation			
				sample[i++] = limitToShort((sin( angle+getLeftPhaseRads() ) > 0 ? 1.0d : -1.0d)*getLeftBalance()*getAmplitude()); //left
				sample[i] = limitToShort((sin( angle+getRightPhaseRads() ) > 0 ? 1.0d : -1.0d)*getRightBalance()*getAmplitude()); //right

				//System.out.println("L: " + sample[i-1] + " R: " + sample[i] + " B: " + balance);

				//sample[i] = (short) (Math.max(Math.min(sin( angle ),0.7),-0.7)*Short.MAX_VALUE);
				angle += increment;
				//Log.d("playSine()","hz1: " + hz1 + ", i: " + i +", sample[i]: " + sample[i] + ", angle: " + angle);
			}

			audioTrack.write(sample, 0, sample.length);			
		}

		Log.d("playSquare()","killflag = " + killflag);

	}

	private void playTriangle(){

		double hz1, nextindex, index, increment, valLeft, valRight;

		hz1 = getHz1();
		nextindex = (double) SAMPLERATE/hz1; //Asymptotes are at the same t
		index = 0;
		increment = (Short.MAX_VALUE*2)/nextindex;
		valLeft = 0;
		valRight = 0;

		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];


		Log.d("playTriangle()","Calling Hz1: " + hz1);
		startAudioTrackStream();

		while (killflag != true) {
			for (int i = 0; i < numSamples; ++i) {

				//Runtime variables
				if (changed) {
					hz1 = getHz1();
					nextindex = (double) SAMPLERATE/hz1; //Asymptotes are at the same t
					index = 0;
					increment = (Short.MAX_VALUE*2)/nextindex;
					valLeft = (getLeftPhaseDeg()/360)*nextindex*increment;
					valRight = (getRightPhaseDeg()/360)*nextindex*increment;
					changed = false;
					Log.d("sawtooth","hz1: "+ hz1 + " sample[i]: "+sample[i] + " inc: " + increment + " index: " + index + " nextIndex: " + nextindex);
				}

				//Tone generation
				/*	if (index>=nextindex) {
					val = Short.MAX_VALUE;
					index -= nextindex;
				}*/

				index++;
				sample[i++] = limitToShort((2*(Math.abs(valLeft)-Short.MAX_VALUE/2)*getLeftBalance()*getAmplitude())/(double) Short.MAX_VALUE);	//once it hits max, automatically rolls over to min
				sample[i] = limitToShort((2*(Math.abs(valRight)-Short.MAX_VALUE/2)*getRightBalance()*getAmplitude())/(double) Short.MAX_VALUE);

				//if (i%500 == 0 || i%500 == 1) System.out.println(sample[i-1] + "     " + sample[i]);

				valLeft = (short) (valLeft - increment);
				valRight = (short) (valRight - increment);
			}
			audioTrack.write(sample, 0, sample.length);
		}

		/*double hz1, nextIndex, index, increment, lastval;

		hz1 = getHz1();
		nextIndex = ((double)SAMPLERATE)/(hz1*2); //Asymptote at following high and low
		index = 0;
		increment = (Short.MAX_VALUE*2)/nextIndex;
		lastval = Short.MIN_VALUE;

		double temp;

		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];


		Log.d("playTriangle()","Calling Hz1: " + hz1);
		startAudioTrackStream();

		while (killflag != true) {
			for (int i = 0; i < numSamples; ++i) {	

				//Runtime variables
				if (changed) {
					hz1 = getHz1();
					nextIndex = ((double)SAMPLERATE)/(hz1*2); //Asymptote at following high and low
					index = 0;
					increment = (Short.MAX_VALUE*2)/nextIndex;
					lastval = Short.MIN_VALUE;
					changed = false;
					Log.d("triangle", "hz1: "+ hz1 + " sample[i]: "+sample[i] + " inc: " + increment + " index: " + index + " nextIndex: " + nextIndex);
				}

				//Tone generation

				if (index>=nextIndex) {
					increment *= -1;
					index -= nextIndex;
				}	
				index++;
				temp = (short) Math.min(Math.max(lastval + increment,Short.MIN_VALUE),Short.MAX_VALUE);
				sample[i++] = (short) (temp*(1-balance));
				sample[i] = (short) (temp*(balance));
				//Log.d("triangle", "hz1: "+ hz1 + " sample[i]: "+sample[i] + " inc: " + increment + " index: " + index + " nextIndex: " + nextIndex);

				lastval = temp;
			}
			audioTrack.write(sample, 0, sample.length);
		}*/
	}

	private void playSawtooth(){

		double hz1, nextindex, index, increment, valLeft, valRight;

		hz1 = getHz1();
		nextindex = (double) SAMPLERATE/hz1; //Asymptotes are at the same t
		index = 0;
		increment = (Short.MAX_VALUE*2)/nextindex;
		valLeft = 0;
		valRight = 0;

		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];


		Log.d("playSawtooth()","Calling Hz1: " + hz1);
		startAudioTrackStream();

		while (killflag != true) {
			for (int i = 0; i < numSamples; ++i) {

				//Runtime variables
				if (changed) {
					hz1 = getHz1();
					nextindex = (double) SAMPLERATE/hz1; //Asymptotes are at the same t
					index = 0;
					increment = (Short.MAX_VALUE*2)/nextindex;
					valLeft = (getLeftPhaseDeg()/360)*nextindex*increment;
					valRight = (getRightPhaseDeg()/360)*nextindex*increment;
					changed = false;
					Log.d("sawtooth","hz1: "+ hz1 + " sample[i]: "+sample[i] + " inc: " + increment + " index: " + index + " nextIndex: " + nextindex);
				}

				//Tone generation
				/*	if (index>=nextindex) {
					val = Short.MAX_VALUE;
					index -= nextindex;
				}*/

				index++;
				sample[i++] = limitToShort(valLeft*getLeftBalance()*getAmplitude()/(double) Short.MAX_VALUE);	//once it hits max, automatically rolls over to min
				sample[i] = limitToShort(valRight*getRightBalance()*getAmplitude()/(double) Short.MAX_VALUE);

				//if (i%500 == 0 || i%500 == 1) System.out.println(sample[i-1] + "     " + sample[i]);

				valLeft = (short) (valLeft - increment);
				valRight = (short) (valRight - increment);
			}
			audioTrack.write(sample, 0, sample.length);
		}		
	}	

	private void playSweep(){

		Log.d("playSweep()","Calling Hz1, Hz2, Dur and isLooping");
		double hz1 = getHz1();
		double hz2 = getHz2();
		double dur = getDur();
		boolean isLooping = isLooping();

		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];
		double freqStep = (hz2-hz1)/(double) (dur * SAMPLERATE);
		double index = 0;

		double increment;
		double angle = 0;

		startAudioTrackStream();

		while (killflag != true) {

			// fill out the array
			for (int i = 0; i < numSamples; ++i) {

				if (changed) {
					hz1 = getHz1();
					hz2 = getHz2();
					dur = getDur();
					isLooping = isLooping();
					freqStep = (hz2-hz1)/(double) (dur * SAMPLERATE);	
					increment = 0;
					index = 0;
					changed = false;
				}

				//Tone generation
				if (index > dur * SAMPLERATE) {

					if (isLooping == true) {
						hz1 = getHz1();
					}

					if (isLooping == false) {
						freqStep *= -1;
					}

					index = 0;					
				}

				hz1 += freqStep;
				increment = (2*Math.PI) * hz1 / SAMPLERATE;

				sample[i++] = limitToShort((sin(angle+getLeftPhaseRads()))*getLeftBalance()*getAmplitude()); //left
				sample[i] = limitToShort((sin(angle+getRightPhaseRads()))*getRightBalance()*getAmplitude()); //right

				//System.out.println(frequency);
				angle += increment;
				index++;
			}
			audioTrack.write(sample, 0, sample.length);
		}
	}

	private void playCubic(){

		Log.d("playCubic()","Calling Hz1, Hz2, Dur and isLooping");
		double hz1 = getHz1();
		double hz2 = getHz2();
		double dur = getDur();
		boolean isLooping = isLooping();

		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];
		double nextindex = dur * SAMPLERATE;
		double frequency = hz1;
		double increment;
		double angle = 0;
		double index = 0;
		double modindex = 0; //used only when reversing, to keep equation as y axis reflection

		startAudioTrackStream();

		while (killflag != true) {
			// fill out the array
			for (int i = 0; i < numSamples; ++i) {

				if (changed) {
					hz1 = getHz1();
					hz2 = getHz2();
					dur = getDur();
					nextindex = dur * SAMPLERATE;
					isLooping = isLooping();
					increment = 0;
					index = 0;
					modindex = 0;
					changed = false;
				}

				//Tone generation
				if (index >= nextindex) {

					if (isLooping == false) {		
						modindex = Math.abs(modindex-nextindex);
					}

					index = 0;					
				}		

				frequency = hz1+(hz2-hz1)*(Math.pow((Math.abs(modindex - index)/nextindex)*9.93953514143,3)/Math.pow(9.93953514143,3)); //9.93.. = x where 2^x = x^3
				increment = (2*Math.PI) * frequency / SAMPLERATE;

				sample[i++] = limitToShort(sin(angle+getLeftPhaseRads())*getLeftBalance()*getAmplitude()); //left
				sample[i] = limitToShort((sin(angle+getRightPhaseRads()))*getRightBalance()*getAmplitude()); //right

				//System.out.println("Freq: " + frequency + " angle: " + angle + " sample[" + i +"] = " + sample[i]);
				angle += increment;
				index++;
			}
			audioTrack.write(sample, 0, sample.length);
		}
	}	

	private void playExp(){

		Log.d("playExp()","Calling Hz1, Hz2, Dur and isLooping");
		double hz1 = getHz1();
		double hz2 = getHz2();
		double dur = getDur();
		boolean isLooping = isLooping();

		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];
		double nextindex = dur * SAMPLERATE;
		double frequency = hz1;
		double increment;
		double angle = 0;
		double index = 0;
		double modindex = 0; //used only when reversing, to keep equation as y axis reflection

		startAudioTrackStream();

		while (killflag != true) {
			// fill out the array
			for (int i = 0; i < numSamples; ++i) {

				if (changed) {
					hz1 = getHz1();
					hz2 = getHz2();
					dur = getDur();
					nextindex = dur * SAMPLERATE;
					isLooping = isLooping();
					increment = 0;
					index = 0;
					modindex = 0;
					changed = false;
				}

				//Tone generation
				if (index >= nextindex) {

					if (isLooping == false) {		
						modindex = Math.abs(modindex-nextindex);
					}

					index = 0;					
				}		

				frequency = hz1+(hz2-hz1)*(Math.pow(2,(Math.abs(modindex - index)/nextindex)*9.93953514143)/Math.pow(2,9.93953514143)); //9.93.. = x where 2^x = x^3
				increment = (2*Math.PI) * frequency / SAMPLERATE;

				sample[i++] = limitToShort(sin(angle+getLeftPhaseRads())*getLeftBalance()*getAmplitude()); //left
				sample[i] = limitToShort(sin(angle+getRightPhaseRads())*getRightBalance()*getAmplitude()); //right


				//System.out.println("Freq: " + frequency + " angle: " + angle + " sample[" + i +"] = " + sample[i]);
				angle += increment;
				index++;
			}
			audioTrack.write(sample, 0, sample.length);
		}
	}	

	private void playAdd() {

		Log.d("playAdd()","Calling Hz1, Hz2, Dur, Bands and isLooping");
		double hz1 = getHz1();
		double hz2 = getHz2();
		double dur = getDur();
		double bands = getBands();
		boolean isLooping = isLooping();

		double duration = dur; // seconds
		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];
		double freqStep = (hz2-hz1)/(double) (bands-1);
		double freqIndex = (dur*SAMPLERATE)/(bands); //the index of the step

		double freqIndexi = 0; //the iterator
		double index = 0;

		double frequency = hz1;
		double increment;
		double angle = 0;

		//System.out.println(freqStep);

		startAudioTrackStream();

		while (killflag != true) {

			// fill out the array
			for (int i = 0; i < numSamples; ++i) {

				if (changed) {
					hz1 = getHz1();
					frequency = hz1;
					hz2 = getHz2();
					dur = getDur();
					bands = getBands();
					//balance = getBalance();
					isLooping = isLooping();
					freqStep = (hz2-hz1)/(double) (bands-1);
					freqIndex = (dur*SAMPLERATE)/(bands);
					freqIndexi = 0;					
					increment = 0;
					index = 0;
					changed = false;
				}

				//Tone generation
				if (freqIndexi > freqIndex) {

					//System.out.println("freq: " + frequency);
					frequency += freqStep; //the steps
					freqIndexi -= freqIndex;
					//System.out.println("freqIndex hit");

					if (isLooping == false) {
						if (Math.round(frequency) == Math.round(hz2) || Math.round(frequency) == Math.round(hz1)) {
							freqStep *= -1;
						}
					}

					if (index >= duration * SAMPLERATE) {

						if (isLooping == true) { //works
							frequency = hz1;
						}
						index = 0;
					}
				}

				increment = (2*Math.PI) * frequency / SAMPLERATE;
				sample[i++] = limitToShort(sin( angle+getLeftPhaseRads() )*getLeftBalance()*getAmplitude()); //left
				sample[i] = limitToShort(sin( angle+getRightPhaseRads() )*getRightBalance()*getAmplitude()); //right

				//System.out.println(frequency);
				angle += increment;
				index++;
				freqIndexi++;
			}
			audioTrack.write(sample, 0, sample.length);
		}
	}

	private void playMult() {

		Log.d("playMult()","Calling Hz1, Hz2, Dur, Bands and isLooping");
		double hz1 = getHz1();
		double hz2 = getHz2();
		double dur = getDur();
		double bands = getBands();
		boolean isLooping = isLooping();

		//System.out.println("do things");
		double duration = dur; // seconds
		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];
		double freqStep = Math.pow(hz2/hz1, (1/(bands-1)));
		double freqIndex = (dur*SAMPLERATE)/bands; //the index of the step
		double freqIndexi = 0; //the iterator
		double index = 0;

		double frequency = hz1;
		double increment;
		double angle = 0;
		double temp;

		startAudioTrackStream();

		//System.out.println(freqStep);

		while (killflag != true) {

			// fill out the array
			for (int i = 0; i < numSamples; ++i) {

				//System.out.println("i: " + index + " of " + (duration*sampleRate) + ", freqi " + freqIndexi + " of " + freqIndex);

				//Tone generation
				if (freqIndexi > freqIndex) { 
					//System.out.println("freq: " + frequency);
					frequency *= freqStep; //the steps
					freqIndexi -= freqIndex;

					if (isLooping == false) {
						if (Math.round(frequency) == Math.round(hz2) || Math.round(frequency) == Math.round(hz1)) {
							freqStep = 1/freqStep;
						}
					}

					if (index >= duration * SAMPLERATE) {

						if (isLooping == true) { //works
							frequency = hz1;
						}
						index = 0;
					}					
				}


				increment = (2*Math.PI) * frequency / SAMPLERATE;
				temp = (sin( angle )*Short.MAX_VALUE);
				sample[i++] = limitToShort(sin( angle+getLeftPhaseRads())*getLeftBalance()*getAmplitude()); //left
				sample[i] = limitToShort(sin( angle +getRightPhaseRads())*getRightBalance()*getAmplitude()); //right


				//System.out.println(frequency);
				angle += increment;
				index++;
				freqIndexi++;
			}
			audioTrack.write(sample, 0, sample.length);
		}
	}

	private void playWhite(){

		Log.d("playWhite()","Calling nothing");
		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];

		Random r = new Random();
		double temp;

		startAudioTrackStream();

		while (killflag != true) {

			// fill out the array
			for (int i = 0; i < numSamples; ++i) {
				temp = (r.nextInt(2)-1);//*Short.MAX_VALUE)-Short.MAX_VALUE);
				sample[i++] = limitToShort(temp*getLeftBalance()*getAmplitude()); //left
				sample[i] = limitToShort(temp*getRightBalance()*getAmplitude()); //right

			}
			audioTrack.write(sample, 0, sample.length);
		}
	}

	private void playPink(){

		Log.d("playPink()","Calling nothing");
		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];

		Random r = new Random();

		double alpha = 1;
		int poles = 20;
		double multipliers[] = new double[poles];
		double values[] = new double[poles];
		double temp;

		startAudioTrackStream();

		while (killflag != true) {

			double a = 1;
			for (int i=0; i < poles; i++) {
				a = (i - alpha/2) * a / (i+1);
				multipliers[i] = a;
			}	 

			for (int i = 0; i < numSamples; ++i) {
				double x = (2*r.nextDouble())-1;

				for (int j=0; j < poles; j++) {
					x -= multipliers[j] * values[j];
				}

				System.arraycopy(values, 0, values, 1, values.length-1);
				values[0] = x;
				temp = x*(1/1.5);//Short.MAX_VALUE/1.5);
				sample[i++] = limitToShort(temp*getLeftBalance()*getAmplitude()); //left
				sample[i] = limitToShort(temp*getRightBalance()*getAmplitude()); //right

				//System.out.println("sample[i]: " + sample[i] + " values: " + values.length);
			}
			audioTrack.write(sample, 0, sample.length);
		}
	}

	private void playBlue(){

		Log.d("playBlue()","Calling nothing");
		int numSamples = BUFFERSIZE;
		short sample[] = new short[numSamples];

		Random r = new Random();

		double alpha = -1;
		int poles = 20;
		double multipliers[] = new double[poles];
		double values[] = new double[poles];
		double temp;

		startAudioTrackStream();

		while (killflag != true) {

			double a = 1;
			for (int i=0; i < poles; i++) {
				a = (i - alpha/2) * a / (i+1);
				multipliers[i] = a;
			}

			for (int i = 0; i < numSamples; ++i) {
				double x = (2*r.nextDouble())-1;

				for (int j=0; j < poles; j++) {
					x -= multipliers[j] * values[j];
				}

				System.arraycopy(values, 0, values, 1, values.length-1);
				values[0] = x;
				temp = x*(1/1.5);//Short.MAX_VALUE/1.5);
				sample[i++] = 	limitToShort(temp*getLeftBalance()*getAmplitude()); //left
				sample[i] = 	limitToShort(temp*getRightBalance()*getAmplitude()); //right

			}

			audioTrack.write(sample, 0, sample.length);
		}
	}

	public void stopTone() {

		killflag = true;
		type = -1;

		if (audioTrack.getState() == 1) {
			try {
				audioTrack.stop();	
				audioTrack.flush();		
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		Log.d("stopTone()","Tone stopped");

	}

	private void startAudioTrackStream() {

		stopTone();
		killflag = false; //to correct for the above

		try {							
			Log.d("startAudioTrackStream()","Starting AudioTrack");
			Log.d("startAudioTrackStream()","AudioTrack buffersize: " + getBufferSize());
			//audioTrack = new AudioTrack(STREAM,	SAMPLERATE, CHANNEL, ENCODING, audioTrackBufferSize, MODE);					
			audioTrack.play();	
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("startAudioTrackStream","Audiotrack broke, state " + audioTrack.getState());
		} 

		if (audioTrack.getState() == 0) {

		}

		Log.d("startAudioTrackStream()","AudioTrack succesfully started");

		return;
	}

	public int getBufferSize() {

		return AudioTrack.getMinBufferSize(SAMPLERATE,CHANNEL,ENCODING);

	}

	public void sendError(Exception e) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		Message msg = handle.obtainMessage(ERROR, sw.toString());
		handle.sendMessage(msg);

		Log.d("sendError()","Stack trace converted to string and sent");		
	}

	private Short limitToShort(double v) {

		v = Math.min(Math.max(v, -1), 1);

		return (short) (v*Short.MAX_VALUE/2);

	}

	
	
	
	public double getHz1() {
		return hz1;
	}

	public void setHz1(double hz1) {
		this.hz1 = hz1;
		changed = true;
	}

	public double getHz2() {
		return hz2;
	}

	public void setHz2(double hz2) {
		this.hz2 = hz2;
		changed = true;
	}

	public double getDur() {
		return dur;
	}

	public void setDur(double dur) {
		this.dur = dur;
		changed = true;
	}

	public double getBands() {
		return bands;
	}

	public void setBands(double bands) {
		this.bands = bands;
		changed = true;
	}

	public boolean isLooping() {
		return isLooping;
	}

	public void setLooping(boolean isLooping) {
		this.isLooping = isLooping;
		changed = true;
	}

	public void setLeftBalance(double balance) {
		leftBalance = Math.min((1-balance)*2,1);
	}

	public void setRightBalance(double balance) {
		rightBalance = Math.min(balance*2,1);
	}


	public double getLeftBalance() {
		return leftBalance;
	}

	public double getRightBalance() {
		return rightBalance;
	}

	/*
	 * Takes input of 0 to 1
	 */
	public void setBalance(double balance) {
		this.balance = balance;
		setLeftBalance(balance);
		setRightBalance(balance);
		changed = true;
	}

	public double getLeftPhaseRads() {
		return leftphase*(Math.PI/180);
	}
	
	public double getRightPhaseRads() {
		return rightphase*(Math.PI/180);
	}

	public double getLeftPhaseDeg() {
		return leftphase;
	}

	public double getRightPhaseDeg() {
		return rightphase;
	}
	
	/*
	 * Restricts to 360 degrees
	 */
	public void setLeftPhase(double phase) {
		this.leftphase = phase%360;
		changed = true;	
	}

	public void setRightPhase(double phase) {
		this.rightphase = phase%360;
		changed = true;	
	}
	

	public double getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(double amplitude) {
		this.amplitude = Math.exp(((double) 86/747)*amplitude);	
		changed = true;	
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		killflag = true;
		this.type = type;
		changed = true;
	}	

	public void killThread() {
		killflag = true;
		killthread = true;
		audioTrack.release();
	}

	public int getNativeOutputSampleRate() {
		return audioTrack.getNativeOutputSampleRate(STREAM);
	}

}
