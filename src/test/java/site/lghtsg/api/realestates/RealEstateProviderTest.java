package site.lghtsg.api.realestates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.lghtsg.api.common.model.Box;
import site.lghtsg.api.realestates.model.RealEstateBox;

import java.util.List;

import static site.lghtsg.api.config.Constant.*;

@SpringBootTest
public class RealEstateProviderTest {

    @Autowired
    private RealEstateProvider realEstateProvider;

    @Test
    void 부동산_정렬조회(){
//        String area = "서울특별시";
        String area = PARAM_DEFAULT;
        String sort = SORT_FLUCTUATION_PARAM;
        String order = ASCENDING_PARAM;

        final int test_len = 10;
        List<RealEstateBox> realEstateBoxList;
        try {
            for(int i = 0; i < test_len; i++) {
                long start = System.currentTimeMillis();
                realEstateBoxList = realEstateProvider.getRealEstateBoxes(sort, order, area);
                long end = System.currentTimeMillis();

                // results
                System.out.println("Test #" + (i + 1));
                System.out.println("Data Length : " + realEstateBoxList.size() + "개의 데이터");
                System.out.println("Duration : " + (double) (end - start) / 1000 + "s");
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}
