package site.lghtsg.api.resells;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import site.lghtsg.api.common.model.Box;
import site.lghtsg.api.realestates.model.RealEstateBox;
import site.lghtsg.api.resells.model.GetResellInfoRes;
import site.lghtsg.api.resells.model.GetResellTransactionRes;
import site.lghtsg.api.resells.model.GetResellBoxRes;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

@Repository
public class ResellDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetResellBoxRes> getResellBoxes() {
        String getResellBoxesQuery = "select R.resellIdx, R.name, I.iconImage from Resell as R left join IconImage I on I.iconImageIdx = R.iconImageIdx";

        return this.jdbcTemplate.query(getResellBoxesQuery, resellBoxResRowMapper());
    }

    public GetResellInfoRes getResellInfo(long resellIdx) {
        String getResellQuery = "select R.resellIdx, R.name, R.releasedPrice, R.releasedDate, R.color, R.brand, R.productNum, R.image1, R.image2, R.image3, I.iconImage from Resell as R left join IconImage I on I.iconImageIdx= R.iconImageIdx where  R.resellIdx = ?";
        long getResellParams = resellIdx;

        return this.jdbcTemplate.queryForObject(getResellQuery, resellInfoResRowMapper(), getResellParams);
    }

    public GetResellBoxRes getResellBox(long resellIdx) {
        String getResellQuery = "select * from Resell where resellIdx = ?";
        long getResellParams = resellIdx;

        return this.jdbcTemplate.queryForObject(getResellQuery, resellBoxResRowMapper(), getResellParams);
    }

    public List<GetResellTransactionRes> getResellTransaction(long resellIdx) {
        String getResellTransactionQuery = "select * from ResellTransaction where resellIdx = ?";
        long getResellTransactionParams = resellIdx;
        return this.jdbcTemplate.query(getResellTransactionQuery, (rs, rowNum) -> new GetResellTransactionRes(rs.getInt("resellIdx"), rs.getInt("price"), rs.getString("transactionTime")), getResellTransactionParams);
    }

    public List<Integer> getResellTransactionForPriceAndRateOfChange(long resellIdx) {
        String getResellTransactionHistoryQuery = "select price from ResellTransaction where resellIdx = ?";
        long getResellTransactionHistory = resellIdx;
        return this.jdbcTemplate.query(getResellTransactionHistoryQuery, (rs, rowNum) -> rs.getInt("price"), getResellTransactionHistory);
    }

    private RowMapper<GetResellBoxRes> resellBoxResRowMapper() {
        return new RowMapper<GetResellBoxRes>() {
            @Override
            public GetResellBoxRes mapRow(ResultSet rs, int rowNum) throws SQLException {
                GetResellBoxRes getResellBoxRes = new GetResellBoxRes();
                getResellBoxRes.setIdx(rs.getLong("resellIdx"));
                getResellBoxRes.setName(rs.getString("name"));
                getResellBoxRes.setRateCalDateDiff("최근 거래가 기준");
                getResellBoxRes.setIconImage(rs.getString("iconImage"));
                return getResellBoxRes;
            }
        };
    }

    private RowMapper<GetResellInfoRes> resellInfoResRowMapper() {
        return new RowMapper<GetResellInfoRes>() {
            @Override
            public GetResellInfoRes mapRow(ResultSet rs, int rowNum) throws SQLException {
                GetResellInfoRes getResellInfoRes = new GetResellInfoRes();
                getResellInfoRes.setResellIdx(rs.getLong("resellIdx"));
                getResellInfoRes.setName(rs.getString("name"));
                getResellInfoRes.setReleasedPrice(rs.getString("releasedPrice"));
                getResellInfoRes.setReleasedDate(rs.getString("releasedDate"));
                getResellInfoRes.setColor(rs.getString("color"));
                getResellInfoRes.setBrand(rs.getString("brand"));
                getResellInfoRes.setProductNum(rs.getString("productNum"));
                getResellInfoRes.setRateCalDateDiff("최근 거래가 기준");
                getResellInfoRes.setImage1(rs.getString("image1"));
                getResellInfoRes.setImage2(rs.getString("image2"));
                getResellInfoRes.setImage3(rs.getString("image3"));
                getResellInfoRes.setIconImage(rs.getString("iconImage"));
                return getResellInfoRes;
            }
        };
    }

    public void scraping() {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        options.addArguments("--disable-popup-blocking");       //팝업안띄움
        //options.addArguments("headless");                       //브라우저 안띄움
        options.addArguments("--disable-gpu");            //gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음
        options.addArguments("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            //login 페이지
            driver.get("https://kream.co.kr/login");
            //id, pw 입력
            driver.findElement(By.xpath("//*[@id=\"__layout\"]/div/div[2]/div[1]/div/div[1]/div/input")).sendKeys("wnsdud6969@naver.com");
            driver.findElement(By.xpath("//*[@id=\"__layout\"]/div/div[2]/div[1]/div/div[2]/div/input")).sendKeys("qkrwnsdud123!");
            //버튼 클릭
            driver.findElement(By.xpath("//*[@id=\"__layout\"]/div/div[2]/div[1]/div/div[3]/a")).click();
            System.out.println("로그인 성공 = " + driver.getCurrentUrl());

            Thread.sleep(1000);
            String url = "https://kream.co.kr/brands/nike";
            driver.navigate().to(url);
            Thread.sleep(1000);
            driver.findElement(By.xpath("//*[@id=\"wrap\"]/div[2]/div/div[2]/div[2]/div[2]/div[1]/div[2]/button")).click();
            Thread.sleep(1000);
            driver.findElement(By.xpath("//*[@id=\"wrap\"]/div[2]/div/div[2]/div[2]/div[2]/div[1]/div[2]/ul/li[4]/a")).click();
            Thread.sleep(1000);


            String createResellQuery = "insert into Resell (name, releasedPrice, releasedDate, color, brand, productNum, image1, iconImageIdx) VALUES (?,?,?,?,?,?,?,?)"; // 실행될 동적 쿼리문
            String createResellTransactionQuery = "insert into ResellTransaction (resellIdx, price, transactionTime) VALUES(?,?,?)";
            List<WebElement> elements = driver.findElements(By.className("product_card"));

            List<String> urlList = new ArrayList<>();

            for (int i = 0; i < elements.size(); i++) {
                String productUrl = elements.get(i).findElement(By.tagName("a")).getAttribute("href");
                urlList.add(productUrl);
            }

            int resellIdx = 0;
            int max = 0;
            while (!urlList.isEmpty()) {
                driver.get(urlList.get(0));
                WebElement name = driver.findElement(By.className("sub_title"));
                WebElement brand = driver.findElement(By.className("brand"));
                String imageUrl = driver.findElement(By.tagName("img")).getAttribute("src");
                List<WebElement> productInfo = driver.findElements(By.tagName("dd"));
                if (productInfo.size() == 0) {
                    urlList.add(urlList.get(0));
                    max++;
                } else {
                    resellIdx++;
                    System.out.println(resellIdx + "번째");

                    Object[] createResellParams = new Object[]{name.getText(), productInfo.get(3).getText(), productInfo.get(1).getText(), productInfo.get(2).getText(), brand.getText(), productInfo.get(0).getText(), imageUrl, 1};

                    Thread.sleep(3000);

                    try {
                        driver.findElement(By.xpath("//*[@id=\"panel1\"]/a")).click();
                    } catch (Exception e) {
                        resellIdx--;
                        urlList.remove(0);
                        continue;
                    }

                    Thread.sleep(2000);
                    List<WebElement> transaction = driver.findElements(By.className("list_txt"));

                    //거래가격, 거래날짜 저장
                    List<List<String>> priceList = new ArrayList<>();

                    for (int i = transaction.size() - 2; i >= 1; i -= 3) {
                        List<String> temp = new ArrayList<>();
                        temp.add(transaction.get(i).getText());
                        temp.add(transaction.get(i + 1).getText());
                        priceList.add(temp);
                    }

                    if (priceList.size() <= 1) {
                        resellIdx--;
                        urlList.remove(0);
                        continue;
                    }

                    this.jdbcTemplate.update(createResellQuery, createResellParams);

                    for (List<String> list : priceList) {
                        String temp = list.get(0);
                        int price = Integer.parseInt(temp.replaceAll("[^0-9]", ""));

                        Object[] createResellTransactionParams = new Object[]{resellIdx, price, list.get(1)};
                        this.jdbcTemplate.update(createResellTransactionQuery, createResellTransactionParams);
                    }

                    System.out.println("------------------------");
                }
                urlList.remove(0);
            }
            System.out.println(max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}