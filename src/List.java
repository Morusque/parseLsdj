public class List {

	public int getNewIndex() {
		return newIndex;
	}

	public void setNewIndex(int newIndex) {
		this.newIndex = newIndex;
	}

	public boolean exists;
	int oldIndex;
	int newIndex;

	List(boolean exists) {
		this.exists = exists;
	}

	List(boolean exists, int oldIndex) {
		this.exists = exists;
		this.oldIndex = oldIndex;
		this.newIndex = -1;
	}

}
