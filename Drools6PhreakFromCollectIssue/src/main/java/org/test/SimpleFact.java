package org.test;

public class SimpleFact {

	private String id;
	private int status;

	public SimpleFact(String anId) {
		this.id = anId;
		status = 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " (id=" + id + " status=" + status + ")";
	}
}
