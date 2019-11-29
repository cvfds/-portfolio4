package domain;

import java.time.LocalDate;

import javafx.scene.image.Image;

public class TodoVO {
	private Image img;
	
	private String name;
	
	
	public TodoVO(Image img,String name) {
		this.img = img;
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

		
	public Image getImg() {
		return img;
	}

	public void setImg(Image img) {
		this.img = img;
	}


}
