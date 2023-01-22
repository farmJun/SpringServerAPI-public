package site.lghtsg.api.resells;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.lghtsg.api.config.BaseException;
import site.lghtsg.api.config.BaseResponse;
import site.lghtsg.api.resells.model.GetResellInfoRes;
import site.lghtsg.api.resells.model.GetResellTransactionRes;
import site.lghtsg.api.resells.model.GetResellBoxRes;

import java.util.List;

@RestController
@RequestMapping("/resells")
public class ResellController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ResellProvider resellProvider;

    @Autowired
    private final ResellDao resellDao;

    @Autowired
    private final ResellService resellService;


    public ResellController(ResellProvider resellProvider, ResellDao resellDao, ResellService resellService) {
        this.resellProvider = resellProvider;
        this.resellDao = resellDao;
        this.resellService = resellService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetResellBoxRes>> getResellBoxes(@RequestParam(required = false) String sort, @RequestParam(required = false) String order) {
        try {
            if (sort == null) {
                List<GetResellBoxRes> getGetResellBoxesRes = resellProvider.getResellBoxesByIdx(order);
                return new BaseResponse<>(getGetResellBoxesRes);
            }

            List<GetResellBoxRes> getGetResellBoxesRes = resellProvider.getResellsByRate(order);
            return new BaseResponse<>(getGetResellBoxesRes);
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/test")
    public void test(){
        //resellDao.scraping();
    }

    @ResponseBody
    @GetMapping("/{resellIdx}/info")
    public BaseResponse<GetResellInfoRes> getResellInfo(@PathVariable("resellIdx") int resellIdx) {
        try {
            GetResellInfoRes getResellInfoRes = resellProvider.getResellInfo(resellIdx);
            return new BaseResponse<>(getResellInfoRes);
        } catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>((e.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/{resellIdx}/prices")
    public BaseResponse<List<GetResellTransactionRes>> getResellTransaction(@PathVariable("resellIdx") int resellIdx) {
        try {
            List<GetResellTransactionRes> getResellTransactionRes = resellProvider.getResellTransaction(resellIdx);
            return new BaseResponse<>(getResellTransactionRes);
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }
}
