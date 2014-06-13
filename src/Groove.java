public class Groove extends List {

	int[] values;

	Groove(boolean exists) {
		super(exists);
		this.values = new int[16];
		// default values
		for (int i = 0; i < 16; i++) {
			values[i] = 0;
		}
	}

	public Groove(int index, int[] values) {
		super(true, index);
		this.values = values;
	}

}
