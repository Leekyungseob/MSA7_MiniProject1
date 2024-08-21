package miniproject1;

import java.util.Date;

import lombok.Data;

@Data
public class Loginout_History {
	private int history_Id;
	private String mid;
	private Date login_Time;
	private Date logout_Time;
}