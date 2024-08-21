package miniproject1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Board_List {
	//필드
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	
	//생성자
	public Board_List() {
		try {
			//JDBC Driver 등록
			Class.forName("oracle.jdbc.OracleDriver");
			
			
			//연결하기
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521/xe",
					"user01",
					"1004"
			);
		} catch(Exception e) {
			e.printStackTrace();
			exit();
		}
	}	

	//메소드
	public void list() {
		System.out.println();
		System.out.println("[게시물 목록]");
		System.out.println("---------------------------------");
		System.out.printf("%-6s%-12s%-16s%-40s\n", "no", "writer", "date", "title");
		System.out.println("---------------------------------");			
	
	
	try {
		String sql = "" + "Select bno,btitle,bcontent,bwriter,created_at " +
		"from board " +
		"order by bno desc";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			Board board = new Board();
			board.setBno(rs.getInt("bno"));
			board.setBtitle(rs.getString("btitle"));
			board.setBcontent(rs.getString("bcontent"));
			board.setBwriter(rs.getString("bwriter"));
			board.setCreated_at(rs.getDate("created_at"));
			System.out.printf("%-6d%-12s%-16tF%-40s \n",
				board.getBno(),
				board.getBwriter(),
				board.getCreated_at(),
				board.getBtitle()				
			);
		}
		rs.close();
		pstmt.close();
		
	} catch(SQLException e) {
		e.printStackTrace();
		exit();
	}
	
	//메인 메뉴 출력
	mainMenu();
	}

	public void mainMenu() {
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println("메인메뉴: 1.create | 2.read | 3. clear | 4.exit");
		System.out.println("메뉴 선택:");
		String menuNo = scanner.nextLine();
		System.out.println();
		
		switch(menuNo) {
		case "1" -> create();
		case "2" -> read();
		case "3" -> clear();
		case "4" -> exit();		
		}		
	}

	public void clear() {
		System.out.println("[게시물 전체 삭제]");
		System.out.println("--------------------------------------------------");
		System.out.println("보조 메뉴: 1.ok | 2.cancle");
		System.out.println("메뉴 선택: ");
		String menuNo = scanner.nextLine();
		if(menuNo.equals("1")) {
			//board 테이블에 게시물 정보 전체 삭제
			try {
				String sql = "truncate table board";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();
				pstmt.close();				
			} catch(Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		list();
	}	

	private void read() {
		//입력받기
		System.out.println("[게시물 읽기]");
		System.out.println("bno: ");
		int bno = Integer.parseInt(scanner.nextLine());
		
		//board 테이블에서 해당 게시물을 가져와서 출력
		try {
			//sql 변수는 실행할 sql 쿼리 문자열을 포함하고 있습니다.
			String sql = "" +
			"select bno, btitle, bcontent, bwriter, created_at " +
			"from board "+
			"where bno=?";
			
			//conn객체는 데이터베이스 연결을 의미, .prepareStatement(sql)은 sql 쿼리를 준비하는 PreparedStatement 객체를 생성
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno); //첫번째 ?에 bno값을 대입
			ResultSet rs = pstmt.executeQuery(); //sql쿼리를 실행하고 결과를 ResultSet 객체로 반환합니다.
			if(rs.next()) {
				Board board = new Board();
				board.setBno(rs.getInt(bno));
				board.setBtitle(rs.getString("btitle"));
				board.setBcontent(rs.getString("bcontent"));
				board.setBwriter(rs.getString("bwriter"));
				board.setCreated_at(rs.getDate("created_at"));
				System.out.println("############################");
				System.out.println("번호: " + board.getBno());
				System.out.println("제목: " + board.getBtitle());
				System.out.println("내용: " + board.getBcontent());
				System.out.println("작성자: " + board.getBwriter());
				System.out.println("생성 날짜: " + board.getCreated_at());
				System.out.println("############################");
				
				//보조 메뉴 출력
				System.out.println("--------------------------");				
				System.out.println("보조 메뉴: 1.update | 2.delete | 3.list");
				System.out.println("메뉴 선택: ");
				String menuNo= scanner.nextLine();
				System.out.println();
				
				if(menuNo.equals("1")) {
					update(board);					
				} else if(menuNo.equals("2")) {
					delete(board);
				} else if(menuNo.equals("3")) {
					//게시물 목록 출력
					list();
				}
				
			}
			rs.close();
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
			exit();
		}
		//게시물 목록 출력
		list();
		
	}



	private void delete(Board board) {
		
		//board 테이블에 게시물 정보 삭제
		try {
			String sql = "delete from board where bno=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, board.getBno());
			pstmt.executeUpdate();
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
			exit();
		}
		
		//게시물 목록 출력
		list();
		
	}



	public void update(Board board) {
		//수정 내용 입력 받기
		System.out.println("[수정 내용 입력]");
		System.out.println("제목: ");
		board.setBtitle(scanner.nextLine());
		System.out.println("내용: ");
		board.setBtitle(scanner.nextLine());
		System.out.println("작성자: ");
		board.setBwriter(scanner.nextLine());
		
		//보조 메뉴 출력
		System.out.println("----------------------------------------");
		System.out.println("보조 메뉴: 1.OK | 2.Cancle");
		System.out.println("메뉴 선택: ");
		String menuNo= scanner.nextLine();
		if(menuNo.equals("1")) {
			try {
				String sql = "" +
			"update board set btitle=?, bcontent=?, bwriter=? " +
			"where bno=?";
				
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getBtitle());
			pstmt.setString(2, board.getBcontent());
			pstmt.setString(3, board.getBwriter());
			pstmt.setInt(4,  board.getBno());
			pstmt.executeUpdate();//쿼리문을 실행해서 정보를 바꾼다.			
			pstmt.close();
			}catch (Exception e) {
				e.printStackTrace();
				exit();
			}		
		} 
		//게시물 목록 출력
		list();
	}



	public void create() {
		//입력 받기
		Board board = new Board();
		System.out.println("[새 게시물 입력]");
		System.out.println("제목: ");
		board.setBtitle(scanner.nextLine());
		System.out.println("내용: ");
		board.setBcontent(scanner.nextLine());
		System.out.println("작성자: ");
		board.setBwriter(scanner.nextLine());
		
		//보조 메뉴 출력
		System.out.println("-----------------------------------------------");
		System.out.println("보조 메뉴: 1.OK | 2.Cancle");
		System.out.println("메뉴 선택: ");
		String menuNo= scanner.nextLine();
		if(menuNo.equals("1")) {
			//board 테이블에 게시물 정보 저장
			try {
				String sql = "" + "insert into board (bno, btitle, bcontent, bwriter, created_at) "+
						"values (seq_bno.nextval, ?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, board.getBtitle());
				pstmt.setString(2, board.getBcontent());
				pstmt.setString(3, board.getBwriter());				
				pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
				pstmt.executeUpdate();
				pstmt.close();
			} catch ( Exception e) {
				e.printStackTrace();
				exit();
			}
		} else if (menuNo.equals("2")) {
	        // 취소 시 아무 작업도 하지 않고 게시물 목록으로 돌아감
	        list();
	        return;
	    }
		list();
	}

	public void exit() {
		if(conn != null) {
	         try {
	            conn.close();
	         } catch (SQLException e) {			
	        	 e.printStackTrace();
	         }
	      }
	      System.out.println("** 게시판 종료 **");
	      System.exit(0);
		
	}



	public static void main(String[] args) {
		Board_List boardList = new Board_List();
		boardList.list();
		
		

	}

}
