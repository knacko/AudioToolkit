package com.AudioToolkit.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.AudioToolkitPro.R;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;

public class spectrum  extends Activity{

	private volatile boolean isRecording = false;
	private Recorder r = new Recorder(new Handler());
	private double[] amplitude;
	
	private static final int UPDATE = 0;

	Button spectrum_start;
	LinearLayout graph;
	GraphView graphView;
	
	@Override
	public void onCreate(Bundle b) {

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		super.onCreate(b);
		setContentView(R.layout.spectrum);

		spectrum_start = (Button) findViewById(R.id.spectrum_start);
		graph = (LinearLayout) findViewById(R.id.spectrum_graph);
		graphView = new BarGraphView(spectrum.this, "");

		graphView.addSeries(new GraphViewSeries(new GraphViewData[] {new GraphViewData(0,0)}));
		graph.addView(graphView);
		
		spectrum_start.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if (getString(R.string.freeApp).equals("true")) {
					Toast.makeText(getApplicationContext(), (CharSequence) getString(R.string.freeAppMessage), 10).show();
					return;	
				}

				if (spectrum_start.getText().equals("ON"))
				{
					spectrum_start.setText("OFF");
					start_meter();
				}
				else
				{
					spectrum_start.setText("ON");
					stop_meter();
				}

			}});

	}

	@Override
	public void onPause()
	{
		super.onPause();
		stop_meter();
		System.out.println("Stopped the recorder");
	}
	
	public void start_meter()
	{	      
		r = new Recorder(mhandle);
		r.start_recorder();
	}

	/**
	 * Stops the SPL Meter
	 */
	public void stop_meter()
	{
		r.stop_recorder();
	}
	
	/**
	 * Handler for displaying messages
	 */
	public Handler mhandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case UPDATE:
				System.out.println("sent");
				updateGraph((GraphViewData[]) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}

		private void updateGraph(GraphViewData[] graphData) {		
			
		//	graphView.removeSeries(0);
			graphView.addSeries(new GraphViewSeries(graphData));
			
		}

	};

	public class Recorder extends Thread
	{
		public volatile boolean isRunning = false;
		int bufferSize = 2048;
		int sampleRate = 44100;
		int channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int encoding = AudioFormat.ENCODING_PCM_16BIT;
		int source = MediaRecorder.AudioSource.MIC;
		//AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,	AudioFormat.ENCODING_PCM_16BIT);
		AudioRecord recordInstance = new AudioRecord(source, sampleRate, channel, encoding, bufferSize);;
		long lastSysTime = System.currentTimeMillis();		
		Handler h = new Handler();
		
		private static final int UPDATE = 0;

		/**
		 * Handler is passed to pass messages to main screen Recording is done
		 * 44100Hz MONO 16 bit
		 */
		public Recorder(Handler h){
			this.h = h;
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

			recordInstance = new AudioRecord(source, sampleRate, channel, encoding, bufferSize*2);

			short[] tempBuffer = new short[bufferSize];
			recordInstance.startRecording();
			// Continue till STOP button is pressed.

			while (isRecording)
			{
				tempBuffer = new short[bufferSize];
				recordInstance.read(tempBuffer, 0, bufferSize);
				h.sendMessage(h.obtainMessage(UPDATE, getGRAPH(getAMP(getFFT(tempBuffer)))));       
			}
			// STOP BUTTON WAS PRESSED.
			// Close resources...
			recordInstance.stop();

		}

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
			//	System.out.println("buffer[" +i + "]: " + buffer[i]);
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

				for (int j = i*(bufferSize/bars); j < (i+1)*(bufferSize/bars) ; j++) {
					amp += (fft[j].abs() * 2) / bsize;
				}

				amplitude[i] = Math.sqrt(Math.pow((amp/(bufferSize/bars)),2)); //Take the rms of the range	
				//System.out.println("Iter i: " + i +" at " + amplitude[i]);
			}

			long newSysTime = System.currentTimeMillis();
			System.out.println("Time is last graph: " + (newSysTime-lastSysTime+0.0)/1000);
			lastSysTime = System.currentTimeMillis();

			return amplitude;			

		}

		private GraphViewData[] getGRAPH(double[] amplitude) {

			double frequency = 19980/amplitude.length;
			GraphViewData[] graphData = new GraphViewData[amplitude.length];
			for (int i = 0; i < amplitude.length; i++) {
				graphData[i] = new GraphViewData(20+frequency*i, amplitude[i]);
			}		
			return graphData;
		}
	}
	
	public static class Complex
	{
		private final double re; // the real part
		private final double im; // the imaginary part

		// create a new object with the given real and imaginary parts
		public Complex(double real, double imag)
		{
			re = real;
			im = imag;
		}

		// return a string representation of the invoking Complex object
		public String toString()
		{
			if (im == 0)
				return re + "";
			if (re == 0)
				return im + "i";
			if (im < 0)
				return re + " - " + (-im) + "i";
			return re + " + " + im + "i";
		}

		// return abs/modulus/magnitude and angle/phase/argument
		public double abs()
		{
			return Math.hypot(re, im);
		} // Math.sqrt(re*re + im*im)

		public double phase()
		{
			return Math.atan2(im, re);
		} // between -pi and pi

		// return a new Complex object whose value is (this + b)
		public Complex plus(Complex b)
		{
			Complex a = this; // invoking object
			double real = a.re + b.re;
			double imag = a.im + b.im;
			return new Complex(real, imag);
		}

		// return a new Complex object whose value is (this - b)
		public Complex minus(Complex b)
		{
			Complex a = this;
			double real = a.re - b.re;
			double imag = a.im - b.im;
			return new Complex(real, imag);
		}

		// return a new Complex object whose value is (this * b)
		public Complex times(Complex b)
		{
			Complex a = this;
			double real = a.re * b.re - a.im * b.im;
			double imag = a.re * b.im + a.im * b.re;
			return new Complex(real, imag);
		}

		// scalar multiplication
		// return a new object whose value is (this * alpha)
		public Complex times(double alpha)
		{
			return new Complex(alpha * re, alpha * im);
		}

		// return a new Complex object whose value is the conjugate of this
		public Complex conjugate()
		{
			return new Complex(re, -im);
		}

		// return a new Complex object whose value is the reciprocal of this
		public Complex reciprocal()
		{
			double scale = re * re + im * im;
			return new Complex(re / scale, -im / scale);
		}

		// return the real or imaginary part
		public double re()
		{
			return re;
		}

		public double im()
		{
			return im;
		}

		// return a / b
		public Complex divides(Complex b)
		{
			Complex a = this;
			return a.times(b.reciprocal());
		}

		// return a new Complex object whose value is the complex exponential of
		// this
		public Complex exp()
		{
			return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re)
					* Math.sin(im));
		}

		// return a new Complex object whose value is the complex sine of this
		public Complex sin()
		{
			return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re)
					* Math.sinh(im));
		}

		// return a new Complex object whose value is the complex cosine of this
		public Complex cos()
		{
			return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re)
					* Math.sinh(im));
		}

		// return a new Complex object whose value is the complex tangent of this
		public Complex tan()
		{
			return sin().divides(cos());
		}

		// a static version of plus
		public static Complex plus(Complex a, Complex b)
		{
			double real = a.re + b.re;
			double imag = a.im + b.im;
			Complex sum = new Complex(real, imag);
			return sum;
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.howtomenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			new AlertDialog.Builder(this).setTitle("Info").setMessage("Audio Toolkit \n \n" +
					"A suite of useful calculators when setting up a stereo system. Includes calculators for subwoofer wiring, wire gauge, Ohm's law and more. \n \n" +  
					"Copyright \u00A9 2011, Werld 3 Labs").setIcon(R.drawable.w3_small_logo).setNeutralButton("Close", null).show();
			break;
		case R.id.howto:
			new AlertDialog.Builder(this).setTitle("How to use").setMessage("Spectrum Analyzer \n \n" +
					"Use the specs given with the LEDs to achieve best results. \n \n" + 
					"Be careful with parallel wiring, as LEDs (even from the same batch) may have slightly different voltages. This may cause just " +
					"the smallest voltage LED light up, and die quickly as a result. It is safer to use one resistor for each LED in the circuit. " +
					"Use the single LED calculator and wire the circuit in parallel."
					).setIcon(R.drawable.ic_menu_help).setNeutralButton("Close", null).show();
		}
		return true;
	}

}