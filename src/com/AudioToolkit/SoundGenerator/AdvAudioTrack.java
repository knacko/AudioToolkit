package com.AudioToolkit.SoundGenerator;

import android.media.AudioTrack;

public class AdvAudioTrack extends AudioTrack {

	private static int BUFFERSIZE;
	private static int MODE;
	
	public AdvAudioTrack(int streamType, int sampleRateInHz, int channelConfig,
			int audioFormat, int mode) {
		
		super(streamType, sampleRateInHz, channelConfig, audioFormat,
				BUFFERSIZE = AudioTrack.getMinBufferSize(sampleRateInHz,channelConfig,audioFormat), MODE = mode);
	}

	public AdvAudioTrack clone() {
		
		return new AdvAudioTrack(getStreamType(), getSampleRate(), getChannelConfiguration(), getAudioFormat(), 
				getMode());
		
	}
	
	public int getBufferSize() {
		
		return BUFFERSIZE;
		
	}
	
	public int getMode() {
		
		return MODE;
	}
	
}
