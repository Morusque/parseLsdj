public class Instr extends List {

	int[] parameters;
	int[] name;
	int mask_wave = 0x000000F0;
	int mask_table = 0x000000DF;

	Instr(boolean exists) {
		super(exists);
		this.parameters = new int[16];
		this.name = new int[5];
		// default values
		for (int i = 0; i < 16; i++) {
			parameters[i] = 0;
		}
		for (int i = 0; i < 5; i++) {
			name[i] = 0;
		}
	}

	public Instr(int index, int[] params, int[] name) {
		super(true, index);
		this.parameters = params;
		this.name = name;
	}

	public void replaceSynthsIndex(Synth synth) {
		if ((parameters[7] & mask_wave) == synth.oldIndex)
			parameters[7] = (parameters[7] & (0xFF - mask_wave))
					+ synth.newIndex;
	}

	public void replaceTableIndex(Table table) {
		if ((parameters[6] & mask_table) == table.oldIndex)
			parameters[6] = (parameters[6] & (0xFF - mask_table))
					+ table.newIndex;
	}

}
