package duolthdelivery.api.rest;

public class ObjetoErro {

	private int status;
	private String erro;
	private String msg;
	private long timestamp;

	public ObjetoErro(int status, String erro, String msg) {
		this.status = status;
		this.erro = erro;
		this.msg = msg;
		this.timestamp = System.currentTimeMillis();
	}
	
	public int getStatus() { return status; }
	public String getErro() { return erro; }
	public String getMsg() { return msg; }
	public long getTimeStamp() { return timestamp; }

}
