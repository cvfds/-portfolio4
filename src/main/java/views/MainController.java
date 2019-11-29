package views;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.TodoVO;
import domain.UserVO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class MainController extends MasterController {
	@FXML
	private Button H;
	@FXML
	private Label sl;
	@FXML
	private Label ul;
	@FXML
	private Button Y;
	@FXML
	private Label lblDate;
	@FXML
	private Label lblDay;
	@FXML
	private Label textLabel;
	@FXML
	private Label loginInfo;
	@FXML
	private GridPane gridCalendar;
	@FXML
	private ImageView imageV;

	private UserVO user;

	private int lv = 0;
	private Stage ps;
	@FXML
	private Label ml;

	public void setPrimaryStage(Stage ps) {
		this.ps = ps;
	}

	private List<TodoVO> itemList = new ArrayList<>();

	private Map<String, String> dayOfWeek = new HashMap<>();

	private int money;

	public UserVO getUser() {
		return user;
	}

	public void setLoginInfo(UserVO vo) {
		this.user = vo;
		loginInfo.setText(vo.getName() + "[" + vo.getId() + "]");
	}

	public void logout() {
		user = null;
		MainApp.app.loadPage("login");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		image();
	}

	@FXML
	private void initialize() {
		String[] names = {"낡은 무기", "목검", "돌검", "금검", "요도", "슬레이어 검", "일본 칼", "대검", "세검", "기사의 검", "비파형 검"};
		
		for (int i = 1; i < names.length+1 ; i++) {
			Image imag = new Image(getClass().getResource("/images/" + i +".jpg").toString());
			TodoVO item = new TodoVO(imag, names[i-1]);
			itemList.add(item);
		}
		Money(lv);
		image();
	}

	public void setToday(LocalDate date) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		lblDate.setText(date.format(dtf));
		lblDay.setText(dayOfWeek.get(date.getDayOfWeek().toString()));

		loadMonthData(YearMonth.now());
		setToday(LocalDate.now());
	}

	public void loadMonthData(YearMonth ym) {
		// 해당 년월의 1일 날짜를 만들어서 가져온다.
		LocalDate calendarDate = LocalDate.of(ym.getYear(), ym.getMonthValue(), 1);
		while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")) {
			// 일요일이 아닐때까지 하루씩 빼나아간다.
			calendarDate = calendarDate.minusDays(1); // 하루씩 감소
		}
		// 여기까지 오면 해당 주간의 첫째날로 설정되게 된다. 여기서부터 캘린더를 그린다.

		LocalDate first = LocalDate.of(ym.getYear(), ym.getMonthValue(), 1);
		LocalDate last = LocalDate.of(ym.getYear(), ym.getMonthValue() + 1, 1).minusDays(1);

		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT date, COUNT(*) AS cnt FROM Hi" + " WHERE owner = ? AND date BETWEEN ? AND ? "
				+ " GROUP BY date";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, user.getId());
			pstmt.setDate(2, Date.valueOf(first));
			pstmt.setDate(3, Date.valueOf(last));
			rs = pstmt.executeQuery();

			Map<LocalDate, Integer> cntMap = new HashMap<>();

			while (rs.next()) {
				LocalDate key = rs.getDate("date").toLocalDate();
				Integer value = rs.getInt("cnt");
				cntMap.put(key, value);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Util.showAlert("에러", "데이터베이스 연결 중 오류 발생", AlertType.ERROR);
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}

	}

	public void image() {
		imageV.setImage(itemList.get(lv).getImg());
		textLabel.setText(itemList.get(lv).getName());
		ml.setText(moneyWith + "원");
		Money(lv);
	}

	public void money() {
		if (money <= 0) {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/views/GameOverScreen.fxml"));
				AnchorPane root = loader.load();

				Scene scene = new Scene(root);
				ps.setScene(scene);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("");
			}
		}
	}

	public void HPage() {
		lv += 1;
		TodoVO selectedItem = itemList.get(lv);

		imageV.setImage(selectedItem.getImg());
		textLabel.setText(selectedItem.getName());

		Money(lv);
	}

	long moneyWith = 10000000;
	

	private void Money(int lv) {
		long P_money = (long) (Math.pow((lv + 1 * 50 / 49), 5) * 5000);
		long M_money = (long) (Math.pow((lv + 1 * 50 / 49), 3.5) * 30000);
		if (lv == 0)
			P_money = 0;
		ul.setText("강화비용 : " + M_money + "원");
		sl.setText("판매금액 : " + P_money + "원");
		ml.setText("자금 : " + moneyWith + "원");

	}
}
