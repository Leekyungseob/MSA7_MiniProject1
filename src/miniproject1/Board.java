package miniproject1;

import java.util.Date;

import lombok.Data;

@Data
public class Board {
	private int bno;
	private String btitle;
	private String bcontent;
	private String bwriter;
	private int bviewCount;
	private Date created_at;
	private Date updated_at;
	private String status;
	private String bpassword;
}
