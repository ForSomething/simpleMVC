package sid.intf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import sid.utils.CommonStringUtils;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(path = "/sid/login")
public class IntfLogin {

    private static Jedis jedis;

    static {
        jedis = new Jedis("192.168.96.128",6379);
    }

    //此处要提供一个接口来获取临时的密码，现在先不允许使用固定密码来登陆
    @RequestMapping(path = "/getToken")
    @ResponseBody
    public Object getToken(@RequestBody Map paraMap) throws Exception{
        String userCode = CommonStringUtils.toString(paraMap.get("userCode"));
        String password = CommonStringUtils.toString(paraMap.get("password"));
        if(!userCode.equals(password)){
            throw new Exception("密码错误");
        }
        String token = UUID.randomUUID().toString().replaceAll("-","");
        paraMap.clear();
        paraMap.put("redisResult",jedis.set(userCode, token, "NX", "EX", 60));
        paraMap.put("token",token);
        paraMap.put("tokenExists",jedis.exists(userCode));
        return paraMap;
    }

    @RequestMapping(path = "/doLogin")
    @ResponseBody
    public Object doLogin(@RequestBody Map paraMap) throws Exception{
        String userCode = CommonStringUtils.toString(paraMap.get("userCode"));
        String token = CommonStringUtils.toString(paraMap.get("token"));
        if(!token.equals(jedis.get(userCode))){
            throw new Exception("密码错误");
        }
        paraMap.clear();
        paraMap.put("sessionId",UUID.randomUUID().toString().replaceAll("-",""));
        return paraMap;
    }

    private String singleTestStr;

    @RequestMapping(path = "/test")
    @ResponseBody
    public Object test(@RequestBody Map paraMap) throws Exception{
        String type = CommonStringUtils.toString(paraMap.get("type"));
            if("in".equals(type)){
            singleTestStr = CommonStringUtils.toString(paraMap.get("testStr"));
            return "input success";
        }
        return singleTestStr;
    }
}
