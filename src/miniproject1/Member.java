package miniproject1;

import java.util.Date;

import lombok.Data;

@Data
public class Member {
	private String mid;
	private String mpassword;
	private String mname;
	private String mphoneNumber;
	private String maddress;
	private String msex;
	private Date mloginHistory;
	private Date mlogoutHistory;
	private int mdeleted;	
	
}