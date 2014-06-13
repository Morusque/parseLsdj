public class Table extends List {

	int[] volumes;
	int[] transps;
	int[] fx1;
	int[] fx1Val;
	int[] fx2;
	int[] fx2Val;

	Table(boolean exists) {
		super(exists);
		this.volumes = new int[16];
		this.transps = new int[16];
		this.fx1 = new int[16];
		this.fx1Val = new int[16];
		this.fx2 = new int[16];
		this.fx2Val = new int[16];
		// default values
		for (int i = 0; i < 16; i++) {
			volumes[i] = 0;
			transps[i] = 0;
			fx1[i] = 0xFF;
			fx1Val[i] = 0xFF;
			fx2[i] = 0xFF;
			fx2Val[i] = 0xFF;
		}
	}

	public Table(int index, int[] volumes, int[] transps, int[] fx1,
			int[] fx1Val, int[] fx2, int[] fx2Val) {
		super(true, index);
		this.volumes = volumes;
		this.transps = transps;
		this.fx1 = fx1;
		this.fx1Val = fx1Val;
		this.fx2 = fx2;
		this.fx2Val = fx2Val;
	}

	public void replaceGrooveIndex(Groove groove) {
		for (int i = 0; i < 16; i++) {
			if (fx1Val[i] == groove.oldIndex && fx1[i] == 0x06)
				fx1Val[i] = groove.newIndex;
			if (fx2Val[i] == groove.oldIndex && fx2[i] == 0x06)
				fx2Val[i] = groove.newIndex;
		}
	}

}
