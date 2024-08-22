package miniproject1;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;


public class Miniproject1 {
	// 자바에서 데이터베이스와 연결을 유지하기 위해 사용하는 Connection객체를 선언
	private static Connection conn;
	private static Scanner sc = new Scanner(System.in);
	private static String menuNo; //메뉴 선택을 위한 변수
	private static String currentUserId;//현재 로그인한 사용자 ID를 저장하는 변수
	private static boolean logout = false; // 로그아웃시키기 위한 변수
	
	//페이지당 게시물 수
	private static final int POSTS_PER_PAGE =10;
	
	//현재 페이지 번호
	private static int currentPage=1;

	public static void main(String[] args) {
		try {
			//Class.forName("")메소드는 클래스 이름을 문자열로 받아 해당 클래스를 메모리에 동적으로 로드하고, 클래스의 class객체를 반환합니다.
			//로드된 클래스는 JVM에 의해 메모리에 적재됩니다.
			//oracle.jdbc.OracleDriver는 Oracle 디비에 접속하기 위한 JDBC 드라이버 클래스의 이름입니다. Oracle 디비와 연결을 지원합니다.
			
			Class.forName("oracle.jdbc.OracleDriver");
			//데이터베이스 연결을 설정하고, connection객체를 반환합니다.
			conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@localhost:1521/xe",
				"user01",
				"1004"
			);
					
		} catch(Exception e) {
			e.printStackTrace(); //예외가 발생한 위치와 예외가 발생하기까지의 메소드 호출 순서를 보여줌.
			System.exit(0); //java프로그램을 강제로 종료시키는 메소드
		}	
		
		//메인루프(초기화면)
		while(true) {
			System.out.println("--------------------");
			System.out.println("미니 프로젝트 1차");
			System.out.println("--------------------");
			System.out.println();
			
			System.out.println("1.회원가입");
			System.out.println("2.로그인");
			System.out.println("3.아이디 찾기");
			System.out.println("4.비밀번호 초기화");
			System.out.println("5.종료");
			
			System.out.print("원하는 기능? ");		
			menuNo= sc.nextLine();		
			
			switch (menuNo) {
				case "1":				
					registerMember(); //회원가입 기능
					break;						
					
				case "2":
					login(); //로그인 기능
					break;
					
				case "3":
					findId();//아이디 찾기
					break;
					
				case "4":
					//비밀번호 초기화 기능
					resetPassword();//패스워드 초기화
					break;
					
				case "5":
					System.out.println("프로그램을 종료합니다.");
					System.exit(0);//프로그램 종료
				default: //1,2,3,4,5에 해당하지 않을 경우
					System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
					break;					
			}		
		}		
	}


	//비밀번호 초기화
	private static void resetPassword() {
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
			//실행할 sql쿼리 문자열을 정의(?는 파라미터(입력값) 자리 표시자로 실제값은 PreparedStatement 객체에서 설정)
			String sql = "update member set mpassword=? where mid=? and mname=? and mphonenumber=?";
			//conn객체는 데이터베이스 연결을 나타내는 Connection 객체입니다.
			//PreparedStatement는 sql 쿼리를 실행하기 위한 객체입니다.
			PreparedStatement pstmt = conn.prepareStatement(sql);
			//파라미터 값 설정
			pstmt.setString(1, newPassword);
			pstmt.setString(2, id);
			pstmt.setString(3, name);
			pstmt.setString(4, phoneNumber);
			
			//쿼리 실행 및 결과를 처리
			//executeUpdate()메소드는 sql 쿼리를 실행하고, 결과 행의수를 반환합니다.
			int rowsUpdated = pstmt.executeUpdate(); 
			
			if(rowsUpdated >0) {
				System.out.println("비밀번호가 성공적으로 초기화 되었습니다.(초기화 비밀번호는 0000입니다.)");
			} else {
				System.out.println("잘못 입력하셧습니다.");
			}
			
			//PreparedStatement 객체를 닫아 리소스를 해제합니다.
			//데이터베이스 자원의 누수를 방지하고, 시스템의 효율성을 유지하는데 중요합니다.
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}		
	}

	//아이디찾기 메소드
	private static void findId() {
		System.out.println("--------------------------");
		System.out.println("아이디 찾기");
		
		//사용자로부터 이름과 전화번호 입력 받기
		System.out.println("이름: ");
		String name = sc.nextLine();
		
		System.out.println("전화번호: ");
		String phoneNumber = sc.nextLine();		
		
		//아이디 찾는 부분(이름과 전화번호 입력)
		try {
			String sql = "select mid from member where mname=? and mphonenumber=? and mdeleted=0";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, phoneNumber);
			
			//executeQuery()메소드는 주로 select 쿼리를 실행하고, 쿼리 결과를 ResultSet 객체로 반환합니다.
			ResultSet rs = pstmt.executeQuery();		
			
			if(rs.next()) {
				//일치하는 아이디가 있는 경우
				System.out.println("찾은 아이디: " + rs.getString("mid"));
			}else {
				//일치하는 아이디가 없을 경우
				System.out.println("입력한 정보와 일치하는 아이디가 없습니다.");
			}
			rs.close();
			pstmt.close();
			
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}



	//로그인
	private static void login() {
		//로그인 시작할때마다 logout변수를 false로 재설정해줘야 회원탈퇴후 로그인이 정상적으로 작동함
		logout=false;
		
		System.out.println("------------------------");
		System.out.println("");
		System.out.println("로그인 화면");
		
		System.out.println("아이디: ");
		String mid=sc.nextLine();
		
		System.out.println("비밀번호: ");
		String mpassword = sc.nextLine();
		
		try {
			//데이터베이스에서 입력한 아이디와 일치하는 회원정보 조회
			String sql = "select mpassword,mdeleted from member where mid =?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mid);
			ResultSet rs = pstmt.executeQuery();
						
			
			if(rs.next()) {
				String dbPassword = rs.getString("mpassword");
				String mdeleted = rs.getString("mdeleted");
				
				//계정이 탈퇴(비활성화)되었는지 확인
				if(mdeleted.equals("1")) {
					System.out.println("해당 계정은 탈퇴된 계정입니다.");
					return; //로그인 차단
				}
				
				//입력한 비밀번호와 데이터베이스에 저장된 비밀번호가 일치하는지 확인
				//dbPassword는 데이터베이스에 저장된 비밀번호, mpassword는 입력한 비밀번호
				if(dbPassword.equals(mpassword)) {
					System.out.println("로그인 성공! 환영합니다.");
					
					//관리자 여부 확인(관리자만 사용하는 메뉴나 기능을 사용하기 위함)
					String adminCheckSql = "select is_admin from member where mid=?";
					PreparedStatement adminCheckPstmt = conn.prepareStatement(adminCheckSql);
					adminCheckPstmt.setString(1,  mid);
					ResultSet adminCheckRs = adminCheckPstmt.executeQuery();
					
					boolean isAdmin=false;
					if(adminCheckRs.next()) {
						isAdmin =adminCheckRs.getInt("is_admin") ==1; //관리자 계정일경우 1이랑 같아서 결과는 true
					} else {
						System.out.println("관리자 정보가 조회되지 않았습니다.");
					}
					adminCheckRs.close();
					adminCheckPstmt.close();
					
					
					//로그인 처리(로그인 시간이 loginout_history,member테이블에 기록됌)
					//데이터베이스의 current_timestamp 기능을 사용하여 자동으로 현재 시각으로 설정합니다.
					String loginSql = "insert into loginout_history(mid,login_time) values(?,current_timestamp)";
					PreparedStatement loginStmt = conn.prepareStatement(loginSql);
					loginStmt.setString(1, mid);
					                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      loginStmt.executeUpdate();				
					
					
					
					currentUserId =mid; //로그인한 유저 아이디 세팅
					
					while(!logout) { //로그아웃 변수가 false일 동안 루프함.
						System.out.println("------------------------------");
						System.out.println("1. 나의 정보 확인");
						System.out.println("2. 게시물 목록");
						System.out.println("3. 회원 목록(관리자인 경우)");
						System.out.println("4. 로그아웃");
						System.out.println("5. 게시물 등록");
						System.out.println("6. 회원정보 수정");
						System.out.println("7. 회원탈퇴");
						System.out.println("8. 비밀번호 초기화");
						System.out.println("9. 종료");
						
						System.out.println("원하는 기능? ");
						menuNo=sc.nextLine();
						
						switch(menuNo) {
						case "1":
							//나의 정보 확인 기능 호출
							myInfo(mid);
							break;
						case "2":
							//게시물 목록 보기 기능 호출
							viewPosts();
							break;
						case "3":
							
							if(isAdmin) {
								//관리자용 회원 목록 보기 기능 호출
								viewMemberList(mid);
							} else {
								System.out.println("관리자만 이용 가능합니다.");
							}
							break;
						case "4":
							logout();						
							break;
						case "5":
							registerPost();
							break;
						case "6":
							memberUpdate();
							break;
						case "7":
							memberLeave();
							break;
						case "8":							
							loginResetPassword(currentUserId);
							break;
						case "9":
							//프로그램 종료
							System.out.println("프로그램을 종료합니다.");
							System.exit(0);
						default: //일치하는 값이 없을때 default문 실행
							System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
							break;
						}
					}
				}else {
					System.out.println("비밀번호가 일치하지 않습니다.");
				}
			}else {
				System.out.println("존재하지 않는 아이디입니다.");
			}
			
			rs.close();
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
	}
	
	//로그인후 패스워드 초기화
	private static void loginResetPassword(String userId) {
		try {			
			System.out.println("새 비밀번호: ");
			String newpassword = sc.nextLine();
			
			//비밀번호를 업데이트하는 SQL 쿼리
			String sql = "update member set mpassword=? where mid=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, newpassword);
			pstmt.setString(2, userId);
			
			int rowsUpdated = pstmt.executeUpdate();
			
			if(rowsUpdated>0) {
				System.out.println("비밀번호가 성공적으로 변경되었습니다.");
			}else {
				System.out.println("비밀번호 변경에 실패했습니다. 사용자 정보를 확인해주세요.");
			}
			pstmt.close();
		} catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		
	}


	//로그아웃
	private static void logout() {
		try {
	        String logoutSql = "update loginout_history set logout_time= current_timestamp where mid=? and logout_time is null";
	        PreparedStatement logoutStmt = conn.prepareStatement(logoutSql);
	        logoutStmt.setString(1, currentUserId);
	        logoutStmt.executeUpdate();

	        logoutStmt.close();
	        currentUserId = null;
	        logout = true;  // 로그아웃 플래그 설정
	        System.out.println("로그아웃 되었습니다.");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	}

	//회원탈퇴
	private static void memberLeave() {
		System.out.println("----------------------------");		
		
		try {
			//로그인한 계정이 관리자인지 확인
			String checkAdminSql = "select is_admin from member where mid=?";
			PreparedStatement checkAdminPstmt = conn.prepareStatement(checkAdminSql);
			checkAdminPstmt.setString(1, currentUserId);
			ResultSet checkAdminRs = checkAdminPstmt.executeQuery();
			
			boolean isAdmin = false;			
			
			if(checkAdminRs.next()) {
				isAdmin = checkAdminRs.getInt("is_admin")==1; //관리자 계정일경우 trun
			} 			
			checkAdminRs.close();
			checkAdminPstmt.close();
			
			//관리자 계정인 경우 비밀번호 확인 없이 바로 탈퇴 처리
			if(isAdmin) {
				//회원 탈퇴 처리
				String updateSql="update member set mdeleted='1' where mid=?";
				PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
	            updatePstmt.setString(1, currentUserId);
	            updatePstmt.executeUpdate();

	            System.out.println("회원 탈퇴가 성공적으로 처리되었습니다.");
	                
	            logout(); //회원 탈퇴후 자동 로그아웃 되도록 로그아웃 메소드 호출
	            return; //메소드 종료
			}		
		
			//관리자가 아닐경우 비밀번호 확인
			System.out.println("비밀번호: ");
			String inputPassword = sc.nextLine();
		
		
			//회원 비밀번호를 확인하기 위한 쿼리
			String sql = "select mpassword from member where mid=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, currentUserId);
			ResultSet rs =pstmt.executeQuery();
			
			if(rs.next()) {
				String dbPassword=rs.getString("mpassword");
				
				//입력한 비밀번호와 데이터베이스에 저장된 비밀번호가 일치하는지 확인
				if(dbPassword.equals(inputPassword)) {
					//비밀번호가 일치하면 회원 탈퇴 처리
					String updateSql="update member set mdeleted='1' where mid=?";
					PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
		            updatePstmt.setString(1, currentUserId);
		            updatePstmt.executeUpdate();

		            System.out.println("회원 탈퇴가 성공적으로 처리되었습니다.");
		                
		            logout(); //회원 탈퇴후 자동 로그아웃 되도록 로그아웃 메소드 호출
		                
				} else {
					System.out.println("비밀번호가 일치하지 않습니다. 회원탈퇴가 취소되었습니다.");
				}
			} else {
				System.out.println("회원 정보를 찾을 수 없습니다.");
			}
			rs.close();
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}		
	}

	//회원정보 수정
	private static void memberUpdate() {
		System.out.println("--------------------------------");
		System.out.println("회원정보 수정");
		
		//비밀번호 확인 절차
		System.out.println("비밀번호: ");
		String inputPassword=sc.nextLine();
		
		//로그인한 유저의 비밀번호를 가져오기
		try {
			String sql = "select mpassword from member where mid=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, currentUserId);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String dbPassword=rs.getString("mpassword");
				
				//입력한 비밀번호와 db의 비밀번호가 일치하는지 확인
				if(dbPassword.equals(inputPassword)) {
					//비밀번호가 일치하면 회원정보 수정 화면으로 이동
					System.out.println("비밀번호가 확인되었습니다. 회원정보를 수정하세요.");
					
					//새로운 정보 입력받기
					System.out.println("새 이름: ");
					String name = sc.nextLine();
					
					System.out.println("새 주소: ");
					String address = sc.nextLine();
					
					System.out.println("새 패스워드: ");
					String password = sc.nextLine();
					
					System.out.println("전화번호: ");
					String phoneNumber = sc.nextLine();
					
					//회원 정보 업데이트
					String UpdateSql = "update member set mname=?, maddress=?, mpassword=?, mphonenumber=? where mid=?";
					PreparedStatement updatePstmt = conn.prepareStatement(UpdateSql);
					updatePstmt.setString(1, name);
					updatePstmt.setString(2, address);
					updatePstmt.setString(3, password);
					updatePstmt.setString(4, phoneNumber);
					updatePstmt.setString(5, currentUserId);
					
					updatePstmt.executeUpdate();
					
					System.out.println("회원정보가 성공적으로 수정되었습니다.");
					
					updatePstmt.close();
				}else {
					//비밀번호가 일치하지 않을경우 오류메세지 출력
					System.out.println("비밀번호가 일치하지 않습니다. 회원정보 수정이 취소됩니다.");
				}
			} else {
				System.out.println("로그인된 회원 정보를 찾을 수 없습니다.");
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
	}

	//게시물 등록
	private static void registerPost() {
		try {
			System.out.println("---------------------------");
			System.out.println("게시물 등록");
			System.out.println("제목: ");
			String title= sc.nextLine();
			
			System.out.println("내용: ");
			String content = sc.nextLine();
			
			System.out.println("비밀번호: ");
			String password =sc.nextLine();
			
			String sql = "insert into board(btitle,bcontent,bwriter,bpassword) values(?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setString(3, currentUserId);
			pstmt.setString(4, password);
			
			pstmt.executeUpdate();
			
			pstmt.close();
			System.out.println("게시물이 성공적으로 등록되었습니다.");
			
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
	}


	//회원리스트(관리자만 접근 가능)
	private static void viewMemberList(String mid) {
		
		try {
			//관리자 권한인지 확인하는 부분
			String checkAdminSql = "select is_admin from member where mid=?";
			PreparedStatement checkAdminPstmt = conn.prepareStatement(checkAdminSql);
			checkAdminPstmt.setString(1, mid);
			ResultSet adminRs=checkAdminPstmt.executeQuery();
			
			if(adminRs.next()) {
				int isAdmin = adminRs.getInt("is_admin");
				if(isAdmin !=1) {
					System.out.println("관리자만 접근할 수 있습니다.");
					return;
				}
			} else {
				System.out.println("사용자 정보를 찾을 수 없습니다.");
				return;
			}
			adminRs.close();
			checkAdminPstmt.close();
			
			//전체 회원 수 계산
			String sqlCount = "select count(*) as total from member where mdeleted=0";
			PreparedStatement pstmtCount = conn.prepareStatement(sqlCount);
			ResultSet rsCount = pstmtCount.executeQuery();
			
			int totalMembers=0;
			
			if(rsCount.next()) {
				totalMembers = rsCount.getInt("total");
				
			}
			rsCount.close();
			pstmtCount.close();
			
			int totalPages = (int) Math.ceil((double) totalMembers / POSTS_PER_PAGE);
			
			if(currentPage>totalPages) {
				System.out.println("존재하지 않는 페이지입니다.");
				return;
			}
			
			//offset은 현재 페이지가 몇번째 행에서 시작할지 정해주는것(건너뛸 행수)
			int offset= (currentPage-1) * POSTS_PER_PAGE;
					
			//회원 목록 조회(페이징)
				//row_number는 over 이후에 조건에 순서대로 순번을 매겨준다.
				String sql = "select mid,mname from(" +
						"select mid,mname, row_number() over (order by mid asc) as rn from member where mdeleted=0" +
						") where rn > ? and rn <= ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, offset);
				pstmt.setInt(2, offset+POSTS_PER_PAGE);			    
				ResultSet rs = pstmt.executeQuery();
				
				System.out.println("회원 목록: ");
				//%n은 줄바꿈
				System.out.printf("%-10s %-10s%n","아이디", "이름"); //헤더 출력
				while(rs.next()) {
					System.out.printf("%-10s %-10s%n",rs.getString("mid"),rs.getString("mname"));
				}
				
				rs.close();
				pstmt.close();
				
				System.out.println("---------------------");
				System.out.println("현재페이지: " + currentPage + "/" + totalPages);
				System.out.println("1. 다음페이지");
				System.out.println("2. 이전페이지");
				System.out.println("3. 특정 페이지 이동");
				System.out.println("4. 이전화면으로");
				System.out.println("");
				System.out.println("원하는 메뉴는? ");
				
				String menuNo = sc.nextLine();
				switch(menuNo) {
					case "1":
						if(currentPage < totalPages) {
							currentPage++;
							viewMemberList(mid);
						} else {
							System.out.println("더 이상 다음 페이지가 없습니다.");
						}
						break;
					case "2":
						if(currentPage >1) {
							currentPage--;
							viewMemberList(mid);
						} else {
							System.out.println("더 이상 이전 페이지가 없습니다.");
						}
						break;
					case "3":
						System.out.println("이동할 페이지 번호: ");
						int pageNumber = Integer.parseInt(sc.nextLine());
						if(pageNumber>=1 && pageNumber <= totalPages) {
							currentPage = pageNumber;
							viewMemberList(mid);
						} else {
							System.out.println("존재하지 않는 페이지입니다.");
						}
						break;
					case "4":
						return; //이전 메뉴로 이동
					default:
						System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
						break;
				}
		
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}


	//게시물 목록
	private static void viewPosts() {
		System.out.println("[게시물 목록]");
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("%-8s%-15s%-12s%-13s%-13s\n", "게시물번호", "작성자", "제목", "읽은수" , "작성일" );
        System.out.println("-------------------------------------------------------------------------");
        
        try {
        	String sqlCount = "select count(*) as total from board";
        	PreparedStatement pstmtCount = conn.prepareStatement(sqlCount);
        	ResultSet rsCount= pstmtCount.executeQuery();
        	int totalPosts = 0;
        	
        	if(rsCount.next()) {
        		totalPosts = rsCount.getInt("total");
        	}
        	rsCount.close();
        	pstmtCount.close();
        	
        	int totalPages = (int) Math.ceil((double)totalPosts/ POSTS_PER_PAGE);
        	
        	if(currentPage>totalPages) {
        		System.out.println("존재하지 않는 페이지입니다.");
        		return;        		
        	}
        	
        	//offset은 현재페이지가 몇번째 행에서 시작할지 정해주는것(건너뛸 행수)
        	int offset = (currentPage-1) * POSTS_PER_PAGE;
        	String sql = "select * from (" +
        	"select a.*, ROWNUM rnum FROM( " +
        	"select bno, btitle, bviewcount, bwriter, created_at from board order by bno desc" +
        	") a where ROWNUM <=? " +
        	") where rnum > ?";
        			
        	PreparedStatement pstmt = conn.prepareStatement(sql);
        	//최대 선택할 행수
        	pstmt.setInt(1, offset + POSTS_PER_PAGE );
        	//건너뛸 행수
        	pstmt.setInt(2,  offset);
        	ResultSet rs = pstmt.executeQuery();
        	        	
        	//현재 시간 얻기
        	Calendar now = Calendar.getInstance();
        	
        	//날짜 및 시간 포맷 설정
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        	           
            while (rs.next()) {
            	//게시물 작성일 가져오기
            	Calendar createdAt=Calendar.getInstance();
            	createdAt.setTime(rs.getDate("created_at"));
            	            	
            	//최근 24시간 이내인지 확인
            	long timeDifference =now.getTimeInMillis()-createdAt.getTimeInMillis();
            	String displayDate;
            	if(timeDifference <= 24*60*60*1000) {
            		displayDate = timeFormat.format(createdAt.getTime()); //시:분 포맷            		
            	} else {
            		displayDate = dateFormat.format(createdAt.getTime()); //yyyy-MM-dd 포맷            	
            	}
            	
                System.out.printf("%-10d%-15s%-15s%-12d%-15s\n",
                    rs.getInt("bno"),
                    rs.getString("bwriter"),
                    rs.getString("btitle"),
                    rs.getInt("bviewcount"),
                    displayDate                   
                );
            }
            rs.close();
            pstmt.close();
            
            System.out.println("------------------------------");
            System.out.println("현재 페이지: " + currentPage + "/" + totalPages);
            System.out.println("1. 다음 페이지");
            System.out.println("2. 이전 페이지");
            System.out.println("3. 특정 페이지 이동");
            System.out.println("4. 이전 화면으로");
            System.out.println("5. 게시물 상세보기");
            System.out.println("");
            System.out.println("원하는 메뉴는? ");
            
            menuNo=sc.nextLine();
            switch(menuNo) {
	            case "1":
	            	if(currentPage<totalPages) {
	            		currentPage++;
	            		viewPosts();
	            	} else {
	            		System.out.println("더 이상 다음 페이지가 없습니다.");
	            		
	            	}
	            	break;
	            case "2":
	            	if(currentPage>1) {
	            		currentPage--;
	            		viewPosts();
	            	} else {
	            		System.out.println("더 이상 이전 페이지가 없습니다.");
	            		
	            	}
	            	break;
	            case "3":
	            	System.out.println("이동할 페이지 번호: ");
	            	int pageNumber= Integer.parseInt(sc.nextLine());
	            	if(pageNumber >=1 && pageNumber <=totalPages) {
	            		currentPage=pageNumber;
	            		viewPosts();
	            	} else {
	            		System.out.println("존재하지 않는 페이지입니다.");
	            	}
	            	break;
	            case "4":
	            	return; //이전메뉴로 되돌아감
	            	
	            case "5":
	            	detail();
	            	break;
	            default:
	            	System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
	            	break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}


	//게시물 상세보기
	private static void detail() {
		System.out.println("---------------------------------");
		System.out.println("게시물 상세보기");
		System.out.println();
		System.out.println("상세보기할 게시물 번호: ");
		
		int postNumber= Integer.parseInt(sc.nextLine());
		
		
		try {
			//먼저 해당 게시물 번호가 존재하는지 확인
			String checkSql = "select bno, bwriter from board where bno=?";
			PreparedStatement checkStmt;
			checkStmt = conn.prepareStatement(checkSql);
			checkStmt.setInt(1, postNumber);
			ResultSet checkRs = checkStmt.executeQuery();
			
			if(!checkRs.next()) {
				System.out.println("해당 게시물 번호가 존재하지 않습니다.");
				checkRs.close();
				checkStmt.close();
				return;
			}
			
			String postWriter = checkRs.getString("bwriter");
					
			checkRs.close();
			checkStmt.close();
			
			//조회수 증가
			String updateSql = "update board set bviewcount = bviewcount+1 where bno=?";
			PreparedStatement updateStmt = conn.prepareStatement(updateSql);
			updateStmt.setInt(1, postNumber);
			updateStmt.executeUpdate();
			updateStmt.close();
			
			//게시물 상세 정보 출력
			String detailsSql = "select bno,btitle,bcontent,bwriter,bviewcount,created_at from board where bno=?";			
			PreparedStatement detailStmt = conn.prepareStatement(detailsSql);
			detailStmt.setInt(1, postNumber);
			ResultSet detailRs= detailStmt.executeQuery();
			
			if(detailRs.next()) {
				System.out.println("게시물 번호: " + detailRs.getInt("bno"));
				System.out.println("제목: " + detailRs.getString("btitle"));
				System.out.println("내용: " + detailRs.getString("bcontent"));
				System.out.println("작성자: " + detailRs.getString("bwriter"));
				System.out.println("조회수: " + detailRs.getInt("bviewcount"));
				System.out.println("작성일: " + detailRs.getDate("created_at"));
			} else {
				System.out.println("해당 게시물을 찾을 수 없습니다.");
			}
			detailRs.close();
			detailStmt.close();
			
			String checkAdminSql = "select is_admin from member where mid=?";
			PreparedStatement checkAdminPstmt = conn.prepareStatement(checkAdminSql);
			checkAdminPstmt.setString(1, currentUserId);
			ResultSet checkAdminRs = checkAdminPstmt.executeQuery();
			int is_admin=0; //지역변수는 초기화 하지않으면 에러가 발생하기 때문에 0으로 일단 초기화 해준다.
			if(checkAdminRs.next()) {
				is_admin = checkAdminRs.getInt("is_admin");				
			} else {
				
			}
			checkAdminRs.close();
			checkAdminPstmt.close();
			
			
			//현재 로그인한 사용자와 게시물 작성자가 같은지 확인
			if(postWriter.equals(currentUserId) || (is_admin==1)) {
				//작성자인 경우 수정 및 삭제 메뉴를 보여줌
				System.out.println();
				System.out.println("1. 게시물 수정");
				System.out.println("2. 게시물 삭제");
				System.out.println("3. 이전 화면으로");
				System.out.println();
				System.out.println("원하는 메뉴는? ");
				
				menuNo = sc.nextLine();
				
				switch(menuNo) {
				case "1":
					updatePost(postNumber, is_admin==1); //게시물 수정 함수 호출(is_admin이 1인지 확인하고 맞으면 true, 틀리면 false를 반환한다.)
					break;
				case "2":
					deletePost(postNumber, is_admin==1);//게시물 삭제 함수 호출(is_admin이 1인지 확인하고 맞으면 true, 틀리면 false를 반환한다.)
					break;
				case "3":
					return;
				default:
					System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
					break;
				}
					
			}
			
		} catch (SQLException e) {			
			e.printStackTrace();
			System.exit(0);
		}
		
		
		
	}
	

	//게시물 삭제
	private static void deletePost(int postNumber, boolean isAdmin) {
		try {
			//저장 프로시저 호출 준비
			CallableStatement cstmt = conn.prepareCall("{call delete_post(?,?,?,?)}");
			
			//in 파라미터 설정
			cstmt.setInt(1, postNumber); //삭제할 게시물번호
			cstmt.setBoolean(2, isAdmin); //관리자 여부
			
			//비관리자일 경우에만 비밀번호 입력
			if(!isAdmin) {
				System.out.println("비밀번호: ");
				String password = sc.nextLine();
				cstmt.setString(3, password); //비밀번호 입력
			}else {				
				cstmt.setNull(3, java.sql.Types.VARCHAR); //관리자인 경우 비밀번호 null로 설정
				
			}
			
			//out 파라미터 등록
			cstmt.registerOutParameter(4, java.sql.Types.VARCHAR); //결과 메세지 받을 변수
			
			//프로시저 실행
			cstmt.execute();
			
			//결과 메세지 출력
			String resultMessage = cstmt.getString(4); //결과 메세지 받아옴
			System.out.println(resultMessage);
			
			//리소스 정리
			cstmt.close();		
			
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
	}

	//게시물 수정
	private static void updatePost(int postNumber, boolean isAdmin) {
		
		try {
			//기존 게시물 정보를 조회
			String selectSql = "select btitle,bcontent, bpassword from board where bno = ?";
			PreparedStatement selectStmt = conn.prepareStatement(selectSql);
			selectStmt.setInt(1, postNumber);
			ResultSet rs = selectStmt.executeQuery();
			
			if(rs.next()) {
				//기존 게시물 정보를 가져와서 사용자에게 보여줍니다.
				System.out.println("현재 제목: " + rs.getString("btitle"));
				System.out.println("현재 내용: " + rs.getString("bcontent"));
				
				//관리자 계정일 경우 비밀번호를 묻지 않고 바로 수정
				if(isAdmin) {
					//사용자로부터 새로운 제목과 내용을 입력 받습니다.
					System.out.println("새 제목: ");
					String newTitle = sc.nextLine();
					System.out.println("새 내용: ");
					String newContent = sc.nextLine();
					
					//게시물 정보를 업데이트 합니다.
					String updateSql="update board set btitle=?, bcontent=? where bno=?";
					PreparedStatement updateStmt = conn.prepareStatement(updateSql);
					updateStmt.setString(1, newTitle);
					updateStmt.setString(2, newContent);
					updateStmt.setInt(3, postNumber);
					updateStmt.executeUpdate();
					
					System.out.println("게시물이 성공적으로 수정되었습니다.");
					
				} else {
					//비밀번호 확인
					System.out.println("비밀번호: ");
					String password = sc.nextLine();
					if(!rs.getString("bpassword").equals(password)) {
						System.out.println("비밀번호가 틀립니다.");
						return;
					}
					
					//사용자로부터 새로운 제목과 내용을 입력 받습니다.
					System.out.println("새 제목: ");
					String newTitle = sc.nextLine();
					System.out.println("새 내용: ");
					String newContent = sc.nextLine();
					
					//게시물 정보를 업데이트 합니다.
					String updateSql="update board set btitle=?, bcontent=? where bno=?";
					PreparedStatement updateStmt = conn.prepareStatement(updateSql);
					updateStmt.setString(1, newTitle);
					updateStmt.setString(2, newContent);
					updateStmt.setInt(3, postNumber);
					updateStmt.executeUpdate();
					
					System.out.println("게시물이 성공적으로 수정되었습니다.");
				}				
			}else {
				System.out.println("해당 게시물을 찾을 수 없습니다.");
			}
			
			rs.close();
			selectStmt.close();
		} catch (SQLException e) {			
			e.printStackTrace();
			System.exit(0);
		}
		
	}

	//나의 정보(아이디,이름,전화번호,주소,성별 확인가능)
	private static void myInfo(String mid) {
		try {
			String sql = "select * from member where mid=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mid);
			ResultSet rs = pstmt.executeQuery();
			
			//rs에는 데이터베이스 쿼리의 결과를 담고 있는데 rs.next() 메소드는 ResultSet에서 다음행으로 커서를 이동하는 역할을 합니다.
			//쿼리 결과의 첫번째 행 바로 앞에 위치하기 때문에 rs.next()사용하는것.
			if(rs.next()) {
				System.out.println("아이디: " + rs.getString("mid"));
				System.out.println("이름: " + rs.getString("mname"));
				System.out.println("전화번호: " + rs.getString("mphonenumber"));
				System.out.println("주소: " + rs.getString("maddress"));
				System.out.println("성별: " + rs.getString("msex"));
			}else {
				System.out.println("정보를 가져오지 못했습니다.");
			}
			
			rs.close();
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
	}



	//회원가입
	private static void registerMember() {
	
		while(true) {
			System.out.println("--------------------");
			System.out.println("");
			System.out.println("회원 가입화면");
			
			Member member = new Member();
			
			System.out.print("아이디: ");
			member.setMid(sc.nextLine());
			
			System.out.print("비번: ");
			member.setMpassword(sc.nextLine());
			
			System.out.print("이름: ");				
			member.setMname(sc.nextLine());			
			
			System.out.print("전화번호: ");
			member.setMphoneNumber(sc.nextLine());
			
			System.out.print("주소: ");			
			member.setMaddress(sc.nextLine());
			
			System.out.print("성별: ");				
			member.setMsex(sc.nextLine());
			
			System.out.println("1.가입");
			System.out.println("2.다시입력");
			System.out.println("3.이전 화면으로");
			
			System.out.print("원하는 기능? ");
			menuNo = sc.nextLine();
			
			if(menuNo.equals("1")) {
				try {
					//이미 존재하는 mid인지 확인
					String checkSql = "select count(*) from member where mid=?";
					PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
					checkPstmt.setString(1, member.getMid());
					ResultSet checkRs = checkPstmt.executeQuery();
					
					checkRs.next();
					int count = checkRs.getInt(1);
					checkRs.close();
					checkPstmt.close();
					
					if(count>0) {
						System.out.println("이미 존재하는 아이디입니다.");
						return;
					}				
					
					String sql ="insert into member (mid,mpassword,mname,mphonenumber,maddress,msex) values(?, ?, ?, ?, ?, ?)";					
						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, member.getMid());
						pstmt.setString(2, member.getMpassword());
						pstmt.setString(3, member.getMname());
						pstmt.setString(4, member.getMphoneNumber());
						pstmt.setString(5, member.getMaddress());
						pstmt.setString(6, member.getMsex());
						pstmt.executeUpdate();
						pstmt.close();
						System.out.println("가입을 축하합니다.");
						break; //가입을 성공하면 while루프 종료
					} catch (SQLException e) {						
						e.printStackTrace();
						System.exit(0);
					}					
			} else if(menuNo.equals("2")) {
				System.out.println("다시 입력해주세요.");
				//while 루프가 계속 실행되므로 처음으로 돌아갑니다.
			} else if(menuNo.equals("3")) {
				return; //이전 화면으로 돌아가기
			} else {
				System.out.println("잘못된 선택입니다. 다시 입력해주세요.");
			}
		}		
	}
	
}
