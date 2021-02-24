package io.dxnet.ciscoduo.model;

public class ResponseCiscoDuo {

	private final long id;
	private final String content;

	public ResponseCiscoDuo(long id, String content) {
		this.id = id;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
}
