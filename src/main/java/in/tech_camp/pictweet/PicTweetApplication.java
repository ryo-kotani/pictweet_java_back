package in.tech_camp.pictweet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("in.tech_camp.pictweet")
@SpringBootApplication
public class PicTweetApplication {

	public static void main(String[] args) {
		SpringApplication.run(PicTweetApplication.class, args);
	}

}
