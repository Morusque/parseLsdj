public class Chain extends List {

	int[] phrases = new int[16];
	int[] transps = new int[16];

	public Chain(boolean exists) {
		super(exists);
		this.phrases = new int[16];
		this.transps = new int[16];
		// default values
		for (int i = 0; i < 16; i++) {
			phrases[i] = 0xFF;
			transps[i] = 0;
		}
	}

	public Chain(int index, int[] phrases, int[] transposes) {
		super(true, index);
		this.phrases = phrases;
		this.transps = transposes;
	}

	public boolean contains(int i) {
		// returns true if it contains the phrase i
		for (int j = 0; j < 16; j++) {
			if (phrases[j] == i)
				return true;
		}
		return false;
	}

	public void replacePhraseIndex(Phrase phrase) {
		for (int i = 0; i < 16; i++) {
			if (phrases[i] == phrase.oldIndex)
				phrases[i] = phrase.newIndex;
		}
	}

}
