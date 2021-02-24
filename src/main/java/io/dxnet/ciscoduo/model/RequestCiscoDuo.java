package io.dxnet.ciscoduo.model;

public class RequestCiscoDuo {
    private String username;
    private String uuid;
    private String doorId;
    private String domain;
    private String message;
   
    public RequestCiscoDuo(){
        
    }
    public RequestCiscoDuo(String username) {
		this.username = username;
	}

	public String getUsername() {
		return this.username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }

    public String getUuid() {
      return uuid;
    }

    public void setUuid(String uuid) {
      this.uuid = uuid;
    }

    public String getDoorId() {
      return doorId;
    }

    public void setDoorId(String doorId) {
      this.doorId = doorId;
    }

    public String getDomain() {
      return domain;
    }

    public void setDomain(String domain) {
      this.domain = domain;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

}
