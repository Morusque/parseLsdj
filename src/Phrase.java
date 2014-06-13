public class Phrase extends List {

	int[] notes;
	int[] instruments;
	int[] fxs;
	int[] fxVals;

	Phrase(boolean exists) {
		super(exists);
		this.notes=new int[16];
		this.instruments=new int[16];
		this.fxs=new int[16];
		this.fxVals=new int[16];
		//default values		
		for (int i=0;i<16;i++) {
			notes[i]=0xFF;
			instruments[i]=0xFF;
			fxs[i]=0xFF;
			notes[i]=0xFF;
		}
	}

	public Phrase(int index, int[] notes, int[] instruments, int[] fxs,
			int[] fxVals) {
		super(true, index);
		this.notes = notes;
		this.instruments = instruments;
		this.fxs = fxs;
		this.fxVals = fxVals;
	}

	public void replaceInstrumentIndex(Instr instr) {
		for (int i = 0; i < 16; i++) {
			if (instruments[i] == instr.oldIndex)
				instruments[i] = instr.newIndex;
		}
	}

	public void replaceTableIndex(Table table) {
		for (int i = 0; i < 16; i++) {
			if (fxVals[i] == table.oldIndex && fxs[i] == 0x01)
				fxVals[i] = table.newIndex;
		}
	}

	public void replaceGrooveIndex(Groove groove) {
		for (int i = 0; i < 16; i++) {
			if (fxVals[i] == groove.oldIndex && fxs[i] == 0x06)
				fxVals[i] = groove.newIndex;
		}
	}

}
