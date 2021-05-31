package com.AudioToolkit.Objects;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.AudioToolkit.Activities.spectrum.Complex;

public class Recorder extends Thread{

	int sampleRate;
	int bufferSize;
	public volatile boolean isRecording = false;
	int channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int encoding = AudioFormat.ENCODING_PCM_16BIT;
	int source = MediaRecorder.AudioSource.MIC;
	//AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,	AudioFormat.ENCODING_PCM_16BIT);
	AudioRecord recordInstance = new AudioRecord(source, sampleRate, channel, encoding, bufferSize);;
	long lastSysTime = System.currentTimeMillis();	
	
	public Recorder(int sampleRate, int bufferSize) {
		this.sampleRate = sampleRate;
		this.bufferSize = bufferSize;		
	}

		public void start_recorder() {
			System.out.println("Recorder started");
			isRecording = true;
			this.start();
		}

		public void stop_recorder()
		{
			System.out.println("Recorder stopped");
			isRecording = false;
			recordInstance.stop();
		}

		/* Recording THREAD */
		public void run()
		{
			System.out.println("Thread is running");
			// We're important...
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

			recordInstance = new AudioRecord(source, sampleRate, channel, encoding, bufferSize);

			short[] buffer = new short[bufferSize];
			recordInstance.startRecording();

			// Continue till STOP button is pressed.
			while (isRecording)
			{
				doAction();   

			}
			// STOP BUTTON WAS PRESSED.
			// Close resources...
			recordInstance.stop();

		}

		
		/**
		 * The 'abstract' method to input what actually happens with the recorder.
		 * The recorder and buffer have been initialized.		 
		 * Unless subclassed, it does nothing 
		 */
		private void doAction() {};

		/**
		 * Calculate the fft of the inputted buffer
		 * 
		 * @param bsize
		 *            - the size of FFT required.
		 */
		public Complex[] getFFT(short[] buffer)
		{

			int bsize = buffer.length;

			Complex[] x = new Complex[bsize];

			for (int i = 0; i < bsize; i++)
			{
				x[i] = new Complex(buffer[i], 0);
			}

			return fft(x);

		}

		//Calculates the fft of an audio stream
		public Complex[] fft(Complex[] x)
		{
			int N = x.length;

			// base case
			if (N == 1)
				return new Complex[]
						{ x[0] };

			// radix 2 Cooley-Tukey FFT
			if (N % 2 != 0)
			{
				throw new RuntimeException("N is not a power of 2");
			}

			// fft of even terms
			Complex[] even = new Complex[N / 2];
			for (int k = 0; k < N / 2; k++)
			{
				even[k] = x[2 * k];
			}
			Complex[] q = fft(even);

			// fft of odd terms
			Complex[] odd = even; // reuse the array
			for (int k = 0; k < N / 2; k++)
			{
				odd[k] = x[2 * k + 1];
			}
			Complex[] r = fft(odd);

			// combine
			Complex[] y = new Complex[N];
			for (int k = 0; k < N / 2; k++)
			{
				double kth = -2 * k * Math.PI / N;
				Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
				y[k] = q[k].plus(wk.times(r[k]));
				y[k + N / 2] = q[k].minus(wk.times(r[k]));
			}
			return y;
		}

		// Gets the amplitude of the inputed fft
		private double[] getAMP(Complex[] fft) {

			int bars = 64; // the number of bars to display, must be a factor of bufferSize (4096)
			int bsize = fft.length;
			double amplitude[] = new double[bars]; // the bottom freq of each is i*(bufferSize/bars)

			for (int i = 0; i < bars; i++) {       

				double amp = 0;        	

				for (int j = i*bars; j < bufferSize/bars ; j++) {
					amp += (fft[i].abs() * 2) / bsize;
				}

				amplitude[i] = Math.sqrt(Math.pow((amp/(bufferSize/bars)),2)); //Take the rms of the range	

			}

			return amplitude;			

		}

		private void graphAMP(double[] amp) {
			long newSysTime = System.currentTimeMillis();
			System.out.println("Time is last graph: " + (newSysTime-lastSysTime+0.0)/1000);
			lastSysTime = System.currentTimeMillis();
		}

	}

	
	
	
	

