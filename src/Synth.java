public class Synth extends List {

	int[] params;
	int[] waveforms;

	Synth(boolean exists) {
		super(exists);
		this.params = new int[16];
		this.waveforms = new int[16 * 16];
		// default values
		for (int i = 0; i < 16; i++) {
			params[i] = 0;
		}
		for (int i = 0; i < 16 * 16; i++) {
			waveforms[i] = 0;
		}
	}

	public Synth(int index, int[] params, int[] waveforms) {
		super(true, index);
		this.params = params;
		this.waveforms = waveforms;
	}

}
