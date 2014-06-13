import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

public class SeparateLists {

	int index = 0;

	// create arrays of final objects
	Vector<Integer> finalSong = new Vector<Integer>();
	Chain[] finalChains = new Chain[0x80];
	Phrase[] finalPhrases = new Phrase[0xFF];
	Instr[] finalInstruments = new Instr[0x40];
	Table[] finalTables = new Table[0x20];
	Synth[] finalSynths = new Synth[0x10];
	Groove[] finalGrooves = new Groove[0x20];
	Vector<Integer> newSav = new Vector<Integer>();

	void init() {
		// fill these objects with empty content
		for (int i = 0; i < finalChains.length; i++) {
			finalChains[i] = new Chain(false);
		}
		for (int i = 0; i < finalPhrases.length; i++) {
			finalPhrases[i] = new Phrase(false);
		}
		for (int i = 0; i < finalInstruments.length; i++) {
			finalInstruments[i] = new Instr(false);
		}
		for (int i = 0; i < finalTables.length; i++) {
			finalTables[i] = new Table(false);
		}
		for (int i = 0; i < finalSynths.length; i++) {
			finalSynths[i] = new Synth(false);
		}
		for (int i = 0; i < finalGrooves.length; i++) {
			finalGrooves[i] = new Groove(false);
		}
	}

	void run() {
		init();
		// first file
		Frame frame = new Frame();
		FileDialog fileDialog = new FileDialog(frame, "open", FileDialog.LOAD);
		fileDialog.setVisible(true);
		String fileAdress = fileDialog.getDirectory() + fileDialog.getFile();
		process(fileAdress);
		// second file
		fileDialog.setVisible(true);
		fileAdress = fileDialog.getDirectory() + fileDialog.getFile();
		process(fileAdress);
		// save it
		fileDialog = new FileDialog(frame, "save", FileDialog.SAVE);
		fileDialog.setVisible(true);
		fileAdress = fileDialog.getDirectory() + fileDialog.getFile();
		if (fileDialog.getDirectory() != null)
			save(fileAdress);
	}

	private void save(String fileAdress) {
		try {
			FileOutputStream file = new FileOutputStream(fileAdress + ".sav");
			for (int i = 0; i < newSav.size(); i++) {
				file.write(newSav.get(i));
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void process(String fileAdress) {
		Vector<Integer> data = new Vector<Integer>();
		try {
			// loads the file
			FileInputStream file = new FileInputStream(fileAdress);
			// puts each byte in the midiData vector
			while (file.available() > 0) {
				data.add(file.read());
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		index = 0;

		int[] notesInPhrases = extractFrom(data, 0x0FF0);
		int[] emptyA = extractFrom(data, 0xA0);
		int[] grooves = extractFrom(data, 0x200);
		int[] chainsInSong = extractFrom(data, 0x400);
		int[] envelopesInTables = extractFrom(data, 0x200);
		int[] wordsInSpeech = extractFrom(data, 0x540);
		int[] namesInSpeech = extractFrom(data, 0xA8);
		int[] memInitFlagA = extractFrom(data, 0x2);
		int[] namesInInstr = extractFrom(data, 0x140);
		index += 70;// skip to next bank
		int[] emptyB = extractFrom(data, 0x20);
		int[] tableAllocTable = extractFrom(data, 0x20);
		int[] instrAllocTable = extractFrom(data, 0x40);
		int[] phrasesInChains = extractFrom(data, 0x800);
		int[] transpoInChains = extractFrom(data, 0x800);
		int[] paramInInstr = extractFrom(data, 0x400);
		int[] transpoInTables = extractFrom(data, 0x200);
		int[] fxInTables = extractFrom(data, 0x200);
		int[] fxValInTables = extractFrom(data, 0x200);
		int[] fx2InTables = extractFrom(data, 0x200);
		int[] fx2ValInTables = extractFrom(data, 0x200);
		int[] memInitFlagB = extractFrom(data, 0x2);
		int[] phraseAllocTable = extractFrom(data, 0x20);
		int[] chainAllocTable = extractFrom(data, 0x10);
		int[] softsynthParam = extractFrom(data, 0x100);
		int[] songSettings = extractFrom(data, 0xF);// last parts of bank 1
		index += 63;// skip to next bank
		int[] fxInPhrases = extractFrom(data, 0xFF0);
		int[] fxValInPhrases = extractFrom(data, 0xFF0);
		index += 32;// skip to next bank
		int[] waveFrames = extractFrom(data, 0x1000);
		int[] instrInPhrases = extractFrom(data, 0xFF0);
		int[] memInitFlagC = extractFrom(data, 0x3);

		int endOfSong = 0;// index of the last filled raw
		Vector<Integer> usedChains = new Vector<Integer>();
		Vector<Integer> usedPhrases = new Vector<Integer>();
		Vector<Integer> usedInstruments = new Vector<Integer>();
		Vector<Integer> usedTables = new Vector<Integer>();
		Vector<Integer> usedSynths = new Vector<Integer>();
		Vector<Integer> usedGrooves = new Vector<Integer>();

		// this song
		Vector<Integer> thisSong = new Vector<Integer>();

		// finds the end of the song
		for (int i = chainsInSong.length - 1; i >= 0 && endOfSong == 0; i--) {
			if (chainsInSong[i] != 0xFF) {
				endOfSong = i;
			}
		}

		// sets it to the last channel
		while (endOfSong % 4 != 0) {
			endOfSong++;
		}

		// finds the used chains
		for (int i = 0; i < endOfSong; i++) {
			if (!findsValueIn(chainsInSong[i], usedChains)
					&& chainsInSong[i] != 0xFF)
				usedChains.add(chainsInSong[i]);
			thisSong.add(chainsInSong[i]);
		}

		// finds the used phrases
		for (int i = 0; i < usedChains.size(); i++) {
			for (int j = 0; j < 16; j++) {
				int thisPhrase = phrasesInChains[usedChains.get(i) * 16 + j];
				if (!findsValueIn(thisPhrase, usedPhrases)
						&& thisPhrase != 0xFF)
					usedPhrases.add(thisPhrase);
			}
		}

		// finds the used instruments
		for (int i = 0; i < usedPhrases.size(); i++) {
			for (int j = 0; j < 16; j++) {
				int thisInstr = instrInPhrases[usedPhrases.get(i) * 16 + j];
				if (!findsValueIn(thisInstr, usedInstruments)
						&& thisInstr != 0xFF)
					usedInstruments.add(thisInstr);
			}
		}

		// finds the used tables
		// from phrase effects
		for (int i = 0; i < usedPhrases.size(); i++) {
			for (int j = 0; j < 16; j++) {
				if (fxInPhrases[usedPhrases.get(i) * 16 + j] == 0x01) {
					int thisTable = fxValInPhrases[usedPhrases.get(i) * 16 + j];
					if (!findsValueIn(thisTable, usedTables)
							&& thisTable != 0xFF)
						usedTables.add(thisTable);
				}
			}
		}
		// from instruments
		int mask_table = 0x000000DF;
		for (int i = 0; i < usedInstruments.size(); i++) {
			int thisParam = paramInInstr[usedInstruments.get(i) * 16 + 6];
			int thisTable = thisParam & mask_table;
			// TODO what happens if the table is set to zero ?
			if (!findsValueIn(thisTable, usedTables) && thisParam != 0x00)
				usedTables.add(thisTable);
		}

		// finds the used synths (waveform groups)
		// from instruments
		int mask_wave = 0x000000F0;
		for (int i = 0; i < usedInstruments.size(); i++) {
			int thisParam = paramInInstr[usedInstruments.get(i) * 16 + 7];
			int thisSynth = thisParam & mask_wave;
			if (!findsValueIn(thisSynth, usedSynths) && thisSynth != 0x40
					&& thisSynth != 0x80 && thisSynth != 0xC0)
				usedSynths.add(thisSynth);
		}

		// finds the used grooves
		// from phrase effects
		for (int i = 0; i < usedPhrases.size(); i++) {
			for (int j = 0; j < 16; j++) {
				if (fxInPhrases[usedPhrases.get(i) * 16 + j] == 0x06) {
					int thisGroove = fxValInPhrases[usedPhrases.get(i) * 16 + j];
					if (!findsValueIn(thisGroove, usedGrooves))
						usedGrooves.add(thisGroove);
				}
			}
		}

		// create vectors of current objects
		Vector<Chain> theseChains = new Vector<Chain>();
		Vector<Phrase> thesePhrases = new Vector<Phrase>();
		Vector<Instr> theseInstruments = new Vector<Instr>();
		Vector<Table> theseTables = new Vector<Table>();
		Vector<Synth> theseSynths = new Vector<Synth>();
		Vector<Groove> theseGrooves = new Vector<Groove>();

		// fill current objects arrays

		// chains
		for (int i = 0; i < usedChains.size(); i++) {
			int index = usedChains.get(i);
			// extract phrases
			int[] phrases = new int[16];
			for (int j = 0; j < 16; j++) {
				phrases[j] = phrasesInChains[index * 16 + j];
			}
			// extract transposes
			int[] transposes = new int[16];
			for (int j = 0; j < 16; j++) {
				phrases[j] = transpoInChains[index * 16 + j];
			}
			theseChains.add(new Chain(index, phrases, transposes));
		}

		// phrases
		for (int i = 0; i < usedPhrases.size(); i++) {
			int index = usedPhrases.get(i);
			// extract notes
			int[] notes = new int[16];
			for (int j = 0; j < 16; j++) {
				notes[j] = notesInPhrases[index * 16 + j];
			}
			// extract instruments
			int[] instruments = new int[16];
			for (int j = 0; j < 16; j++) {
				instruments[j] = instrInPhrases[index * 16 + j];
			}
			// extract effects
			int[] fxs = new int[16];
			for (int j = 0; j < 16; j++) {
				fxs[j] = fxInPhrases[index * 16 + j];
			}
			// extract effect values
			int[] fxVals = new int[16];
			for (int j = 0; j < 16; j++) {
				fxVals[j] = fxValInPhrases[index * 16 + j];
			}
			thesePhrases
					.add(new Phrase(index, notes, instruments, fxs, fxVals));
		}

		// instruments
		for (int i = 0; i < usedInstruments.size(); i++) {
			int index = usedInstruments.get(i);
			// extract parameters
			int[] params = new int[16];
			for (int j = 0; j < 16; j++) {
				params[j] = paramInInstr[index * 16 + j];
			}
			int[] name = new int[5];
			for (int j = 0; j < 5; j++) {
				name[j] = namesInInstr[index * 5 + j];
			}
			theseInstruments.add(new Instr(index, params, name));
		}

		// tables
		for (int i = 0; i < usedTables.size(); i++) {
			int index = usedTables.get(i);
			// extract volumes
			int[] volumes = new int[16];
			for (int j = 0; j < 16; j++) {
				volumes[j] = envelopesInTables[index * 16 + j];
			}
			// extract transpositions
			int[] transps = new int[16];
			for (int j = 0; j < 16; j++) {
				transps[j] = transpoInTables[index * 16 + j];
			}
			// extract first effects
			int[] fx1 = new int[16];
			for (int j = 0; j < 16; j++) {
				fx1[j] = fxInTables[index * 16 + j];
			}
			// extract first effect values
			int[] fx1Val = new int[16];
			for (int j = 0; j < 16; j++) {
				fx1Val[j] = fxValInTables[index * 16 + j];
			}
			// extract second effects
			int[] fx2 = new int[16];
			for (int j = 0; j < 16; j++) {
				fx2[j] = fx2InTables[index * 16 + j];
			}
			// extract second effect values
			int[] fx2Val = new int[16];
			for (int j = 0; j < 16; j++) {
				fx2Val[j] = fx2ValInTables[index * 16 + j];
			}
			theseTables.add(new Table(index, volumes, transps, fx1, fx1Val,
					fx2, fx2Val));
		}

		// grooves
		for (int i = 0; i < usedGrooves.size(); i++) {
			int index = usedGrooves.get(i);
			int[] values = new int[16];
			for (int j = 0; j < 16; j++) {
				values[j] = grooves[index * 16 + j];
			}
			theseGrooves.add(new Groove(index, values));
		}

		// synths
		for (int i = 0; i < usedSynths.size(); i++) {
			int index = usedSynths.get(i);
			int[] params = new int[16];
			for (int j = 0; j < 16; j++) {
				params[j] = softsynthParam[index * 16 + j];
			}
			int[] waveforms = new int[16 * 16];
			for (int j = 0; j < 16 * 16; j++) {
				waveforms[j] = waveFrames[index * (16 * 16) + j];
			}
			theseSynths.add(new Synth(index, params, waveforms));
		}

		// set new indexes

		// for chains
		for (int i = 0; i < theseChains.size(); i++) {
			boolean[] finalExists = new boolean[finalChains.length];
			for (int j = 0; j < finalChains.length; j++) {
				finalExists[j] = finalChains[j].exists;
			}
			int[] theseNewIndexes = new int[theseChains.size()];
			for (int j = 0; j < theseChains.size(); j++) {
				theseNewIndexes[j] = theseChains.get(j).newIndex;
			}
			int newIndex = searchForFreeSlot(finalChains.length, finalExists,
					theseNewIndexes, i, theseChains.get(i).oldIndex);
			if (newIndex != -1) {
				theseChains.get(i).setNewIndex(newIndex);
			}
		}

		// for phrases
		for (int i = 0; i < thesePhrases.size(); i++) {
			boolean[] finalExists = new boolean[finalPhrases.length];
			for (int j = 0; j < finalPhrases.length; j++) {
				finalExists[j] = finalPhrases[j].exists;
			}
			int[] theseNewIndexes = new int[thesePhrases.size()];
			for (int j = 0; j < thesePhrases.size(); j++) {
				theseNewIndexes[j] = thesePhrases.get(j).newIndex;
			}
			int newIndex = searchForFreeSlot(finalPhrases.length, finalExists,
					theseNewIndexes, i, thesePhrases.get(i).oldIndex);
			if (newIndex != -1) {
				thesePhrases.get(i).setNewIndex(newIndex);
			}
		}

		// instruments
		for (int i = 0; i < theseInstruments.size(); i++) {
			boolean[] finalExists = new boolean[finalInstruments.length];
			for (int j = 0; j < finalInstruments.length; j++) {
				finalExists[j] = finalInstruments[j].exists;
			}
			int[] theseNewIndexes = new int[theseInstruments.size()];
			for (int j = 0; j < theseInstruments.size(); j++) {
				theseNewIndexes[j] = theseInstruments.get(j).newIndex;
			}
			int newIndex = searchForFreeSlot(finalInstruments.length,
					finalExists, theseNewIndexes, i,
					theseInstruments.get(i).oldIndex);
			if (newIndex != -1) {
				theseInstruments.get(i).setNewIndex(newIndex);
			}
		}

		// tables
		for (int i = 0; i < theseTables.size(); i++) {
			boolean[] finalExists = new boolean[finalTables.length];
			for (int j = 0; j < finalTables.length; j++) {
				finalExists[j] = finalTables[j].exists;
			}
			int[] theseNewIndexes = new int[theseTables.size()];
			for (int j = 0; j < theseTables.size(); j++) {
				theseNewIndexes[j] = theseTables.get(j).newIndex;
			}
			int newIndex = searchForFreeSlot(finalTables.length, finalExists,
					theseNewIndexes, i, theseTables.get(i).oldIndex);
			if (newIndex != -1) {
				theseTables.get(i).setNewIndex(newIndex);
			}
		}

		// grooves
		for (int i = 0; i < theseGrooves.size(); i++) {
			boolean[] finalExists = new boolean[finalGrooves.length];
			for (int j = 0; j < finalGrooves.length; j++) {
				finalExists[j] = finalGrooves[j].exists;
			}
			int[] theseNewIndexes = new int[theseGrooves.size()];
			for (int j = 0; j < theseGrooves.size(); j++) {
				theseNewIndexes[j] = theseGrooves.get(j).newIndex;
			}
			int newIndex = searchForFreeSlot(finalGrooves.length, finalExists,
					theseNewIndexes, i, theseGrooves.get(i).oldIndex);
			if (newIndex != -1) {
				theseGrooves.get(i).setNewIndex(newIndex);
			}
		}

		// synths
		for (int i = 0; i < theseSynths.size(); i++) {
			boolean[] finalExists = new boolean[finalSynths.length];
			for (int j = 0; j < finalSynths.length; j++) {
				finalExists[j] = finalSynths[j].exists;
			}
			int[] theseNewIndexes = new int[theseSynths.size()];
			for (int j = 0; j < theseSynths.size(); j++) {
				theseNewIndexes[j] = theseSynths.get(j).newIndex;
			}
			int newIndex = searchForFreeSlot(finalSynths.length, finalExists,
					theseNewIndexes, i, theseSynths.get(i).oldIndex);
			if (newIndex != -1) {
				theseSynths.get(i).setNewIndex(newIndex);
			}
		}

		// replace indexes in lists

		// song
		for (int i = 0; i < thisSong.size(); i++) {
			for (int j = 0; j < theseChains.size(); j++) {
				if (theseChains.get(j).oldIndex == thisSong.get(i)) {
					thisSong.set(i, theseChains.get(j).newIndex);
				}
			}
		}

		// chains
		for (int i = 0; i < theseChains.size(); i++) {
			for (int j = 0; j < thesePhrases.size(); j++) {
				theseChains.get(i).replacePhraseIndex(thesePhrases.get(j));
			}
		}

		// phrases
		for (int i = 0; i < thesePhrases.size(); i++) {
			// instruments
			for (int j = 0; j < theseInstruments.size(); j++) {
				thesePhrases.get(i).replaceInstrumentIndex(
						theseInstruments.get(j));
			}
			// tables
			for (int j = 0; j < theseTables.size(); j++) {
				thesePhrases.get(i).replaceTableIndex(theseTables.get(j));
			}
			// grooves
			for (int j = 0; j < theseGrooves.size(); j++) {
				thesePhrases.get(i).replaceGrooveIndex(theseGrooves.get(j));
			}
		}

		// instruments
		for (int i = 0; i < theseInstruments.size(); i++) {
			// synths
			for (int j = 0; j < theseSynths.size(); j++) {
				theseInstruments.get(i).replaceSynthsIndex(theseSynths.get(j));
			}
			// tables
			for (int j = 0; j < theseTables.size(); j++) {
				theseInstruments.get(i).replaceTableIndex(theseTables.get(j));
			}
		}

		// tables
		for (int i = 0; i < theseTables.size(); i++) {
			// grooves
			for (int j = 0; j < theseGrooves.size(); j++) {
				theseTables.get(i).replaceGrooveIndex(theseGrooves.get(j));
			}
		}

		// adds current objects to final objects

		// song
		for (int i = 0; i < thisSong.size(); i++) {
			finalSong.add(thisSong.get(i));
		}

		// chains
		for (int i = 0; i < theseChains.size(); i++) {
			if (theseChains.get(i).newIndex != -1)
				finalChains[theseChains.get(i).newIndex] = theseChains.get(i);
		}

		// phrases
		for (int i = 0; i < thesePhrases.size(); i++) {
			if (thesePhrases.get(i).newIndex != -1)
				finalPhrases[thesePhrases.get(i).newIndex] = thesePhrases
						.get(i);
		}

		// instruments
		for (int i = 0; i < theseInstruments.size(); i++) {
			if (theseInstruments.get(i).newIndex != -1)
				finalInstruments[theseInstruments.get(i).newIndex] = theseInstruments
						.get(i);
		}

		// tables
		for (int i = 0; i < theseTables.size(); i++) {
			if (theseTables.get(i).newIndex != -1)
				finalTables[theseTables.get(i).newIndex] = theseTables.get(i);
		}

		// grooves
		for (int i = 0; i < theseGrooves.size(); i++) {
			if (theseGrooves.get(i).newIndex != -1)
				finalGrooves[theseGrooves.get(i).newIndex] = theseGrooves
						.get(i);
		}

		// synths
		for (int i = 0; i < theseSynths.size(); i++) {
			if (theseSynths.get(i).newIndex != -1)
				finalSynths[theseSynths.get(i).newIndex] = theseSynths.get(i);
		}

		// put everything back in an array
		newSav = new Vector<Integer>();

		// notes in phrases
		for (int i = 0; i < finalPhrases.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalPhrases[i].notes[j]);
			}
		}

		// emptyA
		for (int i = 0; i < emptyA.length; i++) {
			newSav.add(emptyA[i]);
		}

		// grooves
		for (int i = 0; i < finalGrooves.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalGrooves[i].values[j]);
			}
		}

		// chains in song
		for (int i = 0; i < 0x0400; i++) {
			if (i < finalSong.size()) {
				newSav.add(finalSong.get(i));
			} else {
				newSav.add(0xFF);
			}
		}

		// envelopes in tables
		for (int i = 0; i < finalTables.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalTables[i].volumes[j]);
			}
		}

		// words in speech
		for (int i = 0; i < wordsInSpeech.length; i++) {
			newSav.add(wordsInSpeech[i]);
		}

		// names in speech
		for (int i = 0; i < namesInSpeech.length; i++) {
			newSav.add(namesInSpeech[i]);
		}

		// memInitFlagA
		for (int i = 0; i < memInitFlagA.length; i++) {
			newSav.add(memInitFlagA[i]);
		}

		// names in instruments
		for (int i = 0; i < finalInstruments.length; i++) {
			for (int j = 0; j < 5; j++) {
				newSav.add(finalInstruments[i].name[j]);
			}
		}

		// skip to next bank
		for (int i = 0; i < 70; i++) {
			newSav.add(0);
		}

		// emptyB
		for (int i = 0; i < emptyB.length; i++) {
			newSav.add(emptyB[i]);
		}

		// tableAllocTable
		for (int i = 0; i < tableAllocTable.length; i++) {
			newSav.add(tableAllocTable[i]);
		}

		// instrAllocTable
		for (int i = 0; i < instrAllocTable.length; i++) {
			newSav.add(instrAllocTable[i]);
		}

		// phrases in chains
		for (int i = 0; i < finalChains.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalChains[i].phrases[j]);
			}
		}

		// transposes in chains
		for (int i = 0; i < finalChains.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalChains[i].transps[j]);
			}
		}

		// parameters in instruments
		for (int i = 0; i < finalInstruments.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalInstruments[i].parameters[j]);
			}
		}

		// transposes in tables
		for (int i = 0; i < finalTables.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalTables[i].transps[j]);
			}
		}

		// effects 1 in tables
		for (int i = 0; i < finalTables.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalTables[i].fx1[j]);
			}
		}

		// effect 1 values in tables
		for (int i = 0; i < finalTables.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalTables[i].fx1Val[j]);
			}
		}

		// effects 2 in tables
		for (int i = 0; i < finalTables.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalTables[i].fx2[j]);
			}
		}

		// effect 2 values in tables
		for (int i = 0; i < finalTables.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalTables[i].fx2Val[j]);
			}
		}

		// memInitFlagB
		for (int i = 0; i < memInitFlagB.length; i++) {
			newSav.add(memInitFlagB[i]);
		}

		// phraseAllocTable
		for (int i = 0; i < phraseAllocTable.length; i++) {
			newSav.add(phraseAllocTable[i]);
		}

		// chainAllocTable
		for (int i = 0; i < chainAllocTable.length; i++) {
			newSav.add(chainAllocTable[i]);
		}

		// synth params
		for (int i = 0; i < finalSynths.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalSynths[i].params[j]);
			}
		}

		// song settings
		for (int i = 0; i < songSettings.length; i++) {
			newSav.add(songSettings[i]);
		}

		// skip to next bank
		for (int i = 0; i < 63; i++) {
			newSav.add(0);
		}

		// effects in phrases
		for (int i = 0; i < finalPhrases.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalPhrases[i].fxs[j]);
			}
		}

		// effects values in phrases
		for (int i = 0; i < finalPhrases.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalPhrases[i].fxVals[j]);
			}
		}

		// skip to next bank
		for (int i = 0; i < 32; i++) {
			newSav.add(0);
		}

		// waveforms
		for (int i = 0; i < finalSynths.length; i++) {
			for (int j = 0; j < 16 * 16; j++) {
				newSav.add(finalSynths[i].waveforms[j]);
			}
		}

		// instruments in phrase
		for (int i = 0; i < finalPhrases.length; i++) {
			for (int j = 0; j < 16; j++) {
				newSav.add(finalPhrases[i].instruments[j]);
			}
		}

		// memInitFlagC
		for (int i = 0; i < memInitFlagC.length; i++) {
			newSav.add(memInitFlagC[i]);
		}

	}

	private int searchForFreeSlot(int maximumIndex, boolean[] finalExists,
			int[] theseNewIndexes, int currentI, int startingFrom) {
		int newIndex = startingFrom - 1;
		boolean freeSlot = false;
		while (!freeSlot) {
			newIndex++;
			freeSlot = true;
			// check in final lists
			if (finalExists[newIndex % maximumIndex])
				freeSlot = false;
			// check in other current lists
			for (int j = 0; j < theseNewIndexes.length; j++) {
				// don't check the current one
				if (j != currentI) {
					if (theseNewIndexes[j] == newIndex % maximumIndex)
						freeSlot = false;
				}
			}
			// if no slot left, just give up
			if (newIndex - startingFrom >= maximumIndex) {
				freeSlot = true;
			}
		}
		if (newIndex > maximumIndex)
			newIndex = -1;
		return newIndex % maximumIndex;
	}

	private boolean findsValueIn(int i, Vector<Integer> list) {
		// returns true is i is one of the values of the vector
		for (int j = 0; j < list.size(); j++) {
			if (list.get(j) == i) {
				return true;
			}
		}
		return false;
	}

	private int[] extractFrom(Vector<Integer> data, int length) {
		int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = data.get(index++);
		}
		return result;
	}

	@SuppressWarnings("unused")
	private void printAll(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + ";");
		}
		System.out.println();
		System.out.println("-----------------");
	}

	public static void main(String[] args) {
		SeparateLists s = new SeparateLists();
		s.run();
	}

}
