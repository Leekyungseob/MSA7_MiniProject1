package miniproject1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class MemberService {
		//비밀번호 초기화
		public void resetPassword(Connection conn, Scanner sc) {
			System.out.println("---------------------");
			System.out.println("비밀번호 초기화");
			
			String newPassword= "0000"; // 초기화 비밀번호는 0000
			
			//사용자로부터 아이디와 비밀번호 입력 받기
			System.out.println("아이디: ");
			String id = sc.nextLine();
			
			System.out.println("이름: ");
			String name = sc.nextLine();
			
			System.out.println("전화번호: ");
			String phoneNumber = sc.nextLine();
			
			//비밀번호 초기화
			try {
				String sql = "update member set mpassword=? where mid=? and mname=? and mphonenumber=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, newPassword);
				pstmt.setString(2, id);
				pstmt.setString(3, name);
				pstmt.setString(4, phoneNumber);
				
				int rowsUpdated = pstmt.executeUpdate(); //결과 건수를 리턴해줌
				
				if(rowsUpdated >0) {
					System.out.println("비밀번호가 성공적으로 초기화 되었습니다.(초기화 비밀번호는 0000입니다.)");
				} else {
					System.out.println("아이디가 존재하지 않습니다.");
				}
				
				pstmt.close();
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(0);
			}		
		}
}
