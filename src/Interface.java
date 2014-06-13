import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

public class Interface {

	void run() {
		Frame frame = new Frame();
		FileDialog fileDialog = new FileDialog(frame, "open", FileDialog.LOAD);
		fileDialog.setVisible(true);
		Vector<Integer> data = new Vector<Integer>();
		try {
			// loads the file
			FileInputStream file = new FileInputStream(fileDialog
					.getDirectory()
					+ fileDialog.getFile());
			// puts each byte in the midiData vector
			while (file.available() > 0) {
				data.add(file.read());
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(data);
		data = unCompressRLE(data);
		System.out.println(data);
		try {
			FileOutputStream file = new FileOutputStream(fileDialog
					.getDirectory()
					+ "Uncompressed" + fileDialog.getFile());
			for (int i = 0; i < data.size(); i++) {
				file.write(data.get(i));
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Vector<Integer> unCompressRLE(Vector<Integer> data) {
		Vector<Integer> result = new Vector<Integer>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) == 0xC0) {
				int value = data.get(++i);
				if (value != 0xC0) {
					int nb = data.get(++i);
					for (int j = 0; j < nb; j++) {
						result.add(value);
					}
				} else {
					result.add(0xC0);
				}
			} else if (data.get(i) == 0xE0) {
				int value = data.get(++i);
				if (value != 0xE0) {
					// TODO bank switching stuff
					if (value == 0xF1) {
						// default instrument
						int nb = data.get(++i);
						for (int j = 0; j < nb; j++) {
							result.add(0xa8);
							result.add(0x0);
							result.add(0x0);
							result.add(0xFF);
							result.add(0x0);
							result.add(0x0);
							result.add(0x3);
							result.add(0x0);
							result.add(0x0);
							result.add(0xd0);
							result.add(0x0);
							result.add(0x0);
							result.add(0x0);
							result.add(0xf3);
							result.add(0x0);
							result.add(0x0);
						}
					} else if (value == 0xF0) {
						// default wave
						int nb = data.get(++i);
						for (int j = 0; j < nb; j++) {
							result.add(0x8e);
							result.add(0xcd);
							result.add(0xcc);
							result.add(0xbb);
							result.add(0xaa);
							result.add(0xa9);
							result.add(0x99);
							result.add(0x88);
							result.add(0x87);
							result.add(0x76);
							result.add(0x66);
							result.add(0x55);
							result.add(0x54);
							result.add(0x43);
							result.add(0x32);
							result.add(0x31);
						}
					}
				} else {
					result.add(0xE0);
				}

			} else {
				result.add(data.get(i));
			}

		}
		return result;
	}

	public static void main(String[] args) {
		Interface i = new Interface();
		i.run();
	}

}
